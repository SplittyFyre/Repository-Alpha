package renderEngine.textures;

import org.lwjgl.util.vector.Vector2f;

public class GUITexture {
	
	private int textureID;
	private Vector2f position;
	private Vector2f scale;
	private float rotation;
	
	public GUITexture(int textureID, Vector2f position, Vector2f scale) {
		this.textureID = textureID;
		this.position = position;
		this.scale = scale;
		this.rotation = 0;
	}
	
	public GUITexture(int textureID, Vector2f position, Vector2f scale, float rotation) {
		this.textureID = textureID;
		this.position = position;
		this.scale = scale;
		this.rotation = rotation;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public int getTextureID() {
		return textureID;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getScale() {
		return scale;
	}
	
	public void setScale(Vector2f scale) {
		this.scale = scale;
	}
	
	public void setTexture(int textureID) {
		this.textureID = textureID;
	}

}
