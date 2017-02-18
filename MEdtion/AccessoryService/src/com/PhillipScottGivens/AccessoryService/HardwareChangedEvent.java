package com.PhillipScottGivens.AccessoryService;

import java.util.EventObject;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 6/13/12
 * Time: 7:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class HardwareChangedEvent extends EventObject {
    private final boolean isConnected;
    public HardwareChangedEvent(Object source, boolean isConnected)
    {
        super(source);
        this.isConnected = isConnected;
    }
    public boolean  getIsConnected()
    {
        return isConnected;
    }
}
