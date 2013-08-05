/*
 * Created on Jun 11, 2003
 */
package de.torstennahm.series;

/**
 * Creates a VanDerCorput sequence using integer arithmetics to avoid
 * rounding errors.
 * 
 * @author Torsten Nahm
 */

public class VanDerCorput {
	private int base;
	
	private long denom;
	private long nom;
	private double invBase;
	
	/**
	 * Creates a VanDerCorput sequence to the given base.
	 * 
	 * @param base base number of the sequence
	 */
	public VanDerCorput(int base) {
		this(base, 0);
	}
	
	/**
	 * Creates a VanDerCorput sequence to the given base with the specified shift.
	 * 
	 * @param base base number of the sequence
	 */
	public VanDerCorput(int base, long start) {
		this.base = base;
		
		init();
		
		nom = 0;
		while (start != 0) {
			expand();
			nom += start % base;
			start /= base;
		}
	}
	
	/**
	 * Returns the next value in the sequence.
	 * 
	 * @return next value
	 */
	synchronized public double next() {
		long add = denom / base;
		long n = nom;
		while (n + add >= denom) {		// Carry
			nom -= (base - 1) * add;
			add /= base;
		}
		if (add == 0) {
			expand();
			add = 1;
		}
		nom += add;
		
		return nom * invBase;
	}
	
	private void init() {
		denom = 1;
		invBase = 1.0 / denom;
	}
	
	private void expand() {
		nom *= base;
		denom *= base;
		invBase = 1.0 / denom;
	}
	
	/**
	 * Returns the base of the sequence.
	 * 
	 * @return base number of the sequence
	 */
	public int getBase() {
		return base;
	}
}
