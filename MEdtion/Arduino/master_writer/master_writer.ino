// Wire Master Writer
// by Nicholas Zambetti <http://www.zambetti.com>

// Demonstrates use of the Wire library
// Writes data to an I2C/TWI slave device
// Refer to the "Wire Slave Receiver" example for use with this

// Created 29 March 2006

// This example code is in the public domain.


#include <Wire.h>

#define MATRIX_COMMAND_ACCELEROMETER 0x01
#define MATRIX_COMMAND_FLASH         0x02

void setup()
{
  Wire.begin(); // join i2c bus (address optional for master)
}

byte x = 0;
byte current = 0;

void loop()
{
  if (++current > 20)
    current = 0;
    
  Wire.beginTransmission(4); // transmit to device #4
  Wire.write(MATRIX_COMMAND_ACCELEROMETER);
  Wire.write(current);       // x: sends one byte
  Wire.write(current + 1);       // y: sends one byte  
  Wire.endTransmission();    // stop transmitting

  x++;
  delay(500);
}
