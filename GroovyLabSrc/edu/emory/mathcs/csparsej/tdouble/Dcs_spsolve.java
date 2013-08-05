

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

/**
 * Sparse lower or upper triangular solve. x=G\b where G, x, and b are sparse,
 * and G upper/lower triangular.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_spsolve {
    /**
     * solve Gx=b(:,k), where G is either upper (lo=false) or lower (lo=true)
     * triangular.
     * 
     * @param G
     *            lower or upper triangular matrix in column-compressed form
     * @param B
     *            right hand side, b=B(:,k)
     * @param k
     *            use kth column of B as right hand side
     * @param xi
     *            size 2*n, nonzero pattern of x in xi[top..n-1]
     * @param x
     *            size n, x in x[xi[top..n-1]]
     * @param pinv
     *            mapping of rows to columns of G, ignored if null
     * @param lo
     *            true if lower triangular, false if upper
     * @return top, -1 in error
     */
    public static int cs_spsolve(Dcs G, Dcs B, int k, int[] xi, double[] x, int[] pinv, boolean lo) {
        int j, J, p, q, px, top, n, Gp[], Gi[], Bp[], Bi[];
        double Gx[], Bx[];
        if (!Dcs_util.CS_CSC(G) || !Dcs_util.CS_CSC(B) || xi == null || x == null)
            return (-1);
        Gp = G.p;
        Gi = G.i;
        Gx = G.x;
        n = G.n;
        Bp = B.p;
        Bi = B.i;
        Bx = B.x;
        top = Dcs_reach.cs_reach(G, B, k, xi, pinv); /* xi[top..n-1]=Reach(B(:,k)) */
        for (p = top; p < n; p++)
            x[xi[p]] = 0; /* clear x */
        for (p = Bp[k]; p < Bp[k + 1]; p++)
            x[Bi[p]] = Bx[p]; /* scatter B */
        for (px = top; px < n; px++) {
            j = xi[px]; /* x(j) is nonzero */
            J = pinv != null ? (pinv[j]) : j; /* j maps to col J of G */
            if (J < 0)
                continue; /* column J is empty */
            x[j] /= Gx[lo ? (Gp[J]) : (Gp[J + 1] - 1)];/* x(j) /= G(j,j) */
            p = lo ? (Gp[J] + 1) : (Gp[J]); /* lo: L(j,j) 1st entry */
            q = lo ? (Gp[J + 1]) : (Gp[J + 1] - 1); /* up: U(j,j) last entry */
            for (; p < q; p++) {
                x[Gi[p]] -= Gx[p] * x[j]; /* x(i) -= G(i,j) * x(j) */
            }
        }
        return (top); /* return top of stack */
    }
}
