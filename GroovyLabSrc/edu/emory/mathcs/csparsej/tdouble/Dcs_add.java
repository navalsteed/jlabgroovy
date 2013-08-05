

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

/**
 * Add sparse matrices.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_add {
    /**
     * C = alpha*A + beta*B
     * 
     * @param A
     *            column-compressed matrix
     * @param B
     *            column-compressed matrix
     * @param alpha
     *            scalar alpha
     * @param beta
     *            scalar beta
     * @return C=alpha*A + beta*B, null on error
     */
    public static Dcs cs_add(Dcs A, Dcs B, double alpha, double beta) {
        int p, j, nz = 0, anz;
        int Cp[], Ci[], Bp[], m, n, bnz, w[];
        double x[], Bx[], Cx[];
        boolean values;
        Dcs C;
        if (!Dcs_util.CS_CSC(A) || !Dcs_util.CS_CSC(B))
            return null; /* check inputs */
        if (A.m != B.m || A.n != B.n)
            return null;
        m = A.m;             // number of rows
        anz = A.p[A.n];  // non-zero elements of matrix A
        n = B.n;     // number of columns 
        Bp = B.p;   
        Bx = B.x;  // data elements of matrix B
        bnz = Bp[n];
        w = new int[m]; /* get workspace */
        values = (A.x != null) && (Bx != null);
        x = values ? new double[m] : null; /* get workspace */
        C = Dcs_util.cs_spalloc(m, n, anz + bnz, values, false); /* allocate result*/
        Cp = C.p;
        Ci = C.i;
        Cx = C.x;
        for (j = 0; j < n; j++) {
            Cp[j] = nz; /* column j of C starts here */
            nz = Dcs_scatter.cs_scatter(A, j, alpha, w, x, j + 1, C, nz); /* alpha*A(:,j)*/
            nz = Dcs_scatter.cs_scatter(B, j, beta, w, x, j + 1, C, nz); /* beta*B(:,j) */
            if (values)
                for (p = Cp[j]; p < nz; p++)
                    Cx[p] = x[Ci[p]];
        }
        Cp[n] = nz; /* finalize the last column of C */
        Dcs_util.cs_sprealloc(C, 0); /* remove extra space from C */
        return C; /* success; free workspace, return C */
    }

}
