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

package org.seasr.components.text.pos.brill;

//==============
// Java Imports
//==============

import java.util.*;
import java.util.logging.*;

//===============
// Other Imports
//===============

//import org.meandre.tools.components.*;
//import org.meandre.tools.components.FlowBuilderAPI.WorkingFlow;

import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.DocumentConstants;
import org.seasr.components.text.pos.brill.support.DocTokMap;
import org.seasr.components.text.pos.brill.support.LexRule;
import org.seasr.components.text.pos.brill.support.Lexicon;
import org.meandre.core.*;
import org.meandre.annotations.*;


/**
 * "
 * <p>
 * Overview: This module takes a Document object with initial part-of-speech
 * tags and a set of lexical tagging refinement rules and alters the
 * part-of-speech tags in the Document.
 * </p>
 * 
 * <p>
 * Detailed Description: The Brill Tagging algorithm has three basic parts --
 * pre-tagging, lexical tagging, and contextual tagging. The pre-tagger is
 * purely a lookup of part-of-speech assignemnt in a dictionary. It can be used
 * without the other two stages. The lexical and contextual tagging stages apply
 * sets of rules to alter tag assignments. The lexical and contextual taggers
 * depend on the initial assignments of the pre-tagger. Furthermore, it is
 * assumed that lexical tagging will always precede contextual tagging.
 * </p>
 * 
 * <p>
 * References: Brill, Eric, <i>Some Advances In Rule-Based Part of Speech
 * Tagging</i>, AAAI, 1994
 * </p>
 * 
 * <p>
 * Data Type Restrictions: The input document must have been tokenized and must
 * have initial part-of-speech assignments.
 * </p>
 * 
 * <p>
 * Data Handling: This module will modify (as described above) the document
 * object that is input.
 * </p>
 * 
 * <p>
 * Scalability: This module makes one pass over the token list resulting in
 * linear time complexity per the number of tokens. Memory usage is proportional
 * to the number tokens.
 * </p>
 * 
 * <p>
 * Trigger Criteria: Standard.
 * </p>
 * 
 * @author D. Searsmith
 * 
 * TODO: Testing, Unit Testing
 * 
 */

@Component(creator = "Duane Searsmith", 
		
		description = "<p>Overview: <br>"
		+ "This module takes a Document object with initial part-of-speech tags "
		+ "and a set of lexical tagging refinement rules and alters the part-of-speech tags in the "
		+ "Document.</p>"
		+ "<p>Detailed Description: <br>"
		+ "The Brill Tagging algorithm has three basic parts -- pre-tagging, lexical tagging, "
		+ "and contextual tagging.  The pre-tagger is purely a lookup of part-of-speech assignemnt "
		+ "in a dictionary.  It can be used without the other two stages.  The lexical and contextual "
		+ "tagging stages apply sets of rules to alter tag assignments.  The lexical and contextual taggers "
		+ "depend on the initial assignments of the pre-tagger.  Furthermore, it is assumed that lexical "
		+ "tagging will always precede contextual tagging.</p>"
		+ "<p>References: <br>"
		+ "Brill, Eric, <i>Some Advances In Rule-Based Part of Speech Tagging</i>, AAAI, 1994 </p>"
		+ "<p>Data Type Restrictions: <br>"
		+ "The input document must have been tokenized and must have initial part-of-speech assignments.>/p>"
		+ "<p>Data Handling: <br>"
		+ "This module will modify (as described above) the document object that is input.</p>"
		+ "<p>Scalability: <br>"
		+ "This module makes one pass over the token list resulting in linear time complexity "
		+ "per the number of tokens.  Memory usage is proportional to the number tokens.</p>"
		+ "<p>Trigger Criteria: <br>Standard.</p>", 
		
		name = "LexTag", tags = "text brill pos nlp", firingPolicy = Component.FiringPolicy.any)
public class LexTag implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private int m_docsProcessed = 0;
	private long m_start = 0;
	private Lexicon m_lex = null;
	private Object[] m_rules = null;
	private List<Document> _docs = null;

	private static Logger _logger = Logger.getLogger("LexTag");

	// props
	
	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Include tag description? A boolean value (true or false).", name = "include_description", defaultValue = "false")
	final static String DATA_PROPERTY_INCLUDE_DESC = "include_description";

	// io
	
	@ComponentInput(description = "Lexicon object.", name = "lexicon")
	public final static String DATA_INPUT_LEXICON = "lexicon";

	@ComponentInput(description = "Lexical rule object.", name = "lexical_rules")
	public final static String DATA_INPUT_LEXICAL_RULES = "lexical_rules";

	@ComponentInput(description = "Document object.", name = "document")
	public final static String DATA_INPUT_DOCUMENT = "document";

	@ComponentOutput(description = "Document object.", name = "document")
	public final static String DATA_OUTPUT_DOCUMENT = "document";

	// ============
	// Properties
	// ============

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getIncludeDescription(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_INCLUDE_DESC);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	// ================
	// Constructor(s)
	// ================
	public LexTag() {
	}

	// ================
	// Public Methods
	// ================

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meandre.core.ExecutableComponent#initialize(org.meandre.core.ComponentContextProperties)
	 */
	public void initialize(ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
		m_docsProcessed = 0;
		_docs = new ArrayList<Document>();
		m_start = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meandre.core.ExecutableComponent#dispose(org.meandre.core.ComponentContextProperties)
	 */
	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();
		if (getVerbose(ccp)) {
			_logger.info("\nEND EXEC -- LexTag -- Docs Processed: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}
		m_docsProcessed = 0;
		_docs.clear();
		_docs = null;
		m_lex = null;
		m_rules = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meandre.core.ExecutableComponent#execute(org.meandre.core.ComponentContext)
	 */
	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		_logger.fine("execute() called");
		int lextagged = 0;
		int tokensprocessed = 0;
		
		// props ==============================
		boolean verbose = this.getVerbose(ctx);
		boolean incdesc = this.getIncludeDescription(ctx);
		//=====================================
		
		try {

			if (ctx.isInputAvailable(DATA_INPUT_LEXICON)) {
				Object o = ctx.getDataComponentFromInput(DATA_INPUT_LEXICON);
				m_lex = (Lexicon) o;
			}

			if (ctx.isInputAvailable(DATA_INPUT_LEXICAL_RULES)) {
				Object o = ctx
						.getDataComponentFromInput(DATA_INPUT_LEXICAL_RULES);
				m_rules = (Object[]) o;
			}

			if (ctx.isInputAvailable(DATA_INPUT_DOCUMENT)) {
				_docs.add((Document)ctx.getDataComponentFromInput(DATA_INPUT_DOCUMENT));
			}
			
			if ((m_lex != null) && (m_rules != null) && (!_docs.isEmpty())) {

				for (int i = 0, n = _docs.size(); i < n; i++) {

					Document doc = _docs.get(i);

					DocTokMap dtmap = new DocTokMap(doc);
					doc.getAuxMap().put(DocumentConstants.BRILL_TOK_MAP, dtmap);

					AnnotationSet annots = doc.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);

					if (verbose) {
						_logger.info("Begin LexTag");
					}
					for (int x = 0, y = m_rules.length; x < y; x++) {
						for (Iterator<Annotation> iter = annots.iterator(); iter
								.hasNext();) {
							Annotation tok = iter.next();
							if (tok.getType().equals(
									AnnotationConstants.TOKEN_ANNOT_TYPE)) {
								if (tok
										.getFeatures()
										.get(
												AnnotationConstants.TOKEN_ANNOT_FEAT_PRETAGGED_BOOL) == null) {
									((LexRule) m_rules[x]).applyRule(doc, tok,
											m_lex, dtmap,
											incdesc);
									if (tok
											.getFeatures()
											.get(
													AnnotationConstants.TOKEN_ANNOT_FEAT_LEXTAGGED_BOOL) != null) {
										lextagged++;
									}
								}
							}
						}
					}
					if (doc.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS).get(
							AnnotationConstants.TOKEN_ANNOT_TYPE) != null) {
						tokensprocessed = doc.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS).get(
								AnnotationConstants.TOKEN_ANNOT_TYPE).size();
					}
					if (verbose) {
						_logger
								.info("Out of "
										+ tokensprocessed
										+ " unknown words that were applied to the rule set, ");
						_logger.info(lextagged
								+ " had at least one lexical rule applied.");
						_logger.info("End LexicalTagger");
					}

					ctx.pushDataComponentToOutput(DATA_OUTPUT_DOCUMENT, doc);
					m_docsProcessed++;
				}
				_docs.clear();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: LexTag.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

}
