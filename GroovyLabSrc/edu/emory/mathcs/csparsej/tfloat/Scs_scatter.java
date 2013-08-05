

package edu.emory.mathcs.csparsej.tfloat;

import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;

/**
 * Scatter a sparse vector.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_scatter {

    /**
     * Scatters and sums a sparse vector A(:,j) into a dense vector, x = x +
     * beta * A(:,j).
     * 
     * @param A
     *            the sparse vector is A(:,j)
     * @param j
     *            the column of A to use
     * @param beta
     *            scalar multiplied by A(:,j)
     * @param w
     *            size m, node i is marked if w[i] = mark
     * @param x
     *            size m, ignored if null
     * @param mark
     *            mark value of w
     * @param C
     *            pattern of x accumulated in C.i
     * @param nz
     *            pattern of x placed in C starting at C.i[nz]
     * @return new value of nz, -1 on error
     */
    public static int cs_scatter(Scs A, int j, float beta, int[] w, float[] x, int mark, Scs C, int nz) {
        int i, p;
        int Ap[], Ai[], Ci[];
        float[] Ax;
        if (!Scs_util.CS_CSC(A) || w == null || !Scs_util.CS_CSC(C))
            return (-1); /* check inputs */
        Ap = A.p;
        Ai = A.i;
        Ax = A.x;
        Ci = C.i;
        for (p = Ap[j]; p < Ap[j + 1]; p++) {
            i = Ai[p]; /* A(i,j) is nonzero */
            if (w[i] < mark) {
                w[i] = mark; /* i is new entry in column j */
                Ci[nz++] = i; /* add i to pattern of C(:,j) */
                if (x != null)
                    x[i] = beta * Ax[p]; /* x(i) = beta*A(i,j) */
            } else if (x != null)
                x[i] += beta * Ax[p]; /* i exists in C(:,j) already */
        }
        return nz;
    }
}
