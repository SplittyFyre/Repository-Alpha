package scene.entities.projectiles;

import org.lwjgl.util.vector.Vector3f;

import box.TaskManager;
import renderEngine.DisplayManager;
import renderEngine.models.TexturedModel;
import utils.SFMath;

public class Bolt extends Projectile {
	
	private float moveCoff = 0;
	private float mcX, mcY, mcZ;
	private float elapsedTime = 0;
	private boolean split = false;
	private static final int SPEED = 3200;
	
	@Override
	public boolean isDead() {
		return super.isDead;
	}

	public Bolt(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, float damage, float movementCoefficient) {
		super(model, position, rotX, rotY, rotZ, scale, damage);
		this.moveCoff = movementCoefficient;
	}
	
	public Bolt(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float 
			scaleX, float scaleY, float scaleZ, float damage, float mcX, float mcY, float mcZ, boolean split) {
		super(model, position, rotX, rotY, rotZ, scaleX, scaleY, scaleZ, damage, 0);
		super.setScale(scaleX, scaleY, scaleZ); 
		this.mcX = mcX;
		this.mcY = mcY;
		this.mcZ = mcZ;
		System.out.println(mcX);
		System.out.println(mcY);
		System.out.println(mcZ);
		this.split = split;
	}
	
	public Bolt(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ
			, float damage, float movCoff) {
		super(model, position, rotX, rotY, rotZ, scaleX, scaleY, scaleZ, damage, 0);
		this.moveCoff = movCoff < 0 ? 0 : movCoff;
		super.setScale(scaleX, scaleY, scaleZ);
	}

	@Override
	public void update() {
		
		float distanceMoved = (SPEED + this.moveCoff) * DisplayManager.getFrameTime();
		float dx = (float) (distanceMoved * Math.sin(Math.toRadians(super.getRotY())));
		float dy = (float) (distanceMoved * Math.sin(Math.toRadians(-super.getRotX())));
		float dz = (float) (distanceMoved * Math.cos(Math.toRadians(super.getRotY())));
		dx += mcX;
		dy += mcY;
		dz += mcZ;
		super.move(dx, dy, dz);
		elapsedTime += DisplayManager.getFrameTime();
		if (elapsedTime > 5f)
			super.isDead = true;
		
	}

	@Override
	public float getDamage() {
		return this.damage;
	}

	@Override
	public void respondToCollision() {
		this.setDead();
		TaskManager.burnParticleSystem.generateParticles(this.getPosition());
	}
	
	public static Bolt phaser(Vector3f position, float damage, float rotX, float rotY, float rotZ, float movCoff) {
		return new Bolt(TaskManager.phaserBolt, new Vector3f(position), rotX, rotY, rotZ, 1.5f, 1.5f, 8, damage, movCoff);
	}
	
	public static Bolt phaser(Vector3f position, float magSide, float magHeight, float magFront, float damage, float rotX, float rotY, float rotZ, float movCoff) {
		return new Bolt(TaskManager.phaserBolt, 
				new Vector3f(
						
						position.x 
						+ SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_RIGHT, rotY, magSide)
						- SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, rotY, magFront)
						,
						position.y + magHeight
						,
						position.z
						+ SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_RIGHT, rotY, magSide)
						- SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, rotY, magFront)
						
						), rotX, rotY, rotZ, 1.5f, 1.5f, 8, damage, movCoff);
	}
	
}