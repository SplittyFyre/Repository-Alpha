package audio;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

public class AudioEngine {
	
	private static List<Integer> buffers = new ArrayList<Integer>();
	private static List<AudioSrc> sources = new ArrayList<AudioSrc>();
	
	private static List<AudioSrc> autoKills = new ArrayList<AudioSrc>();
	
	public static void init() {
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public static void setListenerData(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_POSITION, x, y, z);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}
	
	public static void addSource(AudioSrc source) {
		sources.add(source);
	}
	
	public static int loadSound(String file) {
		int buffer = 0;
		try {
			buffer = AL10.alGenBuffers();
			buffers.add(buffer);
			//ClassLoader load = Class.class.getClassLoader();
			//URL url = load.getResource("/res/" + file + ".wav");
			WaveData wave = WaveData.create(Class.class.getResourceAsStream("/res/" + file + ".wav"));
			//WaveData wave = WaveData.create(url);
			AL10.alBufferData(buffer, wave.format, wave.data, wave.samplerate);
			wave.dispose();
		}
		catch (Exception e) {
			
		}
		
		return buffer;
	}
	
	public static void cleanUp() {
		for (int el : buffers) {
			AL10.alDeleteBuffers(el);
		}
		for (AudioSrc el : sources) {
			el.delete();
		}
		AL.destroy();
	}
	
	
	
	public static void playTempSrc(int buffer, float vol) {
		AudioSrc src = new AudioSrc();
		autoKills.add(src);
		src.play(buffer, vol);
	}
	
	public static void playTempSrc(int buffer, float vol, float x, float y, float z) {
		AudioSrc src = new AudioSrc();
		src.setPosition(x, y, z);
		autoKills.add(src);
		src.play(buffer, vol);
	}
	
	public static void update() {
		for (int i = 0; i < autoKills.size(); i++) {
			AudioSrc el = autoKills.get(i);
			if (!el.isPlaying()) {
				autoKills.remove(i);
				el = null;
			}
		}
		
		//System.out.println(autoKills.size());
		
		//if (autoKills.size() > 100) {
		//	autoKills.clear();
		//}
		
	}

}
