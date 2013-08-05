/*
 * Created on Sep 23, 2003
 */
package de.torstennahm.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;


/**
 * Generates test functions for benchmarking multidimensional integration
 * routines.
 * 
 * The test function classes are from:
 * <br>
 * Alan Genz, Testing Multidimensional Integration Routines, Tools, Methods and Languages for
 * Scientific and Engineerings Computation, eds. B. Ford et. al, 1984, pp. 81-94.
 * <p>
 * Each function class will produce functions of a specific type (gaussian, oscillatory, etc.)
 * with random parameters. These random parameters that are generated using a <code>Random</code> object.
 * This may be set, for example with a seeded generator, so the randomization is reproducible.
 * <p>
 * All functions are normalized, that is the integral over the unit cube is 1.
 * 
 * @author Torsten Nahm
 */
public class GenzFunctions {
	static private final int NUMBER_OF_FUNCTIONS = 6;
	
	private Random random = new Random();
	
	/**
	 * Returns the number of function classes.
	 * 
	 * @return number of function classes
	 */
	public int getNumberOfFunctionClasses() {
		return NUMBER_OF_FUNCTIONS;
	}
	
	/**
	 * Returns a <code>String</code> description for a specific funtion class.
	 * <code>classNumber</code> must be between 0 and
	 * <code>getNumberOfFunctionClasses() - 1</code>.
	 * 
	 * @param classNumber number of the class
	 * @return description of the class
	 */
	public String getFunctionClassName(int classNumber) {
		return getFunction(classNumber, 1, 1.0).toString();
	}
	
	/**
	 * Generate a function for a given function class.
	 * <code>classNumber</code> must be between 0 and <code>getNumberOfFunctionClasses() - 1</code>.
	 * The difficulty specifies how difficult the function is to integrate
	 * compared to other functions in this class.
	 * Obviously, difficulty depends on the integration algorithm used, so the difficulty
	 * levels are not at all comparable between function classes,
	 * and only heuristics within a function class.
	 * 
	 * @param classNumber number of the class
	 * @param dimension dimension of the function
	 * @param difficulty difficulty of integrating the function
	 * @return test function
	 */
	public Function getFunction(int classNumber, int dimension, double difficulty) {
		if (classNumber == 0) {
			return new GenzOscillatory(dimension, difficulty);
		} else if (classNumber == 1) {
			return new GenzProductPeak(dimension, difficulty);
		} else if (classNumber == 2) {
			return new GenzCornerPeak(dimension, difficulty);
		} else if (classNumber == 3) {
			return new GenzGaussian(dimension, difficulty);
		} else if (classNumber == 4) {
			return new GenzContinuous(dimension, difficulty);
		} else if (classNumber == 5) {
			return new GenzDiscontinuous(dimension, difficulty);
		} else {
			throw new IllegalArgumentException("Function number not valid");
		}
	}
	
	/**
	 * Sets the random generator for the random parameters of the functions.
	 * 
	 * @param random random generator
	 */
	public void setRandom(Random random) {
		this.random = random;
	}
	
	private double[] randomU(int dim) {
		double[] u = new double[dim];
		for (int i = 0; i < u.length; i++) {
			u[i] = random.nextDouble();
		}
		
		return u;
	}
	
	/* The a values are distributed uniformily over the interval [0.05, 1[,
	 * and not [0,1[, as to small values in a component will cause a strongly
	 * singular function.
	 */
	private double[] randomA(int dim, double h) {
		double[] a = new double[dim];
		for (int i = 0; i < a.length; i++) {
			a[i] = random.nextDouble();
		}
		
		double normalizationFactor = h / MathTN.arraySum(a);
		for (int i = 0; i < a.length; i++) {
			a[i] *= normalizationFactor;
		}
		
		return a;
	}
	
	public class GenzOscillatory extends Function {
		private int dimension;
		private double[] a;
		private double[] u;
		private double invResult;
		
		public GenzOscillatory(int dimension, double h) {
			this.dimension = dimension;
			a = randomA(dimension, h);
			u = randomU(dimension);
			invResult = 1.0 / result();
		}
		
		public GenzOscillatory(double[] a, double[] u) {
			this.dimension = a.length;
			this.a = a;
			this.u = u;
			invResult = 1.0 / result();
		}
		
		@Override
		public double sEvaluate(double[] x) {
			checkArgument(x);
			
			double s = 0.0;
			for (int i = 0; i < dimension; i++) {
				s += a[i] * x[i];
			}
			return Math.cos(2 * Math.PI * u[0] + s) * invResult;
		}
		
		private double result() {
			double product = 1.0;
			
			for (int i = 0; i < dimension; i++) {
				product *= 2.0 * Math.sin(0.5 * a[i]) / a[i];
			}
			
			return Math.cos(2 * Math.PI * u[0] + 0.5 * MathTN.arraySum(a)) * product;
		}
		
		@Override
		public int inputDimension() { return dimension; }
		@Override
		public String toString() { return "Genz Oscillatory"; }
	}

	public class GenzProductPeak extends Function {
		private int dimension;
		private double[] a;
		private double[] u;
		private double invResult;
		
		public GenzProductPeak(int dimension, double h) {
			this.dimension = dimension;
			a = randomA(dimension, h);
			u = randomU(dimension);
			invResult = 1.0 / result();
		}
		 
		@Override
		public double sEvaluate(double[] x) {
			checkArgument(x);
			
			double p = 1.0;
			for (int i = 0; i < dimension; i++) {
				p *= 1.0 / (a[i] * a[i]) + (x[i] - u[i]) * (x[i] - u[i]);
			}
			return 1.0 / p * invResult;
		}
		
		private double result() {
			double p = 1.0;
			
			for (int i = 0; i < dimension; i++) {
				p *= a[i] * (Math.atan(a[i] * (1.0 - u[i])) + Math.atan(a[i] * u[i]));
			}
			
			return p;
		}
		
		@Override
		public int inputDimension() { return dimension; }
		@Override
		public String toString() { return "Genz Product Peak"; }
	}
	
	public class GenzCornerPeak extends Function {
		private int dimension;
		private double[] a;
		private BigDecimal bigA[];
		private BigDecimal bigOne = new BigDecimal(1);
		private double invResult;
		
		public GenzCornerPeak(int dimension, double h) {
			if (dimension > 16) {
				// The method result is recursive and takes too long for higher dimensions
				throw new UnsupportedOperationException("GenzCornerPeak supports a maximum dimension of 16");
			}
			
			this.dimension = dimension;
			bigA = new BigDecimal[dimension];
			a = randomA(dimension, h);
			for (int i = 0; i < dimension; i++) {
				bigA[i] = new BigDecimal(a[i]);
			}
			invResult = 1.0 / result();
		}
		 
		@Override
		public double sEvaluate(double[] x) {
			checkArgument(x);
			
			double s = 0.0;
			for (int i = 0; i < dimension; i++) {
				s += a[i] * x[i];
			}
			return Math.pow((1.0 + s), -(dimension + 1)) * invResult;
		}
		
		private double result() {
			BigDecimal p = bigOne;
			for (int i = 0; i < dimension; i++) {
				p = p.multiply(bigA[i]).multiply(new BigDecimal(i + 1));
			}
			return calcResult(bigOne, 0).divide(p, MathContext.DECIMAL128).doubleValue();
		}
		
		private BigDecimal calcResult(BigDecimal denominator, int i) {
			if (i == dimension) {
				return bigOne.divide(denominator, MathContext.DECIMAL128);
			} else  {
				return calcResult(denominator, i + 1).subtract(calcResult(denominator.add(bigA[i]), i + 1));
			}
		}
		
		@Override
		public int inputDimension() { return dimension; }
		@Override
		public String toString() { return "Genz Corner Peak"; }
	}

	public class GenzGaussian extends Function {
		private int dimension;
		private double[] a;
		private double[] u;
		private double invResult;
		
		public GenzGaussian(int dimension, double h) {
			this.dimension = dimension;
			a = randomA(dimension, h);
			u = randomU(dimension);
			invResult = 1.0 / result();
		}
		 
		@Override
		public double sEvaluate(double[] x) {
			checkArgument(x);
			
			double t, s = 0.0;
			for (int i = 0; i < dimension; i++) {
				t = a[i] * (x[i] - u[i]);
				s += t * t;
			}
			return Math.exp(-s) * invResult;
		}
		
		private double result() {
			double p = 1.0;
			final double sr2 = Math.sqrt(2.0);
			
			for (int i = 0; i < dimension; i++) {
				p *= (MathTN.gaussian(sr2 * a[i] * (1 - u[i])) - MathTN.gaussian(sr2 * a[i] * -u[i]))
				   * Math.sqrt(Math.PI) / a[i];
			}
			
			return p;
		}
		
		@Override
		public int inputDimension() { return dimension; }
		@Override
		public String toString() { return "Genz Gaussian"; }
	}
	
	public class GenzContinuous extends Function {
		private int dimension;
		private double[] a;
		private double[] u;
		private double invResult;
		
		public GenzContinuous(int dimension, double h) {
			this.dimension = dimension;
			a = randomA(dimension, h);
			u = randomU(dimension);
			invResult = 1.0 / result();
		}
		 
		@Override
		public double sEvaluate(double[] x) {
			checkArgument(x);
			
			double s = 0.0;
			for (int i = 0; i < dimension; i++) {
				s += a[i] * Math.abs(x[i] - u[i]);
			}
			return Math.exp(-s) * invResult;
		}
		
		private double result() {
			double p = 1.0;
			
			for (int i = 0; i < dimension; i++) {
				p *= (2.0 - Math.exp(-a[i] * u[i]) - Math.exp(-a[i] * (1.0 - u[i]))) / a[i];
			}
			
			return p;
		}
		
		@Override
		public int inputDimension() { return dimension; }
		@Override
		public String toString() { return "Genz Continuous"; }
	}
	
	public class GenzDiscontinuous extends Function {
		private int dimension;
		private double[] a;
		private double[] u;
		private double invResult;
		
		public GenzDiscontinuous(int dimension, double h) {
			this.dimension = dimension;
			a = randomA(dimension, h);
			u = randomU(2);
			// Prevent the function from being too singular
			u[0] = 0.05 + 0.95 * u[0];
			u[1] = 0.05 + 0.95 * u[1];
			invResult = 1.0 / result();
		}
		 
		@Override
		public double sEvaluate(double[] x) {
			if (x[0] > u[0] || (dimension >= 2 && x[1] > u[1])) {
				return 0.0;
			}
			else {
				double s = 0.0;
				for (int i = 0; i < dimension; i++) {
					s += a[i] * x[i];
				}
				return Math.exp(s) * invResult;
			}
		}
		
		private double result() {
			double p = 1.0;
			
			for (int i = 0; i < dimension; i++) {
				if (i < 2) {
					p *= (Math.exp(a[i] * u[i]) - 1) / a[i];
				} else {
					p *= (Math.exp(a[i]) - 1) / a[i]; 
				}
			}
			
			return p;
		}
		
		@Override
		public int inputDimension() { return dimension; }
		@Override
		public String toString() { return "Genz Discontinuous"; }
	}
}
