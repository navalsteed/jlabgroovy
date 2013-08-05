
package edu.emory.mathcs.csparsej.tfloat;

import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;

/**
 * Sparse matrix 1-norm.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_norm {

    /**
     * Computes the 1-norm of a sparse matrix = max (sum (abs (A))), largest
     * column sum.
     * 
     * @param A
     *            column-compressed matrix
     * @return the 1-norm if successful, -1 on error
     */
    public static float cs_norm(Scs A) {
        int p, j, n, Ap[];
        float Ax[], norm = 0, s;
        if (!Scs_util.CS_CSC(A) || A.x == null)
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
