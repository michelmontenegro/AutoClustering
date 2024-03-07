package structures;


import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;
import weka.core.Instances;
import br.rede.autoclustering.structures.grid.Grid;
import br.rede.autoclustering.util.DataBaseStructure;

public class GridTest extends TestCase{

	public void testGrid(){
		DataBaseStructure db = new DataBaseStructure();
		db.loadDataBaseCSV(new File("/home/sfelixjr/dbclasd.in"));		
//		ArrayList<Attribute> al = new ArrayList<Attribute>();
//		for ( int i = 0 ; i < DataBaseStructure.getInstance().getCountAttribute();i++)
//			al.add(DataBaseStructure.getInstance().getAttribute(i));
//		IGrid grid = new Grid();
//		grid.constroiGrade(al);
//		
		Instances instances = db.getDatabase();
		double[] lower_bounds = new double[instances.numAttributes()];
		double[] upper_bounds = new double[instances.numAttributes()];
		createLowerBounds(instances, lower_bounds, upper_bounds);
		
		Grid grade = new Grid();
		grade.createGrid(2, lower_bounds, upper_bounds, instances);
	}
	
	public void createLowerBounds(Instances db, double[] lower_bounds,double[] upper_bounds) {
		Arrays.fill(lower_bounds, Double.MAX_VALUE);
		Arrays.fill(upper_bounds, Double.MIN_VALUE);
		for (int i = 0; i < db.numInstances(); i++) {
			for (int j = 0; j < db.numAttributes(); j++) {
				if ( db.instance(i).value(j) < lower_bounds[j] )
					lower_bounds[j] = db.instance(i).value(j);
				if ( db.instance(i).value(j) > upper_bounds[j] )
					upper_bounds[j] = db.instance(i).value(j);
			}
		}
	}
	
}
