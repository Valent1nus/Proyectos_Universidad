#include "bme280_sensor.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "i2cdev.h"
#include "esp_log.h"

static const char *TAG = "BME280_SENSOR";

// Descriptor del sensor
static bme280_handle_t bme = NULL;

// Últimos valores leídos (globales)
static float last_temp = 0;
static float last_hum  = 0;
static float last_press = 0;

// Pines I2C (cámbialos si tu placa usa otros)
#define I2C_PORT        I2C_NUM_0
#define I2C_SDA_GPIO    21
#define I2C_SCL_GPIO    22
#define I2C_FREQ        400000

//
// ----- TAREA DE LECTURA ----
//

//
// ----- INICIALIZACIÓN ----
//
void bme280_sensor_start(void)
{
    ESP_LOGI(TAG, "Inicializando I2C...");
    i2cdev_init();

    ESP_LOGI(TAG, "Creando descriptor BME280...");

    bme = bme280_create(
        I2C_PORT,
        I2C_SDA_GPIO,
        I2C_SCL_GPIO,
        BME280_I2C_ADDRESS_DEFAULT,
        I2C_FREQ
    );

    if (!bme) {
        ESP_LOGE(TAG, "ERROR creando BME280");
        return;
    }

    esp_err_t err = bme280_default_init(bme);
    if (err != ESP_OK) {
        ESP_LOGE(TAG, "Error en init: %s", esp_err_to_name(err));
        return;
    }

    ESP_LOGI(TAG, "BME280 inicializado.");

    // Lanzamos la tarea
}

//
// ----- GETTERS ----
//
float bme280_get_temp(void)  { bme280_read_temperature(bme, &last_temp); return last_temp; }
float bme280_get_hum(void)   { bme280_read_humidity(bme, &last_hum); return last_hum;  }
float bme280_get_press(void) { bme280_read_pressure(bme, &last_press); return last_press; }
