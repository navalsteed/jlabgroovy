

package edu.emory.mathcs.csparsej.tdouble.demo;

import edu.emory.mathcs.csparsej.tdouble.demo.Dcs_demo.Dproblem;

/**
 * Read a matrix, solve a linear system, update/downdate.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_demo3 {
    public static void main(String[] args) {
        Dproblem Prob = null;
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: java edu.emory.mathcs.csparsej.tdouble.demo.Dcs_demo3 fileName");
        }
        Prob = Dcs_demo.get_problem(args[0], 1e-14);
        Dcs_demo.demo3(Prob);
    }
}
