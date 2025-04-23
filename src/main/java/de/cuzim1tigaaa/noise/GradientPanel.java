package de.cuzim1tigaaa.noise;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GradientPanel extends JPanel {
	private BufferedImage gradientImage;
	private int[][] values;

	public GradientPanel(int[][] newVals) {
		generateRandomGradient(newVals);
		repaint();
		// Initialize with some default values
	}

	public void generateRandomGradient(int[][] newVals) {
		int width = newVals.length;  // Default size, will be adjusted in paintComponent
		int height = newVals[0].length;

		values = new int[width][height];
		for(int x = 0; x < width; x++)
			System.arraycopy(newVals[x], 0, values[x], 0, height);
		updateImage();
	}

	public void updateImage() {
		if(getWidth() <= 0 || getHeight() <= 0) return;

		// Create a new image if needed or if size changed
		if(gradientImage == null ||
				gradientImage.getWidth() != getWidth() ||
				gradientImage.getHeight() != getHeight()) {

			gradientImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
			values = new int[getWidth()][getHeight()];

			// Create a more interesting gradient pattern
			for(int x = 0; x < getWidth(); x++) {
				for(int y = 0; y < getHeight(); y++) {
					// Radial gradient
					double dx = x - getWidth() / 2.0;
					double dy = y - getHeight() / 2.0;
					double distance = Math.sqrt(dx * dx + dy * dy);
					double maxDist = Math.sqrt(getWidth() * getWidth() / 4.0 + getHeight() * getHeight() / 4.0);
				}
			}
		}

		// Update the image pixels
		for(int x = 0; x < gradientImage.getWidth(); x++) {
			for(int y = 0; y < gradientImage.getHeight(); y++) {
				int rgb = values[x][y];
				gradientImage.setRGB(x, y, new Color(rgb).getRGB());
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Update image if size changed
		if(gradientImage == null ||
				gradientImage.getWidth() != getWidth() ||
				gradientImage.getHeight() != getHeight()) {
			updateImage();
		}

		// Draw the gradient image
		if(gradientImage != null) {
			g.drawImage(gradientImage, 0, 0, this);
		}
	}
}