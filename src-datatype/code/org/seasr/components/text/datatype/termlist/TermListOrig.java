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

package org.seasr.components.text.datatype.termlist;

//==============
// Java Imports
//==============
import java.io.Serializable;
import java.util.*;
import java.util.logging.*;

import org.seasr.components.text.datatype.pos.PoSToken;
import org.seasr.components.text.datatype.pos.TokenFlag;

//===============
// Other Imports
//===============

/**
 * @author D. Searsmith
 */
public class TermListOrig implements TermList, Serializable {

	// ==============
	// Data Members
	// ==============

	private static final long serialVersionUID = 1L;

	private TreeSet<PoSToken> m_terms = new TreeSet<PoSToken>(new TermListElementComparator());

	private String m_docID = "";

	private String m_docTitle = "";

	private String m_source = "";

	private long m_date = -1;

	private Hashtable<String, String> m_properties = null;

	private static Logger _logger = Logger.getLogger("TermListOrig");

	// ================
	// Constructor(s)
	// ================
	public TermListOrig() {
	}

	// ================
	// Public Methods
	// ================
	public void removeProperty(String key) {
		if (m_properties == null) {
			return;
		}
		m_properties.remove(key);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param ht
	 */
	public void setProperties(Map<String, String> ht) {
		m_properties = new Hashtable<String, String>(ht);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public Map<String, String> getProperties() {
		return new Hashtable<String, String>(m_properties);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		if (m_properties == null) {
			return null;
		}
		return (String) m_properties.get(key);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param s
	 */
	public void setSource(String s) {
		m_source = s;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public String getSource() {
		return m_source;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param quant
	 */
	public void truncateTerms(int quant) {
		TreeSet<PoSToken> ts = new TreeSet<PoSToken>(new TermListElementComparator());
		Iterator<PoSToken> it = getTerms();
		int i = 0;
		while ((it.hasNext()) && (i < quant)) {
			ts.add(it.next());
			i++;
		}
		m_terms = ts;
	}

	/**
	 * put your documentation comment here
	 */
	public void free() {
		m_terms = null;
		m_docID = null;
		m_docTitle = null;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param post
	 */
	public void addTerm(PoSToken post) {
		m_terms.add(post);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public Iterator<PoSToken> getTerms() {
		return m_terms.iterator();
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param tstr
	 * @return
	 */
	public boolean findTermByImage(String tstr) {
		for (Iterator<PoSToken> it = getTerms(); it.hasNext();) {
			PoSToken post = it.next();
			if (post.getImage().equals(tstr)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param tstr
	 * @return
	 */
	public PoSToken getTermByImage(String tstr) {
		for (Iterator<PoSToken> it = getTerms(); it.hasNext();) {
			PoSToken post = it.next();
			if (post.getImage().equals(tstr)) {
				return post;
			}
		}
		return null;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public int getSize() {
		return m_terms.size();
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param s
	 */
	public void setDocID(String s) {
		m_docID = s;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public String getDocID() {
		return m_docID;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param s
	 */
	public void setDate(long s) {
		m_date = s;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public long getDate() {
		return m_date;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param t
	 */
	public void setTitle(String t) {
		m_docTitle = t;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public String getTitle() {
		return m_docTitle;
	}

	/**
	 * put your documentation comment here
	 */
	public void print() {
		_logger.info("\n\nTerms for document titled \"" + getTitle()
				+ "\" located " + getDocID() + "\n");
		if (getProperties() != null) {
			_logger.info("Properties: " + getProperties() + "\n");
		}
		for (Iterator<PoSToken> it = getTerms(); it.hasNext();) {
			PoSToken tok = (PoSToken) it.next();
			_logger.info(tok.getImage() + " " + tok.getFrequency());
			if (tok.isFlagged(TokenFlag.NORMALIZED)) {
				StringBuffer flagBuff = new StringBuffer();
				flagBuff.append(" --");
				for (Iterator<String> it2 = tok.getOriginalForms().iterator(); it2
						.hasNext();) {
					flagBuff.append(" " + (String) it2.next());
				}
				_logger.info(flagBuff.toString());
			}
			StringBuffer tagBuff = new StringBuffer();
			tagBuff.append(" TAGS:");
			for (Iterator<TokenFlag> it2 = tok.getFlags(); it2.hasNext();) {
				tagBuff.append(" " + it2.next());
			}
			_logger.info(tagBuff.toString());
		}
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public boolean validate() {
		return true;
	}
}
