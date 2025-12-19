/*
 * Librería PWM (CCP1) para PIC16F886
 * Frecuencia configurada: 25 kHz @ 20MHz Osc
 */

#include <xc.h>
#include <stdint.h>

#define _XTAL_FREQ 20000000UL

/**
 * @brief Inicializa el módulo CCP1 en modo PWM a 25kHz.
 * Configura Timer2, PR2 y el pin RC2 como salida.
 */
void PWM1_Init(void) {
    // 1. Deshabilitar el driver de salida del pin CCP1 (RC2)
    // Es buena práctica ponerlo como entrada o desconectado mientras configuramos
    TRISCbits.TRISC2 = 1; 

    // 2. Configurar el periodo del PWM (Frecuencia = 25kHz)
    // Formula: Periodo = [(PR2) + 1] * 4 * Tosc * TMR2Prescale
    // 199 + 1 = 200; 200 * 4 = 800 ticks de reloj base por ciclo PWM.
    PR2 = 199;

    // 3. Configurar el módulo CCP1 en modo PWM
    // bits 3-0 (CCP1M): 1100 = Modo PWM
    CCP1CONbits.CCP1M = 0b1100;
    
    // Inicializamos el ciclo de trabajo a 0%
    CCPR1L = 0;
    CCP1CONbits.DC1B = 0;

    // 4. Configurar el Timer2
    // bits 1-0 (T2CKPS): 00 = Prescaler 1
    T2CONbits.T2CKPS = 0b00; 
    
    // Encender Timer2
    T2CONbits.TMR2ON = 1;

    // Esperar a que el Timer2 se estabilice un poco
    __delay_ms(10); 

    // 5. Habilitar la salida del pin RC2
    // Ahora que todo está listo, dejamos salir la señal
    TRISCbits.TRISC2 = 0; 
}

/**
 * @brief Establece el ciclo de trabajo (Duty Cycle) del PWM.
 * @param porcentaje Valor de 0 a 100.
 */
void PWM1_Set_Duty(uint8_t porcentaje) {
    // Protección: Si pasan más de 100, lo limitamos a 100
    if (porcentaje > 100) {
        porcentaje = 100;
    }

    /* * EXPLICACIÓN MATEMÁTICA:
     * El ciclo completo son 4 * (PR2 + 1) tiempos de instrucción.
     * Con PR2 = 199, tenemos: 4 * 200 = 800 cuentas totales de resolución (10 bits).
     * * 100% Duty = 800
     * 50%  Duty = 400
     * 1%   Duty = 8
     * * Por lo tanto, el valor a cargar es: porcentaje * 8.
     */
    uint16_t pwm_valor = (uint16_t)(porcentaje * 8);

    // El módulo CCP usa 10 bits para el Duty Cycle:
    // - Los 8 bits más significativos (MSB) van en CCPR1L.
    // - Los 2 bits menos significativos (LSB) van en CCP1CON bits 5 y 4 (DC1B).
    
    // Guardamos los 2 bits bajos (usando máscara 0x03)
    CCP1CONbits.DC1B = (pwm_valor & 0x03);

    // Guardamos los 8 bits altos (desplazando 2 a la derecha)
    CCPR1L = (pwm_valor >> 2);
}