package scene.entities.hostiles;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import box.Main;
import box.TaskManager;
import objStuff.OBJParser;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.models.RawModel;
import renderEngine.models.TexturedModel;
import renderEngine.textures.ModelTexture;
import scene.entities.Entity;
import scene.entities.projectiles.Bolt;
import scene.entities.projectiles.HomingTorpedo;
import scene.entities.projectiles.Torpedo;
import utils.SFMath;

public class BorgVessel extends Enemy {
	
	RawModel pretorpedo = OBJParser.loadObjModel("photon");
	TexturedModel privateTorpedoTexture = new TexturedModel(pretorpedo, new ModelTexture(Loader.loadTexture("allGlow")));
	
	RawModel prephaser = OBJParser.loadObjModel("bolt");
	TexturedModel privatePhaserTexture = new TexturedModel(prephaser, new ModelTexture(Loader.loadTexture("allGlow")));
	
	private Entity player;
	private float HEALTH = 25000;
	private float movX, movY = 0, movZ;
	private float counter = 0, counter1 = 0, counter2 = 0, counter3 = 0;
	private boolean flag = false, flag1 = false, flag2 = false;
	
	private List<Torpedo> seeking = new ArrayList<Torpedo>();

	public BorgVessel(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, Entity player) {
		super(model, position, rotX, rotY, rotZ, scale);
		movX = (TaskManager.rng.nextFloat() * 2 - 1) * 10;
		//movY = (TaskManager.rng.nextFloat() * 2 - 1) * 10;
		movZ = (TaskManager.rng.nextFloat() * 2 - 1) * 10;
		privateTorpedoTexture.getTexture().setSpecularMap(Loader.loadTexture("allGlow"));
		privateTorpedoTexture.getTexture().setBrightDamper(1);
		
		privatePhaserTexture.getTexture().setUseFakeLighting(true);
		privatePhaserTexture.getTexture().setSpecularMap(Loader.loadTexture("allGlow"));
		privatePhaserTexture.getTexture().setBrightDamper(1);
		
		this.player = player;
	}
	
	public void update() {
		
		Vector3f vec = SFMath.rotateToFaceVector(super.getPosition(), player.getPosition());
		
		float var = DisplayManager.getFrameTime() * 1000;
		float f = DisplayManager.getFrameTime() * 3500;
		
		float dist = SFMath.distance(player.getPosition(), super.getPosition());
		
		float homingX = (float) (var * Math.sin(Math.toRadians(vec.y)));
		float homingY = (float) (var * Math.sin(Math.toRadians(vec.x)));
		float homingZ = (float) (var * Math.cos(Math.toRadians(vec.y)));
		
		if (dist > 2000) {
			//super.move(homingX, homingY, homingZ);
		}
		
		//super.move(movX, movY, movZ);
		super.rotate(0, 0.15f, 0);
		this.counter += DisplayManager.getFrameTime();
		this.counter1 += DisplayManager.getFrameTime();
		
		if (dist <= 10000) {
			if (counter > 1 && !flag) {
				Main.foeprojectiles.add(new Torpedo(privateTorpedoTexture, 
					new Vector3f(super.getPosition()),
					0, 0, 0, 3, 3, 6.5f, 250, 
					(float) (f * Math.sin(Math.toRadians(vec.y))), (float) ((float) (f * Math.sin(Math.toRadians(vec.x))) + 
							Math.random() - 0.5f), 
					(float) (f * Math.cos(Math.toRadians(vec.y)))));
				flag = true;
			}
			else if (counter > 1.25f) {
				/*Main.foeprojectiles.add(new Torpedo(privateTorpedoTexture, 
						new Vector3f(super.getPosition()),
						0, 0, 0, 3, 3, 6.5f, 250, 
						(float) (f * Math.sin(Math .toRadians(vec.y))), (float) ((float) (f * Math.sin(Math.toRadians(vec.x))) + 
								Math.random() - 0.5f), 
						(float) (f * Math.cos(Math.toRadians(vec.y)))));*/
				
				Vector3f d = Vector3f.sub(player.getPosition(), super.getPosition(), null);
				float xy = (float) Math.sqrt(d.x * d.x + d.z * d.z);
				
				Main.foeprojectiles.add(new Torpedo(privateTorpedoTexture, 
						new Vector3f(super.getPosition()),
						0, 0, 0, 3, 3, 6.5f, 250, 
						f * (d.x / xy), (float) ((float) (f * Math.sin(Math.toRadians(vec.x))) + 
								Math.random() - 0.5f), 
						f * (d.z / xy)));
				
				counter = 0;
				flag = false;
			}
			
			if (TaskManager.rng.nextInt(150) == 7) {
				Main.foeprojectiles.add(new Bolt(privatePhaserTexture, 
						new Vector3f(super.getPosition()), 
						-(vec.x), vec.y + (float) Math.random(), 0, 
						1.5f, 1.5f, 75, 75, 0));
			}
			
			vec = SFMath.rotateToFaceVector(new Vector3f(super.getPosition().x, super.getPosition().y + 400, super.getPosition().z),
					player.getPosition());
			
			/*if (TaskManager.rng.nextInt(225) < 2) {
			    Main.foeprojectiles.add(new Torpedo(privateTorpedoTexture, 
						new Vector3f(super.getPosition().x, super.getPosition().y + 400, super.getPosition().z),
						0, 0, 0, 3, 3, 6.5f, 250, 
						(float) (f * Math.sin(Math.toRadians(vec.y))), (float) (f * Math.sin(Math.toRadians(vec.x))), 
						(float) (f * Math.cos(Math.toRadians(vec.y)))));
			}*/
			
			if (dist <= 3000 && counter1 > 0.5f) {
				Main.foeprojectiles.add(new HomingTorpedo(privateTorpedoTexture,
						new Vector3f(super.getPosition().x, super.getPosition().y + 400, super.getPosition().z), 
						3, 3, 6.5f, 300, 3000, 15, player, 
						0, -5, 0));
				counter1 = 0;
			}

		}
		
	}
	
	public List<Torpedo> getHomingMissiles() {
		return seeking;
	}

	@Override
	public void respondToCollisioni(float damage) {
		HEALTH -= damage;
		if (HEALTH <= 0) {
			this.setDead();
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
		HEALTH -= damage;
		if (HEALTH <= 0)
			this.setDead();
	}
	
	public void setTargeted() {
		this.setHighlight(new Vector4f(1, 0, 0, 0.5f));
	}
	
	public void dropTarget() {
		this.setHighlight(null);
	}

}
