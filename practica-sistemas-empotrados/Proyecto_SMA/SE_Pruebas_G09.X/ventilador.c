#define _XTAL_FREQ 20000000
#include <xc.h>

#pragma config FOSC = HS, WDTE = OFF, PWRTE = ON, MCLRE = ON
#pragma config CP = OFF, CPD = OFF, BOREN = OFF, IESO = OFF
#pragma config FCMEN = OFF, LVP = OFF

void initPWM(void) {
    TRISCbits.TRISC1 = 0;      // CCP2 output

    PR2 = 166;                 // ~30 kHz
    T2CONbits.T2CKPS = 0;      // Prescaler 1
    T2CONbits.TMR2ON = 1;

    CCP2CONbits.CCP2M = 0b1100; // PWM mode
    CCPR2L = 0;
}

void setPotencia(unsigned char porcentaje) {
    if (porcentaje > 100) porcentaje = 100;
    CCPR2L = (porcentaje * 166) / 100;
}

void main(void) {
    OSCCON = 0b00001000; // External crystal

    initPWM();
    setPotencia(100);    // 100 %

    while (1);
}