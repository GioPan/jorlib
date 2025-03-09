package org.jorlib.demo.io.tspLibReader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jorlib.io.tspLibReader.GTSPLibInstance;
import org.jorlib.io.tspLibReader.TSPLibTour;

import javax.swing.*;

/**
 * Simple class which reads a TSPLib instance
 * @author Joris Kinable
 * @since April 24, 2015
 *
 */
public final class TSPDemo {

	public TSPDemo() throws IOException{
		//Read a TSP instance from the TSPLib, as well as a TSP tour 
		File directory = new File("/Users/lct495/Documents/jorlib/data/gtspLib/");
		File instanceData = new File(directory, "4br17.gtsp");
		//File optimalTour = new File(directory, "ulysses16.opt.tour");
		
		//Create a TSP instance, thereby parsing the TSPLib file
		GTSPLibInstance problem = new GTSPLibInstance(instanceData);
		//Add the tour
		//problem.addTour(optimalTour);
		
		//Print some information about the problem
		System.out.println("Name of TSP problem: "+problem.getName());
		System.out.println("Number of vertices: "+problem.getDimension());
		System.out.println("Number of registered tours: "+problem.getTours().size());
		System.out.println("Number of clusters:"+problem.getnClusters());
		for(int k = 1; k <= problem.getnClusters(); k++){
			System.out.println("Cluster "+k);
			for(int i: problem.getClusters()[k-1]){
				System.out.print(i+" ");
			}
			System.out.println();
		}
		for(int i = 1; i <= problem.getDimension(); i++){
			for(int j = 1; j <= problem.getDimension(); j++){
				System.out.println(i+" "+j+" = "+ problem.getDistance(i,j));
			}
		}
		System.out.println();
		//System.out.println("Length of registered tour: "+problem.getTours().get(0).distance(problem));

		File tourData = new File(directory, "4br17.31.tour");
		problem.addTour(tourData);

		List<TSPLibTour> tours = problem.getTours();
		for(TSPLibTour t : tours){
			System.out.println("Tour size " + t.size());
			for(int i =1 ; i <= t.size(); i++){
				System.out.println(t.get(i-1));
			}
		}
	}
	
	public static void main(String[] args){
		try {
			new TSPDemo();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
