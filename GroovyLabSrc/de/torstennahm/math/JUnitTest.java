/*
 * Created on Aug 22, 2003
 */
package de.torstennahm.math;

import java.util.Random;

import junit.framework.TestCase;

/**
 * JUnit test.
 * 
 * @author Torsten Nahm
 */
public class JUnitTest extends TestCase {
	/**
	 * Tests <code>MathTN.gaussian</code> and <code>MathTN.inverseGaussian</code>.
	 */
	public void testGauss() {
		double maxerr = 0.0;
		for (int i = 0; i < 10000; i++) {
			double r = Math.random();
			double err = Math.abs(MathTN.gaussian(MathTN.inverseGaussian(r)) - r);
			if (err > maxerr) {
				maxerr = err;
			}
		}
		assertTrue(maxerr < 1e-8);
	}
	
	public void testMath() {
		Random r = new Random(1);
		
		assertTrue(MathTN.binaryTop(0) == 1);
		assertTrue(MathTN.binaryCeil(0) == 1);
		
		for (int i = 0; i < 100; i++) {
			int k = r.nextInt(30);
			int n = 1 << k;
			assertTrue(MathTN.binaryTop(n) == n * 2);
			assertTrue(MathTN.binaryCeil(n) == n);
			if (n > 2) {
				int m = n + 1 + r.nextInt(n - 2);
				assertTrue(MathTN.binaryTop(m) == n * 2);
				assertTrue(MathTN.binaryCeil(m) == n * 2);
			}
		}
	}
	
	/**
	 * Tests <code>SparseIntVector</code>.
	 */
	public void testSparseIntVector() {
		int[] testArray = new int[] { 0, 0, 2, -6, -5, 0, 2, 7, 0, 0, 0, 3 };
		int[] array = new int[100];
		
		SparseIntVector v;
		
		for (int type = 0; type < 1; type++) {
			v = new SparseIntVector();
			for (int i = 0; i < testArray.length; i++) {
				v.set(i, testArray[i]);
			}
			
			for (int j = 0; j < testArray.length && j < array.length; j++) {
				array[j] = testArray[j];
			}
			
			Random r = new Random(12345678);
			
			for (int k = 0; k < 1000; k++) {
				for (int j = 0; j < array.length; j++) {
					assertTrue(v.get(j) == array[j]);
				}
				int[] carr = v.toIntArray();
				for (int j = 0; j < carr.length && j < array.length; j++) {
					assertTrue(carr[j] == array[j]);
				}
				
				int p = r.nextInt(array.length);
				int n = (r.nextDouble() < 0.4) ? 0 : r.nextInt();
				array[p] = n;
				v = v.duplicate();
				v.set(p, n);
			}
		}
	}
	
	public void testComplex() {
		for (int i = 0; i < 1000; i++) {
			Complex a = newComplex(), b = newComplex();
			
			Complex r = a.add(b).sub(b).mul(b).div(b).div(b).mul(b).sub(b).add(b).sub(a);
			assertTrue(r.abs() < 1e-12);
		}
	}
	
	Random r = new Random(1001);
	Complex newComplex() {
		return new Complex(r.nextDouble() * 20 - 10, r.nextDouble() * 20 - 10);
	}
}
