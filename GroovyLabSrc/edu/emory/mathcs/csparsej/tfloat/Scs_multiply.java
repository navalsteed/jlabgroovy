
package edu.emory.mathcs.csparsej.tfloat;

import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;

/**
 * Sparse matrix multiply.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_multiply {

    /**
     * Sparse matrix multiplication, C = A*B
     * 
     * @param A
     *            column-compressed matrix
     * @param B
     *            column-compressed matrix
     * @return C = A*B, null on error
     */
    public static Scs cs_multiply(Scs A, Scs B) {
        int p, j, nz = 0, anz, Cp[], Ci[], Bp[], m, n, bnz, w[], Bi[];
        float x[], Bx[], Cx[];
        boolean values;
        Scs C;
        if (!Scs_util.CS_CSC(A) || !Scs_util.CS_CSC(B))
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
        x = values ? new float[m] : null; /* get workspace */
        C = Scs_util.cs_spalloc(m, n, anz + bnz, values, false); /* allocate result */
        Cp = C.p;
        for (j = 0; j < n; j++) {
            if (nz + m > C.nzmax) {
                Scs_util.cs_sprealloc(C, 2 * (C.nzmax) + m);
            }
            Ci = C.i;
            Cx = C.x; /* C.i and C.x may be reallocated */
            Cp[j] = nz; /* column j of C starts here */
            for (p = Bp[j]; p < Bp[j + 1]; p++) {
                nz = Scs_scatter.cs_scatter(A, Bi[p], (Bx != null) ? Bx[p] : 1, w, x, j + 1, C, nz);
            }
            if (values)
                for (p = Cp[j]; p < nz; p++)
                    Cx[p] = x[Ci[p]];
        }
        Cp[n] = nz; /* finalize the last column of C */
        Scs_util.cs_sprealloc(C, 0); /* remove extra space from C */
        return C;
    }

}
