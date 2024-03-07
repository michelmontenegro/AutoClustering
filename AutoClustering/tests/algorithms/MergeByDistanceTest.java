package algorithms;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import weka.core.Instances;
import br.rede.autoclustering.algorithms.dbscan.CandidatesByDistance;
import br.rede.autoclustering.algorithms.descry.MergeByDistance;
import br.rede.autoclustering.core.Parameter;
import br.rede.autoclustering.util.DataBaseStructure;
import br.rede.autoclustering.util.DistanceType;

public class MergeByDistanceTest {

		@Test
		public void testRodar() {
			DataBaseStructure db = new DataBaseStructure();
			db.loadDataBaseCSV(new File("/home/sfelixjr/dbclasd.in"));
			CandidatesByDistance byDistance = new CandidatesByDistance(/*new DBScanProperties(3, 5)*/);
//			AdaptableKDTree kdTree = new AdaptableKDTree(new DescryProperties(5,3));
//			AMRTree amr = new AMRTree(new AMRProperties(10, 5,2));
			MergeByDistance distance = new MergeByDistance(/*new DescryProperties(5,3)*/);
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
//			sharedObjects.put(Parameter.AMR_DENSITY, 1);
//			sharedObjects.put(Parameter.AMR_SLICES, 35);
//			sharedObjects.put(Parameter.AMR_LAMBDA, 1);
			sharedObjects.put(Parameter.DBSCAN_NUM_PTS, 3);
			sharedObjects.put(Parameter.DBSCAN_MAX_DIST, 3);
			sharedObjects.put(Parameter.ALL_DISTANCE, DistanceType.EUCLIDEAN);
			sharedObjects.put(Parameter.ALL_INSTANCES, instances);
			sharedObjects.put(Parameter.ALL_LOWER_BOUNDS, lowerBounds);
			sharedObjects.put(Parameter.ALL_UPPER_BOUNDS, upperBounds);
			sharedObjects.put(Parameter.DESCRY_DENSITY, 1);
			sharedObjects.put(Parameter.DESCRY_K,3);
			
//			EquallySizedGrid equallySizedGrid2 = new EquallySizedGrid(null);
//			equallySizedGrid2.executeStep(sharedObjects);
			//kdTree.executeStep(sharedObjects);
			byDistance.executeStep(sharedObjects);
//			amr.executeStep(sharedObjects);
			distance.executeStep(sharedObjects);
			
//			new ClusterViewerFrame("", instances, (List<Group>) sharedObjects.get(Parameter.ALL_GROUPS));
			
			try {
				Thread.sleep(200000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

}
