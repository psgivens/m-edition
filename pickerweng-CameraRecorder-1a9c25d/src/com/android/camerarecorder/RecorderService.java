package com.android.camerarecorder;

import java.io.IOException;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class RecorderService extends Service {
	private static final String TAG = "RecorderService";
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private static Camera mServiceCamera;
	private static boolean mRecordingStatus;
	private MediaRecorder mMediaRecorder;

	
	@Override
	public void onCreate() {
		mServiceCamera = CameraRecorder.mCamera;
		mSurfaceView = CameraRecorder.mSurfaceView;
		mSurfaceHolder = CameraRecorder.mSurfaceHolder;
		
		super.onCreate();
	}

	@Override
    public IBinder onBind(Intent intent) {
        return new CameraBinder(this);
    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		if (mRecordingStatus == false)
			startRecording();

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		stopRecording();
		mRecordingStatus = false;
		
		super.onDestroy();
	}   

    public boolean getRecordingStatus()
    {
        return mRecordingStatus;
    }
	public boolean startRecording(){
		try {
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
			
			return true;
		} catch (IllegalStateException e) {
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
			return false;
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
	}



    public class CameraBinder extends Binder
    {
        private RecorderService service;
        public CameraBinder(RecorderService service)
        {
            this.service = service;
        }
        public RecorderService getRecorder()
        {
            return service;
        }
    }
}
