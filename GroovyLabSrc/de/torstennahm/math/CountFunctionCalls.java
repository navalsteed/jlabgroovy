/*
 * Created on Aug 24, 2003
 */
package de.torstennahm.math;


/**
 * This calls wraps around a <code>Function</code>, and counts the number of times
 * its <code>evaluate</code> method is called.
 * <p>
 * According to general <code>Function</code> contract, this class is thread-safe.
 * 
 * @author Torsten Nahm
 */
public class CountFunctionCalls extends Function {
	private Function function;
	private long calls;
	
	/**
	 * Creates the wrapper.
	 * 
	 * @param function Function whose evaluations are to be counted
	 * @throws NullPointerException if function is null
	 */
	public CountFunctionCalls(Function function) {
		if (function == null) {
			throw new NullPointerException("Function may not be null");
		}
		
		this.function = function;
	}

	/**
	 * Restarts counting at 0.
	 */
	synchronized public void resetCalls() {
		calls = 0;
	}
	
	/**
	 * Gets the number of function calls.
	 * 
	 * @return number of functions calls since the object was created
	 * or since the last reset
	 */
	synchronized public long getCalls() {
		return calls;
	}
	
	@Override
	public int inputDimension() {
		return function.inputDimension();
	}

	@Override
	public double sEvaluate(double[] x) {
		synchronized (this) {
			calls++;
		}
		return function.sEvaluate(x);
	}
	
	@Override
	public String toString() {
		return function.toString();
	}
}
