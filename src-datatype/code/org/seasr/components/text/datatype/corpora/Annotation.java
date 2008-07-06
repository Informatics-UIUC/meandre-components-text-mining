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

//===============
// Other Imports
//===============

/**
 * Annotation interface. A single annotation has 2 nodes, start and end, that
 * point to positions or "offsets" in the document that this annotation
 * describes. An annotation also contains a type and a set of features.
 * 
 * @author Xiaolei Li
 * @author D. Searsmith
 */
public interface Annotation extends FeatureBearer, Serializable, FeatureEncodeDecode<Annotation> {

	// ==============
	// Data Members
	// ==============
	
	/**
	 * Return the substring of the subject of analysis to which
	 * this annotation maps.
	 * 
	 * @param sofa Document object that is the subject of analysis 
	 * for this annotation. 
	 */
	public String getContent(Document sofa);
	

	static final long serialVersionUID = 3L;

	/**
	 * Return the type of annotation, which is just a string.
	 * 
	 * @return
	 */
	public String getType();

	/**
	 * Return the Id of annotation.
	 * 
	 * @return
	 */
	public int getId();

	/**
	 * Return the starting node of the annotation.
	 * 
	 * @return
	 */
	public long getStartNodeOffset();

	/**
	 * Return the ending node of the annotation.
	 * 
	 * @return
	 */
	public long getEndNodeOffset();

	/**
	 * Check if this annotation is compatible with another. Compatible means
	 * they're co-extensive and share the same FeatureMap.
	 * 
	 * @return
	 */
	public boolean isCompatible(Annotation annot);

	/**
	 * Check if this annotation is compatible with another annotation on the
	 * given set of features.
	 * 
	 * @return
	 */
	public boolean isCompatible(Annotation annot, Set<String> featureNamesSet);

	/**
	 * Check if this annotation is partially compatible with another. Partially
	 * compatible means they overlap and the FeatureMap of this annotation is a
	 * subset of annot's FeatureMap.
	 * 
	 * @return
	 */
	public boolean isPartiallyCompatible(Annotation annot);

	/**
	 * Check if this annotation is partially compatible with another given a set
	 * of features.
	 * 
	 * @return
	 */
	public boolean isPartiallyCompatible(Annotation annot, Set<String> featureNamesSet);

	/**
	 * Two annotations are co-extensive if their offsets are the same.
	 * 
	 * @return
	 */
	public boolean coextensive(Annotation annot);

	/**
	 * Check if this annotation overlaps with another.
	 * 
	 * @return
	 */
	public boolean overlaps(Annotation annot);
}
