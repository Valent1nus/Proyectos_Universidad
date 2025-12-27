/*
 * SPDX-FileCopyrightText: 2022-2023 Espressif Systems (Shanghai) CO LTD
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#ifndef _BME280_H_
#define _BME280_H_

#include "i2cdev.h"   // <--- en lugar de "i2c_bus.h"

#define BME280_I2C_ADDRESS_DEFAULT   (0x76)     /*The device's I2C address is either 0x76 or 0x77.*/
#define BME280_DEFAULT_CHIPID        (0x60)

#define WRITE_BIT      I2C_MASTER_WRITE         /*!< I2C master write */
#define READ_BIT       I2C_MASTER_READ          /*!< I2C master read */
#define ACK_CHECK_EN   0x1                      /*!< I2C master will check ack from slave*/
#define ACK_CHECK_DIS  0x0                      /*!< I2C master will not check ack from slave */
#define ACK_VAL        0x0                      /*!< I2C ack value */
#define NACK_VAL       0x1                      /*!< I2C nack value */

#define BME280_REGISTER_DIG_T1              0x88
#define BME280_REGISTER_DIG_T2              0x8A
#define BME280_REGISTER_DIG_T3              0x8C

#define BME280_REGISTER_DIG_P1              0x8E
#define BME280_REGISTER_DIG_P2              0x90
#define BME280_REGISTER_DIG_P3              0x92
#define BME280_REGISTER_DIG_P4              0x94
#define BME280_REGISTER_DIG_P5              0x96
#define BME280_REGISTER_DIG_P6              0x98
#define BME280_REGISTER_DIG_P7              0x9A
#define BME280_REGISTER_DIG_P8              0x9C
#define BME280_REGISTER_DIG_P9              0x9E

#define BME280_REGISTER_DIG_H1              0xA1
#define BME280_REGISTER_DIG_H2              0xE1
#define BME280_REGISTER_DIG_H3              0xE3
#define BME280_REGISTER_DIG_H4              0xE4
#define BME280_REGISTER_DIG_H5              0xE5
#define BME280_REGISTER_DIG_H6              0xE7

#define BME280_REGISTER_CHIPID              0xD0
#define BME280_REGISTER_VERSION             0xD1
#define BME280_REGISTER_SOFTRESET           0xE0

#define BME280_REGISTER_CAL26               0xE1  // R calibration stored in 0xE1-0xF0

#define BME280_REGISTER_CONTROLHUMID        0xF2
#define BME280_REGISTER_STATUS              0XF3
#define BME280_REGISTER_CONTROL             0xF4
#define BME280_REGISTER_CONFIG              0xF5
#define BME280_REGISTER_PRESSUREDATA        0xF7
#define BME280_REGISTER_TEMPDATA            0xFA
#define BME280_REGISTER_HUMIDDATA           0xFD

typedef struct {
    uint16_t dig_t1;
    int16_t  dig_t2;
    int16_t  dig_t3;

    uint16_t dig_p1;
    int16_t  dig_p2;
    int16_t  dig_p3;
    int16_t  dig_p4;
    int16_t  dig_p5;
    int16_t  dig_p6;
    int16_t  dig_p7;
    int16_t  dig_p8;
    int16_t  dig_p9;

    uint8_t  dig_h1;
    int16_t  dig_h2;
    uint8_t  dig_h3;
    int16_t  dig_h4;
    int16_t  dig_h5;
    int8_t   dig_h6;
} bme280_data_t;

typedef enum {
    BME280_SAMPLING_NONE = 0b000,
    BME280_SAMPLING_X1   = 0b001,
    BME280_SAMPLING_X2   = 0b010,
    BME280_SAMPLING_X4   = 0b011,
    BME280_SAMPLING_X8   = 0b100,
    BME280_SAMPLING_X16  = 0b101
} bme280_sensor_sampling;

typedef enum {
    BME280_MODE_SLEEP  = 0b00,
    BME280_MODE_FORCED = 0b01,
    BME280_MODE_NORMAL = 0b11
} bme280_sensor_mode;

typedef enum {
    BME280_FILTER_OFF  = 0b000,
    BME280_FILTER_X2   = 0b001,
    BME280_FILTER_X4   = 0b010,
    BME280_FILTER_X8   = 0b011,
    BME280_FILTER_X16  = 0b100
} bme280_sensor_filter;

// standby durations in ms
typedef enum {
    BME280_STANDBY_MS_0_5   = 0b000,
    BME280_STANDBY_MS_10    = 0b110,
    BME280_STANDBY_MS_20    = 0b111,
    BME280_STANDBY_MS_62_5  = 0b001,
    BME280_STANDBY_MS_125   = 0b010,
    BME280_STANDBY_MS_250   = 0b011,
    BME280_STANDBY_MS_500   = 0b100,
    BME280_STANDBY_MS_1000  = 0b101
} bme280_standby_duration;

// The config register
typedef struct config {
    unsigned int t_sb    : 3; // standby time
    unsigned int filter  : 3; // filter
    unsigned int none    : 1;
    unsigned int spi3w_en: 1;
} bme280_config_t;

// The ctrl_meas register
typedef struct ctrl_meas {
    unsigned int osrs_t : 3; // temp oversampling
    unsigned int osrs_p : 3; // pressure oversampling
    unsigned int mode   : 2; // mode
} bme280_ctrl_meas_t;

// The ctrl_hum register
typedef struct ctrl_hum {
    unsigned int none   : 5;
    unsigned int osrs_h : 3; // humidity oversampling
} bme280_ctrl_hum_t;

/**
 * Descriptor interno de BME280
 */
typedef struct {
    i2c_dev_t          i2c_dev;      // <-- NUEVO: descriptor i2cdev
    bme280_data_t      data_t;
    bme280_config_t    config_t;
    bme280_ctrl_meas_t ctrl_meas_t;
    bme280_ctrl_hum_t  ctrl_hum_t;
    int32_t            t_fine;
} bme280_dev_t;

typedef void *bme280_handle_t; /*handle of bme280*/

#ifdef __cplusplus
extern "C" {
#endif

/**
 * @brief   Crea un handle de BME280 usando i2cdev
 *
 * @param  port        Puerto I2C (p.ej. I2C_NUM_0)
 * @param  sda_gpio    Pin SDA
 * @param  scl_gpio    Pin SCL
 * @param  dev_addr    Dirección I2C (0x76 u 0x77)
 * @param  clk_speed_hz Frecuencia I2C (0 para usar 400kHz por defecto)
 *
 * @return
 *     - bme280_handle_t válido en éxito
 *     - NULL en error
 */
bme280_handle_t bme280_create(i2c_port_t port,
                              gpio_num_t sda_gpio,
                              gpio_num_t scl_gpio,
                              uint8_t dev_addr,
                              uint32_t clk_speed_hz);

/**
 * @brief   Elimina el handle de BME280
 *
 * @param  sensor  puntero al handle de bme280
 *
 * @return
 *     - ESP_OK Success
 *     - ESP_FAIL Fail
 */
esp_err_t bme280_delete(bme280_handle_t *sensor);

/**
 * @brief   Get the value of BME280_REGISTER_CONFIG register
 */
unsigned int bme280_getconfig(bme280_handle_t sensor);

/**
 * @brief   Get the value of BME280_REGISTER_CONTROL measure register
 */
unsigned int bme280_getctrl_meas(bme280_handle_t sensor);

/**
 * @brief   Get the value of BME280_REGISTER_CONTROLHUMID measure register
 */
unsigned int bme280_getctrl_hum(bme280_handle_t sensor);

/**
 * @brief return true if chip is busy reading cal data
 */
bool bme280_is_reading_calibration(bme280_handle_t sensor);

/**
 * @brief Reads the factory-set coefficients
 */
esp_err_t bme280_read_coefficients(bme280_handle_t sensor);

/**
 * @brief  setup sensor with given parameters / settings
 */
esp_err_t bme280_set_sampling(bme280_handle_t sensor, bme280_sensor_mode mode,
                              bme280_sensor_sampling tempsampling,
                              bme280_sensor_sampling presssampling,
                              bme280_sensor_sampling humsampling,
                              bme280_sensor_filter filter,
                              bme280_standby_duration duration);

/**
 * @brief init bme280 device
 */
esp_err_t bme280_default_init(bme280_handle_t sensor);

/**
 * @brief  Take a new measurement (only possible in forced mode)
 */
esp_err_t bme280_take_forced_measurement(bme280_handle_t sensor);

/**
 * @brief  Returns the temperature from the sensor
 */
esp_err_t bme280_read_temperature(bme280_handle_t sensor, float *temperature);

/**
 * @brief  Returns the pressure from the sensor (hPa)
 */
esp_err_t bme280_read_pressure(bme280_handle_t sensor, float *pressure);

/**
 * @brief  Returns the humidity from the sensor (%RH)
 */
esp_err_t bme280_read_humidity(bme280_handle_t sensor, float *humidity);

/**
 * @brief Calculates the altitude (in meters)
 */
esp_err_t bme280_read_altitude(bme280_handle_t sensor, float seaLevel, float *altitude);

/**
 * @brief Calculates sea-level pressure from altitude & atmospheric pressure
 */
esp_err_t bme280_calculates_pressure(bme280_handle_t sensor, float altitude,
                                     float atmospheric, float *pressure);

#ifdef __cplusplus
}
#endif

#endif // _BME280_H_
