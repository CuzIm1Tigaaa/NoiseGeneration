package de.cuzim1tigaaa.noise;

import lombok.Getter;

import java.util.List;

@Getter
public class PointX {

	private final double input, output;

	public PointX(double input, double output) {
		this.input = input;
		this.output = output;
	}


	public static double evaluateSpline(List<PointX> spline, double value) {
		for (int i = 0; i < spline.size() - 1; i++) {
			PointX p1 = spline.get(i);
			PointX p2 = spline.get(i + 1);

			if (value >= p1.input && value <= p2.input) {
				double t = (value - p1.input) / (p2.input - p1.input);
				return p1.output + t * (p2.output - p1.output);
			}
		}
		// Clamp if outside
		return value < spline.getFirst().input ? spline.getFirst().output : spline.getLast().output;
	}
}