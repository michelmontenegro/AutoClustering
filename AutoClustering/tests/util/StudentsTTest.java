package util;

import junit.framework.TestCase;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.inference.TTest;
import org.apache.commons.math.stat.inference.TTestImpl;
import org.junit.Test;

import br.rede.autoclustering.util.StudentsT;

import de.lmu.ifi.dbs.elki.math.statistics.StudentDistribution;

public class StudentsTTest extends TestCase{
	
	@Test
	public void testT() throws Exception{
		
		double pvalue = StudentsT.pairedTTest("runs/summary.csv");
		System.out.println(pvalue);
	}

}
