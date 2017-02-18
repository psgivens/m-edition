package com.PhillipScottGivens.AccessoryService;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 6/14/12
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class HardwareListenerThread extends Thread {
    private static final String TAG = HardwareCommunicationService.class.getSimpleName();
    private final FileInputStream inputStream;
    private Handler handler;
    private volatile boolean isRunning = true;

    public HardwareListenerThread(FileInputStream inputStream, Handler handler)
    {
        super("HardwareListenerThread");

        this.inputStream = inputStream;
        this.handler = handler;
    }

    public void run()
    {
        Log.i(TAG, "thread started");
        int ret = 0;
        byte[] buffer = new byte[16384];
        int index;

        while (isRunning && ret >= 0) {
            try {
                ret = inputStream.read(buffer);
            } catch (IOException e) {
                Log.e(TAG, "read failed", e);
                break;
            }

            if (!isRunning)
                return;

            byte command = buffer[0];
            byte[] data = Arrays.copyOf(buffer, ret);
            Message message = handler.obtainMessage(command, data);
            message.sendToTarget();
        }
        Log.i(TAG, "thread ended");
    }

    public void stopListening()
    {
        isRunning = false;
        try {
            inputStream.close();
        }catch (IOException e){}
    }
}
