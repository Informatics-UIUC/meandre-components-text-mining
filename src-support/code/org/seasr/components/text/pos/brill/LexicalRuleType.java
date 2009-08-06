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

package  org.seasr.components.text.pos.brill;

//==============
// Java Imports
//==============
import  java.io.Serializable;
import  java.io.ObjectStreamException;
import  java.util.Hashtable;


//===============
// Other Imports
//===============
/**
 * This is a fairly slick pattern for coding enumerations in a type safe way.  This
 * pattern also supports serialization/deserialization of the enumeration instance.
 *
 * @author D. Searsmith
 * 
 * TODO: Unit Testing
 */
public class LexicalRuleType
        implements Comparable<LexicalRuleType>, Serializable {
	private static final long serialVersionUID = 1L;
	//==============
    // Data Members
    //==============
    // Ordinal of next suit to be created
    private static int nextOrdinal = 0;
    private static Hashtable<String, LexicalRuleType> s_ht = new Hashtable<String, LexicalRuleType>();
    //Data Bearing
    public static final LexicalRuleType LR_CHAR = new LexicalRuleType("char");
    public static final LexicalRuleType LR_FCHAR = new LexicalRuleType("fchar");
    public static final LexicalRuleType LR_HASSUF = new LexicalRuleType("hassuf");
    public static final LexicalRuleType LR_FHASSUF = new LexicalRuleType("fhassuf");
    public static final LexicalRuleType LR_GOODRIGHT = new LexicalRuleType("goodright");
    public static final LexicalRuleType LR_FGOODRIGHT = new LexicalRuleType("fgoodright");
    public static final LexicalRuleType LR_GOODLEFT = new LexicalRuleType("goodleft");
    public static final LexicalRuleType LR_FGOODLEFT = new LexicalRuleType("fgoodleft");
    public static final LexicalRuleType LR_HASPREF = new LexicalRuleType("haspref");
    public static final LexicalRuleType LR_FHASPREF = new LexicalRuleType("fhaspref");
    public static final LexicalRuleType LR_DELSUF = new LexicalRuleType("deletesuf");
    public static final LexicalRuleType LR_FDELSUF = new LexicalRuleType("fdeletesuf");
    public static final LexicalRuleType LR_DELPREF = new LexicalRuleType("deletepref");
    public static final LexicalRuleType LR_FDELPREF = new LexicalRuleType("fdeletepref");
    public static final LexicalRuleType LR_ADDSUF = new LexicalRuleType("addsuf");
    public static final LexicalRuleType LR_FADDSUF = new LexicalRuleType("faddsuf");
    public static final LexicalRuleType LR_ADDPREF = new LexicalRuleType("addpref");
    public static final LexicalRuleType LR_FADDPREF = new LexicalRuleType("faddpref");
    private static final LexicalRuleType[] VALS =  {
        LR_CHAR, LR_FCHAR, LR_HASSUF, LR_FHASSUF, LR_GOODRIGHT, LR_FGOODRIGHT,
                LR_GOODLEFT, LR_FGOODLEFT, LR_HASPREF, LR_FHASPREF, LR_DELSUF,
                LR_FDELSUF, LR_DELPREF, LR_FDELPREF, LR_ADDSUF, LR_FADDSUF,
                LR_ADDPREF, LR_FADDPREF
    };
    static {
        for (int i = 0, n = VALS.length; i < n; i++) {
            s_ht.put(VALS[i].image, VALS[i]);
        }
    }
    //Don't really need this
    //public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALS));
    // Assign an ordinal to this suit
    private final int ordinal = nextOrdinal++;
    private final String image;

    //================
    // Constructor(s)
    //================
    private LexicalRuleType (String image) {
        this.image = image;
    }

    //================
    // Static Methods
    //================
    static public boolean isLRType (String img) {
        return  (s_ht.get(img) != null);
    }

    /**
     * put your documentation comment here
     * @param img
     * @return
     */
    static public LexicalRuleType getLRType (String img) {
        return  (LexicalRuleType)s_ht.get(img);
    }

    //================
    // Public Methods
    //================
    public String toString () {
        return  this.image;
    }

    /**
     * put your documentation comment here
     * @param o
     * @return
     */
    public int compareTo (LexicalRuleType o) {
        return  ordinal - o.ordinal;
    }

    //=================
    // Private Methods
    //=================
    private Object readResolve () throws ObjectStreamException {
        return  VALS[ordinal];                  // Canonicalize
    }
}



