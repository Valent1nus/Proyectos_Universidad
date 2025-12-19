#ifndef SPI_SOFT_LIB_H
#define SPI_SOFT_LIB_H

#include <stdint.h>

// Definición de pines físicos según tu esquema
// SCK = Pin 11 (RC0)
// SDO = Pin 16 (RC5)
// SDI = No se usa para LEDs, pero lo definimos para evitar errores de compilación
#define SPI_CLK_PIN  PORTCbits.RC0
#define SPI_DAT_OUT_PIN PORTCbits.RC5
#define SPI_DAT_IN_PIN  PORTCbits.RC4 // Dummy

// Configuración de Dirección (TRIS)
#define SPI_CLK_TRIS  TRISCbits.TRISC0
#define SPI_DAT_OUT_TRIS TRISCbits.TRISC5

/**
 * @brief Configura los pines RC0 y RC5 como salida para SPI por software.
 */
void SPI_Soft_Init(void);

/**
 * @brief Envía un byte usando tu implementación bit-banging.
 */
char spi_write_read(char one_byte);

/**
 * @brief Actualiza la tira de LEDs SK9822.
 * @param r Rojo (0-255)
 * @param g Verde (0-255)
 * @param b Azul (0-255)
 * @param brillo Nivel de iluminación (0-31)
 */
void LED_Strip_Set_Color(uint8_t r, uint8_t g, uint8_t b, uint8_t brillo);

#endif