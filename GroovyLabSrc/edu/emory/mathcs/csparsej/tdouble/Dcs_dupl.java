
package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

/**
 * Remove (and sum) duplicates.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_dupl {

    /**
     * Removes and sums duplicate entries in a sparse matrix.
     * 
     * @param A
     *            column-compressed matrix
     * @return true if successful, false on error
     */
    public static boolean cs_dupl(Dcs A) {
        int i, j, p, q, nz = 0, n, m, Ap[], Ai[], w[];
        double Ax[];
        if (!Dcs_util.CS_CSC(A))
            return (false);
        /* check inputs */
        m = A.m;
        n = A.n;
        Ap = A.p;
        Ai = A.i;
        Ax = A.x;
        w = new int[m]; /* get workspace */
        for (i = 0; i < m; i++)
            w[i] = -1; /* row i not yet seen */
        for (j = 0; j < n; j++) {
            q = nz; /* column j will start at q */
            for (p = Ap[j]; p < Ap[j + 1]; p++) {
                i = Ai[p]; /* A(i,j) is nonzero */
                if (w[i] >= q) {
                    Ax[w[i]] += Ax[p]; /* A(i,j) is a duplicate */
                } else {
                    w[i] = nz; /* record where row i occurs */
                    Ai[nz] = i; /* keep A(i,j) */
                    Ax[nz++] = Ax[p];
                }
            }
            Ap[j] = q; /* record start of column j */
        }
        Ap[n] = nz; /* finalize A */
        return Dcs_util.cs_sprealloc(A, 0); /* remove extra space from A */
    }

}
