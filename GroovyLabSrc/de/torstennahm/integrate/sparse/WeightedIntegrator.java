/*
 * Created on Mar 30, 2003
 */
package de.torstennahm.integrate.sparse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import de.torstennahm.integrate.Integrator;
import de.torstennahm.integrate.IntegrationFailedException;
import de.torstennahm.integrate.IntegrationInfo;
import de.torstennahm.integrate.IntegrationResult;
import de.torstennahm.integrate.StopCondition;
import de.torstennahm.integrate.sparse.evaluateindex.Evaluator;
import de.torstennahm.integrate.sparse.index.FastIndex;
import de.torstennahm.integrate.sparse.index.Index;
import de.torstennahm.integrate.sparse.visualize.IndexContribution;
import de.torstennahm.integrate.sparse.visualize.IndexStatus;
import de.torstennahm.integrate.visualize.Visualizer;
import de.torstennahm.integrate.visualize.Visualizers;
import de.torstennahm.integrate.visualizerdata.Integrand;
import de.torstennahm.integrate.visualizerdata.NewResult;
import de.torstennahm.integrate.visualizerdata.StartIntegration;
import de.torstennahm.integrate.visualizerdata.StopIntegration;
import de.torstennahm.math.IntEntry;

/**
 * Adaptive sparse grid integrator that uses a weighted balance of
 * index contributions and work in expanding the index set.
 * 
 * With the default constructor, the integrator uses a pure greedy approache:
 * It will always add that value to the index set whose calculated contribution is the largest,
 * regardless of the work involved. However, it is possible to balance
 * this against the work involved, by supplying a weight <i>w</i> factor
 * in the constructor. In this case, <i>w</i> balances the calculated contribution
 * against the amount of work involved with the following formula:<br>
 * <i>priority = max((1-w)*relContribution,w*(1/relWork))</i>,<br>
 * where <i>relContribution</i> is the quotient of the calculated contribution
 * for the index and the (current estimate of the) integral value,
 * and <i>relWork</i> is the quotient between the number of function calls required
 * for evaluating the index and those for the zero index.
 * <p>
 * The default is the same as setting <i>w</i> to 0 in the above formula.
 * If on the other hand <i>w</i> is 1,
 * the controller will default to non-adaptive simplicial sparse grid integration,
 * as <i>relWork</i> increases monotonically with the length of the index.
 * <p>
 * Note that although the valid indices whose contributions have been
 * calculated are not formally part of the index set, their contributions
 * still are used for the integral value, as this can only improve
 * accuracy.
 * <p>
 * This class implements the original algorithm for sparse grid integration
 * published in:
 * T. Gerstner and M. Griebel. Dimension-Adaptive Tensor-Product Quadrature. Computing, 71(1):65-87, 2003
 * <p>
 * This class is thread-safe.
 * 
 * @author Torsten Nahm
 * 
 * @see de.torstennahm.integrate.Integrator
 * @see de.torstennahm.integrate.IntegrationResult
 * @see de.torstennahm.integrate.sparse.evaluateindex.Evaluator
 * @see de.torstennahm.integrate.visualize.Visualizer
 */

public class WeightedIntegrator extends Integrator<Evaluator> {
	/**
	 * Factor for balance between greedy and work
	 */
	protected final double workWeight;
	
	/**
	 * Constructs the weighted sparse integrator with the default settings.
	 * 
	 * The default setting is to only use the calculated contribution for each
	 * index, and is the same as using <code>WeightedIntegrator(0)</code>.
	 * 
	 * @see #WeightedIntegrator(double)
	 */
	public WeightedIntegrator() {
		this(0);
	}
	
	/**
	 * Construct the weighted sparse integrator with the specified work weight factor.
	 * 
	 * For work weight 0, only the calculated contribution for an index is used,
	 * for and for weight 1, only the work required for evaluating the index
	 * is used. Values in between these extremes represent
	 * a relative mix of the two strategies.
	 * 
	 * @param workWeight factor for balancing contribution against work; must be between 0 and 1
	 * @throws IllegalArgumentException if the work weight is out of range
	 */
	public WeightedIntegrator(double workWeight) {
		if (! (workWeight >= 0.0 && workWeight <= 1.0)) {
			throw new IllegalArgumentException("Weight must be between 0 and 1");
		}
		
		this.workWeight = workWeight;
	}
	
	@Override
	public IntegrationResult integrate(Evaluator evaluator, StopCondition condition, List<Visualizer> visualizers) throws IntegrationFailedException {
		int dimension = evaluator.dimension();
		
		if (dimension == 0) {
			throw new IntegrationFailedException("Cannot integrate integrands with indefinite dimension");
		}
		
		SparseResult result = new SparseResult();
		
		Queue<IndexData> queue = new PriorityQueue<IndexData>();
		Map<Index, IndexData> indexMap = new HashMap<Index, IndexData>();
		
		Visualizers.submitToList(visualizers, new Integrand(evaluator));
		Visualizers.submitToList(visualizers, new StartIntegration());
		
		Index zeroIndex = new FastIndex();
		
		if (! condition.stop(result)) {
			IndexData indexData = evaluateIndex(zeroIndex, evaluator, result, visualizers);
			indexMap.put(zeroIndex, indexData);
			indexData.priority = 0.0;
			queue.add(indexData);
		}
		
		while (! condition.stop(result)) {
			if(queue.isEmpty()) {
				throw new IntegrationFailedException("Index queue is empty");
			}
			
			IndexData indexData = queue.poll();
			Index index = indexData.index;
			
			indexData.completed = true;
			Visualizers.submitToList(visualizers, new IndexStatus(index, "expanded"));
			
			boolean couldExpandFully = true;
			for (int i = 0; i < dimension; i++) {
				Index succIndex = index.add(i, 1);
				if (isValid(indexMap, succIndex)) {
					if (evaluator.canEvaluate(succIndex)) {
						IndexData succData = evaluateIndex(succIndex, evaluator, result, visualizers);
						indexMap.put(succIndex, succData);
						queue.add(succData);
					} else {
						couldExpandFully = false;
						IntegrationInfo info = new CouldNotEvaluateInfo(evaluator, succIndex);
						result.supplementalInfo.add(info);
					}
				}
			}
			
			if (couldExpandFully) {
				result.errorEstimate -= Math.abs(indexData.contribution);
			}
		}
		
		Visualizers.submitToList(visualizers, new StopIntegration(result));
		
		return result;
	}
	
	private IndexData evaluateIndex(Index index, Evaluator evaluator, SparseResult result, List<Visualizer> visualizers) throws IntegrationFailedException {
		IndexData indexData = new IndexData();
		indexData.index = index;
		
		double contribution = evaluator.deltaEvaluate(index);
		result.calls += evaluator.pointsForIndex(index);
		
		result.value += contribution;
		
		indexData.contribution = contribution;
		indexData.priority = calcPriority(index, contribution, evaluator, result.value);
		
		if (Double.isNaN(result.errorEstimate)) {
			result.errorEstimate = 0.0;
		}
		result.errorEstimate += Math.abs(contribution);
		
		Visualizers.submitToList(visualizers, new NewResult(result));
		Visualizers.submitToList(visualizers, new IndexContribution(index, contribution));
		
		return indexData;
	}
	
	private double calcPriority(Index index, double contribution, Evaluator evaluator, double currentValue) {
		double relResult = Math.abs(contribution/ currentValue);
		double relEvals = 1.0 / evaluator.pointsForIndex(index);
		// Most negative priorities get handled first
		return -Math.max((1 - workWeight) * relResult, workWeight * relEvals); 
	}
	
	private boolean isValid(Map<Index, IndexData> indexMap, Index index) {
		for (IntEntry entry : index) {
			Index pred = index.add(entry.getNumber(), -1);
			IndexData predData = indexMap.get(pred);
			if (predData == null || ! predData.completed) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return "WeightedIntegrator with weight " + workWeight;
	}
	
	static private class IndexData implements Comparable {
		private Index index;
		private double priority;
		private double contribution;
		private boolean completed;
		
		public int compareTo(Object o) {
			return Double.compare(priority, ((IndexData) o).priority);
		}
		
		@Override
		public boolean equals(Object o) {
			return compareTo(o) == 0;
		}
		
		@Override
		public int hashCode() {
			return (int) Double.doubleToLongBits(priority);
		}
	}
}
