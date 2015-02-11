package sk.twostrings.beefight.manager;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import sk.twostrings.beefight.GameActivity;

import android.graphics.Color;


public class ResourcesManager
{
	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------
	
	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	public Engine engine;
	public static GameActivity activity;
	public BoundCamera camera;
	public VertexBufferObjectManager vbom;
	
	public Font font;	
	public Font gameTitleFont;	
	//---------------------------------------------
	// TEXTURES & TEXTURE REGIONS
	//---------------------------------------------
	
	public ITextureRegion splash_region;
	public ITextureRegion menu_background_region;
	public ITextureRegion music_button_on_region;
	public ITextureRegion music_button_off_region;
	public ITextureRegion sound_button_on_region;
	public ITextureRegion sound_button_off_region;
	public ITextureRegion play_region;
	
	// Game Texture
	public BuildableBitmapTextureAtlas gameTextureAtlas;
	
	// Game Texture Regions
	public ITextureRegion level_dot_green_region;
	public ITextureRegion level_dot_red_region;
	public ITextureRegion game_background_region;
	public ITextureRegion thorn_region;
	public ITextureRegion leaf_region;
	public ITextureRegion flower_region;
	public ITiledTextureRegion player_region;
	public ITextureRegion life_region;
	public ITextureRegion flower_number_region;
	public ITextureRegion clock_region;
	
	public BitmapTextureAtlas mOnScreenControlTexture;
	public ITextureRegion mOnScreenControlBaseTextureRegion;
	public ITextureRegion mOnScreenControlKnobTextureRegion;
	
	private BitmapTextureAtlas splashTextureAtlas;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	public static Sound flowerUpSound = null;
	public static Sound clickSound = null;
	public static Sound painSound = null;
	public static Sound winSound = null;
	public static Sound looseSound = null;
	public static Music musicLoop = null;
	
	// ====================================================
	// VARIABLES
	// ====================================================
	public boolean mSoundsMuted;
	public boolean mMusicMuted;
	
	public void loadSoundAndMusic() {
		  SoundFactory.setAssetBasePath("mfx/");
	        try {
	                flowerUpSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "jump.mp3");
	                clickSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "click.mp3");
	                painSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "pain.mp3");
	                winSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "win.mp3");
	                looseSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "loose.mp3");
	        } catch (final IOException e) {
	                Debug.e("Error", e);
	        }
	        
	        MusicFactory.setAssetBasePath("mfx/");

			try {
				musicLoop = MusicFactory.createMusicFromAsset(this.engine.getMusicManager(), activity, "music.mp3");
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	// ====================================================
	// METHODS
	// ====================================================
	private static void setVolumeForAllSounds(final float pVolume) {
		clickSound.setVolume(pVolume);
		flowerUpSound.setVolume(pVolume);
		painSound.setVolume(pVolume);
		looseSound.setVolume(pVolume);
		winSound.setVolume(pVolume);
	}
	
	public static boolean isSoundMuted() {
		return getInstance().mSoundsMuted;
	}
	
	public static void setSoundMuted(final boolean pMuted) {
		getInstance().mSoundsMuted = pMuted;
		setVolumeForAllSounds((getInstance().mSoundsMuted? 0f:1f));
		GameActivity.writeIntToSharedPreferences(GameActivity.SHARED_PREFS_SOUND, (getInstance().mSoundsMuted? 1:0));
	}
	
	public static boolean toggleSoundMuted() {
		getInstance().mSoundsMuted = !getInstance().mSoundsMuted;
		setVolumeForAllSounds((getInstance().mSoundsMuted? 0f:1f));
		GameActivity.writeIntToSharedPreferences(GameActivity.SHARED_PREFS_SOUND, (getInstance().mSoundsMuted? 1:0));
		return getInstance().mSoundsMuted;
	}
	
	public static boolean isMusicMuted() {
		return getInstance().mMusicMuted;
	}
	
	public static void setMusicMuted(final boolean pMuted) {
		getInstance().mMusicMuted = pMuted;
		if(getInstance().mMusicMuted) musicLoop.pause(); else musicLoop.play();
		GameActivity.writeIntToSharedPreferences(GameActivity.SHARED_PREFS_MUSIC, (getInstance().mMusicMuted? 1:0));
	}
	
	public static boolean toggleMusicMuted() {
		getInstance().mMusicMuted = !getInstance().mMusicMuted;
		if(getInstance().mMusicMuted) musicLoop.pause(); else musicLoop.play();
		GameActivity.writeIntToSharedPreferences(GameActivity.SHARED_PREFS_MUSIC, (getInstance().mMusicMuted? 1:0));
		return getInstance().mMusicMuted;
	}
	
	
	public static void playMusic() {
		if(!isMusicMuted())
			musicLoop.play();
	}
	
	public static void pauseMusic() {
		musicLoop.pause();
	}
	
	public static void resumeMusic() {
		if(!isMusicMuted())
			musicLoop.resume();
	}
	
	public static float getMusicVolume() {
		return musicLoop.getVolume();
	}
	
	public static void setMusicVolume(final float pVolume) {
		musicLoop.setVolume(pVolume);
	}
	
	public static void playClick(final float pRate, final float pVolume) {
		playSound(clickSound,pRate,pVolume);
	}
	
	public static void playLoose(final float pRate, final float pVolume) {
		playSound(looseSound,pRate,pVolume);
	}
	
	public static void playWin(final float pRate, final float pVolume) {
		playSound(winSound,pRate,pVolume);
	}
	
	public static void playFlowerUp(final float pRate, final float pVolume) {
		playSound(flowerUpSound,pRate,pVolume);
	}
	
	public static void playPain(final float pRate, final float pVolume) {
		playSound(painSound,pRate,pVolume);
	}
	
	private static void playSound(final Sound pSound, final float pRate, final float pVolume) {
		if(ResourcesManager.isSoundMuted()) return;
		pSound.setRate(pRate);
		pSound.setVolume(pVolume);
		pSound.play();
	}
	
	
	/*************************************************************/

	public void loadMenuResources()
	{
		loadMenuGraphics();
		loadMenuFonts();
	}
	
	public void loadGameResources()
	{
		loadGameGraphics();
		loadGameFonts();
	}
	
	private void loadMenuGraphics()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
        menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
        menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
        play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play_button.png");
        music_button_on_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "music_button_on.png");
        music_button_off_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "music_button_off.png");
        sound_button_on_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "sound_button_on.png");
        sound_button_off_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "sound_button_off.png");
       
    	try 
    	{
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		} 
    	catch (final TextureAtlasBuilderException e)
    	{
			Debug.e(e);
		}
	}
	
	private void loadMenuFonts()
	{
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "banana.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
		font.load();
		
		final ITexture titleFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameTitleFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), titleFontTexture, activity.getAssets(), "banana.ttf", 110, true, Color.parseColor("#db4336"), 2, Color.WHITE);		
		gameTitleFont.load();
	}
	
	private void loadGameGraphics()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
        gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
        
        game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "menu_background.png");
        level_dot_green_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "dot_green.png");
        level_dot_red_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "dot_red.png");
       	thorn_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "thorn.png");  
       	leaf_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "leaf.png");
        flower_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "flower.png");
        player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "player.png", 8, 1);
        flower_number_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "flower_number.png");
        life_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "heart.png");
        clock_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "clock.png");
    	try {
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameTextureAtlas.load();
		} 
    	catch (final TextureAtlasBuilderException e)
    	{
			Debug.e(e);
		}
    	
    	mOnScreenControlTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mOnScreenControlTexture, activity, "onscreen_control_base.png", 0, 0);
		mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mOnScreenControlTexture, activity, "onscreen_control_knob.png", 128, 0);
		mOnScreenControlTexture.load();
	}
	
	private void loadGameFonts()
	{
		
	}
	
	public void unloadGameTextures()
	{
		// TODO (Since we did not create any textures for game scene yet)
	}
	
	public void loadSplashScreen()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
        splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
        splashTextureAtlas.load();	
	}
	
	public void unloadSplashScreen()
	{
		splashTextureAtlas.unload();
		splash_region = null;
	}
	
	public void unloadMenuTextures()
	{
		menuTextureAtlas.unload();
	}
	
	public void loadMenuTextures()
	{
		menuTextureAtlas.load();
	}
	
	/**
	 * @param engine
	 * @param activity
	 * @param camera
	 * @param vbom
	 * <br><br>
	 * We use this method at beginning of game loading, to prepare Resources Manager properly,
	 * setting all needed parameters, so we can latter access them from different classes (eg. scenes)
	 */
	public static void prepareManager(Engine engine, GameActivity activity, BoundCamera camera, VertexBufferObjectManager vbom)
	{
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}
	
	//---------------------------------------------
	// GETTERS AND SETTERS
	//---------------------------------------------
	
	public static ResourcesManager getInstance()
	{
		return INSTANCE;
	}
}