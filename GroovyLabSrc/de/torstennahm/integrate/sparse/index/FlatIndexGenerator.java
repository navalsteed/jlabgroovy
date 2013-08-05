/*
 * Created on Sep 11, 2004
 */
package de.torstennahm.integrate.sparse.index;

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.torstennahm.math.IntEntry;
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
 * This class is NOT thread-safe.
 * 
 * @author Torsten Nahm
 */
public class FlatIndexGenerator implements Series<Index> {
	private final int dimension;
	
	private int currentLength;
	private int maxLength;
	private int left;
	private Index subIndex;
	private Index next;
	private boolean end;
	
	/**
	 * Creates the index iterator.
	 * 
	 * @param dimension dimension of the indices
	 */
	public FlatIndexGenerator(int dimension) {
		this(dimension, 0, -1);
	}
	
	/**
	 * Creates the index generator with specified minimum and maximum length.
	 * 
	 * @param dimension
	 * @param minLength start length of indices
	 * @param maxLength maximum length of indices, -1 for infinite
	 */
	public FlatIndexGenerator(int dimension, int minLength, int maxLength) {
		if (dimension <= 0) {
			throw new IllegalArgumentException("dimension must be greater than 0");
		}
		if (minLength < 0) {
			throw new IllegalArgumentException("minimum length must be non-negative");
		}
		if (maxLength < -1) {
			throw new IllegalArgumentException("maximum length must be non-negative or -1");
		}
		if (maxLength >= 0 && maxLength < minLength) {
			throw new IllegalArgumentException("maximum length must be at least as large as minimum length");
		}
		
		this.dimension = dimension;
		currentLength = minLength;
		this.maxLength = maxLength;
		
		prepareFirst();
	}
	
	private void prepareFirst() {
		left = currentLength;
		subIndex = new FastIndex();
		next = subIndex.set(0, left);
	}
	
	private void prepareNext() {
		boolean lengthFinished = false;
		
		if (left > 0) {
			if (dimension == 1) {
				lengthFinished = true;
			} else {
				subIndex = subIndex.add(1, 1);
				left--;
			}
		} else {
			Iterator<IntEntry> iter = subIndex.iterator();
			if (iter.hasNext()) {
				IntEntry entry = iter.next();
				int num = entry.getNumber();
				if (num + 1 == dimension) {
					lengthFinished = true;
				} else {
					left += entry.getValue() - 1;
					subIndex = subIndex.set(num, 0);
					subIndex = subIndex.add(num + 1, 1);
				}
			} else {
				lengthFinished = true;
			}
		}
		
		if (lengthFinished) {
			currentLength++;
			if (maxLength >= 0 && currentLength > maxLength) {
				end = true;
			} else {
				subIndex = new FastIndex();
				left = currentLength;
			}
		}
		
		next = subIndex.set(0, left);
	}
	
	public boolean hasNext() {
		return ! end;
	}
	
	public Index next() {
		if (! hasNext()) {
			throw new NoSuchElementException();
		}
		
		Index current = next;
		prepareNext();
		
		return current;
	}
}
