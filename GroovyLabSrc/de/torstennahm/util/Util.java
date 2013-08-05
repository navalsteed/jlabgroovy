/*
 * Created on Oct 16, 2003
 */
package de.torstennahm.util;

import java.text.DecimalFormat;

/**
 * Provides misceallaneous static methods.
 * 
 * This class cannot be instantiated.
 * 
 * @author Torsten Nahm
 */
public class Util {
	private Util() {}
	
	/**
	 * Formats the number using scientific notation. The number
	 * will be output with a 7 digit normalized mantissa and an exponent
	 * if its absolute value is less than 1e-4. Otherwise, non-exponential
	 * output is used, with 6 digits after the decimal point.
	 * 
	 * @param number number to be formatted
	 * @return formatted string
	 */
	public static String format(double number) {
		DecimalFormat df;
		if (Math.abs(number) < 1e-4 && number != 0.0) {
			df = new DecimalFormat("0.000000E0");
		} else {
			df = new DecimalFormat("0.000000");
		}
		
		return df.format(number);
	}
	
	/**
	 * Returns a new array whose element order has been reversed.
	 * 
	 * <code>new[0]=array[array.length-1], new[1]=array[array.length-2], ...</code>. 
	 * 
	 * @param array reversed array of doubles
	 */
	public static double[] reverseArray(double[] array) {
		int n = array.length;
		double[] a = new double[n];
		for (int i = 0; i < n; i++) {
			a[i] = array[n - 1 - i];
		}
		
		return a;
	}
}
