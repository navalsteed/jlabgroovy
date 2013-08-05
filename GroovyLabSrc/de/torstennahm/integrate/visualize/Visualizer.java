/*
 * Created on Apr 8, 2003
 */
package de.torstennahm.integrate.visualize;

import de.torstennahm.integrate.visualizerdata.VisualizerData;


/**
 * Class for realizing visualization plug-ins.
 * 
 * The methods of this class are called during the integration process by the adaptive
 * sparse integrator with information about the current state of integration.
 * This information is presented to the user.
 * 
 * @author Torsten Nahm
 */
public interface Visualizer {
	/**
	 * Initializes the visualizer for use. Must be called before any other method.
	 */
	void init();
	
	/**
	 * Signals the visualizer that integration is now being started.
	 * This resets the visualizer for this integration process.
	 */
	void start();
	
	/**
	 * Signals the visualizer that integration has now ended and no
	 * further <code>VisualizerData</code> will be sent. The <code>Visualizer</code>
	 * may use this signal to present a final display for this integration process.
	 */
	void stop();
	
	/**
	 * Fully turn off the visualizer. This should include closing all open windows
	 * if any have been opened.
	 */
	void destroy();
	
	/**
	 * Send a data object to the visualizer. The visualizer may extract any data
	 * relevant to its taks.
	 * 
	 * @param data object containing data about the current state of the integration process
	 */
	void submit(VisualizerData data);
}
