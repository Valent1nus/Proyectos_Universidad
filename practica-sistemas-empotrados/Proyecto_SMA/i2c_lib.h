// i2c_lib.h
#ifndef I2C_LIB_H
#define I2C_LIB_H

#include <stdint.h>

void I2C_Master_Init(const unsigned long c);
void I2C_Master_Wait(void);
void I2C_Master_Start(void);
void I2C_Master_Stop(void);
void I2C_Master_Write(unsigned char d);
unsigned char I2C_Master_Read(unsigned char a);

#endif