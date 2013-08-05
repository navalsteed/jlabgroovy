/*
 * Created on Oct 14, 2004
 */
package de.torstennahm.math;

/**
 * @author Torsten Nahm
 */
public class AffineTransform extends Function {
	private double factor;
	private double offset;
	
	public AffineTransform(double factor) {
		this(factor, 0.0);
	}
	
	public AffineTransform(double factor, double offset) {
		this.factor = factor;
		this.offset = offset;
	}
	
	@Override
	public double sEvaluate(double[] x) {
		checkArgument(x);
		return factor * x[0] + offset;
	}

	@Override
	public int inputDimension() {
		return 1;
	}
	
	@Override
	public String toString() {
		return "x->" + factor + "*x+" + offset;
	}
}
