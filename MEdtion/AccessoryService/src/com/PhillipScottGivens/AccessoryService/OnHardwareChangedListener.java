package com.PhillipScottGivens.AccessoryService;

import java.util.EventListener;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 6/13/12
 * Time: 7:40 PM
 * To change this template use File | Settings | File Templates.
 */
public interface OnHardwareChangedListener extends EventListener{
    void onHardwareConnectionChanged(HardwareChangedEvent event);
}
