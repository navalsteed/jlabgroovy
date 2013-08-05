/*
 * Created on Jun 11, 2003
 */
package de.torstennahm.integrate.sparse;


import java.util.List;

import de.torstennahm.integrate.Integrator;
import de.torstennahm.integrate.IntegrationFailedException;
import de.torstennahm.integrate.IntegrationResult;
import de.torstennahm.integrate.StopCondition;
import de.torstennahm.integrate.sparse.evaluateindex.Evaluator;
import de.torstennahm.integrate.sparse.index.Index;
import de.torstennahm.integrate.sparse.index.FlatIndexGenerator;
import de.torstennahm.integrate.sparse.visualize.IndexContribution;
import de.torstennahm.integrate.visualize.Visualizer;
import de.torstennahm.integrate.visualize.Visualizers;
import de.torstennahm.integrate.visualizerdata.Integrand;
import de.torstennahm.integrate.visualizerdata.NewResult;
import de.torstennahm.integrate.visualizerdata.StartIntegration;
import de.torstennahm.integrate.visualizerdata.StopIntegration;

/**
 * Performs integration using the simplicial (non-adaptive) sparse grid algorithm.
 * 
 * @author Torsten Nahm
 */
public class SimplexIntegrator extends Integrator<Evaluator> {
	@Override
	public IntegrationResult integrate(Evaluator evaluator, StopCondition condition, List<Visualizer> visualizers) throws IntegrationFailedException {
		double lastValue;
		
		SparseResult result = new SparseResult();
		
		FlatIndexGenerator indexGenerator = new FlatIndexGenerator(evaluator.dimension());
		
		Visualizers.submitToList(visualizers, new Integrand(evaluator));
		Visualizers.submitToList(visualizers, new StartIntegration());
		
		lastValue = Double.NaN;
		int lastLevel = 0;
		while (! condition.stop(result)) {
			Index index = indexGenerator.next();
			
			int level = index.sum();
			if (level > lastLevel) {
				result.errorEstimate = Math.abs(result.value - lastValue);
				lastValue = result.value;
				lastLevel = level;
			}
			
			double contribution = evaluator.deltaEvaluate(index);
			result.value += contribution;
			result.calls += evaluator.pointsForIndex(index);
			
			Visualizers.submitToList(visualizers, new IndexContribution(index, contribution));
			Visualizers.submitToList(visualizers, new NewResult(result));
		}
		
		Visualizers.submitToList(visualizers, new StopIntegration(result));
		
		return result;
	}
	
	@Override
	public String toString() {
		return "SimplexIntegrator";
	}
}