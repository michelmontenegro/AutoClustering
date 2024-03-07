package structures;

import junit.framework.TestCase;

import org.junit.Test;

import br.rede.autoclustering.util.ChiSquare;
import br.rede.autoclustering.util.PoissonProcess;



public class PoissonTest extends TestCase{

	@Test
	public void testPoisson(){
//		try {
//			System.out.println("combination "+PoissonProcess.combination(50,12));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		float sum = 0;
//		for (int i = 0; i < 3; i++) {
//			sum += (PoissonProcess.calculatePoissonMassFunction(2, i));
//			System.out.println(PoissonProcess.calculatePoissonMassFunction(2, i)+" "+i);
//		}
//		System.out.println();
//		System.out.println("SUM: "+PoissonProcess.calculatePoissonProbabilityDistribution(2, (int) PoissonProcess.calculatePoissonMassFunction(2, 5)));
		
//		System.out.println(PoissonProcess.calculatePoisson(1, 0.5, 2, 2));
		double [] ad = {1,1};
		System.out.println(ChiSquare.chiSquareTest(ad,ad,1, 0.5));
		
	}
}
