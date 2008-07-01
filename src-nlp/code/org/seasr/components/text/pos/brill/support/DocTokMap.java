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

package org.seasr.components.text.pos.brill.support;

//==============
// Java Imports
//==============

import java.util.*;

//===============
// Other Imports
//===============

import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;

import gnu.trove.*;

/**
 * 
 * A support class for the Brill POSTagger.
 * 
 * @author D. Searsmith
 *
 * TODO: Unit Testing
 *
 */
public class DocTokMap {

	// ==============
	// Data Members
	// ==============

	private HashMap<String, TIntHashSet> _tokMap = null;

	private ArrayList<Annotation> _tokList = null;

	private HashSet<String> _tokHash = null;

	private HashMap<Annotation, Integer> _tokPosMap = null;

	// ================
	// Constructor(s)
	// ================

	public DocTokMap() {
		_tokMap = new HashMap<String, TIntHashSet>();
		_tokList = new ArrayList<Annotation>();
		_tokHash = new HashSet<String>();
		_tokPosMap = new HashMap<Annotation, Integer>();
	}

	public DocTokMap(Document doc) {
		_tokMap = new HashMap<String, TIntHashSet>();
		_tokList = new ArrayList<Annotation>();
		_tokHash = new HashSet<String>();
		_tokPosMap = new HashMap<Annotation, Integer>();

		TreeSet<Annotation> tset = new TreeSet<Annotation>(new Annots_Comparator());

		AnnotationSet annots = doc.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);
		for (Iterator<Annotation> iter = annots.iterator(); iter.hasNext();) {
			Annotation tok = iter.next();
			if (tok.getType().equals(AnnotationConstants.TOKEN_ANNOT_TYPE)) {
				tset.add(tok);
			}
		}

		for (Iterator<Annotation> iter = tset.iterator(); iter.hasNext();) {
			Annotation o = iter.next();
			_tokList.add(o);
			String img = (String) o.getContent(doc);
			TIntHashSet lst = _tokMap.get(img);
			if (lst == null) {
				lst = new TIntHashSet();
				lst.add(_tokList.size() - 1);
				_tokMap.put(img, lst);
			} else {
				lst.add(_tokList.size() - 1);
			}
			_tokHash.add(img);
			_tokPosMap.put(o, new Integer(_tokList.size() - 1));
		}

	}

	// ================
	// Public Methods
	// ================

	public int[] getPositions(String img) {
		TIntHashSet iob = _tokMap.get(img);
		if (iob == null) {
			return null;
		} else {
			return iob.toArray();
		}
	}

	public int[] getPositions(Annotation annot, Document doc) {
		String img = (String) ((Annotation) annot).getContent(doc);
		TIntHashSet iob = _tokMap.get(img);
		if (iob == null) {
			return null;
		} else {
			return iob.toArray();
		}
	}

	public int getPosition(Annotation annot) {
		Integer iob = _tokPosMap.get(annot);
		if (iob == null) {
			return -1;
		} else {
			return iob.intValue();
		}
	}

	public Annotation getAnnotationAtPos(int pos) {
		if (_tokList.isEmpty()) {
			return null;
		}
		if (pos >= _tokList.size()) {
			return null;
		}
		return _tokList.get(pos);
	}

	public boolean isWord(String s) {
		return _tokHash.contains(s);
	}

	public int getListSize() {
		return _tokList.size();
	}

	// =================
	// Inner Class(es)
	// =================

	private class Annots_Comparator implements Comparator<Annotation> {

		public Annots_Comparator() {
		}

		// ======================
		// Interface: Comparator
		// ======================
		public int compare(Annotation o1, Annotation o2) {
			long s1 = o1.getStartNodeOffset();
			long s2 = o2.getStartNodeOffset();

			if (s1 > s2) {
				return 1;
			} else if (s1 < s2) {
				return -1;
			} else {
				long e1 = ((Annotation) o1).getEndNodeOffset();
				long e2 = ((Annotation) o2).getEndNodeOffset();
				if (e1 < e2) {
					return 1;
				} else if (e1 > e2) {
					return -1;
				} else {
					int i1 = ((Annotation) o1).getId();
					int i2 = ((Annotation) o2).getId();
					if (i1 > i2) {
						return 1;
					} else if (i1 < i2) {
						return -1;
					} else {
						return 0;
					}
				}
			}
		}

		public boolean equals(Object o) {
			return this.equals(o);
		}
	}
}
