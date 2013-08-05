/*
 * Created on Mar 31, 2004
 */
package de.torstennahm.statistics;

/**
 * Implements an online algorithm for solving the linear regression with
 * least squares.
 * 
 * This class is thread-safe.
 * 
 * @author Torsten Nahm
 */
public class LinearRegression {
	private double n;
	private double sumX, sumY;
	private double sumX2, sumY2, sumXY;
	
	/**
	 * Add a pair (x,y).
	 * 
	 * @param x x value of the pair
	 * @param y y value of the pair
	 */
	synchronized public void add(double x, double y) {
		add(x, y, 1.0);
	}
	
	/**
	 * Adds a value pair (x,y) with the specified weight.
	 * <code>add(1,7,3)</code> yields the same result as calling <code>add(1,7)</code>
	 * 3 times. However, weight may be any real number, and may even be negative.
	 * 
	 * @param x x value of the pair
	 * @param y y value of the pair
	 * @param weight for the value pair
	 */
	synchronized public void add(double x, double y, double weight) {
		sumX += x * weight;
		sumY += y * weight;
		sumX2 += x * x * weight;
		sumY2 += y * y * weight;
		sumXY += x * y * weight;
		n += weight;
	}
	
	/**
	 * Returns the slope of the linear regression.
	 * 
	 * @return slope
	 */
	synchronized public double slope() {
		return (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
	}
	
	/**
	 * Returns the y intercept of the linear regression.
	 * It is equal to <code>getY(0)</code>.
	 * 
	 * @return y intercept
	 */
	synchronized public double yIntercept() {
		return yValue(0.0);
	}
	
	/**
	 * Returns the residual for the linear regression.
	 * 
	 * @return residual
	 */
	synchronized public double residual() {
		double a = yIntercept();
		double b = slope();
		return n * a * a + b * b * sumX2 + sumY2 + 2 * a * (b * sumX - sumY) - 2 * b * sumXY;
	}
	
	synchronized public double correlation() {
		return (n * sumXY - sumX * sumY) / Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));
	}
	
	/**
	 * Returns the y value of the linear regression at the given
	 * x coordinate.
	 * 
	 * @param x x value
	 * @return y value of the linear regression line at x
	 */
	synchronized public double yValue(double x) {
		return sumY / n + (x - sumX / n) *  slope();
	}
}