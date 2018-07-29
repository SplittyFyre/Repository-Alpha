package box;

import java.io.File;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import audio.AudioEngine;
import fontMeshCreator.FontType;
import objStuff.OBJParser;
import renderEngine.Loader;
import renderEngine.models.RawModel;
import renderEngine.models.TexturedModel;
import renderEngine.textures.ModelTexture;
import scene.particles.ParticleSystem;
import scene.particles.ParticleTexture;

public class TaskManager {
	
	public static int photonsnd = AudioEngine.loadSound("res/photon_torpedo.wav");
	public static int quantumsnd = AudioEngine.loadSound("res/quantum_torpedo.wav");
	
	public static RawModel prephaser = OBJParser.loadObjModel("bolt");
	public static TexturedModel phaserBolt = new TexturedModel(prephaser, new ModelTexture(Loader.loadTexture("orange")));
	
	public static RawModel pretorpedo = OBJParser.loadObjModel("photon");
	public static TexturedModel photonTorpedo = new TexturedModel(pretorpedo, new ModelTexture(Loader.loadTexture("photon")));
	public static TexturedModel quantumTorpedo = new TexturedModel(pretorpedo, new ModelTexture(Loader.loadTexture("quantum")));
	
	public static Vector3f vec31 = new Vector3f();
	public static Vector3f vec32 = new Vector3f();
	public static Vector3f vec33 = new Vector3f();
	public static Vector3f vec34 = new Vector3f();
	public static Random rng = new Random();
	
	public static float f1 = 0;
	public static float f2 = 0;
	public static float f3 = 0;
	public static float f4 = 0;
	
	private static ParticleTexture tex1 = new ParticleTexture(Loader.loadTexture("plasma"), 1);
	public static ParticleSystem warpParticleSystem = new ParticleSystem(tex1, 200, 30, 0, 20, 10);
	//texture, pps, speed, gravity, lifelength, scale
	
	private static ParticleTexture tex2 = new ParticleTexture(Loader.loadTexture("fire"), 8);
	public static ParticleSystem burnParticleSystem = new ParticleSystem(tex2, 250, 21, 20, 0.5f, 7);
	public static ParticleSystem explosionParticleSystem = new ParticleSystem(tex2, 250, 21, 0, 1, 100);
	private static ParticleTexture tex3 = new ParticleTexture(Loader.loadTexture("greenfire"), 8);
	public static ParticleSystem borgExplosionSystem = new ParticleSystem(tex3, 250, 21, 0, 9, 5000);
	
	private static ParticleTexture tex4 = new ParticleTexture(Loader.loadTexture("shieldBlue"), 1);
	public static ParticleSystem blueShieldSystem = new ParticleSystem(tex4, 10000, 150, 0, 0.5f, 20);
	
	public static FontType font = new FontType(Loader.loadTexture("segoeUI"), new File("res/segoeUI.fnt"));
	
	public static void init() {
		
		blueShieldSystem.randomizeRotation();
		blueShieldSystem.setScaleError(1);
		blueShieldSystem.setSpeedError(0.5f);
		blueShieldSystem.setLifeError(0.25f);
		
		phaserBolt.getTexture().setUseFakeLighting(true);
		phaserBolt.getTexture().setSpecularMap(Loader.loadTexture("allGlow"));
		phaserBolt.getTexture().setBrightDamper(4);
		
		photonTorpedo.getTexture().setSpecularMap(Loader.loadTexture("allGlow"));
		photonTorpedo.getTexture().setBrightDamper(1);
		quantumTorpedo.getTexture().setUseFakeLighting(true);
		quantumTorpedo.getTexture().setSpecularMap(Loader.loadTexture("allGlow"));
		quantumTorpedo.getTexture().setBrightDamper(1);
		
	}
	
}
