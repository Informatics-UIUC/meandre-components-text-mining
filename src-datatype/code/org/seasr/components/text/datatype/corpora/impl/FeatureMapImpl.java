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

import org.seasr.components.text.datatype.corpora.FeatureMap;

/**
 * An attribute-value mapping. Represents the content of an annotation.
 * 
 * @author Xiaolei Li
 * @author D. Searsmith
 */
public class FeatureMapImpl extends HashMap<String, Object> implements FeatureMap {

	//==============
	// Data Members
	//==============
	
	static final long serialVersionUID = 3L;

	//================
	// Public Methods
	//================
	
	/**
	 * Check if <b>this</b> FeatureMap subsumes fmap, i.e., fmap's keys are a
	 * subset of this FeatureMap's keys.
	 * 
	 * @return True if this FeatureMap subsumes fmap.
	 */
	public boolean subsumes(FeatureMap fmap) {
		// if fmap's null, then it is a subset of anything
		if (fmap == null)
			return true;

		// cannot subsume fmap if fmap has more keys
		if (this.size() < fmap.size())
			return false;

		Object[] keys = fmap.keySet().toArray();

		// check each key in fmap and see if it's included in this
		// FeatureMap. if any one key is not included, then subsumption
		// is false.
		for (int i = 0; i < keys.length; i++) {
			if (!this.containsKey(keys[i]))
				return false;
		}

		return true;
	}

	/**
	 * Check if <b>this</b> FeatureMap subsumes fmap on the given set of names.
	 * 
	 * @return True if this FeatureMap subsumes fmap.
	 */
	public boolean subsumes(FeatureMap fmap, Set<String> nameset) {
		// if fmap's null, then it is a subset of anything
		if (fmap == null)
			return true;

		// if no name set is given, this becomes the regular subsumes
		// check.
		if (nameset == null)
			return this.subsumes(fmap);

		// cannot subsume fmap if the nameset has more keys
		if (this.size() < nameset.size())
			return false;

		Object[] keys = nameset.toArray();

		for (int i = 0; i < nameset.size(); i++) {

			// if fmap doesn't contain this feature name, then forget
			// it. it doesn't matter if this annotation has it or not.
			if (!fmap.containsKey(keys[i]))
				continue;

			if (!this.containsKey(keys[i]))
				return false;
		}

		return true;
	}
}
