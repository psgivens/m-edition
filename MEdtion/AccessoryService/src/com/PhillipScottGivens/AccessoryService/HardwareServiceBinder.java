package com.PhillipScottGivens.AccessoryService;

import android.os.Binder;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 6/9/12
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class HardwareServiceBinder extends Binder{
    private HardwareCommunicationService hardwareService;
    private boolean isConnected;
    private final ArrayList<OnHardwareChangedListener> listeners
            = new ArrayList<OnHardwareChangedListener>();

    public HardwareServiceBinder(HardwareCommunicationService hardwareService, boolean isConnected)
    {
        this.hardwareService = hardwareService;
        this.isConnected = isConnected;
    }

    public HardwareCommunicationService getHardwareService()
    {
        return hardwareService;
    }

    public void setHardwareConnection(boolean isConnected)
    {
        this.isConnected = isConnected;
        notifyHardwareChanged(isConnected);
    }

    public void registerHardwareChangedListener(OnHardwareChangedListener listener)
    {
        listeners.add(listener);
    }
    public void unregisterHardwareChangedListener(OnHardwareChangedListener listener)
    {
        listeners.remove(listener);
    }
    private void notifyHardwareChanged(boolean isConnected)
    {
        HardwareChangedEvent event
                = new HardwareChangedEvent(this, isConnected);
        int numListeners = listeners.size();
        for (int i = 0; i<numListeners; i++)
        {
            ((OnHardwareChangedListener)listeners.get(i))
                    .onHardwareConnectionChanged(event);
        }
    }

}
