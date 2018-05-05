package box;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import collision.CollisionManager;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import gameplay.minimap.MinimapFX;
import objStuff.OBJParser;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.RenderEngine;
import renderEngine.guis.render.GUIRenderer;
import renderEngine.models.RawModel;
import renderEngine.models.TexturedModel;
import renderEngine.textures.GUITexture;
import renderEngine.textures.ModelTexture;
import renderEngine.textures.TerrainTexture;
import renderEngine.textures.TerrainTexturePack;
import scene.Scene;
import scene.entities.BorgVessel;
import scene.entities.Camera;
import scene.entities.Entity;
import scene.entities.Light;
import scene.entities.players.PlayerWarshipVoyager;
import scene.particles.ParticleWatcher;
import scene.terrain.Terrain;
import utils.RaysCast;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class Main {
	
	private static List<Entity> entities = new ArrayList<Entity>();
	private static List<Entity> planets = new ArrayList<Entity>();
	private static List<Entity> enemies = new ArrayList<Entity>();
	private static List<Entity> allEntities = new ArrayList<Entity>();
	
	private static final int MAP = 0;
	private static final int AFT = 1;
	private static int viewScreenMode = 1;
	
	public static void screenFBOMode(int param) {
		viewScreenMode = param;
	}
	
	public static void main(String[] args) {
		
		List<Terrain> terrains = new ArrayList<Terrain>();
		List<Light> lights = new ArrayList<Light>();
		List<WaterTile> waters = new ArrayList<WaterTile>();
		
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		TextMaster.init(loader);
		RenderEngine engine = RenderEngine.init();
		ParticleWatcher.init(loader, engine.getProjectionMatrix());
		Scene scene = new Scene();
		
		//TERRAIN TEXTURE********************************************************************
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		//OTHER TERRAIN STUFF****************************************************************
		
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightMap");
		terrains.add(terrain);
		
		//FERNS******************************************************************************
		
		RawModel fernRaw = OBJParser.loadObjModel("fernModel", loader);
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumRows(2);
		TexturedModel fern = new TexturedModel(fernRaw, fernTextureAtlas);
		
		fern.getTexture().setUseFakeLighting(true);
		fern.getTexture().setTransparent(true);
		
		//PLAYERS****************************************************************************
		
		RawModel playerRaw = OBJParser.loadObjModel("starshipsomeone's", loader);
		TexturedModel playerText = new TexturedModel(playerRaw, new ModelTexture(loader.loadTexture("bottom")));
		
		RawModel apacheRaw = OBJParser.loadObjModel("apache", loader);
		TexturedModel apacheShip = new TexturedModel(apacheRaw, new ModelTexture(loader.loadTexture("dartship")));
		
		RawModel voyagerRaw = OBJParser.loadObjModel("warship_voyager_model", loader);
		TexturedModel voyagerShip = new TexturedModel(voyagerRaw, new ModelTexture(loader.loadTexture("warship_voyager_texture")));
		voyagerShip.getTexture().setSpecularMap(loader.loadTexture("warship_voyager_glowMap"));
		voyagerShip.getTexture().setBrightDamper(3);
		
		//PINE TREES*************************************************************************
		
		RawModel pineRaw = OBJParser.loadObjModel("pine", loader);
		TexturedModel pineText = new TexturedModel(pineRaw, new ModelTexture(loader.loadTexture("pine")));
		
		pineText.getTexture().setTransparent(true);
		pineText.getTexture().setUseFakeLighting(true);
		
		//LAMPS******************************************************************************
		
		RawModel lampRaw = OBJParser.loadObjModel("lampModel", loader);
		TexturedModel lampText = new TexturedModel(lampRaw, new ModelTexture(loader.loadTexture("lamp")));
		
		lampText.getTexture().setUseFakeLighting(true);
		
		//TORPEDOES**************************************************************************
		
		RawModel pretorpedo = OBJParser.loadObjModel("photon", loader);
		TexturedModel torpedo = new TexturedModel(pretorpedo, new ModelTexture(loader.loadTexture("photon")));
		TexturedModel specialTorpedo = new TexturedModel(pretorpedo, new ModelTexture(loader.loadTexture("quantum")));
		
		torpedo.getTexture().setSpecularMap(loader.loadTexture("allGlow"));
		torpedo.getTexture().setBrightDamper(0);
		specialTorpedo.getTexture().setUseFakeLighting(true);
		specialTorpedo.getTexture().setSpecularMap(loader.loadTexture("allGlow"));
		specialTorpedo.getTexture().setBrightDamper(0);
		
		//BOLTS******************************************************************************
		
		RawModel prebolt = OBJParser.loadObjModel("bolt", loader);
		TexturedModel bolt = new TexturedModel(prebolt, new ModelTexture(loader.loadTexture("bolt")));
		bolt.getTexture().setUseFakeLighting(true);
		
		//PHASERS****************************************************************************
		
		RawModel prephaser = OBJParser.loadObjModel("bolt", loader);
		TexturedModel phaser = new TexturedModel(prephaser, new ModelTexture(loader.loadTexture("orange")));
		phaser.getTexture().setUseFakeLighting(true);
		phaser.getTexture().setSpecularMap(loader.loadTexture("allGlow"));
		phaser.getTexture().setBrightDamper(2);
		
		//BULLETS****************************************************************************
		
		RawModel prebullet = OBJParser.loadObjModel("bullet", loader);
		TexturedModel bullet = new TexturedModel(prebullet, new ModelTexture(loader.loadTexture("white")));
		bullet.getTexture().setUseFakeLighting(true);
		
		//PLANETS****************************************************************************
		
		RawModel plane = OBJParser.loadObjModel("photon", loader);
		TexturedModel planet = new TexturedModel(plane, new ModelTexture(loader.loadTexture("ponet")));
		
		//ENEMIES****************************************************************************
		
		RawModel borgRaw = OBJParser.loadObjModel("borge", loader);
		TexturedModel borgShip = new TexturedModel(borgRaw, new ModelTexture(loader.loadTexture("borge")));
		borgShip.getTexture().setSpecularMap(loader.loadTexture("borge_glowMap"));
		borgShip.getTexture().setBrightDamper(2);
		
		//END TEXTURE SECTION****************************************************************
		
		Random random = new Random();
		
		Light sun = new Light(new Vector3f(-600, 1200, 600), new Vector3f(1.3f, 1.3f, 1.3f));
		lights.add(sun);
		
		for (int i = 0; i < 3; i++) {
			Light light = new Light(new Vector3f(random.nextFloat() * 1000000, random.nextFloat() * 1000000, random.nextFloat() * 1000000), 
					new Vector3f(1.3f, 1.3f, 1.3f));
			lights.add(light);
		}
		
		List<GUITexture> guis = new ArrayList<GUITexture>();
		
		PlayerWarshipVoyager player = new PlayerWarshipVoyager(voyagerShip, new Vector3f(0, 0, 0), 0, 0, 0, 10, guis);
		entities.add(player);
		
		//OTHER UTILS************************************************************************
		
		Camera camera = new Camera(player);
		RaysCast caster = new RaysCast(camera, engine.getProjectionMatrix(), terrain);
		
		//SPECIAL WATER COMPONENTS***********************************************************
		
		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, engine.getProjectionMatrix(), buffers);
		WaterTile water = new WaterTile(terrain.getX() + 2400, terrain.getZ() + 2400, 0);
		waters.add(water);
		
		//ADDING RANDOM STUFF (PLACE HOLDER?)*************************************************
		
		BorgVessel borj = new BorgVessel(borgShip, new Vector3f(1000, 0, 1000), 0, 0, 0, 300);
		enemies.add(borj);
		
		for (int i = 0; i < 69; i++) {
			
			BorgVessel borj2 = new BorgVessel(borgShip, new Vector3f(random.nextFloat() * 100000, random.nextFloat() 
					* 100, random.nextFloat() * 100000), 0, 0, 0, 300);
			enemies.add(borj2);
		}

		Fbo fbo = new Fbo(Display.getWidth(), Display.getHeight());
		Fbo output = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		Fbo output2 = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		Fbo minimap = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		Fbo mmout = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		PostProcessing.init(loader);
		
		MinimapFX mmfx = new MinimapFX();
		GUITexture viewsceen = new GUITexture(mmfx.getOutputTexture(), new Vector2f(0.75f, 0.7f), new Vector2f(0.3f, 0.3f), 180);
		guis.add(viewsceen);
		
		GUIRenderer guiRenderer = new GUIRenderer(loader);
		
		/**MAIN GAME LOOP*******************************************************************
		   MAIN GAME LOOP*******************************************************************
		   MAIN GAME LOOP*******************************************************************/
		
		FontType generalFont = new FontType(loader.loadTexture("segoe"), new File("res/segoe.fnt"));
		GUIText coordsX = new GUIText("Loading...", 1.7f, generalFont, new Vector2f(0, 0), 0.5f, false);
		GUIText coordsY = new GUIText("Loading...", 1.7f, generalFont, new Vector2f(0, 0.05f), 0.5f, false);
		GUIText coordsZ = new GUIText("Loading...", 1.7f, generalFont, new Vector2f(0, 0.1f), 0.5f, false);
		GUIText temp = new GUIText("", 3.5f, generalFont, new Vector2f(0.1f, 0.2f), 1, true);
		GUIText phaserEnergy = new GUIText("", 3f, generalFont, new Vector2f(0.35f, 0.05f), 0.5f, false);
		coordsX.setColour(0, 1, 0);
		coordsY.setColour(0, 1, 0);
		coordsZ.setColour(0, 1, 0);
		temp.setColour(1, 0, 0);
		phaserEnergy.setColour(1, 0, 1);
		//phaserEnergy.setText("Made By: Oscar Xu");
		
		while (!Display.isCloseRequested()) {
			
			//player.update(caster, torpedo, bolt, celestial_object);
			//player.update(bullet, bolt);
			player.update(caster);
			camera.move();
			caster.update();
			ParticleWatcher.update();
			
			coordsX.setText(Float.toString(player.getPosition().x));
			coordsY.setText(Float.toString(player.getPosition().y));
			coordsZ.setText(Float.toString(player.getPosition().z));
			//SFUT.println(borj.getBoundingBox().maxX - borj.getBoundingBox().minX);
			allEntities.clear();
			allEntities.addAll(enemies);
			allEntities.addAll(entities);
			allEntities.addAll(player.projectiles);
			
			scene.setEntityList(allEntities);
			scene.setCamera(camera);
			scene.setTerrainList(terrains);
			scene.setLightList(lights);
			
			if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
				viewsceen.getScale().x += 0.01f;
				viewsceen.getPosition().x += 0.01f;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
				viewsceen.getScale().x -= 0.01f;
				viewsceen.getPosition().x -= 0.01f;
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				temp.setText("Attack Pattern : [Sierra]");
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
				temp.setText("");
			}
			
			while (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				DisplayManager.updateDisplay();
			}
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			scene.setClipPlanePointer(new Vector4f(0, -1, 0, 15));
			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			scene.setClipPlanePointer(new Vector4f(0, 1, 0, -water.getHeight() + 0.5f));
			engine.renderScene(scene);
			camera.getPosition().y += distance;
			camera.invertPitch();
			buffers.bindRefractionFrameBuffer();
			scene.setClipPlanePointer(new Vector4f(0, -1, 0, water.getHeight() + 0.5f));
			engine.renderScene(scene);
			buffers.unbindCurrentFrameBuffer();
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			
			TaskManager.vec31 = camera.getPosition();
			TaskManager.f1 = camera.getPitch();
			TaskManager.f2 = camera.getYaw();
			TaskManager.f3 = camera.getRoll();
			TaskManager.f4 = camera.getDistanceFrom();
			
			switch (viewScreenMode) {
			
			case MAP:
				camera.setPosition(new Vector3f(player.getPosition().x, player.getPosition().y + 5000, player.getPosition().z));
				camera.setPitch(90);
				break;
				
			case AFT:
				camera.setDistanceFrom(30);
				camera.setYaw(camera.getYaw() + 180);
				camera.setPitch(0);
				break;
			
			}
			
			
			
			//STOP THIS!!!
			
			minimap.bindFrameBuffer();
			engine.renderMiniMapScene(scene);
			//waterRenderer.render(waters, camera, sun);
			minimap.unbindFrameBuffer();
			
			minimap.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, mmout);
			mmfx.processMinimap(mmout.getColourTexture());
			
			camera.setPosition(new Vector3f(TaskManager.vec31));
			camera.setPitch(TaskManager.f1);
			camera.setYaw(TaskManager.f2);
			camera.setRoll(TaskManager.f3);
			camera.setDistanceFrom(TaskManager.f4);
			
			fbo.bindFrameBuffer();
			
			checkDamageToEnemies();
			engine.renderScene(scene);
			CollisionManager.checkCollisions(player.projectiles, enemies);
			waterRenderer.render(waters, camera, sun);
			ParticleWatcher.renderParticles(camera);
			
			fbo.unbindFrameBuffer();
			
			fbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, output);
			fbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, output2);
			PostProcessing.doPostProcessing(output.getColourTexture(), output2.getColourTexture());
			guiRenderer.render(guis);
			TextMaster.drawText();
			DisplayManager.updateDisplay();
		}
		
		/**MAIN GAME LOOP*******************************************************************
		   MAIN GAME LOOP*******************************************************************
		   MAIN GAME LOOP*******************************************************************/
		
		//CLEAN UP***************************************************************************
		
		mmfx.cleanUp();
		
		TextMaster.cleanUp();
		buffers.cleanUp();
		guiRenderer.cleanUp();
		PostProcessing.cleanUp();
		fbo.cleanUp();
		output.cleanUp();
		output2.cleanUp();
		waterShader.cleanUp();
		engine.cleanUp();
		loader.cleanUp();
		ParticleWatcher.cleanUp();
		DisplayManager.closeDisplay();
		
		//END OF main (String[] args)
		
	}
	
	private static void checkDamageToEnemies() {
		
		for (int i = 0; i < enemies.size(); i++) {

            BorgVessel enemy = (BorgVessel) enemies.get(i);
            
            if (enemy.isDead()) {
            	enemies.remove(i);
            	break;
            }
		}
	}
		
}
