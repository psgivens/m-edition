// character LCD example code
// www.hacktronics.com

#include <LiquidCrystal.h>
#include <Bounce.h>

#define BUTTON_PIN 2


// Connections:
// rs (LCD pin 4) to Arduino pin 12
// rw (LCD pin 5) to Arduino pin 11
// enable (LCD pin 6) to Arduino pin 10
// LCD pin 15 to Arduino pin 13
// LCD pins d4, d5, d6, d7 to Arduino pins 5, 4, 3, 2
LiquidCrystal lcd(12, 11, 10, 7, 6, 5, 4);

int backLight = 13;    // pin 13 will control the backlight
int buttonPin = BUTTON_PIN;
int displayValue = LOW;

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
  
  // if you have a 4 row LCD, uncomment these lines to write to the bottom rows
  // and change the lcd.begin() statement above.
  //lcd.setCursor(0,2);         // set cursor to column 0, row 2
  //lcd.print("Row 3");
  //lcd.setCursor(0,3);         // set cursor to column 0, row 3
  //lcd.print("Row 4");
}

void loop()
{
  if (bouncer.update() && bouncer.read() == LOW)
  {
    displayValue = !displayValue;
    digitalWrite(backLight, displayValue);
    if (displayValue == HIGH)
    {
      lcd.setCursor(0,0);           // set cursor to column 0, row 0 (the first row)
      lcd.print("Phillip Loves Irene");    // change this text to whatever you like. keep it clean.
    }
    else    
    {
      lcd.clear();
    }
  }
//  lcd.begin(20,4);              // columns, rows.  use 16,2 for a 16x2 LCD, etc.
//  lcd.clear();                  // start with a blank screen
//  lcd.setCursor(0,0);           // set cursor to column 0, row 0 (the first row)
//  lcd.print("Hello, World");    // change this text to whatever you like. keep it clean.
//  lcd.setCursor(0,1);           // set cursor to column 0, row 1
//  lcd.print("hacktronics.com");
}
