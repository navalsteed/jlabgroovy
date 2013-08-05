

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

/**
 * Transpose a sparse matrix.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_transpose {

    /**
     * Computes the transpose of a sparse matrix, C =A';
     * 
     * @param A
     *            column-compressed matrix
     * @param values
     *            pattern only if false, both pattern and values otherwise
     * @return C=A', null on error
     */
    public static Dcs cs_transpose(Dcs A, boolean values) {
        int p, q, j, Cp[], Ci[], n, m, Ap[], Ai[], w[];
        double Cx[], Ax[];
        Dcs C;
        if (!Dcs_util.CS_CSC(A))
            return (null); /* check inputs */
        m = A.m;
        n = A.n;
        Ap = A.p;
        Ai = A.i;
        Ax = A.x;
        C = Dcs_util.cs_spalloc(n, m, Ap[n], values && (Ax != null), false); /* allocate result */
        w = new int[m]; /* get workspace */
        Cp = C.p;   // column pointers
        Ci = C.i;  // row indexes
        Cx = C.x;  // values
        for (p = 0; p < Ap[n]; p++)
            w[Ai[p]]++; /* row counts */
        Dcs_cumsum.cs_cumsum(Cp, w, m); /* row pointers */
        for (j = 0; j < n; j++) {   // j over all columns of A
            for (p = Ap[j]; p < Ap[j + 1]; p++) { // Ai[p] over all row indices of column j
                Ci[q = w[Ai[p]]++] = j; /* place A(i,j) as entry C(j,i) */
                if (Cx != null)
                    Cx[q] = Ax[p];
            }
        }
        return C;
    }

}
