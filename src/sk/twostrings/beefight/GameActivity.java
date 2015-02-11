package sk.twostrings.beefight;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import sk.twostrings.beefight.manager.ResourcesManager;
import sk.twostrings.beefight.manager.SceneManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.KeyEvent;


public class GameActivity extends BaseGameActivity
{
	private BoundCamera camera;
	private static Context context;
	private static int HEIGHT = 480;
	private static int WIDTH = 800;
	private static SharedPreferences prefs;
	public static final String SHARED_PREFS_LEVEL = "level";
	public static final String SHARED_PREFS_SOUND = "sound";
	public static final String SHARED_PREFS_MUSIC = "music";
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) 
	{
		return new LimitedFPSEngine(pEngineOptions, 60);
	}
	
	public EngineOptions onCreateEngineOptions()
	{
		context = this;
		camera = new BoundCamera(0, 0, WIDTH, HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), this.camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.getRenderOptions().getConfigChooserOptions().setRequestedMultiSampling(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{  
	    if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
	    	SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
	    }
	    return false; 
	}

	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException
	{
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException
	{
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
	}

	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException
	{
		mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() 
		{
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                SceneManager.getInstance().createMenuScene();
            }
		}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	
	/* zdielane preferences */ 

	
	public static void writeIntToSharedPreferences(final String pStr, final int pValue) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putInt(pStr, pValue).commit(); 
	}
	
	public static int getIntFromSharedPreferences(final String pStr, final int pFirst) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt(pStr, pFirst);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(this.isGameLoaded())
			ResourcesManager.pauseMusic();
	}

	@Override
	protected synchronized void onResume() {
		super.onResume();
		System.gc();
		if(this.isGameLoaded())
			ResourcesManager.resumeMusic();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		System.exit(0);	
	}

	public static int getHEIGHT() {
		return HEIGHT;
	}

	public static int getWIDTH() {
		return WIDTH;
	}
}