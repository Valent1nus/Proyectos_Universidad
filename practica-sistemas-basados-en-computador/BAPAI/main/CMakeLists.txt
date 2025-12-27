idf_component_register(SRCS "main.c"
                       INCLUDE_DIRS "."
			REQUIRES wifi_manager bme280 ota mq135 pir bme280_sensor json mqtt)
