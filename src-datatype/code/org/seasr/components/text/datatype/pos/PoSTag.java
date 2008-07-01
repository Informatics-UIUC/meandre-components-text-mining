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

/**
 * Title: PoSTag
 * Description:  Enumeration class for part-of-speech tags
 * Copyright:    Copyright (c) 2001
 * Company: ALG at NCSA
 * @author D. Searsmith
 * @version 1.0
 */
//==============
// Java Imports
//==============
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.util.HashSet;
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
 */
public class PoSTag implements Comparable<PoSTag>, Serializable {
	static final long serialVersionUID = -8987244283956049610L;

	// ==============
	// Data Members
	// ==============
	// Ordinal of next suit to be created
	private static int nextOrdinal = 0;

	private static Hashtable<String, PoSTag> s_ht = new Hashtable<String, PoSTag>();

	// Data Bearing
	public static final PoSTag NO_PoS = new PoSTag(" ", "no tag whatsoever");

	public static final PoSTag PoS_NN = new PoSTag("NN",
			"noun - singular or mass");

	public static final PoSTag PoS_NNP = new PoSTag("NNP",
			"proper noun - singular");

	public static final PoSTag PoS_NNPS = new PoSTag("NNPS",
			"proper noun - plural");

	public static final PoSTag PoS_NNS = new PoSTag("NNS", "noun - plural");

	public static final PoSTag PoS_NP = new PoSTag("NP",
			"proper noun - singular");

	public static final PoSTag PoS_NPS = new PoSTag("NPS",
			"proper noun - plural");

	public static final PoSTag PoS_JJ = new PoSTag("JJ", "adjective");

	public static final PoSTag PoS_JJR = new PoSTag("JJR",
			"adjective - comparative");

	public static final PoSTag PoS_JJS = new PoSTag("JJS",
			"adjective - superlative");

	public static final PoSTag PoS_JJSS = new PoSTag("JJSS", "-unknown-");

	public static final PoSTag PoS_RB = new PoSTag("RB", "adverb");

	public static final PoSTag PoS_RBR = new PoSTag("RBR",
			"adverb - comparative");

	public static final PoSTag PoS_RBS = new PoSTag("RBS",
			"adverb - superlative");

	public static final PoSTag PoS_VB = new PoSTag("VB", "verb - base form");

	public static final PoSTag PoS_VBD = new PoSTag("VBD", "verb - past tense");

	public static final PoSTag PoS_VBG = new PoSTag("VBG",
			"verb - gerund or present participle");

	public static final PoSTag PoS_VBN = new PoSTag("VBN",
			"verb - past participle");

	public static final PoSTag PoS_VBP = new PoSTag("VBP",
			"verb - non-3rd person singular present");

	public static final PoSTag PoS_VBZ = new PoSTag("VBZ",
			"verb - 3rd person singular present");

	public static final PoSTag PoS_FW = new PoSTag("FW", "foreign word");

	public static final PoSTag PoS_CD = new PoSTag("CD", "cardinal number");

	// Last Data Bearing
	public static final PoSTag PoS_CC = new PoSTag("CC",
			"coordinating conjunction");

	public static final PoSTag PoS_DT = new PoSTag("DT", "determiner");

	public static final PoSTag PoS_EX = new PoSTag("EX", "existential 'there'");

	public static final PoSTag PoS_IN = new PoSTag("IN",
			"preposition or subordinating conjunction");

	public static final PoSTag PoS_LS = new PoSTag("LS", "list item marker");

	public static final PoSTag PoS_MD = new PoSTag("MD", "modal");

	public static final PoSTag PoS_PDT = new PoSTag("PDT", "predeterminer");

	public static final PoSTag PoS_POS = new PoSTag("POS", "possesive ending");

	public static final PoSTag PoS_PP = new PoSTag("PP", "personal pronoun");

	public static final PoSTag PoS_PRP = new PoSTag("PRP",
			"-unknown-, but probably possesive pronoun");

	public static final PoSTag PoS_PRP$ = new PoSTag("PRP$",
			"possesive pronoun");

	public static final PoSTag PoS_PRPR$ = new PoSTag("PRPR$",
			"-unknown-, but probably possesive pronoun");

	public static final PoSTag PoS_RP = new PoSTag("RP", "particle");

	public static final PoSTag PoS_TO = new PoSTag("TO", "literally, 'to'");

	public static final PoSTag PoS_UH = new PoSTag("UH", "interjection");

	public static final PoSTag PoS_WDT = new PoSTag("WDT", "'wh'-determiner");

	public static final PoSTag PoS_WP = new PoSTag("WP", "'wh'-pronoun");

	public static final PoSTag PoS_WP$ = new PoSTag("WP$",
			"possesive 'wh'-pronoun");

	public static final PoSTag PoS_WRB = new PoSTag("WRB", "'wh'-adverb");

	// Last Real Word
	public static final PoSTag PoS_SYM = new PoSTag("SYM", "symbol");

	public static final PoSTag PoS_DQUOTE = new PoSTag("\"",
			"double quotes (\")");

	public static final PoSTag PoS_POUND = new PoSTag("#", "pound sign (#)");

	public static final PoSTag PoS_DOLLAR = new PoSTag("$", "dollar sign ($)");

	public static final PoSTag PoS_QUOTES = new PoSTag("'",
			"single quote or apostrophe (')");

	public static final PoSTag PoS_LPAREN = new PoSTag("(", "left parenthesis");

	public static final PoSTag PoS_RPAREN = new PoSTag(")", "right parenthesis");

	public static final PoSTag PoS_COMMA = new PoSTag(",", "comma (,)");

	public static final PoSTag PoS_DASH2 = new PoSTag("--", "double-dash (--)");

	public static final PoSTag PoS_LRB = new PoSTag("-LRB-",
			"'-LRB-', whatever that means");

	public static final PoSTag PoS_PERIOD = new PoSTag(".", "period (.)");

	public static final PoSTag PoS_COLON = new PoSTag(":", "colon (:)");

	public static final PoSTag PoS_GRAVE = new PoSTag("`", "grave (`)");

	public static final PoSTag PoS_STAART = new PoSTag("STAART",
			"Start of Line");

	// added to be compatible with heptag from GATE
	public static final PoSTag PoS_NNS_VBZ = new PoSTag("NNS|VBZ", "NNS or VBZ");

	private static final PoSTag[] VALS = { NO_PoS, PoS_NN, PoS_NNP, PoS_NNPS,
			PoS_NNS, PoS_NP, PoS_NPS, PoS_JJ, PoS_JJR, PoS_JJS, PoS_JJSS,
			PoS_RB, PoS_RBR, PoS_RBS, PoS_VB, PoS_VBD, PoS_VBG, PoS_VBN,
			PoS_VBP, PoS_VBZ, PoS_FW, PoS_CD, PoS_CC, PoS_DT, PoS_EX, PoS_IN,
			PoS_LS, PoS_MD, PoS_PDT, PoS_POS, PoS_PP, PoS_PRP, PoS_PRP$,
			PoS_PRPR$, PoS_RP, PoS_TO, PoS_UH, PoS_WDT, PoS_WP, PoS_WP$,
			PoS_WRB, PoS_SYM, PoS_DQUOTE, PoS_POUND, PoS_DOLLAR, PoS_QUOTES,
			PoS_LPAREN, PoS_RPAREN, PoS_COMMA, PoS_DASH2, PoS_LRB, PoS_PERIOD,
			PoS_COLON, PoS_GRAVE, PoS_STAART, PoS_NNS_VBZ };

	private static HashSet<PoSTag> _symbols = new HashSet<PoSTag>();

	static {
		for (int i = 0, n = VALS.length; i < n; i++) {
			s_ht.put(VALS[i]._image, VALS[i]);
		}
		_symbols.add(PoS_SYM);
		_symbols.add(PoS_DQUOTE);
		_symbols.add(PoS_POUND);
		_symbols.add(PoS_DOLLAR);
		_symbols.add(PoS_QUOTES);
		_symbols.add(PoS_LPAREN);
		_symbols.add(PoS_RPAREN);
		_symbols.add(PoS_COMMA);
		_symbols.add(PoS_DASH2);
		_symbols.add(PoS_LRB);
		_symbols.add(PoS_PERIOD);
		_symbols.add(PoS_COLON);
		_symbols.add(PoS_GRAVE);
	}

	static public boolean isSymbol(PoSTag tag) {
		return _symbols.contains(tag);
	}

	// Don't really need this
	// public static final List VALUES =
	// Collections.unmodifiableList(Arrays.asList(VALS));
	// Assign an ordinal to this suit
	private final int ordinal = nextOrdinal++;

	private final String _desc;

	private final String _image;

	// ================
	// Constructor(s)
	// ================
	private PoSTag(String image, String desc) {
		_desc = desc;
		_image = image;
	}

	// ================
	// Static Methods
	// ================
	static public boolean isPoSTag(String img) {
		return (s_ht.get(img) != null);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param img
	 * @return
	 */
	static public PoSTag getPoSTag(String img) {
		return (PoSTag) s_ht.get(img);
	}

	// ================
	// Public Methods
	// ================
	public String toString() {
		return _image;
	}
	
	public String getDescriptioin(){
		return _desc;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param o
	 * @return
	 */
	public int compareTo(PoSTag o) {
		return ordinal - o.ordinal;
	}

	// =================
	// Private Methods
	// =================
	private Object readResolve() throws ObjectStreamException {
		return VALS[ordinal]; // Canonicalize
	}
}
