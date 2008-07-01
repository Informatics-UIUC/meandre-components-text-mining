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

package org.seasr.components.text.datatype.corpora.impl;

//==============
// Java Imports
//==============

import java.util.*;

//===============
// Other Imports
//===============

import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.FeatureBearer;

/**
 * Document
 * 
 * @author Xiaolei Li
 * @author D. Searsmith
 */
public class DocumentImpl extends AbstractFeatureBearer implements Document,
		FeatureBearer {

	static final long serialVersionUID = 3L;

	// ==============
	// Data Members
	// ==============

	/**
	 * The content of this document.
	 */
	private String content;

	/**
	 * The next node Id to use for this document.
	 */
	private int nextNodeId;

	/**
	 * The next annotation Id to use for this document.
	 */
	private int nextAnnotationId;

	/**
	 * The default annotation set for this document.
	 */
	private AnnotationSetImpl defaultAnnots;

	/**
	 * Named annotation sets for this document.
	 */
	private HashMap<String, AnnotationSet> namedAnnots;

	// ==============
	// Constructors
	// ==============

	/**
	 * Construction
	 */
	public DocumentImpl() {
		nextNodeId = -1;
		nextAnnotationId = -1;
		defaultAnnots = null;
		namedAnnots = null;
		this.setFeatures(new FeatureMapImpl());
	}

	public void free() {
		if (namedAnnots != null) {
			namedAnnots.clear();
			namedAnnots = null;
		}
		if (defaultAnnots != null) {
			defaultAnnots.clear();
			defaultAnnots = null;
		}
		content = null;
	}

	public void setDocID(String s) {
		getFeatures().put(s_docID, s);
	}

	public String getDocID() {
		return (String) getFeatures().get(s_docID);
	}

	public void setTitle(String t) {
		getFeatures().put(s_docTitle, t);
	}

	public String getTitle() {
		return (String) getFeatures().get(s_docTitle);
	}

	public void setDate(long t) {
		getFeatures().put(s_date, Long.toString(t));
	}

	public long getDate() {
		long dval = -1;
		try {
			dval = Long.parseLong((String) getFeatures().get(s_date));
		} catch (Exception e) {
			return -1;
		}
		return dval;
	}

	/**
	 * Return the document's content as a single string.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Set the document's content to this new string.
	 * 
	 * @param new_content
	 */
	public void setContent(String new_content) {
		this.content = new String(new_content);
	}

	/**
	 * Return the document's next unused Node Id.
	 */
	public int getNextNodeId() {
		nextNodeId++;
		return nextNodeId;
	}

	/**
	 * Return the document's next unused Annotation Id.
	 */
	public int getNextAnnotationId() {
		nextAnnotationId++;
		return nextAnnotationId;
	}

	/**
	 * Check if the given start and end offsets are within the document's
	 * content range.
	 */
	public boolean isValidOffsets(long start, long end) {
		// if the end is before the start, invalid
		if (end < start)
			return false;

		// if the end or start is longer than the actual content,
		// invalid
		if (end > content.length() || start > content.length())
			return false;

		return true;
	}

	/**
	 * Make changes to the document's content.
	 * 
	 * @param start
	 * @param end
	 * @param replacement_content
	 */
	public void edit(long start, long end, String replacement_content)
			throws Exception {
		// do nothing
	}

	/**
	 * Get default set of annotations. The default set is created if it doesn't
	 * exist yet.
	 */
	public AnnotationSet getAnnotations() {
		if (defaultAnnots == null)
			defaultAnnots = new AnnotationSetImpl(this);

		return defaultAnnots;
	}

	/**
	 * Get a named set of annotations. Creates a new set if the requested one
	 * doesn't exist yet.
	 * 
	 * @param name
	 *            The name of the annotation set to retrieve.
	 */
	public AnnotationSet getAnnotations(String name) {
		// no name given, return the default one.
		if (name == null)
			return getAnnotations();

		// no named annotations yet, make a set
		if (namedAnnots == null)
			namedAnnots = new HashMap<String, AnnotationSet>();

		AnnotationSet aset = (AnnotationSet) namedAnnots.get(name);

		// no such annotation set by its name, create one
		if (aset == null) {
			aset = new AnnotationSetImpl(this, name);
			namedAnnots.put(name, aset);
		}

		return aset;
	}

	/**
	 * Return a map with the annotation sets.
	 */
	public Map<String, AnnotationSet> getNamedAnnotationSets() {
		return namedAnnots;
	}

	/**
	 * Remove the named annotation set.
	 * 
	 * @param name
	 *            The name of the annotation set to be removed.
	 */
	public void removeAnnotationSet(String name) {
		// no name given, do nothing
		if (name == null)
			return;

		// no named annotations yet, do nothing
		if (namedAnnots == null)
			return;

		// remove the annotation set
		namedAnnots.remove(name);
	}
}
