package scene.terrain;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.textures.TerrainTexture;
import renderEngine.textures.TerrainTexturePack;
import water.WaterTile;

public class Island {
	
	private Terrain terrain;
	private WaterTile water;
	
	private Vector3f position;
	
	private float size;
	
	public Island(TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap,
			List<Terrain> terrains, List<WaterTile> waters, float x, float y, float z, float size) {
		terrain = new Terrain(x, y, z, size, texturePack, blendMap, heightMap);
		terrains.add(terrain);
		water = new WaterTile(x, z, y, size / 2);
		waters.add(water);
	}

}
