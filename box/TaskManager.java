package box;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.Loader;
import scene.particles.ParticleSystem;
import scene.particles.ParticleTexture;

public class TaskManager {
	
	private static Loader loader = new Loader();
	
	public static Vector3f vec31 = new Vector3f();
	public static Vector3f vec32 = new Vector3f();
	public static Vector3f vec33 = new Vector3f();
	public static Vector3f vec34 = new Vector3f();
	
	public static float f1 = 0;
	public static float f2 = 0;
	public static float f3 = 0;
	public static float f4 = 0;
	
	private static ParticleTexture tex1 = new ParticleTexture(loader.loadTexture("cosmic"), 4);
	public static ParticleSystem warpParticleSystem = new ParticleSystem(tex1, 250, 30, 0, 20, 10);
	//texture, pps, speed, gravity, lifelength, scale
	
	private static ParticleTexture tex2 = new ParticleTexture(loader.loadTexture("fire"), 8);
	public static ParticleSystem burnParticleSystem = new ParticleSystem(tex2, 250, 21, 20, 0.5f, 7);
	public static ParticleSystem explosionParticleSystem = new ParticleSystem(tex2, 250, 21, 0, 1, 100);
	private static ParticleTexture tex3 = new ParticleTexture(loader.loadTexture("greenfire"), 8);
	public static ParticleSystem borgExplosionSystem = new ParticleSystem(tex3, 250, 21, 0, 3, 5000);

}
