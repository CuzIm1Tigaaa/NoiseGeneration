package de.cuzim1tigaaa.noise;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A JPanel that draws a 3D coordinate system (range [-2,2] each axis)
 * projected onto 2D, and plots/ connects 3D points.
 */
public class GraphPanel3D extends JPanel {
	private static final double RANGE_MIN = -2.0;
	private static final double RANGE_MAX = 2.0;
	private static final int PADDING = 40;
	private static final int POINT_SIZE = 8;

	// simple perspective parameters
	private static final double FOCAL_LENGTH = 5.0;
	private static final double SCALE = 100.0;

	// 3D point holder
	public static class Point3D {
		public final double x, y, z;

		public Point3D(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	private final List<Point3D> points = new ArrayList<>();

	public GraphPanel3D() {
		setPreferredSize(new Dimension(600, 600));
	}

	/**
	 * Add a 3D point in graph coordinates [-2,2].
	 */
	public void addPoint(double x, double y, double z) {
		points.add(new Point3D(x, y, z));
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int w = getWidth(), h = getHeight();
		Point center = new Point(w / 2, h / 2);

		// draw axes in 3D: X (red), Y (green), Z (blue)
		drawAxis(g2, center, new Point3D(0, 0, 0), new Point3D(RANGE_MAX, 0, 0), Color.RED, "X");
		drawAxis(g2, center, new Point3D(0, 0, 0), new Point3D(0, RANGE_MAX, 0), Color.GREEN, "Y");
		drawAxis(g2, center, new Point3D(0, 0, 0), new Point3D(0, 0, RANGE_MAX), Color.BLUE, "Z");

		// draw points and connect
		if(!points.isEmpty()) {
			g2.setColor(Color.MAGENTA);
			Point prev = null;
			for(Point3D p3 : points) {
				Point p = project(center, p3);
				// draw circle
				Shape c = new Ellipse2D.Double(p.x - POINT_SIZE / 2., p.y - POINT_SIZE / 2., POINT_SIZE, POINT_SIZE);
				g2.fill(c);
				// draw line from previous
				if(prev != null) {
					g2.drawLine(prev.x, prev.y, p.x, p.y);
				}
				prev = p;
			}
		}
	}

	// draw one axis from p0 to p1 with label
	private void drawAxis(Graphics2D g2, Point center, Point3D p0, Point3D p1, Color col, String label) {
		Point s0 = project(center, p0);
		Point s1 = project(center, p1);
		g2.setColor(col);
		g2.drawLine(s0.x, s0.y, s1.x, s1.y);
		g2.drawString(label, s1.x + 5, s1.y - 5);
	}

	// simple perspective project 3D->2D
	private Point project(Point center, Point3D p) {
		double zOff = p.z + FOCAL_LENGTH;
		double factor = FOCAL_LENGTH / zOff;
		double sx = center.x + p.x * factor * SCALE;
		double sy = center.y - p.y * factor * SCALE;
		return new Point((int) Math.round(sx), (int) Math.round(sy));
	}
}
