/*
 * Created on Oct 20, 2004
 */
package de.torstennahm.integrate.sparse.visualize;

import de.torstennahm.integrate.sparse.index.Index;

/**
 * @author Torsten Nahm
 */
public class IndexContributionEstimate extends IndexVisualizerData {
	public final double estimate;
	
	public IndexContributionEstimate(Index index, double estimate) {
		super(index);
		this.estimate = estimate;
	}
}
