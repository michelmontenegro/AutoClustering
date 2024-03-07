package algorithms;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import br.rede.autoclustering.algorithms.dbscan.CandidatesByDistance;
import br.rede.autoclustering.algorithms.dbscan.ClustersByConnectiveness;
import br.rede.autoclustering.core.ClusteringMethod;
import br.rede.autoclustering.core.Parameter;
import br.rede.autoclustering.structures.groups.Group;
import br.rede.autoclustering.util.ClusterViewerFrame;
import br.rede.autoclustering.util.DataBaseStructure;
import br.rede.autoclustering.util.DistanceType;

public class DBScanTest extends TestCase {

	@Test
	public void testDBSCan() {
			DataBaseStructure db = new DataBaseStructure();
			db.loadDataBaseCSV(new File("/home/sfelixjr/circulos.csv"));
			long first = System.currentTimeMillis();
			List<ClusteringMethod> methods = new ArrayList<ClusteringMethod>();
			Map<Parameter, Object> sharedObjects = new HashMap<Parameter, Object>();

			sharedObjects.put(Parameter.ALL_INSTANCES, db.getDatabase());
			sharedObjects.put(Parameter.ALL_DISTANCE, DistanceType.EUCLIDEAN);
			sharedObjects.put(Parameter.DBSCAN_MAX_DIST, 2d);
			sharedObjects.put(Parameter.DBSCAN_NUM_PTS, 4d);
			/*********************************
			 * DBScan - CandidatesByDistance *
			 *********************************/
//			DBScanProperties dbscanProperties = new DBScanProperties(3,3f);
//			try {
//				MethodPropertyValidator.validate(dbscanProperties, 10);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			CandidatesByDistance cbd = new CandidatesByDistance(/*dbscanProperties*/);
			
			/***********************************
			 * DBScan - ClustersByConnectivess *
			 ***********************************/
			ClustersByConnectiveness cbc = new ClustersByConnectiveness(/*dbscanProperties*/);

			
			for ( ClusteringMethod clusteringMethod : methods )
				clusteringMethod.executeStep(sharedObjects);
				
			List<Group> clusters = (List<Group>) sharedObjects.get(Parameter.ALL_GROUPS);
			new  ClusterViewerFrame( db.getDatabase(),clusters);
			System.out.println("Tempo total: "+(System.currentTimeMillis() - first)/1000);
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
	}
}
