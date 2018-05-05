package collision;

import java.util.List;

import scene.entities.BorgVessel;
import scene.entities.Entity;
import scene.entities.projectiles.Projectile;

public class CollisionManager {
	
	public static void checkCollisions(List<Entity> playerProjectiles, List<Entity> enemies) {
		
		for (Entity projectile : playerProjectiles) {
			
			BoundingBox bb1 = projectile.getBoundingBox();
			
			for (Entity enemy : enemies) {
				
				BoundingBox bb2 = enemy.getBoundingBox();
				
				if (bb1.intersects(bb2)) {
					
					projectile.respondToCollision();
					((BorgVessel) enemy).respondToCollisioni(((Projectile) projectile).getDamage());
				}
				
			}
			
		}
		
	}
	
}