#include <xc.h>
#include "spi_soft_lib.h"

#define _XTAL_FREQ 20000000

// --- Mapeo de variables de tu código a los pines del PIC ---
// Esto hace que tu código funcione directamente con el hardware
#define spi_clk      SPI_CLK_PIN
#define spi_dat_out  SPI_DAT_OUT_PIN
#define spi_dat_in   SPI_DAT_IN_PIN

// --- Inicialización ---
void SPI_Soft_Init(void) {
    SPI_CLK_PIN = 0;
    SPI_DAT_OUT_PIN = 0;
    
    SPI_CLK_TRIS = 0;     // RC0 como Salida (SCK)
    SPI_DAT_OUT_TRIS = 0; // RC5 como Salida (SDO)
}

// --- TU CÓDIGO DE SPI MASTER ---
char spi_write_read(char one_byte)
{
    char answer, x;
    
    answer = 0;
    
    for(x = 8; x > 0; x--)
    {
        // Nota: He casteado a (__bit) porque XC8 a veces es estricto con tipos bit
        spi_dat_out = (__bit)((one_byte >> (x - 1)) & 0b00000001);
        //__delay_us(5);
        spi_clk = 1;
        //__delay_us(10);
        
        // Leemos (aunque para LEDs no hace falta, lo mantenemos por compatibilidad)
        if(spi_dat_in) answer |= 1; 
        
        spi_clk = 0;
        //__delay_us(10);
        if(x > 1)
            answer = answer << 1;
    }
    
    return answer;
}

// --- DRIVER ESPECÍFICO PARA TIRA LED SK9822 ---
// Referencia SK9822: Start Frame (32x0) -> LED Frames -> End Frame (32x1)
void LED_Strip_Set_Color(uint8_t r, uint8_t g, uint8_t b, uint8_t brillo) {
    
    // 1. Start Frame: 32 bits de ceros
    for(uint8_t i=0; i<4; i++) spi_write_read(0x00);

    // 2. LED Frame (Para 1 LED o toda la tira igual)
    // Estructura: [111 + 5 bits Brillo] [Azul] [Verde] [Rojo]
    
    // A. Byte de Brillo (Global Brightness)
    // Los SK9822 requieren que los 3 primeros bits sean '111' (0xE0)
    // El brillo va de 0 a 31 (5 bits).
    if(brillo > 31) brillo = 31; // Protección
    spi_write_read(0xE0 | brillo); 

    // B. Colores (Orden habitual SK9822: Blue, Green, Red)
    // Nota: El protocolo UART manda RGB, aquí lo reordenamos si es necesario.
    spi_write_read(b); 
    spi_write_read(g);
    spi_write_read(r);

    // Nota: Si la tira tiene N leds, repetiríamos el paso 2 N veces.
    // Asumimos 1 tramo o que replicamos la señal para todos (o solo 1 led).
    // Si tienes 10 leds, descomenta el bucle:
    /*
    for(int k=0; k<NUM_LEDS-1; k++) {
         spi_write_read(0xE0 | brillo);
         spi_write_read(b); spi_write_read(g); spi_write_read(r);
    }
    */

    // 3. End Frame: 32 bits de unos (o ceros, depende datasheet, 0xFF es seguro)
    // Esto empuja los datos a través del registro de desplazamiento
    for(uint8_t i=0; i<4; i++) spi_write_read(0xFF);
}
