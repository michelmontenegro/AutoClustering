package util;

import junit.framework.TestCase;

import org.junit.Test;

import br.rede.autoclustering.util.ChiSquare;

public class ChiSquareTest extends TestCase {

	@Test
	public void testChiSquareTest() {
		double[] a = new double[2];
		a[0] = 45;
		a[1] = 9;

		double[] b = new double[2];
		b[0] = 31;
		b[1] = 10;

		System.out.println(ChiSquare.chiSquareTest(a, b, 1, 0.005));
	}
}
