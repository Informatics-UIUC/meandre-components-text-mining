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

//===============
// Other Imports
//===============

import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.pos.PoSTag;

/**
 * 
 * A data structure for contextual linguistic rules in the Brill tagger.
 * 
 * @author D. Searsmith
 * 
 * TODO: Unit Tests
 * 
 */
public class ContextRule implements Serializable {

	private static final long serialVersionUID = 1L;

	// ==============
	// Data Members
	// ==============
	private PoSTag m_oldTag = null;

	private PoSTag m_newTag = null;

	private ContextualRuleType m_type = null;

	private String m_word1 = null;

	private String m_word2 = null;

	private ContextualRuleOperation m_operation = null;

	private PoSTag m_tag1 = null;

	private PoSTag m_tag2 = null;

	// ================
	// Constructor(s)
	// ================
	public ContextRule(PoSTag oldTag, PoSTag newTag, ContextualRuleType type,
			String word1, String word2, ContextualRuleOperation op,
			PoSTag tag1, PoSTag tag2) {
		m_oldTag = oldTag;
		m_newTag = newTag;
		m_type = type;
		m_word1 = word1;
		m_word2 = word2;
		m_operation = op;
		m_tag1 = tag1;
		m_tag2 = tag2;
	}

	// ================
	// Static Methods
	// ================
	static public ContextRule createRule(String line) {
		try {
			PoSTag oldTag = null;
			PoSTag newTag = null;
			ContextualRuleType type = null;
			PoSTag tag1 = null;
			PoSTag tag2 = null;
			String word1 = null;
			String word2 = null;
			ContextualRuleOperation operation = null;
			String rstr, rstr2 = null;
			StringTokenizer tok = new StringTokenizer(line, " \t\r\n|");
			rstr = tok.nextToken();
			if (PoSTag.isPoSTag(rstr)) {
				oldTag = PoSTag.getPoSTag(rstr);
			} else {
				System.out.println("Not a valid POS tag: " + rstr);
				System.out.println("create aborted.");
				return null;
			}
			rstr = tok.nextToken();
			if (PoSTag.isPoSTag(rstr)) {
				newTag = PoSTag.getPoSTag(rstr);
			} else {
				System.out.println("Not a valid POS tag: " + rstr);
				System.out.println("create aborted.");
				return null;
			}
			String typestr = tok.nextToken();
			if (ContextualRuleType.isCRType(typestr)) {
				type = ContextualRuleType.getCRType(typestr);
			} else {
				System.out
						.println("ContextualRule.createRule -- type not valid: "
								+ type);
				System.out.println("create aborted.");
				return null;
			}
			if ((type == ContextualRuleType.CR_NEXTTAG)
					|| (type == ContextualRuleType.CR_NEXT2TAG)
					|| (type == ContextualRuleType.CR_NEXT1OR2TAG)
					|| (type == ContextualRuleType.CR_NEXT1OR2OR3TAG)
					|| (type == ContextualRuleType.CR_PREVTAG)
					|| (type == ContextualRuleType.CR_PREV2TAG)
					|| (type == ContextualRuleType.CR_PREV1OR2TAG)
					|| (type == ContextualRuleType.CR_PREV1OR2OR3TAG)) {
				// POS
				rstr = tok.nextToken();
				if (PoSTag.isPoSTag(rstr)) {
					tag1 = PoSTag.getPoSTag(rstr);
				} else {
					System.out.println("Not a valid POS tag: " + rstr);
					System.out.println("create aborted.");
					return null;
				}
			} else if ((type == ContextualRuleType.CR_CURWD)
					|| (type == ContextualRuleType.CR_NEXTWD)
					|| (type == ContextualRuleType.CR_NEXT2WD)
					|| (type == ContextualRuleType.CR_NEXT1OR2WD)
					|| (type == ContextualRuleType.CR_NEXT1OR2OR3WD)
					|| (type == ContextualRuleType.CR_PREVWD)
					|| (type == ContextualRuleType.CR_PREV2WD)
					|| (type == ContextualRuleType.CR_PREV1OR2WD)
					|| (type == ContextualRuleType.CR_PREV1OR2OR3WD)) {
				// str
				word1 = tok.nextToken();
			} else if ((type == ContextualRuleType.CR_SURROUNDTAG)
					|| (type == ContextualRuleType.CR_NEXTBIGRAM)
					|| (type == ContextualRuleType.CR_PREVBIGRAM)) {
				// POS POS
				rstr = tok.nextToken();
				rstr2 = tok.nextToken();
				if ((PoSTag.isPoSTag(rstr)) && (PoSTag.isPoSTag(rstr2))) {
					tag1 = PoSTag.getPoSTag(rstr);
					tag2 = PoSTag.getPoSTag(rstr2);
				} else {
					System.out.println("Not a valid POS tag: " + rstr + " or "
							+ rstr2);
					System.out.println("create aborted.");
					return null;
				}
			} else if ((type == ContextualRuleType.CR_RBIGRAM)
					|| (type == ContextualRuleType.CR_WDAND2AFT)
					|| (type == ContextualRuleType.CR_WDAND2BFR)
					|| (type == ContextualRuleType.CR_LBIGRAM)) {
				// str str
				word1 = tok.nextToken();
				word2 = tok.nextToken();
			} else if ((type == ContextualRuleType.CR_WDNEXTTAG)
					|| (type == ContextualRuleType.CR_WDAND2TAGAFT)) {
				// str POS
				word1 = tok.nextToken();
				rstr = tok.nextToken();
				if (PoSTag.isPoSTag(rstr)) {
					tag2 = PoSTag.getPoSTag(rstr);
				} else {
					System.out.println("Not a valid POS tag: " + rstr);
					System.out.println("create aborted.");
					return null;
				}
			} else if ((type == ContextualRuleType.CR_WDPREVTAG)
					|| (type == ContextualRuleType.CR_WDAND2TAGBFR)) {
				// POS str
				rstr = tok.nextToken();
				if (PoSTag.isPoSTag(rstr)) {
					tag1 = PoSTag.getPoSTag(rstr);
				} else {
					System.out.println("Not a valid POS tag: " + rstr);
					System.out.println("create aborted.");
					return null;
				}
				word2 = tok.nextToken();
			} else {
				System.out.println("Rule type not found: " + type);
			}
			// set operation
			if (type == ContextualRuleType.CR_SURROUNDTAG) {
				operation = new ContextualRuleOp_SURROUNDTAG();
			} else if (type == ContextualRuleType.CR_NEXTTAG) {
				operation = new ContextualRuleOp_NEXTTAG();
			} else if (type == ContextualRuleType.CR_CURWD) {
				operation = new ContextualRuleOp_CURWD();
			} else if (type == ContextualRuleType.CR_NEXTWD) {
				operation = new ContextualRuleOp_NEXTWD();
			} else if (type == ContextualRuleType.CR_RBIGRAM) {
				operation = new ContextualRuleOp_RBIGRAM();
			} else if (type == ContextualRuleType.CR_WDNEXTTAG) {
				operation = new ContextualRuleOp_WDNEXTTAG();
			} else if (type == ContextualRuleType.CR_WDAND2AFT) {
				operation = new ContextualRuleOp_WDAND2AFT();
			} else if (type == ContextualRuleType.CR_WDAND2TAGAFT) {
				operation = new ContextualRuleOp_WDAND2TAGAFT();
			} else if (type == ContextualRuleType.CR_NEXT2TAG) {
				operation = new ContextualRuleOp_NEXT2TAG();
			} else if (type == ContextualRuleType.CR_NEXT2WD) {
				operation = new ContextualRuleOp_NEXT2WD();
			} else if (type == ContextualRuleType.CR_NEXTBIGRAM) {
				operation = new ContextualRuleOp_NEXTBIGRAM();
			} else if (type == ContextualRuleType.CR_NEXT1OR2TAG) {
				operation = new ContextualRuleOp_NEXT1OR2TAG();
			} else if (type == ContextualRuleType.CR_NEXT1OR2WD) {
				operation = new ContextualRuleOp_NEXT1OR2WD();
			} else if (type == ContextualRuleType.CR_NEXT1OR2OR3TAG) {
				operation = new ContextualRuleOp_NEXT1OR2OR3TAG();
			} else if (type == ContextualRuleType.CR_NEXT1OR2OR3WD) {
				operation = new ContextualRuleOp_NEXT1OR2OR3WD();
			} else if (type == ContextualRuleType.CR_PREVTAG) {
				operation = new ContextualRuleOp_PREVTAG();
			} else if (type == ContextualRuleType.CR_PREVWD) {
				operation = new ContextualRuleOp_PREVWD();
			} else if (type == ContextualRuleType.CR_LBIGRAM) {
				operation = new ContextualRuleOp_LBIGRAM();
			} else if (type == ContextualRuleType.CR_WDPREVTAG) {
				operation = new ContextualRuleOp_WDPREVTAG();
			} else if (type == ContextualRuleType.CR_WDAND2BFR) {
				operation = new ContextualRuleOp_WDAND2BFR();
			} else if (type == ContextualRuleType.CR_WDAND2TAGBFR) {
				operation = new ContextualRuleOp_WDAND2TAGBFR();
			} else if (type == ContextualRuleType.CR_PREV2TAG) {
				operation = new ContextualRuleOp_PREV2TAG();
			} else if (type == ContextualRuleType.CR_PREV2WD) {
				operation = new ContextualRuleOp_PREV2WD();
			} else if (type == ContextualRuleType.CR_PREV1OR2TAG) {
				operation = new ContextualRuleOp_PREV1OR2TAG();
			} else if (type == ContextualRuleType.CR_PREV1OR2WD) {
				operation = new ContextualRuleOp_PREV1OR2WD();
			} else if (type == ContextualRuleType.CR_PREV1OR2OR3TAG) {
				operation = new ContextualRuleOp_PREV1OR2OR3TAG();
			} else if (type == ContextualRuleType.CR_PREV1OR2OR3WD) {
				operation = new ContextualRuleOp_PREV1OR2OR3WD();
			} else if (type == ContextualRuleType.CR_PREVBIGRAM) {
				operation = new ContextualRuleOp_PREVBIGRAM();
			} else {
				System.out.println("Rule type not found: " + type);
			}
			return new ContextRule(oldTag, newTag, type, word1, word2,
					operation, tag1, tag2);
		} catch (Exception exc) {
			System.out.println("ERROR: ContextualRule.createRule -- " + exc);
			exc.printStackTrace();
			return null;
		}
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param term
	 * @param tag
	 * @param lex
	 * @return
	 */
	private static boolean hasTagInLex(Document doc, Annotation term, PoSTag tag, Lexicon lex) {
		PoSTag[] tags = lex.getTagsForTerm((String) term.getContent(doc));
		for (int i = 0, n = tags.length; i < n; i++) {
			if (tags[i] == tag) {
				return true;
			}
		}
		return false;
	}

	// ================
	// Public Methods
	// ================
	public void applyRule(Document doc, Annotation tok, boolean restrict,
			Lexicon lex, DocTokMap dtmap, boolean incdesc) {
		m_operation.applyRule(doc, tok, this, restrict, lex, dtmap, incdesc);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public String getWord1() {
		return m_word1;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public String getWord2() {
		return m_word2;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public PoSTag getTag1() {
		return m_tag1;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public PoSTag getTag2() {
		return m_tag2;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public PoSTag getOldTag() {
		return m_oldTag;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public PoSTag getNewTag() {
		return m_newTag;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public ContextualRuleType getType() {
		return m_type;
	}

	// ===============
	// Inner Classes
	// ===============
	private interface ContextualRuleOperation extends Serializable {

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc);
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the tag to the left and right of term match values in the rule then
	 * set the new tag to that specified by the rule.
	 */
	// SURROUNDTAG
	static private class ContextualRuleOp_SURROUNDTAG implements
			ContextualRuleOperation {
		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {

			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 1)) {
					return;
				}
				// bounds check
				if (pos == 0) {
					return;
				}

				PoSTag termL = null;
				if (tok.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termL = PoSTag.PoS_STAART;
				} else {
					termL = (PoSTag) dtmap.getAnnotationAtPos(pos - 1)
							.getFeatures().get(
									AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				Annotation tokR = dtmap.getAnnotationAtPos(pos + 1);
				PoSTag termR = null;
				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = PoSTag.PoS_STAART;
				} else {
					termR = (PoSTag) tokR.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				if ((termL == cr.getTag1()) && (termR == cr.getTag2())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}

				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the tag to immediately following the term matches values in the rule
	 * then set the new tag to that specified by the rule.
	 */
	// NEXTTAG
	static private class ContextualRuleOp_NEXTTAG implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 1)) {
					return;
				}

				Annotation tokR = dtmap.getAnnotationAtPos(pos + 1);
				PoSTag termR = null;
				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = PoSTag.PoS_STAART;
				} else {
					termR = (PoSTag) tokR.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				if (termR == cr.getTag1()) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the current word matches values in the rule then set the new tag to
	 * that specified by the rule.
	 */
	// CURWD
	static private class ContextualRuleOp_CURWD implements
			ContextualRuleOperation {
		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}

				if (((String) tok.getContent(doc)).equals(cr
						.getWord1())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the next word matches values in the rule then set the new tag to that
	 * specified by the rule.
	 */
	// NEXTWD
	static private class ContextualRuleOp_NEXTWD implements
			ContextualRuleOperation {
		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 1)) {
					return;
				}

				Annotation tokR = dtmap.getAnnotationAtPos(pos + 1);
				String termR = null;
				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = "STAART";
				} else {
					termR = (String) tokR.getContent(doc);
				}

				if (termR.equals(cr.getWord1())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the current and next word matches values in the rule then set the new
	 * tag to that specified by the rule.
	 */
	// RBIGRAM
	static private class ContextualRuleOp_RBIGRAM implements
			ContextualRuleOperation {
		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 1)) {
					return;
				}

				Annotation tokR = dtmap.getAnnotationAtPos(pos + 1);
				String termR = null;
				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = "STAART";
				} else {
					termR = (String) tokR.getContent(doc);
				}

				String img = (String) tok.getContent(doc);
				if ((img.equals(cr.getWord1()))
						&& (termR.equals(cr.getWord2()))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the current word and next tag matches values in the rule then set the
	 * new tag to that specified by the rule.
	 */
	// WDNEXTTAG
	static private class ContextualRuleOp_WDNEXTTAG implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 1)) {
					return;
				}

				Annotation tokR = dtmap.getAnnotationAtPos(pos + 1);
				PoSTag termR = null;
				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = PoSTag.PoS_STAART;
				} else {
					termR = (PoSTag) tokR.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				String img = (String) tok.getContent(doc);
				if ((img.equals(cr.getWord1())) && (termR == cr.getTag1())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the current and word after next word matches values in the rule then
	 * set the new tag to that specified by the rule.
	 */
	// WDAND2AFT
	static private class ContextualRuleOp_WDAND2AFT implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 2)) {
					return;
				}

				Annotation tokR = dtmap.getAnnotationAtPos(pos + 2);
				String termR = null;
				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = "STAART";
				} else {
					termR = (String) tokR.getContent(doc);
				}

				String img = (String) tok.getContent(doc);
				if ((img.equals(cr.getWord1()))
						&& (termR.equals(cr.getWord2()))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the current word and tag after next tag matches values in the rule
	 * then set the new tag to that specified by the rule.
	 */
	// WDAND2TAGAFT
	static private class ContextualRuleOp_WDAND2TAGAFT implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 2)) {
					return;
				}

				Annotation tokR = dtmap.getAnnotationAtPos(pos + 2);
				PoSTag termR = null;
				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = PoSTag.PoS_STAART;
				} else {
					termR = (PoSTag) tokR.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				String img = (String) tok.getContent(doc);
				if ((img.equals(cr.getWord1())) && (termR == cr.getTag1())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the tag after next following the term matches values in the rule then
	 * set the new tag to that specified by the rule.
	 */
	// NEXT2TAG
	static private class ContextualRuleOp_NEXT2TAG implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 2)) {
					return;
				}

				Annotation tokR = dtmap.getAnnotationAtPos(pos + 2);
				PoSTag termR = null;
				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = PoSTag.PoS_STAART;
				} else {
					termR = (PoSTag) tokR.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				// String img =
				// (String)tok.getContent(doc);
				if (termR == cr.getTag1()) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the word after next word matches values in the rule then set the new
	 * tag to that specified by the rule.
	 */
	// NEXT2WD
	static private class ContextualRuleOp_NEXT2WD implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 2)) {
					return;
				}

				Annotation tokR = dtmap.getAnnotationAtPos(pos + 2);
				String termR = null;
				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = "STAART";
				} else {
					termR = (String) tokR.getContent(doc);
				}

				// String img =
				// (String)tok.getContent(doc);
				if (termR.equals(cr.getWord1())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the next two tags after term match values in the rule then set the new
	 * tag to that specified by the rule.
	 */
	// NEXTBIGRAM
	static private class ContextualRuleOp_NEXTBIGRAM implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 2)) {
					return;
				}

				Annotation tokL = dtmap.getAnnotationAtPos(pos + 1);
				PoSTag termL = null;
				Annotation tokR = dtmap.getAnnotationAtPos(pos + 2);
				if (tokL.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termL = PoSTag.PoS_STAART;
					tokR = tokL;
				} else {
					termL = (PoSTag) tokL.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				PoSTag termR = null;
				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = PoSTag.PoS_STAART;
				} else {
					termR = (PoSTag) tokR.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				if ((termL == cr.getTag1()) && (termR == cr.getTag2())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the next tag or the one after match values in the rule then set the
	 * new tag to that specified by the rule.
	 */
	// NEXT1OR2TAG
	static private class ContextualRuleOp_NEXT1OR2TAG implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 2)) {
					return;
				}
				int pos2 = pos + 2;
				if (!(pos < sz - 2)) {
					pos2 = pos + 1;
				}

				Annotation tokL = dtmap.getAnnotationAtPos(pos + 1);
				PoSTag termL = null;
				Annotation tokR = dtmap.getAnnotationAtPos(pos2);
				if (tokL.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termL = PoSTag.PoS_STAART;
					tokR = tokL;
				} else {
					termL = (PoSTag) tokL.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				PoSTag termR = null;
				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = PoSTag.PoS_STAART;
				} else {
					termR = (PoSTag) tokR.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				if ((termL == cr.getTag1()) || (termR == cr.getTag1())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the next word or the one after match values in the rule then set the
	 * new tag to that specified by the rule.
	 */
	// NEXT1OR2WD
	static private class ContextualRuleOp_NEXT1OR2WD implements
			ContextualRuleOperation {
		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 2)) {
					return;
				}
				int pos2 = pos + 2;
				if (!(pos < sz - 2)) {
					pos2 = pos + 1;
				}

				Annotation tokL = dtmap.getAnnotationAtPos(pos + 1);
				String termL = null;
				Annotation tokR = dtmap.getAnnotationAtPos(pos2);
				if (tokL.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termL = "STAART";
					tokR = tokL;
				} else {
					termL = (String) tokL.getContent(doc);
				}

				String termR = null;
				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = "STAART";
				} else {
					termR = (String) tokR.getContent(doc);
				}

				if ((termL.equals(cr.getWord1()))
						|| (termR.equals(cr.getWord1()))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the next tag or either of the two after match values in the rule then
	 * set the new tag to that specified by the rule.
	 */
	// NEXT1OR2OR3TAG
	static private class ContextualRuleOp_NEXT1OR2OR3TAG implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 2)) {
					return;
				}
				int pos2 = pos + 2;
				if (!(pos < sz - 2)) {
					pos2 = pos + 1;
				}
				int pos3 = pos + 3;
				if (!(pos < sz - 3)) {
					pos3 = pos + 1;
				}

				Annotation tok1 = dtmap.getAnnotationAtPos(pos + 1);
				PoSTag term1 = null;
				Annotation tok2 = dtmap.getAnnotationAtPos(pos2);
				Annotation tok3 = dtmap.getAnnotationAtPos(pos3);
				if (tok1.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term1 = PoSTag.PoS_STAART;
					tok2 = tok1;
					tok3 = tok2;
				} else {
					term1 = (PoSTag) tok1.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				PoSTag term2 = null;
				if (tok2.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term2 = PoSTag.PoS_STAART;
					tok3 = tok2;
				} else {
					term2 = (PoSTag) tok2.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				PoSTag term3 = null;
				if (tok3.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term3 = PoSTag.PoS_STAART;
				} else {
					term3 = (PoSTag) tok3.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				if ((term1 == cr.getTag1()) || (term2 == cr.getTag1())
						|| (term3 == cr.getTag1())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the next word or either of the two after match values in the rule then
	 * set the new tag to that specified by the rule.
	 */
	// NEXT1OR2OR3WD
	static private class ContextualRuleOp_NEXT1OR2OR3WD implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				int sz = dtmap.getListSize();
				// bounds check
				if (!(pos < sz - 2)) {
					return;
				}
				int pos2 = pos + 2;
				if (!(pos < sz - 2)) {
					pos2 = pos + 1;
				}
				int pos3 = pos + 3;
				if (!(pos < sz - 3)) {
					pos3 = pos + 1;
				}

				Annotation tok1 = dtmap.getAnnotationAtPos(pos + 1);
				String term1 = null;
				Annotation tok2 = dtmap.getAnnotationAtPos(pos2);
				Annotation tok3 = dtmap.getAnnotationAtPos(pos3);
				if (tok1.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term1 = "STAART";
					tok2 = tok1;
					tok3 = tok2;
				} else {
					term1 = (String) tok1.getContent(doc);
				}

				String term2 = null;
				if (tok2.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term2 = "STAART";
					tok3 = tok2;
				} else {
					term2 = (String) tok2.getContent(doc);
				}

				String term3 = null;
				if (tok3.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term3 = "STAART";
				} else {
					term3 = (String) tok3.getContent(doc);
				}

				if ((term1.equals(cr.getWord1()))
						|| (term2.equals(cr.getWord1()))
						|| (term3.equals(cr.getWord1()))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the tag to immediately before the term matches values in the rule then
	 * set the new tag to that specified by the rule.
	 */
	// PREVTAG
	static private class ContextualRuleOp_PREVTAG implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				// bounds check
				if (pos == 0) {
					return;
				}

				Annotation tokL = dtmap.getAnnotationAtPos(pos - 1);
				PoSTag termL = null;
				if (tokL.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termL = PoSTag.PoS_STAART;
				} else {
					termL = (PoSTag) tokL.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				if (termL == cr.getTag1()) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the last word matches values in the rule then set the new tag to that
	 * specified by the rule.
	 */
	// PREVWD
	static private class ContextualRuleOp_PREVWD implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				// bounds check
				if (pos == 0) {
					return;
				}

				Annotation tokL = dtmap.getAnnotationAtPos(pos - 1);
				String termL = null;
				if (tokL.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termL = "STAART";
				} else {
					termL = (String) tokL.getContent(doc);
				}

				if (termL.equals(cr.getWord1())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the current and last word matches values in the rule then set the new
	 * tag to that specified by the rule.
	 */
	// LBIGRAM
	static private class ContextualRuleOp_LBIGRAM implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				// bounds check
				if (pos == 0) {
					return;
				}

				Annotation tokL = dtmap.getAnnotationAtPos(pos - 1);
				String termL = null;
				if (tokL.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termL = "STAART";
				} else {
					termL = (String) tokL.getContent(doc);
				}

				String img = (String) tok.getContent(doc);
				if ((termL.equals(cr.getWord1()))
						&& (img.equals(cr.getWord2()))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the current word and previous tag matches values in the rule then set
	 * the new tag to that specified by the rule.
	 */
	// WDPREVTAG
	static private class ContextualRuleOp_WDPREVTAG implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				// bounds check
				if (pos == 0) {
					return;
				}

				Annotation tokL = dtmap.getAnnotationAtPos(pos - 1);
				PoSTag termL = null;
				if (tokL.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termL = PoSTag.PoS_STAART;
				} else {
					termL = (PoSTag) tokL.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				String img = (String) tok.getContent(doc);
				if ((termL == cr.getTag1()) && (img.equals(cr.getWord1()))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the current and word before last word matches values in the rule then
	 * set the new tag to that specified by the rule.
	 */
	// WDAND2BFR
	static private class ContextualRuleOp_WDAND2BFR implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				// bounds check
				if (pos < 2) {
					return;
				}

				Annotation tok1 = dtmap.getAnnotationAtPos(pos - 2);
				Annotation tok0 = dtmap.getAnnotationAtPos(pos - 1);
				String term1 = null;
				if (tok0.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term1 = "STAART";
				} else {
					term1 = (String) tok1.getContent(doc);
				}

				String img = (String) tok.getContent(doc);
				if ((term1.equals(cr.getWord1()))
						&& (img.equals(cr.getWord2()))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the current word and tag before last tag matches values in the rule
	 * then set the new tag to that specified by the rule.
	 */
	// WDAND2TAGBFR
	static private class ContextualRuleOp_WDAND2TAGBFR implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				// bounds check
				if (pos < 2) {
					return;
				}

				Annotation tok1 = dtmap.getAnnotationAtPos(pos - 2);
				Annotation tok0 = dtmap.getAnnotationAtPos(pos - 1);
				PoSTag term1 = null;
				if (tok0.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term1 = PoSTag.PoS_STAART;
				} else {
					term1 = (PoSTag) tok1.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				String img = (String) tok.getContent(doc);
				if ((term1 == cr.getTag1()) && (img.equals(cr.getWord1()))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the tag two terms before the term matches values in the rule then set
	 * the new tag to that specified by the rule.
	 */
	// PREV2TAG
	static private class ContextualRuleOp_PREV2TAG implements
			ContextualRuleOperation {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				// bounds check
				if (pos < 2) {
					return;
				}

				Annotation tokL = dtmap.getAnnotationAtPos(pos - 2);
				PoSTag termL = null;
				if (tok.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					tokL = dtmap.getAnnotationAtPos(pos - 1);
					termL = (PoSTag) tokL.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				if (termL == cr.getTag1()) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the word before last matches values in the rule then set the new tag
	 * to that specified by the rule.
	 */
	// PREV2WD
	static private class ContextualRuleOp_PREV2WD implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				// bounds check
				if (pos < 2) {
					return;
				}

				Annotation tokL = dtmap.getAnnotationAtPos(pos - 2);
				String termL = null;
				if (tok.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					tokL = dtmap.getAnnotationAtPos(pos - 1);
					termL = (String) tokL.getContent(doc);
				}

				if (termL.equals(cr.getWord1())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the previous tag or the one before it match values in the rule then
	 * set the new tag to that specified by the rule.
	 */
	// PREV1OR2TAG
	static private class ContextualRuleOp_PREV1OR2TAG implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				// bounds check
				if (pos == 0) {
					return;
				}
				int pos2 = pos - 2;
				if (pos < 2) {
					pos2 = pos - 1;
				}

				PoSTag termL = null;
				PoSTag termR = null;
				Annotation tokL = dtmap.getAnnotationAtPos(pos2);
				Annotation tokR = dtmap.getAnnotationAtPos(pos - 1);
				termR = (PoSTag) tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				if (tok.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = PoSTag.PoS_STAART;
					tokL = tokR;
				}
				termL = (PoSTag) tokL.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_POS);

				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termL = PoSTag.PoS_STAART;
				}

				if ((termL == cr.getTag1()) || (termR == cr.getTag1())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the previous word or the one before it match values in the rule then
	 * set the new tag to that specified by the rule.
	 */
	// PREV1OR2WD
	static private class ContextualRuleOp_PREV1OR2WD implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				// bounds check
				if (pos == 0) {
					return;
				}
				int pos2 = pos - 2;
				if (pos < 2) {
					pos2 = pos - 1;
				}

				String termL = null;
				String termR = null;
				Annotation tokL = dtmap.getAnnotationAtPos(pos2);
				Annotation tokR = dtmap.getAnnotationAtPos(pos - 1);
				termR = (String) tokR.getContent(doc);
				if (tok.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = "STAART";
					tokL = tokR;
				}
				termL = (String) tokL.getContent(doc);

				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termL = "STAART";
				}

				if ((termL.equals(cr.getWord1()))
						|| (termR.equals(cr.getWord1()))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the previous tag or either of the two before it match values in the
	 * rule then set the new tag to that specified by the rule.
	 */
	// PREV1OR2OR3TAG
	static private class ContextualRuleOp_PREV1OR2OR3TAG implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);

				// bounds check
				if (pos == 0) {
					return;
				}
				int pos2 = pos - 2;
				if (pos < 2) {
					pos2 = pos - 1;
				}

				Annotation tok1 = dtmap.getAnnotationAtPos(pos - 1);
				Annotation tok2 = dtmap.getAnnotationAtPos(pos2);
				PoSTag term1 = null;
				PoSTag term2 = null;
				PoSTag term3 = null;

				if (tok.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term1 = PoSTag.PoS_STAART;
					tok2 = dtmap.getAnnotationAtPos(pos - 1);
				} else {
					term1 = (PoSTag) tok.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				if (tok1.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term2 = PoSTag.PoS_STAART;
				} else {
					term2 = (PoSTag) tok.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				if (tok2.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term3 = PoSTag.PoS_STAART;
				} else {
					term2 = (PoSTag) tok.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				}

				if ((term1 == cr.getTag1()) || (term2 == cr.getTag1())
						|| (term3 == cr.getTag1())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the previous word or either of the two before it match values in the
	 * rule then set the new tag to that specified by the rule.
	 */
	// PREV1OR2OR3WD
	static private class ContextualRuleOp_PREV1OR2OR3WD implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);

				// bounds check
				if (pos == 0) {
					return;
				}
				int pos2 = pos - 2;
				if (pos < 2) {
					pos2 = pos - 1;
				}

				Annotation tok1 = dtmap.getAnnotationAtPos(pos - 1);
				Annotation tok2 = dtmap.getAnnotationAtPos(pos2);
				String term1 = null;
				String term2 = null;
				String term3 = null;

				if (tok.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term1 = "STAART";
					tok2 = dtmap.getAnnotationAtPos(pos - 1);
				} else {
					term1 = (String) tok.getContent(doc);
				}

				if (tok1.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term2 = "STAART";
				} else {
					term2 = (String) tok.getContent(doc);
				}

				if (tok2.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					term3 = "STAART";
				} else {
					term2 = (String) tok.getContent(doc);
				}

				if ((term1.equals(cr.getWord1()))
						|| (term2.equals(cr.getWord1()))
						|| (term3.equals(cr.getWord1()))) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}

	/**
	 * If the current tag matched that specified by the rule and if restricted,
	 * the new tag is a possible one from the lexicon for this term THEN ...
	 * 
	 * If the last two tags before the current term match values in the rule
	 * then set the new tag to that specified by the rule.
	 */
	// PREVBIGRAM
	static private class ContextualRuleOp_PREVBIGRAM implements
			ContextualRuleOperation {

		private static final long serialVersionUID = 1L;

		/**
		 * put your documentation comment here
		 * 
		 * @param doc
		 * @param pos
		 * @param cr
		 * @param restrict
		 * @param lex
		 */
		public void applyRule(Document doc, Annotation tok, ContextRule cr,
				boolean restrict, Lexicon lex, DocTokMap dtmap, boolean incdesc) {
			if (tok.getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_POS) == cr
					.getOldTag()) {
				if (restrict) {
					if (!(hasTagInLex(doc, tok, cr.getNewTag(), lex))) {
						return;
					}
				}
				int pos = dtmap.getPosition(tok);
				// bounds check
				if (pos < 2) {
					return;
				}

				PoSTag termL = null;
				PoSTag termR = null;
				Annotation tokL = dtmap.getAnnotationAtPos(pos - 2);
				Annotation tokR = dtmap.getAnnotationAtPos(pos - 1);
				termR = (PoSTag) tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
				if (tok.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termR = PoSTag.PoS_STAART;
					tokL = tokR;
				}
				termL = (PoSTag) tokL.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_POS);

				if (tokR.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE) != null) {
					termL = PoSTag.PoS_STAART;
				}

				if ((termL == cr.getTag1()) && (termR == cr.getTag2())) {
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							cr.getNewTag());
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED,
							Boolean.TRUE);
					if (incdesc) {
						tok
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED_DESC,
										"Contagged by "
												+ cr.getType().toString());
					}
				}
			}
		}
	}
}
