/*
 * Created on Jul 10, 2004
 */
package de.torstennahm.statistics;

import java.util.Random;

import de.torstennahm.math.MathTN;
import junit.framework.TestCase;

/**
 * JUnit test.
 * 
 * @author Torsten Nahm
 */
public class JUnitTest extends TestCase {
	/**
	 * Tests <code>Statistics</code>.
	 */
	public void testStatistics() {
		Random r = new Random(10);
		
		for (int i = 0; i < 10; i++) {
			double[] s = new double[r.nextInt(50) + 1];
			Statistics st = new Statistics();
			for (int j = 0; j < s.length; j++) {
				s[j] = r.nextDouble() * 2.0;
				st.add(s[j]);
			}
			
			double average = 0.0;
			for (int j = 0; j < s.length; j++) {
				average += s[j] / s.length;
			}
			
			double variance = 0.0;
			for (int j = 0; j < s.length; j++) {
				variance += (s[j] - average) * (s[j] - average) / s.length;
			}
			
			assertTrue(Math.abs(average - st.average()) < MathTN.FUDGE);
			if (s.length > 1) {
				assertTrue(Math.abs(variance - st.variance()) < MathTN.FUDGE);
			}
		}
	}
}
