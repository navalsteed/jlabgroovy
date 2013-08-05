

package edu.emory.mathcs.csparsej.tdouble;

/**
 * Permutes a vector, x=P*b.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_pvec {

    /**
     * Permutes a vector, x=P*b, for dense vectors x and b.
     * 
     * @param p
     *            permutation vector, p=null denotes identity
     * @param b
     *            input vector
     * @param x
     *            output vector, x=P*b
     * @param n
     *            length of p, b and x
     * @return true if successful, false otherwise
     */
    public static boolean cs_pvec(int[] p, double[] b, double[] x, int n) {
        int k;
        if (x == null || b == null)
            return (false); /* check inputs */
        for (k = 0; k < n; k++)
            x[k] = b[p != null ? p[k] : k];
        return (true);
    }

}
