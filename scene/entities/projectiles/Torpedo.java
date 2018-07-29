package scene.entities.projectiles;

import org.lwjgl.util.vector.Vector3f;

import audio.AudioEngine;
import box.TaskManager;
import renderEngine.DisplayManager;
import renderEngine.models.TexturedModel;
import utils.SFMath;

public class Torpedo extends Projectile {
	
	public static final float PT = 250;
	public static final float QT = 400;
	
	private float elapsedTime = 0;
	private float lifeLength = 4;
	
	@Override
	public boolean isDead() {
		return super.isDead;
	}

	public Torpedo(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, 
			float scaleX, float scaleY, float scaleZ, float damage, float dx, float dy, float dz) {
		super(model, position, rotX, rotY, rotZ, scaleX, scaleY, scaleZ, damage, dx, dy, dz);
	}
	
	public Torpedo(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, 
			float scaleX, float scaleY, float scaleZ, float damage, float dx, float dy, float dz, float life) {
		super(model, position, rotX, rotY, rotZ, scaleX, scaleY, scaleZ, damage, dx, dy, dz);
		this.lifeLength = life;
	}

	public void setMovement(float dx, float dy, float dz) {
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
	}
	
	public void setLifeLength(float arg) {
		this.lifeLength = arg;
	}

	@Override
	public void update() {
		elapsedTime += DisplayManager.getFrameTime();
		super.move(dx, dy, dz);
		super.rotate(30f, 18f, -12.6f);
		if (elapsedTime > lifeLength) 
			respondToCollision();
	}

	@Override
	public float getDamage() {
		return this.damage;
	}

	@Override
	public void respondToCollision() {
		this.setDead();
		TaskManager.explosionParticleSystem.generateParticles(this.getPosition());
	}
	
	
	public static Torpedo photonTorpedo(Vector3f position, float dx, float dy, float dz) {
		return new Torpedo(TaskManager.photonTorpedo, new Vector3f(position), 0, 0, 0, 2, 2, 5, PT, dx, dy, dz, 10);
	}
	
	public static Torpedo quantumTorpedo(Vector3f position, float dx, float dy, float dz) {
		return new Torpedo(TaskManager.quantumTorpedo, new Vector3f(position), 0, 0, 0, 2, 2, 5, QT, dx, dy, dz, 10);
	}
	
	public static Torpedo photonTorpedo(Vector3f position, float dx, float dy, float dz, 
			float magSide, float magHeight, float magFront, float rotY) {
		
		return new Torpedo(TaskManager.photonTorpedo, new Vector3f(
				
				position.x 
				+ SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_RIGHT, rotY, magSide)
				- SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, rotY, magFront)
				,
				position.y + magHeight
				,
				position.z
				+ SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_RIGHT, rotY, magSide)
				- SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, rotY, magFront)
				
				),
				0, 0, 0, 2, 2, 5, PT, dx, dy, dz);
	}
	
	public static Torpedo quantumTorpedo(Vector3f position, float dx, float dy, float dz, 
			float magSide, float magHeight, float magFront, float rotY) {
		
		return new Torpedo(TaskManager.quantumTorpedo, new Vector3f(
				
				position.x 
				+ SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_RIGHT, rotY, magSide)
				- SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, rotY, magFront)
				,
				position.y + magHeight
				,
				position.z
				+ SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_RIGHT, rotY, magSide)
				- SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, rotY, magFront)
				
				),
				0, 0, 0, 2, 2, 5, QT, dx, dy, dz);
	}
	
	public static Torpedo photonTorpedo(Vector3f position, float speed, 
			float magSide, float magHeight, float magFront, float rotY, float rotX) {
		
		float move = DisplayManager.getFrameTime() * speed;
		
		AudioEngine.playTempSrc(TaskManager.photonsnd, 100, position.x, position.y, position.z);
		
		return new Torpedo(TaskManager.photonTorpedo, new Vector3f(
				
				position.x 
				+ SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_RIGHT, rotY, magSide)
				- SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, rotY, magFront)
				,
				position.y + magHeight
				,
				position.z
				+ SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_RIGHT, rotY, magSide)
				- SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, rotY, magFront)
				
				),
				0, 0, 0, 2, 2, 5, PT, 
				
				(float) Math.sin(Math.toRadians(rotY)) * move
				,
				(float) Math.sin(Math.toRadians(-rotX)) * move
				,
				(float) Math.cos(Math.toRadians(rotY)) * move
				);
	}
	
	public static Torpedo quantumTorpedo(Vector3f position, float speed, 
			float magSide, float magHeight, float magFront, float rotY, float rotX) {
		
		float move = DisplayManager.getFrameTime() * speed;
		
		AudioEngine.playTempSrc(TaskManager.quantumsnd, 100, position.x, position.y, position.z);
		
		return new Torpedo(TaskManager.quantumTorpedo, new Vector3f(
				
				position.x 
				+ SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_RIGHT, rotY, magSide)
				- SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, rotY, magFront)
				,
				position.y + magHeight
				,
				position.z
				+ SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_RIGHT, rotY, magSide)
				- SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, rotY, magFront)
				
				),
				0, 0, 0, 2, 2, 5, QT, 
				
				(float) Math.sin(Math.toRadians(rotY)) * move
				,
				(float) Math.sin(Math.toRadians(-rotX)) * move
				,
				(float) Math.cos(Math.toRadians(rotY)) * move
				);
	}
	
}
