#include "MQ135test.h"

#include <stdio.h>
#include <sys/_intsup.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_adc/adc_oneshot.h"
#include "esp_adc/adc_cali.h"
#include "esp_adc/adc_cali_scheme.h"
#include "esp_log.h"

static const char *TAG = "MQ135";

// Canal ADC usado por el MQ135 
#define MQ135_ADC_CHANNEL  ADC_CHANNEL_6

adc_oneshot_unit_handle_t adc1_handle;


float mq135_task()
{
        int adc_value = 0;
        
        adc_oneshot_read(adc1_handle, MQ135_ADC_CHANNEL, &adc_value);
        
		return adc_value;
}

void mq135_start(void)
{
    adc_oneshot_unit_init_cfg_t init_config = {
        .unit_id = ADC_UNIT_1,
    };
    adc_oneshot_new_unit(&init_config, &adc1_handle);

    adc_oneshot_chan_cfg_t chan_config = {
        .atten = ADC_ATTEN_DB_12,   // reemplazo correcto
        .bitwidth = ADC_BITWIDTH_12,
    };

    adc_oneshot_config_channel(adc1_handle, MQ135_ADC_CHANNEL, &chan_config);

    ESP_LOGI(TAG, "ADC One-shot configurado para MQ135");
}


/*#include "mq135.h"
#include "esp_log.h"

static const char *TAG = "MQ135";

static adc_oneshot_unit_handle_t adc_handle = NULL;
static adc_oneshot_chan_cfg_t adc_chan_cfg = {
    .atten = ADC_ATTEN_DB_11,   // Lectura hasta ~3.3V
    .bitwidth = ADC_BITWIDTH_DEFAULT
};


const char* mq135_classify_air_quality(int adc_value)
{
    if (adc_value < 800) return "Muy buena";
    else if (adc_value < 1600) return "Buena";
    else if (adc_value < 2400) return "Moderada";
    else if (adc_value < 3200) return "Mala";
    else return "Muy mala";
}


 
void mq135_init(void)
{
    adc_oneshot_unit_init_cfg_t init_cfg = {
        .unit_id = ADC_UNIT_1
    };

    ESP_ERROR_CHECK(adc_oneshot_new_unit(&init_cfg, &adc_handle));
    ESP_ERROR_CHECK(adc_oneshot_config_channel(adc_handle, MQ135_ADC_CHANNEL, &adc_chan_cfg));

    ESP_LOGI(TAG, "MQ135 inicializado en canal %d", MQ135_ADC_CHANNEL);
}


 
static void mq135_task(void *pvParameters)
{
    (void)pvParameters;
    int adc_val = 0;

    while (1)
    {
        ESP_ERROR_CHECK(adc_oneshot_read(adc_handle, MQ135_ADC_CHANNEL, &adc_val));
        const char* quality = mq135_classify_air_quality(adc_val);

        ESP_LOGI(TAG, "MQ135 ADC=%d → Calidad del aire: %s", adc_val, quality);

        vTaskDelay(pdMS_TO_TICKS(1000));  // 1 segundo
    }
}


 
void mq135_start(void)
{
    xTaskCreate(mq135_task, "mq135_task", 2048, NULL, 5, NULL);
}

#pragma once

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/gpio.h"
#include "esp_adc/adc_oneshot.h"

// GPIO/Canal ADC por defecto (GPIO34 → ADC1_CHANNEL_6)
#define MQ135_ADC_CHANNEL ADC1_CHANNEL_6

#ifdef __cplusplus
extern "C" {
#endif


 
void mq135_init(void);


void mq135_start(void);

 
const char* mq135_classify_air_quality(int adc_value);

#ifdef __cplusplus
}
#endif

*/
