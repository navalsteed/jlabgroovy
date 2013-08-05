/*
 * Created on Dec 12, 2003
 */
package de.torstennahm.statistics;

/**
 * Provides basic online statistical information.
 * 
 * Information is provided online as new values are added. This includes minimum,
 * maximum, average and variance.
 * <p>
 * This class is thread-safe.
 * 
 * @author Torsten Nahm
 */
public class Statistics {
	private double n;
	private double sum, squaresSum;
	private double min, max;
	
	/**
	 * Creates a new instance.
	 */
	public Statistics() {
		reset();
	}
	
	/**
	 * Resets all data, returning this object to its inital state.
	 */
	synchronized public void reset() {
		n = 0.0;
		sum = squaresSum = 0.0;
		min = max = Double.NaN;
	}
	
	/**
	 * Adds a value to this object.
	 * 
	 * @param d value to be submitted
	 */
	synchronized public void add(double d) {
		add(d, 1.0);
	}
	
	/**
	 * Adds a value to this object with the specified weight.
	 * 
	 * <code>add(d, 3.0)</code> is equivalent to calling <code>add(d)</code> three
	 * times. Non-integer weights and negative weights are also supported.
	 * <p>
	 * Note that the minimum and maximum are defined as the minimum and
	 * maximum over all added values, regardless of the weight. This means
	 * that for a new object, the sequence <code>add(-33,0.0)</code>, <code>add(12)</code>,
	 * <code>add(12,-1)</code> will result in an average of -33, a variance
	 * of 0, a minimum of -33 and a maximum of 12.
	 * 
	 * @param d value to be submitted
	 * @param weight weighting of the submitted value
	 */
	synchronized public void add(double d, double weight) {
		sum += d * weight;
		squaresSum += d * d * weight;
		n += weight;
		if (Double.isNaN(min) || d < min) {
			min = d;
		}
		if (Double.isNaN(max) || d > max) {
			max = d;
		}
	}
	
	/**
	 * Returns the number of values added so far.
	 * 
	 * @return number of submitted values
	 */
	synchronized public double n() {
		return n;
	}
	
	/**
	 * Returns the average of the submitted values.
	 * 
	 * @return average of the values, or Double.NaN if n() == 0.0
	 */
	synchronized public double average() {
		return sum / n;
	}
	
	/**
	 * Returns the minimum of the submitted values.
	 * 
	 * @return minimum of the values, or Double.NaN if none has been yet added
	 */
	synchronized public double minimum() {
		return min;
	}
	
	/**
	 * Returns the maximum of the submitted values.
	 * 
	 * @return maximum of the values, or Double.NaN if none has been yet added
	 */
	synchronized public double maximum() {
		return max;
	}
	
	/**
	 * Returns the variance of the submitted values.
	 * 
	 * @return variance of the values, or Double.NaN if n() == 0.0
	 */
	synchronized public double variance() {
		return squaresSum / n - (sum / n) * (sum / n);
	}
		
	/**
	 * Returns the square root of the variance of the submitted values.
	 * 
	 * @return variance of the values, or Double.NaN if sigma does not exist
	 */
	synchronized public double sigma() {
		return Math.sqrt(variance());
	}
}
