package scene.entities.projectiles;

import org.lwjgl.util.vector.Vector3f;

import box.TaskManager;
import renderEngine.DisplayManager;
import renderEngine.models.TexturedModel;

public class Torpedo extends Projectile {
	
	private float elapsedTime = 0;
	
	public boolean isDead() {
		return super.isDead;
	}

	public Torpedo(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, 
			float scaleX, float scaleY, float scaleZ, float damage, float dx, float dy, float dz) {
		super(model, position, rotX, rotY, rotZ, scaleX, scaleY, scaleZ, damage, dx, dy, dz);
	}

	public void update() {
		elapsedTime += DisplayManager.getFrameTime();
		super.move(dx, dy, dz);
		if (elapsedTime > 4) 
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
	
}
