

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

/**
 * Drop entries from a sparse matrix.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_fkeep {

    /**
     * Drops entries from a sparse matrix;
     * 
     * @param A
     *            column-compressed matrix
     * @param fkeep
     *            drop aij if fkeep.fkeep(i,j,aij,other) is false
     * @param other
     *            optional parameter to fkeep
     * @return nz, new number of entries in A, -1 on error
     */
    public static int cs_fkeep(Dcs A, Dcs_ifkeep fkeep, Object other) {
        int j, p, nz = 0, n, Ap[], Ai[];
        double Ax[];
        if (!Dcs_util.CS_CSC(A))
            return (-1); /* check inputs */
        n = A.n;
        Ap = A.p;
        Ai = A.i;
        Ax = A.x;
        for (j = 0; j < n; j++) {
            p = Ap[j]; /* get current location of col j */
            Ap[j] = nz; /* record new location of col j */
            for (; p < Ap[j + 1]; p++) {
                if (fkeep.fkeep(Ai[p], j, Ax != null ? Ax[p] : 1, other)) {
                    if (Ax != null)
                        Ax[nz] = Ax[p]; /* keep A(i,j) */
                    Ai[nz++] = Ai[p];
                }
            }
        }
        Ap[n] = nz; /* finalize A */
        Dcs_util.cs_sprealloc(A, 0); /* remove extra space from A */
        return (nz);
    }

}
