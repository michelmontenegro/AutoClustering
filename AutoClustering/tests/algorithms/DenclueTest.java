package algorithms;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import weka.core.Instances;
import br.rede.autoclustering.algorithms.denclue.ASH;
import br.rede.autoclustering.algorithms.denclue.ClustersByAttractors;
import br.rede.autoclustering.core.ClusteringMethod;
import br.rede.autoclustering.core.Parameter;
import br.rede.autoclustering.structures.groups.Group;
import br.rede.autoclustering.util.ClusterViewerFrame;
import br.rede.autoclustering.util.DataBaseStructure;
import br.rede.autoclustering.util.DistanceType;

public class DenclueTest {

	@Test
	public void testDenclue(){
		long first = System.currentTimeMillis();
		DataBaseStructure db = new DataBaseStructure();
		db.loadDataBaseCSV(new File("databases/dbclasd.in"));
		List<ClusteringMethod> methods = new ArrayList<ClusteringMethod>();
		Instances instances = db.getNormalizedData();
		
		double[] lowerBounds = new double[instances.numAttributes()],upperBounds = new double[instances.numAttributes()];
		Arrays.fill(lowerBounds, Double.MAX_VALUE);
		Arrays.fill(upperBounds, Double.MIN_VALUE);
		for (int i = 0; i < instances.numInstances(); i++) {
			for (int j = 0; j < instances.numAttributes(); j++) { 
				if ( instances.instance(i).value(j) < lowerBounds[j] )
					lowerBounds[j] = instances.instance(i).value(j);
				if ( instances.instance(i).value(j) > upperBounds[j] )
					upperBounds[j] = instances.instance(i).value(j);
			}
		}
//		DenclueProperties denclueProperties = new DenclueProperties(2, 2);
//		try {
//			MethodPropertyValidator.validate(denclueProperties, 10);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		ASH ash = new ASH(/*denclueProperties*/);
		ClustersByAttractors cba = new ClustersByAttractors(/*denclueProperties*/);
		methods.add(ash);
		methods.add(cba);

		Map<Parameter, Object> sharedObjects = new HashMap<Parameter, Object>();
		sharedObjects.put(Parameter.ALL_INSTANCES,instances );
		sharedObjects.put(Parameter.ALL_DISTANCE,DistanceType.EUCLIDEAN);
		sharedObjects.put(Parameter.ALL_LOWER_BOUNDS, lowerBounds);
		sharedObjects.put(Parameter.ALL_UPPER_BOUNDS, upperBounds);
		sharedObjects.put(Parameter.DENCLUE_SIGMA, 3.8);
		sharedObjects.put(Parameter.DENCLUE_EPSILON, 1);
		
		for ( ClusteringMethod clusteringMethod : methods )
			clusteringMethod.executeStep(sharedObjects);
			
		List<Group> clusters = (List<Group>) sharedObjects.get(Parameter.ALL_GROUPS);
		new  ClusterViewerFrame(instances,clusters);
		System.out.println("Tempo total: "+(System.currentTimeMillis() - first)/1000);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
