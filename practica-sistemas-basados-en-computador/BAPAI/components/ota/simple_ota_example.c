#include "simple_ota_example.h"

#include <stdio.h>
#include <string.h>

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

#include "esp_log.h"
#include "esp_system.h"
#include "esp_app_desc.h"

#include "esp_http_client.h"
#include "esp_https_ota.h"

#include "cJSON.h"

#include "wifi_manager.h"

// ================= Config =================
#define OTA_TAG "OTA_MGR"

#define TB_ATTR_URL \
    "https://thingsboard.cloud/api/v1/kytMdWldW0BbTziQHuf1/attributes?sharedKeys=fw_title,fw_version"

#define OTA_URL_PREFIX \
    "https://thingsboard.cloud/api/v1/kytMdWldW0BbTziQHuf1/firmware?title=ESP32-SBCM10&version="

#define HTTP_RESP_BUFFER_SIZE 512

// Buffer respuesta HTTP
static char http_response_buffer[HTTP_RESP_BUFFER_SIZE];

// Certificado embebido (ajusta el nombre del símbolo a tu .pem/EMBED_TXTFILES)
extern const char tb_root_cert_pem_start[] asm("_binary_eu_thingsboard_cloud_pem_start");
extern const char tb_root_cert_pem_end[]   asm("_binary_eu_thingsboard_cloud_pem_end");

// ================= HTTP handler =================
static esp_err_t http_event_handler(esp_http_client_event_t *evt)
{
    static int output_len = 0;

    switch (evt->event_id) {
    case HTTP_EVENT_ON_DATA:
        if (output_len + evt->data_len < HTTP_RESP_BUFFER_SIZE) {
            memcpy(http_response_buffer + output_len, evt->data, evt->data_len);
            output_len += evt->data_len;
            http_response_buffer[output_len] = '\0';
        }
        break;

    case HTTP_EVENT_ON_FINISH:
        output_len = 0;
        break;

    default:
        break;
    }
    return ESP_OK;
}

// ================= Version helpers =================
static const char *get_current_fw_version(void)
{
    const esp_app_desc_t *desc = esp_app_get_description();
    return desc ? desc->version : "unknown";
}

static int semver_cmp(const char *a, const char *b)
{
    int a1 = 0, a2 = 0, a3 = 0, b1 = 0, b2 = 0, b3 = 0;
    if (a) sscanf(a, "%d.%d.%d", &a1, &a2, &a3);
    if (b) sscanf(b, "%d.%d.%d", &b1, &b2, &b3);

    if (a1 != b1) return (a1 < b1) ? -1 : 1;
    if (a2 != b2) return (a2 < b2) ? -1 : 1;
    if (a3 != b3) return (a3 < b3) ? -1 : 1;
    return 0;
}

// ================= ThingsBoard attributes =================
static bool check_firmware_attributes(char *out_version, size_t out_version_len)
{
    if (!out_version || out_version_len == 0) return false;
    out_version[0] = '\0';

    esp_http_client_config_t config = {
        .url = TB_ATTR_URL,
        .method = HTTP_METHOD_GET,
        .event_handler = http_event_handler,
        .cert_pem = tb_root_cert_pem_start,
        .timeout_ms = 5000,
    };

    esp_http_client_handle_t client = esp_http_client_init(&config);

    esp_err_t err = esp_http_client_perform(client);
    if (err != ESP_OK) {
        ESP_LOGE(OTA_TAG, "HTTP GET error: %s", esp_err_to_name(err));
        esp_http_client_cleanup(client);
        return false;
    }

    ESP_LOGI(OTA_TAG, "HTTP GET status = %d", esp_http_client_get_status_code(client));
    ESP_LOGI(OTA_TAG, "Respuesta JSON: %s", http_response_buffer);

    bool ok = false;

    cJSON *root = cJSON_Parse(http_response_buffer);
    if (root) {
        cJSON *shared = cJSON_GetObjectItem(root, "shared");
        if (shared) {
            cJSON *v = cJSON_GetObjectItem(shared, "fw_version");
            cJSON *t = cJSON_GetObjectItem(shared, "fw_title");

            if (cJSON_IsString(v) && v->valuestring) {
                strncpy(out_version, v->valuestring, out_version_len - 1);
                out_version[out_version_len - 1] = '\0';
                ok = true;
            }

            if (cJSON_IsString(t) && t->valuestring) {
                ESP_LOGI(OTA_TAG, "FW Title: %s", t->valuestring);
            }
            if (ok) {
                ESP_LOGI(OTA_TAG, "FW Version (remota): %s", out_version);
            }
        }
        cJSON_Delete(root);
    }

    esp_http_client_cleanup(client);
    return ok;
}

// ================= OTA download/apply =================
static void do_ota_update(const char *version)
{
    char url[512];
    snprintf(url, sizeof(url), "%s%s", OTA_URL_PREFIX, version ? version : "");

    esp_http_client_config_t http_config = {
        .url = url,
        .event_handler = http_event_handler,
        .cert_pem = tb_root_cert_pem_start,
        .timeout_ms = 5000,
    };

    esp_https_ota_config_t ota_config = {
        .http_config = &http_config,
    };

    ESP_LOGI(OTA_TAG, "Descargando nuevo firmware... (%s)", url);

    esp_err_t ret = esp_https_ota(&ota_config);
    if (ret == ESP_OK) {
        ESP_LOGI(OTA_TAG, "OTA exitosa. Reiniciando...");
        vTaskDelay(pdMS_TO_TICKS(3000));
        esp_restart();
    } else {
        ESP_LOGE(OTA_TAG, "Fallo en OTA: %s. Continuando con firmware actual.", esp_err_to_name(ret));
    }
}

// ================= Task =================
static void ota_scheduler_task(void *arg)
{
    (void)arg;

    while (!wifi_manager_is_connected()) {
        vTaskDelay(pdMS_TO_TICKS(1000));
    }

    while (1) {
        char remote_ver[32];
        const char *current_ver = get_current_fw_version();

        ESP_LOGI(OTA_TAG, "FW Version (actual): %s", current_ver);

        if (check_firmware_attributes(remote_ver, sizeof(remote_ver))) {
            if (semver_cmp(current_ver, remote_ver) < 0) {
                do_ota_update(remote_ver);
            }
        }

        vTaskDelay(pdMS_TO_TICKS(24ULL * 60ULL * 60ULL * 1000ULL));
    }
}

bool ota_manager_start(uint32_t task_stack, uint32_t task_prio)
{
    BaseType_t ok = xTaskCreate(ota_scheduler_task, "ota_scheduler",
                               task_stack, NULL, (UBaseType_t)task_prio, NULL);
    return (ok == pdPASS);
}

