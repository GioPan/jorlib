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
package org.jorlib.io.tspLibReader.fieldTypesAndFormats;

/**
 * Enumeration of the supported data types.
 * 
 * @author David Hadka
 */
public enum DataType {
	
	/**
	 * Data for a symmetric traveling salesman problem.
	 */
	TSP,
	
	/**
	 * Data for an asymmetric traveling salesman problem.
	 */
	ATSP,
	
	/**
	 * Data for a sequential ordering problem.
	 */
	SOP,
	
	/**
	 * Hamiltonian cycle problem data.
	 */
	HCP,
	
	/**
	 * Capacitated vehicle routing problem data.
	 */
	CVRP,
	
	/**
	 * A collection of tours.
	 */
	TOUR,
	/**
	 * Data for an asymmetric generalized traveling salesman problem.
	 */
	AGTSP,
	/**
	 * Data for a symmetric generalized traveling salesman problem.
	 */
	GTSP

}
