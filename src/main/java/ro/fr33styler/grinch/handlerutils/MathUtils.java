package ro.fr33styler.grinch.handlerutils;

import java.util.Random;

public class MathUtils {
	
	private static final Random random = new Random(System.nanoTime());

	public static Random random() {
		return random;
	}
	
    public static int randomRange(int start, int end) {
	   return start + random.nextInt(end - start + 1);
	}
	
    public static double toDegrees(double value) {
		return value > 179.9 ? -180 + (value-179.9) : value;
    }
    
	public static int abs(int value) {
		return value < 0 ? -value : value;
	}
	
}