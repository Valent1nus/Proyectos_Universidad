#pragma once

#include "bme280.h"

#ifdef __cplusplus
extern "C" {
#endif

void bme280_sensor_start(void);      // crea la tarea
float bme280_get_temp(void);
float bme280_get_hum(void);
float bme280_get_press(void);

#ifdef __cplusplus
}
#endif
