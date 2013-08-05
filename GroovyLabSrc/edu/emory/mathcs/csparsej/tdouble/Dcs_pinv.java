

package edu.emory.mathcs.csparsej.tdouble;

/**
 * Invert a permutation vector.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_pinv {

    /**
     * Inverts a permutation vector. Returns pinv[i] = k if p[k] = i on input.
     * 
     * @param p
     *            a permutation vector if length n
     * @param n
     *            length of p
     * @return pinv, null on error
     */
    public static int[] cs_pinv(int[] p, int n) {
        int k, pinv[];
        if (p == null)
            return (null); /* p = NULL denotes identity */
        pinv = new int[n]; /* allocate result */
        for (k = 0; k < n; k++)
            pinv[p[k]] = k;/* invert the permutation */
        return (pinv); /* return result */
    }
}
