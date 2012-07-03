package com.adtworker.choose4u;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.FileBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseQuadInOut;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;

public class Choose4uActivity extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 1280;
	private static final String TAG = "Choose4u";
	private final Random mRandom = new Random(System.currentTimeMillis());
	private int mFromRotation = 0;
	private int mToRotation;

	private Font mFont;
	private Camera mCamera;
	private Scene mScene;
	private Scene mRotateScene;
	private boolean bRotating = false;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mPausedTextureRegion;
	private ITextureRegion mButtonTextureRegion;
	private ITextureRegion[] mRectagleTextureRegion = new ITextureRegion[4];
	private CameraScene mPauseScene;
	private int mRectWidth;

	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	private static final int CAMERA_WITH_DATA = 3023;

	final String img_prefix = "/mnt/sdcard/.adtwkr/";

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_SENSOR,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
	}
	@Override
	public void onCreateResources() {
		this.mFont = FontFactory.createFromAsset(this.getFontManager(),
				this.getTextureManager(), 512, 512, TextureOptions.BILINEAR,
				this.getAssets(), "Plok.ttf", 32, true, Color.WHITE);
		this.mFont.load();

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 200, 870, TextureOptions.BILINEAR);
		this.mPausedTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBitmapTextureAtlas, this, "paused.png",
						0, 0);
		this.mButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBitmapTextureAtlas, this, "next.png", 0,
						50);

		File file = new File(img_prefix + "red.png");
		if (file.exists()) {
			FileBitmapTextureAtlasSource fileBitmapTextureAtlasSource = FileBitmapTextureAtlasSource
					.create(file);
			mRectagleTextureRegion[0] = BitmapTextureAtlasTextureRegionFactory
					.createFromSource(mBitmapTextureAtlas,
							fileBitmapTextureAtlasSource, 0, 150);
		} else {
			mRectagleTextureRegion[0] = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(this.mBitmapTextureAtlas, this, "red.png",
							0, 150);
		}

		file = new File(img_prefix + "green.png");
		if (file.exists()) {
			FileBitmapTextureAtlasSource fileBitmapTextureAtlasSource = FileBitmapTextureAtlasSource
					.create(file);
			mRectagleTextureRegion[1] = BitmapTextureAtlasTextureRegionFactory
					.createFromSource(mBitmapTextureAtlas,
							fileBitmapTextureAtlasSource, 0, 330);
		} else {
			mRectagleTextureRegion[1] = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(this.mBitmapTextureAtlas, this,
							"green.png", 0, 330);
		}

		file = new File(img_prefix + "blue.png");
		if (file.exists()) {
			FileBitmapTextureAtlasSource fileBitmapTextureAtlasSource = FileBitmapTextureAtlasSource
					.create(file);
			mRectagleTextureRegion[2] = BitmapTextureAtlasTextureRegionFactory
					.createFromSource(mBitmapTextureAtlas,
							fileBitmapTextureAtlasSource, 0, 510);
		} else {
			mRectagleTextureRegion[2] = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(this.mBitmapTextureAtlas, this,
							"blue.png", 0, 510);
		}

		file = new File(img_prefix + "yellow.png");
		if (file.exists()) {
			FileBitmapTextureAtlasSource fileBitmapTextureAtlasSource = FileBitmapTextureAtlasSource
					.create(file);
			mRectagleTextureRegion[3] = BitmapTextureAtlasTextureRegionFactory
					.createFromSource(mBitmapTextureAtlas,
							fileBitmapTextureAtlasSource, 0, 690);
		} else {
			mRectagleTextureRegion[3] = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(this.mBitmapTextureAtlas, this,
							"yellow.png", 0, 690);
		}
		this.mBitmapTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mPauseScene = new CameraScene(this.mCamera);
		/* Make the 'PAUSED'-label centered on the camera. */
		final float centerX = (CAMERA_WIDTH - this.mPausedTextureRegion
				.getWidth()) / 2;
		final float centerY = (CAMERA_HEIGHT - this.mPausedTextureRegion
				.getHeight()) / 2;
		final Sprite pausedSprite = new Sprite(centerX, centerY,
				this.mPausedTextureRegion, this.getVertexBufferObjectManager());
		this.mPauseScene.attachChild(pausedSprite);
		/* Makes the paused Game look through. */
		this.mPauseScene.setBackgroundEnabled(false);

		/* Create a nice scene with some rectangles. */
		this.mScene = new Scene();
		this.mRotateScene = new Scene();

		final Entity rectangleGroup = new Entity(CAMERA_WIDTH / 2,
				CAMERA_HEIGHT / 2);

		mRectWidth = (int) (Math.min(CAMERA_WIDTH, CAMERA_HEIGHT) / 2 / Math
				.sqrt(2));

		addChoice(rectangleGroup, 0, -mRectWidth, -mRectWidth);
		addChoice(rectangleGroup, 1, 0, -mRectWidth);

		addChoice(rectangleGroup, 2, 0, 0);
		addChoice(rectangleGroup, 3, -mRectWidth, 0);

		mRotateScene.attachChild(rectangleGroup);

		final Sprite buttonSprite = new Sprite(centerX,
				(float) (centerY + mRectWidth * Math.sqrt(2)),
				mButtonTextureRegion.getWidth() * 2,
				mButtonTextureRegion.getHeight() * 2, mButtonTextureRegion,
				this.getVertexBufferObjectManager()) {

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				if (!bRotating) {

					mToRotation = 7200 + mRandom.nextInt(8) * 90 + 45;

					mRotateScene.getChild(0).registerEntityModifier(
							new RotationModifier(10, mFromRotation,
									mToRotation, pEntityModifierListener,
									EaseQuadInOut.getInstance()));

					mFromRotation = mToRotation - 7200;
				}

				return true;
			}
		};

		this.mRotateScene.attachChild(buttonSprite);
		this.mRotateScene.registerTouchArea(buttonSprite);
		this.mRotateScene.setTouchAreaBindingOnActionDownEnabled(true);

		mScene.setChildScene(mRotateScene);
		// mScene.setTouchAreaBindingOnActionDownEnabled(true);

		return mScene;
	}
	private IEntityModifierListener pEntityModifierListener = new IEntityModifierListener() {
		@Override
		public void onModifierStarted(IModifier<IEntity> pModifier,
				IEntity pItem) {
			bRotating = true;
		}

		@Override
		public void onModifierFinished(IModifier<IEntity> pModifier,
				IEntity pItem) {
			bRotating = false;
		}
	};

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if (pKeyCode == KeyEvent.KEYCODE_MENU
				&& pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if (this.mEngine.isRunning()) {
				this.mScene.getChildScene().setChildScene(this.mPauseScene,
						false, true, true);
				this.mEngine.stop();
			} else {
				this.mScene.getChildScene().clearChildScene();
				this.mEngine.start();
			}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void addChoice(Entity parent, int i, float x, float y) {
		final Sprite sprite = new Sprite(x, y, mRectWidth, mRectWidth,
				this.mRectagleTextureRegion[i],
				this.getVertexBufferObjectManager()) {

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				Log.d(TAG, "onTouched: x=" + pTouchAreaLocalX + ", y="
						+ pTouchAreaLocalY);

				switch (pSceneTouchEvent.getAction()) {
					case TouchEvent.ACTION_DOWN :
						// Intent intent = new Intent(
						// MediaStore.ACTION_IMAGE_CAPTURE);
						// startActivityForResult(intent, CAMERA_WITH_DATA);
						break;

					default :

						break;
				}
				return true;
			}
		};
		parent.attachChild(sprite);
		mRotateScene.registerTouchArea(sprite);

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
			case CAMERA_WITH_DATA :
				final Bitmap photo = data.getParcelableExtra("data");
				if (photo != null) {
					doCropPhoto(photo);
				}
				break;

			case PHOTO_PICKED_WITH_DATA :
				Bitmap photo1 = data.getParcelableExtra("data");
				if (photo1 != null) {
					String fileName = "/mnt/sdcard/.adtwkr/red.png";
					File file = new File(fileName);
					try {
						if (!file.exists())
							file.createNewFile();

						FileOutputStream os = new FileOutputStream(file, false);
						photo1.compress(Bitmap.CompressFormat.PNG, 100, os);
						os.flush();
						os.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

					FileBitmapTextureAtlasSource fileBitmapTextureAtlasSource = FileBitmapTextureAtlasSource
							.create(file);
					mRectagleTextureRegion[0] = BitmapTextureAtlasTextureRegionFactory
							.createFromSource(mBitmapTextureAtlas,
									fileBitmapTextureAtlasSource, 0, 150);
					mBitmapTextureAtlas.load();
				}
				break;

			default :

		}
	}
	protected void doCropPhoto(Bitmap data) {
		Intent intent = getCropImageIntent(data);
		startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
	}

	public static Intent getCropImageIntent(Bitmap data) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		intent.putExtra("data", data);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 180);
		intent.putExtra("outputY", 180);
		intent.putExtra("return-data", true);
		return intent;
	}
}
