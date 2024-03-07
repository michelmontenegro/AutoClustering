package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import br.rede.autoclustering.util.FitnessChart;

import junit.framework.TestCase;


public class FitnessChartTest extends TestCase{

	
	public void testFitnessChart(){
		try {
			
			Scanner s = new Scanner(new File("runs/glass0.txt"));
			while( s.hasNext() ) {
				String[] a = s.nextLine().split(",");
				FitnessChart.getChart().addValue(Double.parseDouble(a[1]));
			}
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
