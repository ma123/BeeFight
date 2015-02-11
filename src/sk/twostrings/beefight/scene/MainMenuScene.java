package sk.twostrings.beefight.scene;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

import sk.twostrings.beefight.GameActivity;
import sk.twostrings.beefight.base.BaseScene;
import sk.twostrings.beefight.manager.ResourcesManager;
import sk.twostrings.beefight.manager.SceneManager;
import sk.twostrings.beefight.manager.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener
{
	private MenuScene menuChildScene;
	private Text gameTitle;
	private IMenuItem playMenuItem;
	private IMenuItem soundMenuItem;
	private IMenuItem musicMenuItem;
	
	private final int MENU_PLAY = 0;
	private final int MENU_SOUND = 1;
	private final int MENU_MUSIC = 2;
		
	@Override
	public void createScene()
	{	
		createBackground();
	    createText();
		createMenuChildScene();
		
		ResourcesManager.playMusic();
		// If the music is muted in the settings, mute it in the game.
		if(GameActivity.getIntFromSharedPreferences(GameActivity.SHARED_PREFS_MUSIC, 0)>0)
			ResourcesManager.setMusicMuted(true);
		// If the sound effects are muted in the settings, mute them in the game.
		if(GameActivity.getIntFromSharedPreferences(GameActivity.SHARED_PREFS_SOUND, 0)>0)
			ResourcesManager.setSoundMuted(true);
	}

	@Override
	public void onBackKeyPressed()
	{
		System.exit(0);
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_MENU;
	}
	

	@Override
	public void disposeScene()
	{
		// TODO Auto-generated method stub
	}
	
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
		switch(pMenuItem.getID())
		{
			case MENU_PLAY:
				ResourcesManager.playClick(1f, 0.5f);
				SceneManager.getInstance().loadLevelScene(engine);			
				return true;
			case MENU_SOUND:
				ResourcesManager.toggleSoundMuted();	
				if(GameActivity.getIntFromSharedPreferences(GameActivity.SHARED_PREFS_SOUND, 0)>0) {
					soundMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SOUND, resourcesManager.sound_button_off_region, vbom), 1.2f, 1);
				}
				else {
					soundMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SOUND, resourcesManager.sound_button_on_region, vbom), 1.2f, 1);
				}
				menuChildScene.addMenuItem(soundMenuItem);
				soundMenuItem.setPosition(60, 420);
				
				return true;
			case MENU_MUSIC:
				ResourcesManager.toggleMusicMuted();
				if(GameActivity.getIntFromSharedPreferences(GameActivity.SHARED_PREFS_MUSIC, 0)>0) {
					musicMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MUSIC, resourcesManager.music_button_off_region, vbom), 1.2f, 1);
				}
				else {
					musicMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MUSIC, resourcesManager.music_button_on_region, vbom), 1.2f, 1);
				}
				menuChildScene.addMenuItem(musicMenuItem);
				musicMenuItem.setPosition(740, 420);
				return true;
			default:
				return false;
		}
	}
	
    private void createText() {
    	gameTitle = new Text(0, 0, resourcesManager.gameTitleFont, "Bee fight", vbom);
		gameTitle.setPosition(camera.getCenterX(), camera.getHeight() - 70);
		attachChild(gameTitle);
    }
	
	private void createBackground()
	{
		 Sprite backGroundSprite = new Sprite(400, 240, resourcesManager.menu_background_region, vbom);
         this.setBackgroundEnabled(true);
         this.attachChild(backGroundSprite);
	}
	
	
	private void createMenuChildScene()
	{
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);
		
		playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1.2f, 1);
		if(GameActivity.getIntFromSharedPreferences(GameActivity.SHARED_PREFS_SOUND, 0)>0) {
			soundMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SOUND, resourcesManager.sound_button_off_region, vbom), 1.2f, 1);
		}
		else {
			soundMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SOUND, resourcesManager.sound_button_on_region, vbom), 1.2f, 1);
		}
		
		if(GameActivity.getIntFromSharedPreferences(GameActivity.SHARED_PREFS_MUSIC, 0)>0) {
			musicMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MUSIC, resourcesManager.music_button_off_region, vbom), 1.2f, 1);
		}
		else {
			musicMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MUSIC, resourcesManager.music_button_on_region, vbom), 1.2f, 1);
		}
		
		
		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(soundMenuItem);
		menuChildScene.addMenuItem(musicMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		playMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() - 60);
		soundMenuItem.setPosition(60, 420);
		musicMenuItem.setPosition(740, 420);
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
	}
}