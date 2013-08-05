

package edu.emory.mathcs.csparsej.tfloat;

import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;

/**
 * Add an entry to a triplet matrix.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_entry {
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
    public static boolean cs_entry(Scs T, int i, int j, float x) {
        if (!Scs_util.CS_TRIPLET(T) || i < 0 || j < 0)
            return (false); /* check inputs */
        if (T.nz >= T.nzmax) {
            Scs_util.cs_sprealloc(T, 2 * (T.nzmax));
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
