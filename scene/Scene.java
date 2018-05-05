package scene;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector4f;

import scene.entities.Camera;
import scene.entities.Entity;
import scene.entities.Light;
import scene.terrain.Terrain;

public class Scene {
	
	private List<Entity> entities = new ArrayList<Entity>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private float skyR = 0, skyG = 0, skyB = 0;
	private List<Light> lights;
	private Camera camera;
	private Vector4f clipPlane;
	
	public Vector4f getClipPlanePointer() {
		return clipPlane;
	}
	
	public void setClipPlanePointer(Vector4f plane) {
		clipPlane = plane;
	}
	
	public void setEntityList(List<Entity> e) {
		entities = new ArrayList<Entity> (e);
	}
	
	public void setTerrainList(List<Terrain> t) {
		terrains = new ArrayList<Terrain> (t);
	}
	
	public void setLightList(List<Light> l) {
		lights = new ArrayList<Light> (l);
	}
	
	public void setCamera(Camera c) {
		this.camera = c;
	}
	
	public List<Entity> getEntities() {
		return entities;
	}
	
	public List<Terrain> getTerrains() {
		return terrains;
	}
	
	public List<Light> getLights() {
		return lights;
	}
	
	public Camera getCamera() {
		return camera;
	}

	public float getSkyR() {
		return skyR;
	}

	public float getSkyG() {
		return skyG;
	}

	public float getSkyB() {
		return skyB;
	}
	
}
