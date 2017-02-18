#include <Bounce.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>
#include <LiquidCrystal.h>
#include <Wire.h>

// This is what we will send to Android 
// when the button is pressed. 
#define SEND_PING_RESPONSE            0X01
#define SEND_BUTTON_PRESSED_BLACK     0x02
#define SEND_BUTTON_PRESSED_RED       0x03
#define SEND_ACCELEROMETER_DATA       0x04
#define SEND_TEMPERATURE_AND_HUMIDITY 0x05
#define SEND_THE_MEANING_OF_LIFE      42

#define COMMAND_PING                  0X01
#define COMMAND_LED                   0x02
#define COMMAND_TIME                  0x03
#define COMMAND_FLASH_LED             0x04

#define MATRIX_COMMAND_ACCELEROMETER 0x01
#define MATRIX_COMMAND_FLASH         0x02

#define TARGET_PIN_2                  0x02
#define VALUE_ON                      0x1
#define VALUE_OFF                     0x0

#define LED_PIN       32
#define BUTTON_PIN    22
#define BUTTON_RED    24
#define BUTTON_BLACK  26
#define LCD_BACKLIGHT 8

AndroidAccessory acc("PhillipScottGivens",
"MEdition",
"An awesome accessory for my car.",
"1.0",
"URI",
"Serial");

// Connections:
// rs (LCD pin 4) to Arduino pin 4
// rw (LCD pin 5) to Arduino pin 5
// enable (LCD pin 6) to Arduino pin 6
// LCD pin 15 to Arduino pin 8
// LCD pins d4, d5, d6, d7 to Arduino pins 10, 11, 12, 13
LiquidCrystal lcd(4, 5, 6, 10, 11, 12, 13);

byte rcvmsg[1024];
byte sntmsg[3];
int ledValue = LOW;
Bounce bouncer = Bounce(BUTTON_PIN, 5);
Bounce bouncer6 = Bounce(BUTTON_RED, 5);
Bounce bouncer5 = Bounce(BUTTON_BLACK, 5);

void setup() {
  Serial.begin(19200);
  Serial.println("Up and running");
  acc.powerOn();

  pinMode(LCD_BACKLIGHT, OUTPUT);
  digitalWrite(LCD_BACKLIGHT, OUTPUT);
  lcd.begin(20,4);              // columns, rows.  use 16,2 for a 16x2 LCD, etc.
  lcd.clear();                  // start with a blank screen
  lcd.setCursor(0,0);           // set cursor to column 0, row 0 (the first row)
  lcd.print("Hello, World");    // change this text to whatever you like. keep it clean.
  lcd.setCursor(0,1);           // set cursor to column 0, row 1
  lcd.print("Text beats a");
  lcd.setCursor(0,2);
  lcd.print("blinking light any");
  lcd.setCursor(0,3);
  lcd.print("day of the week.");

  Wire.begin(); // join i2c bus (address optional for master)

  pinMode(LED_PIN, OUTPUT);  

  pinMode(BUTTON_PIN, INPUT);
  digitalWrite(BUTTON_PIN, HIGH);

  pinMode(BUTTON_RED, INPUT);
  digitalWrite(BUTTON_RED, HIGH);

  pinMode(BUTTON_BLACK, INPUT);
  digitalWrite(BUTTON_BLACK, HIGH);
}

byte current = 0;
void loop() {

  if (++current > 20)
    current = 0;

  Wire.beginTransmission(4); // transmit to device #4
  Wire.write(MATRIX_COMMAND_ACCELEROMETER);
  Wire.write(current);       // x: sends one byte
  Wire.write(current + 1);       // y: sends one byte  
  Wire.endTransmission();    // stop transmitting
  Serial.println(current, DEC);
  delay(200);

  if (acc.isConnected()) {        

    //read the received data into the byte array 
    int len = acc.read(rcvmsg, sizeof(rcvmsg), 1);
    if (len > 0) {
      switch(rcvmsg[0])
      {
      case COMMAND_LED:        
        if (rcvmsg[1] == TARGET_PIN_2){
          //get the switch state
          byte value = rcvmsg[2];
          //set output pin to according state
          if(value == VALUE_ON) {
            digitalWrite(LED_PIN, HIGH);
          } 
          else if(value == VALUE_OFF) {
            digitalWrite(LED_PIN, LOW);
          }          
        }
        break;

      case COMMAND_PING:
        sntmsg[0] = SEND_PING_RESPONSE;
        acc.write(sntmsg, 1);             
        break;

      case COMMAND_TIME:
        ledValue = !ledValue;
        digitalWrite(LED_PIN, ledValue);
        Serial.print("received from ");
        Serial.println(rcvmsg[1], DEC);
        long returnValue = 256;
        returnValue += ( rcvmsg[2] << 24);
        returnValue += ((rcvmsg[3] << 16));
        returnValue += ((rcvmsg[4] << 8));
        returnValue += ( rcvmsg[5]);
        Serial.print("data: ");
        Serial.println(returnValue, DEC);
        break;
      }
    }

    if (bouncer.update() && bouncer.read() == LOW)
    {
      sntmsg[0] = SEND_THE_MEANING_OF_LIFE;
      acc.write(sntmsg, 1);     
    }
    else if (bouncer6.update() && bouncer6.read() == LOW)
    {
      sntmsg[0] = SEND_BUTTON_PRESSED_BLACK;
      acc.write(sntmsg, 1);     
    }
    else if (bouncer5.update() && bouncer5.read() == LOW)
    {
      sntmsg[0] = SEND_BUTTON_PRESSED_RED;
      acc.write(sntmsg, 1);     
    }
  }
  else if (bouncer.update() && bouncer.read() == LOW)
  {
    ledValue = !ledValue;
    digitalWrite(LED_PIN, ledValue);
  }
  else if (bouncer6.update() && bouncer6.read() == LOW)
  {
    ledValue = !ledValue;
    digitalWrite(LED_PIN, ledValue);
  }
  else if (bouncer5.update() && bouncer5.read() == LOW)
  {
    ledValue = !ledValue;
    digitalWrite(LED_PIN, ledValue);
  }

}





