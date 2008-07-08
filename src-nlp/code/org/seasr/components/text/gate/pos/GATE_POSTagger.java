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

package org.seasr.components.text.gate.pos;

// ==============
// Java Imports
// ==============

import java.util.logging.Logger;

// ===============
// Other Imports
// ===============

import gate.*;
import gate.creole.*;

import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
import org.meandre.core.ExecutableComponent;
import org.seasr.components.text.gate.util.GATEInitialiser;

/**
 * <p>
 * <b>Overview</b>: <br>
 * Runs the GATE part-of-speech tagger on a given GATE document.
 * </p>
 * <p>
 * <b>Detailed Description</b>: <br>
 * Given a document object in the GATE framework, this component will annotate
 * existing tokens with their parts-of-speech using the given rules. The found
 * annotations will be stored in an annotation set associated with the document.
 * </p>
 * <p>
 * The tagger requires that the document be tokenized and sentences split
 * already. These tokens should be stored in the Input Annotation Set, which is
 * left to blank by default. The newly found annotations will be stored in the
 * same annotations (as new features).
 * </p>
 * <p>
 * Note that by leaving the Annotation Sets blank uses the default annotation
 * set, which is probably easier to manage within a flow.
 * </p>
 * 
 * @author D. Searsmith
 * 
 * TODO: Testing, Unit Tests
 */
@Component(creator = "Duane Searsmith",

description = "<p><b>Overview</b>: <br>"
		+ "Runs the GATE part-of-speech tagger on a given GATE "
		+ "document.</p>"

		+ "<p><b>Detailed Description</b>: <br>"
		+ "Given a document object in the GATE framework, "
		+ "this component will annotate existing tokens "
		+ "with their parts-of-speech using the given rules. "
		+ "The found annotations will be "
		+ "stored in an annotation set associated with the "
		+ "document.</p>"
		+ "<p>The tagger requires that the document "
		+ "be tokenized and sentences split already.  These tokens should be stored "
		+ "in the Input Annotation Set, which is left to blank "
		+ "by default. The newly found annotations will be stored in "
		+ "the same annotations (as new features). </p>"
		+ "<p>Note that by leaving the Annotation Sets blank "
		+ "uses the default annotation set, which is probably "
		+ "easier to manage within a flow.</p>",

name = "GATE_POSTagger", tags = "text gate pos tagger document")
public class GATE_POSTagger implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private static Logger _logger = Logger.getLogger("GATE_POSTagger");

	private POSTagger _tagger = null;

	private int m_docsProcessed = 0;
	private long m_start = 0;

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "URL of lexicon file in GATE.", name = "lexicon_url", defaultValue = "gate:/creole/heptag/lexicon")
	final static String DATA_PROPERTY_LEXICON_URL = "lexicon_url";

	@ComponentProperty(description = "URL for the parts-of-speech rules file in GATE.", name = "pos_rules_url", defaultValue = "gate:/creole/heptag/ruleset")
	final static String DATA_PROPERTY_POS_RULES_URL = "pos_rules_url";

	@ComponentProperty(description = "Name of the Annotation Set to find the tokens in.  "
			+ "Leave blank for default set. The new feature (the POS tag) is added to Token "
			+ "type annotations found in this set.", name = "token_annotation_set_name", defaultValue = "")
	final static String DATA_PROPERTY_TOKEN_ANNOTATION_SET_NAME = "token_annotation_set_name";

	@ComponentProperty(description = "Name of the Annotation Set that contains the Sentence "
			+ "type annotations. The tokens cannot be tagged if the Document was not processed "
			+ "with a Sentence Splitter.", name = "sentence_annotation_set_name", defaultValue = "")
	final static String DATA_PROPERTY_SENTENCE_ANNOTATION_SET_NAME = "sentence_annotation_set_name";

	// io

	@ComponentInput(description = "Input GATE document.", name = "document_in")
	public final static String DATA_INPUT_DOC_IN = "gate_document_in";

	@ComponentOutput(description = "Output GATE document.", name = "document_out")
	public final static String DATA_OUTPUT_DOC_OUT = "gate_document_out";

	// ===============
	// Constructor(s)
	// ===============

	public GATE_POSTagger() {
	}

	// ================
	// Public Methods
	// ================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public String getLexiconURL(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_LEXICON_URL);
		return s;
	}

	public String getPoSRulesURL(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_POS_RULES_URL);
		return s;
	}

	public String getTokenAnnotationSetName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TOKEN_ANNOTATION_SET_NAME);
		return s;
	}

	public String getSentenceAnnotationSetName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(getSentenceAnnotationSetName(ccp));
		return s;
	}

	public void initialize(ComponentContextProperties ccp)
			throws ComponentExecutionException {
		_logger.fine("initialize() called");
		GATEInitialiser.init();

		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();

		try {
			FeatureMap params = Factory.newFeatureMap();

			// if the m_lexURL is a gate: url, convert it
			String newLexUrl = null;
			String currLexURL = this.getLexiconURL(ccp);
			if (currLexURL.startsWith(GATEInitialiser.GATE_PREFIX)) {
				newLexUrl = GATEInitialiser.getResourceURL(currLexURL);
			}
			if (newLexUrl != null) {
				_logger
						.info("GATE_POSTagger: gate: URLs are deprecated.  Converting "
								+ currLexURL + " to " + newLexUrl);
				params.put(POSTagger.TAG_LEXICON_URL_PARAMETER_NAME, newLexUrl);
			} else {
				params
						.put(POSTagger.TAG_LEXICON_URL_PARAMETER_NAME,
								currLexURL);
			}

			// if the m_ruleURL is a gate: url, convert it
			String newRuleUrl = null;
			String currRuleURL = this.getPoSRulesURL(ccp);
			if (currRuleURL.startsWith(GATEInitialiser.GATE_PREFIX)) {
				newRuleUrl = GATEInitialiser.getResourceURL(currRuleURL);
			}
			if (newRuleUrl != null) {
				System.out
						.println("GATE_POSTagger: gate: URLs are deprecated.  Converting "
								+ currRuleURL + " to " + newRuleUrl);
				params.put(POSTagger.TAG_RULES_URL_PARAMETER_NAME, newRuleUrl);
			} else {
				params.put(POSTagger.TAG_RULES_URL_PARAMETER_NAME, currRuleURL);
			}

			params.put(POSTagger.TAG_INPUT_AS_PARAMETER_NAME, "");

			params.put(POSTagger.TAG_OUTPUT_AS_PARAMETER_NAME, "");

			_tagger = (POSTagger) Factory.createResource(
					"gate.creole.POSTagger", params);
		} catch (Exception e) {
			_logger.info("GATE_POSTagger.initialise() -- " + e);
			e.printStackTrace();
			throw new ComponentExecutionException(e);
		}
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();

		if (this.getVerbose(ccp)) {
			System.out.println("\nEND EXEC -- GATE_POSTagger -- Docs Ouput: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}

		m_docsProcessed = 0;
		_tagger = null;
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		try {
			gate.Document doc = (gate.Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);

			// making sure that there are tokens to tag
			AnnotationSet tokens;
			String tasName = this.getTokenAnnotationSetName(ctx);
			if (tasName == null
					|| tasName.trim().length() == 0) {
				tokens = doc.getAnnotations().get(
						ANNIEConstants.TOKEN_ANNOTATION_TYPE);
			} else
				tokens = doc.getAnnotations(tasName).get(
						ANNIEConstants.TOKEN_ANNOTATION_TYPE);

			// making sure that there are sentences. if the document is not
			// sentence split then pos tagging fails
			AnnotationSet sentences;
			String sasName = this.getSentenceAnnotationSetName(ctx);
			if (sasName == null
					|| sasName.trim().length() == 0) {
				sentences = doc.getAnnotations().get(
						ANNIEConstants.SENTENCE_ANNOTATION_TYPE);
			} else
				sentences = doc.getAnnotations(sasName).get(
						ANNIEConstants.SENTENCE_ANNOTATION_TYPE);

			if (tokens != null && sentences != null) {
				_tagger.setDocument(doc);
				_tagger.execute();
			} else {
				if (sentences == null){
					_logger.severe("GATE_POSTagger: Input Document must have Sentence type annotations "
									+ "in order to be part of speech tagged. Process the Document "
									+ "with a Sentence Splitter first.");
					throw new Exception("GATE_POSTagger: Input Document must have Sentence type annotations "
									+ "in order to be part of speech tagged. Process the Document "
									+ "with a Sentence Splitter first.");
				}
				if (tokens == null){
					_logger.info("GATE_POSTagger: Input Document has no Token type annotations - "
									+ "nothing to do.");
					throw new Exception("GATE_POSTagger: Input Document has no Token type annotations - "
							+ "nothing to do.");
				}
			}

			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, doc);
			m_docsProcessed++;

			if (this.getVerbose(ctx)) {
				if (Math.IEEEremainder(m_docsProcessed, 250) == 0) {
					System.out.println("GATE_POSTagger -- Docs Processed: "
							+ m_docsProcessed);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: GATE_POSTagger.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

}
