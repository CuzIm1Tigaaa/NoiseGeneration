package de.cuzim1tigaaa.noise;

import de.cuzim1tigaaa.noise.t.NoiseGenerator;
import de.cuzim1tigaaa.noise.t.PerlinOctaveGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Noise {

	private static final ThreadLocalRandom random = ThreadLocalRandom.current();
	//	private static final PerlinNoiseGenerator perlinNoise = new PerlinNoiseGenerator(random);

	private static final PerlinOctaveGenerator temperatureNoise = new PerlinOctaveGenerator(random, 5);
	private static final PerlinOctaveGenerator humidityNoise = new PerlinOctaveGenerator(random, 2);

	private static final PerlinOctaveGenerator continentNoise = new PerlinOctaveGenerator(random, 7);
	private static final PerlinOctaveGenerator erosionNoise = new PerlinOctaveGenerator(random, 4);
	private static final PerlinOctaveGenerator weirdnessNoise = new PerlinOctaveGenerator(random, 3);

	private static final double TEMPERATURE_SCALE = 0.01;
	private static final double HUMIDITY_SCALE = 0.01;

	private static final double CONTINENT_SCALE = 0.02;
	private static final double EROSION_SCALE = .02;
	private static final double WEIRDNESS_SCALE = 0.008;

	public static final int WIDTH = 1600, HEIGHT = 800;

	public static void main(String[] args) throws InterruptedException {
		int[][] temp = new int[WIDTH][HEIGHT];
		int[][] hum = new int[WIDTH][HEIGHT];

		int[][] cont = new int[WIDTH][HEIGHT];
		int[][] eros = new int[WIDTH][HEIGHT];
		int[][] weird = new int[WIDTH][HEIGHT];

		Map<Integer, Color> colors = new HashMap<>();
		List<Double> noiseValues = new ArrayList<>();

		Map<Integer, List<Point2D.Double>> mixPoints = new HashMap<>();

		int halfWidth = WIDTH / 2, halfHeight = HEIGHT / 2;
		for(int x = -halfWidth; x < halfWidth; x++) {
			for(int z = -halfHeight; z < halfHeight; z++) {
				double temperature = getLayeredNoise(temperatureNoise, x, z, TEMPERATURE_SCALE, 6, .15);
				int tempLevel = TerraUtils.getTemperatureLevel(temperature);
				int color = (tempLevel + 4) * 20;
				temp[x + halfWidth][z + halfHeight] = new Color(color, color, color).getRGB();

				double humidity = getLayeredNoise(humidityNoise, x, z, HUMIDITY_SCALE, 8, .125);
				int humLevel = TerraUtils.getHumidityLevel(humidity);
				color = (humLevel + 4) * 20;
				hum[x + halfWidth][z + halfHeight] = new Color(color, color, color).getRGB();

				double erosion = getLayeredNoise(erosionNoise, x, z, EROSION_SCALE, 5, .15);
				int erosLevel = TerraUtils.classifyErosionLevel(erosion);
				color = (erosLevel + 2) * 16;
				eros[x + halfWidth][z + halfHeight] = new Color(color, color, color).getRGB();

				double continentalness = getLayeredNoise(continentNoise, x, z, CONTINENT_SCALE, 4, .35);
				int contLevel = TerraUtils.classifyContinentalnessLevel(continentalness);
				Color c = TerraUtils.classifyContinentalColor(contLevel);
				cont[x + halfWidth][z + halfHeight] = c.getRGB();

				double weirdnessValue = getLayeredNoise(weirdnessNoise, x, z, WEIRDNESS_SCALE, 4, .1);
				weirdnessValue = 1 - Math.abs(3 * Math.abs(weirdnessValue) - 2);
				color = (int) ((weirdnessValue + 1) * 255 / 2);
				weird[x + halfWidth][z + halfHeight] = new Color(color, color, color).getRGB();

				double contSpline = new SplineTesting(TerraUtils.contX, TerraUtils.contY).calcSpline(continentalness);
				double erosSpline = new SplineTesting(TerraUtils.erosX, TerraUtils.erosY).calcSpline(erosion);
				double weirdSpline = new SplineTesting(TerraUtils.weirdX, TerraUtils.weirdY).calcSpline(weirdnessValue);

				mixPoints.computeIfAbsent(z, k -> new ArrayList<>())
						.add(new Point2D.Double(x, ((contSpline * 1.25) + (erosSpline * .75) + weirdSpline) * TerraUtils.BASE_HEIGHT));
			}
		}

		int z = -halfHeight;
		while(true) {
			Map<Color, List<Point2D.Double>> points = new HashMap<>();
			points.put(new Color(255, 127, 0), mixPoints.get(z));

			drawGraph("Graph", points);
			Thread.sleep(100);
			z++;
			if(z > halfHeight) {
				z = -halfHeight;
			}
		}


		// drawGraph("Height Map", mixPointsByColor);

		// openPanel("Temperature", temp);
		// openPanel("Humidity", hum);
		// openPanel("Erosion", eros);
		// openPanel("Continentalness", cont);
		// openPanel("Weirdness", weird);
	}

	private static JFrame frame;
	private static GraphPanel graphPanel;

	private static void drawGraph(String title, Map<Color, List<Point2D.Double>> graph) {
		if(frame == null) {
			SwingUtilities.invokeLater(() -> {
				frame = new JFrame(title);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				graphPanel = new GraphPanel();
				graphPanel.addPoint(graph);
				frame.add(graphPanel);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			});
			return;
		}

		SwingUtilities.invokeLater(() -> graphPanel.addPoint(graph));
	}

	private static void openPanel(String title, int[][] vals) throws InterruptedException {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH, HEIGHT);

		GradientPanel panel = new GradientPanel(vals);
		frame.add(panel, BorderLayout.CENTER);
		frame.setResizable(false);
		frame.setVisible(true);

		Thread.sleep(500);

		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int x = e.getX(), y = e.getY();
				System.out.println(vals[x][y]);
			}
		});

		panel.generateRandomGradient(vals);
		panel.repaint();
	}

	private static double getNoise(PerlinOctaveGenerator octaveGenerator, double x, double z, double scale, double frequency, double amplitude) {
		octaveGenerator.setScale(scale);
		return octaveGenerator.noise(x, z, frequency, amplitude, true);
	}

	private static List<PointX> contSpline() {
		return List.of(
				new PointX(-1.2, 0.05),   // Mushroom Fields: shallow terrain
				new PointX(-0.8, 0.02),   // Deep Ocean: very low
				new PointX(-0.5, 0.08),   // Mid-Ocean
				new PointX(-0.25, 0.12),  // Shallow Ocean
				new PointX(-0.12, 0.20),  // Coast: gentle rise
				new PointX(0.0, 0.35),   // Near-Inland: noticeable elevation
				new PointX(0.2, 0.55),   // Mid-Inland: moderate height
				new PointX(0.4, 0.70),   // Early Far-Inland
				new PointX(0.7, 0.85),   // Hills
				new PointX(1.0, 0.92)    // High Inland plateau
		);
	}

	private static List<PointX> erosSpline() {
		return List.of(
				new PointX(-1.0, 0.95),   // Very jagged terrain
				new PointX(-0.85, 0.90),
				new PointX(-0.65, 0.75),  // Tall peaks
				new PointX(-0.4, 0.65),   // High hills
				new PointX(-0.2, 0.50),   // Smooth hills
				new PointX(0.0, 0.35),   // Gentle slopes
				new PointX(0.3, 0.25),   // Plains
				new PointX(0.5, 0.15),   // Flatter areas
				new PointX(0.7, 0.05),   // Depressions / valleys
				new PointX(1.0, 0.0)     // Extreme erosion, flat and low
		);
	}

	private static String classifyBiome(double cont) {
		// Apply your classification logic like before:
		if(cont < -1.05) return "Mushroom Fields";
		if(cont < -0.455) return "Deep Ocean";
		if(cont < -0.19) return "Ocean";
		if(cont < -0.11) return "Coast";
		if(cont < 0.03) return "Near-Inland";
		if(cont < 0.3) return "Mid-Inland";
		return "Far-Inland";
		// You can expand with erosion + weirdness classification
	}

	private static double getLayeredNoise(PerlinOctaveGenerator octaveGenerator, double x, double z, double scale, double frequency, double amplitude) {
		double result = 0;
		double amp = 1.0;
		double freq = 1.0;
		double max = 0;

		for(NoiseGenerator gen : octaveGenerator.getOctaves()) {
			double nx = x * scale * freq;
			double nz = z * scale * freq;
			result += gen.noise(nx, nz) * amp;
			max += amp;

			amp *= amplitude;  // decay amplitude
			freq *= frequency;  // increase detail
		}

		return result / max;  // Normalize to -1..1
	}
}