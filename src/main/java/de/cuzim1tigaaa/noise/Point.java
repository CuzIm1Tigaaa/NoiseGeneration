package de.cuzim1tigaaa.noise;

import lombok.Getter;

import java.util.List;

@Getter
public class Point {

	private final double input, output;

	public Point(double input, double output) {
		this.input = input;
		this.output = output;
	}


	public static double evaluateSpline(List<Point> spline, double value) {
		for (int i = 0; i < spline.size() - 1; i++) {
			Point p1 = spline.get(i);
			Point p2 = spline.get(i + 1);

			if (value >= p1.input && value <= p2.input) {
				double t = (value - p1.input) / (p2.input - p1.input);
				return p1.output + t * (p2.output - p1.output);
			}
		}
		// Clamp if outside
		return value < spline.getFirst().input ? spline.getFirst().output : spline.getLast().output;
	}
}