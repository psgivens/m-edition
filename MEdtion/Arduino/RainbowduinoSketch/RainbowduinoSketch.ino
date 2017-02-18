#include <Rainbowduino.h>
#include <Wire.h>

#define FIRST_COLOR   0xFFFF00
#define SECOND_COLOR  0xFF8800
#define THIRD_COLOR   0xFF0000

#define COMMAND_ACCELEROMETER 0x01
#define COMMAND_FLASH         0x02

long carColor = 0x008100;
long BLACK = 0X00;


void setup()
{
  Rb.init(); //initialize Rainbowduino driver

  Wire.begin(4);                // join i2c bus with address #4
  Wire.onReceive(receiveEvent); // register event
  Serial.begin(9600);           // start serial for output

  Serial.println("Rainbowduino setup");
}


void loop()
{
  //  for(int x=0;x<8;x++)
  //  {
  //    for(int y=0;y<4;y++)
  //    {
  //      Rb.setPixelXY(x,y, 0x222222); //uses R, G and B color bytes
  //    }
  //    for(int y=5;y<8;y++)
  //    {
  //      Rb.setPixelXY(x,y, 0x00, 0x00, 0x00); //uses R, G and B color bytes
  //    }
  //  }
  drawCar();
  delay(100);

}


// function that executes whenever data is received from master
// this function is registered as an event, see setup()
void receiveEvent(int howMany)
{
  byte command = Wire.read();
  switch(command)
  {
    case COMMAND_ACCELEROMETER:
    {
      byte x = Wire.read();
      byte y = Wire.read();
      
      drawCar();
      drawX(x - 10);
      drawY(y - 10);
      break;
    } 
    case COMMAND_FLASH:
      break;
  }

//  while(Wire.available() > 1) // loop through all but the last
//  {
//    b = Wire.read(); // receive byte as a character    
//    Serial.print("receivedx: ");
//    Serial.print(b, DEC);         // print the character
//  }
//  b = Wire.read();    // receive byte as an integer
//  Serial.print("received: ");
//  Serial.println(b, DEC);         // print the integer

}


void drawCar()
{
  for(int x=3;x<=5;x++)
  {
    Rb.setPixelXY(x, 3, carColor);
    Rb.setPixelXY(x, 4, carColor);
  }
}

void drawX(int xcoord)
{
  long firstColor = BLACK;
  long secondColor = BLACK;
  long thirdColor = BLACK;

  if (xcoord > 3 || xcoord < -3)
  {
    firstColor = FIRST_COLOR;
    if (xcoord > 6 || xcoord < -6)
    {
      secondColor = SECOND_COLOR;
      if (xcoord > 8 || xcoord < -8)
      {
        thirdColor = THIRD_COLOR;
      }
    }
  }

  for(int x=3;x<=5;x++)
  {
    if (xcoord < 0)
    {
      Rb.setPixelXY(x, 2, BLACK);
      Rb.setPixelXY(x, 1, BLACK);
      Rb.setPixelXY(x, 0, BLACK);

      Rb.setPixelXY(x, 5, firstColor);
      Rb.setPixelXY(x, 6, secondColor);
      Rb.setPixelXY(x, 7, thirdColor);
    }
    else
    {
      Rb.setPixelXY(x, 2, firstColor);
      Rb.setPixelXY(x, 1, secondColor);
      Rb.setPixelXY(x, 0, thirdColor);

      Rb.setPixelXY(x, 5, BLACK);
      Rb.setPixelXY(x, 6, BLACK);
      Rb.setPixelXY(x, 7, BLACK);
    }
  }
}


void drawY(int ycoord)
{
  long firstColor = BLACK;
  long secondColor = BLACK;
  long thirdColor = BLACK;

  if (ycoord > 3 || ycoord < -3)
  {
    firstColor = FIRST_COLOR;
    if (ycoord > 6 || ycoord < -6)
    {
      secondColor = SECOND_COLOR;
      if (ycoord > 8 || ycoord < -8)
      {
        thirdColor = THIRD_COLOR;
      }
    }
  }

  for(int y=3;y<5;y++)
  {
    if (ycoord > 0)
    {
      Rb.setPixelXY(6, y, firstColor);
      Rb.setPixelXY(7, y, thirdColor == BLACK
        ? secondColor
        : thirdColor);

      Rb.setPixelXY(2, y, BLACK);
      Rb.setPixelXY(1, y, BLACK);      
    }
    else
    {
      Rb.setPixelXY(2, y, firstColor);
      Rb.setPixelXY(1, y, thirdColor == BLACK
        ? secondColor
        : thirdColor);

      Rb.setPixelXY(6, y, BLACK);
      Rb.setPixelXY(7, y, BLACK);      
    }
  }
}






