/*
 * Created on Oct 17, 2004
 */
package de.torstennahm.integrate.visualizerdata;

import de.torstennahm.integrate.IntegrationResult;

/**
 * Signals that integration has stopped.
 * 
 * @author Torsten Nahm
 */
public class StopIntegration extends VisualizerData {
	public final IntegrationResult result;
	
	public StopIntegration(IntegrationResult result) {
		this.result = result;
	}
}
