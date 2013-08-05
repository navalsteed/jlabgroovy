

package edu.emory.mathcs.csparsej.tfloat;

/**
 * Cumulative sum.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_cumsum {

    /**
     * p [0.f.n] = cumulative sum of c [0.f.n-1], and then copy p [0.f.n-1] into c
     * 
     * @param p
     *            size n+1, cumulative sum of c
     * @param c
     *            size n, overwritten with p [0.f.n-1] on output
     * @param n
     *            length of c
     * @return sum (c), null on error
     */
    public static int cs_cumsum(int[] p, int[] c, int n) {
        int i, nz = 0;
        float nz2 = 0;
        if (p == null || c == null)
            return (-1); /* check inputs */
        for (i = 0; i < n; i++) {
            p[i] = nz;
            nz += c[i];
            nz2 += c[i]; /* also in float to avoid int overflow */
            c[i] = p[i]; /* also copy p[0.f.n-1] back into c[0.f.n-1]*/
        }
        p[n] = nz;
        return (int) nz2; /* return sum (c [0.f.n-1]) */
    }

}
