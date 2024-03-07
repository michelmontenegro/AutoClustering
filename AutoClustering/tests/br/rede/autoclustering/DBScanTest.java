package br.rede.autoclustering;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.clusterers.DBScan;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import br.rede.autoclustering.util.DataBaseStructure;

public class DBScanTest {
	
	public static void main(String[] args) throws Exception {
		DataBaseStructure db = new DataBaseStructure();
//		db.loadDataBaseCSV(new File("uci-databases/pima-indians-diabetes/pima-indians-diabetes.data"));
		db.loadDataBaseCSV(new File("uci-databases/heart-disease/processed.cleveland.data"));
	    Instances instances = db.getNormalizedData();
		DBScan clust = new DBScan();
//		clust.setMinPoints(2);
//		clust.setEpsilon(0.3);
		clust.setMinPoints(13);
		clust.setEpsilon(1);
		clust.buildClusterer(instances);
		

		FastVector fastVector = new FastVector(clust.numberOfClusters());
		//
		System.out.println("Number of Clusters: "+clust.numberOfClusters());
		System.out.println("Number of Attributes: "+instances.numAttributes());
		System.out.println("Number of Instances in the Dataset: "+instances.numInstances());
		
		for (int i = 0; i < clust.numberOfClusters(); i++) 
			fastVector.addElement(String.valueOf(((char)(i+65))));
		Attribute cluster = new Attribute("cluster", fastVector);
		instances.insertAttributeAt(cluster, instances.numAttributes());
		
		Map<Integer, List<Instance>> map = new HashMap<Integer,List<Instance>>();
		for (int i = 0; i < clust.numberOfClusters(); i++) {
			map.put(i, new ArrayList<Instance>());
		}
		int numOfNoiseInstances = 0;
		for (int i=0; i < instances.numInstances(); i++){
			Instance l = instances.instance(i);
			try {
				int cl = clust.clusterInstance(l);
				map.get(cl).add(l);
				l.setValue(instances.numAttributes()-1,String.valueOf(((char)(cl+65))));
			}catch (Exception e) {
				numOfNoiseInstances++;
//				System.out.println("NOISE: "+l);
			}
		}
		System.out.println("Number of noise Instances: "+numOfNoiseInstances);
		
		instances.setClass(instances.attribute(instances.numAttributes()-1));
		Evaluation eval = new Evaluation(instances);
		J48 j48  = new J48();
		j48.buildClassifier(instances);
		eval.crossValidateModel(j48, instances, 10, instances.getRandomNumberGenerator(1) );
		double fitness = eval.rootMeanSquaredError();
		instances.setClassIndex(-1);
		instances.deleteAttributeAt(instances.numAttributes()-1);
//		for ( Integer a : map.keySet()) {
//			System.out.println("########################");
//			System.out.println("## Cluster "+a+" size: "+ map.get(a).size()+" ##");
//			System.out.println("########################");
//			for(Instance i : map.get(a))
//				System.out.println(i);
//			System.out.println();
//		}
		System.out.println("sfsfsfsfsfssfsafd: "+instances.firstInstance());
		
		System.out.println("rMSE: "+fitness);
		System.out.println("Fitness: "+100 * (1 - fitness));
		System.out.println(clust.toString());
		
	}

}
