

package edu.emory.mathcs.csparsej.tfloat;

import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;

/**
 * Srop zeros from a sparse matrix.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_dropzeros {

    private static class Cs_nonzero implements Scs_ifkeep {
        @Override
        public boolean fkeep(int i, int j, float aij, Object other) {
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
    public static int cs_dropzeros(Scs A) {
        return (Scs_fkeep.cs_fkeep(A, new Cs_nonzero(), null)); /* keep all nonzero entries */
    }

}
