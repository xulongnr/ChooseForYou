package com.adtworker.choose4u;

import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.render.RenderTexture;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleLayoutGameActivity;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseQuadInOut;

import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;

public class Choose4uActivity extends SimpleLayoutGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 640;
	private static final String TAG = "Choose4u";
	private final Random mRandom = new Random(System.currentTimeMillis());
	private int mFromRotation = 0;
	private int mToRotation;

	private Text mText;
	private Font mFont;
	private Camera mCamera;
	private Scene mScene;
	private Scene mRotateScene;
	private boolean bRotating = false;
	private boolean bFirstClick = true;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mPausedTextureRegion;
	private ITextureRegion mNextTextureRegion;
	private CameraScene mPauseScene;

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean mMotionStreaking = true;

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
	protected int getLayoutID() {
		return R.layout.main;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.rendersurfaceview;
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
	}

	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new Engine(pEngineOptions) {
			private static final int RENDERTEXTURE_COUNT = 2;

			private boolean mRenderTextureInitialized;

			private final RenderTexture[] mRenderTextures = new RenderTexture[RENDERTEXTURE_COUNT];
			private final Sprite[] mRenderTextureSprites = new Sprite[RENDERTEXTURE_COUNT];

			private int mCurrentRenderTextureIndex = 0;

			@Override
			public void onDrawFrame(final GLState pGLState)
					throws InterruptedException {
				final boolean firstFrame = !this.mRenderTextureInitialized;

				if (firstFrame) {
					this.initRenderTextures(pGLState);
					this.mRenderTextureInitialized = true;
				}

				final int surfaceWidth = this.mCamera.getSurfaceWidth();
				final int surfaceHeight = this.mCamera.getSurfaceHeight();

				final int currentRenderTextureIndex = this.mCurrentRenderTextureIndex;
				final int otherRenderTextureIndex = (currentRenderTextureIndex + 1)
						% RENDERTEXTURE_COUNT;

				this.mRenderTextures[currentRenderTextureIndex].begin(pGLState,
						false, true);
				{
					/* Draw current frame. */
					super.onDrawFrame(pGLState);

					/* Draw previous frame with reduced alpha. */
					if (!firstFrame) {
						if (Choose4uActivity.this.mMotionStreaking) {
							this.mRenderTextureSprites[otherRenderTextureIndex]
									.setAlpha(0.9f);
							this.mRenderTextureSprites[otherRenderTextureIndex]
									.onDraw(pGLState, this.mCamera);
						}
					}
				}
				this.mRenderTextures[currentRenderTextureIndex].end(pGLState);

				/* Draw combined frame with full alpha. */
				{
					pGLState.pushProjectionGLMatrix();
					pGLState.orthoProjectionGLMatrixf(0, surfaceWidth, 0,
							surfaceHeight, -1, 1);
					{
						this.mRenderTextureSprites[otherRenderTextureIndex]
								.setAlpha(1);
						this.mRenderTextureSprites[otherRenderTextureIndex]
								.onDraw(pGLState, this.mCamera);
					}
					pGLState.popProjectionGLMatrix();
				}

				/* Flip RenderTextures. */
				this.mCurrentRenderTextureIndex = otherRenderTextureIndex;
			}

			private void initRenderTextures(final GLState pGLState) {
				final int surfaceWidth = this.mCamera.getSurfaceWidth();
				final int surfaceHeight = this.mCamera.getSurfaceHeight();

				final VertexBufferObjectManager vertexBufferObjectManager = this
						.getVertexBufferObjectManager();
				for (int i = 0; i <= 1; i++) {
					this.mRenderTextures[i] = new RenderTexture(
							this.getTextureManager(), surfaceWidth,
							surfaceHeight);
					this.mRenderTextures[i].init(pGLState);

					final ITextureRegion renderTextureATextureRegion = TextureRegionFactory
							.extractFromTexture(this.mRenderTextures[i]);
					this.mRenderTextureSprites[i] = new Sprite(0, 0,
							renderTextureATextureRegion,
							vertexBufferObjectManager);
				}
			}
		};
	}

	@Override
	public void onCreateResources() {
		this.mFont = FontFactory.createFromAsset(this.getFontManager(),
				this.getTextureManager(), 512, 512, TextureOptions.BILINEAR,
				this.getAssets(), "Plok.ttf", 32, true, Color.WHITE);
		this.mFont.load();

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 200, 150, TextureOptions.BILINEAR);
		this.mPausedTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBitmapTextureAtlas, this, "paused.png",
						0, 0);
		this.mNextTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBitmapTextureAtlas, this, "next.png", 0,
						50);
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
		this.mText = new Text(-100, -90, this.mFont, "A", 10,
				this.getVertexBufferObjectManager());

		final Entity rectangleGroup = new Entity(CAMERA_WIDTH / 2,
				CAMERA_HEIGHT / 2);

		rectangleGroup.attachChild(this.makeColoredRectangle(-180, -180, 1, 0,
				0));
		rectangleGroup.attachChild(mText);

		rectangleGroup.attachChild(this.makeColoredRectangle(0, -180, 0, 1, 0));
		rectangleGroup.attachChild(this.makeColoredRectangle(0, 0, 0, 0, 1));
		rectangleGroup.attachChild(this.makeColoredRectangle(-180, 0, 1, 1, 0));

		mRotateScene.attachChild(rectangleGroup);

		final Sprite nextSprite = new Sprite(centerX
				+ mNextTextureRegion.getWidth() / 2, CAMERA_HEIGHT
				- mNextTextureRegion.getHeight(), this.mNextTextureRegion,
				this.getVertexBufferObjectManager()) {

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				Log.v(TAG, "onAreaTouched()");

				if (!bRotating) {

					mToRotation = 7200 + mRandom.nextInt(8) * 90 + 45;

					mRotateScene.getChild(0).registerEntityModifier(
							new RotationModifier(10, mFromRotation,
									mToRotation, pEntityModifierListener,
									EaseQuadInOut.getInstance()));

					if (bFirstClick) {
						mRotateScene.getChild(1).registerEntityModifier(
								new RotationModifier(2, 0, -90, EaseQuadInOut
										.getInstance()));
						bFirstClick = false;
					}

					mFromRotation = mToRotation - 7200;
				}

				return true;
			}
		};

		this.mRotateScene.attachChild(nextSprite);
		this.mRotateScene.registerTouchArea(nextSprite);
		this.mRotateScene.setTouchAreaBindingOnActionDownEnabled(true);

		mScene.setChildScene(mRotateScene);

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

	private Rectangle makeColoredRectangle(final float pX, final float pY,
			final float pRed, final float pGreen, final float pBlue) {
		final Rectangle coloredRect = new Rectangle(pX, pY, 180, 180,
				this.getVertexBufferObjectManager());
		coloredRect.setColor(pRed, pGreen, pBlue);

		return coloredRect;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
