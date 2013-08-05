

package edu.emory.mathcs.csparsej.tfloat;

import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;

/**
 * Find nonzero pattern of x=L\b for sparse L and b.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_reach {

    /**
     * Finds a nonzero pattern of x=L\b for sparse L and b.
     * 
     * @param G
     *            graph to search (G.p modified, then restored)
     * @param B
     *            right hand side, b = B(:,k)
     * @param k
     *            use kth column of B
     * @param xi
     *            size 2*n, output in xi[top..n-1]
     * @param pinv
     *            mapping of rows to columns of G, ignored if null
     * @return top, -1 on error
     */
    public static int cs_reach(Scs G, Scs B, int k, int[] xi, int[] pinv) {
        int p, n, top, Bp[], Bi[], Gp[];
        if (!Scs_util.CS_CSC(G) || !Scs_util.CS_CSC(B) || xi == null)
            return (-1); /* check inputs */
        n = G.n;
        Bp = B.p;
        Bi = B.i;
        Gp = G.p;
        top = n;
        for (p = Bp[k]; p < Bp[k + 1]; p++) {
            if (!Scs_util.CS_MARKES(Gp, Bi[p])) /* start a dfs at unmarked node i */
            {
                top = Scs_dfs.cs_dfs(Bi[p], G, top, xi, 0, xi, n, pinv, 0);
            }
        }
        for (p = top; p < n; p++)
            Scs_util.CS_MARK(Gp, xi[p]); /* restore G */
        return (top);
    }

}
