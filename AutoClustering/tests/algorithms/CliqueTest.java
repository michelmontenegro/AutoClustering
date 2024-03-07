package algorithms;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import weka.core.Instances;
import br.rede.autoclustering.algorithms.clique.ClustersByPartition;
import br.rede.autoclustering.algorithms.clique.DenseAreas;
import br.rede.autoclustering.core.Parameter;
import br.rede.autoclustering.structures.groups.Group;
import br.rede.autoclustering.util.ClusterViewerFrame;
import br.rede.autoclustering.util.DataBaseStructure;

public class CliqueTest {


	@Test
	public void testClusters() {
		long first = System.currentTimeMillis();
		DataBaseStructure db = new DataBaseStructure();
		db.loadDataBaseCSV(new File("uci-databases/pima-indians-diabetes/pima-indians-diabetes.data"));
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
		
		Map<Parameter, Object> sharedObjects = new HashMap<Parameter, Object>();
		sharedObjects.put(Parameter.ALL_INSTANCES, instances);
		sharedObjects.put(Parameter.ALL_LOWER_BOUNDS, lowerBounds);
		sharedObjects.put(Parameter.ALL_UPPER_BOUNDS, upperBounds);
		sharedObjects.put(Parameter.AMR_DENSITY, 1);
		sharedObjects.put(Parameter.AMR_LAMBDA, 2);
		sharedObjects.put(Parameter.AMR_SLICES, 35);
		sharedObjects.put(Parameter.CLIQUE_SLICES, 16);
		sharedObjects.put(Parameter.CLIQUE_THRESHOLD, 0.2f);
//		CliqueProperties cliqueProperties = new CliqueProperties(30f,0.001f);
		
		DenseAreas denseAreas = new DenseAreas(/*cliqueProperties*/);
//		try {
//			MethodPropertyValidator.validate(cliqueProperties, 10);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		ClustersByPartition partition = new ClustersByPartition(/*cliqueProperties*/);

		denseAreas.executeStep(sharedObjects);

		partition.executeStep(sharedObjects);	
		List<Group> clusters = (List<Group>) sharedObjects.get(Parameter.ALL_GROUPS);
		new  ClusterViewerFrame(db.getNormalizedData(),clusters);
		System.out.println("Tempo total: "+(System.currentTimeMillis() - first)/1000);
	}

}
