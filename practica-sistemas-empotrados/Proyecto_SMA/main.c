#include <xc.h>
#include "system_manager.h"

// --- Config Bits (Igual que antes) ---
#pragma config FOSC = HS
#pragma config WDTE = OFF
#pragma config PWRTE = ON
#pragma config MCLRE = ON
#pragma config BOREN = OFF
#pragma config LVP = OFF

#define _XTAL_FREQ 20000000UL

void main(void) {
    // 1. Inicialización
    System_Init();

    // 2. Contadores de tiempo
    uint16_t contador_ciclos = 0; // Cuenta bloques de 10ms

    while(1) {
        // --- BASE DE TIEMPO: 10ms ---
        // Todo lo que ocurra aquí dentro se repite cada 10ms
        
        // 1. Tareas de Alta Frecuencia (Ruido y UART)
        System_Task_10ms();
        
        // 2. Incrementar contador global
        contador_ciclos++;

        // --- TAREA CADA 1 SEGUNDO (100 * 10ms) ---
        if ((contador_ciclos % 100) == 0) {
            System_Task_1s();
        }

        // --- TAREA CADA 5 SEGUNDOS (500 * 10ms) ---
        if (contador_ciclos >= 500) {
            System_Task_5s();
            contador_ciclos = 0; // Reiniciar contador maestro
        }

        // 3. Espera para completar el ciclo de 10ms
        // (Nota: System_Task_10ms toma un tiempo X, esperamos 10ms - X)
        // Para simplificar en este nivel, usamos un delay fijo, asumiendo
        // que las tareas son muy rápidas comparadas con 10ms.
        __delay_ms(10);
    }
}