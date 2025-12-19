#include <xc.h>

// --- CORRECCIÓN CRÍTICA 1: Definir frecuencia ANTES de los includes ---
// Esto es necesario para que __delay_ms() funcione en este archivo.
#define _XTAL_FREQ 20000000UL

#include "system_manager.h"
#include "adc_lib.h"
#include "pwm_lib.h"
#include "comm_lib.h"
#include "i2c_lib.h"

// --- CORRECCIÓN CRÍTICA 2: Incluir la librería de los LEDs ---
// Esto es necesario para que reconozca LED_Strip_Set_Color y SPI_Soft_Init
#include "spi_soft_lib.h"

// Variables para lógica de Ruido
static uint16_t ruido_max_instantaneo = 0; // Max dentro de los 10ms
static uint16_t ruido_max_segundo = 0;     // Max acumulado en 1s
static uint8_t  nivel_ruido_reportar = 0;  // Valor final para enviar (0,1,2)

// Variables para actuadores (Persistencia)
static uint8_t fan_speed = 0;

void System_Init(void) {
    ADC_Init();    
    PWM1_Init();   
    UART_Init();   
    I2C_Master_Init(100000); 
    
    // Inicialización de los LEDs (SPI Software)
    SPI_Soft_Init(); 
}

// Auxiliar: Clasificación de ruido (MO-10)
uint8_t _Clasificar_Ruido(uint16_t val) {
    if (val <= 400) return 0;
    else if (val <= 900) return 1;
    else return 2;
}

// --- TAREA 10ms: Muestreo rápido y Recepción ---
void System_Task_10ms(void) {
    // 1. Muestreo de Ruido (MO-20)
    // Pin definido en adc_lib.h como ADC_CH_RUIDO
    uint16_t lectura_ruido = ADC_Read(ADC_CH_RUIDO);
    
    // Guardamos el pico máximo
    if (lectura_ruido > ruido_max_segundo) {
        ruido_max_segundo = lectura_ruido;
    }

    // 2. Revisar si hay comandos entrantes del PC (Configuración)
    UART_Task_Receive(); 

    if (system_data.dato_nuevo_recibido) {
        // Actualizar Ventilador
        fan_speed = system_data.velocidad_fan; 
        PWM1_Set_Duty(fan_speed);
        
        // Actualizar LEDs
        LED_Strip_Set_Color(
            system_data.leds.rojo, 
            system_data.leds.verde, 
            system_data.leds.azul, 
            system_data.leds.brillo
        );
        
        system_data.dato_nuevo_recibido = 0;
    }
}

// --- TAREA 1s: Procesar Ruido ---
void System_Task_1s(void) {
    // MO-20: Mostrar la categoría más alta alcanzada cada segundo.
    nivel_ruido_reportar = _Clasificar_Ruido(ruido_max_segundo);
    
    // Reiniciamos el acumulador para el siguiente segundo
    ruido_max_segundo = 0;
}

// --- TAREA 5s: Medir resto y ENVIAR ---
void System_Task_5s(void) {
    // 1. Leer Sensores Lentos (MO-30)
    
    // Temperatura
    uint16_t raw_temp = ADC_Read(ADC_CH_TEMP);
    int8_t temp_c = (int8_t)(raw_temp * 0.488); 
    
    // Humedad
    uint16_t raw_hum = ADC_Read(ADC_CH_HUM);
    uint16_t hum_pct = (uint16_t)((raw_hum * 100UL) / 1023); // Cast para evitar overflow
    
    // I2C (Simulados o reales)
    uint16_t luz = 500; // Dummy
    uint16_t co2 = 400; // Dummy

    // 2. ENVIAR TODO (Secuencia de tramas)
    
    UART_Send_Temperatura(temp_c);
    __delay_ms(5); // Ahora funcionará porque definimos _XTAL_FREQ arriba
    
    UART_Send_Ruido(nivel_ruido_reportar); 
    __delay_ms(5);
    
    UART_Send_Humedad(hum_pct);
    __delay_ms(5);
    
    UART_Send_CO2(co2);
    __delay_ms(5);
    
    UART_Send_Luz(luz);
    __delay_ms(5);
    
    // Confirmamos estado actual de actuadores
    UART_Send_Ventilador(fan_speed);
}