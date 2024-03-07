package util;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

import weka.core.Instances;
import br.rede.autoclustering.util.DataBaseStructure;
import br.rede.autoclustering.util.InstancesRandomizer;

public class RandomSamplesTest extends TestCase{
	
	@Test
	public void testCreateRandomInstances(){
		DataBaseStructure db = new DataBaseStructure();
		db.loadDataBaseCSV(new File("databases/circulos.csv"));
		Instances instances = db.getNormalizedData();
		InstancesRandomizer instancesRandomizer = new InstancesRandomizer(instances, 70);
		Instances a = instancesRandomizer.getTrainInstances();
		Instances b = instancesRandomizer.getTestInstances();
		System.out.println(a+"\n\n\n"+b);
	}
}
