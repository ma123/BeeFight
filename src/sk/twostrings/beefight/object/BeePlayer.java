package sk.twostrings.beefight.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import sk.twostrings.beefight.manager.ResourcesManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class BeePlayer extends AnimatedSprite
{
	private Body body;
	private int LEVEL_HEIGHT;
	private int LEVEL_WIDTH;

	public BeePlayer(float pX, float pY, int height, int width, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld)
	{
		super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
		this.LEVEL_HEIGHT = height;
		this.LEVEL_WIDTH = width;
		createPhysics(camera, physicsWorld);
		camera.setChaseEntity(this);
	}
	
	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
	{		
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));

		body.setUserData("player");
		body.setFixedRotation(true);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
	        public void onUpdate(float pSecondsElapsed)
	        {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.8f);
				if(getX() <= 0) {
					setX(0 + (ResourcesManager.getInstance().player_region.getWidth() / 2));
				}
				
				if(getY() <= 0) {
					setY(0 + (ResourcesManager.getInstance().player_region.getHeight() / 2));
				}
				
				if((getX() + ResourcesManager.getInstance().player_region.getWidth() / 2) >= LEVEL_WIDTH) {
					setX(LEVEL_WIDTH - (ResourcesManager.getInstance().player_region.getWidth() / 2));
				}
				
				if((getY() + ResourcesManager.getInstance().player_region.getHeight() / 2) >= LEVEL_HEIGHT) {
					setY(LEVEL_HEIGHT - (ResourcesManager.getInstance().player_region.getHeight() / 2));
				}
	        }
		});
	}
	
	public void setFly(boolean deadOrLive)
	{	
		final long[] PLAYER_ANIMATE = new long[] { 100, 100, 100, 100};
		if(deadOrLive) {
			animate(PLAYER_ANIMATE, 0, 3, true);
		}
		else {
			animate(PLAYER_ANIMATE, 4, 7, true);
		}
		
	}
		
	public Body getBody() {
		return body;
	}
}