/*
 * Created on Jun 11, 2003
 */
package de.torstennahm.series;

import java.util.Random;

import de.torstennahm.math.MathTN;

/**
 * Models a Halton sequence.
 * 
 * @author Torsten Nahm
 */
public class Halton implements Series<double[]> {
	public enum HaltonType { NORMAL, SHIFTED, RANDOM_START };
	
	private final int dimension;
	private final HaltonType type;
	
	private VanDerCorput[] vanDerCorput;
	private double[] shifts;
	
	/**
	 * Creates the Halton sequence from VanDerCorput sequences for
	 * 2, 3, 5, ...
	 * 
	 * @param dimension dimension of the series
	 */
	public Halton(int dimension) {
		this (dimension, HaltonType.NORMAL);
	}
	
	/**
	 * Creates the Halton sequence from VanDerCorput sequences for
	 * 2, 3, 5, ...
	 * 
	 * @param dimension dimension of the series
	 * @param type halton type
	 */
	public Halton(int dimension, HaltonType type) {
		this (dimension, type, 2);
	}
	
	/**
	 * As <code>Halton(int, boolean)</code>, but instead of using
	 * 2 as first prime, uses the primes following on the given
	 * number (which does not have to be a prime).
	 * 
	 * @param dimension dimension of the series
	 * @param startN start number for VanDerCorput sequences
	 * @param type halton type
	 */
	public Halton(int dimension, HaltonType type, int startN) {
		this.dimension = dimension;
		this.type = type;
		
		vanDerCorput = new VanDerCorput[dimension];
		shifts = new double[dimension];
		
		Random r = new Random();
		
		int n = startN;
		for (int i = 0; i < dimension; i++) {
			while (! MathTN.isPrime(n)) {
				n++; 
			}
			
			if (type == HaltonType.NORMAL) {
				vanDerCorput[i] = new VanDerCorput(n);
			}
			else if (type == HaltonType.SHIFTED) {
				vanDerCorput[i] = new VanDerCorput(n);
				shifts[i] = r.nextDouble();
			} else if (type == HaltonType.RANDOM_START) {
				long l = r.nextLong() / 2;
				if (l < 0) {
					l = -l;
				}
				vanDerCorput[i] = new VanDerCorput(n, l);
			}
			
			n++;
		}
	}
		
	/**
	 * Returns the next point in the Halton sequence.
	 * 
	 * @return next point
	 */
	public double[] next() {
		double[] x = new double[dimension];
		
		for (int i = 0; i < dimension; i++) {
			x[i] = vanDerCorput[i].next();
		}
		
		if (type == HaltonType.SHIFTED) {
			for (int i = 0; i < dimension; i++) {
				x[i] += shifts[i];
				if (x[i] >= 1.0) {
					x[i] -= 1.0;
				}
			}
		}
		
		return x;
	}

	public boolean hasNext() {
		return true;
	}
}
