package com.PhillipScottGivens.Camcorder;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class CamcorderService extends Service implements SurfaceHolder.Callback {
	private static final String TAG = "CamcorderService";
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mServiceCamera;
	private boolean mRecordingStatus;
	private MediaRecorder mMediaRecorder;

	
	@Override
	public void onCreate() {
        super.onCreate();

//		mServiceCamera = CamcorderSplashActivity.mCamera;

        // New
        mSurfaceView = new SurfaceView(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // Old
//		mSurfaceView = CamcorderSplashActivity.mSurfaceView;
//		mSurfaceHolder = CamcorderSplashActivity.mSurfaceHolder;

	}

	@Override
    public IBinder onBind(Intent intent) {
        return new CameraBinder(this);
    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
//		if (mRecordingStatus == false)
//			startRecording();

		return START_STICKY;
	}

    public boolean getRecordingStatus()
    {
        return mRecordingStatus;
    }
	public void startRecording(){
		try {
            // Are we already running
            if (mServiceCamera != null)
                return;

			Toast.makeText(getBaseContext(), "Recording Started", Toast.LENGTH_SHORT).show();
			
			mServiceCamera = Camera.open();
			Camera.Parameters params = mServiceCamera.getParameters();
			mServiceCamera.setParameters(params);
			Camera.Parameters p = mServiceCamera.getParameters();
			
			final List<Size> listSize = p.getSupportedPreviewSizes();
			Size mPreviewSize = listSize.get(0);
			Log.v(TAG, "use: width = " + mPreviewSize.width 
						+ " height = " + mPreviewSize.height);
			p.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			p.setPreviewFormat(PixelFormat.YCbCr_420_SP);
			mServiceCamera.setParameters(p);

			try {
				mServiceCamera.setPreviewDisplay(mSurfaceHolder);
				mServiceCamera.startPreview();
			}
			catch (IOException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}
			
			mServiceCamera.unlock();
			
			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setCamera(mServiceCamera);
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            //mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
			mMediaRecorder.setOutputFile("/mnt/sdcard/ext_sd/video2.mp4");
			mMediaRecorder.setVideoFrameRate(30);
			mMediaRecorder.setVideoSize(1920,1080);//mPreviewSize.width, mPreviewSize.height);
			mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
			
			mMediaRecorder.prepare();
			mMediaRecorder.start(); 

			mRecordingStatus = true;

		} catch (IllegalStateException e) {
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	public void stopRecording() {
		Toast.makeText(getBaseContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();
		try {
			mServiceCamera.reconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMediaRecorder.stop();
		mMediaRecorder.reset();
		
		mServiceCamera.stopPreview();
		mMediaRecorder.release();
		
		mServiceCamera.release();
		mServiceCamera = null;

        mRecordingStatus = false;

        stopSelf();
	}



    public class CameraBinder extends Binder
    {
        private CamcorderService service;
        public CameraBinder(CamcorderService service)
        {
            this.service = service;
        }
        public CamcorderService getRecorder()
        {
            return service;
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
}
