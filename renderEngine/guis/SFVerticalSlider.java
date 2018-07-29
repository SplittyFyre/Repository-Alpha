package renderEngine.guis;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import box.TaskManager;
import fontMeshCreator.GUIText;
import renderEngine.Loader;
import renderEngine.textures.GUITexture;

public abstract class SFVerticalSlider implements ISlider, IGUI {

	private SFVerticalSlider that = this;
	private SFAbstractButton slide;
	private GUITexture background;
	private boolean isHidden = true;
	
	private List<IGUI> marks;
	private List<GUIText> markTexts;
	
	public SFVerticalSlider(float sliderLength, Vector2f position, Vector2f scale, String slidetex, String backgroundtex) {
		
		marks = new ArrayList<IGUI>();
		markTexts = new ArrayList<GUIText>();
		
		slide = new SFAbstractButton(slidetex, position, scale, 0, 0.1f) {
			
			@Override
			public void whileHovering(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void whileHolding(IButton button) {
				this.move(0, (float) (Mouse.getDY() * 0.001));
				
			}
			
			@Override
			public void onStopHover(IButton button) {
				sliderStopHover(that);
				
			}
			
			@Override
			public void onStartHover(IButton button) {
				sliderStartHover(that);
				
			}
			
			@Override
			public void onClick(IButton button) {
				// TODO Auto-generated method stub
				
			}
		};
		background = new GUITexture(Loader.loadTexture(backgroundtex), new Vector2f(position), new Vector2f(0.04f / 1.68f, scale.y * sliderLength));
	}
	
	public SFVerticalSlider(List<IGUI> list, float sliderLength, Vector2f position, Vector2f scale, float len, String slidetex, String backgroundtex) {
		
		marks = new ArrayList<IGUI>();
		markTexts = new ArrayList<GUIText>();
		
		slide = new SFAbstractButton(slidetex, position, scale, 0.01f, 0.1f) {
			
			@Override
			public void whileHovering(IButton button) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void whileHolding(IButton button) {
				this.move(0, (float) (Mouse.getDY() * 0.00125));
				
			}
			
			@Override
			public void onStopHover(IButton button) {
				sliderStopHover(that);
				
			}
			
			@Override
			public void onStartHover(IButton button) {
				sliderStartHover(that);
				
			}
			
			@Override
			public void onClick(IButton button) {
				// TODO Auto-generated method stub
				
			}
		};
		background = new GUITexture(Loader.loadTexture(backgroundtex), new Vector2f(position), new Vector2f(0.04f / 1.68f, len * sliderLength));
		list.add(this);
	}
	
	public void addMark(List<GUITexture> guis, String texture, Vector2f scale, float xoffset, float value) {
		
		float distance = background.getScale().y * 2;
		float floor = background.getPosition().y - background.getScale().y;
		
		SFAbstractButton mark = new SFAbstractButton(this.marks, texture, 
				new Vector2f(background.getPosition().x + xoffset, floor + (distance * value)), scale, 0, 0) {
			
			@Override
			public void whileHovering(IButton button) {
				
			}
			
			@Override
			public void whileHolding(IButton button) {
				
			}
			
			@Override
			public void onStopHover(IButton button) {
				
			}
			
			@Override
			public void onStartHover(IButton button) {
				
			}
			
			@Override
			public void onClick(IButton button) {
				slide.getTexture().getPosition().y = floor + (distance * value);
			}
		};
		
		if (!isHidden) {
			mark.show(guis);
		}
				
	}
	
	public void addMark(List<GUITexture> guis, String texture, Vector2f scale, float xoffset, float value, String text, float txtsize, float 
		txtXOff, float r, float g, float b) {
		
		float distance = background.getScale().y * 2;
		float floor = background.getPosition().y - background.getScale().y;
		
		SFAbstractButton mark = new SFAbstractButton(this.marks, texture, 
				new Vector2f(background.getPosition().x + xoffset, floor + (distance * value)), scale, 0, 0) {
			
			@Override
			public void whileHovering(IButton button) {
				
			}
			
			@Override
			public void whileHolding(IButton button) {
				
			}
			
			@Override
			public void onStopHover(IButton button) {
				
			}
			
			@Override
			public void onStartHover(IButton button) {
				
			}
			
			@Override
			public void onClick(IButton button) {
				slide.getTexture().getPosition().y = floor + (distance * value);
			}
		};
		
		if (!isHidden) {
			mark.show(guis);
		}
		
		//-1.0f + 2.0f * background.getPosition().x + xoffset + txtXOff / Display.getWidth()
		//, 1.0f - 2.0f * floor + (distance * value) / Display.getHeight()
		
		float bgX = background.getPosition().x + xoffset + txtXOff;
		float bgY = floor + (distance * value);
		//System.out.println(bgY);
		
		/*GUIText o = new GUIText(text, txtsize, TaskManager.font, 
				new Vector2f((bgX + xoffset), bgY), 1, false);*/
		System.out.println(new Vector2f((bgX), bgY));
		GUIText o = new GUIText(text, txtsize, TaskManager.font, 
				new Vector2f(((bgX / 2) + 0.5f), -(bgY / 2) + 0.5f - (scale.y / 4)), 1, false);
		o.setColour(r, g, b);
		markTexts.add(o);
		
		if (isHidden) {
			o.hide();
		}
				
	}
	
	@Override
	public void show(List<GUITexture> textures) {
		if (isHidden) {
			textures.add(background);
			slide.show(textures);
			for (IGUI el : marks) {
				el.show(textures);
			}
			for (GUIText el : markTexts) {
				el.show();
			}
			isHidden = false;
		}
	}

	@Override
	public void update() {
		slide.update();
		
		for (IGUI el : marks) {
			el.update();
		}
			
			/*if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				markTexts.get(0).getPosition().x += 0.01;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				markTexts.get(0).getPosition().x -= 0.01;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				markTexts.get(0).getPosition().y += 0.01;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				markTexts.get(0).getPosition().y -= 0.01;
			}
			
		System.out.println(markTexts.get(0).getPosition());*/
		
		float pos = slide.getTexture().getPosition().y;
		float scale = slide.getTexture().getScale().y;
		float bpos = background.getPosition().y;
		float bscale = background.getScale().y;
		if (pos + scale > bpos + bscale + scale) {
			slide.getTexture().getPosition().y = bpos + bscale; 
		}
		else if (pos - scale < bpos - bscale - scale) {
			slide.getTexture().getPosition().y = bpos - bscale;
		}
	}
	
	@Override
	public void hide(List<GUITexture> textures) {
		if (!isHidden) {
			textures.remove(background);
			slide.hide(textures);
			for (IGUI el : marks) {
				el.hide(textures);
			}
			for (GUIText el : markTexts) {
				el.hide();
			}
			isHidden = true;
		}
	}
	
	public float getSliderValue() {
		float floor = background.getPosition().y - background.getScale().y;
		//return (slide.getTexture().getPosition().y - floor) / (background.getPosition().y + background.getScale().y - floor);
		/*System.out.println(slide.getTexture().getPosition().y);
		System.out.println(floor);
		System.out.println(background.getScale().y * 2 + "\nResult:");*/
		return (slide.getTexture().getPosition().y - floor) / (background.getScale().y * 2);
	}
	
}
