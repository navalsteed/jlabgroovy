/*
 * Created on Mar 30, 2003
 */
package de.torstennahm.integrate.sparse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.torstennahm.integrate.IntegrationFailedException;
import de.torstennahm.integrate.IntegrationInfo;
import de.torstennahm.integrate.IntegrationResult;
import de.torstennahm.integrate.Integrator;
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
import de.torstennahm.math.MathTN;

/**
 * Sparse grid integrator that uses a hybrid of
 * non-adaptive simplicial and adaptive greedy strategies. 
 * 
 * The controller will also intercalate indices from the standard simplex.
 * This increases robustness for difficult functions. The quota of
 * these simplicial indices may be set between 0 and 1. The quota
 * sets the relative amout of simplicial indices to be used. A quota
 * of 1 thus makes the controller default to standard non-adaptive
 * sparse grid integration.
 * 
 * Note that although the valid indices whose contributions have been
 * calculated are not formally part of the index set, their contributions
 * still are used for the integral value, as this can only improve
 * accuracy.
 * 
 * This class is thread-safe.
 * 
 * @author Torsten Nahm
 * 
 * @see de.torstennahm.integrate.Integrator
 * @see de.torstennahm.integrate.IntegrationResult
 * @see de.torstennahm.integrate.sparse.evaluateindex.Evaluator
 * @see de.torstennahm.integrate.visualize.Visualizer
 */

public class EvaluateIntegrator extends Integrator<Evaluator> {
	/**
	 * Factor for balance between greedy and work
	 */
	protected final double simplexQuota;
	
	/**
	 * Constructs the weighted sparse integrator with the default settings.
	 * 
	 * The constructor has the same result as using a simplex quota of 0.2
	 * in <code>EvaluateIntegrator(double)</code>.
	 * 
	 * @see #EvaluateIntegrator(double)
	 */
	public EvaluateIntegrator() {
		this(0.2);
	}
	
	/**
	 * Construct the sparse integrator with the specified simplex quota.
	 * 
	 * For work quota 0, only the calculated contribution for an index is used
	 * (the greedy approach). For work quota 1, only indices in order of
	 * increasing work are used. In many cases, this defaults to
	 * standard non-adaptive sparse grid integration.
	 * 
	 * @param workQuota quota of simplicial indices used, must be between 0 and 1
	 * @throws IllegalArgumentException if the quota is out of range
	 */
	public EvaluateIntegrator(double workQuota) {
		if (! (workQuota >= 0.0 && workQuota <= 1.0)) {
			throw new IllegalArgumentException("Work quota must be between 0 and 1");
		}
		
		this.simplexQuota = workQuota;
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
		protected int currentDimension;
		
		protected HybridManager hybridManager;
		
		protected int higherThanEstimate = 0;
		protected double higherContributions = 0.0;
		
		protected Map<Index, EvalData> indexMap;
		
		protected double errorEstimate;
		protected int primaryIndices;
		protected boolean primaryCompleted;
		
		protected SparseResult result;
		
		InternalIntegrator(Evaluator evaluator, StopCondition condition, List<Visualizer> visualizers) {
			this.evaluator = evaluator;
			this.condition = condition;
			this.visualizers = visualizers;
			dimension = evaluator.dimension();
			
			hybridManager = new HybridManager(simplexQuota);
			indexMap = new HashMap<Index, EvalData>();
			errorEstimate = 0.0;
			primaryCompleted = false;
		}
		
		IntegrationResult integrate() throws IntegrationFailedException { 
			result = new SparseResult();
			Index zeroIndex = new FastIndex();
			
			Visualizers.submitToList(visualizers, new Integrand(evaluator));
			Visualizers.submitToList(visualizers, new StartIntegration());
			
			if (dimension == 0) {
				currentDimension = 1;
			} else {
				currentDimension = dimension;
			}
			
			if (! condition.stop(result)) {
				activateIndex(zeroIndex, Double.POSITIVE_INFINITY);
			}
			
			while (! condition.stop(result)) {
				Index index = hybridManager.nextIndex();
				if (index == null) { 
					throw new IntegrationFailedException("Could not expand index set");
				}
				EvalData evalData = indexMap.get(index);
				
				evalData.completed = true;
				
				double estimate = index.equals(zeroIndex) ? Double.POSITIVE_INFINITY : Math.abs(evalData.contribution);
				boolean couldExpandFully = true;
				for (int i = 0; i < currentDimension; i++) {
					Index succIndex = index.add(i, 1);
					if (isValid(indexMap, succIndex)) {
						if (evaluator.canEvaluate(succIndex)) {
							activateIndex(succIndex, estimate);
						} else {
							couldExpandFully = false;
							IntegrationInfo info = new CouldNotEvaluateInfo(evaluator, succIndex);
							result.supplementalInfo.add(info);
						}
					}
				}
				
				if (dimension == 0 && index.lastEntry() == currentDimension - 1) {
					currentDimension++;
					activateIndex(zeroIndex.set(currentDimension - 1, 1), Double.POSITIVE_INFINITY);
				}
				
				if (couldExpandFully) {
					errorEstimate -= Math.abs(evalData.contribution);
				}
				
				if (! primaryCompleted && index.sum() == 1) {
					primaryIndices++;
					if (dimension == 0) {
						if (primaryIndices == 2) {
							primaryCompleted = true;
						}
					} else {
						if (primaryIndices == dimension) {
							primaryCompleted = true;
						}
					}
				}
				
				if (primaryCompleted) {
					result.errorEstimate = errorEstimate;
				}
				
				Visualizers.submitToList(visualizers, new IndexStatus(index, "expanded"));
			}
			
			result.supplementalInfo.add(new IntegrationInfo("Indices higher than estimate: " + higherThanEstimate +
					" contribution: " + higherContributions));
			
			Visualizers.submitToList(visualizers, new StopIntegration(result));
			
			return result;
		}
		
		private void activateIndex(Index index, double estimate) throws IntegrationFailedException {
			EvalData evalData = new EvalData();
			indexMap.put(index, evalData);
			
			double contribution = evaluator.deltaEvaluate(index);
			int calls = evaluator.pointsForIndex(index);
			evalData.contribution = contribution;
			evalData.calls = calls;
			
			result.value += contribution;
			result.calls += calls;
			errorEstimate += Math.abs(contribution);
			
			if (Math.abs(contribution) > estimate
			&&	Math.abs(contribution) > MathTN.FUDGE * Math.abs(result.value)) {
				Visualizers.submitToList(visualizers, new IndexStatus(index, ">estimate"));
//				errorEstimate = Double.NaN;		// TODO detection of underestimation should temporarily increase the error estimate
				higherThanEstimate++;
				higherContributions += Math.abs(contribution);
			}
			
			hybridManager.enqueue(index, Math.abs(contribution) / calls, calls);
			
			Visualizers.submitToList(visualizers, new NewResult(result));
			Visualizers.submitToList(visualizers, new IndexContribution(index, contribution));
		}
		
		private boolean isCompleted(Index index) {
			EvalData evalData = indexMap.get(index);
			return evalData != null && evalData.completed;
		}
		
		private boolean isValid(Map<Index, EvalData> indexMap, Index index) {
			for (IntEntry entry : index) {
				Index pred = index.add(entry.getNumber(), -1);
				if (! isCompleted(pred)) {
					return false;
				}
			}
			
			return true;
		}
	}
	
	static protected class EvalData {
		protected boolean completed;
		protected double contribution;
		protected long calls;
	}
	
	@Override
	public String toString() {
		return "EvaluateIntegrator with simplex quota " + simplexQuota;
	}
}
