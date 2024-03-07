package br.rede.autoclustering;

import java.util.Arrays;

import weka.clusterers.DBScan;
import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Standardize;

public class temp {

	public static void main(String[] args) {

		try {
			rodar();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static void  rodar() throws Exception {
		// Load iris data and remove class attribute
		Instances data = (new weka.core.converters.ConverterUtils.DataSource("databases/winequality-red_CPA.arff")).getDataSet(); //principal components analysis 0.23
//		Instances data = (new weka.core.converters.ConverterUtils.DataSource("databases/winequality-red_SelAtt.arff")).getDataSet(); //CfsSubsetEval 0.28
//		Instances data = (new weka.core.converters.ConverterUtils.DataSource("databases/winequality-red.arff")).getDataSet(); //Original Base 0.19
		data.deleteAttributeAt(data.numAttributes() - 1);

		// Standardise the data to compare with R's output at http://www.sthda.com/english/wiki/print.php?id=239 (Section 5.2.3)
		Standardize standardize = new weka.filters.unsupervised.attribute.Standardize();
		standardize.setInputFormat(data);
		data = weka.filters.Filter.useFilter(data, standardize);

		// Specify the distance function to use
		EuclideanDistance distance = new weka.core.EuclideanDistance();
		distance.setDontNormalize(true); // Turn normalisation off because we have standardised the data

		// Specify the clustering algorithm Case 1 [Silhouette usa distancia]
//		SimpleKMeans clusterer = new weka.clusterers.SimpleKMeans();
//		clusterer.setNumClusters(9); // Setting the number of clusters to 3 here as an example
//		clusterer.setDistanceFunction(distance); // In k-means, we can use the same distance function
//		clusterer.buildClusterer(data);

		// Specify the clustering algorithm Case 2
		DBScan clusterer = new DBScan();
//		clusterer.setMinPoints(2);
//		clusterer.setEpsilon(0.3);
		clusterer.setMinPoints(13);
		clusterer.setEpsilon(1);
//		clusterer.setDistanceFunction(distance);
		clusterer.buildClusterer(data);		
//		//******		
		
		// Find cluster index of each instance
		int[] clusterIndexOfInstance = new int[data.numInstances()];
		for (int i = 0; i < data.numInstances(); i++) {
		  clusterIndexOfInstance[i] = clusterer.clusterInstance(data.instance(i));
		}

		double sumSilhouetteCoefficients = 0;
		for (int i = 0; i < data.numInstances(); i++) {
		 
		  // Compute average distance of current instance to each cluster, including its own cluster
		  double[] averageDistancePerCluster = new double[clusterer.numberOfClusters()];
		  int[] numberOfInstancesPerCluster = new int[clusterer.numberOfClusters()];
		  for (int j = 0; j < data.numInstances(); j++) {
			  System.out.println("-------------");
			  System.out.println(distance);
//			  System.out.println(data);
			  System.out.println(data.instance(i));
			  System.out.println(data.instance(j));
			  System.out.println("=>"+clusterIndexOfInstance[j]);
			  System.out.println("->"+distance);
			  System.out.println(">"+distance.distance(data.instance(i), data.instance(j)));
			  System.out.println("=================");
		    averageDistancePerCluster[clusterIndexOfInstance[j]] += distance.distance(data.instance(i), data.instance(j));
		    numberOfInstancesPerCluster[clusterIndexOfInstance[j]]++; // Should the current instance be skipped though?
		  }
		  
		  for (int k = 0; k < averageDistancePerCluster.length; k++) {
		    averageDistancePerCluster[k] /= numberOfInstancesPerCluster[k];
		  }
		 
		  // Average distance to instance's own cluster
		  double a =  averageDistancePerCluster[clusterIndexOfInstance[i]];
		 
		  // Find the distance of the "closest" other cluster
		  averageDistancePerCluster[clusterIndexOfInstance[i]] = Double.MAX_VALUE;
		  double b = Arrays.stream(averageDistancePerCluster).min().getAsDouble();

		  // Compute silhouette coefficient for current instance
		  System.out.println(clusterer.numberOfClusters() > 1 ? (b - a) / Math.max(a, b) : 0);
		  sumSilhouetteCoefficients += clusterer.numberOfClusters() > 1 ? (b - a) / Math.max(a, b) : 0;
		}

		System.out.println("sumSilhouetteCoefficients: " + (sumSilhouetteCoefficients));
		System.out.println("numInstances: " + (data.numInstances()));
		System.out.println("Average silhouette coefficient: " + (sumSilhouetteCoefficients / data.numInstances()));		
	}
	
}
