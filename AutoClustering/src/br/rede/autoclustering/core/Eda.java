package br.rede.autoclustering.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;

import com.opencsv.CSVWriter;

//import sun.invoke.empty.Empty;//deu erro qndo mudou do jre 1.8 para o 1.7
import br.rede.autoclustering.util.FitnessChart;
import br.rede.autoclustering.util.SortedList;
import br.rede.autoclustering.util.Util;
import br.rede.autoclustering.visualautocluster.MainVAC;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * @author aruanda
 *
 */
public final class Eda {

    private static Population pop;
    private static ProbDag probdag;
    private static final float ALPHA = 0.5F;
	private static final Eda eda;
    private static boolean verbose;
	
    static {
    	eda = new Eda();
    }
    
    public static Eda getInstance() {
		return eda;
	}
    
    
    public Individual execute(Collection<Node> nodes, Instances instances, Classifier classifier, int nGenerations, int nPopulation, int slices, BufferedWriter writer,int rodada, String name, MainVAC mainVAC) throws Exception {
        Individual best = null;
    	probdag = new ProbDag(nodes, instances,classifier, slices);
    	double getBestFitnessAnterior = 0;
    	double getBestFitnessAtual = 0;
    	int countChangeBest = 0;
    	String[][] logGenerations = new String[nPopulation][nGenerations]; 
    	
        for (int i = 0; i < nGenerations ; i++) {
            Util.generation=i;
        	if (verbose)
        		printGeneration(i);
			pop = new Population(probdag,nPopulation);
            if ((best!= null) && (pop.bestIndividual().compareTo(best) > 0))  {
                //pop.addIndividual(best);
            	pop.addIndividual(mainVAC.individuoSelecionado);
            }
            probdag.estimateProbability(pop);
            ArrayList<String> listaCombinedBlocks;// armazenar o nome dos blocos conectados
            Map<Parameter, Float> param = null;
            
            int countLine = 0;
            //System.out.println(">>>>"+pop.getIndividual().size()); saida é 50 individuos
            /*
             * Vai percorrrer todos os individuos atualizando 
             */
            for (Individual ind : pop.getIndividual()){
            	listaCombinedBlocks = new ArrayList<String>();
            	String tmp = "";
            	String listaCombinedParam = "";// armazenar o nome dos parâmetros
            	
            	int sizeNodes = ind.getNodes().size();
            	int cs=0;
            	int nOfNodes = ind.getNodes().size()-1;
            	String[] saveParam = new String[nOfNodes];
            	/*Dentro do individuo, ele pega os nodes separadamente(chamados de Nodes Individuais)
            	 * Este Nodes tem a informação da combinação dos blocos (Ex.: listaCombinedBlocks.add(n.getNode().getMethodName());)
            	 * */
//            	System.out.println(">>>>"+ind.getNodes().size()); //Varia de 2 a 4
                for(IndividualNode n : ind.getNodes()){
                	listaCombinedBlocks.add(n.getNode().getMethodName());
                	
                	if(cs == 1 && cs == sizeNodes-1){
						tmp = n.getNode().getMethodName();
                	}else if(cs == 1 && cs < sizeNodes-1) {
                		tmp = n.getNode().getMethodName()+"->";
					}else if(cs > 1 && cs < sizeNodes-1) {
						tmp = tmp+n.getNode().getMethodName()+"->";
					}else if(cs == sizeNodes-1) {
						tmp = tmp+n.getNode().getMethodName();
					}
                	cs++;
                	
                	if (n.getProperties().isEmpty()) {
						param = null;
					}else
					{
						param = n.getProperties();
						listaCombinedParam = listaCombinedParam+" "+param;
						/*for (int j = 0; j < saveParam.length; j++) {
						
						}*/
					}
                }
                // mostra os individuos criados para cada geração.
                //System.out.println(listaCombinedBlocks + " - fitness: " + ind.getFitness() +" | "+param);
                
                //logGenerations[countLine][i] = tmp+"->"+listaCombinedParam+"->"+ind.getFitness();
                logGenerations[countLine][i] = tmp+';'+ind.getFitness()+';'+listaCombinedParam;
                //System.out.println("logGenerations: "+logGenerations[countLine][i]);
                countLine++;
            }
            //System.out.println("//");
            
//            StringBuilder sbGroupsIndAllGen = new StringBuilder();
//            for (Individual ind : pop.getIndividual()){
//            	if (ind.getGroups()!=null) {
//            		sbGroupsIndAllGen.append(ind.getGroups().size()+";"+ind.getFitness()+";"+ ind.+ "\r\n");
//            	} else {
//            		sbGroupsIndAllGen.append("-1;"+ind.getFitness()+"\r\n"); //Nulo
//            	}
//            }
//            Util.writePRobability("GroupsIndAllGen", sbGroupsIndAllGen);
            
//          for (Individual ind : pop.getIndividual()){
//            	mainVAC.listModel.addElement(ind);            	
//            }
//            
            best = pop.bestIndividual();
            mainVAC.setIndividuoSelecionado(best);
            mainVAC.ispause = false;
            while (mainVAC.ispause) {
            	System.out.print("");
            }
            
            saveInfoBestIdiviuo(pop, best);
            /* Acima ele percorre todos osindividuos e aqui ele informa qual o melhor da geação, por isso pode ocorrer repetições de individuos.
             * O mesmo Ind. pode passar a gerações seguintes, logo as variaveis gen e ind, seriam atualizados pra fins de exibição, mas ainda seriam o mesmo objeto,
             * isto faz com que List Model repita Gen e Valor final.*/
//            System.out.println(best);
            mainVAC.listModel.add(mainVAC.indexList, best);
            mainVAC.indexList++;
            

//            try {
//            	  Thread.sleep(2000);// pausa de 2000 milisegundos
//            	}catch (InterruptedException e) {
//            	  e.printStackTrace(); 
//            	}
            
//            Object showDialog = ProgressDialog.show(context, context.getResources()
//        			.getString(R.string.DialogDownloadTitle), context
//        			.getResources().getString(R.string.DialogDownloadText), true,
//        			true);
            
            ArrayList<String> lista = new ArrayList<String>();
            Map<Parameter, Float> bestParam = null;//melhor parametro utilizado no individuo
            for(IndividualNode n : best.getNodes()){
            	lista.add(n.getNode().getMethodName());
            	if (n.getProperties().isEmpty()) {
					bestParam = null;
				}else
				{
					bestParam = n.getProperties();
					// OLHAR ISSO PARA A PARTE DE PRINT DOS PARAMETROS
				}
            }
            // debug dos individuos presentes na geracao atual
            System.out.print(".");
            //System.out.println("\nRodada em andamento de número: "+(rodada+1));
            //System.out.println("Melhor individuo obtido na Geracao:" +(i+1));
            
//            DefaultListModel<String> listModel = new DefaultListModel();
//            listModel.addElement("Melhor individuo obtido na Geracao:" +(i+1));
            
            //System.out.println("Bloco(s) utilizado(s)" + lista);
            //System.out.println("Acuracia = " + best.getFitness());
            try {
				writer.write((i+1)+","+best.getFitness()+",{");
		        for (IndividualNode node : best.getNodes()){
		        	IndividualNode method = node;
		        	writer.write(method.getNode().getMethodName()+"[");
		        	Map<Parameter, Float> parameters = node.getProperties();
		        	writer.write("DISTANCE:" + node.getDistanceType());
		        	for ( Parameter p : parameters.keySet() ) {
		        		writer.write(","+p+ " - " + parameters.get(p));
		        	}
		        	writer.write("],");
		        }
		        writer.write("}\n");
		        writer.flush();
		        //writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
            
            //break if it does not change
            /*getBestFitnessAtual = best.getFitness();
            if (getBestFitnessAtual == getBestFitnessAnterior) {
				countChangeBest++;
				System.out.print(countChangeBest+" - ");
				if (countChangeBest == 50) {
					System.out.print(countChangeBest);
					System.out.println("/nStoped in Generation: "+(i+1));
					break;
				}
			}else {
				countChangeBest = 0;
				getBestFitnessAnterior = best.getFitness();
				getBestFitnessAtual = best.getFitness();
			}*/
         
            //System.gc();
        }// end FOR generations
        
        //create log about the generations
        
        String file = "runs/"+name+"/run-"+name+""+rodada+"/logGenerations-"+name+""+rodada+".csv";
        String[][] information = logGenerations;
        logGenerations = null;
        saveToCSV(file,information);
        
        //System.out.println("\n*****************************");
        return best;
	}
    
    private void saveInfoBestIdiviuo(Population pop, Individual best) {
    	StringBuilder builder;
    	for (Individual ind : pop.getIndividual()){
    		builder = new StringBuilder();
    		int win = 0;
    		if (best.run==ind.run && best.generation==ind.generation && best.individuo==ind.individuo) {win=1;} else {win=0;}
    		for (String out: ind.outBlockFit) {
    			builder.append(out+";"+win+"\n");
    		}
    		Util.writePRobability("AutoCluster_Full_Data", builder);
    	}
    }
    
	private void saveToCSV(String file, String[][] information) {
		List<String[]> records = new ArrayList<>();
		String[] header = new String[information[0].length];
		for (int i = 0; i < header.length; i++) {
			//header[i] = "Generation_"+(i+1);
			header[i] = "B"+(i+1)+";F"+(i+1)+";P"+(i+1);
		}
		records.add(header);
		for (int i = 0; i < information.length; i++) {
			String[] data = new String[information[0].length];
			for (int j = 0; j < information[i].length; j++) {
				data[j] = information[i][j];
			}
			records.add(data);
		}

		File sp = new File(file);
        boolean exists = sp.exists();
    	if (exists) {
			System.out.println("\nFile '"+file+"' was found!");
			try /*(FileOutputStream fos = new FileOutputStream(fileName);
	                OutputStreamWriter osw = new OutputStreamWriter(fos, 
	                        StandardCharsets.UTF_8);
	                CSVWriter csvWriter = new CSVWriter(osw))*/{
				FileWriter fileWriter = new FileWriter(file);
				CSVWriter csvWriter = new CSVWriter(fileWriter,
				        //CSVWriter.DEFAULT_SEPARATOR,
						';',
				        CSVWriter.NO_QUOTE_CHARACTER,
				        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
				        CSVWriter.DEFAULT_LINE_END);
				
				csvWriter.writeAll(records);
	            fileWriter.flush();
				fileWriter.close();
	            csvWriter.flush();
	            csvWriter.close();
	            records = null;
	            //System.out.println("File '"+fileName+"' insertion COMPLETED!");
	        }catch (Exception e) {
				// TODO: handle exception
	        	//e.printStackTrace();
			}
		} else {
			System.out.println("File '"+file+"' NOT found!");
		}
	}


	/**
	 * @param totFitness
	 * @param curProb
	 * @return
	 */
	public static float pbil(double totProb, float curProb) {
		float pbil = (1 - ALPHA) *  curProb;
		pbil += ALPHA * totProb / pop.getIndividual().size()/ 2;
		return pbil;
	}

	/**
	 * @return Returns the pop.
	 */
	public Population getPop() {
		return pop;
	}
	/**
	 * @return Returns the probdag.
	 */
	public ProbDag getProbdag() {
		return probdag;
	}


	public static boolean isVerbose() {
		return verbose;
	}


	public static void setVerbose(boolean verbose) {
		Eda.verbose = verbose;
	}
	
    private void printGeneration(int i) {
    	System.out.println("******************************");
    	System.out.println("******** GENERATION: "+(i+1) + " ********");
    	System.out.println("******************************\n");
	}
}
