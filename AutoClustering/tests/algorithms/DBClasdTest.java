package algorithms;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import weka.core.Instances;
import br.rede.autoclustering.algorithms.dbclasd.CandidatesByNpts;
import br.rede.autoclustering.algorithms.dbclasd.ClustersByDistribution;
import br.rede.autoclustering.algorithms.dbscan.ClustersByConnectiveness;
import br.rede.autoclustering.core.Parameter;
import br.rede.autoclustering.structures.groups.Group;
import br.rede.autoclustering.util.ClusterViewerFrame;
import br.rede.autoclustering.util.DataBaseStructure;
import br.rede.autoclustering.util.DistanceType;

public class DBClasdTest extends TestCase {

	@Test
	public void testDBClasd() throws FileNotFoundException{
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
		sharedObjects.put(Parameter.DBSCAN_NUM_PTS, 5);
		sharedObjects.put(Parameter.DBSCAN_MAX_DIST, 2);
		CandidatesByNpts byNpts = new CandidatesByNpts(/*new DBClasdProperties(3)*/);
		byNpts.executeStep(sharedObjects);
		
		ClustersByConnectiveness byConnectiveness = new ClustersByConnectiveness(/*new DBScanProperties(3,2)*/);
		byConnectiveness.executeStep(sharedObjects);
		
		List<Group> group = (List<Group>) sharedObjects.get(Parameter.ALL_GROUPS);
		
		new  ClusterViewerFrame(instances,group);
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
//		long first = System.currentTimeMillis();
//		db.loadDataBaseCSV(new File("/home/sfelixjr/dbclasd.in"));
//		Instances instances = db.getDatabase();
//		DBClasd clasd = new DBClasd();
//		List<Group> clusters = clasd.buildClusterer(instances);
//		new  ClusterViewerFrame("DBClasd",db.getDatabase(), clusters);
//		System.out.println((System.currentTimeMillis() - first)/1000);
//		try {
//			Thread.sleep(100000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
	public void testVolume(){
		//point
		assertEquals(1.0,ClustersByDistribution.calculateNSphereVolume(1, 0));
		//line segment
		assertEquals(2.0,ClustersByDistribution.calculateNSphereVolume(1, 1));
		//disk
		assertEquals(Math.PI,ClustersByDistribution.calculateNSphereVolume(1, 2));
		//sphere
		assertEquals(4*Math.PI/3,ClustersByDistribution.calculateNSphereVolume(1, 3));
		//4-sphere
		assertEquals(Math.pow(Math.PI,2)/2,ClustersByDistribution.calculateNSphereVolume(1, 4));
		//5-sphere
		assertEquals(8*Math.pow(Math.PI,2)/15,ClustersByDistribution.calculateNSphereVolume(1, 5));
	}
	
}
