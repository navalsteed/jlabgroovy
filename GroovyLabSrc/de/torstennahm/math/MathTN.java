/*
 * Created on May 6, 2003
 */
package de.torstennahm.math;

/**
 * A collection of generally useful functions and methods for mathematics.
 * 
 * This class cannot be instantiated.
 *  
 * @author Torsten Nahm
 */

public class MathTN {
	private MathTN() {}
	
	/**
	 * A totally heuristic variable supposed to be an upper bound in usual situations for errors
	 * arising from rounding when calculating with <code>double</code> precision.
	 */
	public static final double FUDGE = 1e-13;
	
	private static final double A0 = 0.398942270991,
								A1 = 0.020133760596,
								A2 = 0.002946756074,
					 
								B1 = 0.217134277847,
								B2 = 0.018576112465,
								B3 = 0.000643163695,
					 
								C0 = 1.398247031184,
								C1 =-0.360040248231,
								C2 = 0.022719786588,
					 
								D0 = 1.460954518699,
								D1 =-0.305459640162,
								D2 = 0.038611796258,
								D3 =-0.003787400686;
	
	/**
	 * Computes the cumulative distribution function for the Gaussian distribution.
	 * <p>
	 * The value is computed using an algorithm of Moro.
	 * 
	 * @param x x
	 * @return p
	 */
	public static double gaussian(double x) {
		double y;
		double result;
		
		y = Math.abs(x);
		
		if (y <= 1.87) {
			double z = y * y;
			result = 0.5 + y * (A0 + (A1 + A2 * z) * z) / (1.0 + (B1 + (B2 + B3 * z) * z) * z);
		} else if (y < 6.0) {
			result = 1.0 - Math.pow((C0 + (C1 + C2 * y) * y) / (D0 + (D1 + (D2 + D3 * y) * y) * y) , 16.0);
		} else {
			result = 1;
		}
		
		return (x >= 0.0) ? result : 1.0 - result;
	}
	
	private static final double E0 = 2.50662823884,
								E1 = -18.61500062529,
								E2 = 41.39119773534,
								E3 = -25.44106049637,
								F0 = -8.47351093090,
								F1 = 23.08336743743,
								F2 = -21.06224101826,
								F3 = 3.13082909833,
								G0 = 0.3374754822726147,
								G1 = 0.9761690190917186,
								G2 = 0.1607979714918209,
								G3 = 0.0276438810333863,
								G4 = 0.0038405729373609,
								G5 = 0.0003951896511919,
								G6 = 0.0000321767881768,
								G7 = 0.0000002888167364,
								G8 = 0.0000003960315187;

	/**
	 * Computes the inverse of the cumulative gaussian distribution function.
	 * <p>
	 * The value is computed with an algorithm of Moro.
	 * 
	 * @param p value in [0,1]
	 * @return x
	 */
	public static double inverseGaussian(double p) {
		double x, r;

		x = p - 0.5;
		
		if (Math.abs(x) < 0.42) {
			r = x*x;
			r = x*(((E3*r+E2)*r+E1)*r+E0)/((((F3*r+F2)*r+F1)*r+F0)*r+1.0);
			return r;
		}
		
		r = p;
		
		if (x > 0.0) {
			r = 1.0 - p;
		} 
		
		r = Math.log(-Math.log(r));
		r = G0+r*(G1+r*(G2+r*(G3+r*(G4+r*(G5+r*(G6+r*(G7+r*G8)))))));
		
		if (x < 0.0) {
			r = -r;
		}
		
		return r;
	}
	
	/**
	 * Computes the theta function.
	 * 
	 * @param x x
	 * @return 1 if x >= 0, 0 if x < 0
	 */
	public static double theta(double x) {
		return (x >= 0.0) ? 1.0 : 0.0; 
	}
	
	/**
	 * Computes the fermi function.
	 * 
	 * @param x x
	 * @return 1 / (1 + exp(-x))
	 */
	public static double fermi(double x) {
		return 1.0 / (1.0 + Math.exp(-x)); 
	}
	
	private static double base10 = 1.0 / Math.log(10.0);
	
	/**
	 * Computes the logarithm to the base 10.
	 * 
	 * @param x x
	 * @return decimal logarithm of x
	 */
	public static double log10(double x) {
		return Math.log(x) * base10;
	}
	
	private static double base2 = 1.0 / Math.log(2.0);
	
	/**
	 * Computes the logarithm to the base 2.
	 * 
	 * @param x x
	 * @return binary logarithm of x.
	 */
	public static double lb(double x) {
		return Math.log(x) * base2;
	}
	
	/**
	 * Returns the sum over a <code>double</code> array.
	 * 
	 * @param x x
	 * @return x[0] + ... + x[x.length - 1]
	 */
	public static double arraySum(double[] x) {
		double s = 0.0;
		
		for (int i = 0; i < x.length; i++) {
			s += x[i];
		}
		
		return s;
	}
	
	/**
	 * Returns the sum over an <code>int</code> array.
	 * 
	 * @param x x
	 * @return x[0] + ... + x[x.length - 1]
	 */
	public static int arraySum(int[] x) {
		int s = 0;
		
		for (int i = 0; i < x.length; i++) {
			s += x[i];
		}
		
		return s;
	}
	
	/**
	 * Returns the product over a <code>double</code> array.
	 * 
	 * @param x x
	 * @return x[0] * ... * x[x.length - 1]
	 */
	public static double arrayProduct(double[] x) {
		double p = 1.0;
		
		for (int i = 0; i < x.length; i++) {
			p *= x[i];
		}
		
		return p;
	}
	
	/**
	 * Returns the product over an <code>int</code> array.
	 * 
	 * @param x x
	 * @return x[0] * ... * x[x.length - 1]
	 */
	public static long arrayProduct(int[] x) {
		long p = 1;
		
		for (int i = 0; i < x.length; i++) {
			p *= x[i];
		}
		
		return p;
	}
	
	/**
	 * Calculates the factorial <i>n!</i> for the number <code>n</code>.
	 * 
	 * @param n
	 * @return 1*2*...*n
	 */
	public static long factorial(int n) {
		long result = 1;
		for (int i = 1; i <= n; i++) {
			result *= i;
		}
		
		return result;
	}
	
	/**
	 * Calculates the binomial value <code>n</code> over <code>k</code>.
	 * 
	 * @param n n
	 * @param k k
	 * @return n over k
	 */
	public static long binomial(int n, int k) {
		long result = 1;
		
		if (n < 0 || (k < 0) || (k > n)) {
			return 0;
		}
		
		if (2 * k > n) {
			k = n - k;
		}
		
		int j = 2;
		for (int i = 0; i < k; i++) {
			result *= n - i;
			while (j <= k && result % j == 0) {
				result /= j;
				j++;
			}
		}
		
		return result;
	}

	/**
	 * Returns the sign of x.
	 * 
	 * @param x x
	 * @return -1 if x < 0, +1 if x > 0, 0 if x = 0
	 */
	public static double sign(double x) {
		if (x > 0) {
			return 1.0;
		} else if (x < 0) {
			return -1.0;
		} else {
			return 0.0;
		}
	}
	
	/**
	 * Returns the smallest number <i>2^k<i> with <i>2^k >= n</i> and <i>k</i>
	 * a natural number including 0.
	 * 
	 * @param n integer
	 * @return smallest power of 2 larger than or equal to <code>n</code>
	 */
	public static int binaryCeil(int n) {
		if (n > Integer.MAX_VALUE / 2) {
			throw new ArithmeticException("Integer overflow");
		}
		int m = 1;
		while (m < n) {
			m <<= 1;
		}
		
		return m;
	}
	
	/**
	 * Returns the smallest number <i>2^k<i> with <i>2^k > n</i> and <i>k</i>
	 * a natural number including 0.
	 * 
	 * @param n integer
	 * @return smallest power of 2 larger than <code>n</code>
	 */
	public static int binaryTop(int n) {
		return binaryCeil(n + 1);
	}
	
	/**
	 * Returns the sinus hyperbolicus of x.
	 * 
	 * @param x x
	 * @return sinus hyperbolicus of x
	 */
	public static double sinh(double x) {
		return 0.5 * (Math.exp(x) - Math.exp(-x));
	}
	
	/**
	 * Returns the cosinus hyperbolicus of x.
	 * 
	 * @param x x
	 * @return cosinus hyperbolicus of x
	 */
	public static double cosh(double x) {
		return 0.5 * (Math.exp(x) + Math.exp(-x));
	}

	/**
	 * Mathematical modulus of two numbers.
	 * 
	 * The mathematical modulus is defined as the (non-negative) integer <i>c</i> in the
	 * interval from 0 to b-1 so that <i>b*k+c = a</i> for some integer <i>k</i>. This
	 * differs from the Java operator "%" in that <code>mod(-1,3)=2</code>,
	 * whereas <code>-1%3=-1</code>.
	 * 
	 * @param a number
	 * @param b modulus number >0
	 * @return mathematical a mod b
	 */
	public static int mod(long a, int b) {
		if (b <= 0) {
			throw new IllegalArgumentException("b must be greater than 0");
		}
		
		if (a >= 0) {
			return (int) (a % b);
		} else {
			return (int) ((a % b) + b);
		}
	}
	
	
	/**
	 * Checks if the given number is a prime.
	 * 
	 * This method is very slow.
	 * 
	 * @param n number
	 * @return <code>true</code> if <code>n</code> is prime, <code>false</code> otherwise
	 */
	public static boolean isPrime(int n) {
		if (n == 1) {
			return false;
		} else if (n == 2) {
			return true;
		} else if (n % 2 == 0) {
			return false;
		} else {
			for (int i = 3; i * i <= n; i += 2) {
				if (n % i == 0) {
					return false;
				}
			}
			
			return true;
		}
	}
}
