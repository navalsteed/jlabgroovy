/*
 * Created on Mar 30, 2003
 */
package de.torstennahm.math;


/**
 * Models a mathematical function. The function is a mapping
 * from <i>R^d</i> to <i>R</i>, where
 * <i>R</i> is the real numbers, and <i>d</i> the dimension of the function.
 * The function's dimension <i>d</i> is fixed for any one instance, but may
 * vary between instantiations, for example depending on the constructor.
 * The dimension is obtained by the method <code>getDimension</code>.
 * <p>
 * All subclasses are required to be thread-safe.
 * <p>
 * Implementor's note: The protected method <code>checkArgument</code> may
 * be used in <code>evaluate(double[] x)</code> to validate the argument <code>x</code>.
 * 
 * @author Torsten Nahm
 */
public abstract class Function extends VectorFunction {
	@Override
	public int outputDimension() {
		return 1;
	}
	
	/**
	 * Evaluates the function at the specified point.
	 * 
	 * Implementor's note: You must check for the correct size of x.
	 * The protected method <code>checkArgument</code> may be used for this purpose.
	 * 
	 * @param x the point in R^d where the function is to be evaluated
	 * @return the value of the function at x
	 * @throws IllegalArgumentException if <code>x.length</code> != dimension of the function
	 */
	abstract public double sEvaluate(double[] x);
	
	@Override
	public double[] evaluate(double[] x) {
		return new double[] { sEvaluate(x) };
	}
	
	/**
	 * Returns a new function representing the composition of this vector function with
	 * a vector function. This function (the left function) forms the left
	 * and the vector function <code>rightFunction</code> the right term
	 * of the composition. Accordingly, the composition function
	 * will have the input dimension of <code>rightFunction</code>.
	 * 
	 * @param rightFunction vector function to compose with
	 * @return a new function representing the composition of the two functions
	 * @throws IllegalArgumentException if dimension of this function != output dimension of rightFunction
	 * @throws NullPointerException if the argument is null
	 */
	public Function sCompose(final VectorFunction rightFunction) {
		final Function leftFunction = this;
		
		if (leftFunction.inputDimension() != rightFunction.outputDimension()) {
			throw new IllegalArgumentException("Function dimension are incompatible");
		}
		
		return new Function() {
			@Override
			public int inputDimension() {
				return rightFunction.inputDimension();
			}
			@Override
			public double sEvaluate(double[] x) {
				return leftFunction.sEvaluate(rightFunction.evaluate(x));
			}
			@Override
			public String toString() {
				return leftFunction + " after " + rightFunction;
			}
		};
	}
}