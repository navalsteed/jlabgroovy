/*
 * Created on Oct 20, 2004
 */
package de.torstennahm.integrate.sparse;

import java.util.HashSet;
import java.util.Set;

import de.torstennahm.integrate.IntegrationInfo;
import de.torstennahm.integrate.IntegrationResult;

/**
 * Package access class used by several sparse integrators for their
 * integration result.
 * 
 * @author Torsten Nahm
 */

class SparseResult implements IntegrationResult {
	protected double value = 0.0;
	protected double errorEstimate = Double.NaN;
	protected long calls = 0;
	protected Set<IntegrationInfo> supplementalInfo = new HashSet<IntegrationInfo>();
	
	public double value() {
		return value;
	}

	public double errorEstimate() {
		return errorEstimate;
	}

	public long functionCalls() {
		return calls;
	}
	
	public Set<IntegrationInfo> supplementalInfo() {
		return supplementalInfo;
	}
}