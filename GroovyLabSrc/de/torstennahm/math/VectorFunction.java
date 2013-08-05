/*
 * Created on Jul 3, 2004
 */
package de.torstennahm.math;

/**
 * Models a vector-valued mathematical function.
 * 
 * Mathematically, the function is a mapping from <i>R^d</i> to <i>R^m</i>, where
 * <i>R</i> is the real numbers, and <i>d</i> the dimension of domain of the function,
 * and <i>m</i> is the dimension of the range.
 * The function's dimensions <i>d</i> and <i>m</i> are fixed for any one instance, but may
 * vary between instantiations, for example depending on the constructor.
 * <p>
 * Implementor's note: The protected method <code>checkArgument</code> may
 * be used in <code>vectorEvaluate(double[] x)</code> to validate the argument <code>x</code>.
 * <p>
 * All subclasses are required to be thread-safe.
 * 
 * @author Torsten Nahm
 */
public abstract class VectorFunction {
	/**
	 * Returns the dimension of the domain of the function.
	 * 
	 * @return dimension of the domain
	 */
	public abstract int inputDimension();
	
	/**
	 * Returns the dimension of the range of the function.
	 * 
	 * @return dimension of the range
	 */
	public abstract int outputDimension();
	
	/**
	 * Returns the vector result of evaluating the function at the
	 * specified point.
	 * 
	 * @param x point to evaluate at
	 * @return result vector
	 * @throws IllegalArgumentException if x.length != dimension of the function
	 */
	public abstract double[] evaluate(double[] x);
	
	/**
	 * This is an internal routine which can be used to check whether
	 * the argument to <code>vectorEvaluate</code> is proper.
	 * 
	 * @param x array to check
	 */
	protected void checkArgument(double[] x) {
		if (x.length != inputDimension()) {
			throw new IllegalArgumentException("Array size does not match function input dimension");
		}
	}
	
	/**
	 * Returns a new function representing the composition of this vector function with
	 * another vector function. This vector function (the left function) forms the left
	 * and <code>rightFunction</code> the right term
	 * of the composition. Accordingly, the composition vector function
	 * will have the output dimension of the this function and
	 * the input dimension of <code>rightFunction</code>.
	 * 
	 * @param rightFunction vector function to compose with
	 * @return a new function representing the composition of the two functions
	 * @throws IllegalArgumentException if input dimension of this function != output dimension of rightFunction
	 * @throws NullPointerException if the argument is null
	 */
	public VectorFunction compose(final VectorFunction rightFunction) {
		final VectorFunction leftFunction = this;
		
		if (leftFunction.inputDimension() != rightFunction.outputDimension()) {
			throw new IllegalArgumentException("Function dimension are incompatible");
		}
		
		return new VectorFunction() {
			@Override
			public int inputDimension() {
				return rightFunction.inputDimension();
			}
			@Override
			public int outputDimension() {
				return leftFunction.outputDimension();
			}
			@Override
			public double[] evaluate(double[] x) {
				return leftFunction.evaluate(rightFunction.evaluate(x));
			}
			@Override
			public String toString() {
				return leftFunction + " after " + rightFunction;
			}
		};
	}
}
