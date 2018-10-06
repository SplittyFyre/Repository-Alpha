package box;

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

import audio.AudioEngine;
import audio.AudioSrc;
import collision.CollisionManager;
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
import scene.entities.Camera;
import scene.entities.Entity;
import scene.entities.Light;
import scene.entities.StaticEntity;
import scene.entities.hostiles.BorgVessel;
import scene.entities.hostiles.Enemy;
import scene.entities.players.Player;
import scene.entities.players.PlayerBirdOfPrey;
import scene.entities.players.PlayerWarshipVoyager;
import scene.entities.players.trubble.PlayerTrubble;
import scene.entities.projectiles.Projectile;
import scene.particles.ParticleWatcher;
import scene.terrain.Island;
import scene.terrain.Terrain;
import utils.FloatingOrigin;
import utils.RaysCast;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class Main {
	
	private static List<Entity> entities = new ArrayList<Entity>();
	public static List<Projectile> foeprojectiles = new ArrayList<Projectile>();
	private static List<Enemy> enemies = new ArrayList<Enemy>();
	private static List<Entity> allEntities = new ArrayList<Entity>();
	
	private static boolean gamePaused = false;
	
	public static final int MAP = 0;
	public static final int AFT = 1;
	public static final int TRGT = 2;
	private static int viewScreenMode = 2;
	
	public static void screenFBOMode(int param) {
		viewScreenMode = param;
	}
	
	public static void main(String[] args) {
		
		List<Terrain> terrains = new ArrayList<Terrain>();
		List<Light> lights = new ArrayList<Light>();
		List<WaterTile> waters = new ArrayList<WaterTile>();
		
		DisplayManager.createDisplay();
		AudioEngine.init();
		
		TextMaster.init();
		TM.init();
		RenderEngine engine = RenderEngine.init();
		ParticleWatcher.init(engine.getProjectionMatrix());
		
		//TERRAIN TEXTURE********************************************************************
		
		TerrainTexture backgroundTexture = new TerrainTexture(Loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(Loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(Loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(Loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		TerrainTexture blendMap = new TerrainTexture(Loader.loadTexture("blendMap"));
		
		//OTHER TERRAIN STUFF****************************************************************
		
		Terrain terrain = new Terrain(0, 0, 0, 4800, texturePack, blendMap, "heightMap");
		//terrains.add(terrain);
		
		//FERNS******************************************************************************
		
		RawModel fernRaw = OBJParser.loadObjModel("fernModel");
		ModelTexture fernTextureAtlas = new ModelTexture(Loader.loadTexture("fern"));
		fernTextureAtlas.setNumRows(2);
		TexturedModel fern = new TexturedModel(fernRaw, fernTextureAtlas);
		
		fern.getTexture().setUseFakeLighting(true);
		fern.getTexture().setTransparent(true);
		
		//PLAYERS****************************************************************************
		
		RawModel playerRaw = OBJParser.loadObjModel("starshipsomeone's");
		TexturedModel playerText = new TexturedModel(playerRaw, new ModelTexture(Loader.loadTexture("bullet")));
		
		RawModel apacheRaw = OBJParser.loadObjModel("apache");
		TexturedModel apacheShip = new TexturedModel(apacheRaw, new ModelTexture(Loader.loadTexture("dartship")));
		
		RawModel voyagerRaw = OBJParser.loadObjModel("warship_voyager_model");
		TexturedModel voyagerShip = new TexturedModel(voyagerRaw, new ModelTexture(Loader.loadTexture("warship_voyager_texture")));
		
		//RawModel voyagerRaw = OBJParser.loadObjModel("birdOP");
		//TexturedModel voyagerShip = new TexturedModel(voyagerRaw, new ModelTexture(Loader.loadTexture("try")));
		voyagerShip.getTexture().setSpecularMap(Loader.loadTexture("warship_voyager_glowMap"));
		//voyagerShip.getTexture().setSpecularMap(Loader.loadTexture("tempGlowMap"));
		voyagerShip.getTexture().setBrightDamper(4);
		
		//PINE TREES*************************************************************************
		
		RawModel pineRaw = OBJParser.loadObjModel("pine");
		TexturedModel pineText = new TexturedModel(pineRaw, new ModelTexture(Loader.loadTexture("pine")));
		
		pineText.getTexture().setTransparent(true);
		pineText.getTexture().setUseFakeLighting(true);
		
		//LAMPS******************************************************************************
		
		RawModel lampRaw = OBJParser.loadObjModel("lampModel");
		TexturedModel lampText = new TexturedModel(lampRaw, new ModelTexture(Loader.loadTexture("lamp")));
		
		lampText.getTexture().setUseFakeLighting(true);
		
		//TORPEDOES**************************************************************************
		
		RawModel pretorpedo = OBJParser.loadObjModel("photon");
		TexturedModel torpedo = new TexturedModel(pretorpedo, new ModelTexture(Loader.loadTexture("photon")));
		TexturedModel specialTorpedo = new TexturedModel(pretorpedo, new ModelTexture(Loader.loadTexture("quantum")));
		
		torpedo.getTexture().setSpecularMap(Loader.loadTexture("allGlow"));
		torpedo.getTexture().setBrightDamper(0);
		specialTorpedo.getTexture().setUseFakeLighting(true);
		specialTorpedo.getTexture().setSpecularMap(Loader.loadTexture("allGlow"));
		specialTorpedo.getTexture().setBrightDamper(0);
		
		//BOLTS******************************************************************************
		
		RawModel prebolt = OBJParser.loadObjModel("bolt");
		TexturedModel bolt = new TexturedModel(prebolt, new ModelTexture(Loader.loadTexture("bolt")));
		bolt.getTexture().setUseFakeLighting(true);
		
		//PHASERS****************************************************************************
		
		RawModel prephaser = OBJParser.loadObjModel("bolt");
		TexturedModel phaser = new TexturedModel(prephaser, new ModelTexture(Loader.loadTexture("orange")));
		phaser.getTexture().setUseFakeLighting(true);
		phaser.getTexture().setSpecularMap(Loader.loadTexture("allGlow"));
		phaser.getTexture().setBrightDamper(2);
		
		//BULLETS****************************************************************************
		
		RawModel prebullet = OBJParser.loadObjModel("bullet");
		TexturedModel bullet = new TexturedModel(prebullet, new ModelTexture(Loader.loadTexture("white")));
		bullet.getTexture().setUseFakeLighting(true);
		
		//PLANETS****************************************************************************
		
		RawModel plane = OBJParser.loadObjModel("photon");
		TexturedModel planet = new TexturedModel(plane, new ModelTexture(Loader.loadTexture("ponet")));
		
		//ENEMIES****************************************************************************
		
		RawModel borgRaw = OBJParser.loadObjModel("borge");
		TexturedModel borgShip = new TexturedModel(borgRaw, new ModelTexture(Loader.loadTexture("borge")));
		borgShip.getTexture().setSpecularMap(Loader.loadTexture("borge_glowMap"));
		borgShip.getTexture().setBrightDamper(2);
		//borgShip.getTexture().setReflectivity(-0.5f);
		
		//END TEXTURE SECTION****************************************************************
		
		Random random = new Random();
		
		Vector3f yellow = new Vector3f(1.3f, 1.3f, 1.3f);
		
		Light sun = new Light(new Vector3f(7000, 3600, 26000), new Vector3f(1.3f, 1.3f, 1.3f));
		lights.add(sun);
		
		//entities.add(new StaticEntity(new TexturedModel(OBJParser.loadObjModel("photon"), new ModelTexture(Loader.loadTexture("image"))),
		//		new Vector3f(7000, 3600, 26000), 0, 0, 0, 1000));
		
		/*Vector3f[2201.2888, 190080.38, 6211.6206]
		Vector3f[196894.77, 104802.63, 214692.56]
		Vector3f[216764.22, 122352.484, 44561.39]*/
		
		lights.add(new Light(new Vector3f(2201.2888f, 190080.38f, 6211.6206f), yellow));
		lights.add(new Light(new Vector3f(196894.77f, 104802.63f, 214692.56f), yellow));
		lights.add(new Light(new Vector3f(-216764.22f, 122352.484f, -44561.39f), yellow));
		
		List<GUITexture> guis = new ArrayList<GUITexture>();
		
		Player player = null;
		
		int p = 0;
		
		switch (p) {
		
		case 0:
			player = new PlayerWarshipVoyager(voyagerShip, new Vector3f(0, 0, 0), 0, 0, 0, 10, guis);
			entities.add(player);
			break;
			
		case 1:
			player = new PlayerBirdOfPrey(new Vector3f(0, 0, 0), 0, 0, 0, 7.5f, guis);
			entities.add(player); 
			break;
			
		case 2:
			player = new PlayerTrubble(new Vector3f(0, 0, 0), 0, 0, 0, 30, guis);
			((PlayerTrubble) player).add(entities);
		
		}
		
		Scene scene = new Scene(player);
		
		//StaticEntity en = new StaticEntity(voyagerShip, new Vector3f(0, 0, 0), 0, 0, 0, 10);
		//entities.add(en);
		
		//OTHER UTILS************************************************************************
		
		Camera camera = new Camera(player);
		RaysCast caster = new RaysCast(camera, engine.getProjectionMatrix(), terrain);
		
		AudioEngine.setListenerData(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
		
		//SPECIAL WATER COMPONENTS***********************************************************
		
		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(waterShader, engine.getProjectionMatrix(), buffers);
		//waters.add(water);
		/*for (int i = 0; i < 1; i++) {
			new Island(texturePack, blendMap, "heightMap", terrains, waters, entities, random.nextFloat() * 30000, 0, random.nextFloat() * 30000,
					10000, 1750093151);
		}*/
		
		Island home = new Island(texturePack, blendMap, "heightMap", terrains, waters, entities, 0, 0, 0, 10000, 1750093151);
		WaterTile water = home.getWater();
		
		entities.add(new StaticEntity(playerText, new Vector3f(-2500, 750, 2900), 0, 45, 0, 20));
		
		//ADDING RANDOM STUFF (PLACE HOLDER?)*************************************************
		
		BorgVessel borj = new BorgVessel(playerText, new Vector3f(1000, 750, 6000), 0, 0, 0, 20, player);
		enemies.add(borj);
		
		for (int i = 0; i < 50; i++) {
			
			BorgVessel borj2 = new BorgVessel(borgShip, new Vector3f(random.nextFloat() * 100000, random.nextFloat() 
					* 100, random.nextFloat() * 100000), 0, 0, 0, 300, player);
			enemies.add(borj2);
		}

		Fbo fbo = new Fbo(Display.getWidth(), Display.getHeight());
		Fbo output = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		Fbo output2 = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		Fbo minimap = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		Fbo mmout = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		PostProcessing.init();
		
		MinimapFX mmfx = new MinimapFX();
		GUITexture viewsceen = new GUITexture(mmfx.getOutputTexture(), new Vector2f(0.75f, 0.7f), new Vector2f(0.3f, 0.3f), 180);
		guis.add(viewsceen);
		
		GUIRenderer guiRenderer = new GUIRenderer();
		
		List<GUITexture> preguis = new ArrayList<GUITexture>();
		
		/**MAIN GAME LOOP*******************************************************************
		   MAIN GAME LOOP*******************************************************************
		   MAIN GAME LOOP*******************************************************************/
		
		System.out.println("About to start Main Game Loop");
		
		float timer = 0;
		float timer2 = 0;
		float timer3 = 0;
		
		float timer4 = 0;
		float timer5 = 0;
		
		GUITexture sf = new GUITexture(Loader.loadTexture("image"), new Vector2f(-0.666f, 0.35f), TM.sqrgui(0.2f));
		sf.flagAlpha = true;
		sf.custAlpha = 0.1f;
		preguis.add(sf);
		
		GUIText sftxt = new GUIText("Programming By: Splitfire", 2, TM.font, TM.coordtext(new Vector2f(
				sf.getPosition().x - 0.75f, sf.getPosition().y + 0.5f)), 0.5f, false, 0);
		sftxt.setColour(0.6f, 1, 0.6f);
		//sftxt.setColour(1, 1, 1);
		
		GUITexture zc = new GUITexture(Loader.loadTexture("image"), new Vector2f(0.666f, -0.35f), TM.sqrgui(0.2f));
		zc.flagAlpha = true;
		zc.custAlpha = 0.1f;
		preguis.add(zc);
		
		GUIText zctxt = new GUIText("Art Designs By: Zoe Chevrier", 2, TM.font, TM.coordtext(new Vector2f(
				zc.getPosition().x + 0.75f, zc.getPosition().y + 0.5f)), 0.5f, false, 0);
		zctxt.setColour(0.6f, 1, 0.6f);
		//zctxt.setColour(1, 1, 1);
		
		GUITexture accent = new GUITexture(Loader.loadTexture("accentegu"), new Vector2f(0.715f, 1.5f), TM.sqrgui(0.025f));
		accent.flagAlpha = true;
		accent.custAlpha = 0.1f;
		preguis.add(accent);
		accent.setRotation(-10);
		
		//GUIText txt = new GUIText("HI", 4, TM.font, new Vector2f(0f, 0.5f), 1, true, 0);
		//txt.setColour(0, 1, 0.1f, 0.25f);
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);
		DisplayManager.updateDisplay();
		
		boolean die = false;
		
		AudioSrc src = new AudioSrc();
		
		int buff = AudioEngine.loadSound("startrek");
		
		src.play(buff);
		
		while ((timer <= 0.5f || src.isPlaying()) &! Display.isCloseRequested() &! Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			float ft = DisplayManager.getFrameTime();
			timer2 += ft;
			timer3 += ft;
			timer4 += ft;
			timer5 += ft;
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glClearColor(0, 0, 0, 1);
			guiRenderer.render(preguis);
			TextMaster.drawSecondaryText();
			
			if (timer3 > 0.01f && sftxt.getPosition().x < 0.05f) {
				sftxt.getPosition().x += 0.00125f;
				timer3 = 0;
			}
			
			if (timer4 > 0.01f && zctxt.getPosition().x > 0.65f) {
				zctxt.getPosition().x -= 0.0025f;
				timer4 = 0;
			}
			
			if (timer5 > 0.01f && accent.getPosition().y > 0.13f) {
				accent.getPosition().y -= 0.006f;
				timer5 = 0;
			}
			
			if (timer2 > 0.01f && sf.custAlpha <= 1) {
				sf.custAlpha += 0.0025f;
				zc.custAlpha += 0.0025f;
				accent.custAlpha += 0.0025f;
				timer2 = 0;
				//txt.plusAlpha(0.005f);
			}
			else {
				if (!src.isPlaying())
					timer += DisplayManager.getFrameTime();
			}
			
			if (sf.custAlpha >= 1) {
				System.out.println("FULL ALPHA");
			}
			
			DisplayManager.updateDisplay();
		}
		
		src.stop();
		
		GUIText version = new GUIText("Version 0.0.01", 0.65f, TM.font, new Vector2f(0, 0.9775f), 0.5f, false);
		version.setColour(1, 1, 1);
				
		GUIText grid = new GUIText("Loading...", 1.7f, TM.font, new Vector2f(0.1f, 0), 0.5f, false);
		grid.setColour(1, 0, 0);
		
		FloatingOrigin.init(player, 200000);
		
		while (!Display.isCloseRequested()) {
			//sun.setPosition(new Vector3f(random.nextFloat() * 100000, 5000, random.nextFloat() * 100000));
			
			player.update(caster);
			camera.move();
			caster.update();
			AudioEngine.setListenerData(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
			ParticleWatcher.update();
			scene.shootProps();
			
			Vector3f trans = FloatingOrigin.update();
			
			grid.setText(FloatingOrigin.getGridX() + ", " + FloatingOrigin.getGridZ());
			
			/*if (Math.abs(SFMath.distance(player.getPosition(), new Vector3f(0, 0, 0))) > 1000) {
				techicalOrigin = new Vector3f(player.getPosition());
				trans = Vector3f.sub(new Vector3f(0, 0, 0), techicalOrigin, null);
				Vector3f.add(trans, offset, offset);
			}
			
			Vector3f real = Vector3f.add(player.getPosition(), offset, null);
			
			coordsX.setText(Float.toString(real.x));
			coordsY.setText(Float.toString(real.y));
			coordsZ.setText(Float.toString(real.z));*/
			
			//System.out.println(home.getTerrain().getY());
			
			/*if (Math.abs(SFMath.distance(player.getPosition(), techicalOrigin)) > 10000) {
				techicalOrigin = new Vector3f(player.getPosition());
				SFMath.xTranslation = Vector3f.sub(techicalOrigin, player.getPosition(), null);
			}*/
			
			for (Entity e : enemies) {
				((BorgVessel) e).update();
			}
			
			for (int i = 0; i < foeprojectiles.size(); i++) {
				Projectile el = foeprojectiles.get(i);
				if (el.isDead()) {
					foeprojectiles.remove(i);
				}
				else {
					el.update(); 
				}
			}
			
			//SFUT.println(borj.getBoundingBox().maxX - borj.getBoundingBox().minX);
			allEntities.clear();
			allEntities.addAll(enemies);
			allEntities.addAll(entities);
			allEntities.addAll(foeprojectiles);
			allEntities.addAll(player.getProjectiles());
			
			for (Entity el : allEntities) {
				Vector3f.add(el.getPosition(), trans, el.getPosition());
			}
			
			for (Terrain el : terrains) {
				el.addVec(trans);
			}
			
			for (WaterTile el : waters) {
				el.addVec(trans);
			}
			
			for (Light el : lights) {
				el.setPosition(Vector3f.add(el.getPosition(), trans, null));
			}
			
			scene.setEntityList(allEntities);
			scene.setCamera(camera);
			scene.setTerrainList(terrains);
			scene.setLightList(lights);
			
			while (Keyboard.next()) {
				if (Keyboard.isKeyDown(Keyboard.KEY_5)) {
					player.getPosition().x = 0;
					player.getPosition().y = 0;
					player.getPosition().z = 0;
					System.out.println(home.getPosition());
				}
				else if (Keyboard.isKeyDown(Keyboard.KEY_4)) {
					player.setRotY(180);
				}
			}
			
			Vector3f col = new Vector3f(1, 0, 0);	
			int cnt = 0;
			
			while (gamePaused) {
				
				GL11.glClearColor(col.x, col.y, col.z, 1);
				float f = 0.01f;
				switch (cnt) {
				
				case 0:
					if (col.y < 1) {
						col.y += (f / 2);
					}
					else {
						cnt++;
					}
					break;
					
				case 1:
					if (col.x > 0) {
						col.x -= (f / 2);
					}
					else {
						cnt++;
					}
					break;
					
				case 2:
					if (col.z < 1) {
						col.z += f;
					}
					else {
						cnt++;
					}
					break;
					
				case 3:
					if (col.y > 0) {
						col.y -= f;
					}
					else {
						cnt++;
					}
					break;
					
				case 4:
					if (col.x < 1) {
						col.x += f;
					}
					else {
						cnt++;
					}
					break;
				
				case 5:
					if (col.z > 0) {
						col.z -= f;
					}
					else {
						cnt = 0;
					}
					break;
				
				}
				
				/*switch (cnt) {
				
				case 0:
					if (col.x < 1) {
						col.x += 0.01f;
					}
					else if (col.y < 1) {
						col.y += 0.01f;
					}
					else if (col.z < 1) {
						col.z += 0.01f;
					}
					else {
						cnt++;
					}
					break;
					
				case 1:
					if (col.x > 0) {
						col.x -= 0.01f;
					}
					else if (col.y > 0) {
						col.y -= 0.01f;
					}
					else if (col.z > 0) {
						col.z -= 0.01f;
					}
					else {
						cnt++;
					}
					break;
					
				case 2:
					if (col.y < 1) {
						col.y += 0.01f;
					}
					else if (col.z < 1) {
						col.z += 0.01f;
					}
					else if (col.x < 1) {
						col.x += 0.01f;
					}
					else {
						cnt++;
					}
					break;
					
				case 3:
					if (col.y > 0) {
						col.y -= 0.01f;
					}
					else if (col.z > 0) {
						col.z -= 0.01f;
					}
					else if (col.x > 0) {
						col.x -= 0.01f;
					}
					else {
						cnt++;
					}
					break;
					
				case 4:
					if (col.z < 1) {
						col.z += 0.01f;
					}
					else if (col.x < 1) {
						col.x += 0.01f;
					}
					else if (col.y < 1) {
						col.y += 0.01f;
					}
					else {
						cnt++;
					}
					break;
					
				case 5:
					if (col.z > 0) {
						col.z -= 0.01f;
					}
					else if (col.x > 0) {
						col.x -= 0.01f;
					}
					else if (col.y > 0) {
						col.y -= 0.01f;
					}
					else {
						cnt++;
					}
					break;
					
				case 6:
					if (col.x < 1) {
						Vector3f.add(col, new Vector3f(0.01f, 0.01f, 0.01f), col);
					}
					else {
						cnt++;
					}
					break;
					
				case 7:
					if (col.x > 0) {
						Vector3f.sub(col, new Vector3f(0.01f, 0.01f, 0.01f), col);
					}
					else {
						cnt = 0;
					}
					break;
					
				}*/
				
				
				
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				GUIText lcl = new GUIText("HI THERE!", 1f, TM.font, new Vector2f(0.5f, 0.5f), 1, false, 0);
				lcl.setColour(1, 0, 0);
				TextMaster.drawSecondaryText();
				while (Keyboard.next()) {
					if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
						gamePaused = false;
					}
				}
				
				
				
				if (Display.isCloseRequested()) {
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
					Loader.cleanUp();
					ParticleWatcher.cleanUp();
					AudioEngine.cleanUp();
					DisplayManager.closeDisplay();
					System.exit(0);
				}
				
				DisplayManager.updateDisplay();
				//SFMath.xTranslation = new Vector3f();
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
			
			TM.vec31 = camera.getPosition();
			TM.f1 = camera.getPitch();
			TM.f2 = camera.getYaw();
			TM.f3 = camera.getRoll();
			TM.f4 = camera.getDistanceFrom();
			
			switch (viewScreenMode) {
			
			case MAP:
				camera.setPosition(new Vector3f(player.getPosition().x, player.getPosition().y + 15000, player.getPosition().z));
				camera.setPitch(90);
				break;
				
			case AFT:
				camera.setDistanceFrom(30);
				camera.setYaw(camera.getYaw() + 180);
				camera.setPitch(0);
				break;
				
			case TRGT:
				camera.setPitch(90);
				if (player.getTarget() != null)
					camera.setPosition(new Vector3f(player.getTarget().getPosition().x, player.getTarget().getPosition().y + 2500, player.getTarget().getPosition().z));
			
			}
			
			
			
			//STOP THIS!!!
			
			minimap.bindFrameBuffer();
			engine.renderMiniMapScene(scene);
			//waterRenderer.render(waters, camera, sun);
			//ParticleWatcher.renderParticles(camera);
			minimap.unbindFrameBuffer();
			
			minimap.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, mmout);
			mmfx.processMinimap(mmout.getColourTexture()); 
			
			camera.setPosition(new Vector3f(TM.vec31));
			camera.setPitch(TM.f1);
			camera.setYaw(TM.f2); 
			camera.setRoll(TM.f3);
			camera.setDistanceFrom(TM.f4);
			
			fbo.bindFrameBuffer();
			
			checkDamageToEnemies();
			engine.renderScene(scene);
			CollisionManager.checkCollisions(player.getProjectiles(), enemies, player, caster);
			waterRenderer.render(waters, camera, sun);
			ParticleWatcher.renderParticles(camera);
			
			fbo.unbindFrameBuffer();
			
			fbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, output);
			fbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, output2);
			PostProcessing.doPostProcessing(output.getColourTexture(), output2.getColourTexture());
			guiRenderer.render(guis);
			TextMaster.drawText();
			AudioEngine.update();
			DisplayManager.updateDisplay();//SFMath.xTranslation = new Vector3f(0, 0, 0);
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
		Loader.cleanUp();
		ParticleWatcher.cleanUp();
		AudioEngine.cleanUp();
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
