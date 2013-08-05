/*
 * Created on Jan 2, 2004
 */
package de.torstennahm.integrate.sparse.index;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import de.torstennahm.math.IntEntry;
import de.torstennahm.math.MathTN;
import de.torstennahm.math.SparseIntVector;

/**
 * Implements <code>Index</code>.
 * 
 * Only the non-zero entries for the index are stored. For performance,
 * an array is used for storage, which is grown when needed.
 * <p>
 * According to general index contract, this class is thread-safe.
 * 
 * @author Torsten Nahm
 */
public class FastIndex implements Index {
	/**
	 * Number of entries that are not zero.
	 */
	protected int nonZeroEntries;
	
	/**
	 * Array storing the entry number of each entry.
	 */
	protected int[] entryNums;
	/**
	 * Array storing the corresponding value of each entry.
	 */
	protected int[] entryVals;
	
	/**
	 * Hash code.
	 */
	protected int hashCode;
	
	/**
	 * Creates a new fast index. All entries are initially zero.
	 */
	public FastIndex() {
		nonZeroEntries = 0;
		entryNums = new int[nonZeroEntries];
		entryVals = new int[nonZeroEntries];
		hashCode = 0;
	}
	
	/**
	 * Creates an new <code>FastIndex</code> from any <code>Index</code>.
	 * 
	 * @param index index to initialize from
	 */
	public FastIndex(Index index) {
		this(index.nonZeroEntries(), index.iterator());
	}
	
	/**
	 * Creates a new <code>FastIndex</code> from a <code>PackedIntVector</code>.
	 * 
	 * @param vector vector to initialize from
	 */
	public FastIndex(SparseIntVector vector) {
		this(vector.nonZeroEntries(), vector.iterator());
	}
	
	/**
	 * Creates a fast index from an array.
	 * 
	 * @param array array to initialize from
	 */
	public FastIndex(int[] array) {
		this(new SparseIntVector(array));
	}
	
	private FastIndex(int nonZeroEntries, Iterator<IntEntry> iter) {
		this.nonZeroEntries = nonZeroEntries;
		
		entryNums = new int[nonZeroEntries];
		entryVals = new int[nonZeroEntries];
		
		for (int pos = 0; iter.hasNext(); pos++)  {
			IntEntry entry = iter.next();
			entryNums[pos] = entry.getNumber();
			entryVals[pos] = entry.getValue();
			hashCode = newHash(hashCode, entry.getNumber(), 0, entry.getValue());
		}
	}
	
	/**
	 * Private constructor for creating a modified fast index.
	 * 
	 * @param nonZeroEntries
	 * @param entryNums
	 * @param entryVals
	 * @param hash
	 */
	private FastIndex(int nonZeroEntries, int[] entryNums, int[] entryVals, int hash) {
		this.nonZeroEntries = nonZeroEntries;
		this.entryNums = entryNums;
		this.entryVals = entryVals;
		this.hashCode = hash;
	}
	
	public int nonZeroEntries() {
		return nonZeroEntries;
	}
	
	public int lastEntry() {
		return nonZeroEntries == 0 ? -1 : entryNums[nonZeroEntries - 1];
	}

	public int get(int entryNum) {
		if (entryNum < 0) {
			throw new IllegalArgumentException("Entry may not be negative");
		}
		
		int pos = Arrays.binarySearch(entryNums, entryNum);
		return pos >= 0 ? entryVals[pos] : 0;
	}
	
	public Index set(int entryNum, int value) {
		if (entryNum < 0) {
			throw new IllegalArgumentException("Entry may not be negative");
		}
		
		int oldVal;
		
		int newNonZeroEntries;
		int[] newNums;
		int[] newVals;
		
		int pos = Arrays.binarySearch(entryNums, entryNum);
		if (pos >= 0) {			// Entry exists
			oldVal = entryVals[pos];
			if (value != 0) {	// Change entry
				if (oldVal == value) {
					return this;
				}
				newNonZeroEntries = nonZeroEntries;
				newNums = entryNums.clone();
				newVals = entryVals.clone();
				newVals[pos] = value;
			} else {			// Remove entry
				newNonZeroEntries = nonZeroEntries - 1;
				newNums = new int[newNonZeroEntries];
				newVals = new int[newNonZeroEntries];
				System.arraycopy(entryNums, 0, newNums, 0, pos);
				System.arraycopy(entryNums, pos + 1, newNums, pos, newNonZeroEntries - pos);
				System.arraycopy(entryVals, 0, newVals, 0, pos);
				System.arraycopy(entryVals, pos + 1, newVals, pos, newNonZeroEntries - pos);
			}
		} else {
			pos = -pos - 1;
			oldVal = 0;
			if (value == 0) {
				return this;
			} else {
				newNonZeroEntries = nonZeroEntries + 1;
				newNums = new int[newNonZeroEntries];
				newVals = new int[newNonZeroEntries];
				System.arraycopy(entryNums, 0, newNums, 0, pos);
				newNums[pos] = entryNum;
				System.arraycopy(entryNums, pos, newNums, pos + 1, newNonZeroEntries - (pos + 1));
				System.arraycopy(entryVals, 0, newVals, 0, pos);
				newVals[pos] = value;
				System.arraycopy(entryVals, pos, newVals, pos + 1, newNonZeroEntries - (pos + 1));
			}
		}
		
		return new FastIndex(newNonZeroEntries, newNums, newVals, newHash(hashCode, entryNum, oldVal, value));
	}

	public Index add(int entryNum, int add) {
		return set(entryNum, get(entryNum) + add);
	}
	
	public int sum() {
		return MathTN.arraySum(entryVals);
	}

	public Iterator<IntEntry> iterator() {
		return new Iterator<IntEntry>() {
			private int index = 0;
			
			public boolean hasNext() {
				return index < nonZeroEntries;
			}

			public IntEntry next() {
				if (index >= nonZeroEntries) {
					throw new NoSuchElementException();
				}
				
				final int i = index;
				index++;
				
				return new IntEntry() {
					public int getNumber() {
						return entryNums[i];
					}
					public int getValue() {
						return entryVals[i];
					}
				};
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	protected static int newHash(int hashCode, int entryNum, int oldVal, int newVal) {
		return hashCode + (newVal - oldVal) * ((entryNum + 13) << entryNum);
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FastIndex) {
			FastIndex comp = (FastIndex) o;
			
			if (nonZeroEntries != comp.nonZeroEntries) {
				return false;
			} else {
				return Arrays.equals(entryNums, comp.entryNums) &&
					   Arrays.equals(entryVals, comp.entryVals);
			}
		} else if (o instanceof Index) {
			Index comp = (Index) o;
			if (nonZeroEntries() != comp.nonZeroEntries()) {
				return false;
			} else {
				for (int i = 0; i < nonZeroEntries; i++) {
					if (entryVals[i] != comp.get(entryNums[i])) {
						return false;
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		if (nonZeroEntries == 0) {
			sb.append("()");
		} else {
			for (int i = 0; i < nonZeroEntries; i++) {
				if (i > 0) {
					sb.append(" ");
				}
				sb.append("(" + entryNums[i] + ", " + entryVals[i] + ")");
			}
		}
		return sb.toString();
	}
}
