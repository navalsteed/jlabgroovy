/*
 * Created on Oct 29, 2003
 */
package de.torstennahm.math;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Models a vector of integer values.
 * 
 * Mathematically speaking, the sparse int vector represents an element of
 * N-fold direct sum of Z, where N is the naturals including 0, and Z is the
 * integers. The entries of the vector are stored with a sparse coding,
 * so that only entries with a value not equal to 0 take up space.
 * <p>
 * The vector is said to consist of pairs of numbers and values, with 
 * an integer value assigned to each number >= 0. These pairs of number and value
 * are called entries.
 * <p>
 * This class is NOT thread-safe.
 * 
 * @author Torsten Nahm
 */
public class SparseIntVector implements Cloneable, Iterable<IntEntry> {
	private TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
	
	/**
	 * Creates the vector. All elements are initially 0.
	 */
	public SparseIntVector() {
	}
	
	/**
	 * Creates the vector from the given array.
	 * 
	 * All the values of the array are filled in at their corresponding position,
	 * the rest of the values of the vector are set to 0.
	 * 
	 * @param array array to initialize from
	 */
	public SparseIntVector(int[] array) {
		for (int i = 0; i < array.length; i++) {
			set(i, array[i]);
		}
	}

	/**
	 * Creates the vector with the data from the iterator.
	 * 
	 * All entries not specified have the value 0.
	 * 
	 * @param iterator iterator supplying the (index, value) pairs
	 */
	public SparseIntVector(Iterator<IntEntry> iterator) {
		while (iterator.hasNext()) {
			IntEntry pair = iterator.next();
			set(pair.getNumber(), pair.getValue());
		}
	}
	
	/**
	 * Returns the value for the given entry.
	 * 
	 * @param entryNum number of the entry
	 * @return value for the entry
	 * @throws IndexOutOfBoundsException if number < 0
	 */
	public int get(int entryNum) {
		if (entryNum < 0) {
			throw new IndexOutOfBoundsException();
		}
		
		Integer integer = map.get(entryNum);
		
		return integer == null ? 0 : integer.intValue();
	}
	
	/**
	 * Sets the value for the given entry.
	 *  
	 * @param entryNum of the entry
	 * @param entryVal new value for the entry 
	 * @return old value for the entry
	 */
	public int set(int entryNum, int entryVal) {
		if (entryNum < 0) {
			throw new IndexOutOfBoundsException();
		}
		
		Integer last;
		if (entryVal == 0) {
			last = map.remove(entryNum);
		} else {
			last = map.put(entryNum, entryVal);
		}
		
		return last == null ? 0 : last.intValue();
	}
	
	/**
	 * Adds a given integer to the value for the given index.
	 * 
	 * @param entryNum number of the entry
	 * @param add integer to add, may be 0 or negative
	 * @return old value for this entry
	 */
	public int add(int entryNum, int add) {
		return set(entryNum, get(entryNum) + add);
	}
	
	/**
	 * Returns the sum over all the values of the vector.
	 * 
	 * @return sum of all components
	 */
	public int sum() {
		int sum = 0;
		
		for (IntEntry entry : this) {
			sum += entry.getValue();
		}
		
		return sum;
	}
	
	/**
	 * Returns a representation of the packed vector as an <code>int</code> array.
	 * 
	 * The array will have the minimum size needed to contain all non-0
	 * elements.
	 * 
	 * @return an int array representation of the vector
	 */
	public int[] toIntArray() {
		return toIntArray(size());
	}
	
	/**
	 * The same as <code>toIntArray()</code>, but the size the array to
	 * be returned is specified.
	 * 
	 * It must be at least as large as the size of the vector.
	 * 
	 * @param size size of the returned array
	 * @return an int array representation of the vector with the specified size
	 * @throws IllegalArgumentException if the specified size is too small to hold all non-0 elements
	 * @see #toIntArray()
	 */
	public int[] toIntArray(int size) {
		if (size < size()) {
			throw new IllegalArgumentException("Specified size too small");
		}
		
		int[] array = new int[size];
		
		for (IntEntry entry : this) {
			array[entry.getNumber()] = entry.getValue();
		}
		
		return array;
	}
	
	/**
	 * Returns the current size of the vector.
	 * 
	 * @return number of entries up to and including the last non-0 entry, 0 if all entries are 0
	 */
	public int size() {
		return map.isEmpty() ? 0 : map.lastKey().intValue() + 1;
	}
	
	/**
	 * Returns number of non-0 entries.
	 * 
	 * @return number of entries with a value != 0
	 */
	public int nonZeroEntries() {
		return map.size();
	}
	
	/**
	 * Creates a new vector with the entries of this vector.
	 * 
	 * @return copy of vector
	 */
	public SparseIntVector duplicate() {
		SparseIntVector copy = new SparseIntVector();
		copy.map = new TreeMap<Integer, Integer>(map);
		
		return copy;
	}
	
	/**
	 * Returns an interator for iterating over the vector.
	 * 
	 * The iterator will return all entries with non-0 value in order
	 * of ascending index.
	 * 
	 * @return iterator
	 */
	public Iterator<IntEntry> iterator() {
		return new internalIterator();
	}
	
	protected class internalIterator implements Iterator<IntEntry> {
		private final Iterator<Map.Entry<Integer, Integer>> iter = map.entrySet().iterator();
		
		public boolean hasNext() {
			return iter.hasNext();
		}
		
		public IntEntry next() {
			final Map.Entry<Integer, Integer> entry = iter.next();
			return new IntEntry() {
				public int getNumber() {
					return entry.getKey().intValue();
				}
				public int getValue() {
					return entry.getValue().intValue();
				}
			};
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	@Override
	public int hashCode() {
		return map.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof SparseIntVector && ((SparseIntVector) o).map.equals(map);
	}
}
