/*
 * Created on Oct 17, 2004
 */
package de.torstennahm.integrate.visualizerdata;


/**
 * Sends the known correct value for the integral.
 * 
 * @author Torsten Nahm
 */
public class CorrectValue extends VisualizerData {
	public final double correctValue;
	
	public CorrectValue(double value) {
		correctValue = value;
	}
}
