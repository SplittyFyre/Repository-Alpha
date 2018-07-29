package box;

import java.io.IOException;

import audio.AudioEngine;
import audio.AudioSrc;

public class Tests {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		AudioEngine.init();
		AudioEngine.setListenerData(0, 0, 0);
		
		int buffer = AudioEngine.loadSound("res/photon_torpedo.wav");
		
		AudioSrc src = new AudioSrc();
		src.setLooping(true);
		src.play(buffer);
		
		float x = 8;
		src.setPosition(x, 0, 2);
		
		char c = ' ';
		while (c != 'q') {
			
			x -= 0.03f;
			src.setPosition(x, 0, 2);
			Thread.sleep(10);
			
		}
		
		src.delete(); 
		
		AudioEngine.cleanUp();

	}

}
