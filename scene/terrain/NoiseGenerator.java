package scene.terrain;

import java.util.Random;

public class NoiseGenerator {
	
	private static final float AMPLITUDE = 225f;
	
	private Random random = new Random();
	private int seed;
	
	public NoiseGenerator() {
		this.seed = random.nextInt(Integer.MAX_VALUE);
	}
	
	public float generateHeight(int x, int z) {
		/*if ((x == 0 || x == 127) || (z == 0 || z == 127)) {
			return 20;
		}*/
		float total = getInterpolatedNoise(x / 8f, z / 8f ) * AMPLITUDE;
		total += getInterpolatedNoise(x / 4f, z / 4f ) * AMPLITUDE / 3;
		total += getInterpolatedNoise(x / 2f, z / 2f ) * AMPLITUDE / 9;
		return total + 20;
	}
	
	private float getInterpolatedNoise(float x, float z) {
		
		int intX = (int) x;
		int intZ = (int) z;
		float fracX = x - intX;
		float fracZ = z - intZ;
		
		float v1 = getSmoothNoise(intX, intZ);
		float v2 = getSmoothNoise(intX + 1, intZ);
		float v3 = getSmoothNoise(intX, intZ + 1);
		float v4 = getSmoothNoise(intX + 1, intZ + 1);
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracZ);
	}
	
	private float interpolate(float a, float b, float blend) {
		double theta = blend * Math.PI;
		float f = (float) ((1f - Math.cos(theta)) * 0.5f);
		return a * (1f - f) + b * f;
	}
	
	private float getNoise(int x, int z) {
		random.setSeed(x * 50000 + z * 320000 + seed);
		return random.nextFloat() * 2f - 1f;
	}
	
	private float getSmoothNoise(int x, int z) {
		float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1) + getNoise(x + 1, z + 1)) / 16f;
		float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1) + getNoise(x, z + 1)) / 8f;
		float center = getNoise(x, z) / 4f;
		return corners + sides + center;
	}
	
}
