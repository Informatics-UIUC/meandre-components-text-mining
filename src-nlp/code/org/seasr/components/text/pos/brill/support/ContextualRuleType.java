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
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.util.Hashtable;

//===============
// Other Imports
//===============
/**
 * This is a fairly slick pattern for coding enumerations in a type safe way.
 * This pattern also supports serialization/deserialization of the enumeration
 * instance.
 * 
 * @author D. Searsmith
 * 
 * TODO: Testing, Unit Testing
 * 
 */
public class ContextualRuleType implements Comparable<ContextualRuleType>, Serializable {

	private static final long serialVersionUID = 1L;

	// ==============
	// Data Members
	// ==============
	// Ordinal of next suit to be created
	private static int nextOrdinal = 0;

	private static Hashtable<String, ContextualRuleType> s_ht = new Hashtable<String, ContextualRuleType>();

	// Data Bearing
	public static final ContextualRuleType CR_CURWD = new ContextualRuleType(
			"CURWD");

	// public static final ContextualRuleType CR_ENDISWORD = new
	// ContextualRuleType("ENDISWORD");
	// public static final ContextualRuleType CR_ISEND = new
	// ContextualRuleType("ISEND");
	// public static final ContextualRuleType CR_ISSTART = new
	// ContextualRuleType("ISSTART");
	// public static final ContextualRuleType CR_ISSTARTOREND = new
	// ContextualRuleType("ISSTARTOREND");
	public static final ContextualRuleType CR_LBIGRAM = new ContextualRuleType(
			"LBIGRAM");

	public static final ContextualRuleType CR_NEXT1OR2OR3TAG = new ContextualRuleType(
			"NEXT1OR2OR3TAG");

	public static final ContextualRuleType CR_NEXT1OR2OR3WD = new ContextualRuleType(
			"NEXT1OR2OR3WD");

	public static final ContextualRuleType CR_NEXT1OR2TAG = new ContextualRuleType(
			"NEXT1OR2TAG");

	public static final ContextualRuleType CR_NEXT1OR2WD = new ContextualRuleType(
			"NEXT1OR2WD");

	public static final ContextualRuleType CR_NEXT2TAG = new ContextualRuleType(
			"NEXT2TAG");

	public static final ContextualRuleType CR_NEXT2WD = new ContextualRuleType(
			"NEXT2WD");

	public static final ContextualRuleType CR_NEXTBIGRAM = new ContextualRuleType(
			"NEXTBIGRAM");

	// public static final ContextualRuleType CR_NEXTISENDTAG = new
	// ContextualRuleType("NEXTISENDTAG");
	public static final ContextualRuleType CR_NEXTTAG = new ContextualRuleType(
			"NEXTTAG");

	public static final ContextualRuleType CR_NEXTWD = new ContextualRuleType(
			"NEXTWD");

	public static final ContextualRuleType CR_PREV1OR2OR3TAG = new ContextualRuleType(
			"PREV1OR2OR3TAG");

	public static final ContextualRuleType CR_PREV1OR2OR3WD = new ContextualRuleType(
			"PREV1OR2OR3WD");

	public static final ContextualRuleType CR_PREV1OR2TAG = new ContextualRuleType(
			"PREV1OR2TAG");

	public static final ContextualRuleType CR_PREV1OR2WD = new ContextualRuleType(
			"PREV1OR2WD");

	public static final ContextualRuleType CR_PREV2TAG = new ContextualRuleType(
			"PREV2TAG");

	public static final ContextualRuleType CR_PREV2WD = new ContextualRuleType(
			"PREV2WD");

	public static final ContextualRuleType CR_PREVBIGRAM = new ContextualRuleType(
			"PREVBIGRAM");

	// public static final ContextualRuleType CR_PREVISSTARTTAG = new
	// ContextualRuleType("PREVISSTARTTAG");
	public static final ContextualRuleType CR_PREVTAG = new ContextualRuleType(
			"PREVTAG");

	public static final ContextualRuleType CR_PREVWD = new ContextualRuleType(
			"PREVWD");

	public static final ContextualRuleType CR_RBIGRAM = new ContextualRuleType(
			"RBIGRAM");

	// public static final ContextualRuleType CR_STARTISWORD = new
	// ContextualRuleType("STARTISWORD");
	public static final ContextualRuleType CR_SURROUNDTAG = new ContextualRuleType(
			"SURROUNDTAG");

	// public static final ContextualRuleType CR_TAGAFTERSTART = new
	// ContextualRuleType("TAGAFTERSTART");
	// public static final ContextualRuleType CR_TAGBEFOREEND = new
	// ContextualRuleType("TAGBEFOREEND");
	public static final ContextualRuleType CR_WDAND2AFT = new ContextualRuleType(
			"WDAND2AFT");

	public static final ContextualRuleType CR_WDAND2BFR = new ContextualRuleType(
			"WDAND2BFR");

	public static final ContextualRuleType CR_WDAND2TAGAFT = new ContextualRuleType(
			"WDAND2TAGAFT");

	public static final ContextualRuleType CR_WDAND2TAGBFR = new ContextualRuleType(
			"WDAND2TAGBFR");

	public static final ContextualRuleType CR_WDNEXTTAG = new ContextualRuleType(
			"WDNEXTTAG");

	public static final ContextualRuleType CR_WDPREVTAG = new ContextualRuleType(
			"WDPREVTAG");

	// public static final ContextualRuleType CR_WDWITHIN2FROMEND = new
	// ContextualRuleType("WDWITHIN2FROMEND");
	// public static final ContextualRuleType CR_WDWITHIN2FROMSTART = new
	// ContextualRuleType("WDWITHIN2FROMSTART");
	// public static final ContextualRuleType CR_WITHIN2FROMEND = new
	// ContextualRuleType("WITHIN2FROMEND");
	// public static final ContextualRuleType CR_WITHIN2FROMSTART = new
	// ContextualRuleType("WITHIN2FROMSTART");
	// public static final ContextualRuleType CR_WITHIN3FROMEND = new
	// ContextualRuleType("WITHIN3FROMEND");
	// public static final ContextualRuleType CR_WITHIN3FROMSTART = new
	// ContextualRuleType("WITHIN3FROMSTART");
	private static final ContextualRuleType[] VALS = { CR_CURWD,
	/* CR_ENDISWORD, */

	/* CR_ISEND, */

	/* CR_ISSTART, */

	/* CR_ISSTARTOREND, */
	CR_LBIGRAM, CR_NEXT1OR2OR3TAG, CR_NEXT1OR2OR3WD, CR_NEXT1OR2TAG,
			CR_NEXT1OR2WD, CR_NEXT2TAG, CR_NEXT2WD, CR_NEXTBIGRAM,
			/* CR_NEXTISENDTAG, */
			CR_NEXTTAG, CR_NEXTWD, CR_PREV1OR2OR3TAG, CR_PREV1OR2OR3WD,
			CR_PREV1OR2TAG, CR_PREV1OR2WD, CR_PREV2TAG, CR_PREV2WD,
			CR_PREVBIGRAM,
			/* CR_PREVISSTARTTAG, */
			CR_PREVTAG, CR_PREVWD, CR_RBIGRAM,
			/* CR_STARTISWORD, */
			CR_SURROUNDTAG,
			/* CR_TAGAFTERSTART, */

			/* CR_TAGBEFOREEND, */
			CR_WDAND2AFT, CR_WDAND2BFR, CR_WDAND2TAGAFT, CR_WDAND2TAGBFR,
			CR_WDNEXTTAG, CR_WDPREVTAG,
	/* CR_WDWITHIN2FROMEND, */

	/* CR_WDWITHIN2FROMSTART, */

	/* CR_WITHIN2FROMEND, */

	/* CR_WITHIN2FROMSTART, */

	/* CR_WITHIN3FROMEND, */

	/* CR_WITHIN3FROMSTART */

	};
	static {
		for (int i = 0, n = VALS.length; i < n; i++) {
			s_ht.put(VALS[i].image, VALS[i]);
		}
	}

	// Don't really need this
	// public static final List VALUES =
	// Collections.unmodifiableList(Arrays.asList(VALS));
	// Assign an ordinal to this suit
	private final int ordinal = nextOrdinal++;

	private final String image;

	// ================
	// Constructor(s)
	// ================
	private ContextualRuleType(String image) {
		this.image = image;
	}

	// ================
	// Static Methods
	// ================
	static public boolean isCRType(String img) {
		return (s_ht.get(img) != null);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param img
	 * @return
	 */
	static public ContextualRuleType getCRType(String img) {
		return (ContextualRuleType) s_ht.get(img);
	}

	// ================
	// Public Methods
	// ================
	public String toString() {
		return this.image;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param o
	 * @return
	 */
	public int compareTo(ContextualRuleType o) {
		return ordinal - o.ordinal;
	}

	// =================
	// Private Methods
	// =================
	private Object readResolve() throws ObjectStreamException {
		return VALS[ordinal]; // Canonicalize
	}
}
