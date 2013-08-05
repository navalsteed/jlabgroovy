/*
 * Created on Mar 30, 2003
 */
package de.torstennahm.integrate.sparse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.torstennahm.integrate.Integrator;
import de.torstennahm.integrate.IntegrationFailedException;
import de.torstennahm.integrate.IntegrationInfo;
import de.torstennahm.integrate.IntegrationResult;
import de.torstennahm.integrate.StopCondition;
import de.torstennahm.integrate.error.ErrorEstimator;
import de.torstennahm.integrate.error.FastConvergenceEstimator;
import de.torstennahm.integrate.sparse.evaluateindex.Evaluator;
import de.torstennahm.integrate.sparse.index.FastIndex;
import de.torstennahm.integrate.sparse.index.Index;
import de.torstennahm.integrate.sparse.visualize.IndexContribution;
import de.torstennahm.integrate.sparse.visualize.IndexContributionEstimate;
import de.torstennahm.integrate.sparse.visualize.IndexStatus;
import de.torstennahm.integrate.visualize.Visualizer;
import de.torstennahm.integrate.visualize.Visualizers;
import de.torstennahm.integrate.visualizerdata.Integrand;
import de.torstennahm.integrate.visualizerdata.NewResult;
import de.torstennahm.integrate.visualizerdata.StartIntegration;
import de.torstennahm.integrate.visualizerdata.StopIntegration;
import de.torstennahm.math.IntEntry;
import de.torstennahm.math.MathTN;

/**
 * Sparse grid integrator that uses a hybrid of
 * non-adaptive simplicial strategy and adaptive estimation of the index contributions. 
 * 
 * From all valid indices, the one with the highest expected contribution
 * will be added to the index set in each step. The expected contribution
 * is obtained as an estimate from the contributions of the direct
 * predecessors. Either the geometric average, the maximum or the
 * minimum of these contributions can be used as an estimate.
 * <p>
 * The controller will also intercalate indices from the standard simplex.
 * This increases robustness for difficult functions. The quota of
 * these simplicial indices may be set between 0 and 1. The quota
 * sets the relative amout of simplicial indices to be used. A quota
 * of 1 thus makes the controller default to standard non-adaptive
 * sparse grid integration.
 * <p>
 * For the error estimate, the sum of the absolute values of the contributions
 * of all those indices in the index set is used that do not have any
 * forward neighbors in the index set.
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

public class EstimateIntegrator extends Integrator<Evaluator> {
	/**
	 * Averaging mode for forward error estimation.
	 */
	static public final int GEOMETRIC = 0, MAXIMUM = 1, MINIMUM = 2;
	
	/**
	 * Averaging mode used for forward error estimation
	 */
	protected final int mode;
	
	/**
	 * Quotient of simplicial indices to use
	 */
	protected final double simplexQuota;
	
	protected final Index zeroIndex = new FastIndex();
	
	/**
	 * Constructs the estimate sparse integrator with the default settings.
	 * 
	 * The constructor has the same result as using a simplex quota of 0.2
	 * in <code>EstimateIntegrator(double, int)</code> and geometric
	 * estimation.
	 * 
	 * @see #EstimateIntegrator(double, int)
	 */
	public EstimateIntegrator() {
		this(0.5, GEOMETRIC);
	}
	
	/**
	 * Construct the estimate sparse integrator with the specified simplex quota.
	 * 
	 * For simplex quota 0, only the estimated contribution for an index is used
	 * as priority.
	 * For simplex quota 1, only simplicial indices
	 * are used, and the integrator defaults to standard non-adaptive sparse
	 * grid integration.
	 * 
	 * @param simplexQuota quota of simplicial indices used, must be between 0 and 1
	 * @param mode estimate mode, one of GEOMETRIC, MAXIMUM, MINIMUM
	 * @throws IllegalArgumentException if the quota is out of range
	 */
	public EstimateIntegrator(double simplexQuota, int mode) {
		if (! (simplexQuota >= 0.0 && simplexQuota <= 1.0)) {
			throw new IllegalArgumentException("Simplex quota must be between 0 and 1");
		}
		
		this.simplexQuota = simplexQuota;
		this.mode = mode;
	}
	
	@Override
	public IntegrationResult integrate(Evaluator evaluator, StopCondition condition, List<Visualizer> visualizers) throws IntegrationFailedException {
		InternalIntegrator internalIntegrator = new InternalIntegrator(evaluator, condition, visualizers);
		
		return internalIntegrator.integrate();
	}
	
	protected class InternalIntegrator {
		protected final Evaluator evaluator;
		protected final StopCondition condition;
		protected final List<Visualizer> visualizers;
		protected final int dimension;
		
		protected SparseResult result;
		
		protected HybridManager hybridManager;
		
		protected ErrorEstimator estimator;
		
		protected Map<Index, EstimateData> indexMap;
		
		protected int higherThanEstimate;
		protected double higherContributions;
		
		InternalIntegrator(Evaluator evaluator, StopCondition condition, List<Visualizer> visualizers) throws IntegrationFailedException {
			this.evaluator = evaluator;
			this.condition = condition;
			this.visualizers = visualizers;
			dimension = evaluator.dimension();
			
			if (dimension == 0) {
				throw new IntegrationFailedException("Cannot integrate integrands with indefinite dimension");
			}
			
			hybridManager = new HybridManager(simplexQuota);
			indexMap = new HashMap<Index, EstimateData>();
		}
		
		IntegrationResult integrate() throws IntegrationFailedException { 
			result = new SparseResult();
			estimator = new FastConvergenceEstimator();

			Visualizers.submitToList(visualizers, new Integrand(evaluator));
			Visualizers.submitToList(visualizers, new StartIntegration());
			
			activateIndex(zeroIndex);
			
			while (! condition.stop(result)) {
				Index index = hybridManager.nextIndex();
				if (index == null) { 
					throw new IntegrationFailedException("Could not expand index set");
				}
				
				EstimateData indexData = indexMap.get(index);
				
				if (indexData.calls != 0) {
					indexData.contribution = evaluateIndex(index);
					indexData.completed = true;
					
					if (Math.abs(indexData.contribution) > indexData.estimate
					&&	Math.abs(indexData.contribution) > MathTN.FUDGE * Math.abs(result.value)) {
						higherThanEstimate++;
						higherContributions += Math.abs(indexData.contribution);
						Visualizers.submitToList(visualizers, new IndexStatus(index, ">estimate"));
					}
					
					estimator.log(result.calls, result.value);
					result.errorEstimate = estimator.getEstimate();
					
					for (int i = 0; i < dimension; i++) {
						Index succIndex = index.add(i, 1);
						if (isValid(succIndex)) {
							activateIndex(succIndex);
						}
					}
				} else {
					IntegrationInfo info = new CouldNotEvaluateInfo(evaluator, index);
					result.supplementalInfo.add(info);
				}
			}
			
			result.supplementalInfo.add(new IntegrationInfo("Indices higher than estimate: " + higherThanEstimate +
										" contribution: " + higherContributions));
			
			Visualizers.submitToList(visualizers, new StopIntegration(result));
			
			return result;
		}
		
		private void activateIndex(Index index) {
			EstimateData data = new EstimateData();
			data.estimate = calcEstimate(index);
			data.calls = evaluator.pointsForIndex(index);
			indexMap.put(index, data);
			hybridManager.enqueue(index, data.estimate / data.calls, data.calls);
			
			Visualizers.submitToList(visualizers, new IndexContributionEstimate(index, data.estimate));
		}
		
		private boolean isValid(Index index) {
			for (IntEntry entry : index) {
				Index pred = index.add(entry.getNumber(), -1);
				if (! isCompleted(pred)) {
					return false;
				}
			}
			
			return true;
		}
		
		private boolean isCompleted(Index index) {
			EstimateData indexData = indexMap.get(index);
			return indexData != null && indexData.completed;
		}
		
		private double evaluateIndex(Index index) throws IntegrationFailedException {
			double contribution = evaluator.deltaEvaluate(index);
			result.value += contribution;
			result.calls += evaluator.pointsForIndex(index);
			
			Visualizers.submitToList(visualizers, new IndexContribution(index, contribution));
			Visualizers.submitToList(visualizers, new NewResult(result));
			
			return contribution;
		}
		
		protected double calcEstimate(Index index) {
			if (index.equals(zeroIndex)) {
				return Double.POSITIVE_INFINITY;
			}
			
			if (mode == GEOMETRIC) {
				double logSum = 0.0;
				
				int entries = 0;
				for (Iterator<IntEntry> iter = index.iterator(); iter.hasNext(); entries++) {
					IntEntry entry = iter.next();
					Index pred = index.add(entry.getNumber(), -1);
					
					EstimateData predData = indexMap.get(pred);
					logSum += Math.log(Math.abs(predData.contribution));
				}
				
				return Math.exp(logSum / entries);
			} else if (mode == MINIMUM) {
				double estimate = Double.POSITIVE_INFINITY;
				
				for (IntEntry entry : index) {
					Index pred = index.add(entry.getNumber(), -1);
					EstimateData predData = indexMap.get(pred);
					estimate = Math.min(Math.abs(predData.contribution), estimate);
				}
				
				return estimate;
			} else if (mode == MAXIMUM) {
				double estimate = 0.0;
				
				for (IntEntry entry : index) {
					Index pred = index.add(entry.getNumber(), -1);
					EstimateData predData = indexMap.get(pred);
					estimate = Math.max(Math.abs(predData.contribution), estimate);
				}
				
				return estimate;
			} else {
				throw new RuntimeException("Internal state error");
			}
		}
	}
	
	@Override
	public String toString() {
		return "EstimateIntegrator with simplex quota " + simplexQuota;
	}
	
	static protected class EstimateData {
		protected boolean completed;
		protected double estimate;
		protected double contribution;
		protected long calls;
	}
}
