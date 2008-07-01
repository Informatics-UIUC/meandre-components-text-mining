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
import org.seasr.components.text.datatype.corpora.FeatureMap;
import org.seasr.components.text.pos.brill.support.ContextRule;
import org.seasr.components.text.pos.brill.support.DocTokMap;
import org.seasr.components.text.pos.brill.support.Lexicon;
import org.meandre.core.*;
import org.meandre.annotations.*;


/**
 * 
 * <p>
 * Overview: This module takes a Document object with initial part-of-speech
 * tags and a set of contextual tagging refinement rules and alters the
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
		+ "and a set of contextual tagging refinement rules and alters the part-of-speech tags in the "
		+ "Document.</p>"
		+ "<p>Detailed Description: <br>"
		+ "The Brill Tagging algorithm has three basic parts -- pre-tagging, lexical tagging, "
		+ "and contextual tagging.  The pre-tagger is purely a lookup of part-of-speech assignemnt "
		+ "in a dictionary.  It can be used without the other two stages.  The lexical and contextual "
		+ "tagging stages apply sets of rules to alter tag assignments.  The lexical and contextual taggers "
		+ "depend on the initial assignments of the pre-tagger.  Furthermore, it is assumed that lexical "
		+ "tagging will always precede contextual tagging.</P"
		+ "<p>References: <br>"
		+ "Brill, Eric, Some Advances In Rule-Based Part of Speech Tagging, AAAI, 1994 </p>"
		+ "<p>Data Type Restrictions: <br>"
		+ "The input document must have been tokenized and must have initial part-of-speech assignments.</p>"
		+ "<p>Data Handling: <br>"
		+ "This module will modify (as described above) the document object that is input.</p>"
		+ "<p>Scalability: <br>"
		+ "This module makes one pass over the token list resulting in linear time complexity "
		+ "per the number of tokens.  Memory usage is proportional to the number tokens.</p>"
		+ "<p>Trigger Criteria: <br>" + "Standard.</p>", 
		
		name = "ConTag", tags = "text brill pos nlp", firingPolicy = Component.FiringPolicy.any)
public class ConTag implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private long m_docsProcessed = 0;
	private long m_start = 0;
	private Lexicon m_lex = null;
	private Object[] m_rules = null;
	private List<Document> _docs = null;

	// ============
	// Properties
	// ============

	private static Logger _logger = Logger.getLogger("ConTag");

	// props
	
	@ComponentProperty(description = "Verbose output?", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Show progress?", name = "show_progress", defaultValue = "false")
	final static String DATA_PROPERTY_SHOW_PROGRESS = "show_progress";

	@ComponentProperty(description = "Print increment.", name = "print_increment", defaultValue = "250")
	final static String DATA_PROPERTY_PRINT_INCREMENT = "print_increment";

	@ComponentProperty(description = "Include tag description?", name = "include_description", defaultValue = "false")
	final static String DATA_PROPERTY_INCLUDE_DESC = "include_description";

	@ComponentProperty(description = "Restrict tag movement?", name = "restrict_tag_movement", defaultValue = "true")
	final static String DATA_PROPERTY_RESTRICT_TAG_MOVEMENT = "restrict_tag_movement";

	@ComponentProperty(description = "Remove token map from document?", name = "remove_token_map", defaultValue = "true")
	final static String DATA_PROPERTY_REMOVE_TOKEN_MAP = "remove_token_map";

	// io
	
	@ComponentInput(description = "Lexicon object.", name = "lexicon")
	public final static String DATA_INPUT_LEXICON = "lexicon";

	@ComponentInput(description = "Contextual rule object.", name = "contextual_rules")
	public final static String DATA_INPUT_CONTEXTUAL_RULES = "contextual_rules";

	@ComponentInput(description = "Document object.", name = "document")
	public final static String DATA_INPUT_DOCUMENT = "document";

	@ComponentOutput(description = "Document object.", name = "document")
	public final static String DATA_OUTPUT_DOCUMENT = "document";

	static public void main(String[] args) {

//		// get a flow builder instance
//		FlowBuilderAPI flowBuilder = new FlowBuilderAPI();
//		// get a flow object
//		WorkingFlow wflow = flowBuilder.newWorkingFlow("test");
//		// add a component
//		String pushString = wflow
//				.addComponent("org.seasr.meandre.components.io.PushString");
//		// set a component property
//		wflow.setComponentInstanceProp(pushString, "string",
//				"c:/tmp/ThreeLives.txt");
//		// add another component
//		String reader = wflow
//				.addComponent("org.seasr.meandre.components.t2k.io.file.TextFileToDoc");
//
//		// make a connection between two components
//		wflow.connectComponents(pushString, "output_string", reader,
//				TextFileToDoc.DATA_INPUT_FILE_NAME);
//
//		// add another component
//		String sentdetector = wflow
//				.addComponent("org.seasr.meandre.components.t2k.sentence.opennlp.OpenNLP_SentenceDetect");
//
//		// make a connection between two components
//		wflow.connectComponents(reader, TextFileToDoc.DATA_OUTPUT_FILE_DOC,
//				sentdetector, OpenNLP_SentenceDetect.DATA_INPUT_DOC_IN);
//
//		// add another component
//		String tokenizer = wflow
//				.addComponent("org.seasr.meandre.components.t2k.tokenize.opennlp.OpenNLP_Tokenizer");
//
////		// set a component property
////		wflow.setComponentInstanceProp(tokenizer, "verbose", "true");
////
////		// set a component property
////		wflow.setComponentInstanceProp(sentdetector, "verbose", "true");
//
//		// make a connection between two components
//		wflow.connectComponents(sentdetector,
//				OpenNLP_SentenceDetect.DATA_OUTPUT_DOC_OUT, tokenizer,
//				OpenNLP_Tokenizer.DATA_INPUT_DOC_IN);
//
//		// add another component
//		String pretag = wflow
//				.addComponent("org.seasr.meandre.components.t2k.pos.brill.PreTag");
//
//		// make a connection between two components
//		wflow.connectComponents(tokenizer,
//				OpenNLP_Tokenizer.DATA_OUTPUT_DOC_OUT, pretag,
//				PreTag.DATA_INPUT_DOCUMENT);
//
//		// add another component
//		String lextag = wflow
//				.addComponent("org.seasr.meandre.components.t2k.pos.brill.LexTag");
//
//		// make a connection between two components
//		wflow.connectComponents(pretag,
//				PreTag.DATA_OUTPUT_DOCUMENT, lextag,
//				LexTag.DATA_INPUT_DOCUMENT);
//
//		// add another component
//		String contag = wflow
//				.addComponent("org.seasr.meandre.components.t2k.pos.brill.ConTag");
//
//		// make a connection between two components
//		wflow.connectComponents(lextag,
//				LexTag.DATA_OUTPUT_DOCUMENT, contag,
//				ConTag.DATA_INPUT_DOCUMENT);		
//
//		// add another component
//		String contagruleread = wflow
//				.addComponent("org.seasr.meandre.components.t2k.pos.brill.ContextRuleJarReader");
//
//		// add another component
//		String lexiconread = wflow
//				.addComponent("org.seasr.meandre.components.t2k.pos.brill.LexiconJarReader");
//
//		// add another component
//		String lexruleread = wflow
//				.addComponent("org.seasr.meandre.components.t2k.pos.brill.LexRuleJarReader");
//
//		// add another component
//		String fork = wflow
//				.addComponent("org.seasr.meandre.components.control.ForkX5");		
//
//		// make a connection between two components
//		wflow.connectComponents(lexiconread,
//				LexiconJarReader.DATA_OUTPUT_LEXICON, fork,
//				ForkX5.s_INPUT_1);		
//
//		// make a connection between two components
//		wflow.connectComponents(fork,
//				ForkX5.s_OUTPUT_1, pretag,
//				PreTag.DATA_INPUT_LEXICON);		
//
//		// make a connection between two components
//		wflow.connectComponents(fork,
//				ForkX5.s_OUTPUT_2, lextag,
//				LexTag.DATA_INPUT_LEXICON);		
//
//		// make a connection between two components
//		wflow.connectComponents(fork,
//				ForkX5.s_OUTPUT_3, contag,
//				ConTag.DATA_INPUT_LEXICON);		
//
//		// make a connection between two components
//		wflow.connectComponents(lexruleread,
//				LexRuleJarReader.DATA_OUTPUT_LEXICAL_RULES, lextag,
//				LexTag.DATA_INPUT_LEXICAL_RULES);		
//
//		// make a connection between two components
//		wflow.connectComponents(contagruleread,
//				ContextRuleJarReader.DATA_OUTPUT_CONTEXTUAL_RULES, contag,
//				ConTag.DATA_INPUT_CONTEXTUAL_RULES);				
//
//		// set a component property
//		wflow.setComponentInstanceProp(contag, "verbose", "true");
//
////		// set a component property
////		wflow.setComponentInstanceProp(pretag, "verbose", "true");
//
//		// execute the flow specifying that we want a web UI displayed
//		flowBuilder.execute(wflow, false);
//
//		// For some reason the process does not end without a forced exit.
//		System.exit(0);
//
	}

	// ================
	// Constructor(s)
	// ================
	public ConTag() {
	}

	// ================
	// Public Methods
	// ================

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

	public boolean getRestrictTagMovement(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_RESTRICT_TAG_MOVEMENT);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getRemoveTokenMap(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_REMOVE_TOKEN_MAP);
		return Boolean.parseBoolean(s.toLowerCase());
	}

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
		if (getVerbose(ccp) || this.getShowProgress(ccp)) {
			_logger.info("\nEND EXEC -- LexTag -- Docs Processed: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}
		m_docsProcessed = 0;
		_docs.clear();
		_docs = null;
		m_rules = null;
		m_lex = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meandre.core.ExecutableComponent#execute(org.meandre.core.ComponentContext)
	 */
	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		_logger.fine("execute() called");
		int contagged = 0;
		int tokensprocessed = 0;
		try {

			if (ctx.isInputAvailable(DATA_INPUT_LEXICON)) {
				Object o = ctx.getDataComponentFromInput(DATA_INPUT_LEXICON);
				m_lex = (Lexicon) o;
			}

			if (ctx.isInputAvailable(DATA_INPUT_CONTEXTUAL_RULES)) {
				Object o = ctx
						.getDataComponentFromInput(DATA_INPUT_CONTEXTUAL_RULES);
				m_rules = (Object[]) o;
			}

			if (ctx.isInputAvailable(DATA_INPUT_DOCUMENT)) {
				_docs.add((Document) ctx
						.getDataComponentFromInput(DATA_INPUT_DOCUMENT));
			}

			if ((m_lex != null) && (m_rules != null) && (!_docs.isEmpty())) {

				for (int i = 0, n = _docs.size(); i < n; i++) {

					Document doc = _docs.get(i);

					DocTokMap dtmap = (DocTokMap) doc.getFeatures().get(
							DocumentConstants.BRILL_TOK_MAP);
					if (dtmap == null) {
						throw new Exception(
								"No DocTokMap found in document object.");
					}

					if (getVerbose(ctx)) {
						_logger.info("Begin ConTag");
					}

					AnnotationSet annots = doc.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);

					/**
					 * Loop through each rule and apply it to every document
					 * word in turn.
					 */
					for (int x = 0, y = m_rules.length; x < y; x++) {
						for (Iterator<Annotation> iter = annots.iterator(); iter
								.hasNext();) {
							Annotation tok = iter.next();
							((ContextRule) m_rules[x]).applyRule(doc, tok,
									getRestrictTagMovement(ctx), m_lex, dtmap,
									getIncludeDescription(ctx));
							if ((x == (y - 1)) && (tok
									.getFeatures()
									.get(
											AnnotationConstants.TOKEN_ANNOT_FEAT_CONTAGGED) != null)) {
								contagged++;
							}
						}
					}

					if (doc.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS).get(
							AnnotationConstants.TOKEN_ANNOT_TYPE) != null) {
						tokensprocessed = doc.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS).get(
								AnnotationConstants.TOKEN_ANNOT_TYPE).size();
					}
					if (getVerbose(ctx)) {
						_logger.info("Out of " + tokensprocessed
								+ " words that were applied to the rule set, ");
						_logger.info(contagged
								+ " had at least one contextual rule applied.");
						_logger.info("End ConTag");
					}

					if (getVerbose(ctx)) {
						
						AnnotationSet toks = doc.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);

						StringBuffer buff = new StringBuffer();
						for (Iterator<Annotation> itty = toks.getAllSortedAsArrayList().iterator(); itty.hasNext();){
							Annotation tok = itty.next();
							buff.append("Token: " + tok.getContent(doc));
							FeatureMap fm = tok.getFeatures();
							if (fm.isEmpty()){
								_logger.info(buff.toString());
								buff.delete(0, buff.length());
							} else {
								for (Iterator<String> itty2 = fm.keySet().iterator(); itty2.hasNext();){
									String key = itty2.next();
									String val = fm.get(key).toString();
									buff.append("  <" + key + ", " + val + ">");
								}
								_logger.info(buff.toString());
								buff.delete(0, buff.length());
							}
						}
					}
					
					
					if (getRemoveTokenMap(ctx)) {
						doc.getFeatures().remove(
								DocumentConstants.BRILL_TOK_MAP);
					}
					ctx.pushDataComponentToOutput(DATA_OUTPUT_DOCUMENT, doc);
					m_docsProcessed++;

					if (getShowProgress(ctx)) {
						if (Math.IEEEremainder(m_docsProcessed, getPrintIncrement(ctx)) == 0) {
							_logger.info("Contag -- Docs Processed: "
									+ m_docsProcessed);
							_logger.info("Contag -- Number of Rules: "
									+ m_rules.length);
						}
					}
				}
				_docs.clear();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: ConTagger.execute()");
			throw new ComponentExecutionException(ex);
		}
	}
}
