/*
 * Created on Jul 6, 2003
 */
package de.torstennahm.integrate.sparse.evaluateindex;

import de.torstennahm.integrate.IntegrationFailedException;
import de.torstennahm.integrate.quadratureformula.DeltaGenerator;
import de.torstennahm.integrate.quadratureformula.Generator;
import de.torstennahm.integrate.sparse.ProductWeightIntegrator;
import de.torstennahm.integrate.sparse.index.Index;
import de.torstennahm.math.Function;

/**
 * Performs index evaluation using delta quadrature formulas generated from a
 * quadrature formula generator.
 * 
 * The delta quadrature formulas are generated based on the given generator,
 * and integration is performed with the tensor product of the delta quadrature formulas.
 * 
 * This class is thread-safe.
 * 
 * @author Torsten Nahm
 */
public class DeltaWeightEvaluator implements Evaluator {
	private final Function function;
	private final Generator generator;
	private final ProductWeightIntegrator integrator;
	
	/** 
	 * Constructs the evaluator.
	 * 
	 * @param function function to be integrated
	 * @param generator quadrature formula generator
	 */
	public DeltaWeightEvaluator(Function function, Generator generator) {
		this.function = function;
		this.generator = generator;
		integrator = new ProductWeightIntegrator(new DeltaGenerator(generator));
	}
	
	public int dimension() {
		return function.inputDimension();
	}

	public double deltaEvaluate(final Index index) throws IntegrationFailedException {
		return integrator.integrateWithIndex(function, index);
	}
	
	public boolean canEvaluate(Index index) {
		return integrator.canIntegrate(index);
	}
	
	public int pointsForIndex(Index index) {
		return integrator.neededEvaluations(index);
	}
	
	@Override
	public String toString() {
		return "DeltaWeightEvaluator with " + generator + " for function " + function;
	}
}
