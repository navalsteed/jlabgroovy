

package edu.emory.mathcs.csparsej.tfloat;

import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;

/**
 * Solve an upper triangular system L'x=b.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_ltsolve {
    /**
     * Solves an upper triangular system L'x=b where x and b are dense. x=b on
     * input, solution on output.
     * 
     * @param L
     *            column-compressed, lower triangular matrix
     * @param x
     *            size n, right hand side on input, solution on output
     * @return true if successful, false on error
     */
    public static boolean cs_ltsolve(Scs L, float[] x) {
        int p, j, n, Lp[], Li[];
        float Lx[];
        if (!Scs_util.CS_CSC(L) || x == null)
            return (false); /* check inputs */
        n = L.n;
        Lp = L.p;
        Li = L.i;
        Lx = L.x;
        for (j = n - 1; j >= 0; j--) {
            for (p = Lp[j] + 1; p < Lp[j + 1]; p++) {
                x[j] -= Lx[p] * x[Li[p]];
            }
            x[j] /= Lx[Lp[j]];
        }
        return (true);
    }

}
