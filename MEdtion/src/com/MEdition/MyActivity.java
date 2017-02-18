package com.MEdition;

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.widget.*;
import com.PhillipScottGivens.AccessoryService.HardwareChangedEvent;
import com.PhillipScottGivens.AccessoryService.HardwareServiceBinder;
import com.PhillipScottGivens.AccessoryService.OnHardwareChangedListener;
import com.PhillipScottGivens.Camcorder.CamcorderSplashActivity;

public class MyActivity extends Activity
        implements OnHardwareChangedListener
{
    private static final String TAG = MyActivity.class.getSimpleName();
    private Intent hardwareServiceIntent;
    private Intent camcorderActivityIntent;
    private HardwareServiceBinder binder;
    private MyService hardwareService;
    private TextView textView;
    private ToggleButton serviceButton;
    private ToggleButton ledButton;
    private ToggleButton camcorderButton;

    // We want to know this across all instances.
    private static boolean isServiceRunning;
    private static boolean isHardwareConnected;

    private CheckedChangedListener checkedChangeListener = new CheckedChangedListener();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        hardwareServiceIntent = new Intent(this, MyService.class);
        camcorderActivityIntent = new Intent(this, CamcorderSplashActivity.class);
        hardwareServiceIntent.setFlags(1);

        ledButton = (ToggleButton)findViewById(R.id.led_toggle_button);
        serviceButton = (ToggleButton)findViewById(R.id.serviceRunningButton);
        camcorderButton = (ToggleButton)findViewById(R.id.camcorderOnButton);

        ledButton.setEnabled(false);

        serviceButton.setOnCheckedChangeListener(checkedChangeListener);
        ledButton.setOnCheckedChangeListener(checkedChangeListener);
        camcorderButton.setOnCheckedChangeListener(checkedChangeListener);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Start the service if it is not already running. It is okay to call
        // start on a running service. By explicitly starting the service, it
        // will not stop when we unbind.
        startService(hardwareServiceIntent);

        // Binding to the running service. Binding will create the start the service
        // if it were not already running.
        bindService(hardwareServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Unbind the service; we are going to sleep.
        unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //To change body of implemented methods use File | Settings | File Templates.

            binder = (HardwareServiceBinder)iBinder;
            hardwareService = (MyService)binder.getHardwareService();
            binder.registerHardwareChangedListener(MyActivity.this);
            isServiceRunning = hardwareService.isStarted();
            isHardwareConnected = hardwareService.isConnected();

            // When we change the state of the button on, it will raise
            // an event. We want to suppress that event.
            checkedChangeListener.suspend();
            serviceButton.setChecked(isServiceRunning);
            checkedChangeListener.resume();
            ledButton.setEnabled(isHardwareConnected);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //To change body of implemented methods use File | Settings | File Templates.

            binder.unregisterHardwareChangedListener(MyActivity.this);
            hardwareService = null;
            binder = null;
        }
    };

    public void onHardwareConnectionChanged(HardwareChangedEvent event)
    {
        isHardwareConnected = event.getIsConnected();
        ledButton.setEnabled(isHardwareConnected);
    }


    /**
     * UI Handlers
     * **/

    private class CheckedChangedListener implements ToggleButton.OnCheckedChangeListener
    {
        private boolean areNotificationsSuspended;
        public void suspend(){
            areNotificationsSuspended = true;
        }
        public void resume(){
            areNotificationsSuspended = false;
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            if (areNotificationsSuspended)
                return;

            switch (buttonView.getId())
            {
                case R.id.led_toggle_button:
                    hardwareService.sendLedSwitchCommand(isChecked);
                    break;

                case R.id.serviceRunningButton:
                    boolean isStarted = hardwareService != null && hardwareService.isStarted();
                    if (!isChecked && isStarted)
                    {
                        hardwareService.stop();
                        Log.i(TAG, "stopService");
                        isServiceRunning = false;
                    }
                    else if (isChecked && !isStarted)
                    {
                        startService(hardwareServiceIntent);
                        Log.i(TAG, "startService");
                        isServiceRunning = true;
                    }
                    break;
                case R.id.camcorderOnButton:
                    startActivity(camcorderActivityIntent);
                    break;
            }
        }
    };
}
