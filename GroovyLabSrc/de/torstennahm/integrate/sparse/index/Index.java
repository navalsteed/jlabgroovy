/*
 * Created on Dec 17, 2003
 */
package de.torstennahm.integrate.sparse.index;

import java.util.Iterator;

import de.torstennahm.math.IntEntry;

/**
 * Models a multi-index of infinite dimension.
 * 
 * The multi-index is an element of the N-fold direct sum of N,
 * where N is the natural numbers including 0.
 * <p>
 * An index is immutable. Changing the value of an entry will return a
 * new index with the specified change, but leave the original index untouched.
 * <p>
 * All implementations of this interface must be thread-safe.
 * 
 * @author Torsten Nahm
 */

public interface Index extends Iterable<IntEntry> {
	/**
	 * Returns the number of entries with values not equal to 0.
	 * 
	 * @return number of entries with value != 0
	 */
	int nonZeroEntries();
	
	/**
	 * Returns the index of highest entry whose value is not 0.
	 * 
	 * @return number of last non-0 entry, -1 if all entries are 0
	 */
	int lastEntry();
	
	/**
	 * Returns the value for the specified entry.
	 * 
	 * @param number number of the entry
	 * @return value value of the entry
	 */
	int get(int number);
	
	/**
	 * Returns a new index with the specified new value at the specified entry.
	 * 
	 * @param entryNum number of the entry
	 * @param value new value for the entry
	 * @return new index with the requested change
	 */
	Index set(int entryNum, int value);
	
	/**
	 * Returns a new index with the specified increment at the specified entry.
	 * 
	 * @param entryNum number of the entry
	 * @param increment increment for the entry, may be negative
	 * @return new index with the requested change
	 */
	Index add(int entryNum, int increment);
	
	/**
	 * Returns the the sum of all entry values.
	 * 
	 * @return length of the index
	 */
	int sum();
	
	/**
	 * Returns an <code>IntPairIterator</code> for iterating over the index.
	 * The iterator skips all entries whose value is zero. 
	 * 
	 * @return iterator iterator over the non-zero entries
	 */
	Iterator<IntEntry> iterator();
	
	/**
	 * Compares the specified object with this index for equality.
	 * Returns true if the specified object is also an index, the
	 * two indices have the same dimension, and all the
	 * entries of the two indices are equal.
	 * This definition ensures that the equals method works properly
	 * across different implementations of the <code>Index</code> interface.
	 */
	boolean equals(Object o);
	
	/**
	 * Returns the hash code value for this index. The hash code of an index is defined
	 * by the following expression:<br>
	 * <i>hashCode = e[0] * a0 + ... + e[dimension - 1] * a_(dimension-1)</i><br>,
	 * where e[i] is the i-th entry, and a_i = (i + 13) << i in integer arithmetic.
	 */
	int hashCode();
}
