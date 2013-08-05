

package edu.emory.mathcs.csparsej.tfloat;

import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;

/**
 * Convert a triplet form to compressed-column form.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_compress {

    /**
     * C = compressed-column form of a triplet matrix T. The columns of C are
     * not sorted, and duplicate entries may be present in C.
     * 
     * @param T
     *            triplet matrix
     * @return C if successful, null on error
     */
    public static Scs cs_compress(Scs T) {
        int m, n, nz, p, k, Cp[], Ci[], w[], Ti[], Tj[];
        float Cx[], Tx[];
        Scs C;
        if (!Scs_util.CS_TRIPLET(T))
            return (null); /* check inputs */
        m = T.m;
        n = T.n;
        Ti = T.i;
        Tj = T.p;
        Tx = T.x;
        nz = T.nz;
        C = Scs_util.cs_spalloc(m, n, nz, Tx != null, false); /* allocate result */
        w = new int[n]; /* get workspace */
        Cp = C.p;
        Ci = C.i;
        Cx = C.x;
        for (k = 0; k < nz; k++)
            w[Tj[k]]++; /* column counts */
        Scs_cumsum.cs_cumsum(Cp, w, n); /* column pointers */
        for (k = 0; k < nz; k++) {
            Ci[p = w[Tj[k]]++] = Ti[k]; /* A(i,j) is the pth entry in C */
            if (Cx != null)
                Cx[p] = Tx[k];
        }
        return C;
    }
}
