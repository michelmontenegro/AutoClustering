package br.rede.autoclustering.algorithms.dpc;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

import br.rede.autoclustering.core.ClusteringMethod;
import br.rede.autoclustering.util.DataBaseStructure;
import demo.XYLineAndShapeRendererDemo1;

//deu erro qndo mudou do jre 1.8 para o 1.7
//import javafx.scene.chart.ScatterChart;
//import sun.font.CreatedFontTracker;

import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

public class DpcIC {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DataBaseStructure db = new DataBaseStructure();
		//db.loadDataBaseCSV(new File("databases/dbclasd.in"));
		//db.loadDataBaseCSV(new File("uci-databases/heart-disease/processed.cleveland.data"));// 4 clusters
		//db.loadDataBaseCSV(new File("databases/sinteticDataXY_1.in"));// 4 clusters
		//db.loadDataBaseCSV(new File("databases/iris2.in"));// 3 clusters
		//db.loadDataBaseCSV(new File("databases/sinteticDataXY_2.in"));// 4 clusters
		//db.loadDataBaseCSV(new File("databases/sinteticDataXY_3_num.in"));// 4 clusters
		
		 File files = new File("databases/ufpaFault.arff");
         //DataBaseStructure db = new DataBaseStructure();
         db.loadDataBase(files);
		
		//List<ClusteringMethod> methods = new ArrayList<ClusteringMethod>();
		//Instances instances = db.getNormalizedData();
		Instances instances = db.getDatabase();
		
		//instances.setClassIndex(-1);
		System.out.println("# of attributes: "+instances.numAttributes());
		System.out.println("Attributo: "+instances.instance(0).value(6));
		System.out.println("Instância: "+instances.instance(0));
		
		
		int numInst = instances.numInstances();
		int numAtt = instances.numAttributes();
		double pos = 10;// percent of data points to choice the Dc parameter
		int nCombination = numInst*(numInst-1)/2;//length m(m–1)/2, corresponding to pairs of observations in X
		
		int[] target = new int[numInst];
		for (int i = 0; i < target.length; i++) {
			target[i] = (int)instances.instance(i).value(numAtt-1);
		}
		
		//Calculate Euclidian distance between each object
		double[][] distObj = new double[numInst][numInst];// dist matrix
		double[] distPDIST = new double[nCombination];//vector to save the dist according to pdist function(matlab)
		
		EuclideanDistance distance = new EuclideanDistance();
		distance.setDontNormalize(false);
		distance.setInstances(instances);
		
		// similar to pdist function on Matlab
		int count = 0;
		double gd =0;		
		
		for (int i = 0; i < numInst-1; i++) {
			Instance i1 = instances.instance(i);
			for (int j = i+1; j < numInst; j++) {
				Instance i2 = instances.instance(j);
				gd = distance.distance(i2,i1);
				distPDIST[count] = Math.round(gd*1000000.0)/1000000.0;
				count++;
				gd=0;
			}
		}
		
		System.out.print("pdsit: ");
		for (int i = 0; i < 15; i++) {
			System.out.print(distPDIST[i]+" | ");
		}
		System.out.println();
		Arrays.sort(distPDIST);
		
		// distance matrix
		gd = 0;
		for (int i = 0; i < numInst; i++) {
			Instance i1 = instances.instance(i);
			for (int j = 0; j < numInst; j++) {
				Instance i2 = instances.instance(j);
				gd=0;
				gd = distance.distance(i1,i2);
				distObj[i][j] = Math.round(gd*1000000.0)/1000000.0;
			}
		}
		
		/*for (int i = 0; i < 14; i++) {
			for (int j = 0; j < 14; j++) {
				System.out.print(distObj[i][j]+" | ");
			}
			System.out.println("");
		}*/
		
		//Estimate dc parameter according to the percentage entered (1% to 99%)
		double a = nCombination*pos/100;
		int position = (int) Math.ceil(a);
		double dc = distPDIST[position];
		System.out.println("position: "+position);
		System.out.println("dc: "+dc);
		
		//calculate the local density(rho) | Step 2
		double rho[] = new double[numInst];
		for (int i = 0; i < rho.length-1; i++) {
			for (int j = i+1; j < rho.length; j++) {
				double tp = -(distObj[i][j]/dc)*(distObj[i][j]/dc);
				//tp = Math.round(tp*1000000.0)/1000000.0;
				rho[i] = rho[i]+Math.exp(tp);
				rho[i] = Math.round(rho[i]*1000000.0)/1000000.0;
				
				tp = -(distObj[i][j]/dc)*(distObj[i][j]/dc);
				//tp = Math.round(tp*1000000.0)/1000000.0;
				rho[j] = rho[j]+Math.exp(tp);
				rho[j] = Math.round(rho[j]*1000000.0)/1000000.0;
			}
		}
		System.out.println("rho: "+rho[0]+" | "+rho[1]+" | "+rho[2]+" | "+rho[3]+" | "+rho[4]+" | "+rho[5]+" | "+rho[6]+" | "+rho[7]
				+" | "+rho[8]+" | "+rho[9]);
		
		double maxD = 0;// find the max distance point
		for (int i = 0; i < distObj.length; i++) {
			for (int j = i; j < distObj[i].length; j++) {
				if (distObj[i][j] >= maxD) {
					maxD = distObj[i][j]; 
				}
			}
		}
		System.out.println("Max distance: "+maxD);
		
		// put rho in descending order and keep track the index
		int[] ordRhoPos = new int[rho.length];//track the index after sorting
		double[] rhoDescending = new double[rho.length];// rho ordered vector
		double[] tmp = rho.clone();
		
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
		System.out.println("Index rho descending: "+ordRhoPos[0]+" | "+ordRhoPos[1]+" | "+ordRhoPos[2]+" | "+ordRhoPos[3]+" | "+ordRhoPos[4]
				+" | "+ordRhoPos[5]+" | "+ordRhoPos[6]+" | "+ordRhoPos[7]+" | "+ordRhoPos[8]+" | "+ordRhoPos[9]);
		
		System.out.println("'"+ordRhoPos[0]+"': "+rho[ordRhoPos[0]]+" | "+"'"+ordRhoPos[1]+"': "+rho[ordRhoPos[1]]+" | "+
				"'"+ordRhoPos[2]+"': "+rho[ordRhoPos[2]]+"");
		System.out.println("'"+ordRhoPos[8]+"': "+rho[ordRhoPos[8]]);
		
		//calculate delta and nearest neighbor | Step 2
		double[] delta = new double[numInst];
		int[] nearestNeighbor = new int[numInst];
		delta[ordRhoPos[0]] = -1;
		nearestNeighbor[ordRhoPos[0]] = 0;
		
		for (int i = 1; i < numInst; i++) {
			delta[ordRhoPos[i]] = maxD;
			for (int j = 0; j < i-1; j++) {
				if (distObj[ordRhoPos[i]][ordRhoPos[j]] < delta[ordRhoPos[i]]) {
					delta[ordRhoPos[i]] = distObj[ordRhoPos[i]][ordRhoPos[j]];
					nearestNeighbor[ordRhoPos[i]] = ordRhoPos[j];
				}
			}//j
		}//i
		maxV = delta[0];
		for (int i = 0; i < delta.length; i++) {
			if (delta[i] >= maxV) {
				maxV = delta[i];
			}
		}
		delta[ordRhoPos[0]] = maxV;
		
		System.out.println("delta: "+delta[0]+" | "+delta[1]+" | "+delta[2]+" | "+delta[3]+" | "+delta[4]+" | "+delta[5]+" | "+delta[6]
				+" | "+delta[7]+" | "+delta[8]+" | "+delta[9]+" | "+delta[10]+" | "+delta[11]+" | "+delta[12]+" | "+delta[13]+" | "+delta[14]);
		System.out.println("NN: "+nearestNeighbor[0]+" | "+nearestNeighbor[1]+" | "+nearestNeighbor[2]+" | "+nearestNeighbor[3]+" | "
				+nearestNeighbor[4]+" | "+nearestNeighbor[5]+" | "+nearestNeighbor[6]+" | "+nearestNeighbor[7]+" | "+nearestNeighbor[8]
						+" | "+nearestNeighbor[9]);
		
		
		
		//Normalize(0-1) rho and delta Step 3
		//rho
		double[] rhoNormalized = new double[rho.length];
		double min = rho[0];
		maxV = rho[0];
		for (int i = 0; i < rho.length; i++) {
			if (rho[i] <= min) {
				min = rho[i];
			}
			if (rho[i] >= maxV) {
				maxV = rho[i];
			}
		}
		
		for (int i = 0; i < rho.length; i++) {
			rhoNormalized[i] = (rho[i] - min)/(maxV - min);
			rhoNormalized[i] = Math.round(rhoNormalized[i]*1000000.0)/1000000.0;
		}
		
		//delta
		double[] deltaNormalized = new double[delta.length];
		min = delta[0];
		maxV = delta[0];
		for (int i = 0; i < delta.length; i++) {
			if (delta[i] <= min) {
				min = delta[i];
			}
			if (delta[i] >= maxV) {
				maxV = delta[i];
			}
		}
		
		for (int i = 0; i < delta.length; i++) {
			deltaNormalized[i] = (delta[i] - min)/(maxV - min);
			deltaNormalized[i] = Math.round(deltaNormalized[i]*1000000.0)/1000000.0;
		}
		//System.out.println("Delta(distance) and Rho(density) were normalized");
		
		// find the min for each position between rho and delta | Step 4
		double[] gamma = new double[rho.length];
		for (int i = 0; i < gamma.length; i++) {
			if (rhoNormalized[i] < deltaNormalized[i]) {
				gamma[i] = rhoNormalized[i];
			}else
			{
				gamma[i] = deltaNormalized[i];
			}
		}
		System.out.println("Gamma: "+gamma[0]+" | "+gamma[1]+" | "+gamma[2]+" | "+gamma[3]+" | "+gamma[4]+" | "+gamma[5]+" | "+gamma[6]
				+" | "+gamma[7]+" | "+gamma[8]+" | "+gamma[9]);
		
		double[] ordGamma = gamma.clone();
		int sizeGamma = ordGamma.length;
		Arrays.sort(ordGamma);
		
		//Calculate the upper bound - Step 5
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
		System.out.println("Mean(gamma): "+uGamma);
		
		//variance
		double sqDiff = 0;
		for (int i = 0; i < gamma.length; i++) {
			sqDiff += (gamma[i] - uGamma)*(gamma[i] - uGamma);
		}
		sqDiff = sqDiff/gamma.length;
		sqDiff = Math.round(sqDiff*1000000.0)/1000000.0;
		System.out.println("Variace(gamma): "+sqDiff);
		
		//std (standard deviation)
		stdGamma = Math.sqrt(sqDiff);
		stdGamma = Math.round(stdGamma*1000000.0)/1000000.0;
		System.out.println("Std(gamma): "+stdGamma);
		
		limUpper = (uGamma + 3 * stdGamma);
		limUpper = Math.round(limUpper*1000000.0)/1000000.0;
		//limUpper = uGamma + 3*stdGamma;
		System.out.println("Chebyshev's Inequality(upper bound): "+limUpper);
		
		//plot(1)
		//gamma
		int[] ordVectorNInstances = ordRhoPos.clone();
		Arrays.sort(ordVectorNInstances);
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries xySeries = new XYSeries("Theta values");
		for (int i = 0; i < rho.length; i++) {
			xySeries.add(ordVectorNInstances[i], ordGamma[i]);
		}
		dataset.addSeries(xySeries);
		
		//bound
		XYSeries lineD = new XYSeries("Upper bound");
		/*lineD.add(0, limUpper);
		//lineD.add(60, limUpper);
		lineD.add(numInst, limUpper);*/
		dataset.addSeries(lineD);
		
		//plot 1
		JFreeChart chart = ChartFactory.createScatterPlot("Judgement Index", "Number of Instances", "Theta", dataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot chartProp = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		
		//0 is the scatter plot
		renderer.setSeriesVisibleInLegend(0, false);
		renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesShapesVisible(0, true);
		
		//1 is the line plot
		renderer.setSeriesLinesVisible(1, true);
		renderer.setSeriesShapesVisible(1, false);
		chartProp.setRenderer(renderer);
		
		ValueMarker upperBoundLimit = new ValueMarker(limUpper);
		upperBoundLimit.setLabel("Upper bound");
		upperBoundLimit.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
		upperBoundLimit.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
		upperBoundLimit.setPaint(Color.red);
		chartProp.setBackgroundPaint(Color.white);
		chartProp.addRangeMarker(upperBoundLimit);
		
		ChartFrame frame = new ChartFrame("Plot 1", chart);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(572, 510);
		//frame.setSize(700, 640);
		
		//plot 3
		XYSeriesCollection dataset2 = new XYSeriesCollection();
		XYSeries xySeries3 = new XYSeries("Theta values");
		for (int i = 0; i < deltaNormalized.length; i++) {
			xySeries3.add(rhoNormalized[i], deltaNormalized[i]);
		}
		dataset2.addSeries(xySeries3);
		
		//bound
		XYSeries xySeries4 = new XYSeries("Upper bound");
		xySeries4.add(limUpper, 1);
		xySeries4.add(limUpper, limUpper);
		xySeries4.add(1, limUpper);
		//lineD.add(numInst, limUpper);
		dataset2.addSeries(xySeries4);
		
		JFreeChart chart3 = ChartFactory.createScatterPlot("Judgement Index", "Rho", "Delta", dataset2, PlotOrientation.VERTICAL, true, true, false);
		XYPlot chartProp3 = chart3.getXYPlot();
		XYLineAndShapeRenderer renderer3 = new XYLineAndShapeRenderer();
		
		//0 is the scatter plot
		renderer3.setSeriesVisibleInLegend(0, false);
		renderer3.setSeriesLinesVisible(0, false);
		renderer3.setSeriesShapesVisible(0, true);
		
		//1 is the line plot
		renderer3.setSeriesLinesVisible(1, true);
		renderer3.setSeriesShapesVisible(1, false);
		chartProp3.setRenderer(renderer3);
		
		/*ValueMarker upperBoundLimit = new ValueMarker(limUpper);
		upperBoundLimit.setLabel("Upper bound");
		upperBoundLimit.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
		upperBoundLimit.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
		upperBoundLimit.setPaint(Color.red);*/
		chartProp3.setBackgroundPaint(Color.white);
		//chartProp3.addRangeMarker(upperBoundLimit);
		
		ChartFrame frame3 = new ChartFrame("Plot 3", chart3);
		frame3.pack();
		frame3.setVisible(true);
		frame3.setSize(572, 510);
		//frame3.setSize(700, 640);
		
		// Find clusters | Step 5
		int nC = 0;//number of clusters
		//int[] clustersID = new int[nC];
		ArrayList<Integer> clusterID = new ArrayList<Integer>();
		for (int i = 0; i < gamma.length; i++) {
			if (gamma[i] > limUpper) {
				nC++;
				clusterID.add(i);
			}
		}
		System.out.println("Number of clusters: "+nC);
		System.out.println("Cluster centers (position): "+clusterID);
		
		double[] newGammaData = new double[gamma.length-nC];// gamma values without the clusters
		count = 0;
		for (int i = 0; i < gamma.length; i++) {
			if (gamma[i] < limUpper) {
				newGammaData[count] = gamma[i];
				count++;
			}
		}
		
		// STEP 6
		// Assign the cluster centers
		int[] clustersGroup = new int[numInst];
		for (int i = 0; i < clustersGroup.length; i++) {
			clustersGroup[i] = -1;
		}
		for (int i = 0; i < clusterID.size(); i++) {
			//clustersGroup[clusterID.get(i)] = (i+1);
			clustersGroup[clusterID.get(i)] = i;
		}
		
		// Assign the nearest neighbor of higher density
		for (int i = 0; i < numInst; i++) {
			if (clustersGroup[ordRhoPos[i]] == -1) {
				clustersGroup[ordRhoPos[i]] = clustersGroup[nearestNeighbor[ordRhoPos[i]]];
			}
		}
		
		System.out.println("CG: "+Arrays.toString(clustersGroup));
		System.out.println("Ta: "+Arrays.toString(target));
		
		//acc
		double acc = 0;
		count = 0;
		for (int i = 0; i < clustersGroup.length; i++) {
			if (clustersGroup[i] == target[i]) {
				count++;
			}
		}
		acc = ((double)count/numInst);
		System.out.println("Accuracy: "+(acc*100));
		
		//Plot 2
	}

}