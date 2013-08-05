

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

/**
 * Sparse matrix multiply.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_multiply {

    /**
     * Sparse matrix multiplication, C = A*B
     * 
     * @param A
     *            column-compressed matrix
     * @param B
     *            column-compressed matrix
     * @return C = A*B, null on error
     */
    public static Dcs cs_multiply(Dcs A, Dcs B) {
        int p, j, nz = 0, anz, Cp[], Ci[], Bp[], m, n, bnz, w[], Bi[];
        double x[], Bx[], Cx[];
        boolean values;
        Dcs C;
        if (!Dcs_util.CS_CSC(A) || !Dcs_util.CS_CSC(B))
            return (null); /* check inputs */
        if (A.n != B.m)
            return (null);
        m = A.m;
        anz = A.p[A.n];
        n = B.n;
        Bp = B.p;
        Bi = B.i;
        Bx = B.x;
        bnz = Bp[n];
        w = new int[m]; /* get workspace */
        values = (A.x != null) && (Bx != null);
        x = values ? new double[m] : null; /* get workspace */
        C = Dcs_util.cs_spalloc(m, n, anz + bnz, values, false); /* allocate result */
        Cp = C.p;
        for (j = 0; j < n; j++) {
            if (nz + m > C.nzmax) {
                Dcs_util.cs_sprealloc(C, 2 * (C.nzmax) + m);
            }
            Ci = C.i;
            Cx = C.x; /* C.i and C.x may be reallocated */
            Cp[j] = nz; /* column j of C starts here */
            for (p = Bp[j]; p < Bp[j + 1]; p++) {
                nz = Dcs_scatter.cs_scatter(A, Bi[p], (Bx != null) ? Bx[p] : 1, w, x, j + 1, C, nz);
            }
            if (values)
                for (p = Cp[j]; p < nz; p++)
                    Cx[p] = x[Ci[p]];
        }
        Cp[n] = nz; /* finalize the last column of C */
        Dcs_util.cs_sprealloc(C, 0); /* remove extra space from C */
        return C;
    }

}
