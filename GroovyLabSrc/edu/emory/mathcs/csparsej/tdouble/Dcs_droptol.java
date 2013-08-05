

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

/**
 * Drop small entries from a sparse matrix.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_droptol {

    private static class Cs_tol implements Dcs_ifkeep {
        @Override
        public boolean fkeep(int i, int j, double aij, Object other) {
            return (Math.abs(aij) > (Double) other);
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
    public static int cs_droptol(Dcs A, double tol) {
        return (Dcs_fkeep.cs_fkeep(A, new Cs_tol(), tol)); /* keep all large entries */
    }
}
