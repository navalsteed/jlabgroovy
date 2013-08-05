

package edu.emory.mathcs.csparsej.tfloat;

import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;

/**
 * Srop small entries from a sparse matrix.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_droptol {

    private static class Cs_tol implements Scs_ifkeep {
        @Override
        public boolean fkeep(int i, int j, float aij, Object other) {
            return (Math.abs(aij) > (Float) other);
        }
    }

    /**
     * Removes entries from a matrix with absolute value <= tol.
     * 
     * @param A
     *            column-compressed matrix
     * @param tol
     *            drop tolerance
     * @return nz, new number of entries in A, -1 on error
     */
    public static int cs_droptol(Scs A, float tol) {
        return (Scs_fkeep.cs_fkeep(A, new Cs_tol(), tol)); /* keep all large entries */
    }
}
