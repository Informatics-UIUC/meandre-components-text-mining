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
import java.io.Serializable;
import java.io.ObjectStreamException;

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
public class TokenFlag implements Comparable<TokenFlag>, Serializable {

	// ==============
	// Data Members
	// ==============

	private static final long serialVersionUID = 1L;
		
	// Ordinal of next suit to be created
	private static int nextOrdinal = 0;

	public static final TokenFlag START_OF_LINE = new TokenFlag("Start-of-Line");

	public static final TokenFlag END_OF_LINE = new TokenFlag("End-of-Line");

	public static final TokenFlag PRETAGGED = new TokenFlag("Pre-tagged");

	public static final TokenFlag LEXTAGGED = new TokenFlag("Lex-tagged");

	public static final TokenFlag CONTAGGED = new TokenFlag("Con-tagged");

	public static final TokenFlag NORMALIZED = new TokenFlag("Normalized");

	public static final TokenFlag IN_TITLE = new TokenFlag("In_Title");

	private static final TokenFlag[] VALS = { START_OF_LINE, END_OF_LINE,
			PRETAGGED, LEXTAGGED, CONTAGGED, NORMALIZED, IN_TITLE };

	// Don't really need this
	// public static final List VALUES =
	// Collections.unmodifiableList(Arrays.asList(VALS));
	// Assign an ordinal to this suit
	private final int ordinal = nextOrdinal++;

	private final String name;

	// ================
	// Constructor(s)
	// ================
	private TokenFlag(String name) {
		this.name = name;
	}

	// ================
	// Public Methods
	// ================
	public String toString() {
		return this.name;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param o
	 * @return
	 */
	public int compareTo(TokenFlag o) {
		return ordinal - o.ordinal;
	}

	// =================
	// Private Methods
	// =================
	private Object readResolve() throws ObjectStreamException {
		return VALS[ordinal]; // Canonicalize
	}
}
