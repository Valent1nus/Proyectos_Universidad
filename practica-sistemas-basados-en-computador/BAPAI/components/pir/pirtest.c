#include "pirtest.h"

#include <stdio.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/gpio.h"
#include "esp_log.h"

static const char *TAG = "PIR";
static int pir_gpio = PIR_GPIO_DEFAULT;

void pir_init(int gpio)
{
    if (gpio != -1) {
        pir_gpio = gpio;
    }

    ESP_LOGI(TAG, "Inicializando sensor PIR en GPIO %d...", pir_gpio);

    gpio_config_t io_conf = {
        .pin_bit_mask = (1ULL << pir_gpio),
        .mode = GPIO_MODE_INPUT,
        .pull_up_en = GPIO_PULLUP_DISABLE,
        .pull_down_en = GPIO_PULLDOWN_DISABLE,
        .intr_type = GPIO_INTR_DISABLE
    };
    gpio_config(&io_conf);

    ESP_LOGI(TAG, "PIR inicializado.");
}

int pir_task()
{
    int level = gpio_get_level(pir_gpio);

    return level;

}

void pir_start(void)
{
    pir_init(-1);
}
