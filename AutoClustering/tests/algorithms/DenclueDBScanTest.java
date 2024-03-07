package algorithms;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import br.rede.autoclustering.algorithms.dbscan.CandidatesByDistance;
import br.rede.autoclustering.algorithms.dbscan.ClustersByConnectiveness;
import br.rede.autoclustering.core.ClusteringMethod;
import br.rede.autoclustering.core.Parameter;
import br.rede.autoclustering.structures.groups.Group;
import br.rede.autoclustering.util.ClusterViewerFrame;
import br.rede.autoclustering.util.DataBaseStructure;

public class DenclueDBScanTest extends TestCase{
	
	public void testCliqueDenclue(){
		long first = System.currentTimeMillis();
		DataBaseStructure db = new DataBaseStructure();
		db.loadDataBaseCSV(new File("bases/dbclasd.in"));
		List<ClusteringMethod> methods = new ArrayList<ClusteringMethod>();
		
		/*********************************
		 * DBScan - CandidatesByDistance *
		 *********************************/
//		DBScanProperties dbscanProperties = new DBScanProperties(3,3f);
//		try {
//			MethodPropertyValidator.validate(dbscanProperties, 10);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		CandidatesByDistance cbd = new CandidatesByDistance(/*dbscanProperties*/);
		
		/***********************************
		 * DBScan - ClustersByConnectivess *
		 ***********************************/
		ClustersByConnectiveness cbc = new ClustersByConnectiveness(/*dbscanProperties*/);
		Map<Parameter, Object> sharedObjects = new HashMap<Parameter, Object>();

		sharedObjects.put(Parameter.ALL_INSTANCES, db.getDatabase());
		sharedObjects.put(Parameter.DBSCAN_MAX_DIST, 2d);
		sharedObjects.put(Parameter.DBSCAN_NUM_PTS, 4d);
		
		for ( ClusteringMethod clusteringMethod : methods )
			clusteringMethod.executeStep(sharedObjects);
			
		List<Group> clusters = (List<Group>) sharedObjects.get(Parameter.ALL_GROUPS);
		new  ClusterViewerFrame(db.getDatabase(),clusters);
		System.out.println("Tempo total: "+(System.currentTimeMillis() - first)/1000);

	}
	
}
