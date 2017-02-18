package com.PhillipScottGivens;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import com.PhillipScottGivens.Led.LedMatrix;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 5/26/12
 * Time: 9:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class CarAccelermoterActivity extends Activity implements SensorEventListener {
    private LedMatrix leds;

    private final int CarColor = 0xFF008100;

    TextView xCoor; // declare X axis object
    TextView yCoor; // declare Y axis object
    TextView zCoor; // declare Z axis object

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.led_accelermeter_car);
        leds = (LedMatrix)findViewById(R.id.ledMatrix);

        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        xCoor=(TextView)findViewById(R.id.xCoordinate); // create X axis object
        yCoor=(TextView)findViewById(R.id.yCoordinate); // create Y axis object
        zCoor=(TextView)findViewById(R.id.zCoordinate); // create Z axis object

        drawCar();
    }

    private void drawCar()
    {
        for(int x=3;x<=5;x++)
        {
            leds.setLed(x, 3, CarColor);
            leds.setLed(x, 4, CarColor);
        }
    }

    private void drawX(float xcoord)
    {
        int firstColor = Color.BLACK;
        int secondColor = Color.BLACK;
        int thirdColor = Color.BLACK;

        if (xcoord > 3f || xcoord < -3.0)
        {
            firstColor = 0x00FFFF00;
            if (xcoord > 6f || xcoord < -6.0)
            {
                secondColor = 0xFFFF8800;
                if (xcoord > 8f || xcoord < -8.0)
                {
                    thirdColor = 0xffFF0000;
                }
            }
        }

        for(int x=3;x<=5;x++)
        {
            if (xcoord < 0)
            {
                leds.setLed(x, 5, firstColor);
                leds.setLed(x, 6, secondColor);
                leds.setLed(x, 7, thirdColor);
            }
            else
            {
                leds.setLed(x, 2, firstColor);
                leds.setLed(x, 1, secondColor);
                leds.setLed(x, 0, thirdColor);
            }
        }
    }

    private void drawY(float ycoord)
    {
        int firstColor = Color.BLACK;
        int secondColor = Color.BLACK;
        int thirdColor = Color.BLACK;

        if (ycoord > 3f || ycoord < -3.0)
        {
            firstColor = 0x00FFFF00;
            if (ycoord > 6f || ycoord < -6.0)
            {
                secondColor = 0xFFFF8800;
                if (ycoord > 8f || ycoord < -8.0)
                {
                    thirdColor = 0xffFF0000;
                }
            }
        }

        for(int y=3;y<5;y++)
        {
            if (ycoord > 0)
            {
                leds.setLed(6, y, firstColor);
                leds.setLed(7, y, secondColor);
            }
            else
            {
                leds.setLed(2, y, firstColor);
                if (thirdColor == Color.BLACK)
                {
                    leds.setLed(1, y, secondColor);
                }
                else
                {
                    leds.setLed(1, y, thirdColor);
                }
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // check sensor type
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

            // assign directions
            float x=sensorEvent.values[0];
            float y=sensorEvent.values[1];
            float z=sensorEvent.values[2];

            xCoor.setText("X: "+x);
            yCoor.setText("Y: "+y);
            zCoor.setText("Z: "+z);

            drawX(x);
            drawY(y);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}