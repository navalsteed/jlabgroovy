

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

/**
 * Find elimination tree.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_etree {

    /**
     * Compute the elimination tree of A or A'A (without forming A'A).
     * 
     * @param A
     *            column-compressed matrix
     * @param ata
     *            analyze A if false, A'A oterwise
     * @return elimination tree, null on error
     */
    public static int[] cs_etree(Dcs A, boolean ata) {
        int i, k, p, m, n, inext, Ap[], Ai[], w[], parent[], ancestor[], prev[];
        if (!Dcs_util.CS_CSC(A))
            return (null); /* check inputs */
        m = A.m;
        n = A.n;
        Ap = A.p;
        Ai = A.i;
        parent = new int[n]; /* allocate result */
        w = new int[n + (ata ? m : 0)]; /* get workspace */
        ancestor = w;
        prev = w;
        int prev_offset = n;
        if (ata)
            for (i = 0; i < m; i++)
                prev[prev_offset + i] = -1;
        for (k = 0; k < n; k++) {
            parent[k] = -1; /* node k has no parent yet */
            ancestor[k] = -1; /* nor does k have an ancestor */
            for (p = Ap[k]; p < Ap[k + 1]; p++) {
                i = ata ? (prev[prev_offset + Ai[p]]) : (Ai[p]);
                for (; i != -1 && i < k; i = inext) /* traverse from i to k */
                {
                    inext = ancestor[i]; /* inext = ancestor of i */
                    ancestor[i] = k; /* path compression */
                    if (inext == -1)
                        parent[i] = k; /* no anc., parent is k */
                }
                if (ata)
                    prev[prev_offset + Ai[p]] = k;
            }
        }
        return parent;
    }

}
