package algorithms;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import weka.core.Instances;
import br.rede.autoclustering.algorithms.dbclasd.ClustersByDistribution;
import br.rede.autoclustering.algorithms.denclue.ASH;
import br.rede.autoclustering.core.Parameter;
import br.rede.autoclustering.structures.groups.Group;
import br.rede.autoclustering.util.ClusterViewerFrame;
import br.rede.autoclustering.util.DataBaseStructure;
import br.rede.autoclustering.util.DistanceType;

public class DBClasd2Test extends TestCase{

	@Test
	public void testDBClasd(){
		DataBaseStructure db = new DataBaseStructure();
		db.loadDataBaseCSV(new File("/home/sfelixjr/dbclasd.in"));
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
		sharedObjects.put(Parameter.ALL_DISTANCE, DistanceType.EUCLIDEAN);
		sharedObjects.put(Parameter.ALL_INSTANCES, instances);
		sharedObjects.put(Parameter.ALL_LOWER_BOUNDS, lowerBounds);
		sharedObjects.put(Parameter.ALL_UPPER_BOUNDS, upperBounds);
		sharedObjects.put(Parameter.DBCLASD_PTS, 3);
		sharedObjects.put(Parameter.DBSCAN_NUM_PTS, 2);
		sharedObjects.put(Parameter.DBSCAN_MAX_DIST, 2);
		sharedObjects.put(Parameter.DENCLUE_EPSILON, 4);
		sharedObjects.put(Parameter.DENCLUE_SIGMA, 2);
		
//		CandidatesByNpts byNpts = new CandidatesByNpts(new DBClasdProperties(3));
//		byNpts.executeStep(sharedObjects);
		
//		CandidatesByDistance byDistance = new CandidatesByDistance(null);
//		byDistance.executeStep(sharedObjects);
		
		ASH ash = new ASH();
		ash.executeStep(sharedObjects);
		
		ClustersByDistribution byDistribution = new ClustersByDistribution();
		byDistribution.executeStep(sharedObjects);
		new ClusterViewerFrame(instances, (List<Group>) sharedObjects.get(Parameter.ALL_GROUPS));
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}