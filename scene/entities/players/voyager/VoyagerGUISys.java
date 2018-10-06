package scene.entities.players.voyager;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import box.TM;
import renderEngine.Loader;
import renderEngine.guis.IButton;
import renderEngine.guis.IGUI;
import renderEngine.guis.SFAbstractButton;
import renderEngine.textures.GUITexture;

public class VoyagerGUISys {
	
	private static Vector2f panelpos = new Vector2f(0.65f, -0.3f);
	private static Vector2f schmpos = Vector2f.add(panelpos, new Vector2f(-0.2455f, 0), null);
	//schmpos = [0.4045, -0.3f]
	
	private static GUITexture gui_panel = new GUITexture(Loader.loadTexture("LCARSpanel"), panelpos, new Vector2f(0.35f, 0.7f));
	private static GUITexture schematic = new GUITexture(Loader.loadTexture("schematic1"), schmpos, new Vector2f(0.233f, 0.466f));
	
	
	private static List<IGUI> tacticalElements;
	private static List<IGUI> opsElements;
	private static List<IGUI> helmElements;
	private static List<IGUI> miscElements;
	
	private static PlayerWarshipVoyager player;
	
	public VoyagerGUISys(PlayerWarshipVoyager player) {
		tacticalElements = player.tacticalElements;
		opsElements = player.opsElements;
		helmElements = player.helmElements;
		miscElements = player.miscElements;
		VoyagerGUISys.player = player;
		setupButtons();
	}
	
	private static void setupButtons() {
		
		int a1 = Loader.loadTexture("voyphaserdiag2");
		int b1 = Loader.loadTexture("voyphaserdiagactive");
		//BOOKMARK front phaser shoot button 
		SFAbstractButton phaserbutton = new SFAbstractButton(tacticalElements, "voyphaserdiag2", schematic.getPosition(), new Vector2f(-0.0045f, 0.375f), TM.sqr8) {
			
			@Override
			public void whileHovering(IButton button) {
		
			}
			
			@Override
			public void onStopHover(IButton button) {
				this.getTexture().setTexture(a1);
			}
			
			@Override
			public void onStartHover(IButton button) {
				this.getTexture().setTexture(b1);
			}
			
			@Override
			public void onClick(IButton button) {
				
			}

			@Override
			public void whileHolding(IButton button) {
				player.fireFrontPhasers();
			}
		};
		
	}

}
