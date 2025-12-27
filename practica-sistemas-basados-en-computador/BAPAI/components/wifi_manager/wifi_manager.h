#pragma once
#include "freertos/FreeRTOS.h"
#include "freertos/event_groups.h"
#include "esp_err.h"
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

void wifi_manager_init(void);

/**
 * @brief Bloquea hasta que haya conexión (STA con IP).
 * @return true si conectó, false si timeout.
 */
bool wifi_manager_wait_connected(TickType_t timeout_ticks);

/**
 * @brief Indica si está conectado (STA con IP).
 */
bool wifi_manager_is_connected(void);

#ifdef __cplusplus
}
#endif
