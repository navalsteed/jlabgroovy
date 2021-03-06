/*
 * Created on Jul 19, 2003
 */
package de.torstennahm.distribution;

import de.torstennahm.math.VectorFunction;

/**
 * Creates Brownian paths from Gassian input using the Brownian walk algorithm.
 * 
 * Specifically, the class converts input with the <i>n</i>-dimensional
 * Gaussian distibution to the distribution
 * of discretized brownian paths with <i>n</i> steps with the specified total variance
 * and starting point <i>x0</i>. The discretized Brownian path is represented by an
 * <i>n+1</i>-dimensional path array <code>x</code>, where <code>x[0]</code> represents
 * the path position at time 0 (this will always be <i>x0</i>),
 * <code>x[1]</code> the path position after step 1,
 * ..., and <code>x[n]</code> the final path position.
 * <p>
 * Algorithmically, the path is generated by beginning with the first point and successively
 * adding random normal increments with the specified unit variance.
 * <p>
 * According to general <code>Function</code> contract, this class is thread-safe.
 * 
 * @author Torsten Nahm
 */

public class BrownianWalk extends VectorFunction  {
	protected final double variance;
	protected final int steps;
	protected final double sigmaPerStep;
	
	/**
	 * Constructs the function.
	 * 
	 * @param steps number of steps of the path
	 * @param variance total variance of the path
	 */		
	public BrownianWalk(double variance, int steps) {
		this.variance = variance;
		this.steps = steps;
		sigmaPerStep = Math.sqrt(variance / steps);
	}
		
	@Override
	public int inputDimension() {
		return steps;
	}
	
	@Override
	public int outputDimension() {
		return steps + 1;
	}
	
	/**
	 * Produces the discretized Brownian path.
	 * 
	 * The discretized Brownian path is represented by an
	 * <i>n+1</i>-dimensional path array <code>x</code>, where <code>x[0]</code> represents
	 * the path position at time 0 (this will always be <i>x0</i>),
	 * <code>x[1]</code> the path position after step 1,
	 * ..., and <code>x[n]</code> the final path position.
	 * 
	 * @param x the <i>n</i>-dimensional gaussian noise
	 * @return the <i>n+1</i>-dimensional discretized path
	 */
	@Override
	public double[] evaluate(double[] x) {
		checkArgument(x);
		double[] path = new double[steps + 1];
		double y = 0.0;

		path[0] = y;
		for (int i = 0; i < steps; i++) {
			y += sigmaPerStep * x[i];
			path[i + 1] = y;
		}
	
		return path;
	}
	
	@Override
	public String toString() {
		return "Brownian Walk(v=" + variance + ",steps=" + steps + ")"; 
	}
}