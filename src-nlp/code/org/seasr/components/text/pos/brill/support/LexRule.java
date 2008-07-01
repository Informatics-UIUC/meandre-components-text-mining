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
import java.util.StringTokenizer;
import java.lang.Integer;
import java.io.Serializable;

//===============
// Other Imports
//===============
import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.pos.PoSTag;

import gnu.trove.*;

/**
 * This class parses rules from a text file (one rule per line). Rules can have
 * the following format.
 * 
 * [from doc README.LONG in Brill package] You can also manually add rules to
 * the rule lists. Here are some examples of the meaning of lexical rules:
 * 
 * 0 haspref 1 CD x == if a word has prefix "0" (of length 1 character), tag it
 * as a "CD"
 * 
 * VBN un fhaspref 2 JJ x == if a word has prefix "un" (of length 2 characters),
 * and it is currently tagged as "VBN", then change the tag to "JJ".
 *  - char JJ x == If the character "-" appears anywhere in the word, tag it as
 * "JJ".
 * 
 * ly hassuf 2 RB x == If a word has suffix "ly", tag it as "RB".
 * 
 * ly addsuf 2 JJ x == If adding the letters "ly" to the end of a word results
 * in a word (the new word appears in LEXICON or the extended wordlist), tag it
 * as "JJ"
 * 
 * 
 * Mr. goodright NNP x == If the word ever appears to the right of "Mr.", tag it
 * as NNP.
 * 
 * 
 * Note the difference between haspref/fhaspref, goodright/fgoodright, etc. Rule
 * names starting with "f" are retricted (only apply if the current tag matches
 * the specified current tag), while the other rules change a tagging regardless
 * of the current tagging. [End Brill doc]
 * 
 * There are a predetermined number of rule templates (defined in
 * LexicalRuleType.java). An inner class for each type appears here and is the
 * mechanism used to create a rule instance that can be applied to words.
 * 
 * @author D. Searsmith
 * 
 * TODO: Unit Testing
 * 
 */
public class LexRule implements Serializable {

	private static final long serialVersionUID = 1L;

	// ==============
	// Data Members
	// ==============
	private PoSTag m_posTag = null;

	private String m_compareStr = null;

	private LexicalRuleType m_type = null;

	private int m_length = 0;

	private LexRuleOperation m_operation = null;

	private PoSTag m_changeTo = null;

	// ================
	// Constructor(s)
	// ================
	public LexRule(PoSTag post, String compareStr, LexicalRuleType type,
			int length, LexRuleOperation op, PoSTag changeTo) {
		m_posTag = post;
		m_compareStr = compareStr;
		m_type = type;
		m_length = length;
		m_operation = op;
		m_changeTo = changeTo;
	}

	// ================
	// Static Methods
	// ================
	static public LexRule createRule(String line) {
		try {
			PoSTag post = null;
			String compareStr = null;
			LexicalRuleType type = null;
			int length = 0;
			LexRuleOperation operation = null;
			PoSTag changeTo = null;
			StringTokenizer tok = new StringTokenizer(line, " \t\r\n|");
			String rstr = tok.nextToken();
			if (PoSTag.isPoSTag(rstr)) {
				post = PoSTag.getPoSTag(rstr);
				compareStr = tok.nextToken();
			} else {
				compareStr = rstr;
			}
			String typestr = tok.nextToken();
			if (LexicalRuleType.isLRType(typestr)) {
				type = LexicalRuleType.getLRType(typestr);
			} else {
				System.out.println("LexicalRule.createRule -- type not valid: "
						+ type);
				System.out.println("create aborted.");
				return null;
			}
			rstr = tok.nextToken();
			if (PoSTag.isPoSTag(rstr)) {
				changeTo = PoSTag.getPoSTag(rstr);
			} else {
				length = Integer.parseInt(rstr);
				rstr = tok.nextToken();
				if (PoSTag.isPoSTag(rstr)) {
					changeTo = PoSTag.getPoSTag(rstr);
				} else {
					throw new Exception(
							"LexicalRule.constructor -- Should have been POS but was: "
									+ rstr);
				}
			}
			rstr = tok.nextToken();
			if (!rstr.equals("x")) {
				throw new Exception(
						"LexicalRule.constructor -- Should have been 'x' but was: "
								+ rstr);
			}
			// set operation
			if (type == LexicalRuleType.LR_CHAR) {
				operation = new LexRuleOp_CHAR();
			} else if (type == LexicalRuleType.LR_FCHAR) {
				operation = new LexRuleOp_FCHAR();
			} else if (type == LexicalRuleType.LR_HASSUF) {
				operation = new LexRuleOp_HASSUF();
			} else if (type == LexicalRuleType.LR_FHASSUF) {
				operation = new LexRuleOp_FHASSUF();
			} else if (type == LexicalRuleType.LR_GOODLEFT) {
				operation = new LexRuleOp_GOODLEFT();
			} else if (type == LexicalRuleType.LR_FGOODLEFT) {
				operation = new LexRuleOp_FGOODLEFT();
			} else if (type == LexicalRuleType.LR_GOODRIGHT) {
				operation = new LexRuleOp_GOODRIGHT();
			} else if (type == LexicalRuleType.LR_FGOODRIGHT) {
				operation = new LexRuleOp_FGOODRIGHT();
			} else if (type == LexicalRuleType.LR_HASPREF) {
				operation = new LexRuleOp_HASPREF();
			} else if (type == LexicalRuleType.LR_FHASPREF) {
				operation = new LexRuleOp_FHASPREF();
			} else if (type == LexicalRuleType.LR_DELSUF) {
				operation = new LexRuleOp_DELSUF();
			} else if (type == LexicalRuleType.LR_FDELSUF) {
				operation = new LexRuleOp_FDELSUF();
			} else if (type == LexicalRuleType.LR_DELPREF) {
				operation = new LexRuleOp_DELPREF();
			} else if (type == LexicalRuleType.LR_FDELPREF) {
				operation = new LexRuleOp_FDELPREF();
			} else if (type == LexicalRuleType.LR_ADDSUF) {
				operation = new LexRuleOp_ADDSUF();
			} else if (type == LexicalRuleType.LR_FADDSUF) {
				operation = new LexRuleOp_FADDSUF();
			} else if (type == LexicalRuleType.LR_ADDPREF) {
				operation = new LexRuleOp_ADDPREF();
			} else if (type == LexicalRuleType.LR_FADDPREF) {
				operation = new LexRuleOp_FADDPREF();
			} else {
				System.out.println("Rule type not found: " + type);
			}
			return new LexRule(post, compareStr, type, length, operation,
					changeTo);
		} catch (Exception exc) {
			System.out.println("ERROR: LexicalRule.createRule -- " + exc);
			exc.printStackTrace();
			return null;
		}
	}

	// ================
	// Public Methods
	// ================
	public void applyRule(Document doc, Annotation tok, Lexicon lex,
			DocTokMap dtmap, boolean incdesc) {
		m_operation.applyRule(doc, tok, this, lex, dtmap, incdesc);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public String getCompareStr() {
		return m_compareStr;
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
	 * @return
	 */
	public int getLength() {
		return m_length;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public PoSTag getChangeTo() {
		return m_changeTo;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public LexicalRuleType getType() {
		return m_type;
	}

	// ===============
	// Inner Classes
	// ===============
	private interface LexRuleOperation extends Serializable {

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc);
	}

	/**
	 * If the char specified by the rule appears anywhere in the term then
	 * change the term's pos to that specified by the rule.
	 */
	// CHAR
	static private class LexRuleOp_CHAR implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			String img = (String) tok.getContent(doc);
			if (img.indexOf(lr.getCompareStr()) != -1) {
				tok.getFeatures().put(AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
						lr.getChangeTo());
				tok.getFeatures().put(
						AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
						new Boolean(true));
				if (incdesc) {
					tok
							.getFeatures()
							.put(
									AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
									"Lextagged by " + lr.getType().toString());
				}
			}
		}
	}

	/**
	 * If the pos of the term matched that specified by the rule AND if the char
	 * specified by the rule appears anywhere in the term then change the term's
	 * pos to that specified by the rule.
	 */
	// FCHAR
	static private class LexRuleOp_FCHAR implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == lr
					.getPoSTag()) {
				String img = (String) tok.getContent(doc);
				if (img.indexOf(lr.getCompareStr()) != -1) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							lr.getChangeTo());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
							new Boolean(true));
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
										"Lextagged by "
												+ lr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the term has the suffix specified in the rule then change its pos to
	 * that specified by the rule.
	 */
	// HASSUF
	static private class LexRuleOp_HASSUF implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			String img = (String) tok.getContent(doc);
			if (img.endsWith(lr.getCompareStr())) {
				tok.getFeatures().put(AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
						lr.getChangeTo());
				tok.getFeatures().put(
						AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
						new Boolean(true));
				if (incdesc) {
					tok
							.getFeatures()
							.put(
									AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
									"Lextagged by " + lr.getType().toString());
				}
			}
		}
	}

	/**
	 * If the term is already flagged with pos specified in rule AND if the term
	 * has the suffix specified in the rule then change its pos to that
	 * specified by the rule.
	 */
	// FHASSUF
	static private class LexRuleOp_FHASSUF implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == lr
					.getPoSTag()) {
				String img = (String) tok.getContent(doc);
				if (img.endsWith(lr.getCompareStr())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							lr.getChangeTo());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
							new Boolean(true));
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
										"Lextagged by "
												+ lr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the term has the prefix specified in the rule then change its pos to
	 * that specified by the rule.
	 */
	// HASPREF
	static private class LexRuleOp_HASPREF implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			String img = (String) tok.getContent(doc);
			if (img.startsWith(lr.getCompareStr())) {
				tok.getFeatures().put(AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
						lr.getChangeTo());
				tok.getFeatures().put(
						AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
						new Boolean(true));
				if (incdesc) {
					tok
							.getFeatures()
							.put(
									AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
									"Lextagged by " + lr.getType().toString());
				}
			}
		}
	}

	/**
	 * If the term is already flagged with pos specified in rule AND if the term
	 * has the prefix specified in the rule then change its pos to that
	 * specified by the rule.
	 */
	// FHASPREF
	static private class LexRuleOp_FHASPREF implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == lr
					.getPoSTag()) {
				String img = (String) tok.getContent(doc);
				if (img.startsWith(lr.getCompareStr())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							lr.getChangeTo());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
							new Boolean(true));
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
										"Lextagged by "
												+ lr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the term ends in suffix and when this suffix is removed the term
	 * appears in the lexicon, then change its pos to that specified by the
	 * rule.
	 */
	// DELSUF
	static private class LexRuleOp_DELSUF implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			String img = (String) tok.getContent(doc);
			if (img.endsWith(lr.getCompareStr())) {
				String newimg = img.substring(0, img.length() - lr.getLength());
				if ((lex.isInLexicon(newimg)) || (dtmap.isWord(newimg))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							lr.getChangeTo());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
							new Boolean(true));
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
										"Lextagged by "
												+ lr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the term is already flagged with pos specified in rule AND if the term
	 * ends in suffix and when this suffix is removed the term appears in the
	 * lexicon, then change its pos to that specified by the rule.
	 */
	// FDELSUF
	static private class LexRuleOp_FDELSUF implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == lr
					.getPoSTag()) {
				String img = (String) tok.getContent(doc);
				if (img.endsWith(lr.getCompareStr())) {
					String newimg = img.substring(0, img.length()
							- lr.getLength());
					if ((lex.isInLexicon(newimg)) || (dtmap.isWord(newimg))) {
						tok.getFeatures().put(
								AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
								lr.getChangeTo());
						tok.getFeatures().put(
								AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
								new Boolean(true));
						if (incdesc) {
							tok
									.getFeatures()
									.put(
											AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
											"Lextagged by "
													+ lr.getType().toString());
						}
					}
				}
			}
		}
	}

	/**
	 * If the term begins with the prefix specified by the rule AND when the
	 * prefix is removed the term now appears in the Lexicon, then change the
	 * pos of the term to that specified by the rule.
	 */
	// DELPREF
	static private class LexRuleOp_DELPREF implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			String img = (String) tok.getContent(doc);
			if (img.startsWith(lr.getCompareStr())) {
				String newimg = img.substring(lr.getLength());
				if ((lex.isInLexicon(newimg)) || (dtmap.isWord(newimg))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							lr.getChangeTo());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
							new Boolean(true));
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
										"Lextagged by "
												+ lr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the term is already flagged with pos specified in rule AND if the term
	 * begins with the prefix specified by the rule AND when the prefix is
	 * removed the term now appears in the Lexicon, then change the pos of the
	 * term to that specified by the rule.
	 */
	// FDELPREF
	static private class LexRuleOp_FDELPREF implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == lr
					.getPoSTag()) {
				String img = (String) tok.getContent(doc);
				if (img.startsWith(lr.getCompareStr())) {
					String newimg = img.substring(lr.getLength());
					if ((lex.isInLexicon(newimg)) || (dtmap.isWord(newimg))) {
						tok.getFeatures().put(
								AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
								lr.getChangeTo());
						tok.getFeatures().put(
								AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
								new Boolean(true));
						if (incdesc) {
							tok
									.getFeatures()
									.put(
											AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
											"Lextagged by "
													+ lr.getType().toString());
						}
					}
				}
			}
		}
	}

	/**
	 * If adding the suffix specified by the rule causes this term to appear in
	 * the lexicon then change this term pos to the pos specified by the rule.
	 */
	// ADDSUF
	static private class LexRuleOp_ADDSUF implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			String img = (String) tok.getContent(doc);
			String newimg = img + lr.getCompareStr();
			if ((lex.isInLexicon(newimg)) || (dtmap.isWord(newimg))) {
				tok.getFeatures().put(AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
						lr.getChangeTo());
				tok.getFeatures().put(
						AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
						new Boolean(true));
				if (incdesc) {
					tok
							.getFeatures()
							.put(
									AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
									"Lextagged by " + lr.getType().toString());
				}
			}
		}
	}

	/**
	 * If the term is already flagged with pos specified in rule AND if adding
	 * the suffix specified by the rule causes this term to appear in the
	 * lexicon then change this term pos to the pos specified by the rule.
	 */
	// FADDSUF
	static private class LexRuleOp_FADDSUF implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == lr
					.getPoSTag()) {
				String img = (String) tok.getContent(doc);
				String newimg = img + lr.getCompareStr();
				if ((lex.isInLexicon(newimg)) || (dtmap.isWord(newimg))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							lr.getChangeTo());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
							new Boolean(true));
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
										"Lextagged by "
												+ lr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If adding the prefix specified by the rule causes this term to appear in
	 * the lexicon then change this term pos to the pos specified by the rule.
	 */
	// ADDPREF
	static private class LexRuleOp_ADDPREF implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			String img = (String) tok.getContent(doc);
			String newimg = lr.getCompareStr() + img;
			if ((lex.isInLexicon(newimg)) || (dtmap.isWord(newimg))) {
				tok.getFeatures().put(AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
						lr.getChangeTo());
				tok.getFeatures().put(
						AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
						new Boolean(true));
				if (incdesc) {
					tok
							.getFeatures()
							.put(
									AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
									"Lextagged by " + lr.getType().toString());
				}
			}
		}
	}

	/**
	 * If the term is already flagged with pos specified in rule AND if adding
	 * the prefix specified by the rule causes this term to appear in the
	 * lexicon then change this term pos to the pos specified by the rule.
	 */
	// FADDPREF
	static private class LexRuleOp_FADDPREF implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == lr
					.getPoSTag()) {
				String img = (String) tok.getContent(doc);
				String newimg = lr.getCompareStr() + img;
				if ((lex.isInLexicon(newimg)) || (dtmap.isWord(newimg))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							lr.getChangeTo());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
							new Boolean(true));
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
										"Lextagged by "
												+ lr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If this token appears to the right of the token specified by this rule
	 * anywhere in the document then change the pos of this token to that
	 * specified by the rule.
	 */
	// GOODRIGHT
	static private class LexRuleOp_GOODRIGHT implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			int[] rposits = dtmap.getPositions(lr.getCompareStr());
			if (rposits != null) {
				TIntHashSet set = new TIntHashSet(rposits);
				int[] aposits = dtmap.getPositions(tok, doc);
				for (int i = 0, n = aposits.length; i < n; i++) {
					int pos = aposits[i];
					if (set.contains(pos - 1)) {
						tok.getFeatures().put(
								AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
								lr.getChangeTo());
						tok.getFeatures().put(
								AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
								new Boolean(true));
						if (incdesc) {
							tok
									.getFeatures()
									.put(
											AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
											"Lextagged by "
													+ lr.getType().toString());
						}
					}
				}
			}
		}
	}

	/**
	 * If the term is already flagged with pos specified in rule AND if this
	 * token appears to the right of the token specified by this rule anywhere
	 * in the document then change the pos of this token to that specified by
	 * the rule.
	 */
	// FGOODRIGHT
	static private class LexRuleOp_FGOODRIGHT implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == lr
					.getPoSTag()) {
				int[] rposits = dtmap.getPositions(lr.getCompareStr());
				if (rposits != null) {
					TIntHashSet set = new TIntHashSet(rposits);
					int[] aposits = dtmap.getPositions(tok, doc);
					for (int i = 0, n = aposits.length; i < n; i++) {
						int pos = aposits[i];
						if (set.contains(pos - 1)) {
							tok.getFeatures().put(
									AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
									lr.getChangeTo());
							tok
									.getFeatures()
									.put(
											AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
											new Boolean(true));
							if (incdesc) {
								tok
										.getFeatures()
										.put(
												AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
												"Lextagged by "
														+ lr.getType()
																.toString());
							}
						}
					}
				}
			}
		}
	}

	/**
	 * If this token appears to the left of the token specified by this rule
	 * anywhere in the document the change the pos of this token to that
	 * specified by the rule.
	 */
	// GOODLEFT
	static private class LexRuleOp_GOODLEFT implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			int[] rposits = dtmap.getPositions(lr.getCompareStr());
			if (rposits != null) {
				TIntHashSet set = new TIntHashSet(rposits);
				int[] aposits = dtmap.getPositions(tok, doc);
				for (int i = 0, n = aposits.length; i < n; i++) {
					int pos = aposits[i];
					if (set.contains(pos + 1)) {
						tok.getFeatures().put(
								AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
								lr.getChangeTo());
						tok.getFeatures().put(
								AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
								new Boolean(true));
						if (incdesc) {
							tok
									.getFeatures()
									.put(
											AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
											"Lextagged by "
													+ lr.getType().toString());
						}
					}
				}
			}
		}
	}

	/**
	 * If the term is already flagged with pos specified in rule AND if this
	 * token appears to the left of the token specified by this rule anywhere in
	 * the document then change the pos of this token to that specified by the
	 * rule.
	 */
	// FGOODLEFT
	static private class LexRuleOp_FGOODLEFT implements LexRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param lr
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, LexRule lr,
				Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == lr
					.getPoSTag()) {
				int[] rposits = dtmap.getPositions(lr.getCompareStr());
				if (rposits != null) {
					TIntHashSet set = new TIntHashSet(rposits);
					int[] aposits = dtmap.getPositions(tok, doc);
					for (int i = 0, n = aposits.length; i < n; i++) {
						int pos = aposits[i];
						if (set.contains(pos + 1)) {
							tok.getFeatures().put(
									AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
									lr.getChangeTo());
							tok
									.getFeatures()
									.put(
											AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED,
											new Boolean(true));
							if (incdesc) {
								tok
										.getFeatures()
										.put(
												AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_DESC,
												"Lextagged by "
														+ lr.getType()
																.toString());
							}
						}
					}
				}
			}
		}
	}
}
