package br.rede.autoclustering.algorithms.dpcci;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import br.rede.autoclustering.core.ClusteringMethod;
import br.rede.autoclustering.core.Parameter;
import br.rede.autoclustering.structures.groups.Group;
import weka.core.Instance;
import weka.core.Instances;

public class ClusterByDensityPeak implements ClusteringMethod {

	@Override
	public void executeStep(Map<Parameter, Object> sharedObjects) {
		
		if ( isReady(sharedObjects) ) {
			Instances instances = (Instances) sharedObjects.get(Parameter.ALL_INSTANCES);
			double[] rhoNormalized = ((double[]) sharedObjects.get(Parameter.DPCI_NORM_RHO));
			double[] deltaNormalized = ((double[]) sharedObjects.get(Parameter.DPCI_NORM_DELTA));
			int[] ordRho = ((int[]) sharedObjects.get(Parameter.DPCI_ORD_RHO));
			int[] nearestNeighbor = ((int[]) sharedObjects.get(Parameter.DPCI_NEAREST_NEIGHBOR));
			
			//System.out.println("Typ Dis: "+sharedObjects.get(Parameter.DISTANCE_INFO));
			//System.out.println("Ord Rho: "+Arrays.toString(ordRho));
			//System.out.println("Nea Nei: "+Arrays.toString(nearestNeighbor));
			
			// Variables
			double[] gamma = null; // judgment index
			double[] ordGamma = null; // judgment index sorted
			double upperBound = 0; // upper bound according to Chebychev inequality
			//ArrayList<Integer> clusterID = null;
			//List<Group> groups = new ArrayList<Group>(); 
			
			Object[] judgeI = calcJudgeIndex(rhoNormalized,deltaNormalized);
			gamma = (double[]) judgeI[0];
			ordGamma = (double[]) judgeI[1];
			Object[] calcBoundCenters = calcUpperBound(gamma);
			upperBound = (double) calcBoundCenters[0];
			ArrayList<Integer> clusterID = (ArrayList<Integer>) calcBoundCenters[1];// center clusters
			List<Group> groups = formGroups(instances,upperBound, clusterID,ordRho,nearestNeighbor);// clusters identified
			
			//System.out.println("DensityPEak");
			//System.out.println("Número de clusters: "+groups.size());
			
			sharedObjects.put(Parameter.ALL_GROUPS, groups);
			sharedObjects.put(Parameter.DPCI_UPPERBOUND, upperBound);
		}
	}

	private List<Group> formGroups(Instances instances, double upperBound, ArrayList<Integer> clusterID, int[] ordRho, int[] nearestNeighbor) {
		
		List<Group> groups = new ArrayList<Group>();
		Group group;
		
		// Assign the cluster centers
		int[] clustersGroup = new int[instances.numInstances()];
		for (int i = 0; i < clustersGroup.length; i++) {
			clustersGroup[i] = -1;
		}
		
		int nOfClusters = clusterID.size();
		for (int i = 0; i < nOfClusters; i++) {
			//clustersGroup[clusterID.get(i)] = (i+1);
			clustersGroup[clusterID.get(i)] = i;
		}
		
		// Assign the nearest neighbor of higher density
		for (int i = 0; i < instances.numInstances(); i++) {
			if (clustersGroup[ordRho[i]] == -1) {
				clustersGroup[ordRho[i]] = clustersGroup[nearestNeighbor[ordRho[i]]];
			}
		}
		//System.out.println("CG: "+Arrays.toString(clustersGroup));
		
		for (int i = 0; i < nOfClusters; i++) {
			group = new Group();
			groups.add(group);
		}
		
		//checking the crash error (bad cluster not found)
		if (nOfClusters == 0) {
			group = new Group();
			groups.add(group);
			
			for (int i = 0; i < clustersGroup.length; i++) {
				if (clustersGroup[i] == -1) {
					clustersGroup[i] = 0;
				}
			}
		}
		
		//System.out.println("Size clusterGroup: "+clustersGroup.length);
		//System.out.println("nOfInstances: "+instances.numInstances());
		//System.out.println("Near Neighb: "+Arrays.toString(nearestNeighbor));
		for (int i = 0; i < clustersGroup.length; i++) {
			//System.out.println("´I´"+i);
			Instance instance = instances.instance(i);
			groups.get(clustersGroup[i]).addInstance(instance);
		}
		
		return groups;
	}

	private Object[] calcUpperBound(double[] gamma) {
		
		Object[] obj = new Object[2];
		double uGamma = 0;//mean
		double stdGamma = 0;//standard deviation
		double limUpper = 0;//upper bound
		
		// Mean
		double sum = 0;
		for (int i = 0; i < gamma.length; i++) {
			sum += gamma[i];
		}
		uGamma = sum/gamma.length;
		uGamma = Math.round(uGamma*1000000.0)/1000000.0;
		//System.out.println("Mean(gamma): "+uGamma);
		
		//variance
		double sqDiff = 0;
		for (int i = 0; i < gamma.length; i++) {
			sqDiff += (gamma[i] - uGamma)*(gamma[i] - uGamma);
		}
		sqDiff = sqDiff/gamma.length;
		sqDiff = Math.round(sqDiff*1000000.0)/1000000.0;
		//System.out.println("Variace(gamma): "+sqDiff);
		
		//std (standard deviation)
		stdGamma = Math.sqrt(sqDiff);
		stdGamma = Math.round(stdGamma*1000000.0)/1000000.0;
		//System.out.println("Std(gamma): "+stdGamma);
		
		limUpper = (uGamma + 3 * stdGamma);
		limUpper = Math.round(limUpper*1000000.0)/1000000.0;
		
		int nC = 0;//number of clusters
		//int[] clustersID = new int[nC];
		ArrayList<Integer> clusterID = new ArrayList<Integer>();
		for (int i = 0; i < gamma.length; i++) {
			if (gamma[i] > limUpper) {
				nC++;
				clusterID.add(i);
			}
		}
		//System.out.println("Number of clusters: "+nC);
		//System.out.println("Cluster centers (position): "+clusterID);
		
		double[] newGammaData = new double[gamma.length-nC];// gamma values without the clusters
		int count = 0;
		for (int i = 0; i < gamma.length; i++) {
			if (gamma[i] < limUpper) {
				newGammaData[count] = gamma[i];
				count++;
			}
		}
		
		obj[0] = limUpper;
		obj[1] = clusterID;
		
		return obj;
	}

	private Object[] calcJudgeIndex(double[] rhoNormalized, double[] deltaNormalized) {
		Object[] obj = new Object[2];
		
		double[] gamma = new double[rhoNormalized.length];
		for (int i = 0; i < gamma.length; i++) {
			if (rhoNormalized[i] < deltaNormalized[i]) {
				gamma[i] = rhoNormalized[i];
			}else
			{
				gamma[i] = deltaNormalized[i];
			}
		}
		
		double[] ordGamma = gamma.clone();
		Arrays.sort(ordGamma);
		obj[0] = gamma;
		obj[1] = ordGamma;
		
		return obj;
	}

	@Override
	public boolean isReady(Map<Parameter, Object> sharedObjects) {
		if ( sharedObjects.get(Parameter.ALL_INSTANCES) != null &&
			 sharedObjects.get(Parameter.DPCI_NORM_RHO) != null &&
			 sharedObjects.get(Parameter.DPCI_NORM_DELTA) != null &&
			 sharedObjects.get(Parameter.DPCI_ORD_RHO) != null &&
			 sharedObjects.get(Parameter.DPCI_NEAREST_NEIGHBOR) != null){
				return true;
			}else{
				return false;
			}
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ClusterByDensityPeak";
	}

	@Override
	public String technicalInformation() {
		// TODO Auto-generated method stub
		return "It will find groups according to the densities, distances and nearest neigbors of each point using the Chebychev inequality.";
	}

}
