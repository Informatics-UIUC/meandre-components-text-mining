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

package org.seasr.components.text.gate.tokenize;

// ==============
// Java Imports
// ==============
import java.util.logging.Logger;

// ===============
// Other Imports
// ===============
import gate.*;
import gate.creole.splitter.SentenceSplitter;
import gate.creole.tokeniser.*;

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
 * <p><b>Overview</b>: <br>
 * Performs tokenization on a given GATE document. 
 * Stores the token annotations in a annotation set 
 * of the document.</p>
 * <p><b>Detailed Description</b>: <br>
 * Given a document object in the GATE framework, 
 * this component will perform tokenization using the 
 * given tokenizer and grammar rules. 
 * The computed tokens (ie, annotations) will be 
 * stored in an annotation set associated with the 
 * document.</p>
 * <p>The particular annotation set to use can be 
 * named by the user. If the computed tokens are to 
 * be used by other GATE components, either use a 
 * name the other modules will recognize, or leave it blank 
 * so the always-present default annotation set 
 * is used.</p>
 * 
 * @author D. Searsmith
 * 
 * TODO: Testing, Unit Tests
 */
@Component(creator = "Duane Searsmith",

description = "<p><b>Overview</b>: <br>"
		+ "Performs tokenization on a given GATE document. "
		+ "Stores the token annotations in a annotation set "
		+ "of the document.</p>" 
		+ "<p><b>Detailed Description</b>: <br>"
		+ "Given a document object in the GATE framework, "
		+ "this module will perform tokenization using the "
		+ "given tokenizer and grammar rules. "
		+ "The computed tokens (ie, annotations) will be "
		+ "stored in an annotation set associated with the " 
		+ "document.</p>"
		+ "<p>The particular annotation set to use can be "
		+ "named by the user. "
		+ "If the computed tokens are to be used by other "
		+ "GATE modules, either use a name the other "
		+ "modules will recognize, or leave it blank "
		+ "so the always-present default annotation set " 
		+ "is used.</p>",

name = "GATE_Tokenizer", tags = "text gate token tokenize document")
public class GATE_Tokenizer implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private static Logger _logger = Logger.getLogger("GATE_Tokenizer");

	private int m_docsProcessed = 0;
	private long m_start = 0;

	private DefaultTokeniser _toker = null;

	// props

	@ComponentProperty(description = "Verbose output?", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Encoding type of the document.", name = "document_encoding", defaultValue = "UTF-8")
	final static String DATA_PROPERTY_DOCUMENT_ENCODING = "document_encoding";

	@ComponentProperty(description = "URL of the tokenizer rules file in GATE.", name = "tokenizer_rules_url", defaultValue = "gate:/creole/tokeniser/DefaultTokeniser.rules")
	final static String DATA_PROPERTY_TOKENIZER_RULES_URL = "tokenizer_rules_url";

	@ComponentProperty(description = "URL of the grammar rules file in GATE.", name = "grammar_rules_url", defaultValue = "")
	final static String DATA_PROPERTY_GRAMMAR_RULES_URL = "grammar_rules_url";

	@ComponentProperty(description = "Name of the Annotation Set to find the tokens "
			+ "in. Leave blank for default.", name = "annotation_set_name", defaultValue = "")
	final static String DATA_PROPERTY_ANNOTATION_SET_NAME = "annotation_set_name";

	@ComponentProperty(description = "Show progress?", name = "show_progress", defaultValue = "false")
	final static String DATA_PROPERTY_SHOW_PROGRESS = "show_progress";

	@ComponentProperty(description = "This property sets the number of documents to process "
			+ "before a print statement is generated to announce "
			+ "the total number records processed to that point.", name = "print_increment", defaultValue = "250")
	final static String DATA_PROPERTY_PRINT_INCREMENT = "show_progprint_incrementress";

	// io

	@ComponentInput(description = "Input GATE document.", name = "document_in")
	public final static String DATA_INPUT_DOC_IN = "gate_document_in";

	@ComponentOutput(description = "Output GATE document.", name = "document_out")
	public final static String DATA_OUTPUT_DOC_OUT = "gate_document_out";

	// ================
	// Constructor(s)
	// ================
	public GATE_Tokenizer() {
	}

	// ================
	// Public Methods
	// ================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public String getDocumentEncoding(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_DOCUMENT_ENCODING);
		return s;
	}

	public String getTokenizerRulesURL(ComponentContextProperties ccp) {
		String s = ccp.getProperty(getTokenizerRulesURL(ccp));
		return s;
	}

	public String getGrammarRulesURL(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_GRAMMAR_RULES_URL);
		return s;
	}

	public String getAnnotationSetName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_ANNOTATION_SET_NAME);
		return s;
	}

	public boolean getShowProgress(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_SHOW_PROGRESS);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public int getPrintIncrement(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_PRINT_INCREMENT);
		return Integer.parseInt(s);
	}

	public void initialize(ComponentContextProperties ccp)
			throws ComponentExecutionException {
		_logger.fine("initialize() called");
		GATEInitialiser.init();

		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();

		try {
			FeatureMap params = Factory.newFeatureMap();
			params.put(SentenceSplitter.SPLIT_ENCODING_PARAMETER_NAME,
					getDocumentEncoding(ccp));

			// try to convert the tokrules if it starts with gate:
			String newTokRules = null;
			String currTokRules = this.getTokenizerRulesURL(ccp);
			if (currTokRules.startsWith(GATEInitialiser.GATE_PREFIX)) {
				newTokRules = GATEInitialiser.getResourceURL(currTokRules);
			}
			if (newTokRules != null) {
				_logger
						.info("GATE_Tokenizer: gate: URLs are deprecated.  Converting "
								+ currTokRules + " to " + newTokRules);
				params.put(
						DefaultTokeniser.DEF_TOK_TOKRULES_URL_PARAMETER_NAME,
						newTokRules);
			} else {
				params.put(
						DefaultTokeniser.DEF_TOK_TOKRULES_URL_PARAMETER_NAME,
						currTokRules);
			}
			// try to convert the grammar rules if it starts with gate:
			String newGramRules = null;
			String currGramRules = this.getGrammarRulesURL(ccp);
			if (currGramRules.startsWith(GATEInitialiser.GATE_PREFIX)) {
				newGramRules = GATEInitialiser.getResourceURL(currGramRules);
			}
			if (newGramRules != null) {
				_logger
						.info("GATE_Tokenizer: gate: URLs are deprecated.  Converting "
								+ currGramRules + " to " + newGramRules);
				params.put(
						DefaultTokeniser.DEF_TOK_GRAMRULES_URL_PARAMETER_NAME,
						newGramRules);
			} else {
				params.put(
						DefaultTokeniser.DEF_TOK_GRAMRULES_URL_PARAMETER_NAME,
						currGramRules);
			}
			_toker = (DefaultTokeniser) Factory.createResource(
					"gate.creole.tokeniser.DefaultTokeniser", params);
		} catch (Exception e) {
			_logger.info("GATE_Tokenizer.initialise() -- " + e);
			e.printStackTrace();
			throw new ComponentExecutionException(e);
		}
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();

		if (this.getVerbose(ccp)) {
			System.out.println("\nEND EXEC -- GATE_Tokenizer -- Docs Ouput: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}

		m_docsProcessed = 0;
		_toker = null;
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		try {
			gate.Document doc = (gate.Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);

			_toker.setDocument(doc);
			if (!(getAnnotationSetName(ctx).trim().length() == 0)) {
				_toker.setAnnotationSetName(getAnnotationSetName(ctx));
			}
			_toker.execute();

			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, doc);
			m_docsProcessed++;

			if (this.getShowProgress(ctx)) {
				if (Math.IEEEremainder(m_docsProcessed, this
						.getPrintIncrement(ctx)) == 0) {
					System.out.println("GATE_Tokenizer -- Docs Processed: "
							+ m_docsProcessed);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: GATE_Tokenizer.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

}
