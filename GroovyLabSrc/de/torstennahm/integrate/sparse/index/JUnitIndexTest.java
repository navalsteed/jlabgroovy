/*
 * Created on Jan 2, 2004
 */
package de.torstennahm.integrate.sparse.index;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;
import de.torstennahm.math.IntEntry;
import de.torstennahm.math.MathTN;
import de.torstennahm.series.Series;


/**
 * Tests the index classes.
 * 
 * @author Torsten Nahm
 */
public class JUnitIndexTest extends TestCase {
	/**
	 * Tests FastIndex and StackedIndex.
	 */
	public void testIndex() {
		int dimension = 173;
		testIndexClass(new FastIndex(), dimension);
		testIndexClass(new StackedIndex(), dimension);
	}
	
	private void testIndexClass(Index index, int dimension) {
		int[] testArray = new int[] { 0, 0, 2, -6, -5, 0, 2, 7, 0, 0, 0, 3 };
		int[] array = new int[dimension];
		
		for (int i = 0; i < testArray.length && i < dimension; i++) {
			index = index.set(i, testArray[i]);
		}
		
		for (int j = 0; j < testArray.length && j < dimension; j++) {
			array[j] = testArray[j];
		}
		
		Random r = new Random(12345678);
		
		for (int k = 0; k < 1000; k++) {
			for (int j = 0; j < array.length; j++) {
				assertTrue(index.get(j) == array[j]);
			}

			int hash = 0;
			for (IntEntry entry : index) {
				assertTrue(array[entry.getNumber()] == entry.getValue());
				hash += (entry.getValue() * (entry.getNumber() + 13)) << entry.getNumber();
			}
			assertTrue(hash == index.hashCode());
			
			int p = r.nextInt(dimension);
			int n = (r.nextDouble() < 0.4) ? 0 : r.nextInt();
			index = index.add(p, n - array[p]);
			array[p] = n;
			
			Index comp = (k % 2 == 0) ? (Index) new FastIndex() : (Index) new StackedIndex();
			for (int i = dimension - 1; i >= 0; i--) {
				comp = comp.set(i, array[i]);
			}
			assertTrue(comp.equals(index));
			assertTrue(index.equals(comp));
			
			comp = comp.add(r.nextInt(array.length), -1);
			assertFalse(comp.equals(index));
			assertFalse(index.equals(comp));
		}
	}
	
	/**
	 * Tests <code>IndexGenerator</code>.
	 */
	public void testIndexGenerator() {
		Random r = new Random(12345678);
		
		for (int i = 0; i < 100; i++) {
			int dimension = r.nextInt(8) + 1;
			int start = r.nextInt(4);
			int depth = start + r.nextInt(4);
			Set<Index> indexSet = new HashSet<Index>();
			
			Series<Index> generator = new FlatIndexGenerator(dimension, start, depth);
			int j;
			for (j = 0; generator.hasNext(); j++) {
				Index index = generator.next();
				for (int k = 0; k < dimension; k++) {
					assertTrue(index.get(k) >= 0 && index.get(k) <= depth);
				}
				assertTrue(! indexSet.contains(index));
				indexSet.add(index);
			}
			
			long indices = MathTN.binomial(dimension + depth, depth);
			if (start > 0) {
				indices -= MathTN.binomial(dimension + (start - 1), start - 1);
			}
			assertTrue(j == indices);
		}
	}
	
	/**
	 * Tests <code>WeightedIndexIterator</code>
	 */
	public void testWeightedIndexIterator() {
		final double[] weights1 = { 0.5, 0.8, 2, 3, 4, 4, 20, 21 };
		final double[] weights2 = { 20, 0.8, 4, 2, 21, 4, 3, 0.5 };
		
		double weight;
		int count1, count2;
		
		Series<Index> generator = new WeightedIndexGenerator(new WeightedIndexGenerator.WeightFunction() {
			public double get(int number) {
				return weights1[number];
			}
		}, weights1.length);
		
		weight = 0;
		count1 = 0;
		while (weight <= 22 * (1 + MathTN.FUDGE)) {
			Index index = generator.next();
			double w = indexWeight(index, weights1);
			assertTrue(w * (1 + MathTN.FUDGE) >= weight);
			weight = w;
			count1++;
		}

		generator = new WeightedIndexGenerator(weights2);
		weight = 0;
		count2 = 0;
		while (weight <= 22 * (1 + MathTN.FUDGE)) {
			Index index = generator.next();
			double w = indexWeight(index, weights2);
			assertTrue(w * (1 + MathTN.FUDGE) >= weight);
			weight = w;
			count2++;
		}
		
		assertTrue(count1 == count2);
	}
	
	private double indexWeight(Index index, double[] weights) {
		double weight = 0.0;
		for (IntEntry entry : index) {
			weight += weights[entry.getNumber()] * entry.getValue();
		}
		
		return weight;
	}
}
