package scene.entities.players.voyager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import audio.AudioEngine;
import audio.AudioSrc;
import box.Main;
import box.TM;
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
import scene.entities.entityUtils.ModelSys;
import scene.entities.entityUtils.StatusText;
import scene.entities.hostiles.Enemy;
import scene.entities.players.Player;
import scene.entities.projectiles.Bolt;
import scene.entities.projectiles.HomingTorpedo;
import scene.entities.projectiles.Projectile;
import scene.entities.projectiles.Torpedo;
import scene.particles.Particle;
import scene.particles.ParticleTexture;
import utils.RaysCast;
import utils.SFMath;

public class PlayerWarshipVoyager extends Player {
	
	static Vector2f panelpos = new Vector2f(0.65f, -0.3f);
	static Vector2f schmpos = Vector2f.add(panelpos, new Vector2f(-0.2455f, 0), null);
	//schmpos = [0.4045, -0.3f]
	
	private GUITexture gui_panel = new GUITexture(Loader.loadTexture("LCARSpanel"), panelpos, new Vector2f(0.35f, 0.7f));
	private GUITexture schematic = new GUITexture(Loader.loadTexture("schematic1"), schmpos, new Vector2f(0.233f, 0.466f));
	
	//package visibility
	List<IGUI> tacticalElements = new ArrayList<IGUI>();
	List<IGUI> opsElements = new ArrayList<IGUI>();
	List<IGUI> helmElements = new ArrayList<IGUI>();
	List<IGUI> miscElements = new ArrayList<IGUI>();
	
	private static final String tag = "HMCS Voyager";
	private GUIText trmText = new GUIText(tag, 1, TM.font, new Vector2f(0.75f, 0.3f), 0.25f, true);
	
	private boolean shieldWarning = false;
	
	private SFAbstractButton viewScreenViewAft;
	//private SFAbstractButton viewScreenViewMap;
	
	VoyagerGUISys pacman = new VoyagerGUISys(this);

	private int a1 = Loader.loadTexture("voyphaserdiag2");
	private int b1 = Loader.loadTexture("voyphaserdiagactive");
	//BOOKMARK fire turret to target
	private SFAbstractButton fireturret = new SFAbstractButton(tacticalElements, "voyphaserdiag2", new Vector2f(hotbarx, hotbarheight + 0.075f), TM.sqr4) {
		
		@Override
		public void whileHovering(IButton button) {
			if (target == null)
				this.getTexture().setTexture(a1);
		}
		
		@Override
		public void whileHolding(IButton button) {
			if (target != null)
				fireTurret(target.getPosition());
		}
		
		@Override
		public void onStopHover(IButton button) {
			this.getTexture().setTexture(a1);
		}
		
		@Override
		public void onStartHover(IButton button) {
			if (target != null)
				this.getTexture().setTexture(b1);
		}
		
		@Override
		public void onClick(IButton button) {
			if (target != null)
				fireTurret(target.getPosition());
		}
	};
	
	//BOOKMARK switch LCARS to tactical
	private SFAbstractButton changebuttontactical = new SFAbstractButton(miscElements, "tacticalicon", new Vector2f(0.324f, 0.36f), TM.sqr4) {
		
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
	private SFAbstractButton changebuttonhelm = new SFAbstractButton(miscElements, "helmicon", new Vector2f(0.394f, 0.36f), TM.sqr4) {
		
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
			currentPanel = HELM_PANEL;
			for (IGUI el : tacticalElements) {
				el.hide(guis);
			}
			for (IGUI el : helmElements) {
				el.show(guis);
			}
		}
	};
	
	//BOOKMARK change viewscreen to minimap
	private SFAbstractButton viewscreen_minimap = new SFAbstractButton(miscElements, "image", new Vector2f(0.324f, 0.96f), TM.sqr4) {
		
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
			Main.screenFBOMode(Main.MAP);
		}
	};
	
	//BOOKMARK change viewscreen to aft
	private SFAbstractButton viewscreen_aft = new SFAbstractButton(miscElements, "image", new Vector2f(0.374f, 0.96f), TM.sqr4) {
			
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
			Main.screenFBOMode(Main.AFT);
		}
	};
	
	//BOOKMARK transfer button energy to shields
	private SFAbstractButton energyshields = new SFAbstractButton(miscElements, "clear", new Vector2f(0.35f, 0.45f), new Vector2f(0.08f, 0.04f)) {
		
		@Override
		public void whileHovering(IButton button) {
			
		}
		
		@Override
		public void whileHolding(IButton button) {
			if (shieldsOn && SHIELD + 100 <= FULL_SHIELDS) {
				ENERGY -= 500;
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
	private SFAbstractButton autogunStd = new SFAbstractButton(tacticalElements, "image", new Vector2f(0.6f, -0.75f), TM.sqr4) {
		
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
	private SFAbstractButton autogunDeadWeight = new SFAbstractButton(tacticalElements, "image", new Vector2f(0.65f, -0.75f), TM.sqr4) {
		
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
	private SFVerticalSlider impulseslider = new SFVerticalSlider(helmElements, 0.48f, 0.01f, 0, new Vector2f(0.65f, -0.5f), TM.sqr8, "knob", "ramp") {
		
		@Override
		public void sliderStopHover(ISlider slider) {
			
		}
		
		@Override
		public void sliderStartHover(ISlider slider) {
			
		}
	};
	
	//BOOKMARK impulse speed slider
	private SFVerticalSlider warpslider = new SFVerticalSlider(helmElements, 0.48f, 0.01f, 0, new Vector2f(0.81f, -0.5f), TM.sqr8, "knob", "ramp") {
		
		@Override
		public void sliderStopHover(ISlider slider) {
				
		}
			
		@Override
		public void sliderStartHover(ISlider slider) {
				
		}
	};
	
	//BOOKMARK port phaser angle slider
	private SFVerticalSlider portslider = new SFVerticalSlider(tacticalElements, 0.12f, -0.01f, 0, new Vector2f(0.325f, -0.345f), TM.sqr4, "knob", "tramp") {
		
		@Override
		public void sliderStopHover(ISlider slider) {
			
		}
		
		@Override
		public void sliderStartHover(ISlider slider) {
			
		}
	};
	
	//BOOKMARK starboard phaser angle slider
	private SFVerticalSlider starslider = new SFVerticalSlider(tacticalElements, 0.12f, -0.01f, 0, new Vector2f(0.48f, -0.345f), TM.sqr4, "knob", "tramp") {
		
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
	
	private List<GUIText> abilityTexts = new ArrayList<GUIText>();
	private float listpos = 0;
	
	private void addAbility(SFAbstractButton button, String msg) {
		button.getTexture().setPosition(new Vector2f(0.8f, listpos));
		button.getTexture().setScale(new Vector2f(0.075f, 0.065f / TM.GUI_SCALE_DIV));
		GUIText o = new GUIText(msg, 1, TM.font, TM.coordtext(
				
				0.8f - 0.07f,
				listpos + (0.08f / TM.GUI_SCALE_DIV / 2) + 0.01f)
				
				, 0.5f, false);
		
		o.setColour(0, 65f / 255f, 171f / 255f);
		
		listpos -= 0.1f;
	}
	
	private SFAbstractButton abilityStinger = new SFAbstractButton(tacticalElements, "agray", new Vector2f(0.75f, -0.1f), TM.sqr4) {
		
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
					, 2.5f, 2.5f, 6.25f, 1500, 10000 + (this.currentSpeed < 0 ? 0 : this.currentSpeed), 15, 
					this.target,
					(TM.rng.nextFloat() * 2 - 1),
					(TM.rng.nextFloat() * 2 - 1),
					(TM.rng.nextFloat() * 2 - 1),
					new ParticleTexture(Loader.loadTexture("particleAtlas"), 4), 1 , 40, new Runnable() {
						
						@Override
						public void run() {
							stingersrc.play(AudioEngine.loadSound("explosion"), 10000);
						}
					}));
			
			stingersrc.play(AudioEngine.loadSound("missile_launch"), 100);
		}
	}
	
	private SFAbstractButton abilityHydraRockets = new SFAbstractButton(tacticalElements, "image", new Vector2f(0.85f, -0.1f), TM.sqr4) {
		
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
				, 2.5f, 2.5f, 6.25f, Torpedo.PT * 1.5f, 5000 + (this.currentSpeed < 0 ? 0 : this.currentSpeed), 15, 
				target,
				(TM.rng.nextFloat() * 2 - 1) * 10,
				(TM.rng.nextFloat() * 2 - 1) * 10,
				(TM.rng.nextFloat() * 2 - 1) * 10,
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
	
	private int a6 = Loader.loadTexture("grabmouse");
	private int b6 = Loader.loadTexture("grabmousefilled");
	//BOOKMARK grab mouse
	private SFAbstractButton grabmouse = new SFAbstractButton(helmElements, "grabmouse", new Vector2f(0.45f, -0.9f), TM.sqrgui(0.06f)) {
		
		@Override
		public void whileHovering(IButton button) {
			
		}
		
		@Override
		public void whileHolding(IButton button) {
			
		}
		
		@Override
		public void onStopHover(IButton button) {
			this.getTexture().setTexture(a6);
		}
		
		@Override
		public void onStartHover(IButton button) {
			this.getTexture().setTexture(b6);
		}
		
		@Override
		public void onClick(IButton button) {
			Mouse.setGrabbed(true);
			camera.setAngleAround(0);
			camera.setPitch(20);
			
			for (IGUI el : miscElements) {
				el.hide(guis);
			}
			
			switch (currentPanel) {
			
			case TACTICAL_PANEL:
				for (IGUI el : tacticalElements) {
					el.hide(guis);
				}
				break;
				
			case HELM_PANEL:
				for (IGUI el : helmElements) {
					el.hide(guis);
				}
				break;
			}
			
			gui_panel.setTexture(Loader.loadTexture("clear"));
			//trmText.hide();
			
		}
	};
	
	private int a7 = Loader.loadTexture("warpicon");
	private int b7 = Loader.loadTexture("warpiconfilled");
	//BOOKMARK move at warp
	private boolean warpFlag = false;
	private SFAbstractButton warpbutton = new SFAbstractButton(helmElements, "warpicon", new Vector2f(0.35f, -0.9f), TM.sqr8) {
		
		@Override
		public void whileHovering(IButton button) {
			
		}
		
		@Override
		public void whileHolding(IButton button) {
			warpFlag = true;
		}
		
		@Override
		public void onStopHover(IButton button) {
			this.getTexture().setTexture(a7);
		}
		
		@Override
		public void onStartHover(IButton button) {
			this.getTexture().setTexture(b7);
		}
		
		@Override
		public void onClick(IButton button) {
			
		}
	};
	
	//BOOKMARK maneuvaring 'joystick'
	private boolean grabStick = false;
	private SFAbstractButton joystick = new SFAbstractButton(helmElements, "cntrl", colPos, TM.sqr4, 0.1f, 0.1f) {
		
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
			grabStick = true;
		}
	};
	
	//BOOKMARK break target
	private SFAbstractButton breaktarget = new SFAbstractButton(tacticalElements, "break", schmpos, new Vector2f(0.13f, 0.525f), TM.sqr4) {
		
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
			setTarget(null);
			dropRetical();
			trmText.setText(tag);
			trmText.setColour(0, 0, 1);
		}
	};
	
	private GUIText rottext = new GUIText("", 1, TM.font, new Vector2f(0, 0), 0.25f, false);
	
	private SFAbstractButton temp = new SFAbstractButton(helmElements, "image", new Vector2f(0.5f, -0.5f), TM.sqr4) {
		
		@Override
		public void whileHovering(IButton button) {
			UPWARDS_ROT_CAP += Math.signum(Mouse.getDWheel());
			UPWARDS_ROT_CAP = (UPWARDS_ROT_CAP < 15 ? 15 : UPWARDS_ROT_CAP);
			UPWARDS_ROT_CAP = (UPWARDS_ROT_CAP > 90 ? 90 : UPWARDS_ROT_CAP);
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
			
		}
	};
	
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
	
	Random rng = new Random();
	
	//BOOKMARK: TACTICAL VARS
	
	private static final float MAX_HEALTH = 12500;
	private static final float FULL_SHIELDS = 100000;
	
	private float turretTimer = 0;
	private boolean turretToggle = true;
	
	private int turretFunc = 0;
	
	private static final int TURRET_MIN = 0;
	private static final int TURRET_MAX = 2; 
	
	private static final int ONE_TORP = 0;
	private static final int TWO_SHOT = 1;
	private static final int PHASER_QUANTUM = 2;
	
	private int autoFunc = 2;
	
	private static final int AUTO_MIN = 0;
	private static final int AUTO_MAX = 3;
	
	private static final int STD_AUTO = 0;
	private static final int DEADWEIGHT = 1;
	private static final int GATLING = 2;
	private static final int TORPEDO_SPLIT = 3;
	
	private int shotsFired = 0;
	
	private static final float distBetweenSlots = 0.05f;
	private static final float hotbarheight = -0.9f;
	private static final float hotbarx = 0.35f;
	

	private float autogunTimer = 0;
	
	private Particle retical;
	
	@Override
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
	
	public void setPreRetical(Vector3f pos) {
		this.retical = new Particle(new ParticleTexture(Loader.loadTexture("reticalpre"), 1), pos,
				new Vector3f(0, 0, 0), 0, Float.POSITIVE_INFINITY, 45, 300, true);
	}
	
	public void dropRetical() {
		if (this.retical != null)
			this.retical.setLife(0);
		this.retical = null;
	}
	
	private float leftPhaserTimer = 0;
	private float centerPhaserTimer = 0;
	private float rightPhaserTimer = 0;
	
	private float mainPhaserTimer = 0;
	
	//BOOKMARK: NAVIGATION VARS
	
	private static final Vector2f colPos = new Vector2f(0, -0.1f); 
	
	private static final float MAX_WARP_FACTOR = 9.975f;
	
	private static final float FULL_IMPULSE_SPEED = 3000;
	private static final float BOOST_SPEED = 5000;
	
	private static final float WARP_SPEED = 214000;
	
	private float IMPULSE_MOVE_SPEED_VAR = 0;
	private float WARP_SPEED_VAR = 0;
	private static final float TURN_SPEED = 180;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	
	private float UPWARDS_ROT_CAP = 30;
	
	private GUITexture turrethotbar;
	
	private boolean flagUp = false;
	private boolean flagDown = false;
	private boolean flagLeft = false;
	private boolean flagRight = false;
	
	//BOOKMARK: OPS VARS
	
	private static final int MAX_ENERGY = 1000000;
	
	private float energyCounter = 0;
	
			
	//BOOKMARK: MISCELLANIOUS VARS
	
	private AudioSrc asrc = new AudioSrc();
	private AudioSrc stingersrc = new AudioSrc();

	TexturedModel privatePhaserTexture = TM.phaserBolt;
	
	RawModel pretorpedo = OBJParser.loadObjModel("photon");
	TexturedModel privateTorpedoTexture = new TexturedModel(pretorpedo, new ModelTexture(Loader.loadTexture("photon")));
	TexturedModel privateSpecialTorpedoTexture = new TexturedModel(pretorpedo, new ModelTexture(Loader.loadTexture("quantum")));
	
	public PlayerWarshipVoyager(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, List<GUITexture> guin) {
		super(model, position, rotX, rotY, rotZ, scale, guin);
		
		privatePhaserTexture.getTexture().setUseFakeLighting(true);
		privateTorpedoTexture.getTexture().setSpecularMap(Loader.loadTexture("allGlow"));
		privateTorpedoTexture.getTexture().setBrightDamper(1);
		privateSpecialTorpedoTexture.getTexture().setUseFakeLighting(true);
		privateSpecialTorpedoTexture.getTexture().setSpecularMap(Loader.loadTexture("allGlow"));
		privateSpecialTorpedoTexture.getTexture().setBrightDamper(1);
		
		this.guis = guin;
		initGUIS();
		initsuper(MAX_HEALTH, FULL_SHIELDS, MAX_ENERGY);
	}
	
	@Override
	protected void initWeapons() {

		weapons.put("center_front_phaser", new Runnable() {
			
			@Override
			public void run() {
				fire_center_front_phaser();
			}
		});
		
	}
	
	//UI STUFF*****************************************
	
	private void initGUIS() {
		
		rottext.setPosition(TM.coordtext(temp.getTexture().getPosition()));
		rottext.getPosition().x -= 0.01f;
		rottext.getPosition().y -= 0.05f;
		rottext.setColour(0, 0, 1);
		
		trmText.setColour(0, 0, 1);
		
		impulseslider.setCounter(1, TM.font, 0, 0, 1);
		warpslider.setCounter(1, TM.font, 0, 0, 1, new Callable<String>() {
			
			@Override
			public String call() throws Exception {
				return "Warp " + TM.df.format((warpslider.getSliderValue() * MAX_WARP_FACTOR));
			}
		});
		
		//GUITexture gui = new GUITexture(this.loader.loadTexture("orange"), new Vector2f(0.f, 0.5f), new Vector2f(0.05f, 0.5f));
		//guis.add(gui);
		//gui_panel = new GUITexture(Loader.loadTexture("LCARSpanel"), panelpos, new Vector2f(0.35f, 0.7f));
		guis.add(gui_panel);
		tacticalElements.add(schematic);
				
		impulseslider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 0.25f, "1/4", 1f, 0.04f
				, 0, 65f / 255f, 171f / 255f);
		
		impulseslider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 0.5f, "1/2", 1f, 0.04f
				, 0, 1, 1);
		
		impulseslider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 0.75f, "3/4", 1f, 0.04f
				, 1, 1, 0);
		
		impulseslider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 1, "full", 1f, 0.04f
				, 1, 1, 0);
		
		
		
		warpslider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 1f / MAX_WARP_FACTOR, "Warp 1", 1f, 0.04f
				, 1, 1, 0);
		
		warpslider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 2f / MAX_WARP_FACTOR, "Warp 2", 1f, 0.04f
				, 1, 1, 0);
		
		warpslider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 3f / MAX_WARP_FACTOR, "Warp 3", 1f, 0.04f
				, 1, 1, 0);
		
		warpslider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 4f / MAX_WARP_FACTOR, "Warp 4", 1f, 0.04f
				, 1, 1, 0);
		
		warpslider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 5f / MAX_WARP_FACTOR, "Warp 5", 1f, 0.04f
				, 1, 1, 0);
		
		warpslider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 6f / MAX_WARP_FACTOR, "Warp 6", 1f, 0.04f
				, 1, 1, 0);
		
		warpslider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 7f / MAX_WARP_FACTOR, "Warp 7", 1f, 0.04f
				, 1, 1, 0);
		
		warpslider.addMark(guis, "mk", new Vector2f(0.08f / 1.68f, 0.08f), 0.05f, 8f / MAX_WARP_FACTOR, "Warp 8", 1f, 0.04f
				, 1, 1, 0);
		
		//addAbility(abilityStinger, "Fire Stinger");
		//addAbility(abilityHydraRockets, "");
		
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
		
		energyText.setText(Integer.toString(this.ENERGY));
		
		coordsX.setText(Float.toString(super.getPosition().x));
		coordsY.setText(Float.toString(super.getPosition().y));
		coordsZ.setText(Float.toString(super.getPosition().z));
		
		rottext.setText(Float.toString(UPWARDS_ROT_CAP));
		
		if (shieldWarning && SHIELD > 0) {
			shieldWarning = false;
		}
		
		//trmText.setColour(0, 148f / 255f, 1);
		//trmText.setColour(211f / 255f, 0.3f, 0.2666f);
		//trmText.setColour(119f / 255f, 68f / 255f, 102f / 255f);
		
		if (!Mouse.isGrabbed()) {
			for (IGUI el : miscElements) {
				el.update();
			}
			
			warpFlag = false;
			WARP_SPEED_VAR = 0;
			
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
		}
		
		if (grabStick && Mouse.isButtonDown(0)) {
			Vector2f tobe = DisplayManager.getNormalizedMouseCoords();
			tobe.y = -tobe.y;
			
			if (!(SFMath.distance(tobe, colPos) > 0.35f)) {
				joystick.getTexture().setPosition(tobe);
				//joystick.getTexture().setRotation(-joystick.getTexture().getPosition().x * 100);
				joystick.getTexture().setRotation(-super.getRotZ());
			}
			else {

			}
			
			Vector2f cntrl = Vector2f.sub(colPos, joystick.getTexture().getPosition(), null);
			
			super.rotate(-cntrl.y * 2, cntrl.x * 5, -cntrl.x * 10);
			
			if (super.getRotZ() > 45) {
				super.setRotZ(45);
			}
			else if (super.getRotZ() < -45) {
				super.setRotZ(-45);
			}
			
		}
		else {
			grabStick = false;
			joystick.getTexture().setPosition(colPos);
			joystick.getTexture().setRotation(0);
		}
			
		
		IMPULSE_MOVE_SPEED_VAR = FULL_IMPULSE_SPEED * impulseslider.getSliderValue();
		WARP_SPEED_VAR = (float) Math.pow((warpslider.getSliderValue() * MAX_WARP_FACTOR), 1.25) * WARP_SPEED;

	}
	
	//UI STUFF*****************************************
	
	@Override
	public void update(RaysCast caster) {
		
		prerequisite();
		
		if (this.target != null) {
			
			/*trmText.setText("Targeting Vessel at: "
					+ (int) target.getPosition().x + ", "
					+ (int) target.getPosition().y + ", "
					+ (int) target.getPosition().z );*/
			
			statusQueue.add(new StatusText("Targeting Vessel at: "
					+ (int) target.getPosition().x + ", "
					+ (int) target.getPosition().y + ", "
					+ (int) target.getPosition().z, 2, 
					0, 0, 1));
			
			if (this.target.isDead()) {
				this.setTarget(null);
				this.dropRetical();
			}
			
		}
		
		asrc.setPosition(getPosition().x, getPosition().y, getPosition().z);
		stingersrc.setPosition(getPosition().x, getPosition().y, getPosition().z);
		
		if (ENERGY < 0) {
			ENERGY = 0;
		}
		else if (ENERGY > MAX_ENERGY) {
			ENERGY = MAX_ENERGY;
		}
		
		if (!shieldsOn && SHIELD < FULL_SHIELDS) {
			SHIELD++;
		}
		
		//this.getBoundingBox().printSpecs("TEST");
		move();
		fireAllWeapons(caster);
		updateGUIS();
		
		energyCounter += DisplayManager.getFrameTime();
		
		if (energyCounter > 1 && ENERGY < MAX_ENERGY) {
			if (ENERGY < MAX_ENERGY * 0.9) {
				if (ENERGY > MAX_ENERGY * 0.5) {
					ENERGY += 50;
				}
				else if (ENERGY > MAX_ENERGY * 0.3) {
					ENERGY += 100;
				}
				else if (ENERGY > MAX_ENERGY * 0.2f) {
					ENERGY += 150;
				}
				else {
					ENERGY += 350;
				}
			}
			else {
				ENERGY += 10;;
			}
			energyCounter = 0;
		}
		
		//System.out.println(Keyboard.getKeyName(Keyboard.getEventKey()));
		
		statusQueue.add(new StatusText(tag, 1, 0, 0, 1));
		
		if (shieldWarning) {
			statusQueue.add(new StatusText("Warning: Shields Down", 3, 1, 0, 0));
		}
		
		int highest = -32;
		StatusText current = null;
		
		for (StatusText txt : statusQueue) {
			if (txt.priority > highest) {
				highest = txt.priority;
				current = txt;
			}
		}
		
		trmText.conformToStatusText(current);
		statusQueue.clear();
		
	}
	
	private void move() {
		checkInputs();
		super.rotate(0, currentTurnSpeed * DisplayManager.getFrameTime(), 0);	
		float distanceMoved = currentSpeed * DisplayManager.getFrameTime();
		
		float dy = (float) (distanceMoved * Math.sin(Math.toRadians(super.getRotX())));
		
		float l = (float) Math.cos(Math.toRadians(super.getRotX()));
		
		float dx = (float) (distanceMoved * Math.sin(Math.toRadians(super.getRotY()))) * l;
		//float dy = (float) (distanceMoved * Math.sin(Math.toRadians(super.getRotX())));
		float dz = (float) (distanceMoved * Math.cos(Math.toRadians(super.getRotY()))) * l;
		super.move(dx, -dy, dz);
		
		tracingX = dx;
		tracingY = dy;
		tracingZ = dz;
		distMoved = distanceMoved;
		
		//super.move(0, upwardsSpeed * DisplayManager.getFrameTime(), 0);
		this.setRotY(this.getRotY() % 360);
		if (this.getRotY() < 0) {
			this.setRotY(Math.abs(360 - this.getRotY()));
		}
		if (Mouse.isGrabbed()) {
			float mx = Mouse.getDX();
			this.rotate(-Mouse.getDY() * 0.025f, -mx * 0.1f, Math.signum(mx) * 0.9f);
		}
		
		if (this.target != null && Keyboard.isKeyDown(Keyboard.KEY_X)) {
			/*float mv = this.target.getPosition().y - super.getPosition().y;

			super.move(0, mv * 0.03f, 0);
			
			if (super.getPosition().y < this.target.getPosition().y + 50) {
				flagUp = true;
			}
			else if (super.getPosition().y > this.target.getPosition().y - 50) {
				flagDown = true;
			}
			
			float ty = SFMath.rotateToFaceVector(getPosition(), this.target.getPosition()).y;
			
			super.setRotY(ty + Math.signum(ty));*/
			
			Vector3f vec = SFMath.rotateToFaceVector(getPosition(), this.target.getPosition());
			
			float rot = super.getRotY() - vec.y;
			
			if (Math.abs(rot) > 180) {
				rot += (rot > 0 ? -360 : 360);
			}
			
			vec.x = -vec.x;
			
			float rtx = super.getRotX();
			
			if (Math.abs(rtx - vec.x) < 0.3f) {
				super.setRotX(vec.x);
				//System.out.println("Exception Made");
			}
			else if (rtx > vec.x) {
				flagUp = true;
			}
			else if (rtx < vec.x) {
				flagDown = true;
			}
			
			//System.out.println(rot);
			
			if (rot < -0.3f) {
				flagLeft = true;
			}
			else if (rot > 0.3f) {
				flagRight = true;
			}
			
		}
		
	}
	
	private void checkInputs() {
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W) && !Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			this.currentSpeed = IMPULSE_MOVE_SPEED_VAR;
			this.getModel().getTexture().setBrightDamper(4);
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.getModel().getTexture().setBrightDamper(4);
			this.currentSpeed = -IMPULSE_MOVE_SPEED_VAR;
		}
		
		else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_W)) {
			//this.currentSpeed = IMPULSE_MOVE_SPEED_VAR * 30 * 9.975f * 42;
			//TaskManager.warpParticleSystem.generateParticles(new Vector3f(super.getPosition()));
			this.currentSpeed = BOOST_SPEED;
			this.getModel().getTexture().setBrightDamper(1);
		}
		else {
			if (warpFlag) {
				this.currentSpeed = WARP_SPEED_VAR;
			}
			else {
				this.currentSpeed = 0;
			}
			this.getModel().getTexture().setBrightDamper(4);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A) || flagLeft) { 
			flagLeft = false;
			this.currentTurnSpeed = TURN_SPEED;
			if (this.getRotZ() > -45)
				super.rotate(0, 0, -60 * DisplayManager.getFrameTime());
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_D) || flagRight) { 
			flagRight = false;
			this.currentTurnSpeed = -TURN_SPEED;
			if (this.getRotZ() < 45)
				super.rotate(0, 0, 60 * DisplayManager.getFrameTime());
		}
		else {											
			this.currentTurnSpeed = 0; 		
			if (this.getRotZ() < 0) {
				super.rotate(0, 0, 70 * DisplayManager.getFrameTime());
				
				if (this.getRotZ() > 0)
					super.setRotZ(0);
			}
			else if (this.getRotZ() > 0) {
				super.rotate(0, 0, -70 * DisplayManager.getFrameTime());
				
				if (this.getRotZ() < 0)
					super.setRotZ(0);
			}
					
		}
		
		//TODO: Rotate according to vertical speed
		
		if (!Mouse.isGrabbed()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) || flagUp) {
				
				flagUp = false;
				
				if (super.getRotX() > -UPWARDS_ROT_CAP)
					super.rotate(-20 * DisplayManager.getFrameTime(), 0, 0);
				
				if (super.getRotX() > 0) {
					super.rotate(-60 * DisplayManager.getFrameTime(), 0, 0);
					
					if (super.getRotX() < 0)
						super.setRotX(0);
				}
				
			} else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || flagDown) {
				
				flagDown = false;
				
				if (super.getRotX() < UPWARDS_ROT_CAP)
					super.rotate(20 * DisplayManager.getFrameTime(), 0, 0);
				
				if (super.getRotX() < 0) {
					super.rotate(60 * DisplayManager.getFrameTime(), 0, 0);
					
					if (super.getRotX() > 0)
						super.setRotX(0);
				}
			}
			else if (!Keyboard.isKeyDown(Keyboard.KEY_C)) {
				if (super.getRotX() < 0) {
					super.rotate(30 * DisplayManager.getFrameTime(), 0, 0);
					
					if (super.getRotX() > 0)
						super.setRotX(0);
				}
				else if (super.getRotX() > 0) {
					super.rotate(-30 * DisplayManager.getFrameTime(), 0, 0);
					
					if (super.getRotX() < 0)
						super.setRotX(0);
				}
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
			
		}
		
		if (Mouse.isGrabbed() && Keyboard.isKeyDown(Keyboard.KEY_GRAVE)) {
			Mouse.setGrabbed(false);
			
			gui_panel.setTexture(Loader.loadTexture("LCARSpanel"));
			//trmText.show();
			
			for (IGUI el : miscElements) {
				el.show(guis);
			}
			
			switch (currentPanel) {
			
			case TACTICAL_PANEL:
				for (IGUI el : tacticalElements) {
					el.show(guis);
				}
				break;
				
			case HELM_PANEL:
				for (IGUI el : helmElements) {
					el.show(guis);
				}
				break;
			}
			
		}
		
		/*while (Keyboard.next()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				
			}
		}*/
	
	}
	
	void fire_port_front_phaser() {
		
		leftPhaserTimer += DisplayManager.getFrameTime();
		
		if (leftPhaserTimer > 0.005f) {		
			projectiles.add(Bolt.phaser(ModelSys.pos(super.tmat, new Vector3f(4, 22.375f, 40 + distMoved)),
					20, super.getRotX(), super.getRotY(), 0, this.currentSpeed));
			ENERGY--;
			leftPhaserTimer = 0;
		}
		
	}
	
	void fire_center_front_phaser() {
		
		centerPhaserTimer += DisplayManager.getFrameTime();
		
		if (centerPhaserTimer > 0.005f) {
			projectiles.add(Bolt.phaser(ModelSys.pos(super.tmat, new Vector3f(-0.75f, 20.9f, 54 + distMoved)),
					20, super.getRotX(), super.getRotY(), 0, this.currentSpeed));
			ENERGY--;
			centerPhaserTimer = 0;
		}
		
	}
	
	void fire_starb_front_phaser() {
		
		rightPhaserTimer += DisplayManager.getFrameTime();
		
		if (rightPhaserTimer > 0.005f) {
			projectiles.add(Bolt.phaser(ModelSys.pos(super.tmat, new Vector3f(-5, 22.375f, 40 + distMoved)),
					20, super.getRotX(), super.getRotY(), 0, this.currentSpeed));
			ENERGY--;
			rightPhaserTimer = 0;
		}
		
	}
	
	void fireFrontPhasers() {
		
		mainPhaserTimer += DisplayManager.getFrameTime();
		
		if (mainPhaserTimer > 0.005f) {
			
			projectiles.add(Bolt.phaser(ModelSys.pos(super.tmat, new Vector3f(-5, 22.375f, 40 + distMoved)),
					20, super.getRotX(), super.getRotY(), 0, this.currentSpeed));
			
			projectiles.add(Bolt.phaser(ModelSys.pos(super.tmat, new Vector3f(-0.75f, 20.9f, 54 + distMoved)),
					20, super.getRotX(), super.getRotY(), 0, this.currentSpeed));
			
			projectiles.add(Bolt.phaser(ModelSys.pos(super.tmat, new Vector3f(4, 22.375f, 40 + distMoved)),
					20, super.getRotX(), super.getRotY(), 0, this.currentSpeed));
			
			ENERGY -= 3;
			mainPhaserTimer = 0;
		}
	
	}
	
	void fire_port_front_photon() {
		projectiles.add(Torpedo.photonTorpedo(this.currentSpeed + 4500, super.getRotY(), super.getRotX(), 
				ModelSys.pos(super.tmat, new Vector3f(5, 11, 11))));
		ENERGY -= 40;
	}
	
	void fire_starb_front_photon() {
		projectiles.add(Torpedo.photonTorpedo(this.currentSpeed + 4500, super.getRotY(), super.getRotX(), 
				ModelSys.pos(super.tmat, new Vector3f(-5, 11, 11))));
		ENERGY -= 40;
	}
	
	void fireFrontPhotons() {
		fire_port_front_photon();
		fire_starb_front_photon();
	}
	
	void fire_port_front_quantum() {
		projectiles.add(Torpedo.quantumTorpedo(this.currentSpeed + 4500, super.getRotY(), super.getRotX(), 
				ModelSys.pos(super.tmat, new Vector3f(5, 11, 11))));
		ENERGY -= 70;
	}
	
	void fire_starb_front_quantum() {
		projectiles.add(Torpedo.quantumTorpedo(this.currentSpeed + 4500, super.getRotY(), super.getRotX(), 
				ModelSys.pos(super.tmat, new Vector3f(-5, 11, 11))));
		ENERGY -= 70;
	}
	
	void fireFrontQuantums() {
		fire_port_front_quantum();
		fire_starb_front_quantum();
	}
	
	void firePhaserSpray() {
		
		projectiles.add(new Bolt(privatePhaserTexture, new Vector3f(super.getPosition().x, super.getPosition().y + 10, super.getPosition().z), 
				super.getRotX() + rng.nextFloat() * 2 - 1, super.getRotY() + rng.nextFloat() * 2 - 1, super.getRotZ(),
				1.5f, 1.5f, 20, 300, tracingX, -tracingY, tracingZ, false));
		
		ENERGY -= 100;
		
	}
	
	void firePortArrays(boolean mode) {
		
		Vector3f rots;
		Vector3f firing;
		
		if (mode) {
			firing = SFMath.vecShifts(super.getRotY(), getPosition(), -9, 20, 57.5f);
		}
		else {
			firing = SFMath.vecShifts(super.getRotY(), getPosition(), -10, 20, 45);
		}
		
		if (this.target == null) {
			rots = new Vector3f(-super.getRotX(), super.getRotY(), 0);
		}
		else {
			rots = SFMath.rotateToFaceVector(firing, this.target.getPosition());
		}
		
		projectiles.add(new Bolt(TM.phaserBolt, firing,
				-rots.x, rots.y, 0,
				1.5f, 1.5f, 20, 15, this.currentSpeed + 5000));
		
	}
	
	void fireStarbArrays(boolean mode) {
		
		Vector3f rots;
		Vector3f firing;
		
		if (mode) {
			firing = SFMath.vecShifts(super.getRotY(), getPosition(), 9, 20, 57.5f);
		}
		else {
			firing = SFMath.vecShifts(super.getRotY(), getPosition(), 10, 20, 45);
		}
		
		if (this.target == null) {
			rots = new Vector3f(-super.getRotX(), super.getRotY(), 0);
		}
		else {
			rots = SFMath.rotateToFaceVector(firing, this.target.getPosition());
		}
		
		projectiles.add(new Bolt(TM.phaserBolt, firing,
				-rots.x, rots.y, 0,
				1.5f, 1.5f, 20, 15, this.currentSpeed + 5000));
		
	}
	
	void fireDorsalPortArrays(float angle) {
		
		projectiles.add(new Bolt(privatePhaserTexture, 
				new Vector3f(super.getPosition().x + 
						SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_LEFT, super.getRotY(), 9.1f)
				- SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, super.getRotY(), 12)
						, super.getPosition().y + 33,
						super.getPosition().z + SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_LEFT, super.getRotY(), 9.1f)
						- SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, super.getRotY(), 12)),
				-super.getRotX() - angle, super.getRotY() + 180, 0
				, 1.5f, 1.5f, 20, 10, tracingX, tracingY, tracingZ, true));
		
		ENERGY--;
		
	}
	
	void fireDorsalStarbArrays(float angle) {
		
		projectiles.add(new Bolt(privatePhaserTexture, 
				new Vector3f(super.getPosition().x + 
						SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_RIGHT, super.getRotY(), 9.1f)
				- SFMath.relativePosShiftX(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, super.getRotY(), 12)
						, super.getPosition().y + 33,
						super.getPosition().z + SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_RIGHT, super.getRotY(), 9.1f)
						- SFMath.relativePosShiftZ(SFMath.SF_DIRECTION_AZIMUTH_NEUTRAL, super.getRotY(), 12)),
				-super.getRotX() - angle, super.getRotY() + 180, 0
				, 1.5f, 1.5f, 20, 10, tracingX, -tracingY, tracingZ, true));
		
		ENERGY--;
		
	}
	
	void fireBackMountedPhaser() {
		
		projectiles.add(Bolt.phaser(getPosition(), -0.5f, 21, 0, 10, super.getRotX(), super.getRotY() + 180, super.getRotZ(), this.currentSpeed));
		
		ENERGY -= 1.5f;
		
	}
	
	void fireBackEndPhaser() {
		
		projectiles.add(Bolt.phaser(getPosition(), -0.5f, 6.1f, 45, 10, super.getRotX(), super.getRotY() + 180, super.getRotZ(), this.currentSpeed)); 
		
		ENERGY -= 1.5f;
		
	}
	
	private void fireAllWeapons(RaysCast caster) {
					
		if (Keyboard.isKeyDown(Keyboard.KEY_P))
			fireFrontPhasers();
		
		if (Keyboard.isKeyDown(Keyboard.KEY_O) && counter < 0) {
			fireFrontPhotons();
			counter = 2;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_I) && counter < 0) {
			fireFrontQuantums();
			counter = 0.4f;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_U) && !flag) {
			flag = true;
		}
		
		if (flag) {
			if (counterS >= 1.25f) {
				counterS = 1.25f;
				fireFrontPhotons();
			}
			
			if (SFMath.nIsWithin(counterS, 0, 1)) {
				fireFrontPhasers();
			}
			
			if (counterS < 0) {
				fireFrontQuantums();
				counterS = 10;
				flag = false;
			}
			counterS -= DisplayManager.getFrameTime();
		}
		
		counter -= DisplayManager.getFrameTime();
		
		if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
			fireDorsalPortArrays(0);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_Z) && !flag2) {
			flag2 = true;
			vec = SFMath.rotateToFaceVector(super.getPosition(), this.target != null ? this.target.getPosition() : new Vector3f(0, 0, 0));
			alpha = (vec.y % 360) - super.getRotY();
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
		
		float spd = (this.currentSpeed < 0 ? 0 : this.currentSpeed);
		
		switch (autoFunc) {
		
		case STD_AUTO:
			if (autogunTimer > 0.5f) {
				
				this.projectiles.add(new HomingTorpedo(privateTorpedoTexture,
						new Vector3f(super.getPosition()),
						2, 2, 5, Torpedo.PT * 2f, 3000 + spd, 10, target, 0, 5, 0));
				
				AudioEngine.playTempSrc(TM.photonsnd, 150, super.getPosition().x, super.getPosition().y, super.getPosition().z);
				
				ENERGY -= 100;
				autogunTimer = 0;
			}
			
		case DEADWEIGHT:
			if (autogunTimer > 2) {
				
				this.projectiles.add(new HomingTorpedo(privateTorpedoTexture,
						new Vector3f(super.getPosition()),
						2, 2, 5, Torpedo.PT * 4.5f, 5000 + spd, 10, target, 0, 50, 0));
				
				AudioEngine.playTempSrc(TM.photonsnd, 150, super.getPosition().x, super.getPosition().y, super.getPosition().z);
				
				ENERGY -= 202.5f;
				autogunTimer = 0;
			}
			break;
			
		case GATLING:
			if (autogunTimer > 1f) {
				
				this.projectiles.add(new HomingTorpedo(privateTorpedoTexture,
						new Vector3f(super.getPosition()),
						2, 2, 5, Torpedo.PT * 3f, 3000 + spd, 10, target, 0, 0, 0));
				
				AudioEngine.playTempSrc(TM.photonsnd, 150, super.getPosition().x, super.getPosition().y, super.getPosition().z);
				
				shotsFired++;
				
				if (shotsFired == 4) {
					autogunTimer = 0;
					shotsFired = 0;
				}
				else {
					autogunTimer = 0.9f;
				}
				
			}
			break;
		
		}
		
	}
	
	public void fireTurret(float dx, float dy, float dz) {
		
		float cof = 40;
		
		dx *= cof; dy *= cof; dz *= cof;
		
		switch (turretFunc) {
		
		case ONE_TORP:
			projectiles.add(Torpedo.photonTorpedo(super.getPosition(), dx, dy, dz));
			ENERGY -= 40;
			break;
			
		case TWO_SHOT:
			projectiles.add(new Torpedo(privateTorpedoTexture, 
					new Vector3f(super.getPosition()),
					0, 0, 0, 2, 2, 5, Torpedo.PT, dx, dy, dz));
			
			projectiles.add(new Torpedo(privateTorpedoTexture, 
					new Vector3f(super.getPosition().x, super.getPosition().y + 10, super.getPosition().z),
					0, 0, 0, 2, 2, 5, Torpedo.PT, dx, dy, dz));
			ENERGY -= 80;
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
				ENERGY -= 40;
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
				ENERGY -= 80;
			}
			break;
			
		case PHASER_QUANTUM: 
			Vector3f rotations = SFMath.rotateToFaceVector(super.getPosition(), target);
			projectiles.add(new Bolt(privatePhaserTexture, new Vector3f(super.getPosition().x, super.getPosition().y + 10, super.getPosition().z)
					, -rotations.x, rotations.y, rotations.z, 1.5f, 1.5f, 10, 15, 0));
			ENERGY--;
			
			if (turretTimer > 0.5f || turretToggle) {
				
				projectiles.add(Torpedo.quantumTorpedo(super.getPosition(), vec.x, vec.y, vec.z));
				
				turretTimer = 0;
				turretToggle = false;
				ENERGY -= 70;
			}
			break;
			
		default:
			break;
			//throw new IllegalArgumentException("Ur Turret thing is broken");
		
		}
		
	}

	@Override
	public void respondToCollisioni(float damage) {
		if (shieldsOn && this.SHIELD > 0) {
			if (this.SHIELD - damage > 0) {
				this.SHIELD -= damage;
				TM.blueShieldSystem.generateParticles(super.getPosition(), super.getPosition());
			}
			else {
				this.SHIELD = 0;
				TM.blueShieldSystem.generateParticles(super.getPosition(), super.getPosition());
				shieldWarning = true;
				AudioEngine.playTempSrc(AudioEngine.loadSound("critical_stereo"), 200);
			}
		}
		else {
			this.HEALTH -= damage;
			if (this.HEALTH <= 0) {
				System.exit(0);
			}
		}
		
	}
	
	@Override
	public void choreCollisions(List<Enemy> enemies, RaysCast caster) {
		 
		boolean virg = true;
		
		for (Enemy enemy : enemies) {
			
			BoundingBox bb1 = enemy.getBoundingBox(); 
			
			if (!Mouse.isGrabbed() && caster.penetrates(bb1)) {
				
				if (this.retical == null) {
					this.setPreRetical(enemy.getPosition());
				}
				
				virg = false;

				try {
					Mouse.setNativeCursor(Loader.loadCursor("bullseye"));
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
				
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
					Mouse.next();
				}
				
			}
			
			for (Entity projectile : this.getProjectiles()) {
				
				BoundingBox bb2 = projectile.getBoundingBox();
				
				if (bb1.intersects(bb2)) {
					projectile.respondToCollision();
					enemy.respondToCollisioni(((Projectile) projectile).getDamage());
				}
				
			}
			
			if (bb1.intersects(this.getBoundingBox())) {
				enemy.respondToCollisioni(500);
				this.respondToCollisioni(0);
			}
			
		}
		
		if (virg) {
			
			if (this.target == null)
				this.dropRetical();
			
			if (Mouse.getNativeCursor() != DisplayManager.cursor) {
				try {
					Mouse.setNativeCursor(DisplayManager.cursor);
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
			}
			
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
	
	boolean checkShields() {
		shieldsOn = !shieldsOn;
		if (shieldsOn) {
			shieldsText.setColour(0, 1, 0.75f);
			return true;
		}
		else {
			shieldsText.setColour(0, 0, 1);
			return false;
		}
	}
	
	@Override
	public Vector3f getPlayerPos() {
		return new Vector3f(super.getPosition().x, super.getPosition().y + 10, super.getPosition().z);
		/*return new Vector3f(super.getPosition().x + (TM.rng.nextFloat() - 0.5f) * 100000,
				super.getPosition().y + (TM.rng.nextFloat() - 0.5f) * 10000,
				super.getPosition().z + (TM.rng.nextFloat() - 0.5f) * 100000);*/
	}
	
}
