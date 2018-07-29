package scene.entities.players.trubble;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import box.TaskManager;
import objStuff.OBJParser;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.models.RawModel;
import renderEngine.models.TexturedModel;
import renderEngine.textures.GUITexture;
import renderEngine.textures.ModelTexture;
import scene.entities.Entity;
import scene.entities.hostiles.Enemy;
import scene.entities.players.Player;
import utils.RaysCast;
import utils.SFMath;

public class PlayerTrubble extends Player {
	
	static RawModel dm = OBJParser.loadObjModel("TRUBBLEDeck");
	static TexturedModel deck_model = new TexturedModel(dm, new ModelTexture(Loader.loadTexture("uss")));
	
	static RawModel tm = OBJParser.loadObjModel("TRUBBLEStarDrive");
	static TexturedModel main_model = new TexturedModel(tm, new ModelTexture(Loader.loadTexture("uss")));
	
	static RawModel sm = OBJParser.loadObjModel("TRUBBLESternDrive");
	static TexturedModel stern_model = new TexturedModel(sm, new ModelTexture(Loader.loadTexture("uss")));
	
	private PlayerTrubbleDeck deck = new PlayerTrubbleDeck(SFMath.vecadd(getPosition(), 50, 0, 0), this);
	private PlayerTrubbleStern stern = new PlayerTrubbleStern(SFMath.vecadd(getPosition(), -50, 0, 0), this);
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	
	private static final float FULL_IMPULSE_SPEED = 3000;
	private static final float BOOST_SPEED = 5000;
	
	private float IMPULSE_MOVE_SPEED_VAR = 2000;
	private static final float TURN_SPEED = 180;
	private static final float VERT_POWER = 60;
	
	public boolean isSeperated = false;

	public PlayerTrubble(Vector3f position, float rotX, float rotY, float rotZ, float scale,
			List<GUITexture> guis) {
		super(main_model, position, rotX, rotY, rotZ, scale, guis);
	}
	
	public void add(List<Entity> entities) {
		entities.add(deck);
		entities.add(this);
		entities.add(stern);
	}

	@Override
	public void update(RaysCast caster) {
		//deck.customRotationAxis = !isSeperated;
		//stern.customRotationAxis = !isSeperated;
		move();	
	}
	
	private void move() {
		checkInputs();
		super.rotate(0, currentTurnSpeed * DisplayManager.getFrameTime(), 0);
		//deck.rotate(0, currentTurnSpeed * DisplayManager.getFrameTime(), 0);
		//stern.rotate(0, currentTurnSpeed * DisplayManager.getFrameTime(), 0);
		float distanceMoved = currentSpeed * DisplayManager.getFrameTime();
		
		float dx = (float) (distanceMoved * Math.sin(Math.toRadians(super.getRotY())));
		float dy = (float) (distanceMoved * Math.sin(Math.toRadians(super.getRotX())));
		float dz = (float) (distanceMoved * Math.cos(Math.toRadians(super.getRotY())));
		super.move(dx, -dy, dz);
		
		float deckfront = 93;
		
		deck.setPosition((isSeperated ? new Vector3f(
				
				super.getPosition().x + SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, 90, 200), 
				super.getPosition().y - 22.5f,
				super.getPosition().z
				
				) 
				
				:
					
				new Vector3f(
						
						super.getPosition().x, 
						super.getPosition().y - 22.5f,
						super.getPosition().z + deckfront
						
						)));
		
		stern.setPosition((isSeperated ? new Vector3f(
				
				super.getPosition().x - SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, 90, 200), 
				super.getPosition().y,
				super.getPosition().z
				
				) 
				
				:
					
				new Vector3f(
						
						super.getPosition().x, 
						super.getPosition().y - 12.5f,
						super.getPosition().z
						
						)));
		
		deck.setRotX(super.getRotX());
		stern.setRotX(super.getRotX());
		
		deck.setRotY(super.getRotY());
		stern.setRotY(super.getRotY());
		
		deck.setRotZ(super.getRotZ());
		stern.setRotZ(super.getRotZ());
		
		deck.customOrigin = new Vector3f(super.getPosition());
		stern.customOrigin = new Vector3f(super.getPosition());
		
		//this.mx += dx; this.mx /= 2; this.my += dy; this.my /= 2; this.mz += dz; this.mz /= 2;
		//super.move(0, upwardsSpeed * DisplayManager.getFrameTime(), 0);
		this.setRotY(this.getRotY() % 360);
		if (this.getRotY() < 0) {
			this.setRotY(Math.abs(360 - this.getRotY()));
		}
	}
	
	private void rotateAll(float x, float y, float z) {
		super.rotate(x, y, z);
		deck.rotate(x, y, z);
		stern.rotate(x, y, z);
	}
	
	private void setRotations(float x, float y, float z) {
		super.setRotX(x);
		deck.setRotX(x);
		stern.setRotX(x);
		
		super.setRotY(y);
		deck.setRotY(y);
		stern.setRotY(y);
		
		super.setRotZ(z);
		deck.setRotZ(z);
		stern.setRotZ(z);
		
	}
	
	private void checkInputs() {
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W) && !Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			this.currentSpeed = IMPULSE_MOVE_SPEED_VAR;
			// TaskManager.warpParticleSystem.generateParticles(new Vector3f(super.getPosition()));
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_S))
			this.currentSpeed = -IMPULSE_MOVE_SPEED_VAR;
		
		else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_W)) {
			//this.currentSpeed = IMPULSE_MOVE_SPEED_VAR * 30 * 9.975f * 42;
			TaskManager.warpParticleSystem.generateParticles(new Vector3f(super.getPosition()));
			this.currentSpeed = BOOST_SPEED;
		}
		else 
			this.currentSpeed = 0;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) { 
			this.currentTurnSpeed = TURN_SPEED;
			if (this.getRotZ() > -45)
				super.rotate(0, 0, -0.6f);
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_D)) { 
			this.currentTurnSpeed = -TURN_SPEED;
			if (this.getRotZ() < 45)
				super.rotate(0, 0, 0.6f);
		}
		else {											
			this.currentTurnSpeed = 0; 		
			if (this.getRotZ() < 0) {
				super.rotate(0, 0, 0.7f);
				
				if (this.getRotZ() > 0)
					super.setRotZ(0);
			}
			else if (this.getRotZ() > 0) {
				super.rotate(0, 0, -0.7f);
				
				if (this.getRotZ() < 0)
					super.setRotZ(0);
			}
					
		}
		
		//TODO: Rotate according to vertical speed
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			this.upwardsSpeed = VERT_POWER;
			
			if (super.getRotX() > -15)
				super.rotate(-0.2f, 0, 0);
			
		} else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			this.upwardsSpeed = -VERT_POWER;
			if (super.getRotX() < 15)
				super.rotate(0.2f, 0, 0);
		}
		else {
			if (super.getRotX() < 0) {
				super.rotate(0.3f, 0, 0);
				
				if (super.getRotX() > 0)
					super.setRotX(0);
				
				while (this.upwardsSpeed > 0)
					upwardsSpeed--;
			}
			else if (super.getRotX() > 0) {
				super.rotate(-0.3f, 0, 0);
				
				if (super.getRotX() < 0)
					super.setRotX(0);

				while (this.upwardsSpeed < 0)
					upwardsSpeed++;
			}
		}
		
		while (Keyboard.next()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				isSeperated = !isSeperated;
			}	
		}
		
	}

	@Override
	public void choreCollisions(List<Enemy> enemies, RaysCast caster) {
		
	}
	
	@Override
	public void respondToCollisioni(float damage) {
		
	}

	@Override
	@Deprecated
	public void respondToCollision() {
		System.err.println("Error: use respondToCollisioni() instead ");
		System.exit(1);
	}

}
