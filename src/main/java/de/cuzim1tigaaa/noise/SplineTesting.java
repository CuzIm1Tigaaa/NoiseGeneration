package de.cuzim1tigaaa.noise;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplineTesting {

	private final double[] x, y;

	public SplineTesting(double[] x, double[] y) {
		this.x = x;
		this.y = y;
	}

	public double calcSpline(double n) {
		int N = x.length;

		if(n >= x[N - 1])
			return y[N - 1];

		int i = 0;
		while(i < N - 1 && n > x[i + 1])
			i++;

		double t = (n - x[i]) / (x[i + 1] - x[i]);

		double y0 = y[Math.max(0, i - 1)];
		double y1 = y[i];
		double y2 = y[i + 1];
		double y3 = y[Math.min(N - 1, i + 2)];

		// Catmull-Rom formula
		double t2 = t * t, t3 = t2 * t;
		double a = 0.5 * (2 * y1);
		double b = 0.5 * (y2 - y0) * t;
		double c = 0.5 * (2 * y0 - 5 * y1 + 4 * y2 - y3) * t2;
		double d = 0.5 * (-y0 + 3 * y1 - 3 * y2 + y3) * t3;

		return a + b + c + d;
	}

	public static void main(String[] args) {
		double[] contX = new double[]{-1.2, -1.05, -.455, -.19, -.11, .03, .3, 1.0},
				contY = new double[]{.45, .05, .1, .3, .56, .6, .9, 1.0};

		double[] erosX = new double[]{-1.0, -0.78, -0.375, -0.2225, 0.05, 0.45, 0.55, 1.0},
				erosY = new double[]{1., .7, .75, .2, .13, .3, .11, .1};

		double[] weirdX = new double[]{-1.0, -0.85, -0.15, 0.2, 0.7, 1.0},
				weirdY = new double[]{0., .17, .2, .75, .73, .76};

		Map<Color, List<Point2D.Double>> contPoints = new HashMap<>();
		Map.Entry<Color, List<Point2D.Double>> cont = getSplines(Color.BLUE, contX, contY),
				eros = getSplines(Color.RED, erosX, erosY),
				weird = getSplines(Color.MAGENTA, weirdX, weirdY);

		contPoints.put(cont.getKey(), cont.getValue());
		contPoints.put(eros.getKey(), eros.getValue());
		contPoints.put(weird.getKey(), weird.getValue());

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Simple Graph Coordinate System");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			GraphPanel graphPanel = new GraphPanel();
			graphPanel.addPoint(contPoints);
			frame.add(graphPanel);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}

	private static Map.Entry<Color, List<Point2D.Double>> getSplines(Color color, double[] x, double[] y) {
		List<Point2D.Double> points = new ArrayList<>();
		SplineTesting spline = new SplineTesting(x, y);
		//for(int i = 0; i < x.length; i++)
		//	points.add(new Point2D.Double(x[i], y[i]));

		for(double i = x[0]; i <= x[x.length - 1]; i += 0.02) {
			double yVal = spline.calcSpline(i);
			points.add(new Point2D.Double(i, yVal));
		}
		return Map.entry(color, points);
	}
}