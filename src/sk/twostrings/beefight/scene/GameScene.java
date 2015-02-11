package sk.twostrings.beefight.scene;

import java.io.IOException;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import sk.twostrings.beefight.GameActivity;
import sk.twostrings.beefight.base.BaseScene;
import sk.twostrings.beefight.manager.ResourcesManager;
import sk.twostrings.beefight.manager.SceneManager;
import sk.twostrings.beefight.manager.SceneManager.SceneType;
import sk.twostrings.beefight.object.BeePlayer;

import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameScene extends BaseScene {
	private int flowers;
	private static int LIFE = 0;
	private static int TIME = 0;
	private static int actualTime = 0;
	private boolean mPaused = true;

	private HUD gameHUD;
	private Text timeText;
	private Sprite timeSprite;
	private Text lifeText;
	private Sprite lifeSprite;
	private Text flowerText;
	private Sprite flowerSprite;
	private PhysicsWorld physicsWorld;

	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_THORN = "thorn";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEAF = "leaf";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_FLOWER = "flower";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE_LIFE = "life";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE_TIME = "time";

	private int LEVEL_HEIGHT;
	private int LEVEL_WIDTH;
    private boolean removeLife = true;
    private boolean boolGameOver = true;
	
	// private Player player;
	private BeePlayer beePlayer;
	private Body playerBody;
	private Thread gameTimer;

	private Text gameOverText;
	private Text winText;
	

	public GameScene(int iLevel) {
		super(iLevel);
	}

	@Override
	public void createScene() {
		createBackground();
		createPhysics();
		loadLevel(iLevel);
		createHUD();
		createGameOverAndWinText();
		playerBody = beePlayer.getBody();
		createAnalogOnScreenControl();
		startGameTime();
	}

	// spustenie vlakna v ktorom sa odpocitava cas zostavajuci do konca hry
	private void startGameTime() {
		gameTimer = new Thread(new Runnable() {
			public void run() {
				try {
					actualTime = TIME - 1;
					while (actualTime >= 0) {
						Thread.sleep(1000);
						timeText.setText(" : " + actualTime);
						System.out.println(actualTime);
						actualTime--;
					}
					if (actualTime <= 0) {
						ResourcesManager.playLoose(1f, 0.5f);// jump zvuk po zobrani kvetinky
						displayGameOverText();
					}
				} catch (Throwable t) {
					// nieco
				}
			}
		});
		gameTimer.start();
	}

	private void createAnalogOnScreenControl() { // opacne prima uhly a opacne ich zobrazuje
		final float x1 = 130;
		final float y1 = 120;
		final AnalogOnScreenControl velocityOnScreenControl = new AnalogOnScreenControl(x1, y1, camera,
				ResourcesManager.getInstance().mOnScreenControlBaseTextureRegion,
				ResourcesManager.getInstance().mOnScreenControlKnobTextureRegion,
				0.1f, vbom, new IAnalogOnScreenControlListener() {
					public void onControlChange(BaseOnScreenControl pBaseOnScreenControl, float pValueX, float pValueY) {
						playerBody.setLinearVelocity(pValueX * 6, pValueY * 6);
						double angle = getAngle(pValueX, pValueY);
						 if(((angle <= 180) && (angle > 0)) || ((angle >= 250) && (angle <= 290)) || (angle == 360)) { // stred, hore , dolu
							 beePlayer.setRotation(0);
						 } else if((angle > 180) && (angle < 220)){ 
							 beePlayer.setRotation(300);
						 } else if((angle > 320) && (angle < 360)){ 
							 beePlayer.setRotation(60);
						 } else if((angle >= 220) && (angle < 250)){ 
							 beePlayer.setRotation(320);
						 } else if((angle > 290) && (angle <= 320)){ 
							 beePlayer.setRotation(40);
						 }
					}

					public void onControlClick(
							AnalogOnScreenControl pAnalogOnScreenControl) {
					}
				});
		velocityOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		velocityOnScreenControl.getControlBase().setAlpha(0.5f);

		this.setChildScene(velocityOnScreenControl);
	}
	
	private double getAngle(float x, float y) {
        double inRads = Math.atan2(y,x);
        if (inRads < 0) {
            inRads = Math.abs(inRads);
        } else {
            inRads = 2*Math.PI - inRads;
        }
        return Math.toDegrees(inRads);
    }

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	// citanie levelu z xml parameter je cislo levelu
	private void loadLevel(final int levelID) {
		final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
		final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL) {
					public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException {
						LEVEL_WIDTH = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
						LEVEL_HEIGHT = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
						LIFE = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE_LIFE);
						TIME = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE_TIME);
						camera.setBounds(0, 0, LEVEL_WIDTH, LEVEL_HEIGHT);
						camera.setBoundsEnabled(true);
						return GameScene.this;
					}
				});

		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY) {
					public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException {
						final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
						final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
						final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);

						final Sprite levelObject;

						if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_THORN)) {
								levelObject = new Sprite(x, y, resourcesManager.thorn_region, vbom);
								PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF).setUserData("thorn");			
						} else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEAF)) {
							levelObject = new Sprite(x, y, resourcesManager.leaf_region, vbom);
							final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
							body.setUserData("leaf");
							physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
						} else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_FLOWER)) {
							levelObject = new Sprite(x, y,resourcesManager.flower_region, vbom) {
								@Override
								protected void onManagedUpdate(float pSecondsElapsed) {
									super.onManagedUpdate(pSecondsElapsed);
									if (beePlayer.collidesWith(this)) {
										ResourcesManager.playFlowerUp(1f, 0.5f); // jump zvuk po zobrani kvetinky
										removeFlowers();
										this.setVisible(false);
										this.setIgnoreUpdate(true);
										if (flowers <= 0) {
											int hightLevelScore = GameActivity.getIntFromSharedPreferences(GameActivity.SHARED_PREFS_LEVEL, 0);
											if (hightLevelScore < (levelID + 2)) {
												GameActivity.writeIntToSharedPreferences(GameActivity.SHARED_PREFS_LEVEL, levelID + 2);
											}
											ResourcesManager.playWin(1f, 0.5f); // vyherny zvuk
											displayWinText();
										}
									}
								}
							};
							addToFlowers();
							levelObject.registerEntityModifier(new LoopEntityModifier( new ScaleModifier(1, 1, 1.3f)));
						} else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER)) {
							beePlayer = new BeePlayer(x, y, LEVEL_HEIGHT, LEVEL_WIDTH, vbom, camera, physicsWorld);
							beePlayer.setFly(true);
							levelObject = beePlayer;
						} else {
							throw new IllegalArgumentException();
						}

						levelObject.setCullingEnabled(true);

						return levelObject;
					}
				});

		levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID + ".lvl");
	}

	// inicializacia gameover a win textu
	private void createGameOverAndWinText() {
		gameOverText = new Text(0, 0, resourcesManager.font, "Game Over!", vbom);
		winText = new Text(0, 0, resourcesManager.font, "You Win!", vbom);
	}

	// zobrazi game over text
	private void displayGameOverText() {
		camera.setChaseEntity(null);
		gameOverText.setPosition(camera.getCenterX(), camera.getCenterY());
		attachChild(gameOverText);
		mPaused = false;
		waitOneSecond();
	}

	// zobrazi win text
	private void displayWinText() {
		camera.setChaseEntity(null);
		winText.setPosition(camera.getCenterX(), camera.getCenterY());
		attachChild(winText);
		mPaused = false;
		waitOneSecond();
	}

	/* nastavenie casovaca na 1 sekundu */
	private void waitOneSecond() {
		engine.registerUpdateHandler(new TimerHandler(1.0f,
				new ITimerCallback() {
					public void onTimePassed(final TimerHandler pTimerHandler) {
						pTimerHandler.reset();
						engine.unregisterUpdateHandler(pTimerHandler);
						gameTimer.interrupt();
						gameTimer = null;
						disposeScene();
						SceneManager.getInstance().loadLevelScene(engine);
					}
				}));
	}

	// vytvori HUD panel zivot zostavajuci cas a pocet zostavajucich kvetin
	private void createHUD() {
		gameHUD = new HUD();

		lifeSprite = new Sprite(50, 420, resourcesManager.life_region, vbom);
		lifeSprite.setAnchorCenter(0, 0);
		gameHUD.attachChild(lifeSprite);

		lifeText = new Text(100, 420, resourcesManager.font, "0123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
		lifeText.setAnchorCenter(0, 0);
		lifeText.setText(" : " + LIFE);
		gameHUD.attachChild(lifeText);

		timeSprite = new Sprite(300, 420, resourcesManager.clock_region, vbom);
		timeSprite.setAnchorCenter(0, 0);
		gameHUD.attachChild(timeSprite);

		timeText = new Text(350, 420, resourcesManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		timeText.setAnchorCenter(0, 0);
		timeText.setText(" : " + TIME);
		gameHUD.attachChild(timeText);

		flowerSprite = new Sprite(600, 420, resourcesManager.flower_number_region, vbom);
		flowerSprite.setAnchorCenter(0, 0);
		gameHUD.attachChild(flowerSprite);

		flowerText = new Text(660, 420, resourcesManager.font, " : 0123456789", new TextOptions(HorizontalAlign.RIGHT), vbom);
		flowerText.setAnchorCenter(0, 0);
		flowerText.setText(" : " + flowers);
		gameHUD.attachChild(flowerText);

		camera.setHUD(gameHUD);
	}

	private void createBackground() {
		ParallaxBackground background = new ParallaxBackground(0, 0, 0);
		background.attachParallaxEntity(new ParallaxEntity(0, new Sprite(400, 240, resourcesManager.game_background_region, vbom)));
		setBackground(background);
		setBackgroundEnabled(true);
	}

	// zistuje pocet kvetov v levele v xml
	private void addToFlowers() {
		flowers++;
	}

	// odoberie jeden kvet a zobrazi
	private void removeFlowers() {
		flowers--;
		flowerText.setText(" : " + flowers);
	}

	// odoberie jeden zivot a zobrazi
	private void removeToLife() {
		LIFE--;
		if (LIFE >= 0) {
			lifeText.setText(" : " + LIFE);
		} else {
			lifeText.setText(" : " + 0);
		}
	}

	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -40), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}

	private ContactListener contactListener() {
		ContactListener contactListener = new ContactListener() { 
			 public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null) {
					if (x1.getBody().getUserData().equals("thorn") && x2.getBody().getUserData().equals("player")) {
						beePlayer.setFly(false);
						if(removeLife) {
							removeToLife(); // remove life
							ResourcesManager.playPain(1f, 0.5f); // zvuk pri odobrani zivota
							removeLife = false;	
						}
						else {				
							engine.registerUpdateHandler(new TimerHandler(1.0f, new ITimerCallback()
						    {                                    
						        public void onTimePassed(final TimerHandler pTimerHandler)
						        {
						            //pTimerHandler.reset();
						            //engine.unregisterUpdateHandler(pTimerHandler);
						            removeLife = true;
						        }
						    }));
							
						}
						
						
						if (LIFE <= 0) {
							if(boolGameOver) {
								ResourcesManager.playLoose(1f, 0.5f);// jump zvuk po zobrani kvetinky
								displayGameOverText();
								boolGameOver = false;
							}							
						}  
					}
			   }
			   
			}

			public void endContact(Contact contact) {
				beePlayer.setFly(true);
			}
			public void preSolve(Contact contact, Manifold oldManifold) {
			}
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
		};
		return contactListener;
	}

	@Override
	public void onBackKeyPressed() {
		gameTimer.interrupt();
		gameTimer = null;
		disposeScene();
		SceneManager.getInstance().loadLevelScene(engine);
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setChaseEntity(null);
		camera.setCenter(0, 0);
	}

	@Override
	public void onManagedUpdate(float pSecondsElapsed) {
		if (mPaused) {
			super.onManagedUpdate(pSecondsElapsed);
		} else {
			super.onManagedUpdate(0);
		}
	}
}