package com.PhillipScottGivens.AccessoryService;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.util.Log;
import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 6/9/12
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class HardwareCommunicationService extends Service {

    /** Fields **/
    private static final String TAG = HardwareCommunicationService.class.getSimpleName();
    private PendingIntent permissionIntent;
    private final String ACTION_USB_PERMISSION;
    private final byte COMMAND_PING;
    private boolean permissionRequestPending;
    private UsbManager usbManager;
    private UsbAccessory accessory;
    private ParcelFileDescriptor parcelFileDescriptor;
    private FileOutputStream outputStream;
    private int startId;
    private HardwareServiceBinder binder;
    private HardwareListenerThread listenerThread;
    private Handler handler;

    /**
     * Initialize and Tear down
     * **/

    /**
     * .ctor
     * @param ACTION_USB_PERMISSION string used for issuing the permissions intent.
     * @param COMMAND_PING byte value used for issuing the ping command to the arduino.
     */
    public HardwareCommunicationService(
            String ACTION_USB_PERMISSION,
            byte COMMAND_PING)
    {
        this.ACTION_USB_PERMISSION = ACTION_USB_PERMISSION;
        this.COMMAND_PING = COMMAND_PING;
    }

    protected void initialize(Handler handler, HardwareServiceBinder binder)
    {
        this.handler = handler;
        this.binder = binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        usbManager = UsbManager.getInstance(this);
        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(usbReceiver, filter);
    }

    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
    }

    /**
     * Starting, Binding and Stopping Service
     * **/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        this.startId = startId;
        if (listenerThread != null)
            return START_STICKY;

        UsbAccessory[] accessories = usbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            if (usbManager.hasPermission(accessory)) {
                openAccessory(accessory);
            } else {
                synchronized (usbReceiver) {
                    if (!permissionRequestPending) {
                        usbManager.requestPermission(accessory, permissionIntent);
                        permissionRequestPending = true;
                    }
                }
            }
        } else {
            Log.i(TAG, "accessory is null");
        }

        return START_STICKY;
    }

    public void stop()
    {
        if (listenerThread != null)
        {
            closeAccessory();
        }
        startId = 0;
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
       return binder;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }

    public boolean isStarted()
    {
        return startId != 0;
    }


    /**
     * USB Accessory Connection
     * **/

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = UsbManager.getAccessory(intent);
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openAccessory(accessory);
                    } else {
                        Log.d(TAG, "permission denied for accessory " + accessory);
                    }
                    permissionRequestPending = false;
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = UsbManager.getAccessory(intent);
                if (accessory != null && accessory.equals(accessory)) {
                    closeAccessory();
                }
            }
        }
    };


    private void openAccessory(UsbAccessory accessory)
    {
        if (listenerThread != null)
            return;

        parcelFileDescriptor = usbManager.openAccessory(accessory);
        if (parcelFileDescriptor != null) {
            this.accessory = accessory;
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            FileInputStream inputStream = new FileInputStream(fileDescriptor);
            outputStream = new FileOutputStream(fileDescriptor);
            listenerThread = new HardwareListenerThread(inputStream, handler);
            listenerThread.start();
            binder.setHardwareConnection(true);
            Log.i(TAG, "streams open");
        } else {
            Log.e(TAG, "accessory open fail");
        }

        onAccessoryOpen();
    }

    public void onAccessoryOpen(){}

    private void closeAccessory() {
        if (listenerThread == null)
            return;

        try
        {
            onAccessoryClosing();
        }
        finally {
            try {
                // flag the reading thread to stop.
                listenerThread.stopListening();

                // elicit a response to unblock the
                // listening thread from the input stream.
                ping();

                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                    outputStream.close();
                }
                binder.setHardwareConnection(false);

                listenerThread.join();

                Log.i(TAG, "streams closed");
            } catch (IOException e) {
                Log.e(TAG, "close accessory error", e);
            } catch (InterruptedException e){
                Log.e(TAG, "close accessory error", e);
            } finally {
                parcelFileDescriptor = null;
                accessory = null;
                outputStream = null;
                listenerThread = null;
                onAccessoryClosed();
            }
        }
    }

    public void onAccessoryClosing() {}
    public void onAccessoryClosed() {}

    public boolean isConnected()
    {
        return accessory != null;
    }

    public void ping()
    {
        byte [] buffer = new byte[]{ COMMAND_PING };
        sendBuffer(buffer);
    }

    protected void sendBuffer(byte[] buffer)
    {
        try {
            if (outputStream != null)
                outputStream.write(buffer);
        } catch (IOException e) {
            Log.e(TAG, "write failed", e);
        }
    }
}
