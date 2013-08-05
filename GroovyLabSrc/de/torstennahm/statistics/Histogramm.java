/*
 * Created on Mar 25, 2004
 */
package de.torstennahm.statistics;

import de.torstennahm.math.SparseIntVector;

/**
 * Creates a histogramm with a fixed number of bins.
 * <p>
 * This class is thread-safe.
 * 
 * @author Torsten Nahm
 */
public class Histogramm {
	private SparseIntVector binVector;
	private final double start, end;
	private final int bins;
	private final double binSize;
	
	/**
	 * Creates a histogramm for the specified bins.
	 * The first and last bins are are special. The first
	 * bin (with the number <code>0</code>) will have the range <i>[-infinity, start]</i>,
	 * and the last bin (with the number <b>bins - 1</b>) the range <i>[end, +infinity]</i>.
	 * These other <code>bins - 2</code> bins divide up the interval <i>[start, end]</i>
	 * equidistantly.
	 *  
	 * @param start start value for the second bin
	 * @param end end value for the second to last bin
	 * @param bins number of bins, must be at least 3
	 */
	public Histogramm(double start, double end, int bins) {
		if (bins < 1) {
			throw new IllegalArgumentException("bins must be at least 3");
		}
		if (start >= end) {
			throw new IllegalArgumentException("start must be less than end");
		}
		
		this.start = start;
		this.end = end;
		this.bins = bins;
		binSize = (end - start) / (bins - 2);
		binVector = new SparseIntVector();
	}
	
	/**
	 * Add the value to the histogramm.
	 * 
	 * @param d value to be added
	 */
	synchronized public void add(double d) {
		if (d < start) {
			binVector.add(0, 1);
		} else if (d >= end) {
			binVector.add(bins - 1, 1);
		} else {
			binVector.add((int)((d - start) / binSize) + 1, 1);
		}
	}
	
	/**
	 * Returns the number of values added to this bin.
	 * 
	 * @param bin bin number
	 * @return number of values in the bin
	 */
	synchronized public int getBinPopulation(int bin) {
		checkBin(bin);
		return binVector.get(bin);
	}
	
	/**
	 * Returns the number of bins.
	 * 
	 * @return number of bins
	 */
	public int getNumberOfBins() {
		return bins;
	}
	
	/**
	 * Returns the start of the range for the specified bin.
	 * 
	 * @param bin bin number
	 * @return start of range; is <code>Double.NEGATIVE_INFINITY</code> for the first bin (bin 0)
	 */
	public double getBinStart(int bin) {
		checkBin(bin);
		return bin == 0 ? Double.NEGATIVE_INFINITY : start + binSize * (bin - 1);
	}
	
	/**
	 * Returns the end of the range for the specified bin.
	 * 
	 * @param bin bin number
	 * @return end of range; is <code>Double.POSITIVE_INFINITY</code> for the last bin
	 */
	public double getBinEnd(int bin) {
		checkBin(bin);
		return bin == bins - 1 ? Double.POSITIVE_INFINITY : start + binSize * bin;
	}
	
	private void checkBin(int bin) {
		if (bin < 0 || bin >= bins) {
			throw new IllegalArgumentException("Bin number not valid");
		}
	}
}
