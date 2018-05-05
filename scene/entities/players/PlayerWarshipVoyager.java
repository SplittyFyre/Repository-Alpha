 package scene.entities.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import box.TaskManager;
import objStuff.OBJParser;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.guis.buttons.IButton;
import renderEngine.guis.buttons.SFAbstractButton;
import renderEngine.models.RawModel;
import renderEngine.models.TexturedModel;
import renderEngine.textures.GUITexture;
import renderEngine.textures.ModelTexture;
import scene.entities.Entity;
import scene.entities.projectiles.Bolt;
import scene.entities.projectiles.Torpedo;
import utils.RaysCast;
import utils.SFMath;
import utils.SFUT;

public class PlayerWarshipVoyager extends Entity {
	
	private List<Bolt> bolts = new ArrayList<Bolt>();
	public List<Entity> projectiles = new ArrayList<Entity>();
	private List<Torpedo> torpedoes = new ArrayList<Torpedo>();
	
	private static final float MOVE_SPEED = 750;
	private static final float TURN_SPEED = 180;
	private static final float VERT_POWER = 60;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;

	private boolean hover = false;
	private boolean toggle = true;
	private boolean toggle1 = false;
	
	private Loader loader = new Loader();
	private List<GUITexture> guis;
	
	private SFAbstractButton viewScreenViewAft;
	//private SFAbstractButton viewScreenViewMap; 
	
	private SFAbstractButton phaserbutton;
	private SFAbstractButton frontphotonbutton;
	private SFAbstractButton frontquantumbutton;
	
	private SFAbstractButton changebuttontactical;
	private SFAbstractButton changebuttonops;
	private SFAbstractButton changebuttonhelm;
	
	public float phaserEnergy = 5000;
	
	private int currentPanel = 1;
	private final int TACTICAL_PANEL = 1;
	private final int OPS_PANEL = 2;
	private final int HELM_PANEL = 3;
	
	private List<SFAbstractButton> tacticalElements = new ArrayList<SFAbstractButton>();
	private List<SFAbstractButton> opsElements = new ArrayList<SFAbstractButton>();
	private List<SFAbstractButton> helmElements = new ArrayList<SFAbstractButton>();
	
	private Vector3f vec;
	private float alpha;
	private float counting;
	private float register = 0;
	private float counter = 0;
	private float counter2 = 0;
	private float counterS = 1.25f;
	private boolean flag = false;
	private boolean flag2 = false;
	private float mx, my, mz;
	
	Random rng = new Random();
	
	private GUITexture gui_panel;
	
	RawModel prephaser = OBJParser.loadObjModel("bolt", loader);
	TexturedModel privatePhaserTexture = new TexturedModel(prephaser, new ModelTexture(loader.loadTexture("orange")));
	
	RawModel pretorpedo = OBJParser.loadObjModel("photon", loader);
	TexturedModel privateTorpedoTexture = new TexturedModel(pretorpedo, new ModelTexture(loader.loadTexture("photon")));
	TexturedModel privateSpecialTorpedoTexture = new TexturedModel(pretorpedo, new ModelTexture(loader.loadTexture("quantum")));
	
	public PlayerWarshipVoyager(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, List<GUITexture> guin) {
		super(model, position, rotX, rotY, rotZ, scale);
		
		privatePhaserTexture.getTexture().setUseFakeLighting(true);
		privatePhaserTexture.getTexture().setSpecularMap(loader.loadTexture("allGlow"));
		privatePhaserTexture.getTexture().setBrightDamper(2);
		
		privatePhaserTexture.getTexture().setUseFakeLighting(true);
		privateTorpedoTexture.getTexture().setSpecularMap(loader.loadTexture("allGlow"));
		privateTorpedoTexture.getTexture().setBrightDamper(0);
		privateSpecialTorpedoTexture.getTexture().setUseFakeLighting(true);
		privateSpecialTorpedoTexture.getTexture().setSpecularMap(loader.loadTexture("allGlow"));
		privateSpecialTorpedoTexture.getTexture().setBrightDamper(0);
		
		this.guis = guin;
		initGUIS();
	}
	
	//UI STUFF*****************************************
	
	private void initGUIS() {
		
		viewScreenViewAft = new SFAbstractButton(loader, "masterpiece", new Vector2f(0.3f, 0.5f), new Vector2f(0.04f / 1.68f, 0.04f)) {
			
			@Override
			public void whileHovering(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void whileHolding(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStopHover(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartHover(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClick(IButton button) {
				// TODO Auto-generated method stub
				
			}
		};
		
		changebuttontactical = new SFAbstractButton(loader, "tacticalicon", new Vector2f(0.3f, 0.2f), new Vector2f(0.04f / 1.68f, 0.04f)) {
			
			@Override
			public void whileHovering(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void whileHolding(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStopHover(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartHover(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClick(IButton button) {
				gui_panel.setTexture(loader.loadTexture("panel"));
				currentPanel = TACTICAL_PANEL;
				for (SFAbstractButton element : tacticalElements) {
					element.show(guis);
				}
			}
		};
		
		changebuttonhelm = new SFAbstractButton(loader, "helmicon", new Vector2f(0.5f, 0.2f), new Vector2f(0.04f / 1.68f, 0.04f)) {
			
			@Override
			public void whileHovering(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void whileHolding(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStopHover(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartHover(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClick(IButton button) {
				gui_panel.setTexture(loader.loadTexture("panel3"));
				currentPanel = HELM_PANEL;
				for (SFAbstractButton element : tacticalElements) {
					element.hide(guis);
				}
			}
		};
		
		//GUITexture gui = new GUITexture(this.loader.loadTexture("orange"), new Vector2f(0.f, 0.5f), new Vector2f(0.05f, 0.5f));
		//guis.add(gui);
		gui_panel = new GUITexture(loader.loadTexture("panel"), new Vector2f(0.65f, -0.3f), new Vector2f(0.35f, 0.7f));
		guis.add(gui_panel);
		phaserbutton = new SFAbstractButton(loader, "phaserbutton", new Vector2f(0.4f, 0), new Vector2f(0.04f / 1.68f, 0.04f)) {
			
			@Override
			public void whileHovering(IButton button) {
		
			}
			
			@Override
			public void onStopHover(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartHover(IButton button) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onClick(IButton button) {
				
			}

			@Override
			public void whileHolding(IButton button) {
				fireFrontPhasers();
			}
		};
		
		frontphotonbutton = new SFAbstractButton(loader, "photonbutton", new Vector2f(0.35f, -0.2f), new Vector2f(0.04f / 1.68f, 0.04f)) {
			
			@Override
			public void whileHovering(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void whileHolding(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStopHover(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartHover(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClick(IButton button) {
				fireSecondaryForwardPhotons();
			}
		};
		
		frontquantumbutton = new SFAbstractButton(loader, "quantumbutton", new Vector2f(0.45f, -0.2f), new Vector2f(0.04f / 1.68f, 0.04f)) {
			
			@Override
			public void whileHovering(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void whileHolding(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStopHover(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartHover(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClick(IButton button) {
				fireSecondaryForwardQuantums();
			}
		};
		
		tacticalElements.add(phaserbutton);
		tacticalElements.add(frontphotonbutton);
		tacticalElements.add(frontquantumbutton);
		
		changebuttonhelm.show(guis);
		changebuttontactical.show(guis);
		
		viewScreenViewAft.show(guis);
		
		for (SFAbstractButton element : tacticalElements) {
			element.show(guis);
		}
		
	}
	
	private void updateGUIS() {
		
		changebuttonhelm.update();
		changebuttontactical.update();
		
		viewScreenViewAft.update();
		
		switch (currentPanel) {
		
			case TACTICAL_PANEL:
				for (SFAbstractButton element : tacticalElements) {
					element.update();
				}
				break;
				
			case HELM_PANEL:
				
				break;
			
		}

	}
	
	//UI STUFF*****************************************
	
	public void update(RaysCast caster) {
		move();
		fireAllWeapons(caster);
		projectiles.clear();
		projectiles.addAll(bolts);
		projectiles.addAll(torpedoes);
		if (phaserEnergy < 5000)
			phaserEnergy += 2.5;
		if (phaserEnergy > 5000)
			phaserEnergy = 5000;
		
		updateGUIS();
		
		while (Keyboard.next()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			frontphotonbutton.getTexture().getPosition().y += 0.1f;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			frontphotonbutton.getTexture().getPosition().y -= 0.1f;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			frontphotonbutton.getTexture().getPosition().x -= 0.1f;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			frontphotonbutton.getTexture().getPosition().x += 0.1f;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
				SFUT.println(frontphotonbutton.getTexture().getPosition());
			}
		}
	}
	
	private void move() {
		checkInputs();
		super.rotate(0, currentTurnSpeed * DisplayManager.getFrameTime(), 0);	
		float distanceMoved = currentSpeed * DisplayManager.getFrameTime();
		float dx = (float) (distanceMoved * Math.sin(Math.toRadians(super.getRotY())));
		float dy = (float) (distanceMoved * Math.sin(Math.toRadians(super.getRotX())));
		float dz = (float) (distanceMoved * Math.cos(Math.toRadians(super.getRotY())));
		super.move(dx, -dy, dz);
		this.mx += dx; this.mx /= 2; this.my += dy; this.my /= 2; this.mz += dz; this.mz /= 2;
		//super.move(0, upwardsSpeed * DisplayManager.getFrameTime(), 0);
		this.setRotY(this.getRotY() % 360);
		if (this.getRotY() < 0) {
			this.setRotY(Math.abs(360 - this.getRotY()));
		}
	}
	
	private void checkInputs() {
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W) && !Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
			this.currentSpeed = MOVE_SPEED;
		
		else if (Keyboard.isKeyDown(Keyboard.KEY_S))
			this.currentSpeed = -MOVE_SPEED;
		
		else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeed = MOVE_SPEED * 30 * 9.975f;
			TaskManager.warpParticleSystem.generateParticles(new Vector3f(super.getPosition()));
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
	
	}
	
	private void fireFrontPhasers() {
		
		Vector3f playerPos = (super.getPosition());
		
		bolts.add(new Bolt(privatePhaserTexture, new Vector3f((float) (playerPos.x + (Math.sin
				(Math.toRadians(super.getRotY() - 90)) / 1.35) + Math.sin
				(Math.toRadians(super.getRotY())) * 32), playerPos.y + 20.9f, (float) (playerPos.z + (Math.cos
						(Math.toRadians(super.getRotY() - 90)) / 1.35) + Math.cos
						(Math.toRadians(super.getRotY())) * 32)), super.getRotX(), super.getRotY(), 0, 1.5f, 1.5f, 5, 5, this.currentSpeed));
		
		bolts.add(new Bolt(privatePhaserTexture, new Vector3f((float) (playerPos.x + (Math.sin(Math.toRadians(super.getRotY() + 90)) * 4) + Math.sin
				(Math.toRadians(super.getRotY())) * 19), playerPos.y + 22.35f, (float) (playerPos.z + (Math.cos
						(Math.toRadians(super.getRotY() + 90)) * 4) + Math.cos
						(Math.toRadians(super.getRotY())) * 19) ), super.getRotX(), super.getRotY(), 0, 1.5f, 1.5f, 5, 5, this.currentSpeed));
		
		bolts.add(new Bolt(privatePhaserTexture, new Vector3f((float) (playerPos.x + (Math.sin(Math.toRadians(super.getRotY() - 90)) * 5) + Math.sin
				(Math.toRadians(super.getRotY())) * 19), playerPos.y + 22.35f, (float) (playerPos.z + (Math.cos
						(Math.toRadians(super.getRotY() - 90)) * 5) + Math.cos
						(Math.toRadians(super.getRotY())) * 19) ), super.getRotX(), super.getRotY(), 0, 1.5f, 1.5f, 5, 5, this.currentSpeed));
	}
	
	private void fireSecondaryForwardPhotons() {
		
		Vector3f playerPos = (super.getPosition());
		float distanceToMove = (this.currentSpeed + 410) * DisplayManager.getFrameTime();
		
		torpedoes.add(new Torpedo(privateTorpedoTexture, new Vector3f((float) (playerPos.x + (Math.sin(Math.toRadians(super.getRotY() + 90)) * 5) + Math.sin
				(Math.toRadians(super.getRotY())) * 11), playerPos.y + 11, (float) (playerPos.z + (Math.cos
						(Math.toRadians(super.getRotY() + 90)) * 4) + Math.cos
						(Math.toRadians(super.getRotY())) * 11) ), 0, super.getRotY(), 0, 2, 2, 5, 100, (float) (distanceToMove * 
						 Math.sin(Math.toRadians(super.getRotY()))), 0, (float) (distanceToMove * Math.cos(Math.toRadians(super.getRotY())))));
		
		torpedoes.add(new Torpedo(privateTorpedoTexture, new Vector3f((float) (playerPos.x + (Math.sin(Math.toRadians(super.getRotY() - 90)) * 5) + Math.sin
				(Math.toRadians(super.getRotY())) * 11), playerPos.y + 11, (float) (playerPos.z + (Math.cos
						(Math.toRadians(super.getRotY() - 90)) * 4) + Math.cos
						(Math.toRadians(super.getRotY())) * 11) ), 0, super.getRotY(), 0, 2, 2, 5, 100, (float) (distanceToMove * 
						 Math.sin(Math.toRadians(super.getRotY()))), 0, (float) (distanceToMove * Math.cos(Math.toRadians(super.getRotY())))));
		
	}
	
	private void fireSecondaryForwardQuantums() {
		
		Vector3f playerPos = (super.getPosition());
		float distanceToMove = (this.currentSpeed + 410) * DisplayManager.getFrameTime();
		
		torpedoes.add(new Torpedo(privateSpecialTorpedoTexture, new Vector3f((float) (playerPos.x + (Math.sin(Math.toRadians(super.getRotY() + 90)) * 5) + Math.sin
				(Math.toRadians(super.getRotY())) * 11), playerPos.y + 11, (float) (playerPos.z + (Math.cos
						(Math.toRadians(super.getRotY() + 90)) * 4) + Math.cos
						(Math.toRadians(super.getRotY())) * 11) ), 0, super.getRotY(), 0, 2, 2, 5, 200, (float) (distanceToMove * 
						 Math.sin(Math.toRadians(super.getRotY()))), 0, (float) (distanceToMove * Math.cos(Math.toRadians(super.getRotY())))));
		
		torpedoes.add(new Torpedo(privateSpecialTorpedoTexture, new Vector3f((float) (playerPos.x + (Math.sin(Math.toRadians(super.getRotY() - 90)) * 5) + Math.sin
				(Math.toRadians(super.getRotY())) * 11), playerPos.y + 11, (float) (playerPos.z + (Math.cos
						(Math.toRadians(super.getRotY() - 90)) * 4) + Math.cos
						(Math.toRadians(super.getRotY())) * 11) ), 0, super.getRotY(), 0, 2, 2, 5, 200, (float) (distanceToMove * 
						 Math.sin(Math.toRadians(super.getRotY()))), 0, (float) (distanceToMove * Math.cos(Math.toRadians(super.getRotY())))));

		
	}
	
	private void firePhaserSpray() {
		
		bolts.add(new Bolt(privatePhaserTexture, new Vector3f(super.getPosition().x, super.getPosition().y + 10, super.getPosition().z), 
				super.getRotX() + rng.nextFloat() * 2 - 1, super.getRotY() + rng.nextFloat() * 2 - 1, super.getRotZ(),
				1.5f, 1.5f, 20, 3, mx, my, mz, false));
	}
	
	private void fireDorsalPortArrays() {
		
		bolts.add(new Bolt(privatePhaserTexture, 
				new Vector3f(super.getPosition().x + 
						SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_LEFT, super.getRotY(), 9.1f)
				- SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, super.getRotY(), 12)
						, super.getPosition().y + 33,
						super.getPosition().z + SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_LEFT, super.getRotY(), 9.1f)
						- SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, super.getRotY(), 12)),
				-super.getRotX() - 15, super.getRotY() + 180, 0
				, 1.5f, 1.5f, 20, 69, mx, my, mz, true));
		
	}
	
	private void fireAllWeapons(RaysCast caster) {
			
		Vector3f ray = new Vector3f(caster.getCurrentRay());
		Vector3f target = new Vector3f(caster.getPointOnRay(ray, 1000));
			
		if (Keyboard.isKeyDown(Keyboard.KEY_P) && phaserEnergy - 7 >= 0)
			fireFrontPhasers();
		
		if (Keyboard.isKeyDown(Keyboard.KEY_O) && counter < 0) {
			fireSecondaryForwardPhotons();
			counter = 2;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_I) && counter < 0) {
			fireSecondaryForwardQuantums();
			counter = 0.4f;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_U) && !flag) {
			flag = true;
		}
		
		if (flag) {
			if (counterS >= 1.25f) {
				counterS = 1.25f;
				fireSecondaryForwardPhotons();
			}
			
			if (SFMath.nIsWithin(counterS, 0, 1)) {
				fireFrontPhasers();
			}
			
			if (counterS < 0) {
				fireSecondaryForwardQuantums();
				counterS = 10;
				flag = false;
			}
			counterS -= DisplayManager.getFrameTime();
		}
		
		counter -= DisplayManager.getFrameTime();
		
		while (Keyboard.next()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_T) && phaserEnergy > 0) {
				Vector3f rotations = new Vector3f(SFMath.rotateToFaceVector(super.getPosition(), target));
				torpedoes.add(new Torpedo(privateTorpedoTexture, new Vector3f(super.getPosition()), 
						rotations.x, rotations.y, rotations.z, 2, 2, 5, 100, caster.getCurrentRay().x * 10, 
						caster.getCurrentRay().y * 10, caster.getCurrentRay().z * 10));
			}	
			
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_G) && phaserEnergy > 0) {
			fireDorsalPortArrays();
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_Z) && !flag2) {
			flag2 = true;
			vec = SFMath.rotateToFaceVector(super.getPosition(), new Vector3f(0, 0, 0));
			alpha = vec.y - super.getRotY();
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
			firePhaserSpray();
		}
		
		if (flag2) {
			
			float toRot = TURN_SPEED * DisplayManager.getFrameTime();
			
			if (register < Math.abs(alpha)) {
				super.rotate(0, toRot * Math.signum(alpha), 0);
				register += toRot;
			}
			else {
				register = 0;
				flag2 = false;
			}
			
		}
		
		for (int i = 0; i < bolts.size(); i++) {
			Bolt bolt = bolts.get(i);
			if (!bolt.isDead())
				bolt.update();
			else
				bolts.remove(i);
		}
		
		for (int i = 0; i < torpedoes.size(); i++) {
			Torpedo torp = torpedoes.get(i);
			if (!torp.isDead())
				torp.update();
			else
				torpedoes.remove(i);
		}
		
	}
	
	public float getPhaserEnergy() {
		return phaserEnergy;
	}
	
	@Override
	public void respondToCollision() {
		System.exit(0);
	}
	
}
