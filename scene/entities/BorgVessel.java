package scene.entities;

import org.lwjgl.util.vector.Vector3f;

import box.TaskManager;
import renderEngine.models.TexturedModel;
import scene.entities.entityUtils.ITakeDamage;

public class BorgVessel extends Entity implements ITakeDamage {
	
	private float health = 25000;
	private boolean isDead = false;

	public BorgVessel(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public boolean isDead() {
		return isDead;
	}

	@Override
	public void respondToCollisioni(float damage) {
		health -= damage;
		if (health <= 0) {
			this.isDead = true;
			TaskManager.borgExplosionSystem.generateParticles(this.getPosition());
		}
	}

	/**
	 * @deprecated
	 * **/
	@Deprecated
	@Override
	public void respondToCollision() {
		
	}
	
	public void takeDamage(float damage) {
		health -= damage;
		if (health <= 0)
			this.isDead = true;
	}

}
