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
import java.util.*;
import java.util.logging.*;

import org.seasr.components.text.datatype.pos.PoSToken;


/**
 * @author D. Searsmith
 */
public class TermListLite implements TermList, java.io.Serializable {

	// ==============
	// Data Members
	// ==============

	private static final long serialVersionUID = 1L;

	private ArrayList<String> m_terms = new ArrayList<String>();

	private ArrayList<Integer> m_freqs = new ArrayList<Integer>();

	private ArrayList<ArrayList<String>> m_oforms = new ArrayList<ArrayList<String>>();

	private String m_docID = "";

	private String m_docTitle = "";

	private String m_source = "";

	private Hashtable<String, String> m_properties = null;

	private long m_date = -1;

	private HashMap<String, Integer> _termPos = new HashMap<String, Integer>();

	private static Logger _logger = Logger.getLogger("TermListLite");

	// ================
	// Constructor(s)
	// ================
	public TermListLite() {
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param TermListOrig
	 *            
	 */
	public TermListLite(TermListOrig tl) {
		setTitle(tl.getTitle());
		setDocID(tl.getDocID());
		setDate(tl.getDate());
		setProperties(tl.getProperties());
		for (Iterator<PoSToken> it = tl.getTerms(); it.hasNext();) {
			addTerm((PoSToken) it.next());
		}
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param StringBuffer
	 *            
	 */
	public TermListLite(StringBuffer xml) {
		StringBuffer title = new StringBuffer();
		StringBuffer docid = new StringBuffer();
		StringBuffer image = new StringBuffer();
		StringBuffer freq = new StringBuffer();
		StringBuffer oform = new StringBuffer();
		StringBuffer date = new StringBuffer();
		StringBuffer key = new StringBuffer();
		StringBuffer value = new StringBuffer();
		char cval = ' ';
		boolean appendToTitle = false;
		boolean appendToDocID = false;
		boolean appendToImage = false;
		boolean appendToFreq = false;
		boolean appendToOForm = false;
		boolean appendToDate = false;
		boolean appendToKey = false;
		boolean appendToValue = false;
		int i = 0;
		ArrayList<String> oforms = null;
		try {
			while (true) {
				cval = xml.charAt(i++);
				if (cval == '<') {
					cval = xml.charAt(i++);
					if (cval == '/') {
						cval = xml.charAt(i++);
						if (cval == 'D') {
							return;
						} else if (cval == 'T') {
							cval = xml.charAt(i++);
							if (cval == 'I') {
								i += 4;
								appendToTitle = false;
								m_docTitle = title.toString();
							} else if (cval == 'E') {
								i += 3;
								m_oforms.add(oforms);
							}
						} else if (cval == 'S') {
							i += 3;
							appendToDocID = false;
							m_docID = docid.toString();
						} else if (cval == 'P') {
							i += 4;
						} else if (cval == 'K') {
							i += 3;
							appendToKey = false;
						} else if (cval == 'V') {
							i += 3;
							appendToValue = false;
							if (m_properties == null) {
								m_properties = new Hashtable<String, String>();
							}
							m_properties.put(key.toString(), value.toString());
							key = new StringBuffer();
							value = new StringBuffer();
						} else if (cval == 'W') {
							i += 4;
							appendToDate = false;
							m_date = Long.parseLong(date.toString());
						} else if (cval == 'I') {
							i += 3;
							appendToImage = false;
							m_terms.add(image.toString());
							_termPos.put(image.toString(), new Integer(m_terms
									.size() - 1));
							image = new StringBuffer();
						} else if (cval == 'F') {
							i += 4;
							appendToFreq = false;
							m_freqs.add(new Integer(freq.toString()));
							freq = new StringBuffer();
						} else if (cval == 'O') {
							i += 5;
							appendToOForm = false;
							oforms.add(oform.toString());
							oform = new StringBuffer();
						}
					} else if (cval == 'I') {
						i += 3;
						cval = xml.charAt(i++);
						appendToImage = true;
					} else if (cval == 'W') {
						i += 4;
						cval = xml.charAt(i++);
						appendToDate = true;
					} else if (cval == 'S') {
						i += 3;
						cval = xml.charAt(i++);
						appendToDocID = true;
					} else if (cval == 'F') {
						i += 4;
						cval = xml.charAt(i++);
						appendToFreq = true;
					} else if (cval == 'O') {
						i += 5;
						cval = xml.charAt(i++);
						appendToOForm = true;
					} else if (cval == 'P') {
						i += 4;
					} else if (cval == 'K') {
						i += 3;
						cval = xml.charAt(i++);
						appendToKey = true;
					} else if (cval == 'V') {
						i += 3;
						cval = xml.charAt(i++);
						appendToValue = true;
					} else if (cval == 'T') {
						cval = xml.charAt(i++);
						if (cval == 'I') {
							i += 4;
							cval = xml.charAt(i++);
							appendToTitle = true;
						}
						if (cval == 'E') {
							i += 3;
							oforms = new ArrayList<String>();
						}
					} else if (cval == 'D') {
						i += 3;
					} else {
						cval = '<';
						i--;
					}
				}
				if (appendToDate) {
					date.append(cval);
				}
				if (appendToTitle) {
					title.append(cval);
				}
				if (appendToDocID) {
					docid.append(cval);
				}
				if (appendToImage) {
					image.append(cval);
				}
				if (appendToOForm) {
					oform.append(cval);
				}
				if (appendToFreq) {
					freq.append(cval);
				}
				if (appendToKey) {
					key.append(cval);
				}
				if (appendToValue) {
					value.append(cval);
				}
			}
		} catch (Exception e) {
			_logger.severe("Error setting TermListLite from XML, default settings applied");
		}
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
		if (m_properties == null) {
			m_properties = new Hashtable<String, String>();
		}
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
		return m_properties.get(key);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param quant
	 */
	public void truncateTerms(int quant) {
		ArrayList<String> terms = new ArrayList<String>();
		ArrayList<Integer> freqs = new ArrayList<Integer>();
		ArrayList<ArrayList<String>> oforms = new ArrayList<ArrayList<String>>();
		_termPos.clear();
		int i = 0;
		int size = m_terms.size();
		while ((i < size) && (i < quant)) {
			terms.add(m_terms.get(i));
			_termPos.put(m_terms.get(i), new Integer(terms.size() - 1));
			freqs.add(m_freqs.get(i));
			oforms.add(m_oforms.get(i));
			i++;
		}
		m_terms = terms;
		m_freqs = freqs;
		m_oforms = oforms;
	}

	/**
	 * put your documentation comment here
	 */
	public void free() {
		m_terms.clear();
		m_freqs.clear();
		m_oforms.clear();
		m_docID = "";
		m_docTitle = "";
		_termPos.clear();
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param image
	 * @param freq
	 * @param oforms
	 */
	public void addTerm(String image, int freq, ArrayList<String> oforms) {
		m_terms.add(image);
		_termPos.put(image, new Integer(m_terms.size() - 1));
		m_freqs.add(new Integer(freq));
		m_oforms.add(oforms);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param pos
	 */
	public void addTerm(PoSToken pos) {
		m_terms.add(pos.getImage());
		_termPos.put(pos.getImage(), new Integer(m_terms.size() - 1));
		m_freqs.add(new Integer(pos.getFrequency()));
		m_oforms.add(pos.getOriginalForms());
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public Iterator<String> getTerms() {
		return m_terms.iterator();
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public Object[] getTermsAsArray() {
		return m_terms.toArray();
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public Iterator<Integer> getFrequencies() {
		return m_freqs.iterator();
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public Object[] getFrequenciesAsArray() {
		return m_freqs.toArray();
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param tstr
	 * @return
	 */
	public boolean findTermByImage(String tstr) {
		return _termPos.keySet().contains(tstr);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param tstr
	 * @return
	 */
	public int getTermFreqByImage(String tstr) {
		Integer iob = (Integer) _termPos.get(tstr);
		if (iob == null) {
			return -1;
		} else {
			return ((Integer) m_freqs.get(iob.intValue())).intValue();
		}
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param ind
	 * @return
	 */
	public int getTermFreqByIndex(int ind) {
		if ((ind >= 0) && (ind < m_freqs.size())) {
			return ((Integer) m_freqs.get(ind)).intValue();
		}
		return -1;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param tstr
	 * @return
	 */
	public ArrayList<String> getTermOrigFormsByImage(String tstr) {
		Integer iob = (Integer) _termPos.get(tstr);
		if (iob == null) {
			return null;
		} else {
			return m_oforms.get(iob.intValue());
		}
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param ind
	 * @return
	 */
	public ArrayList<String> getTermOrigFormsByIndex(int ind) {
		if ((ind >= 0) && (ind < m_oforms.size())) {
			return m_oforms.get(ind);
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
	 * @return
	 */
	public StringBuffer toXML() {
		StringBuffer ret = new StringBuffer();
		ret.append("<DOC>");
		ret.append("<SRC>" + this.getDocID() + "</SRC>");
		ret.append("<WHEN>" + Long.toString(getDate()) + "</WHEN>");
		ret.append("<TITLE>" + this.getTitle() + "</TITLE>");
		// properties
		if (m_properties != null) {
			if (m_properties.size() > 0) {
				ret.append("<PROP>");
				for (Enumeration<String> enum1 = m_properties.keys(); enum1
						.hasMoreElements();) {
					String key = enum1.nextElement();
					String value = m_properties.get(key).toString();
					ret.append("<KEY>" + key + "</KEY>");
					ret.append("<VAL>" + value + "</VAL>");
				}
				ret.append("</PROP>");
			}
		}
		int sz = m_terms.size();
		if (sz != m_freqs.size()) {
			_logger.warning("DocID: " + this.getDocID() + " num terms: "
					+ m_terms.size() + " num freqs: " + m_freqs.size());
			if (sz > m_freqs.size()) {
				sz = m_freqs.size();
			}
		}
		if (sz != m_oforms.size()) {
			_logger.warning("DocID: " + this.getDocID() + " num terms: "
					+ m_terms.size() + " num oforms: " + m_oforms.size());
			if (sz > m_oforms.size()) {
				sz = m_oforms.size();
			}
		}
		for (int x = 0, xn = sz; x < xn; x++) {
			ret.append("<TERM><IMG>" + (String) m_terms.get(x) + "</IMG><FREQ>"
					+ m_freqs.get(x) + "</FREQ>");
			ArrayList<String> oform = m_oforms.get(x);
			for (int y = 0, yn = oform.size(); y < yn; y++) {
				ret.append("<OFORM>" + (String) oform.get(y) + "</OFORM>");
			}
			ret.append("</TERM>");
		}
		ret.append("</DOC>");
		return ret;
	}

	/**
	 * put your documentation comment here
	 */
	public void print() {
		_logger.info(toXML().toString());
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public boolean validate() {
		int sz = m_terms.size();
		if (sz != m_freqs.size()) {
			_logger.warning("DocID: " + this.getDocID() + " num terms: "
					+ m_terms.size() + " num freqs: " + m_freqs.size());
			return false;
		}
		if (sz != m_oforms.size()) {
			_logger.warning("DocID: " + this.getDocID() + " num terms: "
					+ m_terms.size() + " num oforms: " + m_oforms.size());
			return false;
		}
		return true;
	}
}
