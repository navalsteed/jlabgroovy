

package edu.emory.mathcs.csparsej.tfloat;

import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;

/**
 * Apply Householder reflection.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_happly {

    /**
     * Applies a Householder reflection to a dense vector, x = (I -
     * beta*v*v')*x.
     * 
     * @param V
     *            column-compressed matrix of Householder vectors
     * @param i
     *            v = V(:,i), the ith column of V
     * @param beta
     *            scalar beta
     * @param x
     *            vector x of size m
     * @return true if successful, false on error
     */
    public static boolean cs_happly(Scs V, int i, float beta, float[] x) {
        int p, Vp[], Vi[];
        float Vx[], tau = 0;
        if (!Scs_util.CS_CSC(V) || x == null)
            return (false); /* check inputs */
        Vp = V.p;
        Vi = V.i;
        Vx = V.x;
        for (p = Vp[i]; p < Vp[i + 1]; p++) /* tau = v'*x */
        {
            tau += Vx[p] * x[Vi[p]];
        }
        tau *= beta; /* tau = beta*(v'*x) */
        for (p = Vp[i]; p < Vp[i + 1]; p++) /* x = x - v*tau */
        {
            x[Vi[p]] -= Vx[p] * tau;
        }
        return (true);
    }

}
