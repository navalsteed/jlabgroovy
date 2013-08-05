/*
 * Created on Jul 6, 2004
 */
package de.torstennahm.integrate.sparse.index;

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.torstennahm.math.IntEntry;
import de.torstennahm.math.SparseIntVector;

/**
 * Implements <code>Index</code>.
 * 
 * Only the modification to a parent index is stored. In this way, each index is effectively
 * given as a series of changes to the original 0 index. This makes for
 * a low memory footprint and very fast generation times. However, due to
 * the stacked nature of the indices, the methods <code>getEntry, </code><code>intPairIterator</code>,
 * <code>nonZeroEntries</code> and <code>equals</code> may be slow.
 * Also, while <code>addEntry</code> is fast, <code>setEntry</code> may be slow.
 * This is due to the fact that modifictations are stored as increments to an
 * entry, not as absolute entry values.
 * 
 * @author Torsten Nahm
 */
public class StackedIndex extends WrapperIndex {
	public int nonZeroEntries() {
		return 0;
	}

	public int lastEntry() {
		return -1;
	}

	public int get(int number) {
		return 0;
	}

	public Index set(int entryNum, int value) {
		return new ModifiedIndex(this, entryNum, value - 0);
	}

	public Index add(int entryNum, int increment) {
		return new ModifiedIndex(this, entryNum, increment);
	}

	public int sum() {
		return 0;
	}

	public Iterator<IntEntry> iterator() {
		return new Iterator<IntEntry>() {
			public boolean hasNext() {
				return false;
			}

			public IntEntry next() {
				throw new NoSuchElementException();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Index && ((Index) o).nonZeroEntries() == 0;
	}

	@Override
	protected SparseIntVector toSparseIntVector() {
		return new SparseIntVector();
	}
}

abstract class WrapperIndex implements Index {
	abstract protected SparseIntVector toSparseIntVector();
}

class ModifiedIndex extends WrapperIndex {
	protected final WrapperIndex base;
	protected final int hashCode;
	private final int entryNum;
	private final int entryValIncrement;
	
	protected ModifiedIndex(WrapperIndex base, int entryNum, int entryValIncrement) {
		this.base = base;
		this.entryNum = entryNum;
		this.entryValIncrement = entryValIncrement;
		hashCode = base.hashCode() + entryValIncrement * ((entryNum + 13) << entryNum);
	}
	
	public int nonZeroEntries() {
		return toSparseIntVector().nonZeroEntries();
	}
	
	public int lastEntry() {
		SparseIntVector vector = toSparseIntVector();
		return vector.size() - 1;
	}

	public Index set(int entryNum, int entryVal) {
		return add(entryNum, entryVal - get(entryNum));
	}
	
	public Index add(int entryNum, int entryVal) {
		return new ModifiedIndex(this, entryNum, entryVal);
	}
	
	public int sum() {
		return toSparseIntVector().sum();
	}
	
	public Iterator<IntEntry> iterator() {
		return toSparseIntVector().iterator();
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof StackedIndex) {
			StackedIndex comp = (StackedIndex) o;
			return toSparseIntVector().equals(comp.toSparseIntVector());
		} else if (o instanceof Index) {
			Index comp = (Index) o;
			return toSparseIntVector().equals(new SparseIntVector(comp.iterator()));
		}
		
		return false;
	}

	public int get(int entryNum) {
		if (entryNum == this.entryNum) {
			return base.get(entryNum) + entryValIncrement;
		} else {
			return base.get(entryNum);
		}
	}
	
	@Override
	protected SparseIntVector toSparseIntVector() {
		SparseIntVector vector = base.toSparseIntVector();
		vector.add(entryNum, entryValIncrement);
		return vector;
	}
}
