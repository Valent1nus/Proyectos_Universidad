idf_component_register(SRCS "simple_ota_example.c"
                      INCLUDE_DIRS "."
                      EMBED_TXTFILES "eu-thingsboard-cloud.pem"
                      REQUIRES esp_https_ota esp_http_client esp_partition json wifi_manager)
