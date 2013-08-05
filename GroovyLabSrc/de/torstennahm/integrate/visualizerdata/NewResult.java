/*
 * Created on Oct 17, 2004
 */
package de.torstennahm.integrate.visualizerdata;

import de.torstennahm.integrate.IntegrationResult;

/**
 * Signals that the integrator has arrived at a new integration result.
 * 
 * @see de.torstennahm.integrate.IntegrationResult
 * 
 * @author Torsten Nahm
 */
public class NewResult extends VisualizerData {
	public final IntegrationResult result;
	
	/**
	 * Constructor.
	 * 
	 * @param result current result state for the integration process
	 */
	public NewResult(IntegrationResult result) {
		this.result = result;
	}
}
