// character LCD example code
// www.hacktronics.com

#include <LiquidCrystal.h>
#include <Bounce.h>
#include "DHT.h"

#define BUTTON_PIN 2
#define DHT_PIN 8
#define DHTTYPE DHT11   // DHT 11 

// Connections:
// rs (LCD pin 4) to Arduino pin 12
// rw (LCD pin 5) to Arduino pin 11
// enable (LCD pin 6) to Arduino pin 10
// LCD pin 15 to Arduino pin 13
// LCD pins d4, d5, d6, d7 to Arduino pins 5, 4, 3, 2
LiquidCrystal lcd(12, 11, 10, 7, 6, 5, 4);
DHT dht(DHT_PIN, DHTTYPE);
int backLight = 13;    // pin 13 will control the backlight
int buttonPin = BUTTON_PIN;
int displayValue = LOW;
boolean displayWithTimeout = true;

Bounce bouncer = Bounce(buttonPin, 5);

void setup()
{
  pinMode(backLight, OUTPUT);
  digitalWrite(backLight, LOW); 
  lcd.begin(20,4);              // columns, rows.  use 16,2 for a 16x2 LCD, etc.
  lcd.clear();                  // start with a blank screen

  
  // initialize the pushbutton pin as an input:
  pinMode(buttonPin, INPUT);     
  digitalWrite(buttonPin, HIGH);
  attachInterrupt(0, showDisplay, FALLING);
  dht.begin();
  Serial.begin(9600); 
  Serial.println("DHTxx test!");
}

void loop()
{
  if (displayWithTimeout)
  {    
    displayWithTimeout = false;
    Serial.println("Button Pressed.");
    digitalWrite(backLight, HIGH);
    lcd.clear();
    lcd.setCursor(0,0);           // set cursor to column 0, row 0 (the first row)
    lcd.print("Phillip Loves Irene");    // change this text to whatever you like. keep it clean.
    
    // Reading temperature or humidity takes about 250 milliseconds!
    // Sensor readings may also be up to 2 seconds 'old' (its a very slow sensor)
    float h = dht.readHumidity();
    float t = dht.readTemperature();
  
    // check if returns are valid, if they are NaN (not a number) then something went wrong!
    if (isnan(t) || isnan(h)) {
      lcd.setCursor(0,3);
      Serial.println("Failed to read from DHT");
      lcd.print("Failed to read from DHT");
    } else {
      Serial.println("Read from DHT");
      lcd.setCursor(0,2);
      lcd.print("Humidity: "); 
      lcd.print(h);

      lcd.setCursor(0,3);
      lcd.print("Temp: "); 
      lcd.print(t);
      lcd.print(" *C");
    }  
    
    delay(1000);
  }
  else    
  {
    digitalWrite(backLight, LOW);
    lcd.clear();
  }
  
//  lcd.begin(20,4);              // columns, rows.  use 16,2 for a 16x2 LCD, etc.
//  lcd.clear();                  // start with a blank screen
//  lcd.setCursor(0,0);           // set cursor to column 0, row 0 (the first row)
//  lcd.print("Hello, World");    // change this text to whatever you like. keep it clean.
//  lcd.setCursor(0,1);           // set cursor to column 0, row 1
//  lcd.print("hacktronics.com");
}


void showDisplay()
{
  displayWithTimeout = true;
}











