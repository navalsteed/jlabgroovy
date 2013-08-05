/*
 * Created on Jul 6, 2003
 */
package de.torstennahm.integrate.sparse;

import de.torstennahm.integrate.IntegrationFailedException;
import de.torstennahm.integrate.quadratureformula.Generator;
import de.torstennahm.integrate.quadratureformula.QuadratureFormula;
import de.torstennahm.integrate.sparse.index.Index;
import de.torstennahm.math.Function;
import de.torstennahm.math.IntEntry;

/**
 * Integrates a function by using product weights generated from a one-dimensional
 * quadrature formula generator, with a depth specified seperately for each dimension.
 * <p>
 * This class is a helper class for sparse grid integration. It is not part of
 * the <code>Integrator</code> hierarchy, as it does not support open-ended
 * integration, but only uses a fixed number of points.
 * <p>
 * This class is thread-safe.
 * 
 * @author Torsten Nahm
 */
public class ProductWeightIntegrator {
	final private Generator generator;
	
	/**
	 * Construct the integrator with the specified quadrature formula generator.
	 * If the generator is slow, it should be wrapped in a <code>de.torstennahm.integrate.quadratureformula.GeneratorCache</code>
	 * before passing it to this class.
	 * 
	 * @param generator quadrature formula generator
	 */
	public ProductWeightIntegrator(Generator generator) {
		this.generator = generator;
	}
	
	/**
	 * Integrates the function at the specified index by using the tensor product of one-dimensional
	 * quadrature formulas. The entry <code>index.getEntry(i)</code> will tell
	 * the algorithm to use the quadrature formula returned by
	 * <code>getQuadratureFormulaByLevel(index.getEntry(i))</code> for the
	 * dimension <code>i</code>. In this way, the index specifies the
	 * depth of integration for each dimension.
	 * 
	 * @param index integration index
	 * @return integral value
	 * @throws IntegrationFailedException if an integration error occurs
	 */	
	public double integrateWithIndex(Function function, Index index) throws IntegrationFailedException {
		QuadratureFormula[] qf = new QuadratureFormula[function.inputDimension()];
		
		for (int i = 0; i < qf.length; i++) {
			int level = index.get(i);
			
			if (level > generator.maxLevel()) {
				throw new IntegrationFailedException("Generator does not support level " + level);
			}
			qf[i] = generator.getByLevel(level);
		}
		
		return new InternalIntegrator(function, qf).value;
	}
	
	public double integrateWithNodes(Function function, int[] nodes) throws IntegrationFailedException {
		QuadratureFormula[] qf = new QuadratureFormula[function.inputDimension()];
		
		for (int i = 0; i < qf.length; i++) {
			if (nodes[i] > generator.maxNodes()) {
				throw new IntegrationFailedException("Generator does not support " + nodes[i] + "nodes");
			}
			qf[i] = generator.getByNodes(nodes[i]);
		}
		
		return new InternalIntegrator(function, qf).value;
	}
	
	private class InternalIntegrator {
		Function function;
		Index index;
		int dimension;
		QuadratureFormula[] quadratureFormulas;
		
		double[] x;
		double value;
			
		InternalIntegrator(Function function, QuadratureFormula[] qf) {
			this.function = function;
			this.quadratureFormulas = qf;
			
			dimension = function.inputDimension();
			
			x = new double[dimension];
			
			value = DoIntegrate(0);
		}
		
		double DoIntegrate(int entryNum) {
			double value;
		
			if (entryNum == dimension) {
				value = function.sEvaluate(x);
			}
			else {
				QuadratureFormula w = quadratureFormulas[entryNum];
			
				value = 0.0;
				int size = w.getSize();
				for (int i = 0; i < size; i++) {
					x[entryNum] = w.getNode(i);
					value += DoIntegrate(entryNum + 1) * w.getWeight(i);
				}
			}
		
			return value;
		}
	}
	
	/**
	 * Returns whether integration is possible with the given index.
	 * 
	 * @param index
	 * @return <code>true</code> if integration with this index is possible
	 */
	public boolean canIntegrate(Index index) {
		int max = generator.maxLevel();
		if (max != -1) {
			for (IntEntry entry : index) {
				if (entry.getValue() > max) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Returns the number of evaluations of the function needed for evaluation
	 * of the specified index.
	 * 
	 * @param index integration index
	 * @return number of evaluations; <tt>0</tt> is returned if the integration with this index is not possible
	 */
	public int neededEvaluations(Index index) {
		int evals = 1;
		int max = generator.maxLevel();
		
		for (IntEntry entry : index) {
			int value = entry.getValue();
			if (max != -1 && value > max) {
				evals = 0;
			} else {
				evals *= generator.getByLevel(value).getSize();
			}
		}
		
		return evals;
	}
	
	public int neededEvaluations(int[] nodes) {
		int evals = 1;
		int max = generator.maxNodes();
		
		for (int num : nodes) {
			if (max != -1 && num > max) {
				evals = 0;
			} else {
				evals *= generator.getByNodes(num).getSize();
			}
		}
		
		return evals;
	}
	
	@Override
	public String toString() {
		return "ProductWeightIntegrator with " + generator;
	}
}
