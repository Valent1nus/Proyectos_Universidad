#pragma config CPD = OFF, BOREN = OFF, IESO = OFF, DEBUG = OFF, FOSC = HS
#pragma config FCMEN = OFF, MCLRE = ON, WDTE = OFF, CP = OFF, LVP = OFF
#pragma config PWRTE = ON, BOR4V = BOR21V, WRT = OFF

#define _XTAL_FREQ 20000000

#include <xc.h>
#include <stdint.h>
#include <stdio.h>
#include "i2c-v2.h"

/* Dirección I2C del iAQ-Core */
#define IAQ_CORE_ADDR 0x5A

/* Variables globales */
uint16_t CO2 = 0;
uint16_t TVoc = 0;
uint8_t status = 0;

/* ================= UART ================= */

void init_uart(void)
{
    TXSTAbits.BRGH = 0;
    BAUDCTLbits.BRG16 = 0;

    SPBRGH = 0;
    SPBRG  = 32;             // 9600 baudios @ 20 MHz

    TXSTAbits.SYNC = 0;      // Asíncrono
    TXSTAbits.TX9   = 0;
    RCSTAbits.RX9   = 0;

    RCSTAbits.SPEN  = 1;     // Habilitar puerto serie
    TXSTAbits.TXEN  = 1;     // Habilitar transmisión
    RCSTAbits.CREN  = 1;     // Habilitar recepción
}

void putch(char c)
{
    while (!TXSTAbits.TRMT);
    TXREG = c;
}

/* ================= I2C ================= */

void I2C_Init()
{
    SSPCON  = 0b00101000; // Master mode, I2C
    SSPADD  = ((_XTAL_FREQ / 4) / 100000) - 1; // 100 kHz
    SSPIE   = 0; // No interrupciones I2C
    SSPIF   = 0;
    PEIE    = 1; // Interrupciones periféricas
}

/* ================= Funciones sensor CO2 ================= */

void read_CO2()
{
    uint8_t ack;
    unsigned char buff[9];

    i2c_start();
    ack = i2c_write((IAQ_CORE_ADDR << 1) | 1); // Leer
    if (ack != 0) { i2c_stop(); return; }       // Fallo en ACK

    // Leer 9 bytes
    for (uint8_t i = 0; i < 8; i++)
        buff[i] = i2c_read(1); // ACK
    buff[8] = i2c_read(0);     // NACK final
    i2c_stop();

    // Extraer datos
    CO2     = (buff[0] << 8) | buff[1];
    status  = buff[2];
    TVoc    = (buff[7] << 8) | buff[8];
}

/* ================= MAIN ================= */

void main(void)
{
    OSCCON = 0b00001000; // Cristal externo

    GIE = 1; // Habilitar interrupciones globales

    init_uart();
    I2C_Init();

    while (1)
    {
        read_CO2();

        // Solo imprimir si sensor OK
        if (status == 0x00)
        {
            printf("CO2: %d ppm\r\n", CO2);
            printf("TVoc: %d ppb\r\n", TVoc);
        }
        else if (status == 0x01)
        {
            printf("Sensor ocupado, esperando...\r\n");
        }
        else
        {
            printf("Error sensor!\r\n");
        }

        __delay_ms(1000); // Espera 1 segundo entre lecturas
    }
}