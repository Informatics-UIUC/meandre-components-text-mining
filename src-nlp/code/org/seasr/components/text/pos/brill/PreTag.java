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
import org.seasr.components.text.datatype.pos.PoSTag;
import org.seasr.components.text.pos.brill.support.Lexicon;
import org.meandre.core.*;
import org.meandre.annotations.*;


/**
 * <p>
 * Overview: This module takes a Document object and Lexicon and tags the terms
 * in the Document with POS tags from the Lexicon. In accordance with Brill's
 * method if a term is not in the Lexicon but it is capitalized then it is
 * marked as a NNP (proper noun) else a NN (noun).
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
 * Data Type Restrictions: The input document must have been tokenized.
 * </p>
 * 
 * <p>
 * Data Handling: This module will modify (as described above) the document
 * object that is input.
 * </p>
 * 
 * <p>
 * Scalability: This module makes one pass over the token list resulting in O(n)
 * time complexity per the number of tokens. Memory usage is proportional to the
 * number tokens.
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
		+ "This module takes a Document object and Lexicon and tags the terms in the "
		+ "Document with POS tags from the Lexicon.  In accordance with Brill's method "
		+ "if a term is not in the Lexicon but it is capitalized then it is marked as "
		+ "a NNP (proper noun) else a NN (noun). </p>"
		+ "<p>Detailed Description: <br>"
		+ "The Brill Tagging algorithm has three basic parts -- pre-tagging, lexical tagging, "
		+ "and contextual tagging.  The pre-tagger is purely a lookup of part-of-speech assignemnt "
		+ "in a dictionary.  It can be used without the other two stages.  The lexical and contextual "
		+ "tagging stages apply sets of rules to alter tag assignments.  The lexical and contextual taggers "
		+ "depend on the initial assignments of the pre-tagger.  Furthermore, it is assumed that lexical "
		+ "tagging will always precede contextual tagging.</p>"
		+ "<p>References: <br>"
		+ "Brill, Eric, Some Advances In Rule-Based Part of Speech Tagging, AAAI, 1994 </p>"
		+ "<p>Data Type Restrictions: <br>"
		+ "The input document must have been tokenized.</p>"
		+ "<p>Data Handling: <br>"
		+ "This module will modify (as described above) the document object that is input.</p>"
		+ "<p>Scalability: <br>"
		+ "This module makes one pass over the token list resulting in linear time complexity "
		+ "per the number of tokens.  Memory usage is proportional to the number tokens.</p>"
		+ "<p>Trigger Criteria: <br>" + "Standard.</p>", 
		
		name = "PreTag", tags = "nlp text brill pos", firingPolicy = Component.FiringPolicy.any)
public class PreTag implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private int m_docsProcessed = 0;
	private long m_start = 0;
	private Lexicon m_lex = null;
	private List<Document> _docs = null;

	private static Logger _logger = Logger.getLogger("PreTag");

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";
	private boolean _verbose = false;

	@ComponentProperty(description = "Show progress? A boolean value (true or false).", name = "show_progress", defaultValue = "false")
	final static String DATA_PROPERTY_SHOW_PROGRESS = "show_progress";
	private boolean _showProg = false;

	@ComponentProperty(description = "Print increment.", name = "print_increment", defaultValue = "250")
	final static String DATA_PROPERTY_PRINT_INCREMENT = "print_increment";
	private int _printInc = -1;
	
	@ComponentProperty(description = "Include tag description? A boolean value (true or false).", name = "include_description", defaultValue = "false")
	final static String DATA_PROPERTY_INCLUDE_DESC = "include_description";
	private boolean _incDesc = false;

	// io
	
	@ComponentInput(description = "Lexicon object.", name = "lexicon")
	public final static String DATA_INPUT_LEXICON = "lexicon";

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

	public boolean getShowProgress(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_SHOW_PROGRESS);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getIncludeDescription(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_INCLUDE_DESC);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public int getPrintIncrement(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_PRINT_INCREMENT);
		return Integer.parseInt(s);
	}

	// ================
	// Constructor(s)
	// ================
	public PreTag() {
	}

	// ================
	// Public Methods
	// ================

	public void initialize(ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
		m_docsProcessed = 0;
		_docs = new ArrayList<Document>();
		m_start = System.currentTimeMillis();
		
		// props ==============================
		_verbose = this.getVerbose(ccp);
		_incDesc = this.getIncludeDescription(ccp);
		_printInc = this.getPrintIncrement(ccp);
		_showProg = this.getShowProgress(ccp);
		//=====================================
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();
		if (getShowProgress(ccp) || getVerbose(ccp)) {
			_logger.info("\nEND EXEC -- Pretag -- Docs Processed: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}
		m_docsProcessed = 0;
		m_lex = null;
		_docs.clear();
		_docs = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meandre.core.ExecutableComponent#execute(org.meandre.core.ComponentContext)
	 */
	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		_logger.fine("execute() called");


		int tagsFoundInLex = 0;
		int tagsSetNNP = 0;
		int tagsSetNN = 0;
		int tagsSetCD = 0;
		int tagsSetSYM = 0;

		try {
			if (ctx.isInputAvailable(DATA_INPUT_LEXICON)) {
				Object o = ctx.getDataComponentFromInput(DATA_INPUT_LEXICON);
				m_lex = (Lexicon) o;
			}

			if (ctx.isInputAvailable(DATA_INPUT_DOCUMENT)) {
				_docs.add((Document) ctx
						.getDataComponentFromInput(DATA_INPUT_DOCUMENT));
			}
			if ((m_lex != null) && (!_docs.isEmpty())) {

				for (int i = 0, n = _docs.size(); i < n; i++) {

					Document doc = _docs.get(i);

					AnnotationSet annots = doc.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);

					for (Iterator<Annotation> iter = annots.iterator(); iter
							.hasNext();) {
						Annotation tok = iter.next();
						if (tok.getType().equals(
								AnnotationConstants.TOKEN_ANNOT_TYPE)) {
							String img = (String) tok.getContent(doc);
							PoSTag[] tags = m_lex.getTagsForTerm(img);
							if (tags.length > 0) {
								tok
										.getFeatures()
										.put(
												AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
												tags[0].toString());
								if (_incDesc) {
									tok
											.getFeatures()
											.put(
													AnnotationConstants.TOKEN_ANNOT_FEAT_PRETAGGED_DESC,
													"Pretagged lookup as: "
															+ tags[0]
																	.toString());
								}
								tok
										.getFeatures()
										.put(
												AnnotationConstants.TOKEN_ANNOT_FEAT_PRETAGGED_BOOL,
												Boolean.TRUE.toString());
								tagsFoundInLex++;
							} else if (Character.isDigit(img.charAt(0))) {
								tok
										.getFeatures()
										.put(
												AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
												PoSTag.PoS_CD.toString());
								if (_incDesc) {
									tok
											.getFeatures()
											.put(
													AnnotationConstants.TOKEN_ANNOT_FEAT_PRETAGGED_DESC,
													"Pretagged default as: CD");
								}
								tagsSetCD++;
							} else if (!Character.isLetter(img.charAt(0))) {
								tok
										.getFeatures()
										.put(
												AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
												PoSTag.PoS_SYM.toString());
								if (_incDesc) {
									tok
											.getFeatures()
											.put(
													AnnotationConstants.TOKEN_ANNOT_FEAT_PRETAGGED_DESC,
													"Pretagged default as: SYM");
								}
								tagsSetSYM++;
							} else if (Character.isLetter(img.charAt(0))) {
								tok
										.getFeatures()
										.put(
												AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
												PoSTag.PoS_NNP.toString());
								if (_incDesc) {
									tok
											.getFeatures()
											.put(
													AnnotationConstants.TOKEN_ANNOT_FEAT_PRETAGGED_DESC,
													"Pretagged default as: NNP");
								}
								tagsSetNNP++;
							} else {
								tok
										.getFeatures()
										.put(
												AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
												PoSTag.PoS_NN.toString());
								if (_incDesc) {
									tok
											.getFeatures()
											.put(
													AnnotationConstants.TOKEN_ANNOT_FEAT_PRETAGGED_DESC,
													"Pretagged default as NN");
								}
								tagsSetNN++;
							}
						}
					}

					if (_verbose) {
						_logger.info("\n\nDocument contained "
								+ doc.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS).get(
										AnnotationConstants.TOKEN_ANNOT_TYPE)
										.size() + " tokens.");
						_logger.info(tagsFoundInLex
								+ " Tags were found in Lexicon.");
						_logger.info(tagsSetNNP
								+ " Tags were set to proper nouns.");
						_logger.info(tagsSetNN
								+ " Tags were set to regular nouns.\n\n");
						_logger.info(tagsSetCD
								+ " Tags were set to cardinal numbers.\n\n");
						_logger.info(tagsSetSYM
								+ " Tags were set to symbols.\n\n");
					}

					ctx.pushDataComponentToOutput(DATA_OUTPUT_DOCUMENT, doc);
					m_docsProcessed++;

					if (_showProg) {
						if (Math.IEEEremainder(m_docsProcessed,
								_printInc) == 0) {
							_logger.info("Pretag -- Docs Processed: "
									+ m_docsProcessed);
						}
					}
				}
				_docs.clear();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: PreTag.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

}
