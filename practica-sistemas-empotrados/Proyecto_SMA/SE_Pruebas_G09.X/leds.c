#define _XTAL_FREQ 20000000

#include <xc.h>
#include <stdint.h>
#include "spi-master-v1.h"

#pragma config CPD = OFF, BOREN = OFF, IESO = OFF, DEBUG = OFF, FOSC = HS
#pragma config FCMEN = OFF, MCLRE = ON, WDTE = OFF, CP = OFF, LVP = OFF
#pragma config PWRTE = ON, BOR4V = BOR21V, WRT = OFF

#define NUM_LEDS 10

void setColor(uint8_t lum, uint8_t r, uint8_t g, uint8_t b)
{
    // Start frame
    for (uint8_t i = 0; i < 4; i++)
        spi_write_read(0x00);

    // LED frames
    for (uint8_t i = 0; i < NUM_LEDS; i++)
    {
        spi_write_read(0xE0 | (lum & 0x1F)); // brillo correcto
        spi_write_read(b);
        spi_write_read(g);
        spi_write_read(r);
    }

    // End frame
    for (uint8_t i = 0; i < 4; i++)
        spi_write_read(0xFF);
}

void main(void)
{
    TRISCbits.TRISC3 = 0; // CLK
    TRISCbits.TRISC5 = 0; // DATA

    OSCCON = 0b00001000; // External crystal

    while (1)
    {
        setColor(31, 255, 0, 0);
        __delay_ms(300);

        setColor(31, 0, 255, 0);
        __delay_ms(300);

        setColor(31, 0, 0, 255);
        __delay_ms(300);
    }
}