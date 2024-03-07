package algorithms;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import weka.core.Instances;
import br.rede.autoclustering.algorithms.amr.AMRTree;
import br.rede.autoclustering.algorithms.amr.ClustersAMR;
import br.rede.autoclustering.algorithms.sudephic.EquallySizedGrid;
import br.rede.autoclustering.core.Parameter;
import br.rede.autoclustering.structures.tree.Tree;
import br.rede.autoclustering.util.ClusterViewerFrame;
import br.rede.autoclustering.util.DataBaseStructure;

public class TreeTest extends TestCase{
	
	@Test
	public void testTree(){
		DataBaseStructure db = new DataBaseStructure();
		db.loadDataBaseCSV(new File("/home/sfelixjr/circulos.csv"));
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
		
		EquallySizedGrid equallySizedGrid2 = new EquallySizedGrid();
		equallySizedGrid2.executeStep(sharedObjects);
		
		AMRTree tree2 = new AMRTree(/*new AMRProperties(10, 1,2)*/);
		tree2.executeStep(sharedObjects);
		
		ClustersAMR amr = new ClustersAMR(/*new AMRProperties(30, 1,2)*/);
		amr.executeStep(sharedObjects);
		
		Tree tree = (Tree) sharedObjects.get(Parameter.AMR_TREE);
		ClusterViewerFrame frame = new ClusterViewerFrame(instances, tree.getGroups());
		frame.setVisible(true);
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
