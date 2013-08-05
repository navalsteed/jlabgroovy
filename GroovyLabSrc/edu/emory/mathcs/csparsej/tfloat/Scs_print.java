

package edu.emory.mathcs.csparsej.tfloat;

import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;

/**
 * Print a sparse matrix.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_print {

    /**
     * Prints a sparse matrix.
     * 
     * @param A
     *            sparse matrix (triplet ot column-compressed)
     * @param brief
     *            print all of A if false, a few entries otherwise
     * @return true if successful, false on error
     */
    public static boolean cs_print(Scs A, boolean brief) {
        int p, j, m, n, nzmax, nz, Ap[], Ai[];
        float Ax[];
        if (A == null) {
            System.out.print("(null)\n");
            return (false);
        }
        m = A.m;
        n = A.n;
        Ap = A.p;
        Ai = A.i;
        Ax = A.x;
        nzmax = A.nzmax;
        nz = A.nz;
        System.out.print(String.format("CSparseJ Version %d.%d.%d, %s.  %s\n", Scs_common.CS_VER, Scs_common.CS_SUBVER,
                Scs_common.CS_SUBSUB, Scs_common.CS_SATE, Scs_common.CS_COPYRIGHT));
        if (nz < 0) {
            System.out.print(String.format("%d-by-%d, nzmax: %d nnz: %d, 1-norm: %g\n", m, n, nzmax, Ap[n], Scs_norm
                    .cs_norm(A)));
            for (j = 0; j < n; j++) {
                System.out.print(String.format("    col %d : locations %d to %d\n", j, Ap[j], Ap[j + 1] - 1));
                for (p = Ap[j]; p < Ap[j + 1]; p++) {
                    System.out.print(String.format("      %d : %g\n", Ai[p], Ax != null ? Ax[p] : 1));
                    if (brief && p > 20) {
                        System.out.print("  ...\n");
                        return (true);
                    }
                }
            }
        } else {
            System.out.print(String.format("triplet: %d-by-%d, nzmax: %d nnz: %d\n", m, n, nzmax, nz));
            for (p = 0; p < nz; p++) {
                System.out.print(String.format("    %d %d : %g\n", Ai[p], Ap[p], Ax != null ? Ax[p] : 1));
                if (brief && p > 20) {
                    System.out.print("  ...\n");
                    return (true);
                }
            }
        }
        return (true);
    }

}
