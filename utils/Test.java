package utils;

public class Test {
	
	public static void main(String[] args) {

		float p = 0.328f;
		int k = 0;
		int n = 10;
		
		System.out.printf("%.10f\n", binom(p, k, n));
		System.out.printf("%f\n", binom(p, k, n));
		
	}
	
	public static float binom(float p, int k, int n) {
		
		int choose = fac(n) / (fac(k) * fac(n - k));
		
		float var = (float) Math.pow((1 - p), (n - k));
		
		return (float) (choose * Math.pow(p, k) * var);
		
	}
	
	public static double binomd(float p, int k, int n) {
		
		int choose = fac(n) / (fac(k) * fac(n - k));
		
		double var = Math.pow((1 - p), (n - k));
		
		return choose * Math.pow(p, k) * var;
		
	}
	
	public static int fac(int i) {
		
		int ret = 1;
		
		for (; i > 0; i--) {
			ret *= i;
		}
		
		return ret;
		
	}

}
