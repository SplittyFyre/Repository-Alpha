package utils;

import org.lwjgl.util.vector.Vector3f;

import scene.entities.players.Player;

public class FloatingOrigin {
	
	private Player player;
	
	private float gridLen;
	private float part;
	
	private int gridX = 0;
	private int gridZ = 0;
	
	public FloatingOrigin(Player player, float gridSideLen) {
		this.player = player;
		this.gridLen = gridSideLen;
		this.part = this.gridLen / 2;
	}
	
	public Vector3f update() {
		
		float transX = 0;
		float transZ = 0;
		
		if (player.getPosition().x > part) {
			transX = -gridLen;
			gridX++;
		}
		else if (player.getPosition().x < -part) {
			transX = gridLen;
			gridX--;
		}
		
		if (player.getPosition().z > part) {
			transZ = -gridLen;
			gridZ++;
		}
		else if (player.getPosition().z < -part) {
			transZ = gridLen;
			gridZ--;
		}
		
		return new Vector3f(transX, 0, transZ);
		
	}
	
	public int getGridX() {
		return gridX;
	}

	public int getGridZ() {
		return gridZ;
	}

}
