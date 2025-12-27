#include "wifi_manager.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "freertos/event_groups.h"
#include "esp_log.h"
#include "esp_wifi.h"
#include "esp_event.h"
#include "esp_netif.h"
#include "esp_http_server.h"
#include "nvs.h"

static const char *MODULE_TAG = "WIFI_SETUP";

#define DEFAULT_AP_SSID "ESP32-10M"
#define DEFAULT_AP_PASS "atiqueteimporta"

#define WIFI_CONNECTED_BIT BIT0
#define WIFI_FAIL_BIT      BIT1

static EventGroupHandle_t s_wifi_event_group;
static esp_netif_t *s_sta_netif = NULL;
static esp_netif_t *s_ap_netif  = NULL;

static bool s_connected = false;

// ---------- NVS ----------
esp_err_t wifi_credentials_load(char *out_ssid, size_t ssid_len, char *out_pass, size_t pass_len) {
    nvs_handle_t storage;
    esp_err_t res = nvs_open("wifi_config", NVS_READONLY, &storage);
    if (res == ESP_OK) {
        res = nvs_get_str(storage, "ssid", out_ssid, &ssid_len);
        if (res == ESP_OK) {
            res = nvs_get_str(storage, "pass", out_pass, &pass_len);
        }
        nvs_close(storage);
    }
    return res;
}

static void wifi_credentials_save(const char *ssid, const char *pass) {
    nvs_handle_t storage;
    if (nvs_open("wifi_config", NVS_READWRITE, &storage) == ESP_OK) {
        nvs_set_str(storage, "ssid", ssid);
        nvs_set_str(storage, "pass", pass);
        nvs_commit(storage);
        nvs_close(storage);
    }
}

// ---------- URL decode simple (maneja + y %xx) ----------
static int hexval(char c) {
    if (c >= '0' && c <= '9') return c - '0';
    if (c >= 'a' && c <= 'f') return 10 + (c - 'a');
    if (c >= 'A' && c <= 'F') return 10 + (c - 'A');
    return -1;
}
static void urldecode_inplace(char *s) {
    char *o = s;
    while (*s) {
        if (*s == '+') {
            *o++ = ' ';
            s++;
        } else if (*s == '%' && s[1] && s[2]) {
            int h1 = hexval(s[1]), h2 = hexval(s[2]);
            if (h1 >= 0 && h2 >= 0) {
                *o++ = (char)((h1 << 4) | h2);
                s += 3;
            } else {
                *o++ = *s++;
            }
        } else {
            *o++ = *s++;
        }
    }
    *o = 0;
}

// ---------- Web server ----------
static esp_err_t web_root_handler(httpd_req_t *req) {
    const char *html_form =
        "<!DOCTYPE html><html><body>"
        "<h2>Configuración WiFi</h2>"
        "<form action=\"/save\" method=\"post\">"
        "SSID: <input type=\"text\" name=\"ssid\"><br>"
        "Password: <input type=\"password\" name=\"pass\"><br>"
        "<input type=\"submit\" value=\"Guardar\">"
        "</form></body></html>";
    httpd_resp_send(req, html_form, HTTPD_RESP_USE_STRLEN);
    return ESP_OK;
}

static esp_err_t web_save_handler(httpd_req_t *req) {
    char request_buf[256];
    int recv_len = httpd_req_recv(req, request_buf, sizeof(request_buf) - 1);
    if (recv_len <= 0) return ESP_FAIL;
    request_buf[recv_len] = '\0';

    // Parse simple: ssid=...&pass=...
    char *ssid_kv = strstr(request_buf, "ssid=");
    char *pass_kv = strstr(request_buf, "pass=");
    if (!ssid_kv || !pass_kv) {
        httpd_resp_sendstr(req, "Formato inválido.");
        return ESP_OK;
    }

    char input_ssid[32] = {0};
    char input_pass[64] = {0};

    // Extraer ssid hasta '&'
    ssid_kv += 5;
    char *amp = strchr(ssid_kv, '&');
    if (!amp) {
        httpd_resp_sendstr(req, "Formato inválido (sin &).");
        return ESP_OK;
    }
    size_t ssid_sz = (size_t)(amp - ssid_kv);
    if (ssid_sz >= sizeof(input_ssid)) ssid_sz = sizeof(input_ssid) - 1;
    memcpy(input_ssid, ssid_kv, ssid_sz);
    input_ssid[ssid_sz] = 0;

    // Extraer pass hasta fin
    pass_kv += 5;
    strncpy(input_pass, pass_kv, sizeof(input_pass) - 1);

    urldecode_inplace(input_ssid);
    urldecode_inplace(input_pass);

    wifi_credentials_save(input_ssid, input_pass);

    httpd_resp_sendstr(req, "Credenciales almacenadas. Reiniciando...");
    vTaskDelay(pdMS_TO_TICKS(1500));
    esp_restart();
    return ESP_OK;
}

static httpd_handle_t launch_webserver(void) {
    httpd_handle_t httpd_server = NULL;
    httpd_config_t cfg = HTTPD_DEFAULT_CONFIG();
    if (httpd_start(&httpd_server, &cfg) == ESP_OK) {
        httpd_uri_t root_page = {.uri="/", .method=HTTP_GET, .handler=web_root_handler};
        httpd_register_uri_handler(httpd_server, &root_page);

        httpd_uri_t save_page = {.uri="/save", .method=HTTP_POST, .handler=web_save_handler};
        httpd_register_uri_handler(httpd_server, &save_page);
    }
    return httpd_server;
}

// ---------- Eventos WiFi ----------
static void wifi_event_handler(void *arg, esp_event_base_t base, int32_t id, void *data) {
    if (base == WIFI_EVENT && id == WIFI_EVENT_STA_START) {
        esp_wifi_connect();
    } else if (base == WIFI_EVENT && id == WIFI_EVENT_STA_DISCONNECTED) {
        s_connected = false;
        xEventGroupSetBits(s_wifi_event_group, WIFI_FAIL_BIT);
    } else if (base == IP_EVENT && id == IP_EVENT_STA_GOT_IP) {
        s_connected = true;
        xEventGroupSetBits(s_wifi_event_group, WIFI_CONNECTED_BIT);
    }
}

static void start_sta(const char *ssid, const char *pass) {
    if (!s_sta_netif) s_sta_netif = esp_netif_create_default_wifi_sta();

    wifi_init_config_t init_cfg = WIFI_INIT_CONFIG_DEFAULT();
    ESP_ERROR_CHECK(esp_wifi_init(&init_cfg));

    ESP_ERROR_CHECK(esp_event_handler_register(WIFI_EVENT, ESP_EVENT_ANY_ID, &wifi_event_handler, NULL));
    ESP_ERROR_CHECK(esp_event_handler_register(IP_EVENT, IP_EVENT_STA_GOT_IP, &wifi_event_handler, NULL));

    wifi_config_t sta_cfg = {0};
    strncpy((char*)sta_cfg.sta.ssid, ssid, sizeof(sta_cfg.sta.ssid));
    strncpy((char*)sta_cfg.sta.password, pass, sizeof(sta_cfg.sta.password));

    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
    ESP_ERROR_CHECK(esp_wifi_set_config(WIFI_IF_STA, &sta_cfg));
    ESP_ERROR_CHECK(esp_wifi_start());
}

static void start_ap(void) {
    if (!s_ap_netif) s_ap_netif = esp_netif_create_default_wifi_ap();

    wifi_init_config_t ap_init_cfg = WIFI_INIT_CONFIG_DEFAULT();
    ESP_ERROR_CHECK(esp_wifi_init(&ap_init_cfg));

    wifi_config_t ap_cfg = {
        .ap = {
            .ssid = DEFAULT_AP_SSID,
            .ssid_len = 0,
            .password = DEFAULT_AP_PASS,
            .max_connection = 4,
            .authmode = WIFI_AUTH_WPA_WPA2_PSK
        },
    };

    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_AP));
    ESP_ERROR_CHECK(esp_wifi_set_config(WIFI_IF_AP, &ap_cfg));
    ESP_ERROR_CHECK(esp_wifi_start());

    launch_webserver();
    ESP_LOGW(MODULE_TAG, "AP activo: SSID=%s PASS=%s (abre http://192.168.4.1/)", DEFAULT_AP_SSID, DEFAULT_AP_PASS);
}

void wifi_manager_init(void) {
    if (!s_wifi_event_group) s_wifi_event_group = xEventGroupCreate();

    char stored_ssid[32] = {0};
    char stored_pass[64] = {0};

    esp_err_t load_res = wifi_credentials_load(stored_ssid, sizeof(stored_ssid), stored_pass, sizeof(stored_pass));
    if (load_res == ESP_OK && strlen(stored_ssid) > 0) {
        ESP_LOGI(MODULE_TAG, "Intentando conectar a: %s", stored_ssid);
        start_sta(stored_ssid, stored_pass);

        // Espera un poco a ver si consigue IP
        EventBits_t bits = xEventGroupWaitBits(
            s_wifi_event_group,
            WIFI_CONNECTED_BIT | WIFI_FAIL_BIT,
            pdTRUE,
            pdFALSE,
            pdMS_TO_TICKS(15000)
        );

        if (bits & WIFI_CONNECTED_BIT) {
            ESP_LOGI(MODULE_TAG, "Conectado OK (STA con IP).");
            return;
        }

        ESP_LOGW(MODULE_TAG, "No se pudo conectar con credenciales guardadas.");
        // Limpieza mínima antes de AP
        esp_wifi_stop();
        esp_wifi_deinit();
        esp_event_handler_unregister(WIFI_EVENT, ESP_EVENT_ANY_ID, &wifi_event_handler);
        esp_event_handler_unregister(IP_EVENT, IP_EVENT_STA_GOT_IP, &wifi_event_handler);
    }

    ESP_LOGW(MODULE_TAG, "Arrancando modo AP + servidor configuración...");
    start_ap();
}

bool wifi_manager_is_connected(void) {
    return s_connected;
}

bool wifi_manager_wait_connected(TickType_t timeout_ticks) {
    if (s_connected) return true;
    EventBits_t bits = xEventGroupWaitBits(
        s_wifi_event_group,
        WIFI_CONNECTED_BIT,
        pdFALSE,
        pdTRUE,
        timeout_ticks
    );
    return (bits & WIFI_CONNECTED_BIT) != 0;
}
