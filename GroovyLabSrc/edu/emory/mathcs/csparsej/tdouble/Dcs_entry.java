

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

/**
 * Add an entry to a triplet matrix.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_entry {
    /**
     * Adds an entry to a triplet matrix. Memory-space and dimension of T are
     * increased if necessary.
     * 
     * @param T
     *            triplet matrix; new entry added on output
     * @param i
     *            row index of new entry
     * @param j
     *            column index of new entry
     * @param x
     *            numerical value of new entry
     * @return true if successful, false otherwise
     */
    public static boolean cs_entry(Dcs T, int i, int j, double x) {
        if (!Dcs_util.CS_TRIPLET(T) || i < 0 || j < 0)
            return (false); /* check inputs */
        if (T.nz >= T.nzmax) {
            Dcs_util.cs_sprealloc(T, 2 * (T.nzmax));
        }
        if (T.x != null)
            T.x[T.nz] = x;
        T.i[T.nz] = i;
        T.p[T.nz++] = j;
        T.m = Math.max(T.m, i + 1);
        T.n = Math.max(T.n, j + 1);
        return (true);
    }
}
