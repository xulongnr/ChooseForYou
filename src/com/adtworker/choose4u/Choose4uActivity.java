package com.adtworker.choose4u;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Choose4uActivity extends Activity
		implements
			SurfaceHolder.Callback {
	private static final String TAG = "Choose4uActivity";
	Camera mCamera;
	byte[] tempdata;
	boolean mPreviewRunning = false;
	private SurfaceHolder mSurfaceHolder;
	private SurfaceView mSurfaceView;
	private TextView mLogView;
	private ImageView mImgView;
	private ViewGroup mImgsGrp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mLogView = (TextView) findViewById(R.id.logLayout);
		mImgView = (ImageView) findViewById(R.id.imgView);
		mImgsGrp = (ViewGroup) findViewById(R.id.imgsLayout);
		mSurfaceView = (SurfaceView) findViewById(R.id.surface);

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mSurfaceView.setOnClickListener(mSurfaceClick);
	}

	OnClickListener mSurfaceClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mCamera.takePicture(mShutterCallback, mPictureCallback, mjpeg);
		}

	};

	ShutterCallback mShutterCallback = new ShutterCallback() {

		@Override
		public void onShutter() {
		}

	};

	PictureCallback mPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera c) {
		}
	};

	PictureCallback mjpeg = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera c) {
			if (data != null) {
				tempdata = data;
				done();
			}
		}
	};

	void done() {
		Bitmap bm = BitmapFactory.decodeByteArray(tempdata, 0, tempdata.length);
		mImgView.setImageBitmap(bm);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		Log.d(TAG, "surfaceChanged()");

		try {
			if (mPreviewRunning) {
				mCamera.stopPreview();
				mPreviewRunning = false;
			}

			Camera.Parameters p = mCamera.getParameters();
			p.setPreviewSize(w, h);

			int rotation = getWindowManager().getDefaultDisplay().getRotation();
			int degrees = 0;
			switch (rotation) {
				case Surface.ROTATION_0 :
					degrees = 0;
					break;
				case Surface.ROTATION_90 :
					degrees = 90;
					break;
				case Surface.ROTATION_180 :
					degrees = 180;
					break;
				case Surface.ROTATION_270 :
					degrees = 270;
					break;
			}
			mCamera.setDisplayOrientation(degrees + 90);

			mCamera.setParameters(p);
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
			mPreviewRunning = true;
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
		mCamera = null;
	}

	private void addLog(String str) {
		if (mLogView.getText().length() != 0) {
			str = mLogView.getText() + "\n" + str;
		}
		mLogView.setText(str);
	}

}