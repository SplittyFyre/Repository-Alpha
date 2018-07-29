 package scene.entities.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import audio.AudioEngine;
import audio.AudioSrc;
import box.TaskManager;
import collision.BoundingBox;
import fontMeshCreator.GUIText;
import objStuff.OBJParser;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.guis.IButton;
import renderEngine.guis.IGUI;
import renderEngine.guis.ISlider;
import renderEngine.guis.SFAbstractButton;
import renderEngine.guis.SFVerticalSlider;
import renderEngine.models.RawModel;
import renderEngine.models.TexturedModel;
import renderEngine.textures.GUITexture;
import renderEngine.textures.ModelTexture;
import scene.entities.Entity;
import scene.entities.hostiles.BorgVessel;
import scene.entities.hostiles.Enemy;
import scene.entities.projectiles.Bolt;
import scene.entities.projectiles.HomingTorpedo;
import scene.entities.projectiles.Projectile;
import scene.entities.projectiles.Torpedo;
import scene.particles.Particle;
import scene.particles.ParticleTexture;
import utils.RaysCast;
import utils.SFMath;

public class PlayerWarshipVoyager extends Player {
	
	private List<IGUI> tacticalElements = new ArrayList<IGUI>();
	private List<IGUI> opsElements = new ArrayList<IGUI>();
	private List<IGUI> helmElements = new ArrayList<IGUI>();
	
	private List<IGUI> miscElements = new ArrayList<IGUI>();
	
	private SFAbstractButton viewScreenViewAft;
	//private SFAbstractButton viewScreenViewMap;

	//BOOKMARK front phaser shoot button 
	private SFAbstractButton phaserbutton = new SFAbstractButton(tacticalElements, "phaserbutton", new Vector2f(0.4f, 0.1f), new Vector2f(0.04f / 1.68f, 0.04f)) {
		
		@Override
		public void whileHovering(IButton button) {
	
		}
		
		@Override
		public void onStopHover(IButton button) {
			
		}
		
		@Override
		public void onStartHover(IButton button) {
			
		}
		
		@Override
		public void onClick(IButton button) {
			
		}

		@Override
		public void whileHolding(IButton button) {
			fireFrontPhasers();
		}
	};
	
	//BOOKMARK front double photon shots
	private SFAbstractButton frontphotonbutton = new SFAbstractButton(tacticalElements, "photonbutton", new Vector2f(0.35f, -0.05f), new Vector2f(0.04f / 1.68f, 0.04f)) {
		
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
	
	//BOOKMARK front double quantum shots
	private SFAbstractButton frontquantumbutton = new SFAbstractButton(tacticalElements, "quantumbutton", new Vector2f(0.45f, -0.05f), new Vector2f(0.04f / 1.68f, 0.04f)) {
		
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
	
	//BOOKMARK switch LCARS to tactical
	private SFAbstractButton changebuttontactical = new SFAbstractButton(miscElements, "tacticalicon", new Vector2f(0.324f, 0.36f), new Vector2f(0.04f / 1.68f, 0.04f)) {
		
		@Override
		public void whileHovering(IButton button) {
			
		}
		
		@Override
		public void whileHolding(IButton button) {
			
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
			currentPanel = TACTICAL_PANEL;
			
			for (IGUI el : helmElements) {
				el.hide(guis);
			}
			for (IGUI el : tacticalElements) {
				el.show(guis);
			}
		}
	};
	
	//BOOKMARK switch LCARS to helm
	private SFAbstractButton changebuttonhelm = new SFAbstractButton(miscElements, "helmicon", new Vector2f(0.394f, 0.36f), new Vector2f(0.04f / 1.68f, 0.04f)) {
		
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
			currentPanel = HELM_PANEL;
			for (IGUI el : tacticalElements) {
				el.hide(guis);
			}
			for (IGUI el : helmElements) {
				el.show(guis);
			}
		}
	};
	
	//BOOKMARK transfer button energy to shields
	private SFAbstractButton energyshields = new SFAbstractButton(miscElements, "clear", new Vector2f(0.35f, 0.45f), new Vector2f(0.08f, 0.04f)) {
		
		@Override
		public void whileHovering(IButton button) {
			
		}
		
		@Override
		public void whileHolding(IButton button) {
			if (SHIELD + 100 <= FULL_SHIELDS) {
				energy -= 500;
				SHIELD += 100;
			}
		}
		
		@Override
		public void onStopHover(IButton button) {
			
		}
		
		@Override
		public void onStartHover(IButton button) {
			
		}
		
		@Override
		public void onClick(IButton button) {
			
		}
	};
	
	//BOOKMARK autogun to standard
	private SFAbstractButton autogunStd = new SFAbstractButton(tacticalElements, "image", new Vector2f(0.4f, 0.3f), new Vector2f(0.04f / 1.68f, 0.04f)) {
		
		@Override
		public void whileHovering(IButton button) {
			
		}
		
		@Override
		public void whileHolding(IButton button) {
			
		}
		
		@Override
		public void onStopHover(IButton button) {
			
		}
		
		@Override
		public void onStartHover(IButton button) {
			
		}
		
		@Override
		public void onClick(IButton button) {
			autoFunc = STD_AUTO;
		}
	};
	
	//BOOKMARK autogun to dead-weight
	private SFAbstractButton autogunDeadWeight = new SFAbstractButton(tacticalElements, "image", new Vector2f(0.5f, 0.3f), new Vector2f(0.04f / 1.68f, 0.04f)) {
		
		@Override
		public void whileHovering(IButton button) {
		
		}
	
		@Override
		public void whileHolding(IButton button) {
		
		}
	
		@Override
		public void onStopHover(IButton button) {
		
		}
	
		@Override
		public void onStartHover(IButton button) {
			
		}
	
		@Override
		public void onClick(IButton button) {
		autoFunc = DEADWEIGHT;
		}
		
	};
	
	//BOOKMARK impulse speed slider
	private SFVerticalSlider slider = new SFVerticalSlider(helmElements, 12, new Vector2f(0.65f, -0.5f), new Vector2f(0.08f / 1.68f, 0.08f), 0.04f, "knob", "ramp") {
		
		@Override
		public void sliderStopHover(ISlider slider) {
			
		}
		
		@Override
		public void sliderStartHover(ISlider slider) {
			
		}
	};
	
	//BOOKMARK
	//BOOKMARK
	//BOOKMARK
	//BOOKMARK
	//BOOKMARK
	//BOOKMARK
	//BOOKMARK
	
	private SFAbstractButton abilityStinger = new SFAbstractButton(tacticalElements, "stinger", new Vector2f(0, 0), new Vector2f(0.04f / 1.68f, 0.04f)) {
		
		@Override
		public void whileHovering(IButton button) {
			
		}
		
		@Override
		public void whileHolding(IButton button) {
			
		}
		
		@Override
		public void onStopHover(IButton button) {
			
		}
		
		@Override
		public void onStartHover(IButton button) {
			
		}
		
		@Override
		public void onClick(IButton button) {
			fireStinger();
		}
	};
	
	private void fireStinger() {
		if (this.target != null) {
			projectiles.add(new HomingTorpedo(privateTorpedoTexture, new Vector3f(super.getPosition())
					, 2.5f, 2.5f, 6.25f, 1500, 10000, 15, 
					this.target,
					(TaskManager.rng.nextFloat() * 2 - 1) * 10,
					(TaskManager.rng.nextFloat() * 2 - 1) * 10,
					(TaskManager.rng.nextFloat() * 2 - 1) * 10,
					new ParticleTexture(Loader.loadTexture("particleAtlas"), 4), 1 , 40));
		}
	}
	
	private SFAbstractButton abilityHydraRockets = new SFAbstractButton(tacticalElements, "image", new Vector2f(0, 0.1f), new Vector2f(0.04f / 1.68f, 0.04f)) {
		
		@Override
		public void whileHovering(IButton button) {
			
		}
		
		@Override
		public void whileHolding(IButton button) {
			
		}
		
		@Override
		public void onStopHover(IButton button) {
			
		}
		
		@Override
		public void onStartHover(IButton button) {
			
		}
		
		@Override
		public void onClick(IButton button) {
			fireHydraRockets();
		}
	};
	int texture = Loader.loadTexture("particleAtlas");
	Thread firethings = new Thread() {
		
		@Override
		public void run() {
			for (int i = 0; i < 30; i++) {
				fireHydraRockett();
				try {
					Thread.sleep(75);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	};
	
	private synchronized void fireHydraRockett() {
		projectiles.add(new HomingTorpedo(privateTorpedoTexture, new Vector3f(getPosition())
				, 2.5f, 2.5f, 6.25f, Torpedo.PT * 1.5f, 5000, 15, 
				target,
				(TaskManager.rng.nextFloat() * 2 - 1) * 10,
				(TaskManager.rng.nextFloat() * 2 - 1) * 10,
				(TaskManager.rng.nextFloat() * 2 - 1) * 10,
				new ParticleTexture(texture, 4), 0.5f, 20));
	}
	
	private void fireHydraRockets() {
		if (this.target != null &! firethings.isAlive()) {
			firethings = new Thread() {
				
				@Override
				public void run() {
					for (int i = 0; i < 30; i++) {
						fireHydraRockett();
						try {
							Thread.sleep(75);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
				
			};
			firethings.start();
		}
	}
	
	private int currentPanel = 1;
	private final int TACTICAL_PANEL = 1;
	private final int OPS_PANEL = 2;
	private final int HELM_PANEL = 3;
	
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
	private GUITexture schematic;
	
	//BOOKMARK: TACTICAL VARS
	
	private float HEALTH = 12500;
	private float SHIELD = 100000;
	
	private static final float FULL_SHIELDS = 100000;
	
	private float turretTimer = 0;
	private boolean turretToggle = true;
	
	private int turretFunc = 0;
	
	private static final int TURRET_MIN = 0;
	private static final int TURRET_MAX = 2; 
	
	private static final int ONE_TORP = 0;
	private static final int TWO_SHOT = 1;
	private static final int PHASER_QUANTUM = 2;
	
	private int autoFunc = 0;
	
	private static final int AUTO_MIN = 0;
	private static final int AUTO_MAX = 3;
	
	private static final int STD_AUTO = 0;
	private static final int DEADWEIGHT = 1;
	private static final int GATLING = 2;
	private static final int TORPEDO_SPLIT = 3;
	
	private static final float distBetweenSlots = 0.05f;
	private static final float hotbarheight = -0.9f;
	private static final float hotbarx = 0.35f;
	

	private float autogunTimer = 0;
	
	private GUIText healthText;
	private GUIText shieldsText;
	private GUIText energyText;
	
	private GUIText coordsX = new GUIText("Loading...", 1.7f, TaskManager.font, new Vector2f(0, 0), 0.5f, false);
	private GUIText coordsY = new GUIText("Loading...", 1.7f, TaskManager.font, new Vector2f(0, 0.05f), 0.5f, false);
	private GUIText coordsZ = new GUIText("Loading...", 1.7f, TaskManager.font, new Vector2f(0, 0.1f), 0.5f, false);
	
	private Particle retical;
	
	private Enemy target;
	
	public Enemy getTarget() {
		return target;
	}

	public void setTarget(Enemy target) {
		this.target = target;
	}
	
	public void setRetical() {
		this.retical = new Particle(new ParticleTexture(Loader.loadTexture("retical2"), 1), target.getPosition(),
				new Vector3f(0, 0, 0), 0, Float.POSITIVE_INFINITY, 0, 500, true);
	}
	
	public void dropRetical() {
		if (this.retical != null)
			this.retical.setLife(0);
	}
	
	//BOOKMARK: NAVIGATION VARS
	
	private static final float FULL_IMPULSE_SPEED = 3000;
	private static final float BOOST_SPEED = 5000;
	
	private float IMPULSE_MOVE_SPEED_VAR = 0;
	private static final float TURN_SPEED = 180;
	private static final float VERT_POWER = 60;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	
	private GUITexture turrethotbar;
	
	//BOOKMARK: OPS VARS
	
	private static final int MAX_ENERGY = 1000000;
	private int energy = MAX_ENERGY;
	
	private float energyCounter = 0;
	
			
	//BOOKMARK: MISCELLANIOUS VARS
	
	private AudioSrc asrc = new AudioSrc();
	
	private int photonsnd = AudioEngine.loadSound("res/photon_torpedo.wav");
	private int quantumsnd = AudioEngine.loadSound("res/quantum_torpedo.wav");
	
	
	RawModel prephaser = OBJParser.loadObjModel("bolt");
	TexturedModel privatePhaserTexture = new TexturedModel(prephaser, new ModelTexture(Loader.loadTexture("orange")));
	
	RawModel pretorpedo = OBJParser.loadObjModel("photon");
	TexturedModel privateTorpedoTexture = new TexturedModel(pretorpedo, new ModelTexture(Loader.loadTexture("photon")));
	TexturedModel privateSpecialTorpedoTexture = new TexturedModel(pretorpedo, new ModelTexture(Loader.loadTexture("quantum")));
	
	public PlayerWarshipVoyager(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, List<GUITexture> guin) {
		super(model, position, rotX, rotY, rotZ, scale, guin);
		
		privatePhaserTexture.getTexture().setUseFakeLighting(true);
		privatePhaserTexture.getTexture().setSpecularMap(Loader.loadTexture("allGlow"));
		privatePhaserTexture.getTexture().setBrightDamper(4);
		
		privatePhaserTexture.getTexture().setUseFakeLighting(true);
		privateTorpedoTexture.getTexture().setSpecularMap(Loader.loadTexture("allGlow"));
		privateTorpedoTexture.getTexture().setBrightDamper(1);
		privateSpecialTorpedoTexture.getTexture().setUseFakeLighting(true);
		privateSpecialTorpedoTexture.getTexture().setSpecularMap(Loader.loadTexture("allGlow"));
		privateSpecialTorpedoTexture.getTexture().setBrightDamper(1);
		
		this.guis = guin;
		initGUIS();
	}
	
	//UI STUFF*****************************************
	
	private void initGUIS() {
		
		healthText = new GUIText("Loading...", 1.7f, TaskManager.font, new Vector2f(0.64f, 0.20f), 1, false);
		healthText.setColour(1, 0, 0);
		
		shieldsText = new GUIText("Loading...", 1.7f, TaskManager.font, new Vector2f(0.64f, 0.25f), 1, false);
		shieldsText.setColour(0, 1, 0.75f);
		
		energyText = new GUIText("Loading...", 1.7f, TaskManager.font, new Vector2f(0.64f, 0.15f), 1, false);
		energyText.setColour(1, 0.75f, 0);
		
		coordsX.setColour(0, 1, 0);
		coordsY.setColour(0, 1, 0);
		coordsZ.setColour(0, 1, 0);
		
		//GUITexture gui = new GUITexture(this.loader.loadTexture("orange"), new Vector2f(0.f, 0.5f), new Vector2f(0.05f, 0.5f));
		//guis.add(gui);
		gui_panel = new GUITexture(Loader.loadTexture("LCARSpanel"), new Vector2f(0.65f, -0.3f), new Vector2f(0.35f, 0.7f));
		guis.add(gui_panel);
		
		schematic = new GUITexture(Loader.loadTexture("schematic1"), new Vector2f(0.4045f, -0.3f), new Vector2f(0.233f, 0.466f));
		tacticalElements.add(schematic);
		
		slider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 0.25f, "1/4", 1f, 0.04f
				, 1, 1, 0);
		
		slider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 0.5f, "1/2", 1f, 0.04f
				, 1, 1, 0);
		
		slider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 0.75f, "3/4", 1f, 0.04f
				, 1, 1, 0);
		
		slider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 1, "full", 1f, 0.04f
				, 1, 1, 0);
		
		//GUITexture bh0 = new GUITexture(Loader.loadTexture("hb0"), new Vector2f(0.65f, -0.35f), new Vector2f(0.04f / 1.68f, 0.04f));
		//guis.add(bh0);
		
		float x = hotbarx;
		
		for (int i = 0; i <= TURRET_MAX; i++) {
			String path = "hb" + i;
			GUITexture o = new GUITexture(Loader.loadTexture(path), new Vector2f(x, hotbarheight), new Vector2f(0.04f / 1.68f, 0.04f));
			tacticalElements.add(o);
			x += distBetweenSlots;
		}
		
		turrethotbar = new GUITexture(Loader.loadTexture("hotbarptr"), new Vector2f(hotbarx, hotbarheight), new Vector2f(0.04f / 1.68f, 0.04f));
		tacticalElements.add(turrethotbar);
		
		/*phaserbutton = new SFAbstractButton("phaserbutton", new Vector2f(0.4f, 0), new Vector2f(0.04f / 1.68f, 0.04f)) {
			
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
		};*/
		
		for (IGUI el : miscElements) {
			el.show(guis);
		}
		
		for (IGUI el : tacticalElements) {
			el.show(guis);
		}
		
	}
	
	private void updateGUIS() {
		
		healthText.setText(Float.toString(this.HEALTH));
		
		shieldsText.setText(Integer.toString((int) (this.SHIELD / FULL_SHIELDS * 100)) + '%');
		
		energyText.setText(Integer.toString(this.energy));
		
		coordsX.setText(Float.toString(super.getPosition().x));
		coordsY.setText(Float.toString(super.getPosition().y));
		coordsZ.setText(Float.toString(super.getPosition().z));
		
		for (IGUI el : miscElements) {
			el.update();
		}
		
		switch (currentPanel) {
		
			case TACTICAL_PANEL:
				for (IGUI el : tacticalElements) {
					el.update();
				}
				break;
				
			case HELM_PANEL:
				for (IGUI el : helmElements) {
					el.update();
				}
				break;
		}
		
		IMPULSE_MOVE_SPEED_VAR = FULL_IMPULSE_SPEED * slider.getSliderValue();

	}
	
	//UI STUFF*****************************************
	
	@Override
	public void update(RaysCast caster) {
		
		if (this.target != null && this.target.isDead()) {
			this.setTarget(null);
			this.dropRetical();
		}
		
		asrc.setPosition(getPosition().x, getPosition().y, getPosition().z);
		
		if (energy < 0) {
			energy = 0;
		}
		else if (energy > MAX_ENERGY) {
			energy = MAX_ENERGY;
		}
		
		//this.getBoundingBox().printSpecs("TEST");
		move();
		fireAllWeapons(caster);
		updateGUIS();
		
		energyCounter += DisplayManager.getFrameTime();
		
		if (energyCounter > 1 && energy < MAX_ENERGY) {
			if (energy < MAX_ENERGY * 0.9) {
				if (energy > MAX_ENERGY * 0.5) {
					energy += 5;
				}
				else if (energy > MAX_ENERGY * 0.3) {
					energy += 10;
				}
				else if (energy > MAX_ENERGY * 0.2f) {
					energy += 15;
				}
				else {
					energy += 35;
				}
			}
			else {
				energy++;
			}
			energyCounter = 0;
		}
		
		//System.out.println(Keyboard.getKeyName(Keyboard.getEventKey()));
		
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
		
		//Key ALT
		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
			int wheel = Mouse.getDWheel();
			if (wheel < 0) {
				turretFunc--;
			}
			else if (wheel > 0) {
				turretFunc++;
			}
			
			if (turretFunc < TURRET_MIN) {
				turretFunc = TURRET_MAX;
			}
			else if (turretFunc > TURRET_MAX) {
				turretFunc = TURRET_MIN;
			}  
			
			turrethotbar.setPosition(new Vector2f(hotbarx + (turretFunc * distBetweenSlots), hotbarheight));
			
			System.out.println(turretFunc);
			
		}
		
		/*while (Keyboard.next()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				
			}
		}*/
	
	}
	
	private void fireFrontPhasers() {
		
		Vector3f playerPos = (super.getPosition());
		
		projectiles.add(new Bolt(privatePhaserTexture, new Vector3f((float) (playerPos.x + (Math.sin
				(Math.toRadians(super.getRotY() - 90)) / 1.35) + Math.sin
				(Math.toRadians(super.getRotY())) * 25), playerPos.y + 20.9f, (float) (playerPos.z + (Math.cos
						(Math.toRadians(super.getRotY() - 90)) / 1.35) + Math.cos
						(Math.toRadians(super.getRotY())) * 25)), super.getRotX(), super.getRotY(), 0, 1.5f, 1.5f, 8, 5, this.currentSpeed));
		
		projectiles.add(new Bolt(privatePhaserTexture, new Vector3f((float) (playerPos.x + (Math.sin(Math.toRadians(super.getRotY() + 90)) * 4) + Math.sin
				(Math.toRadians(super.getRotY())) * 12), playerPos.y + 22.35f, (float) (playerPos.z + (Math.cos
						(Math.toRadians(super.getRotY() + 90)) * 4) + Math.cos
						(Math.toRadians(super.getRotY())) * 12) ), super.getRotX(), super.getRotY(), 0, 1.5f, 1.5f, 8, 5, this.currentSpeed));
		
		projectiles.add(new Bolt(privatePhaserTexture, new Vector3f((float) (playerPos.x + (Math.sin(Math.toRadians(super.getRotY() - 90)) * 5) + Math.sin
				(Math.toRadians(super.getRotY())) * 12), playerPos.y + 22.35f, (float) (playerPos.z + (Math.cos
						(Math.toRadians(super.getRotY() - 90)) * 5) + Math.cos
						(Math.toRadians(super.getRotY())) * 12) ), super.getRotX(), super.getRotY(), 0, 1.5f, 1.5f, 8, 5, this.currentSpeed));
	
		energy -= 3;
	
	}
	
	private void fireSecondaryForwardPhotons() {
		
		projectiles.add(Torpedo.photonTorpedo(super.getPosition(), this.currentSpeed + 4500,
				-5, 11, 11, super.getRotY(), super.getRotX()));
		
		projectiles.add(Torpedo.photonTorpedo(super.getPosition(), this.currentSpeed + 4500,
				5, 11, 11, super.getRotY(), super.getRotX()));
		
		energy -= 10;
		
	}
	
	private void fireSecondaryForwardQuantums() {
		
		projectiles.add(Torpedo.quantumTorpedo(super.getPosition(), this.currentSpeed + 4500,
				-5, 11, 11, super.getRotY(), super.getRotX()));
		
		projectiles.add(Torpedo.quantumTorpedo(super.getPosition(), this.currentSpeed + 4500,
				5, 11, 11, super.getRotY(), super.getRotX()));
		
		energy -= 15;
				
	}
	
	private void firePhaserSpray() {
		
		projectiles.add(new Bolt(privatePhaserTexture, new Vector3f(super.getPosition().x, super.getPosition().y + 10, super.getPosition().z), 
				super.getRotX() + rng.nextFloat() * 2 - 1, super.getRotY() + rng.nextFloat() * 2 - 1, super.getRotZ(),
				1.5f, 1.5f, 20, 3, mx, my, mz, false));
		
		energy -= 1;
		
	}
	
	private void fireDorsalPortArrays() {
		
		projectiles.add(new Bolt(privatePhaserTexture, 
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
		Vector3f target = new Vector3f(caster.getPointOnRay(ray, 200));
			
		if (Keyboard.isKeyDown(Keyboard.KEY_P))
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
		
		if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
			fireDorsalPortArrays();
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_Z) && !flag2) {
			flag2 = true;
			vec = SFMath.rotateToFaceVector(super.getPosition(), this.target != null ? this.target.getPosition() : new Vector3f(0, 0, 0));
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
		
		if (this.target != null) {
			fireAutoGun();
		}
		
		for (int i = 0; i < this.projectiles.size(); i++) {
			
			Projectile el = projectiles.get(i);
			
			if (el.isDead()) {
				projectiles.remove(i);
			}
			else {
				el.update();
			}
			
		}
		
	}
	
	@Override
	@Deprecated
	public void respondToCollision() {
		
	}
	
	public void setHealth(float argf) {
		this.HEALTH = argf;
	}
	
	public float getHealth() { 
		return HEALTH;
	}
	
	public void fireAutoGun() {
		
		autogunTimer += DisplayManager.getFrameTime();
		
		switch (autoFunc) {
		
		case STD_AUTO:
			if (autogunTimer > 0.5f) {
				
				this.projectiles.add(new HomingTorpedo(privateTorpedoTexture,
						new Vector3f(super.getPosition()),
						2, 2, 5, Torpedo.PT * 2, 3000, 10, target, 0, 5, 0));
				
				energy -= 20;
				autogunTimer = 0;
			}
			
		case DEADWEIGHT:
			if (autogunTimer > 2) {
				
				this.projectiles.add(new HomingTorpedo(privateTorpedoTexture,
						new Vector3f(super.getPosition()),
						2, 2, 5, Torpedo.PT * 2, 3000, 10, target, 0, 50, 0));
				
				energy -= 25;
				autogunTimer = 0;
			}
		
		}
		
	}
	
	public void fireTurret(float dx, float dy, float dz) {
		
		dx *= 10; dy *= 10; dz *= 10;
		
		switch (turretFunc) {
		
		case ONE_TORP:
			projectiles.add(Torpedo.photonTorpedo(super.getPosition(), dx, dy, dz));
			break;
			
		case 1:
			projectiles.add(new Torpedo(privateTorpedoTexture, 
					new Vector3f(super.getPosition()),
					0, 0, 0, 2, 2, 5, Torpedo.PT, dx, dy, dz));
			
			projectiles.add(new Torpedo(privateTorpedoTexture, 
					new Vector3f(super.getPosition().x, super.getPosition().y + 10, super.getPosition().z),
					0, 0, 0, 2, 2, 5, Torpedo.PT, dx, dy, dz));
			break;
		
		}
		
	}
	
	public void fireTurret(Vector3f target) {
		
		Vector3f vec = SFMath.moveToVector(target, super.getPosition(), 3500);
		
		turretTimer += DisplayManager.getFrameTime();
		
		switch (turretFunc) {
		
		case ONE_TORP:
			if (turretTimer > 0.5f || turretToggle) {
				projectiles.add(Torpedo.photonTorpedo(super.getPosition(), vec.x, vec.y, vec.z));
				
				turretTimer = 0;
				turretToggle = false;
				energy -= 10;
			}
			break;
			
		case TWO_SHOT:
			if (turretTimer > 0.5f || turretToggle) {
				 
				projectiles.add(Torpedo.photonTorpedo(super.getPosition(), vec.x, vec.y, vec.z));
				
				projectiles.add(Torpedo.photonTorpedo(new Vector3f(super.getPosition().x,
						super.getPosition().y + 10, super.getPosition().z)
						, vec.x, vec.y, vec.z));
				
				turretTimer = 0;
				turretToggle = false;
				energy -= 25;
			}
			break;
			
		case PHASER_QUANTUM:
			Vector3f rotations = SFMath.rotateToFaceVector(super.getPosition(), target);
			projectiles.add(new Bolt(privatePhaserTexture, new Vector3f(super.getPosition().x, super.getPosition().y + 10, super.getPosition().z)
					, -rotations.x, rotations.y, rotations.z, 1.5f, 1.5f, 10, 100, 0));
			energy--;
			
			if (turretTimer > 0.5f || turretToggle) {
				
				projectiles.add(Torpedo.quantumTorpedo(super.getPosition(), vec.x, vec.y, vec.z));
				
				turretTimer = 0;
				turretToggle = false;
				energy -= 15;
			}
			break;
			
		default:
			break;
			//throw new IllegalArgumentException("Ur Turret thing is broken");
		
		}
		
	}

	@Override
	public void respondToCollisioni(float damage) {
		if (this.SHIELD > 0) {
			if (this.SHIELD - damage > 0) {
				this.SHIELD -= damage;
				TaskManager.blueShieldSystem.generateParticles(super.getPosition(), super.getPosition());
				TaskManager.blueShieldSystem.generateParticles(super.getPosition(), super.getPosition());
			}
			else {
				this.SHIELD = 0;
				TaskManager.blueShieldSystem.generateParticles(super.getPosition(), super.getPosition());
				TaskManager.blueShieldSystem.generateParticles(super.getPosition(), super.getPosition());
			}
		}
		else {
			this.HEALTH -= damage;
			if (this.HEALTH <= 0) {
				System.exit(0);
			}
		}
		
	}
	
	public List<Projectile> getProjectiles() {
		return projectiles;
	}
	
	@Override
	public void choreCollisions(List<Enemy> enemies, RaysCast caster) {
		 
		boolean virg = true;
		
		for (Enemy enemy : enemies) {
			
			BoundingBox bb1 = enemy.getBoundingBox(); 
			
			if (caster.penetrates(bb1)) {
				virg = false;
				if (Mouse.isButtonDown(0)) {
					while (Mouse.next()) {
						if (Mouse.getEventButtonState()) {
							if (Mouse.getEventButton() == 0) {
								
								if (enemy == this.getTarget()) {
									this.setTarget(null);
									this.dropRetical();
								}
								else {
									this.setTarget(enemy);
									this.dropRetical();
									this.setRetical();
								}
								
							}
						}
					
					}
					
				}
				
				if (Mouse.isButtonDown(1)) {
					fireTurret(enemy.getPosition());
				}
				else {
					turretTimer = 0;
					turretToggle = true;
				}
				
			}
			
			for (Entity projectile : this.getProjectiles()) {
				
				BoundingBox bb2 = projectile.getBoundingBox();
				
				if (bb1.intersects(bb2)) {
					projectile.respondToCollision();
					((BorgVessel) enemy).respondToCollisioni(((Projectile) projectile).getDamage());
				}
				
			}
			
		}
		
		if (virg) {
			while (Mouse.next()) {
				if (Mouse.getEventButtonState()) {
					if (Mouse.getEventButton() == 1) {
						this.fireTurret(caster.getCurrentRay().x,
						caster.getCurrentRay().y, caster.getCurrentRay().z);
					}
				}
			}
			
		}
		
	}
	
}
