/*
 * Created on Nov 9, 2003
 */
package de.torstennahm.util;

import java.awt.Color;

public interface ColorScale {
	/**
	 * Get a color for the given value. Only the range the range between
	 * 0 and 1 is differentiated, values below 0 will get the same color as 0,
	 * and those above 1 the same as 1.
	 * 
	 * @param value value
	 * @return <code>Color</code> object for the value
	 */
	Color getColor(double value);
}
