#ifndef PWM_LIB_H
#define PWM_LIB_H

#include <stdint.h>

/**
 * @brief Configura el hardware para PWM en RC2.
 * Frecuencia base: 25kHz (Asumiendo cristal de 20MHz).
 */
void PWM1_Init(void);

/**
 * @brief Cambia el ancho de pulso.
 * @param porcentaje Un número entero entre 0 (0%) y 100 (100%).
 * Valores mayores a 100 se recortarán automáticamente.
 */
void PWM1_Set_Duty(uint8_t porcentaje);

#endif