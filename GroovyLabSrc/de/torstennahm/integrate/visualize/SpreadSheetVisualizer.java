/*
 * Created on Apr 8, 2003
 */
package de.torstennahm.integrate.visualize;

import de.torstennahm.integrate.IntegrationResult;
import de.torstennahm.integrate.visualizerdata.Integrand;
import de.torstennahm.integrate.visualizerdata.StartIntegration;
import de.torstennahm.integrate.visualizerdata.StopIntegration;
import de.torstennahm.integrate.visualizerdata.VisualizerData;


/**
 * Prints basic data for the integration process in a tab-delimited form
 * for use in a spreadsheet.
 * The function name, integration result, error estimate and time used
 * are printed.
 * 
 * @author Torsten Nahm
 */
public class SpreadSheetVisualizer implements Visualizer {
	long millisStart, millisEnd;
	Object integrand;
	
	public void init() {
		System.out.println("Function\tValue\tError Estimate\tTime used");
	}
	
	public void start() {
		integrand = null;
		millisStart = millisEnd = 0;
	}
	
	public void stop() {
	}
	
	public void destroy() {
	}
	
	public void submit(VisualizerData data) {
		if (data instanceof Integrand) {
			integrand = ((Integrand) data).integrand;
		} else if (data instanceof StartIntegration) {
			millisStart = System.currentTimeMillis();
		} else if (data instanceof StopIntegration) {
			millisEnd = System.currentTimeMillis();
			IntegrationResult result = ((StopIntegration) data).result;
			System.out.println((integrand == null ? "Unknown" : integrand.toString()) + "\t" + result.value() + "\t" + result.errorEstimate() + "\t" + (millisEnd - millisStart));
		}
	}
}
