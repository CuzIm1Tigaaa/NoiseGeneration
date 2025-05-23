package de.cuzim1tigaaa.noise.t;

import java.util.Random;

public class PerlinOctaveGenerator extends OctaveGenerator {

	/**
	 * Creates a perlin octave generator for the given world
	 *
	 * @param seed Seed to construct this generator for
	 * @param octaves Amount of octaves to create
	 */
	public PerlinOctaveGenerator(long seed, int octaves) {
		this(new Random(seed), octaves);
	}

	/**
	 * Creates a perlin octave generator for the given {@link Random}
	 *
	 * @param rand Random object to construct this generator for
	 * @param octaves Amount of octaves to create
	 */
	public PerlinOctaveGenerator(Random rand, int octaves) {
		super(createOctaves(rand, octaves));
	}

	private static NoiseGenerator[] createOctaves(Random rand, int octaves) {
		NoiseGenerator[] result = new NoiseGenerator[octaves];

		for (int i = 0; i < octaves; i++) {
			result[i] = new PerlinNoiseGenerator(rand);
		}

		return result;
	}
}