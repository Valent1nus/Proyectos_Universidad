#pragma config CPD = OFF, BOREN = OFF, IESO = OFF, DEBUG = OFF, FOSC = HS
#pragma config FCMEN = OFF, MCLRE = ON, WDTE = OFF, CP = OFF, LVP = OFF
#pragma config PWRTE = ON, BOR4V = BOR21V, WRT = OFF

#define _XTAL_FREQ 20000000

#include <xc.h>
#include <stdint.h>
#include <stdio.h>

/* Variables globales */
volatile uint16_t adc_val = 0;
volatile uint16_t cont_int = 0;
volatile uint8_t flag_print = 0;

/* Prototipos */
void init_uart(void);
void init_TMR0(void);
void init_ADC(void);
void putch(char c);

/* ================= UART ================= */

void init_uart(void)
{
    TXSTAbits.BRGH = 0;
    BAUDCTLbits.BRG16 = 0;

    SPBRGH = 0;
    SPBRG = 32;              // 9600 baudios @ 20 MHz

    TXSTAbits.SYNC = 0;      // Asíncrono
    TXSTAbits.TX9 = 0;
    RCSTAbits.RX9 = 0;

    RCSTAbits.SPEN = 1;      // Habilitar puerto serie
    TXSTAbits.TXEN = 1;      // Habilitar transmisión
    RCSTAbits.CREN = 1;      // Habilitar recepción
}

/* ================= TIMER0 ================= */

void init_TMR0(void)
{
    OPTION_REGbits.T0CS = 0; // Reloj interno
    OPTION_REGbits.PSA  = 0; // Prescaler asignado a TMR0
    OPTION_REGbits.PS   = 0b111; // 1:256

    TMR0 = 60;               // ?10 ms
    INTCONbits.T0IE = 1;
    INTCONbits.T0IF = 0;
}

/* ================= ADC ================= */

void init_ADC(void)
{
    ADCON0bits.ADCS = 0b10;  // Fosc/32
    ADCON0bits.CHS  = 0b0001; // Canal AN1
    ADCON0bits.ADON = 1;

    ADCON1bits.ADFM = 1;     // Resultado justificado a la derecha
    ADCON1bits.VCFG0 = 0;    // Vref = Vdd
    ADCON1bits.VCFG1 = 0;

    ANSELbits.ANS1 = 1;      // AN1 analógico
    TRISAbits.TRISA1 = 1;    // RA1 entrada

    PIE1bits.ADIE = 1;
    PIR1bits.ADIF = 0;
}

/* ================= ISR ================= */

void __interrupt() ISR(void)
{
    if (INTCONbits.T0IF)
    {
        TMR0 = 60;
        cont_int++;

        ADCON0bits.GO = 1;   // Iniciar conversión ADC

        if (cont_int >= 500) // 500 × 10 ms = 5 s
        {
            flag_print = 1;
            cont_int = 0;
        }

        INTCONbits.T0IF = 0;
    }

    if (PIR1bits.ADIF)
    {
        adc_val = ((uint16_t)ADRESH << 8) | ADRESL;
        PIR1bits.ADIF = 0;
    }
}

/* ================= PUTCH (printf) ================= */

void putch(char c)
{
    while (!TXSTAbits.TRMT);
    TXREG = c;
}

/* ================= MAIN ================= */

void main(void)
{
    OSCCON = 0b00001000; // Cristal externo

    INTCONbits.PEIE = 1;
    INTCONbits.GIE  = 1;

    TRISC = 0; // UART TX como salida

    init_uart();
    init_ADC();
    init_TMR0();

    while (1)
    {
        if (flag_print)
        {
            uint16_t humedad;

            /* HIH4000 (aprox, Vdd=5V):
               RH ? (ADC - 163) / 6
            */
            if (adc_val < 163)
                humedad = 0;
            else
                humedad = (adc_val - 163) / 6;

            if (humedad > 100)
                humedad = 100;

            printf("Humedad: %d %%\r\n", humedad);

            flag_print = 0;
        }
    }
}