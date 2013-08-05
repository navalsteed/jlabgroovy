

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

/**
 * Solve a lower triangular system U'x=b.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_utsolve {
    
    /**
     * Solves a lower triangular system U'x=b, where x and b are dense vectors.
     * The diagonal of U must be the last entry of each column.
     * 
     * @param U
     *            upper triangular matrix in column-compressed form
     * @param x
     *            size n, right hand side on input, solution on output
     * @return true if successful, false on error
     */
    public static boolean cs_utsolve(Dcs U, double[] x) {
        int p, j, n, Up[], Ui[];
        double Ux[];
        if (!Dcs_util.CS_CSC(U) || x == null)
            return (false); /* check inputs */
        n = U.n;
        Up = U.p;
        Ui = U.i;
        Ux = U.x;
        for (j = 0; j < n; j++) {
            for (p = Up[j]; p < Up[j + 1] - 1; p++) {
                x[j] -= Ux[p] * x[Ui[p]];
            }
            x[j] /= Ux[Up[j + 1] - 1];
        }
        return (true);
    }

}
