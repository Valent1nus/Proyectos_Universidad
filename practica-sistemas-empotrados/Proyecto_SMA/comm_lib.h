/*
 * comm_lib.h
 * Librería de comunicaciones UART bajo protocolo PC-10/PC-50
 * Dispositivo: SMA-LAMP (PIC16F886)
 */

#ifndef COMM_LIB_H
#define COMM_LIB_H

#include <stdint.h>

// --- Configuración UART ---
#define UART_BAUDRATE 9600
#define HEADER_BYTE   0xAA
#define CRC_DEFAULT   0x0000

// --- Comandos del Protocolo (Requisito PC-20) ---
#define CMD_RUIDO       0x00 // 1 byte  (0=Bajo, 1=Medio, 2=Alto)
#define CMD_LUZ         0x01 // 2 bytes (uint16_t)
#define CMD_CO2         0x02 // 2 bytes (uint16_t PPM)
#define CMD_HUMEDAD     0x03 // 2 bytes (uint16_t %)
#define CMD_VENTILADOR  0x04 // 1 byte  (0-100 %)
#define CMD_LEDS        0x05 // 4 bytes (R, G, B, Brillo)
#define CMD_TEMP        0x06 // 1 byte  (ºC Entero)

// --- Estructura de Estado del Sistema ---
// Almacena la configuración recibida desde el PC para los actuadores
typedef struct {
    // Actuadores (Escritura desde PC, Lectura por PIC)
    uint8_t  velocidad_fan;    // 0-100%
    
    // Estructura para LEDs (Requisito PC-40)
    struct {
        uint8_t rojo;
        uint8_t verde;
        uint8_t azul;
        uint8_t brillo; // 0-31
    } leds;
    
    // Bandera de notificación
    // Se pone a 1 cuando el PC envía una configuración válida
    uint8_t dato_nuevo_recibido; 
    
} SystemState_t;

// Variable global accesible desde el main/manager
extern volatile SystemState_t system_data;

// --- Funciones de Inicialización y Recepción ---
void UART_Init(void);
void UART_Task_Receive(void); // Llamar frecuentemente (Polling)

// --- Funciones de Envío (Telemetría hacia el PC) ---
void UART_Send_Temperatura(int8_t temp);    // PC-50
void UART_Send_Ruido(uint8_t nivel);        // PC-30
void UART_Send_Humedad(uint16_t hum);       // PC-20
void UART_Send_CO2(uint16_t co2);           // PC-20
void UART_Send_Luz(uint16_t luz);           // PC-20
void UART_Send_Ventilador(uint8_t velocidad); // Confirmación estado

#endif