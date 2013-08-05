/*
 * Created on Jul 11, 2004
 */
package de.torstennahm.integrate.sparse.evaluateindex;

import de.torstennahm.integrate.IntegrationFailedException;
import de.torstennahm.integrate.sparse.index.Index;

/**
 * Calculates the integral contribution of a function for a multi-index.
 * 
 * The index evaluator also keeps track of how often the integrand
 * function has been called as a result of index evaluation.
 * 
 * @author Torsten Nahm
 */
public interface Evaluator  {
	/**
	 * Returns the dimension with which this index evaluator works. The value
	 * 0 means the dimension is indeterminate.
	 * 
	 * @return dimension for the index evaluator
	 */
	int dimension();
	
	/**
	 * Evaluates the integration function for the given integration index.
	 * 
	 * @param index integration index
	 * @return index contribution
	 * @throws IntegrationFailedException if an integration error occurs
	 */
	double deltaEvaluate(Index index) throws IntegrationFailedException;
	
	/**
	 * Returns whether the index evaluator can evaluate the given index.
	 * For example, if an underlying quadrature formula generator does not
	 * support the required number of nodes, evaluation of the index
	 * is not possible. Calling <code>evaluateIndex</code> on an index
	 * for which this method returns <code>false</code> will produce an
	 * <code>IntegrationFailedException</code>.
	 * This method returns <code>false</code> exactly
	 * if <code>neededEvaluations</code> returns 0.
	 * 
	 * @param index index
	 * @return <code>true</code> if the index can be evaluated
	 */
	boolean canEvaluate(Index index);

	/**
	 * Returns the number of points the function needs to
	 * be evaluated at for this index.
	 * 
	 * @param index integration index
	 * @return number of function calls; 0 is returned if the evaluation of this index is not possible
	 */
	int pointsForIndex(Index index);
}