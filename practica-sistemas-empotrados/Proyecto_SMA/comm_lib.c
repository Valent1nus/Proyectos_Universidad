
/*
 * comm_lib.c
 * Implementación del protocolo de comunicaciones.
 */

#include <xc.h>
#include "comm_lib.h"

#define _XTAL_FREQ 20000000UL

// Instancia global de los datos
volatile SystemState_t system_data;

// --- Variables internas para la Máquina de Estados de Recepción ---
typedef enum {
    ST_WAIT_HEADER,
    ST_WAIT_LENGTH,
    ST_WAIT_CMD,
    ST_WAIT_DATA,
    ST_WAIT_CRC_L,
    ST_WAIT_CRC_H
} rx_state_t;

static rx_state_t estado_rx = ST_WAIT_HEADER;
static uint8_t rx_len_esperada = 0;
static uint8_t rx_cmd_actual = 0;
static uint8_t rx_buffer[10]; // Buffer para datos (Max payload es pequeño)
static uint8_t rx_idx = 0;

// --- Inicialización UART (9600 Baudios @ 20MHz) ---
void UART_Init(void) {
    // Configurar pines RC7(RX) y RC6(TX) [Requisito Hardware PIC16F886]
    TRISCbits.TRISC7 = 1; 
    TRISCbits.TRISC6 = 0;

    // Configuración Baudrate: SPBRG = 129 para 9600 baudios con BRGH=1
    TXSTAbits.BRGH = 1;
    BAUDCTLbits.BRG16 = 0;
    SPBRG = 129;

    // Configurar Transmisión y Recepción Asíncrona
    TXSTAbits.SYNC = 0;
    RCSTAbits.SPEN = 1; // Habilitar puerto serie
    TXSTAbits.TXEN = 1; // Habilitar transmisor
    RCSTAbits.CREN = 1; // Habilitar receptor continuo

    // Limpiar estado inicial
    system_data.dato_nuevo_recibido = 0;
    // Valores seguros por defecto
    system_data.velocidad_fan = 0;
    system_data.leds.brillo = 0;
}

// --- Función Privada: Escribir un byte ---
void _UART_Write(uint8_t data) {
    while(!PIR1bits.TXIF); // Esperar a que el buffer de salida esté libre
    TXREG = data;
}

// --- Función Privada: Enviar Trama Completa (PC-10) ---
void _UART_Send_Frame(uint8_t cmd, uint8_t* payload, uint8_t len) {
    // 1. Cabecera (0xAA)
    _UART_Write(HEADER_BYTE);
    
    // 2. Longitud (Solo campo de datos)
    _UART_Write(len);
    
    // 3. Comando
    _UART_Write(cmd);
    
    // 4. Datos
    for(uint8_t i=0; i<len; i++) {
        _UART_Write(payload[i]);
    }
    
    // 5. CRC (0x0000 según PC-10)
    _UART_Write(0x00);
    _UART_Write(0x00);
}

// --- Implementación de Funciones de Envío Específicas ---

void UART_Send_Temperatura(int8_t temp) {
    // PC-50: 1 byte con signo
    _UART_Send_Frame(CMD_TEMP, (uint8_t*)&temp, 1);
}

void UART_Send_Ruido(uint8_t nivel) {
    // PC-30: Valores 0, 1, 2
    if(nivel > 2) nivel = 2; 
    _UART_Send_Frame(CMD_RUIDO, &nivel, 1);
}

void UART_Send_Humedad(uint16_t hum) {
    // PC-20: 2 bytes. Enviamos Little Endian (Bajo primero)
    uint8_t payload[2];
    payload[0] = (uint8_t)(hum & 0xFF);
    payload[1] = (uint8_t)((hum >> 8) & 0xFF);
    _UART_Send_Frame(CMD_HUMEDAD, payload, 2);
}

void UART_Send_CO2(uint16_t co2) {
    // PC-20: 2 bytes
    uint8_t payload[2];
    payload[0] = (uint8_t)(co2 & 0xFF);
    payload[1] = (uint8_t)((co2 >> 8) & 0xFF);
    _UART_Send_Frame(CMD_CO2, payload, 2);
}

void UART_Send_Luz(uint16_t luz) {
    // PC-20: 2 bytes
    uint8_t payload[2];
    payload[0] = (uint8_t)(luz & 0xFF);
    payload[1] = (uint8_t)((luz >> 8) & 0xFF);
    _UART_Send_Frame(CMD_LUZ, payload, 2);
}

void UART_Send_Ventilador(uint8_t velocidad) {
    if(velocidad > 100) velocidad = 100;
    _UART_Send_Frame(CMD_VENTILADOR, &velocidad, 1);
}

// --- Lógica de Procesamiento de Trama Recibida ---
void _Process_Received_Frame(void) {
    // Solo nos interesa procesar comandos de Configuración (Actuadores)
    
    switch(rx_cmd_actual) {
        case CMD_LEDS: // PC-40: 4 bytes (R, G, B, Brillo)
            if(rx_len_esperada == 4) {
                system_data.leds.rojo   = rx_buffer[0];
                system_data.leds.verde  = rx_buffer[1];
                system_data.leds.azul   = rx_buffer[2];
                system_data.leds.brillo = rx_buffer[3];
                system_data.dato_nuevo_recibido = 1;
            }
            break;
            
        case CMD_VENTILADOR: // PC-20: 1 byte (0-100%)
             if(rx_len_esperada == 1) {
                 system_data.velocidad_fan = rx_buffer[0];
                 system_data.dato_nuevo_recibido = 1;
             }
             break;
    }
}

// --- Máquina de Estados de Recepción (Llamar en bucle) ---
void UART_Task_Receive(void) {
    // 1. Verificar error de Overrun (común si el PIC se bloquea un momento)
    if(RCSTAbits.OERR) {
        RCSTAbits.CREN = 0; // Reiniciar módulo recepción
        RCSTAbits.CREN = 1;
        estado_rx = ST_WAIT_HEADER;
    }

    // 2. Si no hay datos, salir
    if(!PIR1bits.RCIF) return;

    // 3. Leer dato
    uint8_t byte = RCREG;

    // 4. Procesar según estado
    switch(estado_rx) {
        case ST_WAIT_HEADER:
            if(byte == HEADER_BYTE) { // 0xAA [cite: 106]
                estado_rx = ST_WAIT_LENGTH;
            }
            break;

        case ST_WAIT_LENGTH:
            rx_len_esperada = byte;
            // Validación simple de longitud (máximo buffer 10)
            if(rx_len_esperada > 10 || rx_len_esperada == 0) {
                 estado_rx = ST_WAIT_HEADER;
            } else {
                estado_rx = ST_WAIT_CMD;
            }
            break;

        case ST_WAIT_CMD:
            rx_cmd_actual = byte;
            rx_idx = 0;
            estado_rx = ST_WAIT_DATA;
            break;

        case ST_WAIT_DATA:
            rx_buffer[rx_idx++] = byte;
            if(rx_idx >= rx_len_esperada) {
                estado_rx = ST_WAIT_CRC_L;
            }
            break;

        case ST_WAIT_CRC_L:
            // Ignoramos validación real CRC por ahora (PC-10 dice CRC=0x0000)
            estado_rx = ST_WAIT_CRC_H;
            break;

        case ST_WAIT_CRC_H:
            // Trama completa -> Procesar
            _Process_Received_Frame();
            estado_rx = ST_WAIT_HEADER;
            break;
            
        default:
            estado_rx = ST_WAIT_HEADER;
            break;
    }
}