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

package org.seasr.components.text.gate.ie;

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
 * <p><b>Overview</b>: <br>
 * Runs the GATE Transducer on a given GATE document.</p>
 * <p><b>Detailed Description</b>: <br>
 * Given a document object in the GATE framework, 
 * this component will run the GATE Transducer on it. 
 * The found annotations will be stored in an annotation 
 * set associated with the document.</p>
 * <p>The transducer requires that the document 
 * be tokenized already. These tokens should be stored 
 * in the Input Annotation Set, which is left to blank 
 * by default.</p>
 * <p>The newly found annotations will be stored in 
 * the Output Annotation Set, also blank by default. 
 * Note that by leaving the Annotation Sets blank 
 * uses the default annotation set, which is probably 
 * easier to manage within a flow.</p>
 * 
 * @author D. Searsmith
 * 
 * TODO: Testing, Unit Tests
 */
@Component(creator = "Duane Searsmith",

description = "<p><b>Overview</b>: <br>"
 + "Runs the GATE Transducer on a given GATE document.</p>"
 + "<p><b>Detailed Description</b>: <br>"
 + "Given a document object in the GATE framework, "
 + "this component will run the GATE Transducer on it. "
 + "The found annotations will be "
 + "stored in an annotation set associated with the "
 + "document.</p>"
 + "<p>The transducer requires that the document "
 + "be tokenized already. These tokens should be stored "
 + "in the Input Annotation Set, which is left to blank "
 + "by default.</p>"
 + "<p>The newly found annotations will be stored in "
 + "the Output Annotation Set, also blank by default."
 + "Note that by leaving the Annotation Sets blank "
 + "uses the default annotation set, which is probably "
 + "easier to manage within a flow.</p>",

name = "GATE_Transducer", tags = "text gate sentence splitter document")
public class GATE_Transducer implements ExecutableComponent {
	// ==============
	// Data Members
	// ==============

	private static Logger _logger = Logger.getLogger("GATE_Transducer");
	private int m_docsProcessed = 0;
	private long m_start = 0;

	private AbstractLanguageAnalyser _trans = null;

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Encoding type of the document.", name = "document_encoding", defaultValue = "UTF-8")
	final static String DATA_PROPERTY_DOCUMENT_ENCODING = "document_encoding";

	@ComponentProperty(description = "URL of grammar rules file in GATE.", name = "grammar_rules_url", defaultValue = "gate:/creole/transducer/NE/main.jape")
	final static String DATA_PROPERTY_GRAMMAR_RULES_URL = "grammar_rules_url";

	@ComponentProperty(description = "Name of the Annotation Set to find the tokens in.  "
			+ "Leave blank for default set.", name = "token_annotation_set_name", defaultValue = "")
	final static String DATA_PROPERTY_TOKEN_ANNOTATION_SET_NAME = "token_annotation_set_name";

	@ComponentProperty(description = "Name of the Transduction Annotation Set to store rsults in. "
			+ " Leave blank for default.", name = "trans_annotation_set_name", defaultValue = "")
	final static String DATA_PROPERTY_TRANS_ANNOTATION_SET_NAME = "trans_annotation_set_name";

	@ComponentProperty(description = "Taransducer Java class name.", name = "trans_class_name", defaultValue = "gate.creole.Transducer")
	final static String DATA_PROPERTY_TRANS_CLASS_NAME = "trans_class_name";

	// io

	@ComponentInput(description = "Input GATE document.", name = "document_in")
	public final static String DATA_INPUT_DOC_IN = "gate_document_in";

	@ComponentOutput(description = "Output GATE document.", name = "document_out")
	public final static String DATA_OUTPUT_DOC_OUT = "gate_document_out";

	// ================
	// Constructor(s)
	// ================
	public GATE_Transducer() {
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

	public String getGrammarRulesURL(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_GRAMMAR_RULES_URL);
		return s;
	}

	public String getTokenAnnotationSetName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TOKEN_ANNOTATION_SET_NAME);
		return s;
	}

	public String getTransAnnotationSetName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TRANS_ANNOTATION_SET_NAME);
		return s;
	}

	public String getTransClassName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TRANS_CLASS_NAME);
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

			params.put(Transducer.TRANSD_ENCODING_PARAMETER_NAME, this
					.getDocumentEncoding(ccp));

			String newGrammarUrl = null;
			String currGrammarURL = this.getGrammarRulesURL(ccp);
			if (currGrammarURL.startsWith(GATEInitialiser.GATE_PREFIX)) {
				newGrammarUrl = GATEInitialiser.getResourceURL(currGrammarURL);
			}
			if (newGrammarUrl != null) {
				System.out
						.println("GATE_Transducer: gate: URLs are deprecated.  Converting "
								+ currGrammarURL + " to " + newGrammarUrl);
				params.put(Transducer.TRANSD_GRAMMAR_URL_PARAMETER_NAME,
						newGrammarUrl);
			} else {
				params.put(Transducer.TRANSD_GRAMMAR_URL_PARAMETER_NAME,
						currGrammarURL);
			}
			params.put(Transducer.TRANSD_INPUT_AS_PARAMETER_NAME, this
					.getTokenAnnotationSetName(ccp));
			params.put(Transducer.TRANSD_OUTPUT_AS_PARAMETER_NAME, this
					.getTransAnnotationSetName(ccp));

			_trans = (AbstractLanguageAnalyser) Factory.createResource(this
					.getTransClassName(ccp), params);
		} catch (Exception e) {
			_logger.info("GATE_Transducer.initialise() -- " + e);
			e.printStackTrace();
			throw new ComponentExecutionException(e);
		}
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();

		if (this.getVerbose(ccp)) {
			System.out.println("\nEND EXEC -- GATE_Transducer -- Docs Ouput: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}

		m_docsProcessed = 0;
		_trans = null;
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		try {
			gate.Document doc = (gate.Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);

			_trans.setDocument(doc);
			_trans.execute();

			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, doc);
			m_docsProcessed++;

			if (this.getVerbose(ctx)) {
				if (Math.IEEEremainder(m_docsProcessed, 250) == 0) {
					System.out.println("GATE_Transducer -- Docs Processed: "
							+ m_docsProcessed);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: GATE_Transducer.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

}
