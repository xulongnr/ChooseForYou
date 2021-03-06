package com.adtworker.choose4u;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;

public class SplashScreen extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = new SplashView(SplashScreen.this);
		setContentView(view);

		// set time to splash out
		final int nWelcomeScreenDisplay = 2000;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent mainIntent = new Intent(SplashScreen.this,
						SetupChoicesActivity.class);
				startActivity(mainIntent);
				SplashScreen.this.finish();
			}
		}, nWelcomeScreenDisplay);

		FileUtils.clearAdCache();
	}

	@Override
	public boolean onKeyUp(int keycode, KeyEvent event) {
		switch (keycode) {

			case KeyEvent.KEYCODE_BACK :
				return true;
		}
		return super.onKeyUp(keycode, event);
	}

	class SplashView extends View {
		SplashView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Paint paint = new Paint();

			// Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
			// R.drawable.ic_launcher_alarmclock);
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream(getAssets().open(
						"target_3d.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
			int x = displayMetrics.widthPixels / 2 - bitmap.getWidth() / 2;
			int y = displayMetrics.heightPixels / 2 - bitmap.getHeight() / 2;

			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(bitmap, x, y, paint);

			paint.setTextSize(25);
			canvas.drawText("by adtworker", x + bitmap.getWidth() / 2, y
					+ bitmap.getHeight(), paint);
		}
	}
}
