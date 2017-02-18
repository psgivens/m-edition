package com.MEdition;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.PhillipScottGivens.AccessoryService.HardwareCommunicationService;
import com.PhillipScottGivens.AccessoryService.HardwareServiceBinder;
import com.PhillipScottGivens.Camcorder.CamcorderService;
import com.PhillipScottGivens.Camcorder.CamcorderSplashActivity;

import java.net.IDN;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 6/15/12
 * Time: 8:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyService extends HardwareCommunicationService {

    // Some weird intent constant. What is this anyway?
    private static final String ACTION_USB_PERMISSION = "com.android.MEdition.USB_PERMISSION";

    // Commands
    public static final byte COMMAND_PING = 0x01;
    public static final byte COMMAND_LED  = 0x02;
    public static final byte COMMAND_TIME = 0X03;

    // Responses
    public static final byte INCOMING_MEANING_OF_LIFE = 42;
    public static final byte INCOMING_PING_RESPONSE = 0X01;
    public static final byte INCOMING_BUTTON_PRESSED_BLACK = 0x02;
    public static final byte INCOMING_BUTTON_PRESSED_RED = 0x03;
    public static final byte INCOMING_ACCELEROMETER_DATA = 0x04;
    public static final byte INCOMING_TEMPERATURE_AND_HUMIDITY = 0x05;

    // Other arguments
    public static final byte TARGET_PIN_2 = 0x2;
    public static final byte VALUE_ON = 0x1;
    public static final byte VALUE_OFF = 0x0;

    //private final MyHardwareHandler handler = new MyHardwareHandler(this);
    private final HardwareServiceBinder binder = new HardwareServiceBinder(this, false);
    private StopWatch stopWatch;
    private boolean isLightOn;

    // intents
    private Intent camcorderActivityIntent;

    public MyService()
    {
        super(ACTION_USB_PERMISSION, COMMAND_PING);
        initialize(handler, binder);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        camcorderActivityIntent = new Intent(this, CamcorderSplashActivity.class);
        camcorderActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void onAccessoryOpen()
    {
        super.onAccessoryOpen();
        stopWatch = new StopWatch(this, (byte)42);
        stopWatch.start();
    }

    @Override
    public void onAccessoryClosing()
    {
        super.onAccessoryClosing();
        stopWatch.stop();
        stopWatch = null;
    }

    public void sendLedSwitchCommand(boolean isSwitchedOn) {
        isLightOn = isSwitchedOn;
        byte[] buffer = new byte[]
        {
          COMMAND_LED,  // Issue LED command
          TARGET_PIN_2, // LED is on pin 2
          isSwitchedOn  // Set the value based on parameters.
            ? VALUE_ON
            : VALUE_OFF
        };

        sendBuffer(buffer);
    }

    public void sendClock(byte id, int time)
    {
        byte[] bytes = new byte[6];

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put(COMMAND_TIME);
        buffer.put(id);
        buffer.putInt(2, time);

        sendBuffer(buffer.array());
    }

    private final Handler handler = new Handler()
    {
        /**
          * @param message coming back from the hardware listener thread.
         *                 The entire rest of the byte array is contained in
         *                 the message.obj object.
         */
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case INCOMING_MEANING_OF_LIFE:
                {
                    Context context = getApplicationContext();
                    CharSequence text = "We have received the meaning of life from Arduino!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    sendLedSwitchCommand(!isLightOn);
                    break;
                }

                case INCOMING_PING_RESPONSE:
                {
                    Context context = getApplicationContext();
                    CharSequence text = "We have received a response from Arduino!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }

                case INCOMING_BUTTON_PRESSED_BLACK:
                    startActivity(camcorderActivityIntent);
                    break;

                case INCOMING_BUTTON_PRESSED_RED:
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Red Button";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }

                case INCOMING_ACCELEROMETER_DATA:
                    break;

                case INCOMING_TEMPERATURE_AND_HUMIDITY:
                    break;
            }
        }
    };
}
