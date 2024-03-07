package algorithms;

import java.io.File;

import org.junit.Test;

import weka.core.Instances;
import br.rede.autoclustering.algorithms.descry.AdaptableKDTree;
import br.rede.autoclustering.util.DataBaseStructure;

public class AdaptableKDTreeTest {

	@Test
	public void testRodar() {
		DataBaseStructure db = new DataBaseStructure();
		db.loadDataBaseCSV(new File("databases/circulos.csv"));
		AdaptableKDTree kdTree = new AdaptableKDTree();
		Instances instances = db.getNormalizedData();
//		Node root = kdTree.kdtree(instances, 0, 5);
//		while (root != null) {
//			new ClusterViewerFrame(root.getChildren().get(0).getInstances());
//			new ClusterViewerFrame(root.getChildren().get(1).getInstances());
//			root = root.getChildren().get(0);
//		}
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
