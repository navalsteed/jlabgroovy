

package edu.emory.mathcs.csparsej.tdouble;

/**
 * Interface for Dcs_fkeep.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public interface Dcs_ifkeep {

    /**
     * Function used for entries from a sparse matrix
     * 
     * @param i
     *            row index
     * @param j
     *            column index
     * @param aij
     *            value
     * @param other
     *            optional parameter
     * @return if false then aij should be dropped
     */
    public boolean fkeep(int i, int j, double aij, Object other);
}
