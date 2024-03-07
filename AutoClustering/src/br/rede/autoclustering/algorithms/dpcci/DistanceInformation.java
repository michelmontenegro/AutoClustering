package br.rede.autoclustering.algorithms.dpcci;

import java.util.Arrays;
import java.util.Map;

import br.rede.autoclustering.core.ClusteringMethod;
import br.rede.autoclustering.core.Parameter;
import br.rede.autoclustering.util.DistanceMeasures;
import br.rede.autoclustering.util.DistanceType;
import de.lmu.ifi.dbs.elki.distance.distancefunction.CosineDistanceFunction;
import weka.core.ChebyshevDistance;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;

public class DistanceInformation implements ClusteringMethod {


	@Override
	public void executeStep(Map<Parameter, Object> sharedObjects) {

		if (isReady(sharedObjects)) {
			Instances instances = (Instances) sharedObjects.get(Parameter.ALL_INSTANCES);
			double pos = ((Number) sharedObjects.get(Parameter.DPCI_DC_PERCENT)).doubleValue();
			int distanceType = ((Number) sharedObjects.get(Parameter.DISTANCE_INFO)).intValue();
			
			int nOfInstaces = instances.numInstances();
			double[][] DistanceMatrix = null;
			double dc = 0; // cutt of distance
			double[] rhoDensity = null; //local density for each point i
			int[] ordRho = null; //put rho in descending order and keep track the index
			double[] deltaDistance = null; //minimum distance between the point i and any point with higher density
			int[] nnHD = null; //nearest neighbors of higher density
			double[] rhoNormalized = null;
			double[] deltaNormalized = null;
			double maxDist = 0;
			
			if (distanceType == 1) {
				//EuclideanDistance euclideanDist = new EuclideanDistance(); //1
				//System.out.println("Euclidean");
				
				DistanceType distance = DistanceType.EUCLIDEAN;
				DistanceMatrix = calcFitDist(instances, distance);
				dc = calDC(instances,pos,distance);
				/*rhoDensity = calcRho(DistanceMatrix,dc,nOfInstaces);
				ordRho = sortRho(rhoDensity);
				maxDist = findMaxDist(DistanceMatrix);
				Object[] DeltaxNNHD = calcDeltaNNHD(ordRho,DistanceMatrix,nOfInstaces,maxDist);
				deltaDistance = (double[]) DeltaxNNHD[0];
				nnHD = (int[]) DeltaxNNHD[1];
				Object[] normalized = normilizeData(rhoDensity,deltaDistance);
				rhoNormalized = (double[]) normalized[0];
				deltaNormalized = (double[]) normalized[1];*/
				
			} else if (distanceType == 2)
			{
				//ManhattanDistance manhattanDist = new ManhattanDistance(); //2
				//System.out.println("Manhattan");
				
				DistanceType distance = DistanceType.MANHATTAN;
				DistanceMatrix = calcFitDist(instances, distance);
				dc = calDC(instances,pos,distance);
				/*rhoDensity = calcRho(DistanceMatrix,dc,nOfInstaces);
				ordRho = sortRho(rhoDensity);
				maxDist = findMaxDist(DistanceMatrix);
				Object[] DeltaxNNHD = calcDeltaNNHD(ordRho,DistanceMatrix,nOfInstaces,maxDist);
				deltaDistance = (double[]) DeltaxNNHD[0];
				nnHD = (int[]) DeltaxNNHD[1];
				Object[] normalized = normilizeData(rhoDensity,deltaDistance);
				rhoNormalized = (double[]) normalized[0];
				deltaNormalized = (double[]) normalized[1];*/
				
			} else //chebychev faz o código bugar
			{
				//ChebyshevDistance chebyshevDist = new ChebyshevDistance(); //3
				//System.out.println("BRAYCURTIS");
				
				//DistanceType distance = DistanceType.CHEBYCHEV;
				DistanceType distance = DistanceType.BRAYCURTIS;
				DistanceMatrix = calcFitDist(instances, distance);
				dc = calDC(instances,pos,distance);
				/*rhoDensity = calcRho(DistanceMatrix,dc,nOfInstaces);
				ordRho = sortRho(rhoDensity);
				maxDist = findMaxDist(DistanceMatrix);
				Object[] DeltaxNNHD = calcDeltaNNHD(ordRho,DistanceMatrix,nOfInstaces,maxDist);
				deltaDistance = (double[]) DeltaxNNHD[0];
				nnHD = (int[]) DeltaxNNHD[1];
				Object[] normalized = normilizeData(rhoDensity,deltaDistance);
				rhoNormalized = (double[]) normalized[0];
				deltaNormalized = (double[]) normalized[1];*/
				
			}
			
			if (sharedObjects.get(Parameter.SNN_DENSITY) == null) {//execute the pure dpcci
				rhoDensity = calcRho(DistanceMatrix,dc,nOfInstaces);
				ordRho = sortRho(rhoDensity);
				maxDist = findMaxDist(DistanceMatrix);
				Object[] DeltaxNNHD = calcDeltaNNHD(ordRho,DistanceMatrix,nOfInstaces,maxDist);
				deltaDistance = (double[]) DeltaxNNHD[0];
				nnHD = (int[]) DeltaxNNHD[1];
				Object[] normalized = normilizeData(rhoDensity,deltaDistance);
				rhoNormalized = (double[]) normalized[0];
				deltaNormalized = (double[]) normalized[1];
			} else {//execute the dpcci using the snnDensity
				//rhoDensity = calcRho(DistanceMatrix,dc,nOfInstaces);
				rhoDensity = (double[]) sharedObjects.get(Parameter.SNN_DENSITY);
				ordRho = sortRho(rhoDensity);
				maxDist = findMaxDist(DistanceMatrix);
				Object[] DeltaxNNHD = calcDeltaNNHD(ordRho,DistanceMatrix,nOfInstaces,maxDist);
				deltaDistance = (double[]) DeltaxNNHD[0];
				nnHD = (int[]) DeltaxNNHD[1];
				Object[] normalized = normilizeData(rhoDensity,deltaDistance);
				rhoNormalized = (double[]) normalized[0];
				deltaNormalized = (double[]) normalized[1];
			}
			
			
			sharedObjects.put(Parameter.DISTANCE_MATRIX, DistanceMatrix);
			sharedObjects.put(Parameter.DPCI_NORM_RHO, rhoNormalized);
			sharedObjects.put(Parameter.DPCI_NORM_DELTA, deltaNormalized);
			sharedObjects.put(Parameter.DPCI_ORD_RHO, ordRho);
			sharedObjects.put(Parameter.DPCI_NEAREST_NEIGHBOR, nnHD);
		}
		
	}

	private Object[] normilizeData(double[] rhoDensity, double[] deltaDistance) {
		//rho
		double[] rhoNormalized = new double[rhoDensity.length];
		double min = rhoDensity[0];
		double maxV = rhoDensity[0];
		for (int i = 0; i < rhoDensity.length; i++) {
			if (rhoDensity[i] <= min) {
				min = rhoDensity[i];
			}
			if (rhoDensity[i] >= maxV) {
				maxV = rhoDensity[i];
			}
		}
		
		for (int i = 0; i < rhoDensity.length; i++) {
			rhoNormalized[i] = (rhoDensity[i] - min)/(maxV - min);
			rhoNormalized[i] = Math.round(rhoNormalized[i]*1000000.0)/1000000.0;
		}
		
		//delta
		double[] deltaNormalized = new double[deltaDistance.length];
		min = deltaDistance[0];
		maxV = deltaDistance[0];
		for (int i = 0; i < deltaDistance.length; i++) {
			if (deltaDistance[i] <= min) {
				min = deltaDistance[i];
			}
			if (deltaDistance[i] >= maxV) {
				maxV = deltaDistance[i];
			}
		}
		
		for (int i = 0; i < deltaDistance.length; i++) {
			deltaNormalized[i] = (deltaDistance[i] - min)/(maxV - min);
			deltaNormalized[i] = Math.round(deltaNormalized[i]*1000000.0)/1000000.0;
		}
		Object[] obj = new Object[2];
		obj[0] = rhoNormalized;
		obj[1] = deltaNormalized;
		return obj;
	}

	private Object[] calcDeltaNNHD(int[] ordRho, double[][] distanceMatrix, int nOfInstaces, double maxDist) {
		double[] delta = new double[nOfInstaces];
		int[] nearestNeighbor = new int[nOfInstaces];
		delta[ordRho[0]] = -1;
		nearestNeighbor[ordRho[0]] = 0;
		
		for (int i = 1; i < nOfInstaces; i++) {
			delta[ordRho[i]] = maxDist;
			for (int j = 0; j < i-1; j++) {
				if (distanceMatrix[ordRho[i]][ordRho[j]] < delta[ordRho[i]]) {
					delta[ordRho[i]] = distanceMatrix[ordRho[i]][ordRho[j]];
					nearestNeighbor[ordRho[i]] = ordRho[j];
				}
			}//j
		}//i
		double maxV = delta[0];
		for (int i = 0; i < delta.length; i++) {
			if (delta[i] >= maxV) {
				maxV = delta[i];
			}
		}
		delta[ordRho[0]] = maxV;
		
		Object[] obj = new Object[2];
		obj[0] = delta;
		obj[1] = nearestNeighbor;
		return obj;
	}

	private int[] sortRho(double[] rhoDensity) {
		int[] ordRhoPos = new int[rhoDensity.length]; //track the index after sorting
		double[] rhoDescending = new double[rhoDensity.length]; //rho ordered vector
		double[] tmp = rhoDensity.clone();
		
		double maxV = tmp[0];
		int tI = 0;
		for (int i = 0; i < tmp.length; i++) {
			for (int j = 0; j < tmp.length; j++) {
				if (tmp[j] == -123321) {
					continue;
				}else if (tmp[j] >= maxV) {
					maxV = tmp[j];
					tI = j;
				}
			}//j
			
			tmp[tI] = -123321;
			rhoDescending[i] = maxV;
			ordRhoPos[i] = tI;
			maxV = -123321;
			tI = 0;
		}//i
		return ordRhoPos;
	}

	private double[] calcRho(double[][] distanceMatrix, double dc, int nOfInstaces) {
		double rho[] = new double[nOfInstaces];
		for (int i = 0; i < rho.length-1; i++) {
			for (int j = i+1; j < rho.length; j++) {
				double tp = -(distanceMatrix[i][j]/dc)*(distanceMatrix[i][j]/dc);
				//tp = Math.round(tp*1000000.0)/1000000.0;
				rho[i] = rho[i]+Math.exp(tp);
				rho[i] = Math.round(rho[i]*1000000.0)/1000000.0;
				
				tp = -(distanceMatrix[i][j]/dc)*(distanceMatrix[i][j]/dc);
				//tp = Math.round(tp*1000000.0)/1000000.0;
				rho[j] = rho[j]+Math.exp(tp);
				rho[j] = Math.round(rho[j]*1000000.0)/1000000.0;
			}
		}
		return rho;
	}

	private double findMaxDist(double[][] distanceMatrix) {
		double maxD = 0;// find the max distance point
		for (int i = 0; i < distanceMatrix.length; i++) {
			for (int j = i; j < distanceMatrix[i].length; j++) {
				if (distanceMatrix[i][j] >= maxD) {
					maxD = distanceMatrix[i][j]; 
				}
			}
		}
		
		return maxD;
	}

	private double calDC(Instances instances, double pos, DistanceType distance) {
		
		int nOfInstances = instances.numInstances();
		double dc = 0;
		int nCombination = nOfInstances*(nOfInstances-1)/2;//length m(m–1)/2, corresponding to pairs of observations in X
		double[] distPDIST = new double[nCombination];//vector to save the dist according to pdist function(matlab)
		
		// similar to pdist function on Matlab
		int count = 0;
		double gd =0;		
		
		for (int i = 0; i < nOfInstances-1; i++) {
			Instance i1 = instances.instance(i);
			for (int j = i+1; j < nOfInstances; j++) {
				Instance i2 = instances.instance(j);
				gd = DistanceMeasures.getInstance().calculateDistance(i2, i1, distance);
				//gd = distance.distance(i2,i1);
				distPDIST[count] = Math.round(gd*1000000.0)/1000000.0;
				count++;
				gd=0;
			}
		}
		
		Arrays.sort(distPDIST);
		
		//Estimate dc parameter according to the percentage entered (1% to 99%)
		double a = nCombination*pos/100;
		int position = (int) Math.ceil(a);
		dc = distPDIST[position];
		
		return dc;
	}

	private double[][] calcFitDist(Instances instances, DistanceType distance) {
		int nOfInstances = instances.numInstances();
		double[][] matrix = new double[nOfInstances][nOfInstances];
		
		// distance matrix
		double gd = 0;
		for (int i = 0; i < nOfInstances; i++) {
			Instance i1 = instances.instance(i);
			for (int j = 0; j < nOfInstances; j++) {
				Instance i2 = instances.instance(j);
				gd=0;
				gd = DistanceMeasures.getInstance().calculateDistance(i1, i2, distance);
				//gd = distance.distance(i1,i2);
				matrix[i][j] = Math.round(gd*1000000.0)/1000000.0;
			}
		}
		
		return matrix;
	}

	@Override
	public boolean isReady(Map<Parameter, Object> sharedObjects) {
		// TODO Auto-generated method stub
		if ( sharedObjects.get(Parameter.ALL_INSTANCES) != null &&
			 sharedObjects.get(Parameter.DPCI_DC_PERCENT) != null &&
			 sharedObjects.get(Parameter.DISTANCE_INFO) != null ||
			 sharedObjects.get(Parameter.SNN_DENSITY) != null){
				return true;
		}else{
				return false;
		}
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "DistanceInformation";
	}

	@Override
	public String technicalInformation() {
		// TODO Auto-generated method stub
		return "Este bloco retorna uma matriz com as distâncias entre os objetos.";
	}

}
