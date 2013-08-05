/*
 * Created on Oct 17, 2004
 */
package de.torstennahm.integrate.sparse.visualize;

import de.torstennahm.integrate.sparse.index.Index;

/**
 * @author Torsten Nahm
 */
public class IndexContribution extends IndexVisualizerData {
	public final double contribution;
	
	public IndexContribution(Index index, double contribution) {
		super(index);
		this.contribution = contribution;
	}
}
