package de.cuzim1tigaaa.noise;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple JPanel that draws an X-Y coordinate system.
 */
public class GraphPanel extends JPanel {
	private static final double X_MIN = -500;
	private static final double X_MAX = 500;
	private static final double Y_MIN = 0;
	private static final double Y_MAX = 200;
	private static final int PADDING = 40;
	private static final int TICK_SIZE = 6;
	private static final int NUM_TICKS = 10; // ticks at -2, -1, 0, 1, 2
	private static final int POINT_SIZE = 4;

	// List of points in graph coordinates (gx, gy)
	private final Map<Color, List<Point2D.Double>> points = new HashMap<>();

	public GraphPanel() {
		setPreferredSize(new Dimension(1000, 600));
	}

	/**
	 * Add a point in graph coordinates to be drawn and connected.
	 */
	public void addPoint(Map<Color, List<Point2D.Double>> points) {
		this.points.clear();
		this.points.putAll(points);
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int w = getWidth();
		int h = getHeight();

		// draw axes
		Point origin = toScreen(0, 0, w, h);
		g2.setColor(Color.BLACK);
		g2.drawLine(PADDING, origin.y, w - PADDING, origin.y); // X axis
		g2.drawLine(origin.x, PADDING, origin.x, h - PADDING); // Y axis

		g2.setColor(Color.BLUE);
		Point pWater = toScreen(PADDING, 64, w, h);
		g2.drawLine(PADDING, pWater.y, w - PADDING, pWater.y);

		// draw ticks and labels
		g2.setColor(Color.BLACK);
		for(int i = 0; i <= NUM_TICKS; i++) {
			double xVal = X_MIN + i * (X_MAX - X_MIN) / NUM_TICKS;
			double yVal = Y_MIN + i * (Y_MAX - Y_MIN) / NUM_TICKS;

			// X-axis ticks
			Point pX = toScreen(xVal, 0, w, h);
			g2.drawLine(pX.x, origin.y - TICK_SIZE / 2, pX.x, origin.y + TICK_SIZE / 2);
			String xLabel = String.format("%.1f", xVal);
			g2.drawString(xLabel, pX.x - g2.getFontMetrics().stringWidth(xLabel) / 2, origin.y + PADDING / 2);

			// Y-axis ticks
			Point pY = toScreen(0, yVal, w, h);
			g2.drawLine(origin.x - TICK_SIZE / 2, pY.y, origin.x + TICK_SIZE / 2, pY.y);
			String yLabel = String.format("%.1f", yVal);
			g2.drawString(yLabel, origin.x - PADDING / 2 - g2.getFontMetrics().stringWidth(yLabel), pY.y + g2.getFontMetrics().getAscent() / 2);
		}

		if(!points.isEmpty()) {
			// draw lines connecting points
			for(Color c : points.keySet()) {
				g2.setColor(c);
				List<Point2D.Double> points = this.points.get(c);
				for(int i = 0; i < points.size() - 1; i++) {
					Point p1 = toScreen(points.get(i).x, points.get(i).y, w, h);
					Point p2 = toScreen(points.get(i + 1).x, points.get(i + 1).y, w, h);
					g2.drawLine(p1.x, p1.y, p2.x, p2.y);
				}

				// draw points
				//g2.setColor(Color.BLACK);
				//for (Point.Double pt : points) {
				//	Point p = toScreen(pt.x, pt.y, w, h);
				//	Shape circle = new Ellipse2D.Double(
				//			p.x - POINT_SIZE / 2., p.y - POINT_SIZE / 2.,
				//			POINT_SIZE, POINT_SIZE);
				//	g2.fill(circle);
				//}
			}
		}
	}

	/**
	 * Convert graph coordinates (gx,gy) to screen pixel coordinates.
	 */
	private Point toScreen(double gx, double gy, int w, int h) {
		double sx = PADDING + (gx - X_MIN) / (X_MAX - X_MIN) * (w - 2 * PADDING);
		double sy = h - PADDING - (gy - Y_MIN) / (Y_MAX - Y_MIN) * (h - 2 * PADDING);
		return new Point((int) Math.round(sx), (int) Math.round(sy));
	}
}

