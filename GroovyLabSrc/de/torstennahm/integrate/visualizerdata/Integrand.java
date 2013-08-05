/*
 * Created on Oct 17, 2004
 */
package de.torstennahm.integrate.visualizerdata;



/**
 * Sends the integrand object.
 * 
 * @author Torsten Nahm
 */
public class Integrand extends VisualizerData {
	public final Object integrand;
	
	public Integrand(Object integrand) {
		this.integrand = integrand;
	}
}
