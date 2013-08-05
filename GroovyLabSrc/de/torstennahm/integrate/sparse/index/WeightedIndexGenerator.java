/*
 * Created on Sep 11, 2004
 */
package de.torstennahm.integrate.sparse.index;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import de.torstennahm.series.Series;

/**
 * Iterates over all indices with a given dimension, or with infinite dimension.
 * 
 * All indices are returned exactly once. The exact order in which indices are provided is not
 * specified. However, indices are returned so that for each index provided, all
 * smaller indices (with regard to the canonical partial ordering of indices)
 * have been returned before it. In addition, for finite dimension,
 * it is also guaranteed that the length of the returned indices (that is,
 * the sum of their components) increases monotonously. This is not possible
 * for infinite dimension.
 * <p>
 * This class is <i>not</i> thread-safe.
 * 
 * @author Torsten Nahm
 */
public class WeightedIndexGenerator implements Series<Index> {
	boolean useArray;
	private final double [] weights;
	private final WeightFunction weightFunction;
	private final int dimension;
	
	private PriorityQueue<IndexWeight> queue = new PriorityQueue<IndexWeight>();
	private double startWeight, endWeight;
	
	public WeightedIndexGenerator() {
		this(new WeightedIndexGenerator.WeightFunction() {
			public double get(int number) {
				return 1 << number;
			}
		}, 0);
	}
	
	public WeightedIndexGenerator(double[] weights) {
		if (weights.length == 0) {
			throw new IllegalArgumentException("Weight array must have at least length 1");
		}
		for (double w : weights) {
			if (w <= 0) {
				throw new IllegalArgumentException("Weights must be strictly positive");
			}
		}
		
		this.weights = weights.clone();
		dimension = weights.length;
		weightFunction = null;
		useArray = true;
		
		init();
	}
	
	public WeightedIndexGenerator(WeightFunction weightFunction, int dimension) {
		this.dimension = dimension;
		this.weightFunction = weightFunction;
		weights = null;
		useArray = false;
		
		init();
	}
	
	private void init() {
		startWeight = 0.0;
		endWeight = startWeight + (useArray ? weights[0] : weightFunction.get(0));
	}
	
	public boolean hasNext() {
		return true;
	}
	
	public Index next() {
		while (queue.isEmpty()) {
			fillQueue();
			
			startWeight = endWeight;
			endWeight = startWeight + (useArray ? weights[0] : weightFunction.get(0));
		}
		
		return queue.poll().index;
	}
	
	private void fillQueue() {
		List<IndexWeight> list = new LinkedList<IndexWeight>();
		if (useArray) {
			recurseWithArray(list, 0, new FastIndex(), 0.0);
		} else {
			recurseWithFunction(list, 0, new FastIndex(), 0.0);
		}
		queue = new PriorityQueue<IndexWeight>(list);
	}
	
	private void recurseWithFunction(List<IndexWeight> list, int number, Index index, double weightUsed) {
		if (dimension != 0 && number == dimension) {
			if (weightUsed >= startWeight) {
				list.add(new IndexWeight(index, weightUsed));
			}
		} else {
			double weight = weightFunction.get(number);
			
			if (weightUsed + weightFunction.get(number) >= endWeight) {
				if (weightUsed >= startWeight) {
					list.add(new IndexWeight(index, weightUsed));
				}
			} else {
				while (weightUsed < endWeight) {
					recurseWithFunction(list, number + 1, index, weightUsed);
					index = index.add(number, 1);
					weightUsed += weight;
				}
			}
		}
	}
	
	private void recurseWithArray(List<IndexWeight> list, int number, Index index, double weightUsed) {
		if (number == dimension) {
			if (weightUsed >= startWeight) {
				list.add(new IndexWeight(index, weightUsed));
			}
		} else {
			double weight = weights[number];
			
			while (weightUsed < endWeight) {
				recurseWithArray(list, number + 1, index, weightUsed);
				index = index.add(number, 1);
				weightUsed += weight;
			}
		}
	}
	
	private static class IndexWeight implements Comparable {
		Index index;
		double weight;
		
		IndexWeight(Index index, double weight) {
			this.index = index;
			this.weight = weight;
		}
		
		public int compareTo(Object o) {
			return Double.compare(weight, ((IndexWeight) o).weight);
		}
	}
	
	public interface WeightFunction {
		double get(int number);
	}
}
