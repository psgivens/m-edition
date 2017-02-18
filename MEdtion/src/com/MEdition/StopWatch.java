package com.MEdition;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 6/16/12
 * Time: 1:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class StopWatch {
    private final MyService myService;
    private final byte id;
    private final Timer timer;
    private int time;
    private boolean isRunning;
    public StopWatch(MyService myService, byte id)
    {
        this.myService = myService;
        this.id = id;
        this.timer = new Timer("Stop Watch " + id, true);
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (isRunning)
                myService.sendClock(id, ++time);
        }
    };

    public void start()
    {
        isRunning = true;
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    public void stop()
    {
        isRunning = false;
        timerTask.cancel();
        timer.cancel();
        timer.purge();
    }

}
