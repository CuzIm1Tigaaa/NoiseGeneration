package de.cuzim1tigaaa.noise;

import java.awt.*;

public class TerraUtils {

	public static int getTemperatureLevel(double temp) {
		temp = Math.min(1.0, Math.max(-1.0, temp));

		if(temp < -.45) return 0;
		if(temp < -.15) return 1;
		if(temp < .2) return 2;
		if(temp < .55) return 3;
		return 4;
	}

	public static int getHumidityLevel(double hum) {
		hum = Math.min(1.0, Math.max(-1.0, hum));

		if(hum < -.35) return 0;
		if(hum < -.1) return 1;
		if(hum < .1) return 2;
		if(hum < .3) return 3;
		return 4;
	}

	public static int classifyErosionLevel(double erosion) {
		erosion = Math.min(1.0, Math.max(-1.0, erosion));

		if(erosion < -0.78) return 0;
		if(erosion < -0.375) return 1;
		if(erosion < -0.2225) return 2;
		if(erosion < 0.05) return 3;
		if(erosion < 0.45) return 4;
		if(erosion < 0.55) return 5;
		return 6;
	}

	public static int classifyContinentalnessLevel(double continentalness) {
		continentalness = convertToRange(continentalness, -1.0, 1.0, -1.2, 1.0);

		if(continentalness < -1.05) return 0;
		if(continentalness < -.455) return 1;
		if(continentalness < -.19) return 2;
		if(continentalness < -.11) return 3;
		if(continentalness < 0.03) return 4;
		if(continentalness < 0.3) return 5;
		return 6;
	}

	public static Color classifyContinentalColor(int contLevel) {
		return switch(contLevel) {
			case 0 -> new Color(255, 0, 255);
			case 1 -> new Color(0, 0, 255);
			case 2 -> new Color(0, 127, 255);
			case 3 -> new Color(255, 234, 158);
			case 4 -> new Color(0, 255, 0);
			case 5 -> new Color(0, 128, 0);
			case 6 -> new Color(0, 96, 0);
			default -> new Color(0, 0, 0); // For unknown
		};
	}


	public static double calculateRiverValue(double erosion, double pv, double cont) {
		double eFlatness = 1.0 - Math.min(1.0, Math.abs(erosion) / 0.05); // peak at erosion ≈ 0
		double pvFavor = 1.0 - pv; // lower PV → more river
		double contFavor = 1.0 - Math.abs(cont) / 0.3;
		contFavor = clamp(contFavor, 0.0, 1.0);
		return clamp(eFlatness * pvFavor * contFavor, 0.0, 1.0);
	}

	public static Color blendRiver(Color base, double riverValue) {
		Color river = new Color(30, 90, 160); // river blue

		// Blend colors
		int r = (int) (base.getRed() * (1 - riverValue) + river.getRed() * riverValue);
		int g = (int) (base.getGreen() * (1 - riverValue) + river.getGreen() * riverValue);
		int b = (int) (base.getBlue() * (1 - riverValue) + river.getBlue() * riverValue);

		return new Color(r, g, b);
	}

	public static Color applyBrightness(Color base, double pv, double erosion) {
		// Normalize erosion from -1..1 → 0..1
		double normErosion = (erosion + 1.0) / 2.0;

		// High erosion = flatter, less contrast → flatten PV influence
		double erosionFactor = 0.5 + (normErosion * 0.5); // range: 0.5 to 1.0
		double brightness = 0.6 + (pv * 0.8 * erosionFactor); // limit PV influence

		int r = (int) clamp(base.getRed() * brightness, 0, 255);
		int g = (int) clamp(base.getGreen() * brightness, 0, 255);
		int b = (int) clamp(base.getBlue() * brightness, 0, 255);

		return new Color(r, g, b);
	}


	public static double convertToRange(double value, double oldMin, double oldMax, double newMin, double newMax) {
		value = Math.min(oldMax, Math.max(oldMin, value));
		double oldRange = oldMax - oldMin;
		double newRange = newMax - newMin;
		return (((value - oldMin) * newRange) / oldRange) + newMin;
	}


	public static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	private static int biomeColor(String biome) {
		return switch(biome) {
			case "Mushroom Fields" -> new Color(180, 0, 255).getRGB();
			case "Deep Ocean" -> new Color(0, 0, 90).getRGB();
			case "Ocean" -> new Color(0, 0, 160).getRGB();
			case "Coast" -> new Color(0, 120, 180).getRGB();
			case "Near-Inland" -> new Color(100, 200, 100).getRGB();
			case "Mid-Inland" -> new Color(50, 180, 50).getRGB();
			case "Far-Inland" -> new Color(10, 140, 10).getRGB();
			default -> Color.MAGENTA.getRGB(); // For unknown
		};
	}

	private static double calculatePeakValley(double weirdness) {
		return (1 - Math.abs((3 * Math.abs(weirdness)) - 2));
	}

}