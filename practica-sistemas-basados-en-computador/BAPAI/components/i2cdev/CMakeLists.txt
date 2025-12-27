# ESP-IDF CMake component for i2cdev library
set(req driver freertos esp_idf_lib_helpers)

# 🔧 Fuerza uso del driver legacy (para evitar conflicto con driver_ng)
set(USE_LEGACY_DRIVER TRUE)
message(STATUS "i2cdev: Forced legacy driver (manual override for ESP-IDF 5.3.1)")

if(USE_LEGACY_DRIVER)
    set(SRCS "i2cdev_legacy.c")
    message(STATUS "i2cdev: Compiling with legacy I2C driver (i2cdev_legacy.c)")
else()
    set(SRCS "i2cdev.c")
    message(STATUS "i2cdev: Compiling with new I2C master driver (i2cdev.c)")
endif()

idf_component_register(SRCS ${SRCS}
                       INCLUDE_DIRS "."
                       REQUIRES ${req})
