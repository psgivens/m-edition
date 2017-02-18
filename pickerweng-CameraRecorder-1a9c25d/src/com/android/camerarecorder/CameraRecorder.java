package com.android.camerarecorder;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Camera;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.*;
import android.widget.Button;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CameraRecorder extends Activity implements SurfaceHolder.Callback {
	private static final String TAG = "Recorder";
	public static SurfaceView mSurfaceView;
	public static SurfaceHolder mSurfaceHolder;
	public static Camera mCamera;
	public static boolean mPreviewRunning;
    private Window window;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		Button btnStart = (Button) findViewById(R.id.StartService);
		btnStart.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(CameraRecorder.this, RecorderService.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startService(intent);
				finish();
			}
		});

		Button btnStop = (Button) findViewById(R.id.StopService);
		btnStop.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				stopService(new Intent(CameraRecorder.this, RecorderService.class));
			}
		});
    }
    private Intent createServiceIntent()
    {
        return new Intent(CameraRecorder.this, RecorderService.class);
    }

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    private int taskTimeDelay;
    private PowerManager powerManager;
    @Override
    protected void onResume()
    {
        super.onResume() ;

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = powerManager.isScreenOn();
        taskTimeDelay = isScreenOn
                ? 100
                : 1000;

        if (service != null)
        {
            worker.schedule(task, taskTimeDelay, TimeUnit.MILLISECONDS);
        }
        else
        {
            Intent intent = createServiceIntent();
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }
    
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	}

    private RecorderService service;

    private Runnable task = new Runnable() {
        public void run() {
            if (service.getRecordingStatus())
            {
                stopService(new Intent(CameraRecorder.this, RecorderService.class));
                finish();
            }
            else
            {
                Intent intent = createServiceIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);
                finish();
            }
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //To change body of implemented methods use File | Settings | File Templates.
            service = ((RecorderService.CameraBinder)iBinder).getRecorder();
            worker.schedule(task, taskTimeDelay, TimeUnit.MILLISECONDS);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

}