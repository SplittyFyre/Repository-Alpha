package scene.entities.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.models.TexturedModel;
import renderEngine.textures.GUITexture;
import scene.entities.Entity;
import scene.entities.entityUtils.ITakeDamage;
import scene.entities.hostiles.Enemy;
import scene.entities.projectiles.Projectile;
import utils.RaysCast;

public abstract class Player extends Entity implements ITakeDamage {
	
	public abstract void update(RaysCast caster);
	public abstract void choreCollisions(List<Enemy> enemies, RaysCast caster);
	
	protected List<Projectile> projectiles = Collections.synchronizedList(new ArrayList<Projectile>());
	
	protected List<GUITexture> guis;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, List<GUITexture> guis) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.guis = guis;
	}

}
