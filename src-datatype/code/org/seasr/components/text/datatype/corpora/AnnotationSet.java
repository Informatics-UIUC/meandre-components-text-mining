/**
 * University of Illinois/NCSA
 * Open Source License
 * 
 * Copyright (c) 2008, Board of Trustees-University of Illinois.  
 * All rights reserved.
 * 
 * Developed by: 
 * 
 * Automated Learning Group
 * National Center for Supercomputing Applications
 * http://www.seasr.org
 * 
 *  
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal with the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions: 
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimers. 
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimers in the 
 *    documentation and/or other materials provided with the distribution. 
 * 
 *  * Neither the names of Automated Learning Group, The National Center for
 *    Supercomputing Applications, or University of Illinois, nor the names of
 *    its contributors may be used to endorse or promote products derived from
 *    this Software without specific prior written permission. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * WITH THE SOFTWARE.
 */ 

package org.seasr.components.text.datatype.corpora;

//==============
// Java Imports
//==============

import java.io.*;
import java.util.*;

/**
 * AnnotationSet interface keeps a set of annotations for easy access.
 * 
 * @author Xiaolei Li
 * @author D. Searsmith
 */
public interface AnnotationSet extends Set<Annotation>, Serializable {

	//==============
	// Data Members
	//==============
	
	static final long serialVersionUID = 3L;

	
	/**
	 * Add a new annotation with existing nodes to this set. Return its ID.
	 */
	public int add(long start, long end, String type, FeatureMap features)
			throws Exception;

	/**
	 * Add a new annotation with an existing ID.
	 */
	public void add(int ID, long start, long end, String type,
			FeatureMap features) throws Exception;

	/**
	 * Add an existing annotation.
	 * 
	 * @return true if insertion is successful.
	 */
	public boolean add(Annotation o);

	/**
	 * Get an iterator for this set.
	 */
	public Iterator<Annotation> iterator();

	/**
	 * Return the size of this set.
	 */
	public int size();

	/**
	 * Remove an element from this set.
	 * 
	 * @return true if removal is successful.
	 */
	public boolean remove(Object o);

	/**
	 * Get an annotation by ID.
	 */
	// public Annotation get(Integer id);
	public Annotation get(int id);

	/**
	 * Return all annotations in this set.
	 */
	public AnnotationSet get();

	/**
	 * Return all annotations with this given type.
	 */
	public AnnotationSet get(String type);

	/**
	 * Return all annotations with all types in this given set.
	 */
	public AnnotationSet get(Set<String> types);

	/**
	 * Return all annotations with a given type and features.
	 */
	public AnnotationSet get(String type, FeatureMap features);

	/**
	 * Return all annotations with a given type and set of features.
	 */
	public AnnotationSet get(String type, Set<String> featureNames);

	/**
	 * Return all annotations by type, features, and offset.
	 */
	public AnnotationSet get(String type, FeatureMap features, long offset);

	/**
	 * Return all annotations with an offset that is greater than or equal to
	 * the given offset.
	 */
	public AnnotationSet get(long offset);

	/**
	 * Return all annotations that overlap with the given offsets.
	 */
	public AnnotationSet get(long offsetStart, long offsetEnd);

	/**
	 * Returns true if at least one annotation in this set covers a range that
	 * overlaps with the given range.
	 */
	public boolean coversPartialRange(long offsetStart, long offsetEnd);

	/**
	 * Return all annotations with the given type and that overlap with the
	 * given offsets.
	 */
	public AnnotationSet get(String type, long offsetStart, long offsetEnd);

	/**
	 * Return all annotations that are exclusively inside the given offsets. In
	 * other words, they have to start and end within the given offsets. This is
	 * more restrictive than get(Long offsetStart, Long offsetEnd).
	 */
	public AnnotationSet getContained(long offsetStart, long offsetEnd);

	/**
	 * Return all annotations that have exact offsets as the given offsets.
	 */
	public AnnotationSet getExact(long offsetStart, long offsetEnd);

	/**
	 * Return all annotations that have exact starting offsets as the given
	 * offset.
	 */
	public AnnotationSet getExactStart(long offsetStart);

	/**
	 * Return all annotations in this set in an ArrayList as sorted by their
	 * starting node's offset in ascending order. If two annotations have the
	 * same starting node, the order is arbitrary.
	 */
	public ArrayList<Annotation> getAllSortedAsArrayList();

	/**
	 * Get the name of this set.
	 */
	public String getName();

	/**
	 * Get a set of String objects that list all the annotation types in this
	 * set.
	 */
	public Set<String> getAllTypes();

	/**
	 * Get the document this set is attached to.
	 */
	public Document getDocument();
}
