package com.PhillipScottGivens.Camcorder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.*;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CamcorderSplashActivity extends Activity implements ToggleButton.OnCheckedChangeListener {
	private static final String TAG = "Recorder";
//	public static SurfaceView mSurfaceView;
//	public static SurfaceHolder mSurfaceHolder;
	//public static Camera mCamera;
	public static boolean mPreviewRunning;
    private Window window;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_splash);

        window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);


        ToggleButton serviceButton = (ToggleButton)findViewById(R.id.camera);
        serviceButton.setOnCheckedChangeListener(this);

//		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
//		mSurfaceHolder = mSurfaceView.getHolder();
//		mSurfaceHolder.addCallback(this);
//		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
//		Button btnStart = (Button) findViewById(R.id.StartService);
//		btnStart.setOnClickListener(new View.OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				Intent intent = new Intent(CamcorderSplashActivity.this, CamcorderService.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startService(intent);
//				finish();
//			}
//		});
//
//		Button btnStop = (Button) findViewById(R.id.StopService);
//		btnStop.setOnClickListener(new View.OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				stopService(new Intent(CamcorderSplashActivity.this, CamcorderService.class));
//			}
//		});
    }
    private Intent createServiceIntent()
    {
        return new Intent(CamcorderSplashActivity.this, CamcorderService.class);
    }

//    private static final ScheduledExecutorService worker =
//            Executors.newSingleThreadScheduledExecutor();

    private int taskTimeDelay;
//    private PowerManager powerManager;
    @Override
    protected void onResume()
    {
        super.onResume() ;

//        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        boolean isScreenOn = powerManager.isScreenOn();
//        taskTimeDelay = isScreenOn
//                ? 100
//                : 1000;

//        if (service != null)
//        {
//            worker.schedule(task, taskTimeDelay, TimeUnit.MILLISECONDS);
//        }
//        else
//        {
//            Intent intent = createServiceIntent();
//            startService(intent);
//            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//        }

        if (service == null)
        {
            Intent intent = createServiceIntent();
            startService(intent);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        else
        {
            toggleRecording();
        }
    }

    public void toggleRecording()
    {
        if (service.getRecordingStatus())
        {
            service.stopRecording();
            //finish();
        }
        else
        {
            Intent intent = createServiceIntent();
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);
            service.startRecording();
            //finish();
        }
    }

    public void onCheckedChanged(CompoundButton button, boolean value)
    {
        if (!value && service.getRecordingStatus())
        {
            service.stopRecording();
            //finish();
        }
        else if (value && !service.getRecordingStatus())
        {
            Intent intent = createServiceIntent();
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);
            service.startRecording();
            //finish();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        unbindService(serviceConnection);
    }

//	@Override
//	public void surfaceCreated(SurfaceHolder holder) {
//
//	}
//
//	@Override
//	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//	}
//
//	@Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		// TODO Auto-generated method stub
//	}

    private CamcorderService service;

//    private Runnable task = new Runnable() {
//        public void run() {
//            if (service.getRecordingStatus())
//            {
//                service.stopRecording();
//                finish();
//            }
//            else
//            {
//                Intent intent = createServiceIntent();
//                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startService(intent);
//                service.startRecording();
//                finish();
//            }
//        }
//    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //To change body of implemented methods use File | Settings | File Templates.
            service = ((CamcorderService.CameraBinder)iBinder).getRecorder();
//            worker.schedule(task, taskTimeDelay, TimeUnit.MILLISECONDS);

            toggleRecording();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

}