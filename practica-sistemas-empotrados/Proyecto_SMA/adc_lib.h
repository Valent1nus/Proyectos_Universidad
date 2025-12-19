#ifndef ADC_LIB_H
#define ADC_LIB_H

#include <stdint.h>

/*
 * -------------------------------------------------------------------
 * Mapeo de Pines Físicos a Canales ADC
 * Usa estas constantes como argumento en la función ADC_Read()
 * -------------------------------------------------------------------
 */
#define ADC_CH_TEMP   1  
#define ADC_CH_HUM    2  
#define ADC_CH_RUIDO  3  
/*
 * -------------------------------------------------------------------
 * Prototipos de Funciones
 * -------------------------------------------------------------------
 */

/**
 * @brief Configura el módulo ADC y los pines 3, 4 y 5 como entradas analógicas.
 * - Frecuencia: Fosc/32
 * - Referencia: VDD/VSS (5V/0V)
 * - Justificación: Derecha
 */
void ADC_Init(void);

/**
 * @brief Realiza una lectura del convertidor analógico-digital.
 * * @param canal El número de canal a leer (0-13). 
 * Recomendado usar las macros: ADC_PIN_3, ADC_PIN_4, etc.
 * * @return uint16_t Valor de la conversión (0 a 1023).
 */
uint16_t ADC_Read(uint8_t canal);

#endif	/* ADC_LIB_H */