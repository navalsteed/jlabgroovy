

package edu.emory.mathcs.csparsej.tfloat.demo;

import edu.emory.mathcs.csparsej.tfloat.demo.Scs_demo.Sproblem;

/**
 * Read a matrix, solve a linear system, update/downdate.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_demo3 {
    public static void main(String[] args) {
        Sproblem Prob = null;
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: java edu.emory.mathcs.csparsej.tfloat.semo.Scs_demo3 fileName");
        }
        Prob = Scs_demo.get_problem(args[0], 1e-14f);
        Scs_demo.demo3(Prob);
    }
}
