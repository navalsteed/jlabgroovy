

package edu.emory.mathcs.csparsej.tfloat.demo;

import edu.emory.mathcs.csparsej.tfloat.Scs_add;
import edu.emory.mathcs.csparsej.tfloat.Scs_compress;
import edu.emory.mathcs.csparsej.tfloat.Scs_entry;
import edu.emory.mathcs.csparsej.tfloat.Scs_load;
import edu.emory.mathcs.csparsej.tfloat.Scs_multiply;
import edu.emory.mathcs.csparsej.tfloat.Scs_norm;
import edu.emory.mathcs.csparsej.tfloat.Scs_print;
import edu.emory.mathcs.csparsej.tfloat.Scs_transpose;
import edu.emory.mathcs.csparsej.tfloat.Scs_util;
import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;

/**
 * Read a matrix from a file and perform basic matrix operations.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_demo1 {
    public static void main(String[] args) {
        Scs T = null, A, Eye, AT, C, S;
        int i, m;
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: java edu.emory.mathcs.csparsej.tfloat.semo.Scs_demo1 fileName");
        }
        T = Scs_load.cs_load(args[0]); /* load triplet matrix T from file */
        System.out.print("T:\n");
        Scs_print.cs_print(T, false); /* print T */
        A = Scs_compress.cs_compress(T); /* A = compressed-column form of T */
        System.out.print("A:\n");
        Scs_print.cs_print(A, false); /* print A */
        T = null; /* clear T */
        AT = Scs_transpose.cs_transpose(A, true); /* AT = A' */
        System.out.print("AT:\n");
        Scs_print.cs_print(AT, false); /* print AT */
        m = A != null ? A.m : 0; /* m = # of rows of A */
        T = Scs_util.cs_spalloc(m, m, m, true, true); /* create triplet identity matrix */
        for (i = 0; i < m; i++)
            Scs_entry.cs_entry(T, i, i, 1);
        Eye = Scs_compress.cs_compress(T); /* Eye = speye (m) */
        T = null;
        C = Scs_multiply.cs_multiply(A, AT); /* C = A*A' */
        S = Scs_add.cs_add(C, Eye, 1, Scs_norm.cs_norm(C)); /* S = C + Eye*norm (C,1) */
        System.out.print("S:\n");
        Scs_print.cs_print(S, false); /* print S */
    }
}
