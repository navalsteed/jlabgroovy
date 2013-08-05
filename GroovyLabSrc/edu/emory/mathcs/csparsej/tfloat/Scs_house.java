

package edu.emory.mathcs.csparsej.tfloat;

/**
 * Compute Householder reflection.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_house {

    /**
     * Compute a Householder reflection, overwrite x with v, where
     * (I-beta*v*v')*x = s*e1. See Algo 5.1f.1f, Golub & Van Loan, 3rd ed.
     * 
     * @param x
     *            x on output, v on input
     * @param x_offset
     *            the index of the first element in array x
     * @param beta
     *            scalar beta
     * @param n
     *            the length of x
     * @return norm2(x), -1 on error
     */
    public static float cs_house(float[] x, int x_offset, float[] beta, int n) {
        float s, sigma = 0;
        int i;
        if (x == null || beta == null)
            return (-1); /* check inputs */
        for (i = 1; i < n; i++)
            sigma += x[x_offset + i] * x[x_offset + i];
        if (sigma == 0) {
            s = Math.abs(x[x_offset + 0]); /* s = |x(0)| */
            beta[0] = (x[x_offset + 0] <= 0) ? 2.0f : 0.0f;
            x[x_offset + 0] = 1;
        } else {
            s = (float)Math.sqrt(x[x_offset + 0] * x[x_offset + 0] + sigma); /* s = norm (x) */
            x[x_offset + 0] = (x[x_offset + 0] <= 0) ? (x[x_offset + 0] - s) : (-sigma / (x[x_offset + 0] + s));
            beta[0] = -1.0f / (s * x[x_offset + 0]);
        }
        return (s);
    }
}
