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

package org.seasr.components.text.datatype.pos;

//==============
// Java Imports
//==============
import java.io.*;
import java.util.*;

//===============
// Other Imports
//===============

/**
 * @author D. Searsmith
 */
public class PoSToken implements Serializable {
	// ==============
	// Data Members
	// ==============
	static final long serialVersionUID = 1L;

	private PoSTag m_posTag = null;

	private HashSet<TokenFlag> m_flags = new HashSet<TokenFlag>(10);

	private String m_image = null;

	private int m_frequency = 1;

	// list of tokens
	private ArrayList<String> m_originalForms = new ArrayList<String>();

	// ================
	// Constructor(s)
	// ================
	public PoSToken(String image) {
		m_image = image;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param String
	 *            image
	 * @param TokenFlag
	 *            flag
	 */
	public PoSToken(String image, TokenFlag flag) {
		m_image = image;
		m_flags.add(flag);
	}

	// ================
	// Public Methods
	// ================
	public void free() {
		m_posTag = null;
		m_flags = null;
		m_image = null;
		m_originalForms = null;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public Iterator<TokenFlag> getFlags() {
		return m_flags.iterator();
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public PoSTag getPoSTag() {
		return m_posTag;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param tag
	 */
	public void setPoSTag(PoSTag tag) {
		m_posTag = tag;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param tflag
	 * @return
	 */
	public boolean isFlagged(TokenFlag tflag) {
		return m_flags.contains(tflag);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param flag
	 */
	public void addFlag(TokenFlag flag) {
		if (!m_flags.contains(flag)) {
			m_flags.add(flag);
		}
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public String getImage() {
		return m_image;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param image
	 */
	public void setImage(String image) {
		m_image = image;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public int getFrequency() {
		return m_frequency;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param f
	 */
	public void setFrequency(int f) {
		m_frequency = f;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param s
	 */
	public void addOriginalForm(String s) {
		m_originalForms.add(s);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public ArrayList<String> getOriginalForms() {
		return m_originalForms;
	}
}
