

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

/**
 * Drop zeros from a sparse matrix.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_dropzeros {

    private static class Cs_nonzero implements Dcs_ifkeep {
        @Override
        public boolean fkeep(int i, int j, double aij, Object other) {
            return (aij != 0);
        }
    }

    /**
     * Removes numerically zero entries from a matrix.
     * 
     * @param A
     *            column-compressed matrix
     * @return nz, new number of entries in A, -1 on error
     */
    public static int cs_dropzeros(Dcs A) {
        return (Dcs_fkeep.cs_fkeep(A, new Cs_nonzero(), null)); /* keep all nonzero entries */
    }

}
