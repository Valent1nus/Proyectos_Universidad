#ifndef SYSTEM_MANAGER_H
#define SYSTEM_MANAGER_H
#include <stdint.h>

void System_Init(void);

/**
 * @brief Ejecuta la tarea rápida (cada 10ms).
 * - Muestrea ruido.
 * - Gestiona recepción de comandos UART (si llega config de LEDs/Ventilador).
 */
void System_Task_10ms(void);

/**
 * @brief Ejecuta la tarea de 1 segundo (MO-20).
 * - Calcula la categoría de ruido máxima del último segundo.
 */
void System_Task_1s(void);

/**
 * @brief Ejecuta la tarea de 5 segundos (MO-30).
 * - Lee todos los sensores (I2C, ADC).
 * - Envía la telemetría completa por UART.
 */
void System_Task_5s(void);

#endif