package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import br.rede.autoclustering.algorithms.InitialNode;
import br.rede.autoclustering.algorithms.amr.AMRTree;
import br.rede.autoclustering.algorithms.amr.ClustersAMR;
import br.rede.autoclustering.algorithms.clique.ClustersByPartition;
import br.rede.autoclustering.algorithms.clique.DenseAreas;
import br.rede.autoclustering.algorithms.dbclasd.CandidatesByNpts;
import br.rede.autoclustering.algorithms.dbclasd.ClustersByDistribution;
import br.rede.autoclustering.algorithms.dbscan.CandidatesByDistance;
import br.rede.autoclustering.algorithms.dbscan.ClustersByConnectiveness;
import br.rede.autoclustering.algorithms.denclue.ASH;
import br.rede.autoclustering.algorithms.denclue.ClustersByAttractors;
import br.rede.autoclustering.algorithms.descry.AdaptableKDTree;
import br.rede.autoclustering.algorithms.descry.MergeByDistance;
import br.rede.autoclustering.algorithms.dhc.AttractionTree;
import br.rede.autoclustering.algorithms.dhc.DensityTree;
import br.rede.autoclustering.algorithms.sudephic.EquallySizedGrid;
import br.rede.autoclustering.algorithms.sudephic.MergeByOverlap;
import br.rede.autoclustering.core.Parameter;
import br.rede.autoclustering.vo.AutoClusteringVO;
import br.rede.autoclustering.vo.EdgeVO;
import br.rede.autoclustering.vo.NodeVO;
import br.rede.autoclustering.vo.ParameterVO;

public class VOTest extends TestCase{
	@Test
	public void testVO() throws Exception {
		File configFile = new File("resources/config.xml");
		Serializer serializer = new Persister();
		
		//dbscan ParameterVO\s*(.*?)\s*[=]\s*new\s*ParameterVO[(](.*?),(.*?),(.*?)[)];\s*(\n) ParameterVO $1 = new ParameterVO();$5$1.setMin($2);$5$1.setMax($3);$5$1.setParameter($4);$5
		ParameterVO pointsDBScan = new ParameterVO();
		pointsDBScan.setMin(2);
		pointsDBScan.setMax( 5);
		pointsDBScan.setType( Parameter.DBSCAN_NUM_PTS);
		ParameterVO distanceDBScan = new ParameterVO();
		distanceDBScan.setMin(1);
		distanceDBScan.setMax( 8);
		distanceDBScan.setType( Parameter.DBSCAN_MAX_DIST);
		List<ParameterVO> propertiesDbscan = new ArrayList<ParameterVO>();
		propertiesDbscan.add(pointsDBScan);
		propertiesDbscan.add(distanceDBScan);
		// DBScan NodeVO\s*(.*?)\s*[=]\s*new\s*NodeVO[(](.*?),(.*?),(.*?),(.*?),(.*?)[)];\s*(\n) 
		// NodeVO $1 = new NodeVO();$7\t\t$1.setNumber($2);$7\t\t$1.setClazz($3);$7\t\t$1.setOptk($4);$7\t\t$1.setOptOver($5);$7\t\t$1.setProperties($6);$7
		NodeVO distanceNodeVO = new NodeVO();
		distanceNodeVO.setNumber(1);
		distanceNodeVO.setClazz( CandidatesByDistance.class.getName());
		distanceNodeVO.setOptk( true);
		distanceNodeVO.setOptOver( false);
		distanceNodeVO.setProperties( propertiesDbscan);
		NodeVO connectivenessNodeVO = new NodeVO();
		connectivenessNodeVO.setNumber(2);
		connectivenessNodeVO.setClazz( ClustersByConnectiveness.class.getName());
		connectivenessNodeVO.setOptk( false);
		connectivenessNodeVO.setOptOver( false);
		connectivenessNodeVO.setProperties( propertiesDbscan);
		List<ParameterVO> empty = new ArrayList<ParameterVO>();
		
		//InitialNode
		NodeVO initialNode = new NodeVO();
		initialNode.setNumber(0);
		initialNode.setClazz( InitialNode.class.getName());
		initialNode.setOptk( false);
		initialNode.setOptOver( false);
		initialNode.setProperties( empty);
		// DBClasd
		ParameterVO pts = new ParameterVO();
		pts.setMin(1);
		pts.setMax( 10);
		pts.setType( Parameter.DBCLASD_PTS);
		List<ParameterVO> propertiesDBClasd = new ArrayList<ParameterVO>();
		propertiesDBClasd.add(pts);
		NodeVO nptsNodeVO = new NodeVO();
		nptsNodeVO.setNumber(3);
		nptsNodeVO.setClazz( CandidatesByNpts.class.getName());
		nptsNodeVO.setOptk( false);
		nptsNodeVO.setOptOver( true);
		nptsNodeVO.setProperties( propertiesDBClasd);
		NodeVO distributionNodeVO = new NodeVO();
		distributionNodeVO.setNumber(4);
		distributionNodeVO.setClazz( ClustersByDistribution.class.getName());
		distributionNodeVO.setOptk( false);
		distributionNodeVO.setOptOver( false);
		distributionNodeVO.setProperties( propertiesDBClasd);
		// Denclue
		ParameterVO sigmaDenclue = new ParameterVO();
		sigmaDenclue.setMin(1);
		sigmaDenclue.setMax( 10);
		sigmaDenclue.setType( Parameter.DENCLUE_SIGMA);
		ParameterVO epsilonDenclue = new ParameterVO();
		epsilonDenclue.setMin(0);
		epsilonDenclue.setMax( 5);
		epsilonDenclue.setType( Parameter.DENCLUE_EPSILON);
		List<ParameterVO> propertiesDenclue = new ArrayList<ParameterVO>();
		propertiesDenclue.add(sigmaDenclue);
		propertiesDenclue.add(epsilonDenclue);
		NodeVO ashNodeVO = new NodeVO();
		ashNodeVO.setNumber(5);
		ashNodeVO.setClazz( ASH.class.getName());
		ashNodeVO.setOptk( false);
		ashNodeVO.setOptOver( false);
		ashNodeVO.setProperties( propertiesDenclue);
		NodeVO clustersByAttractorNodeVO = new NodeVO();
		clustersByAttractorNodeVO.setNumber(6);
		clustersByAttractorNodeVO.setClazz( ClustersByAttractors.class.getName());
		clustersByAttractorNodeVO.setOptk( true);
		clustersByAttractorNodeVO.setOptOver( true);
		clustersByAttractorNodeVO.setProperties( propertiesDenclue);
		// Clique
		ParameterVO slicesClique = new ParameterVO();
	slicesClique.setMin(10);
	slicesClique.setMax( 30);
	slicesClique.setType( Parameter.CLIQUE_SLICES);
		ParameterVO thresholdClique = new ParameterVO();
	thresholdClique.setMin(0.0000001f);
	thresholdClique.setMax( 0.2f);
	thresholdClique.setType( Parameter.CLIQUE_THRESHOLD);
		List<ParameterVO> propertiesClique = new ArrayList<ParameterVO>();
		propertiesClique.add(slicesClique);
		propertiesClique.add(thresholdClique);
		NodeVO denseAreasNodeVO = new NodeVO();
		denseAreasNodeVO.setNumber(7);
		denseAreasNodeVO.setClazz( DenseAreas.class.getName());
		denseAreasNodeVO.setOptk( false);
		denseAreasNodeVO.setOptOver( false);
		denseAreasNodeVO.setProperties( propertiesClique);
		NodeVO partitionNodeVO = new NodeVO();
		partitionNodeVO.setNumber(8);
		partitionNodeVO.setClazz( ClustersByPartition.class.getName());
		partitionNodeVO.setOptk( true);
		partitionNodeVO.setOptOver( false);
		partitionNodeVO.setProperties( propertiesClique);
		// AMR
		ParameterVO slicesAMR = new ParameterVO();
	slicesAMR.setMin(25);
	slicesAMR.setMax( 30);
	slicesAMR.setType( Parameter.AMR_SLICES);
		ParameterVO densityAMR = new ParameterVO();
	densityAMR.setMin(1);
	densityAMR.setMax( 2);
	densityAMR.setType( Parameter.AMR_DENSITY);
		ParameterVO lambdaAMR = new ParameterVO();
	lambdaAMR.setMin(1);
	lambdaAMR.setMax( 3);
	lambdaAMR.setType( Parameter.AMR_LAMBDA);
		List<ParameterVO> propertiesAMR = new ArrayList<ParameterVO>();
		propertiesAMR.add(slicesAMR);
		propertiesAMR.add(densityAMR);
		propertiesAMR.add(lambdaAMR);

		NodeVO amrTreeNodeVO = new NodeVO();
		amrTreeNodeVO.setNumber(9);
		amrTreeNodeVO.setClazz( AMRTree.class.getName());
		amrTreeNodeVO.setOptk( true);
		amrTreeNodeVO.setOptOver( false);
		amrTreeNodeVO.setProperties( propertiesAMR);
		NodeVO clustersByAMRNodeVO = new NodeVO();
		clustersByAMRNodeVO.setNumber(10);
		clustersByAMRNodeVO.setClazz( ClustersAMR.class.getName());
		clustersByAMRNodeVO.setOptk( false);
		clustersByAMRNodeVO.setOptOver( false);
		clustersByAMRNodeVO.setProperties( propertiesAMR);
		ParameterVO densityDescry = new ParameterVO();
	densityDescry.setMin(1);
	densityDescry.setMax( 10);
	densityDescry.setType( Parameter.DESCRY_DENSITY);
		ParameterVO numptsDescry = new ParameterVO();
	numptsDescry.setMin(2);
	numptsDescry.setMax( 10);
	numptsDescry.setType( Parameter.DESCRY_K);
		List<ParameterVO> propertiesDescry = new ArrayList<ParameterVO>();
		propertiesDescry.add(densityDescry);
		propertiesDescry.add(numptsDescry);
		
		NodeVO adaptableVO = new NodeVO();
		adaptableVO.setNumber(11);
		adaptableVO.setClazz( AdaptableKDTree.class.getName());
		adaptableVO.setOptk( true);
		adaptableVO.setOptOver( true);
		adaptableVO.setProperties( propertiesDescry);
		NodeVO mergeBydistance = new NodeVO();
		mergeBydistance.setNumber(12);
		mergeBydistance.setClazz( MergeByDistance.class.getName());
		mergeBydistance.setOptk( false);
		mergeBydistance.setOptOver( true);
		mergeBydistance.setProperties( propertiesDescry);
		// SUDEPHIC
		NodeVO esg = new NodeVO();
		esg.setNumber(13);
		esg.setClazz( EquallySizedGrid.class.getName());
		esg.setOptk( false);
		esg.setOptOver( false);
		esg.setProperties( empty);
		NodeVO mergeOverlap = new NodeVO();
		mergeOverlap.setNumber(14);
		mergeOverlap.setClazz( MergeByOverlap.class.getName());
		mergeOverlap.setOptk( true);
		mergeOverlap.setOptOver( true);
		mergeOverlap.setProperties( empty);
		// DHC
		ParameterVO minPtsDHC = new ParameterVO();
	minPtsDHC.setMin(5);
	minPtsDHC.setMax( 10);
	minPtsDHC.setType( Parameter.DHC_MINPTS);
		ParameterVO thresholdDHC = new ParameterVO();
	thresholdDHC.setMin(10);
	thresholdDHC.setMax( 17);
	thresholdDHC.setType( Parameter.DHC_THRESHOLD);
		ParameterVO sigmaDHC = new ParameterVO();
		sigmaDHC.setMin(1);
		sigmaDHC.setMax( 5);
		sigmaDHC.setType( Parameter.DHC_SIGMA);
		List<ParameterVO> propertiesDHC = new ArrayList<ParameterVO>();
		propertiesDHC.add(minPtsDHC);
		propertiesDHC.add(thresholdDHC);
		propertiesDHC.add(sigmaDHC);
		NodeVO attTree = new NodeVO();
		attTree.setNumber(15);
		attTree.setClazz( AttractionTree.class.getName());
		attTree.setOptk( false);
		attTree.setOptOver( false);
		attTree.setProperties( propertiesDHC);
		NodeVO densityTree = new NodeVO();
		densityTree.setNumber(16);
		densityTree.setClazz( DensityTree.class.getName());
		densityTree.setOptk( true);
		densityTree.setOptOver( false);
		densityTree.setProperties( propertiesDHC);
		List<NodeVO> nodeVOs = new ArrayList<NodeVO>();
		
		nodeVOs.add(initialNode);		// DBScan
		nodeVOs.add(distanceNodeVO);		
		nodeVOs.add(connectivenessNodeVO);		// DBClasd
		nodeVOs.add(nptsNodeVO);		
		nodeVOs.add(distributionNodeVO);		// Denclue
		nodeVOs.add(ashNodeVO);		
		nodeVOs.add(clustersByAttractorNodeVO);		// Clique
		nodeVOs.add(denseAreasNodeVO);		
		nodeVOs.add(partitionNodeVO);		// AMR
		nodeVOs.add(amrTreeNodeVO);		
		nodeVOs.add(clustersByAMRNodeVO);		// Descry 
		nodeVOs.add(adaptableVO);		
		nodeVOs.add(mergeBydistance);		// SUDEPHIC
		nodeVOs.add(esg);		
		nodeVOs.add(mergeOverlap);		// DHC
		nodeVOs.add(attTree);		
		nodeVOs.add(densityTree);//		
		
		//Edges
		//From InitialNode
		EdgeVO edgeV00 = new EdgeVO();
		edgeV00.setIn(0);
		edgeV00.setOut(15);
		EdgeVO edgeVO1 = new EdgeVO();
		edgeVO1.setIn(0);
		edgeVO1.setOut(1);
		EdgeVO edgeVO2 = new EdgeVO();
		edgeVO2.setIn(0);
		edgeVO2.setOut(3);
		EdgeVO edgeVO3 = new EdgeVO();
		edgeVO3.setIn(0);
		edgeVO3.setOut(5);
		EdgeVO edgeVO4 = new EdgeVO();
		edgeVO4.setIn(0);
		edgeVO4.setOut(7);
		EdgeVO edgeVO5 = new EdgeVO();
		edgeVO5.setIn(0);
		edgeVO5.setOut(13);
		EdgeVO edgeVO6 = new EdgeVO();
		edgeVO6.setIn(0);
		edgeVO6.setOut(11);

		//From CandidatesByDistance
		EdgeVO edgeVO8 = new EdgeVO();
		edgeVO8.setIn(1);
		edgeVO8.setOut(2);
		EdgeVO edgeVO9 = new EdgeVO();
		edgeVO9.setIn(1);
		edgeVO9.setOut(12);
		EdgeVO edgeV10 = new EdgeVO();
		edgeV10.setIn(1);
		edgeV10.setOut(4);
		EdgeVO edgeV11 = new EdgeVO();
		edgeV11.setIn(1);
		edgeV11.setOut(14);
		//From ASH
		EdgeVO edgeV12 = new EdgeVO();
		edgeV12.setIn(5);
		edgeV12.setOut(2);
		EdgeVO edgeV13 = new EdgeVO();
		edgeV13.setIn(5);
		edgeV13.setOut(4);
		EdgeVO edgeV14 = new EdgeVO();
		edgeV14.setIn(5);
		edgeV14.setOut(6);
		EdgeVO edgeV15 = new EdgeVO();
		edgeV15.setIn(5);
		edgeV15.setOut(15);
		//From CandidatesByNPts
		EdgeVO edgeV16 = new EdgeVO();
		edgeV16.setIn(3);
		edgeV16.setOut(2);
		EdgeVO edgeV17 = new EdgeVO();
		edgeV17.setIn(3);
		edgeV17.setOut(12);
		EdgeVO edgeV18 = new EdgeVO();
		edgeV18.setIn(3);
		edgeV18.setOut(4);
		EdgeVO edgeV19 = new EdgeVO();
		edgeV19.setIn(3);
		edgeV19.setOut(14);
		//From DenseAreas
		EdgeVO edgeV20 = new EdgeVO();
		edgeV20.setIn(7);
		edgeV20.setOut(6);
		EdgeVO edgeV21 = new EdgeVO();
		edgeV21.setIn(7);
		edgeV21.setOut(8);
		EdgeVO edgeV22 = new EdgeVO();
		edgeV22.setIn(7);
		edgeV22.setOut(9);
		//From EquallySizedGrid
		EdgeVO edgeV23 = new EdgeVO();
		edgeV23.setIn(13);
		edgeV23.setOut(9);
		EdgeVO edgeV24 = new EdgeVO();
		edgeV24.setIn(13);
		edgeV24.setOut(12);
		EdgeVO edgeV25 = new EdgeVO();
		edgeV25.setIn(13);
		edgeV25.setOut(14);
		//From AMRTree
		EdgeVO edgeV26 = new EdgeVO();
		edgeV26.setIn(9);
		edgeV26.setOut(12);
		EdgeVO edgeV27 = new EdgeVO();
		edgeV27.setIn(9);
		edgeV27.setOut(10);
		//From AdaptableKDTree
		EdgeVO edgeV28 = new EdgeVO();
		edgeV28.setIn(11);
		edgeV28.setOut(10);
		EdgeVO edgeV29 = new EdgeVO();
		edgeV29.setIn(11);
		edgeV29.setOut(12);
		//From DHC
		EdgeVO edgeV30 = new EdgeVO();
		edgeV30.setIn(15);
		edgeV30.setOut(16);
		//
		List<EdgeVO> edgesVos = new ArrayList<EdgeVO>();
		edgesVos.add(edgeV00);
		edgesVos.add(edgeVO1);
		edgesVos.add(edgeVO2);
		edgesVos.add(edgeVO3);
		edgesVos.add(edgeVO4);
		edgesVos.add(edgeVO5);
		edgesVos.add(edgeVO6);

		//From CandidatesByDistance
		edgesVos.add(edgeVO8);
		edgesVos.add(edgeVO9);
		edgesVos.add(edgeV10);
		edgesVos.add(edgeV11);

		//From ASH
		edgesVos.add(edgeV12);
		edgesVos.add(edgeV13);
		edgesVos.add(edgeV14);
		edgesVos.add(edgeV15);
		
		//From CandidatesByNPts
		edgesVos.add(edgeV16);
		edgesVos.add(edgeV17);
		edgesVos.add(edgeV18);
		edgesVos.add(edgeV19);
		
		//From DenseAreas
		edgesVos.add(edgeV20);
		edgesVos.add(edgeV21);
		edgesVos.add(edgeV22);
		
		//From EquallySizedGrid
		edgesVos.add(edgeV23);
		edgesVos.add(edgeV24);
		edgesVos.add(edgeV25);
		
		//From AMRTree
		edgesVos.add(edgeV26);
		edgesVos.add(edgeV27);
		
		//From AdaptableKDTree
		edgesVos.add(edgeV28);
		edgesVos.add(edgeV29);
		
		//From DHC
		edgesVos.add(edgeV30);
		
		AutoClusteringVO clusteringVO = new AutoClusteringVO();
		clusteringVO.setNodes(nodeVOs);
		clusteringVO.setEdges(edgesVos);
		
		clusteringVO.setSlices(10);
		clusteringVO.setVersion("0.9");
		serializer.write(clusteringVO, configFile);
	}
}
