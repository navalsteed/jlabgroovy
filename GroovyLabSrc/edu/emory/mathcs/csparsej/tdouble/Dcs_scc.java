

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;
import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcsd;

/**
 * Strongly-connected components.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_scc {
    /**
     * Finds the strongly connected components of a square matrix.
     * 
     * @param A
     *            column-compressed matrix (A.p modified then restored)
     * @return strongly connected components, null on error
     */
    public static Dcsd cs_scc(Dcs A) {
        int n, i, k, b, nb = 0, top, xi[], pstack[], p[], r[], Ap[], ATp[], rcopy[], Blk[];
        Dcs AT;
        Dcsd D;
        if (!Dcs_util.CS_CSC(A))
            return (null); /* check inputs */
        n = A.n;
        Ap = A.p;
        D = Dcs_util.cs_dalloc(n, 0); /* allocate result */
        AT = Dcs_transpose.cs_transpose(A, false); /* AT = A' */
        xi = new int[2 * n + 1]; /* get workspace */
        if (D == null || AT == null)
            return (null);
        Blk = xi;
        rcopy = xi;
        int rcopy_offset = n;
        pstack = xi;
        int pstack_offset = n;
        p = D.p;
        r = D.r;
        ATp = AT.p;
        top = n;
        for (i = 0; i < n; i++) /* first dfs(A) to find finish times (xi) */
        {
            if (!Dcs_util.CS_MARKED(Ap, i))
                top = Dcs_dfs.cs_dfs(i, A, top, xi, 0, pstack, pstack_offset, null, 0);
        }
        for (i = 0; i < n; i++)
            Dcs_util.CS_MARK(Ap, i); /* restore A; unmark all nodes*/
        top = n;
        nb = n;
        for (k = 0; k < n; k++) /* dfs(A') to find strongly connnected comp */
        {
            i = xi[k]; /* get i in reverse order of finish times */
            if (Dcs_util.CS_MARKED(ATp, i))
                continue; /* skip node i if already ordered */
            r[nb--] = top; /* node i is the start of a component in p */
            top = Dcs_dfs.cs_dfs(i, AT, top, p, 0, pstack, pstack_offset, null, 0);
        }
        r[nb] = 0; /* first block starts at zero; shift r up */
        for (k = nb; k <= n; k++)
            r[k - nb] = r[k];
        D.nb = nb = n - nb; /* nb = # of strongly connected components */
        for (b = 0; b < nb; b++) /* sort each block in natural order */
        {
            for (k = r[b]; k < r[b + 1]; k++)
                Blk[p[k]] = b;
        }
        for (b = 0; b <= nb; b++)
            rcopy[rcopy_offset + b] = r[b];
        for (i = 0; i < n; i++)
            p[rcopy[rcopy_offset + Blk[i]]++] = i;
        return D;
    }

}
