package com.PhillipScottGivens;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 5/22/12
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class HardwareMonitorService extends Service {

    public class TimeBinder extends Binder
    {
        private MainActivity activity;
        public void registerActivity(MainActivity activity)
        {
            this.activity = activity;
        }
        public void notifyActivity(TimeModel model)
        {
            activity.update(model);
        }
        public HardwareMonitorService getHardwareMonitor()
        {
            return HardwareMonitorService.this;
        }
    }

    private TimeBinder binder = new TimeBinder();
    private TimeModel model;
    private Handler handler = new Handler();
    public boolean isMonitoring = false;
    private Runnable timerTask = new Runnable()
    {
        @Override
        public void run()
        {
            Calendar now = Calendar.getInstance();
            model.updateCurrentTime(DateTime.now());
            handler.postDelayed(timerTask, 1000);
            binder.notifyActivity(model);
        }
    };

    public void startMonitoring()
    {
        model = new TimeModel(DateTime.now());
        handler.post(timerTask);
        isMonitoring =true;
    }

    public void stopMonitoring()
    {
        handler.removeCallbacks(timerTask);
        isMonitoring = false;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    public IBinder onBind(Intent intent) {
        return binder;
    }
}
