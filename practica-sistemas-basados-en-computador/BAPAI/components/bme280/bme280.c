/*
 * SPDX-FileCopyrightText: 2022-2023 Espressif Systems (Shanghai) CO LTD
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <stdio.h>
#include <math.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

#include "bme280.h"
#include "esp_log.h"

static const char *TAG = "BME280";

/* --------- Helpers de acceso I2C usando i2cdev --------- */

static esp_err_t bme280_read_bytes(bme280_dev_t *sens, uint8_t reg, uint8_t *data, size_t len)
{
    I2C_DEV_TAKE_MUTEX(&sens->i2c_dev);
    esp_err_t res = i2c_dev_read_reg(&sens->i2c_dev, reg, data, len);
    I2C_DEV_GIVE_MUTEX(&sens->i2c_dev);
    return res;
}

static esp_err_t bme280_read_u8(bme280_dev_t *sens, uint8_t reg, uint8_t *value)
{
    return bme280_read_bytes(sens, reg, value, 1);
}

static esp_err_t bme280_write_u8(bme280_dev_t *sens, uint8_t reg, uint8_t value)
{
    I2C_DEV_TAKE_MUTEX(&sens->i2c_dev);
    esp_err_t res = i2c_dev_write_reg(&sens->i2c_dev, reg, &value, 1);
    I2C_DEV_GIVE_MUTEX(&sens->i2c_dev);
    return res;
}

/* ---------- API pública ---------- */

bme280_handle_t bme280_create(i2c_port_t port,
                              gpio_num_t sda_gpio,
                              gpio_num_t scl_gpio,
                              uint8_t dev_addr,
                              uint32_t clk_speed_hz)
{
    bme280_dev_t *sens = (bme280_dev_t *)calloc(1, sizeof(bme280_dev_t));
    if (!sens)
        return NULL;

    // Inicializa descriptor i2cdev
    sens->i2c_dev.port         = port;
    sens->i2c_dev.addr         = dev_addr;
    sens->i2c_dev.addr_bit_len = I2C_ADDR_BIT_LEN_7;
    sens->i2c_dev.timeout_ticks = 0; // usa valor por defecto / HW timeout

    sens->i2c_dev.cfg.sda_io_num     = sda_gpio;
    sens->i2c_dev.cfg.scl_io_num     = scl_gpio;
    sens->i2c_dev.cfg.sda_pullup_en  = 1;
    sens->i2c_dev.cfg.scl_pullup_en  = 1;
    sens->i2c_dev.cfg.master.clk_speed = clk_speed_hz ? clk_speed_hz : 400000;

    esp_err_t err = i2c_dev_create_mutex(&sens->i2c_dev);
    if (err != ESP_OK) {
        ESP_LOGE(TAG, "Failed to create I2C mutex for BME280: %s", esp_err_to_name(err));
        free(sens);
        return NULL;
    }

    return (bme280_handle_t)sens;
}

esp_err_t bme280_delete(bme280_handle_t *sensor)
{
    if (!sensor || *sensor == NULL) {
        return ESP_OK;
    }

    bme280_dev_t *sens = (bme280_dev_t *)(*sensor);

    i2c_dev_delete_mutex(&sens->i2c_dev);
    free(sens);
    *sensor = NULL;
    return ESP_OK;
}

/* ---------- Helpers de lectura de coeficientes / 16 bits ---------- */

static esp_err_t bme280_read_uint16(bme280_handle_t sensor, uint8_t addr, uint16_t *data)
{
    bme280_dev_t *sens = (bme280_dev_t *)sensor;
    uint8_t buf[2];
    esp_err_t res = bme280_read_bytes(sens, addr, buf, 2);
    if (res != ESP_OK)
        return res;

    *data = ((uint16_t)buf[0] << 8) | buf[1];
    return ESP_OK;
}

static esp_err_t bme280_read_uint16_le(bme280_handle_t sensor, uint8_t addr, uint16_t *data)
{
    bme280_dev_t *sens = (bme280_dev_t *)sensor;
    uint8_t buf[2];
    esp_err_t res = bme280_read_bytes(sens, addr, buf, 2);
    if (res != ESP_OK)
        return res;

    *data = ((uint16_t)buf[1] << 8) | buf[0];
    return ESP_OK;
}

/* ---------- Getters internos de registros de configuración ---------- */

unsigned int bme280_getconfig(bme280_handle_t sensor)
{
    bme280_dev_t *sens = (bme280_dev_t *) sensor;
    return (sens->config_t.t_sb << 5) | (sens->config_t.filter << 3) | sens->config_t.spi3w_en;
}

unsigned int bme280_getctrl_meas(bme280_handle_t sensor)
{
    bme280_dev_t *sens = (bme280_dev_t *) sensor;
    return (sens->ctrl_meas_t.osrs_t << 5) | (sens->ctrl_meas_t.osrs_p << 3) | sens->ctrl_meas_t.mode;
}

unsigned int bme280_getctrl_hum(bme280_handle_t sensor)
{
    bme280_dev_t *sens = (bme280_dev_t *) sensor;
    return (sens->ctrl_hum_t.osrs_h);
}

/* ---------- Funciones de estado / calibración ---------- */

bool bme280_is_reading_calibration(bme280_handle_t sensor)
{
    uint8_t rstatus = 0;
    bme280_dev_t *sens = (bme280_dev_t *) sensor;
    if (bme280_read_u8(sens, BME280_REGISTER_STATUS, &rstatus) != ESP_OK) {
        return false;
    }
    return (rstatus & (1 << 0)) != 0;
}

esp_err_t bme280_read_coefficients(bme280_handle_t sensor)
{
    uint8_t data = 0;
    uint8_t data1 = 0;
    uint16_t data16 = 0;
    bme280_dev_t *sens = (bme280_dev_t *) sensor;

    if (bme280_read_uint16_le(sensor, BME280_REGISTER_DIG_T1, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_t1 = data16;
    if (bme280_read_uint16_le(sensor, BME280_REGISTER_DIG_T2, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_t2 = (int16_t) data16;
    if (bme280_read_uint16_le(sensor, BME280_REGISTER_DIG_T3, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_t3 = (int16_t) data16;

    if (bme280_read_uint16_le(sensor, BME280_REGISTER_DIG_P1, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_p1 = data16;
    if (bme280_read_uint16_le(sensor, BME280_REGISTER_DIG_P2, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_p2 = (int16_t) data16;
    if (bme280_read_uint16_le(sensor, BME280_REGISTER_DIG_P3, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_p3 = (int16_t) data16;
    if (bme280_read_uint16_le(sensor, BME280_REGISTER_DIG_P4, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_p4 = (int16_t) data16;
    if (bme280_read_uint16_le(sensor, BME280_REGISTER_DIG_P5, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_p5 = (int16_t) data16;
    if (bme280_read_uint16_le(sensor, BME280_REGISTER_DIG_P6, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_p6 = (int16_t) data16;
    if (bme280_read_uint16_le(sensor, BME280_REGISTER_DIG_P7, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_p7 = (int16_t) data16;
    if (bme280_read_uint16_le(sensor, BME280_REGISTER_DIG_P8, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_p8 = (int16_t) data16;
    if (bme280_read_uint16_le(sensor, BME280_REGISTER_DIG_P9, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_p9 = (int16_t) data16;

    if (bme280_read_u8(sens, BME280_REGISTER_DIG_H1, &data) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_h1 = data;

    if (bme280_read_uint16_le(sensor, BME280_REGISTER_DIG_H2, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_h2 = (int16_t) data16;

    if (bme280_read_u8(sens, BME280_REGISTER_DIG_H3, &data) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_h3 = data;

    if (bme280_read_u8(sens, BME280_REGISTER_DIG_H4, &data) != ESP_OK) {
        return ESP_FAIL;
    }
    if (bme280_read_u8(sens, BME280_REGISTER_DIG_H4 + 1, &data1) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_h4 = (data << 4) | (data1 & 0xF);

    if (bme280_read_u8(sens, BME280_REGISTER_DIG_H5 + 1, &data) != ESP_OK) {
        return ESP_FAIL;
    }
    if (bme280_read_u8(sens, BME280_REGISTER_DIG_H5, &data1) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_h5 = (data << 4) | (data1 >> 4);

    if (bme280_read_u8(sens, BME280_REGISTER_DIG_H6, &data) != ESP_OK) {
        return ESP_FAIL;
    }
    sens->data_t.dig_h6 = (int8_t) data;

    return ESP_OK;
}

/* ---------- Configuración de modo / oversampling ---------- */

esp_err_t bme280_set_sampling(bme280_handle_t sensor,
                              bme280_sensor_mode mode,
                              bme280_sensor_sampling tempSampling,
                              bme280_sensor_sampling pressSampling,
                              bme280_sensor_sampling humSampling,
                              bme280_sensor_filter filter,
                              bme280_standby_duration duration)
{
    bme280_dev_t *sens = (bme280_dev_t *) sensor;

    sens->ctrl_meas_t.mode   = mode;
    sens->ctrl_meas_t.osrs_t = tempSampling;
    sens->ctrl_meas_t.osrs_p = pressSampling;
    sens->ctrl_hum_t.osrs_h  = humSampling;
    sens->config_t.filter    = filter;
    sens->config_t.t_sb      = duration;

    // CONTROLHUMID -> CONFIG -> CONTROL (ver DS 5.4.3)
    if (bme280_write_u8(sens, BME280_REGISTER_CONTROLHUMID, bme280_getctrl_hum(sensor)) != ESP_OK) {
        return ESP_FAIL;
    }
    if (bme280_write_u8(sens, BME280_REGISTER_CONFIG, bme280_getconfig(sensor)) != ESP_OK) {
        return ESP_FAIL;
    }
    if (bme280_write_u8(sens, BME280_REGISTER_CONTROL, bme280_getctrl_meas(sensor)) != ESP_OK) {
        return ESP_FAIL;
    }
    return ESP_OK;
}

/* ---------- Inicialización por defecto ---------- */

esp_err_t bme280_default_init(bme280_handle_t sensor)
{
    uint8_t chipid = 0;
    bme280_dev_t *sens = (bme280_dev_t *) sensor;

    if (bme280_read_u8(sens, BME280_REGISTER_CHIPID, &chipid) != ESP_OK) {
        ESP_LOGI(TAG, "bme280_default_init: read CHIPID failed (%02x)", chipid);
        return ESP_FAIL;
    }
    if (chipid != BME280_DEFAULT_CHIPID) {
        ESP_LOGI(TAG, "bme280_default_init: wrong CHIPID (%02x)", chipid);
        return ESP_FAIL;
    }

    // Soft reset
    if (bme280_write_u8(sens, BME280_REGISTER_SOFTRESET, 0xB6) != ESP_OK) {
        return ESP_FAIL;
    }

    // Espera a que el chip se despierte
    vTaskDelay(pdMS_TO_TICKS(300));

    // Espera mientras lee coeficientes
    while (bme280_is_reading_calibration(sensor)) {
        vTaskDelay(pdMS_TO_TICKS(100));
    }

    // Lee coeficientes de calibración
    if (bme280_read_coefficients(sensor) != ESP_OK) {
        return ESP_FAIL;
    }

    // Configuración por defecto
    if (bme280_set_sampling(sensor,
                            BME280_MODE_NORMAL,
                            BME280_SAMPLING_X16,
                            BME280_SAMPLING_X16,
                            BME280_SAMPLING_X16,
                            BME280_FILTER_OFF,
                            BME280_STANDBY_MS_0_5) != ESP_OK) {
        return ESP_FAIL;
    }

    return ESP_OK;
}

/* ---------- Medición forzada ---------- */

esp_err_t bme280_take_forced_measurement(bme280_handle_t sensor)
{
    uint8_t data = 0;
    bme280_dev_t *sens = (bme280_dev_t *) sensor;

    if (sens->ctrl_meas_t.mode == BME280_MODE_FORCED) {
        // Dispara la medida
        if (bme280_write_u8(sens, BME280_REGISTER_CONTROL, bme280_getctrl_meas(sensor)) != ESP_OK) {
            return ESP_FAIL;
        }
        // Espera hasta que acabe
        if (bme280_read_u8(sens, BME280_REGISTER_STATUS, &data) != ESP_OK) {
            return ESP_FAIL;
        }
        while (data & 0x08) {
            (void)bme280_read_u8(sens, BME280_REGISTER_STATUS, &data);
            vTaskDelay(pdMS_TO_TICKS(10));
        }
    }
    return ESP_OK;
}

/* ---------- Lectura de temperatura / presión / humedad ---------- */

esp_err_t bme280_read_temperature(bme280_handle_t sensor, float *temperature)
{
    int32_t var1, var2;
    uint8_t data[3] = { 0 };
    bme280_dev_t *sens = (bme280_dev_t *) sensor;

    if (bme280_read_bytes(sens, BME280_REGISTER_TEMPDATA, data, 3) != ESP_OK) {
        return ESP_FAIL;
    }
    int32_t adc_T = (data[0] << 16) | (data[1] << 8) | data[2];
    if (adc_T == 0x800000) {      // temp disabled
        return ESP_FAIL;
    }
    adc_T >>= 4;

    var1 = ((((adc_T >> 3) - ((int32_t) sens->data_t.dig_t1 << 1)))
            * ((int32_t) sens->data_t.dig_t2)) >> 11;

    var2 = (((((adc_T >> 4) - ((int32_t) sens->data_t.dig_t1))
              * ((adc_T >> 4) - ((int32_t) sens->data_t.dig_t1))) >> 12)
            * ((int32_t) sens->data_t.dig_t3)) >> 14;

    sens->t_fine = var1 + var2;
    *temperature = ((sens->t_fine * 5 + 128) >> 8) / 100.0f;
    return ESP_OK;
}

esp_err_t bme280_read_pressure(bme280_handle_t sensor, float *pressure)
{
    int64_t var1, var2, p;
    uint8_t data[3] = { 0 };
    bme280_dev_t *sens = (bme280_dev_t *) sensor;
    float temp = 0.0f;

    if (bme280_read_temperature(sensor, &temp) != ESP_OK) {
        return ESP_FAIL; // t_fine
    }
    if (bme280_read_bytes(sens, BME280_REGISTER_PRESSUREDATA, data, 3) != ESP_OK) {
        return ESP_FAIL;
    }

    int32_t adc_P = (data[0] << 16) | (data[1] << 8) | data[2];
    if (adc_P == 0x800000) {  // pressure disabled
        return ESP_FAIL;
    }
    adc_P >>= 4;

    var1 = ((int64_t) sens->t_fine) - 128000;
    var2 = var1 * var1 * (int64_t) sens->data_t.dig_p6;
    var2 = var2 + ((var1 * (int64_t) sens->data_t.dig_p5) << 17);
    var2 = var2 + (((int64_t) sens->data_t.dig_p4) << 35);
    var1 = ((var1 * var1 * (int64_t) sens->data_t.dig_p3) >> 8) +
           ((var1 * (int64_t) sens->data_t.dig_p2) << 12);
    var1 = (((((int64_t) 1) << 47) + var1)) * ((int64_t) sens->data_t.dig_p1) >> 33;
    if (var1 == 0) {
        return ESP_FAIL; // avoid div by zero
    }
    p = 1048576 - adc_P;
    p = (((p << 31) - var2) * 3125) / var1;
    var1 = (((int64_t) sens->data_t.dig_p9) * (p >> 13) * (p >> 13)) >> 25;
    var2 = (((int64_t) sens->data_t.dig_p8) * p) >> 19;
    p = ((p + var1 + var2) >> 8) + (((int64_t) sens->data_t.dig_p7) << 4);
    p = p >> 8; // /256

    *pressure = (float)p / 100.0f;
    return ESP_OK;
}

esp_err_t bme280_read_humidity(bme280_handle_t sensor, float *humidity)
{
    uint16_t data16;
    bme280_dev_t *sens = (bme280_dev_t *) sensor;
    float temp = 0.0f;

    if (bme280_read_temperature(sensor, &temp) != ESP_OK) {
        return ESP_FAIL; // t_fine
    }
    if (bme280_read_uint16(sensor, BME280_REGISTER_HUMIDDATA, &data16) != ESP_OK) {
        return ESP_FAIL;
    }
    int32_t adc_H = data16;
    if (adc_H == 0x8000) { // humidity disabled
        return ESP_FAIL;
    }

    int32_t v_x1_u32r;
    v_x1_u32r = (sens->t_fine - ((int32_t) 76800));
    v_x1_u32r = (((((adc_H << 14) - (((int32_t) sens->data_t.dig_h4) << 20)
                    - (((int32_t) sens->data_t.dig_h5) * v_x1_u32r))
                   + ((int32_t) 16384)) >> 15)
                 * (((((((v_x1_u32r * ((int32_t) sens->data_t.dig_h6)) >> 10)
                        * (((v_x1_u32r * ((int32_t) sens->data_t.dig_h3)) >> 11)
                           + ((int32_t) 32768))) >> 10)
                      + ((int32_t) 2097152))
                     * ((int32_t) sens->data_t.dig_h2) + 8192) >> 14));
    v_x1_u32r = (v_x1_u32r
                 - (((((v_x1_u32r >> 15) * (v_x1_u32r >> 15)) >> 7)
                     * ((int32_t) sens->data_t.dig_h1)) >> 4));
    v_x1_u32r = (v_x1_u32r < 0) ? 0 : v_x1_u32r;
    v_x1_u32r = (v_x1_u32r > 419430400) ? 419430400 : v_x1_u32r;
    *humidity = (v_x1_u32r >> 12) / 1024.0f;
    return ESP_OK;
}

/* ---------- Altitud / presión a nivel del mar ---------- */

esp_err_t bme280_read_altitude(bme280_handle_t sensor, float seaLevel, float *altitude)
{
    float pressure = 0.0f;

    if (bme280_read_pressure(sensor, &pressure) != ESP_OK) {
        return ESP_FAIL;
    }

    float atmospheric = pressure / 100.0f;
    *altitude = 44330.0f * (1.0f - powf(atmospheric / seaLevel, 0.1903f));
    return ESP_OK;
}

esp_err_t bme280_calculates_pressure(bme280_handle_t sensor, float altitude,
                                     float atmospheric, float *pressure)
{
    (void)sensor; // no usado, se mantiene firma por compatibilidad
    *pressure = atmospheric / powf(1.0f - (altitude / 44330.0f), 5.255f);
    return ESP_OK;
}
