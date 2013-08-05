/*
 * Created on Oct 20, 2004
 */
package de.torstennahm.integrate.sparse.visualize;

import de.torstennahm.integrate.sparse.index.Index;
import de.torstennahm.integrate.visualizerdata.VisualizerData;

/**
 * @author Torsten Nahm
 */
public class IndexVisualizerData extends VisualizerData {
	public final Index index;
	
	public IndexVisualizerData(Index index) {
		this.index = index;
	}
}
