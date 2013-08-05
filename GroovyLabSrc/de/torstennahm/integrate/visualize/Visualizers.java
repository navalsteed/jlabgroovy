/*
 * Created on Oct 17, 2004
 */
package de.torstennahm.integrate.visualize;

import java.util.Iterator;
import java.util.List;

import de.torstennahm.integrate.visualizerdata.VisualizerData;


/**
 * Convenience functions for propagating <code>Visualizer</code> method calls to a list of <code>Visualizers</code>.
 * Using these functions, an integrator can easily submit integration events to
 * a whole list of visualizers.
 * 
 * @author Torsten Nahm
 */
public class Visualizers {
	/**
	 * This class is simply a collection of static methods and may not be instatiated.
	 */
	private Visualizers() {};
	
	static public void initList(List<Visualizer> visualizers) {
		if (visualizers != null) {
			for (Iterator<Visualizer> iter = visualizers.iterator(); iter.hasNext(); ) {
				iter.next().init();
			}
		}
	}
	
	static public void startList(List<Visualizer> visualizers) {
		if (visualizers != null) {
			for (Iterator<Visualizer> iter = visualizers.iterator(); iter.hasNext(); ) {
				iter.next().start();
			}
		}
	}
	
	static public void stopList(List<Visualizer> visualizers) {
		if (visualizers != null) {
			for (Iterator<Visualizer> iter = visualizers.iterator(); iter.hasNext(); ) {
				iter.next().stop();
			}
		}
	}
	
	static public void destroyList(List<Visualizer> visualizers) {
		if (visualizers != null) {
			for (Iterator<Visualizer> iter = visualizers.iterator(); iter.hasNext(); ) {
				iter.next().destroy();
			}
		}
	}
	
	static public void submitToList(List<Visualizer> visualizers, VisualizerData data) {
		if (visualizers != null) {
			for (Iterator<Visualizer> iter = visualizers.iterator(); iter.hasNext(); ) {
				iter.next().submit(data);
			}
		}
	}
}
