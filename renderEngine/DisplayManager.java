package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector2f;

public class DisplayManager {
	
	private static final int WIDTH = 3200;
	private static final int HEIGHT = 1800;
	private static final int FPS_CAP = 120;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay() {	
		
		ContextAttribs attributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setInitialBackground(0.569f, 0.869f, 0.6969f);
			Display.setResizable(true);
			Display.create(new PixelFormat(), attributes);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
			Display.setTitle("Ascenalias Java OpenGL Engine");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		lastFrameTime = getCurrentTime();
	}
	
	public static void updateDisplay() {
		if (Display.wasResized()) {
			System.out.println("Resized");
			GL11.glViewport(0, 0, WIDTH, HEIGHT);
		}
		Display.sync(FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
		//SFUT.println((double) 1 / delta); 
	}
	
	public static float getFrameTime() {
		return delta;
	}
	
	public static void closeDisplay() {
		Display.destroy();
	}
	
	public static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
	
	public static Vector2f getNormalizedMouseCoords() {
		return new Vector2f(-1.0f + 2.0f * Mouse.getX() / Display.getWidth()
				, 1.0f - 2.0f * Mouse.getY() / Display.getHeight());
	}
	
}
