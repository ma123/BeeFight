package sk.twostrings.beefight.scene;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ClickDetector;
import org.andengine.input.touch.detector.ClickDetector.IClickDetectorListener;

import sk.twostrings.beefight.GameActivity;
import sk.twostrings.beefight.base.BaseScene;
import sk.twostrings.beefight.manager.ResourcesManager;
import sk.twostrings.beefight.manager.SceneManager;
import sk.twostrings.beefight.manager.SceneManager.SceneType;

public class LevelScene extends BaseScene implements IOnSceneTouchListener, IClickDetectorListener {
		protected static int FONT_SIZE = 15;
        
        protected static int CAMERA_WIDTH = 800;
        protected static int CAMERA_HEIGHT = 480;
 
        protected static int LEVELS = 20;
        protected static int LEVEL_COLUMNS_PER_SCREEN = 5;
        protected static int LEVEL_ROWS_PER_SCREEN = 4;
        protected static int LEVEL_PADDING = 50;
 
        // ===========================================================
        // Fields
        // ===========================================================
        protected PhysicsWorld mPhysicsWorld;
 
        // Scrolling
        private ClickDetector mClickDetector;
        private static int iLevelClicked = -1;
       
        // preferences
        private final int FIRSTLEVEL = 2;
        private static int mMaxLevelReached = 0;
        private Sprite dotLevel;
        
        @Override
		public void createScene() {
        	 createBackground();
          	 this.engine.registerUpdateHandler(new FPSLogger());
             this.mClickDetector = new ClickDetector(this);
             setOnSceneTouchListener(this);
             setTouchAreaBindingOnActionDownEnabled(true);
             setTouchAreaBindingOnActionMoveEnabled(true);            
             setOnSceneTouchListenerBindingOnActionDownEnabled(true);
             setPreferences();
             createLevelBoxes();
		}
 
        private void setPreferences() {
        	mMaxLevelReached = GameActivity.getIntFromSharedPreferences(GameActivity.SHARED_PREFS_LEVEL, FIRSTLEVEL);
		}

		public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
                mClickDetector.onTouchEvent(pSceneTouchEvent);
                return true;
        }
 
        // ===========================================================
        // Methods
        // ===========================================================
        
    	private void createBackground()
    	{
    		Sprite backGroundSprite = new Sprite(400, 240, resourcesManager.game_background_region, vbom);
            this.setBackgroundEnabled(true);
            this.attachChild(backGroundSprite);
    	}
 
        private void createLevelBoxes() {
                // calculate the amount of required columns for the level count
                int totalRows = (LEVELS / LEVEL_COLUMNS_PER_SCREEN) + 1;
 
                // Calculate space between each level square
                int spaceBetweenRows = (CAMERA_HEIGHT / LEVEL_ROWS_PER_SCREEN) - LEVEL_PADDING;
                int spaceBetweenColumns = (CAMERA_WIDTH / LEVEL_COLUMNS_PER_SCREEN) - LEVEL_PADDING;
 
                //Current Level Counter
                int iLevel = 1;
                //Create the Level selectors, one row at a time.
                int boxX = LEVEL_PADDING, boxY = LEVEL_PADDING;
                for (int y = 0; y < totalRows; y++) {
                        for (int x = 0; x < LEVEL_COLUMNS_PER_SCREEN; x++) {
 
                                //On Touch, save the clicked level in case it's a click and not a scroll.
                                final int levelToLoad = iLevel;
                             
                                if(iLevel >= mMaxLevelReached) {
                                	   dotLevel = new Sprite(boxX, boxY, resourcesManager.level_dot_red_region, vbom) {
                                           @Override
                                           public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                                                   if (levelToLoad >= mMaxLevelReached)
                                                           iLevelClicked = -1;
                                                   else
                                                           iLevelClicked = levelToLoad;
                                                   return false;
                                           }
                                   };
                                }
                                else {
                                	dotLevel = new Sprite(boxX, boxY, resourcesManager.level_dot_green_region, vbom) {
                                        @Override
                                        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                                                if (levelToLoad >= mMaxLevelReached)
                                                        iLevelClicked = -1;
                                                else
                                                        iLevelClicked = levelToLoad;
                                                return false;
                                        }
                                };
                                }
                                
                                this.attachChild(dotLevel);
                               
                                this.attachChild(new Text(boxX, boxY - 5, resourcesManager.font, String.valueOf(iLevel), vbom));
                                
                                
                                this.registerTouchArea(dotLevel);
 
                                iLevel++;
                                boxX += spaceBetweenColumns + LEVEL_PADDING;
 
                                if (iLevel > LEVELS)
                                        break;
                        }
 
                        if (iLevel > LEVELS)
                                break;
 
                        boxY += spaceBetweenRows + LEVEL_PADDING;
                        boxX = 50;
                }
        }
 
       private void loadLevel(final int iLevel) {
                if (iLevel != -1) {
                      iLevelClicked = -1;
                      SceneManager.getInstance().loadGameScene(engine, iLevel);
                }            
        }

		public void onClick(ClickDetector pClickDetector, int pPointerID, float pSceneX, float pSceneY) {
			ResourcesManager.playClick(1f, 0.5f);
			loadLevel(iLevelClicked);
		}

		@Override
		public void onBackKeyPressed() {
			SceneManager.getInstance().loadMenuScene(engine);
		}

		@Override
		public SceneType getSceneType() {
			return SceneType.SCENE_LEVEL;
		}

		@Override
		public void disposeScene() {
		}
}