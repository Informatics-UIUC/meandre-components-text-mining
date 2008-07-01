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

package org.seasr.components.text.datatype.termmap;

//==============
// Java Imports
//==============
import java.util.*;
import java.io.*;

/**
 * @author D. Searsmith
 * @author Bei Yu
 */
public class TermMap extends HashMap<String, TermMap.TermData> {
	
	// ==============
	// Data Members
	// ==============
	
	private static final long serialVersionUID = 1L;

	public static String s_RES_KEY = "term_map_key";

	/**
	 * put your documentation comment here
	 * 
	 * @param col
	 * @param image
	 * @param ofs
	 */
	public void addTermData(String col, String image, ArrayList<String> ofs) {
		TermData td = (TermData) get(col);
		if (td == null) {
			put(col, new TermData(col, image, ofs));
		} else {
			td.addOrigForms(ofs);
		}
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param r
	 * @return
	 */
	public TermData getTermData(String r) {
		if (containsKey(r)) {
			return (TermData) get(r);
		}
		return null;
	}

	// =================
	// Inner Class(es)
	// =================
	public class TermData implements Serializable {

		// ==============
		// Data Members
		// ==============

		private static final long serialVersionUID = 1L;

		private ArrayList<String> _origForms = null;

		private String _image = "";

		private String _id = "";

		// ================
		// Constructor(s)
		// ================
		public TermData() {
		}

		/**
		 * put your documentation comment here
		 * 
		 * @param int
		 *            id
		 * @param String
		 *            img
		 * @param ArrayList
		 *            ofs
		 */
		public TermData(String id, String img, ArrayList<String> ofs) {
			setImage(img);
			setID(id);
			setOrigForms(ofs);
		}

		/**
		 * put your documentation comment here
		 * 
		 * @param s
		 */
		public void setOrigForms(ArrayList<String> s) {
			_origForms = s;
		}

		/**
		 * put your documentation comment here
		 * 
		 * @return
		 */
		public ArrayList<String> getOrigForms() {
			return _origForms;
		}

		/**
		 * put your documentation comment here
		 * 
		 * @param list
		 */
		public void addOrigForms(ArrayList<String> list) {
			// bei added 09-13-2004 to avoid null pointer exception from addAll.
			if (list == null)
				list = new ArrayList<String>();
			// //
			if (_origForms == null) {
				_origForms.addAll(list);
				return;
			}
			for (int i = 0, n = list.size(); i < n; i++) {
				String ofrm = list.get(i);
				if (!_origForms.contains(ofrm)) {
					_origForms.add(ofrm);
				}
			}
		}

		/**
		 * put your documentation comment here
		 * 
		 * @param s
		 */
		public void setImage(String s) {
			_image = s;
		}

		/**
		 * put your documentation comment here
		 * 
		 * @return
		 */
		public String getImage() {
			return _image;
		}

		/**
		 * put your documentation comment here
		 * 
		 * @param s
		 */
		public void setID(String s) {
			_id = s;
		}

		/**
		 * put your documentation comment here
		 * 
		 * @return
		 */
		public String getID() {
			return _id;
		}
	}
}
