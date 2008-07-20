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
 * Document interface. A document has content, which is just a plain String. A
 * document also has a set of annotations that markup the content.
 * 
 * @author Xiaolei Li
 * @author D. Searsmith
 */
public interface Document extends FeatureBearer, Serializable {

	static final long serialVersionUID = 3L;

	static public String s_docID = "t2k_docid";

	public void setDocID(String s);

	public String getDocID();

	static public String s_docTitle = "t2k_title";

	public void setTitle(String t);

	public String getTitle();

	static public String s_date = "T2K_DATE";

	public void setDate(long t);

	public long getDate();

	/**
	 * Return the document's content as a single string.
	 */
	public String getContent();

	/**
	 * Set the document's content to this new string.
	 * 
	 * @param new_content
	 */
	public void setContent(String new_content);

	/**
	 * Make changes to the document's content. This doesn't work right now.
	 * 
	 * @param start
	 * @param end
	 * @param replacement_content
	 */
	public void edit(long start, long end, String replacement_content)
			throws Exception;

	/**
	 * Get default set of annotations. The default set is created if it doesn't
	 * exist yet.
	 */
	public AnnotationSet getAnnotations();

	/**
	 * Get a named set of annotations. Creates a new set if the requested one
	 * doesn't exist yet.
	 * 
	 * @param name
	 *            The name of the annotation set to retrieve.
	 */
	public AnnotationSet getAnnotations(String name);

	/**
	 * Return a map with the annotation sets.
	 */
	public Map<String, AnnotationSet> getNamedAnnotationSets();

	/**
	 * Remove the named annotation set.
	 * 
	 * @param name
	 *            The name of the annotation set to be removed.
	 */
	public void removeAnnotationSet(String name);

	/**
	 * Free this object for garbage collection.
	 */
	public void free();
	
	/**
	 * Get an auxiliary feature map for storing temporary data structures
	 * used during flow execution but not intended for meta-data storage.
	 */
	public Map<String, Object> getAuxMap();
}
