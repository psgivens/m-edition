package com.PhillipScottGivens;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class MainActivity extends Activity implements View.OnClickListener
{
    private TextView clockTextView;
    private boolean isRunning = false;
    private HardwareMonitorService hardwareMonitor;
    private Intent monitorIntent;
    private Intent launchDrawIntent;
    private Intent launchAccelerometerIntent;
    private Intent cameraIntent;

    private final String TAG = "MainActivity";


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button startButton = (Button)findViewById(R.id.startClockButton);
        Button stopButton = (Button)findViewById(R.id.stopClockButton);
        Button launchDraw = (Button)findViewById(R.id.launchDrawButton);
        Button launchAccelerometerButton = (Button)findViewById(R.id.launchAccelermeterButton);
        Button cameraButton = (Button)findViewById(R.id.cameraButton);
        clockTextView = (TextView)findViewById(R.id.clockTextView);

        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        launchDraw.setOnClickListener(this);
        cameraButton.setOnClickListener(this);
        launchAccelerometerButton.setOnClickListener(this);

        monitorIntent = new Intent(this, HardwareMonitorService.class);
        launchDrawIntent = new Intent(this, LedDrawActivity.class);
        launchAccelerometerIntent = new Intent(this, CarAccelermoterActivity.class);
        cameraIntent = new Intent(this, AndroidVideoCapture.class);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        startService(monitorIntent);

        bindService(monitorIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (hardwareMonitor != null && !hardwareMonitor.isMonitoring)
        {
            stopService(monitorIntent);
        }
        unbindService(serviceConnection);

    }

    public void update(TimeModel model)
    {
        if (model == null)
            return;

        Period elapsed = model.getElapsedTime();

        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendHours().appendSuffix(" hours, ")
                .appendMinutes().appendSuffix(" minutes, ")
                .appendSeconds().appendSuffix(" seconds")
                .toFormatter();

        String elapsedText = formatter.print(elapsed);

        clockTextView.setText(elapsedText);
    }

    private HardwareMonitorService.TimeBinder timeBinder;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //To change body of implemented methods use File | Settings | File Templates.
            timeBinder = (HardwareMonitorService.TimeBinder)iBinder;
            timeBinder.registerActivity(MainActivity.this);

            hardwareMonitor = timeBinder.getHardwareMonitor();
            //update(timeBinder.getModel());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //To change body of implemented methods use File | Settings | File Templates.
            timeBinder.registerActivity(null);
            timeBinder=null;
            hardwareMonitor = null;
        }
    };


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.startClockButton:
                hardwareMonitor.startMonitoring();
                break;
            case R.id.stopClockButton:
                hardwareMonitor.stopMonitoring();
                break;
            case R.id.launchDrawButton:
                startActivity(launchDrawIntent);
                break;
            case R.id.launchAccelermeterButton:
                startActivity(launchAccelerometerIntent);
                break;
            case R.id.cameraButton:
                startActivity(cameraIntent);
                break;
        }
    }

}
