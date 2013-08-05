/*
 * Created on Jan 22, 2005
 */
package de.torstennahm.series;

import junit.framework.TestCase;

/**
 * @author Torsten Nahm
 */
public class JUnitTest extends TestCase {
	public void testVanDerCorput() {
		for (int i = 2; i < 1000; i++) {
			VanDerCorput v1 = new VanDerCorput(i);
			for (int j = 0; j < 200000; j++) {
				VanDerCorput v2 = new VanDerCorput(i, j);
				double n1 = v1.next();
				double n2 = v2.next();
				System.out.println(n1 + " " + n2);
				assertTrue(0.0 < n1 && n1 < 1.0);
				assertTrue(n1 == n2);
			}
		}
	}
}
