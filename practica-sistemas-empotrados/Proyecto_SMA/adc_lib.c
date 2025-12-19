#include <xc.h>
#include <stdint.h>

#define _XTAL_FREQ 20000000 

/**
 * @brief Inicializa el ADC configurando individualmente los pines 3, 4 y 5.
 */
void ADC_Init(void) {
    // ---------------------------------------------------------
    // 1. Configuración de Hardware (Pines Físicos)
    // ---------------------------------------------------------
    
    // --- PIN 3 (RA1 / AN1) ---
    TRISAbits.TRISA1 = 1;  // Configurar RA1 como ENTRADA
    ANSELbits.ANS1   = 1;  // Configurar AN1 como ANALÓGICO

    // --- PIN 4 (RA2 / AN2) ---
    TRISAbits.TRISA2 = 1;  // Configurar RA2 como ENTRADA
    ANSELbits.ANS2   = 1;  // Configurar AN2 como ANALÓGICO

    // --- PIN 5 (RA3 / AN3) ---
    TRISAbits.TRISA3 = 1;  // Configurar RA3 como ENTRADA
    ANSELbits.ANS3   = 1;  // Configurar AN3 como ANALÓGICO

    // ---------------------------------------------------------
    // 2. Configuración del Módulo ADC
    // ---------------------------------------------------------

    // ADCON1: Configuración de Referencia y Formato
    // IMPORTANTE: Para usar PIN 4 y 5 como entradas analógicas normales,
    // debemos asegurar que NO estén configurados como Vref externa.
    ADCON1bits.VCFG1 = 0;   // Vref- conectado a VSS (Tierra)
    ADCON1bits.VCFG0 = 0;   // Vref+ conectado a VDD (5V)
    ADCON1bits.ADFM  = 1;   // Justificado a la DERECHA

    // ADCON0: Configuración de Reloj
    ADCON0bits.ADCS1 = 1;   // Fosc/32 (Bit alto)
    ADCON0bits.ADCS0 = 0;   // Fosc/32 (Bit bajo)
    
    // Encender el módulo ADC
    ADCON0bits.ADON = 1;

    // Pequeña espera para estabilización inicial
    __delay_us(20);
}

/**
 * @brief Lee el canal analógico seleccionado.
 * @param canal El número de canal (1 para Pin3, 2 para Pin4, 3 para Pin5).
 */
uint16_t ADC_Read(uint8_t canal) {
    if (canal > 13) return 0; 

    // 1. Limpiar selección anterior (Bits CHS0-CHS3)
    // Usamos máscara para no tocar ADON ni ADCS
    ADCON0 &= 0b11000011; 

    // 2. Seleccionar nuevo canal
    // Desplazamos 2 bits porque CHS empieza en el bit 2 de ADCON0
    ADCON0 |= (canal << 2);

    // 3. Tiempo de adquisición (Hold Time)
    // Necesario para cargar el condensador interno del ADC
    __delay_us(25); 

    // 4. Iniciar conversión
    ADCON0bits.GO = 1;

    // 5. Esperar a que termine (Hardware pone GO a 0)
    while (ADCON0bits.GO);

    // 6. Retornar resultado (unir ADRESH y ADRESL)
    return ((uint16_t)((ADRESH << 8) + ADRESL));
}