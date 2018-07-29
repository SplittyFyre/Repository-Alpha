package scene.entities.players.trubble;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import scene.entities.hostiles.Enemy;
import scene.entities.players.Player;
import utils.RaysCast;

public class PlayerTrubbleStern extends Player {

	private PlayerTrubble stardrive;

	public PlayerTrubbleStern(Vector3f position, PlayerTrubble stardrive) {
		super(PlayerTrubble.stern_model, position, stardrive.getRotX(), stardrive.getRotY(), stardrive.getRotZ(),
				stardrive.getScale().x, null);
		this.stardrive = stardrive;
		this.customRotationAxis = true;
	}

	@Override
	public void update(RaysCast caster) {
		
	}

	@Override
	public void choreCollisions(List<Enemy> enemies, RaysCast caster) {
		
	}

	@Override
	@Deprecated
	public void respondToCollision() {
		
	}
	
	@Override
	public void respondToCollisioni(float damage) {
		
	}

}
