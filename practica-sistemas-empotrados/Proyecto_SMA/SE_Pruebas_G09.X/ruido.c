

#pragma config CPD = OFF, BOREN = OFF, IESO = OFF, DEBUG = OFF, FOSC = HS
#pragma config FCMEN = OFF, MCLRE = ON, WDTE = OFF, CP = OFF, LVP = OFF
#pragma config PWRTE = ON, BOR4V = BOR21V, WRT = OFF

#define _XTAL_FREQ 20000000

#include <xc.h>
#include <stdint.h>
#include <stdio.h>

/* Variables globales */
volatile uint16_t adc_val = 0;        // Última lectura ADC
volatile uint16_t max_val = 0;        // Valor máximo durante 1 s
volatile uint16_t cont_int = 0;
volatile uint8_t flag_print = 0;

/* ================= UART ================= */

void init_uart(void)
{
    TXSTAbits.BRGH = 0;
    BAUDCTLbits.BRG16 = 0;

    SPBRGH = 0;
    SPBRG  = 32;            // 9600 baudios @ 20 MHz

    TXSTAbits.SYNC = 0;     // Asíncrono
    TXSTAbits.TX9   = 0;
    RCSTAbits.RX9   = 0;

    RCSTAbits.SPEN  = 1;    // Habilitar puerto serie
    TXSTAbits.TXEN  = 1;    // Habilitar transmisión
    RCSTAbits.CREN  = 1;    // Habilitar recepción
}

void putch(char c)
{
    while (!TXSTAbits.TRMT);
    TXREG = c;
}

/* ================= TIMER0 ================= */

void init_TMR0(void)
{
    OPTION_REGbits.T0CS = 0;  // Reloj interno
    OPTION_REGbits.PSA  = 0;  // Prescaler asignado a TMR0
    OPTION_REGbits.PS   = 0b111; // 1:256

    TMR0 = 60;                 // Inicialización
    INTCONbits.T0IE = 1;
    INTCONbits.T0IF = 0;
}

/* ================= ADC ================= */

void init_ADC(void)
{
    ADCON0bits.ADCS = 0b10;   // Fosc/32
    ADCON0bits.CHS  = 0b010;  // AN2 (RA2)
    ADCON0bits.ADON = 1;

    ADCON1bits.ADFM = 1;      // Justificación derecha
    ADCON1bits.VCFG0 = 0;     // Vref = Vdd
    ADCON1bits.VCFG1 = 0;

    ANSELbits.ANS2 = 1;       // AN2 analógico
    TRISAbits.TRISA2 = 1;     // RA2 como entrada
}

/* ================= ISR ================= */

void __interrupt() ISR(void)
{
    if (INTCONbits.T0IF)
    {
        TMR0 = 60;
        cont_int++;

        ADCON0bits.GO = 1;   // Inicia conversión ADC

        if (cont_int >= 100) // 100 × 10 ms = 1 s
        {
            flag_print = 1;
            cont_int = 0;
        }

        INTCONbits.T0IF = 0;
    }

    if (PIR1bits.ADIF)
    {
        adc_val = ((uint16_t)ADRESH << 8) | ADRESL;

        if (adc_val > max_val)
            max_val = adc_val;

        PIR1bits.ADIF = 0;
    }
}

/* ================= MAIN ================= */

void main(void)
{
    OSCCON = 0b00001000; // Cristal externo

    INTCONbits.GIE  = 1;
    INTCONbits.PEIE = 1;

    TRISC6 = 0; // TX como salida
    TRISC7 = 1; // RX como entrada

    init_uart();
    init_ADC();
    init_TMR0();

    while (1)
    {
        if (flag_print)
        {
            printf("Ruido: %d\r\n", max_val);
            max_val = 0;   // Reinicia máximo
            flag_print = 0;
        }
    }
}