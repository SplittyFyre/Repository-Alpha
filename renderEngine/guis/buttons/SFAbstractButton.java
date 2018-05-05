package renderEngine.guis.buttons;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.textures.GUITexture;

public abstract class SFAbstractButton implements IButton {
	
	protected GUITexture buttonTexture;
	private Vector2f defaultScale;
	private boolean isHidden = true, isHovering = false;
	
	public SFAbstractButton(Loader loader, String texture, Vector2f position, Vector2f scale) {
		buttonTexture = new GUITexture(loader.loadTexture(texture), position, scale);
		defaultScale = new Vector2f(scale);
	}
	
	public GUITexture getTexture() {
		return buttonTexture;
	}
	
	@Override
	public void update() {
		if (!isHidden) {
			Vector2f location = buttonTexture.getPosition();
			Vector2f scale = buttonTexture.getScale();
			Vector2f mouseCoordinates = DisplayManager.getNormalizedMouseCoords();
			if (mouseIntersect(location, mouseCoordinates, scale)) {
				whileHovering(this);
				if (!isHovering) {
					isHovering = true;
					onStartHover(this);
				}
				while (Mouse.next()) { 
					if (Mouse.getEventButtonState() && Mouse.isButtonDown(0) && Mouse.getEventButton() == 0) {
				        onClick(this);    
				    }
				}

				if (Mouse.isButtonDown(0))
					whileHolding(this);
				
			} else if (isHovering) {
				isHovering = false;
				onStopHover(this);
			}
		}
	}
	
	@Override
	public void show(List<GUITexture> textures) {
		if (isHidden) {
			textures.add(buttonTexture);
			isHidden = false;
		}
	}
	
	@Override
	public void hide(List<GUITexture> textures) {
		if (!isHidden) {
			textures.remove(buttonTexture);
			isHidden = true;
		}
	}
	
	@Override
	public void resetScale() {
		buttonTexture.setScale(defaultScale);
	}
	
	@Override
	public void playHoverAnimation(float scaleIncrease) {
		buttonTexture.setScale(new Vector2f(defaultScale.x + scaleIncrease, defaultScale.y + scaleIncrease));
	}
	
	private boolean mouseIntersect(Vector2f location, Vector2f mouseCoordinates, Vector2f scale) {
		return location.y + scale.y > -mouseCoordinates.y && 
				location.y - scale.y < -mouseCoordinates.y && 
				location.x + scale.x > mouseCoordinates.x && 
				location.x - scale.x < mouseCoordinates.x;
	}
	
	public boolean isHidden() {
		return isHidden;
	}
	
}
