/* Copyright 2012 David Hadka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.jorlib.io.tspLibReader;

import org.jorlib.io.tspLibReader.fieldTypesAndFormats.*;
import org.jorlib.io.tspLibReader.graph.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A TSPLIB problem instance.
 * 
 * @author David Hadka
 */
public class GTSPLibInstance {

	/**
	 * The name of this problem instance.
	 */
	private String name;

	/**
	 * The type of this problem instance.
	 */
	private DataType dataType;

	/**
	 * Any comments about this problem instance.
	 */
	private String comment;

	/**
	 * The number of nodes defined by this problem instance.
	 */
	private int dimension;
	/**
	 * The number of clusters.
	 */
	private int nClusters;
	/**
	 * The array of clusters. Each element of the array is a cluster containing a list of nodes.
	 */
	private List<Integer>[] clusters;

	/**
	 * The truck capacity in CVRP problem instances.
	 */
	private int capacity;

	/**
	 * The way edge weights are specified.
	 */
	private EdgeWeightType edgeWeightType;

	/**
	 * The format of the edge weight matrix when explicit weights are used; or
	 * {@code null} if edge weights are not explicit.
	 */
	private EdgeWeightFormat edgeWeightFormat;

	/**
	 * The format of edge data; or {@code null} if edge data is not explicitly
	 * defined.
	 */
	private EdgeDataFormat edgeDataFormat;

	/**
	 * The format of node coordinate data.
	 */
	private NodeCoordType nodeCoordinateType;

	/**
	 * The way graphical displays of the data should be generated.
	 */
	private DisplayDataType displayDataType;

	/**
	 * The distance table that defines the nodes, edges, and weights for this
	 * problem instance.
	 */
	private DistanceTable distanceTable;

	/**
	 * The data used to graphically display the nodes; or {@code null} if the
	 * display data is not explicitly defined.
	 */
	private NodeCoordinates displayData;

	/**
	 * The edges that are required in each solution to this problem instance.
	 */
	private EdgeData fixedEdges;

	/**
	 * The solutions to this problem instance.
	 */
	private List<TSPLibTour> tours;

	/**
	 * The demands and depot nodes for vehicle routing problems; or {@code null}
	 * if this is not a vehicle routing problem instance.
	 */
	private VehicleRoutingTable vehicleRoutingTable;

	/**
	 * Constructs a new, empty TSPLIB problem instance.
	 */
	public GTSPLibInstance() {
		super();

		tours = new ArrayList<TSPLibTour>();
	}

	/**
	 * Constructs a TSPLIB problem instance from the specified TSPLIB file.
	 *
	 * @param file the TSPLIB file defining the problem
	 * @throws IOException if an I/O error occurred while loading the TSPLIB
	 *         file
	 */
	public GTSPLibInstance(File file) throws IOException {
		this();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		load(reader);
	}

	/**
	 * Constructs a TSPLIB problem instance from the specified TSPLIB file.
	 *
	 * @param inputStream inputStream to the TSPLIB file defining the problem
	 * @throws IOException if an I/O error occurred while loading the TSPLIB
	 *         file
	 */
	public GTSPLibInstance(InputStream inputStream) throws IOException {
		this();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		load(reader);
	}
	
	/**
	 * Loads a problem instance from the specified TSPLIB file.
	 * 
	 * @param reader input stream to a TSPLIB file defining the problem
	 * @throws IOException if an I/O error occurred while loading the TSPLIB
	 *         file
	 */
	public void load(BufferedReader reader) throws IOException {
		String line = null;
		
		try {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				System.out.println(line);
				if (line.equals("NODE_COORD_SECTION")) {
					if (nodeCoordinateType == null) {
						nodeCoordinateType = edgeWeightType.getNodeCoordType();
					}
					
					distanceTable = new NodeCoordinates(dimension, edgeWeightType);
					distanceTable.load(reader);
				} else if (line.equals("EDGE_WEIGHT_SECTION")) {
					if (DataType.SOP.equals(dataType)) {
						// for whatever reason, SOP instances have an extra line with
						// the node count
						reader.readLine();
					}
					
					distanceTable = new EdgeWeightMatrix(dimension, edgeWeightFormat);
					distanceTable.load(reader);
				} else if (line.equals("EDGE_DATA_SECTION")) {
					distanceTable = new EdgeData(dimension, edgeDataFormat);
					distanceTable.load(reader);
				} else if (line.equals("DISPLAY_DATA_SECTION")) {
					displayData = new NodeCoordinates(dimension, NodeCoordType.TWOD_COORDS, null);
					displayData.load(reader);
				} else if (line.equals("TOUR_SECTION") || line.equals("-1")) {
					TSPLibTour tour = new TSPLibTour();
					tour.load(reader);
					tours.add(tour);
				} else if (line.equals("FIXED_EDGES_SECTION") || line.matches("^\\s*FIXED_EDGES\\s*\\:\\s*$")) {
					fixedEdges = new EdgeData(dimension, EdgeDataFormat.EDGE_LIST);
					fixedEdges.load(reader);
				} else if (line.equals("DEMAND_SECTION")) {
					if (vehicleRoutingTable == null) {
						vehicleRoutingTable = new VehicleRoutingTable(dimension);
					}
					
					vehicleRoutingTable.loadDemands(reader);
				} else if (line.equals("DEPOT_SECTION")) {
					if (vehicleRoutingTable == null) {
						vehicleRoutingTable = new VehicleRoutingTable(dimension);
					}
					
					vehicleRoutingTable.loadDepots(reader);
				}else if(line.equals("GTSP_SET_SECTION:")){
					clusters = new ArrayList[nClusters];
					for(int k = 1; k <= nClusters; k++){
						clusters[k-1] = new ArrayList<Integer>();
						String clusterLine = reader.readLine();
						String[] tokens = clusterLine.split(" ");
						for(int i = 2; i <= tokens.length-1; i++){
							clusters[k-1].add(Integer.parseInt(tokens[i-1].trim()));
						}
					}
				} else if (line.equals("EOF")) {
					break;
				} else if (line.isEmpty()) {
					//do nothing
				} else {
					String[] tokens = line.split(":");
					String key = tokens[0].trim();
					String value = tokens[1].trim();
					if (key.equals("NAME")) {
						name = value;
					} else if (key.equals("COMMENT")) {
						if (comment == null) {
							comment = value;
						} else {
							comment = comment + "\n" + value;
						}
					} else if (key.equals("TYPE")) {
						dataType = DataType.valueOf(value);
					} else if (key.equals("DIMENSION")) {
						dimension = Integer.parseInt(value);
					} else if (key.equals("CAPACITY")) {
						capacity = Integer.parseInt(value);
					} else if (key.equals("EDGE_WEIGHT_TYPE")) {
						edgeWeightType = EdgeWeightType.valueOf(value);
					} else if (key.equals("EDGE_WEIGHT_FORMAT")) {
						edgeWeightFormat = EdgeWeightFormat.valueOf(value);
					} else if (key.equals("EDGE_DATA_FORMAT")) {
						edgeDataFormat = EdgeDataFormat.valueOf(value);
					} else if (key.equals("NODE_COORD_FORMAT")) {
						nodeCoordinateType = NodeCoordType.valueOf(value);
					} else if (key.equals("DISPLAY_DATA_TYPE")) {
						displayDataType = DisplayDataType.valueOf(value);
					} else if(key.equals("GTSP_SETS")){
						nClusters = Integer.parseInt(value);
					}
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		// fill in default settings
		if (nodeCoordinateType == null) {
			nodeCoordinateType = NodeCoordType.NO_COORDS;
		}
		
		if (displayDataType == null) {
			if (NodeCoordType.NO_COORDS.equals(nodeCoordinateType)) {
				displayDataType = DisplayDataType.NO_DISPLAY;
			} else if (displayData != null) {
				displayDataType = DisplayDataType.TWOD_DISPLAY;
			} else {
				displayDataType = DisplayDataType.COORD_DISPLAY;
			} 
		}
	}
	
	/**
	 * Adds a solution to this TSPLIB problem instance.  This method does not
	 * verify that the solution has all required edges; the caller must ensure
	 * this condition holds.
	 * 
	 * @param tour the solution to add
	 */
	public void addTour(TSPLibTour tour) {
		tours.add(tour);
	}
	
	/**
	 * Adds a solution to this TSPLIB problem instance that is defined in a
	 * separate file.  This method does not verify that the solution is a
	 * valid tour for this problem instance; the caller must ensure this
	 * condition holds.
	 * 
	 * @param file the file containing a solution to this TSPLIB problem
	 *             instance
	 * @throws IOException if an I/O error occurred while loading the tour
	 */
	public void addTour(File file) throws IOException {
		GTSPLibInstance problem = new GTSPLibInstance();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		problem.load(reader);
		
		if (problem.getDataType().equals(DataType.TOUR)) {
			tours.addAll(problem.getTours());
		} else {
			throw new IllegalArgumentException("not a tour file");
		}
	}

	/**
	 * Adds a solution to this TSPLIB problem instance that is defined in a
	 * separate file.  This method does not verify that the solution is a
	 * valid tour for this problem instance; the caller must ensure this
	 * condition holds.
	 *
	 * @param inputStream inputStream to the file containing a solution to this TSPLIB problem
	 *        instance
	 * @throws IOException if an I/O error occurred while loading the tour
	 */
	public void addTour(InputStream inputStream) throws IOException {
		GTSPLibInstance problem = new GTSPLibInstance();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		problem.load(reader);

		if (problem.getDataType().equals(DataType.TOUR)) {
			tours.addAll(problem.getTours());
		} else {
			throw new IllegalArgumentException("not a tour file");
		}
	}

	/**
	 * Returns the name of this problem instance.
	 * 
	 * @return the name of this problem instance
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type of this problem instance.
	 * 
	 * @return the type of this problem instance
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * Returns any comments about this problem instance.
	 * 
	 * @return any comments about this problem instance
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Returns the number of nodes defined by this problem instance.
	 * 
	 * @return the number of nodes defined by this problem instance
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * A surrogate for getDimension().
	 * @return the dimension of the problem (number of nodes)
	 */
	public int getnNodes(){
		return getDimension();
	}

	/**
	 * Returns the truck capacity in CVRP problem instances.  The return value
	 * is undefined if the data type is not {@code CVRP}.
	 * 
	 * @return the truck capacity in CVRP problem instances
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Returns the way edge weights are specified.
	 * 
	 * @return the way edge weights are specified
	 */
	public EdgeWeightType getEdgeWeightType() {
		return edgeWeightType;
	}

	/**
	 * Returns the format of the edge weight matrix when explicit weights are
	 * used; or {@code null} if edge weights are not explicit.
	 * 
	 * @return the format of the edge weight matrix when explicit weights are
	 *         used; or {@code null} if edge weights are not explicit
	 */
	public EdgeWeightFormat getEdgeWeightFormat() {
		return edgeWeightFormat;
	}

	/**
	 * Returns the format of edge data; or {@code null} if edge data is not
	 * explicitly defined.
	 * 
	 * @return the format of edge data; or {@code null} if edge data is not
	 *         explicitly defined
	 */
	public EdgeDataFormat getEdgeDataFormat() {
		return edgeDataFormat;
	}

	/**
	 * Returns the format of node coordinate data.
	 * 
	 * @return the format of node coordinate data
	 */
	public NodeCoordType getNodeCoordinateType() {
		return nodeCoordinateType;
	}

	/**
	 * Returns the way graphical displays of the data should be generated.
	 * 
	 * @return the way graphical displays of the data should be generated
	 */
	public DisplayDataType getDisplayDataType() {
		return displayDataType;
	}

	/**
	 * Returns the distance table that defines the nodes, edges, and weights
	 * for this problem instance.
	 * 
	 * @return the distance table that defines the nodes, edges, and weights
	 *         for this problem instance
	 */
	public DistanceTable getDistanceTable() {
		return distanceTable;
	}

	/**
	 * A wrapper around get distance table. This allows indexing the nodes from
	 * 1..nNodes, instead for 0..nNodes-1.
	 * @param i a node
	 * @param j another node
	 * @return the distance
	 */
	public double getDistance(int i, int j){
		return distanceTable.getDistanceBetween(i-1,j-1);
	}

	/**
	 * Returns the data used to graphically display the nodes; or {@code null}
	 * if the display data is not explicitly defined.
	 * 
	 * @return the data used to graphically display the nodes; or {@code null}
	 *         if the display data is not explicitly defined
	 */
	public NodeCoordinates getDisplayData() {
		return displayData;
	}

	/**
	 * Returns the edges that are required in each solution to this problem
	 * instance.
	 * 
	 * @return the edges that are required in each solution to this problem
	 * instance
	 */
	public EdgeData getFixedEdges() {
		return fixedEdges;
	}

	/**
	 * Returns the solutions to this problem instance.
	 * 
	 * @return the solutions to this problem instance
	 */
	public List<TSPLibTour> getTours() {
		return tours;
	}

	/**
	 * Returns the number of clusters.
	 * @return the number of clusters
	 */
	public int getnClusters(){
		return nClusters;
	}

	/**
	 * Returns the clusters.
	 * @return the array of node sets
	 */
	public List<Integer>[] getClusters() {
		return clusters;
	}

	/**
	 * Returns the demands and depot nodes for vehicle routing problems; or
	 * {@code null} if this is not a vehicle routing problem instance.
	 * 
	 * @return the demands and depot nodes for vehicle routing problems; or
	 *         {@code null} if this is not a vehicle routing problem instance
	 */
	public VehicleRoutingTable getVehicleRoutingTable() {
		return vehicleRoutingTable;
	}

}
