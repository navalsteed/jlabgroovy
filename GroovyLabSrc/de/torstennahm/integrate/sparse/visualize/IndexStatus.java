/*
 * Created on Oct 17, 2004
 */
package de.torstennahm.integrate.sparse.visualize;

import de.torstennahm.integrate.sparse.index.Index;

/**
 * @author Torsten Nahm
 */
public class IndexStatus extends IndexVisualizerData {
	public final String status;
	
	public IndexStatus(Index index, String status) {
		super(index);
		this.status = status;
	}
}
