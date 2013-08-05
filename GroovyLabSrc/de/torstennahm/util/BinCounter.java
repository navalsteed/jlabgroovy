/*
 * Created on Aug 3, 2005
 */
package de.torstennahm.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class counts the number of elements placed in each bin.
 * 
 * @author Torsten Nahm
 */

public class BinCounter<E> {
	private Map<E, Integer> binMap = new HashMap<E, Integer>();
	
	/**
	 * Increment the count for the given bin by 1.
	 * 
	 * @param bin bin to increment by
	 */
	public void incrementBin(E bin) {
		addToBin(bin, 1);
	}
	
	/**
	 * Increment the count for the given bin by a specified number.
	 * 
	 * @param bin bin to increment
	 * @param add increment, may be 0 or negative
	 */
	public void addToBin(E bin, int add) {
		binMap.put(bin, number(bin) + add);
	}
	
	/**
	 * Returns the quantity for the given bin.
	 * 
	 * @param bin bin object
	 * @return quantity for the bin
	 */
	public int number(E bin) {
		Integer i = binMap.get(bin);
		return i == null ? 0 : i;
	}
	
	/**
	 * Returns the set of those bins that have been accessed so far.
	 * 
	 * All other bins have the value 0.
	 * 
	 * @return set of accessed bins
	 */
	public Set<E> accessedBins() {
		return new TreeSet<E>(binMap.keySet());
	}
}
