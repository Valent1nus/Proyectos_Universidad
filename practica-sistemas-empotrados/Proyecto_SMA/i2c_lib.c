// i2c_lib.c
#include <xc.h>
#include "i2c_lib.h"

#define _XTAL_FREQ 20000000

void I2C_Master_Init(const unsigned long c) {
    // Configurar pines RC3 (SCL) y RC4 (SDA) como entradas
    // El módulo MSSP toma el control aunque sean entradas
    TRISCbits.TRISC3 = 1;
    TRISCbits.TRISC4 = 1;

    SSPCON = 0b00101000; // SSPEN habilitado, Modo Maestro
    SSPCON2 = 0;
    SSPADD = (_XTAL_FREQ / (4 * c)) - 1; // Baud rate
    SSPSTAT = 0;
}

void I2C_Master_Wait(void) {
    // Esperar a que el bus esté libre (bits de estado)
    while ((SSPCON2 & 0x1F) || (SSPSTAT & 0x04));
}

void I2C_Master_Start(void) {
    I2C_Master_Wait();
    SSPCON2bits.SEN = 1; // Iniciar condición Start
}

void I2C_Master_Stop(void) {
    I2C_Master_Wait();
    SSPCON2bits.PEN = 1; // Iniciar condición Stop
}

void I2C_Master_Write(unsigned char d) {
    I2C_Master_Wait();
    SSPBUF = d;
}

unsigned char I2C_Master_Read(unsigned char a) {
    unsigned char temp;
    I2C_Master_Wait();
    SSPCON2bits.RCEN = 1; // Habilitar recepción
    I2C_Master_Wait();
    temp = SSPBUF;
    I2C_Master_Wait();
    
    // ACK o NACK
    SSPCON2bits.ACKDT = (a) ? 0 : 1;
    SSPCON2bits.ACKEN = 1;
    
    return temp;
}
