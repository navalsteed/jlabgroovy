

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

/**
 * Sparse matrix 1-norm.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_norm {

    /**
     * Computes the 1-norm of a sparse matrix = max (sum (abs (A))), largest
     * column sum.
     * 
     * @param A
     *            column-compressed matrix
     * @return the 1-norm if successful, -1 on error
     */
    public static double cs_norm(Dcs A) {
        int p, j, n, Ap[];
        double Ax[], norm = 0, s;
        if (!Dcs_util.CS_CSC(A) || A.x == null)
            return (-1); /* check inputs */
        n = A.n;
        Ap = A.p;
        Ax = A.x;
        for (j = 0; j < n; j++) {
            for (s = 0, p = Ap[j]; p < Ap[j + 1]; p++)
                s += Math.abs(Ax[p]);
            norm = Math.max(norm, s);
        }
        return (norm);
    }
}
