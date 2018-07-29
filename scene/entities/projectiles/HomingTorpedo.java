package scene.entities.projectiles;

import org.lwjgl.util.vector.Vector3f;

import box.TaskManager;
import renderEngine.DisplayManager;
import renderEngine.models.TexturedModel;
import scene.entities.Entity;
import scene.particles.Particle;
import scene.particles.ParticleTexture;
import utils.SFMath;

public class HomingTorpedo extends Projectile {

	private float speed;
	private Entity target;
	
	private float timer = 0;
	private float lifelength = 10;
	private Vector3f tracing;
	private float arcX, arcY, arcZ;
	
	private boolean trail = false;
	private ParticleTexture trailTexture = null;
	
	private float particleLife, particleScale;
	
	public HomingTorpedo(TexturedModel model, Vector3f position, float scaleX, float scaleY, float scaleZ,
			float damage, float speed, float lifelength, Entity target, float arcX, float arcY, float arcZ) {
		super(model, position, 0, 0, 0, scaleX, scaleY, scaleZ, damage, 0, 0, 0);
		this.speed = speed;
		this.target = target;
		this.lifelength = lifelength;
		this.arcX = arcX;
		this.arcY = arcY;
		this.arcZ = arcZ;
	}
	
	public HomingTorpedo(TexturedModel model, Vector3f position, float scaleX, float scaleY, float scaleZ,
			float damage, float speed, float lifelength, Entity target, float arcX, float arcY, float arcZ, 
			ParticleTexture trailTexture, float particleLife, float particleScale) {
		super(model, position, 0, 0, 0, scaleX, scaleY, scaleZ, damage, 0, 0, 0);
		this.speed = speed;
		this.target = target;
		this.lifelength = lifelength;
		this.arcX = arcX;
		this.arcY = arcY;
		this.arcZ = arcZ;
		
		this.trail = true;
		
		this.trailTexture = trailTexture;
		this.particleLife = particleLife;
		this.particleScale = particleScale;
	}
	
	@Override
	public void update() {
		
		if (target == null) {
			this.respondToCollision();
		}
		timer += DisplayManager.getFrameTime();
		float dtmove = DisplayManager.getFrameTime() * this.speed;
		if (target != null)
			tracing = SFMath.rotateToFaceVector(this.getPosition(), target.getPosition());
		else
			tracing = new Vector3f(0, 0, 0);
		
		float homingX = (float) (dtmove * Math.sin(Math.toRadians(tracing.y + arcX)));
		float homingY = (float) (dtmove * Math.sin(Math.toRadians(tracing.x + arcY)));
		float homingZ = (float) (dtmove * Math.cos(Math.toRadians(tracing.y + arcZ)));
		
		super.move(homingX, homingY, homingZ);
		
		super.rotate(30f, 18f, -12.6f);
		
		if (trail) {
			new Particle(trailTexture, new Vector3f(super.getPosition()), new Vector3f(0, 0, 0), 0, 
					this.particleLife, 0, this.particleScale);
		}
		
		if (timer > lifelength) 
			this.respondToCollision();
	}
	
	public void setTarget(Entity arg) {
		this.target = arg;
	}
	
	public Entity getTarget() {
		return this.target;
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
