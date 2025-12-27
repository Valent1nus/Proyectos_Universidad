idf_component_register(
    SRCS "wifi_manager.c"
    INCLUDE_DIRS "."
    REQUIRES driver nvs_flash esp_wifi esp_http_server
)
