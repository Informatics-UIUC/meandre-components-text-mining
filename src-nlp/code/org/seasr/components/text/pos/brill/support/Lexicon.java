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

import java.io.*;
import java.util.*;
import java.io.Serializable;
import java.util.logging.*;

//===============
// Other Imports
//===============

import org.seasr.components.text.datatype.pos.PoSTag;

/**
 * This class reads a lexicon text file and populates a 
 * lexicon data structure which is a map of terms and part of speech
 * tags.  The lexicon file has one term entry per line.  A term entry
 * consists of the term itself followed by a list of one or more
 * part of speech tags.  Part of speech tags are in PennTreeBank format
 * and are represented internally by instances of the PoSTag class.
 * 
 * This lexicon file is used for the Brill POS tagger.
 * 
 * @author D. Searsmith
 * 
 * TODO: Unit Testing
 * 
 */
public class Lexicon implements Serializable {

	// ==============
	// Data Members
	// ==============

	private static final long serialVersionUID = 1L;

	static final int s_OK = 0;

	static final int s_EOF = 1;

	private String m_term = null;

	private PoSTag[] m_tags = null;

	private HashMap<String, PoSTag[]> m_ht = new HashMap<String, PoSTag[]>(125000);

	private long m_linecnt = 0;

	private boolean m_verbose = false;

	private static Logger _logger = Logger.getLogger("Lexicon");

	// ================
	// Constructor(s)
	// ================
	public Lexicon(String filename, boolean verbose) {
		m_verbose = verbose;
		buildLexicon(filename);
	}

	// ================
	// Public Methods
	// ================
	
	/**
	 * Number of terms in the lexicon.
	 */
	public int size() {
		return m_ht.size();
	}

	/**
	 * Find the tags for the given term in the 
	 * map.
	 * 
	 * @param key Term value as String.
	 * @return An array of PoSTag objects.
	 */
	public PoSTag[] getTagsForTerm(String key) {
		PoSTag[] ptarr = (PoSTag[]) m_ht.get(key);
		if (ptarr == null) {
			ptarr = new PoSTag[0];
		}
		return ptarr;
	}

	/**
	 * Is this term in the lexicon.
	 * 
	 * @param key Term to look for.
	 * @return True/False as to whether term is in the lexicon.
	 */
	public boolean isInLexicon(String key) {
		return (m_ht.get(key) != null);
	}

	/**
	 * Print the lexicon's map.
	 */
	public void print() {
		System.out.println(m_ht);
	}

	// =================
	// Private Methods
	// =================
	
	
	final private boolean contains(Vector<PoSTag> fv, PoSTag o) {
		for (int x = 0, y = fv.size(); x < y; x++) {
			if (fv.elementAt(x) == o) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Parse one line of the Lexicon file for term and tags.
	 * 
	 * @param reader A reader for the Lexicon file being parsed.
	 * @return Return s_OK unless end of file when we will returns s_EOF.
	 * @exception IOException File reading error.
	 */
	final private int nextTokens(BufferedReader reader) throws IOException {
		Vector<PoSTag> tags = new Vector<PoSTag>();
		String line = reader.readLine();
		m_linecnt++;
		if (line == null) {
			m_term = null;
			m_tags = null;
			return s_EOF;
		}
		StringTokenizer tok = new StringTokenizer(line, " \t\r\n|");
		m_term = tok.nextToken();
		while (tok.hasMoreTokens()) {
			String tag = tok.nextToken();
			if (PoSTag.isPoSTag(tag)) {
				if (contains(tags, PoSTag.getPoSTag(tag))) {
					if (m_verbose) {
						_logger.info("Tag: " + tag
								+ " already included for this term: " + m_term);
					}
				} else {
					tags.addElement(PoSTag.getPoSTag(tag));
				}
			} else {
				if (m_verbose) {
					_logger.info("Line " + m_linecnt + " unknown tag: "
							+ tag);
				}
			}
		}
		if (tags.size() > 0) {
			m_tags = new PoSTag[tags.size()];
			for (int x = 0, y = tags.size(); x < y; x++) {
				m_tags[x] = (PoSTag) tags.elementAt(x);
			}
		} else {
			m_tags = null;
		}
		return s_OK;
	}

	/**
	 * Reads a lexicon text file line by line.  Each line is passed to
	 * "nextTokens" where the term and tags are parsed out and set into
	 * member variables.  The term and tags are then written into the hash
	 * map which serves as the core datastruct for the Lexicon class.
	 * 
	 * @param filename Name/path of the lexicon file to parse.
	 */
	final private void buildLexicon(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			while (nextTokens(reader) != s_EOF) {
				if (m_tags != null) {
					m_ht.put(m_term, m_tags);
				}
			}
		} catch (Exception e) {
			_logger.severe("ERROR in Lexicon building process: " + e);
			e.printStackTrace();
		}
	}
}
