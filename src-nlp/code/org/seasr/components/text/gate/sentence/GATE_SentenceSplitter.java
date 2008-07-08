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

package org.seasr.components.text.gate.sentence;

// ==============
// Java Imports
// ==============
import java.util.logging.Logger;

// ===============
// Other Imports
// ===============
import gate.*;
import gate.creole.splitter.*;

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
 * Runs the GATE Sentence Splitter on a given GATE document. 
 * Annotates individual sentences.</p>
 * <p><b>Detailed Description</b>: <br>
 * Given a document object in the GATE framework, 
 * this component will annotate sentences using the given rules. 
 * The found annotations will be stored in an annotation set 
 * associated with the document.</p>
 * <p>The sentence splitter requires that the document 
 * be tokenized already.  These tokens should be stored 
 * in the Input Annotation Set, which is left to blank 
 * by default. The newly found annotations will be stored in 
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
		+ "Runs the GATE Sentence Splitter on a given GATE document.  "
		+ "Annotates individual sentences.</p>"
		+ "<p><b>Detailed Description</b>: <br>"
		+ "Given a document object in the GATE framework, "
		+ "this component will annotate sentences " 
		+ "using the given rules. "
		+ "The found annotations will be "
		+ "stored in an annotation set associated with the " 
		+ "document.</p>"
		+ "<p>The sentence splitter requires that the document "
		+ "be tokenized already.  These tokens should be stored "
		+ "in the Input Annotation Set, which is left to blank "
		+ "by default. The newly found annotations will be stored in "
		+ "the Output Annotation Set, also blank by default. "
		+ "Note that by leaving the Annotation Sets blank "
		+ "uses the default annotation set, which is probably "
		+ "easier to manage within a flow.</p>",

name = "GATE_SentenceSplitter", tags = "text gate sentence splitter document")
public class GATE_SentenceSplitter implements ExecutableComponent {
	// ==============
	// Data Members
	// ==============

	private static Logger _logger = Logger.getLogger("GATE_SentenceSplitter");

	private int m_docsProcessed = 0;
	private long m_start = 0;

	// props

	@ComponentProperty(description = "Verbose output?", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Encoding type of the document.", name = "document_encoding", defaultValue = "UTF-8")
	final static String DATA_PROPERTY_DOCUMENT_ENCODING = "document_encoding";

	@ComponentProperty(description = "URL of the gazetteer rules list file in GATE.", name = "gazetteer_list_url", defaultValue = "gate:/creole/splitter/gazetteer/lists.def")
	final static String DATA_PROPERTY_GAZETTEER_LIST_URL = "gazetteer_list_url";

	@ComponentProperty(description = "URL of the transducer rules file in GATE.", name = "transducer_rules_url", defaultValue = "gate:/creole/splitter/grammar/main.jape")
	final static String DATA_PROPERTY_TRANSDUCER_RULES_URL = "transducer_rules_url";

	@ComponentProperty(description = "Name of the input Annotation Set to find the tokens "
			+ "in. Leave blank for default.", name = "input_annotation_set_name", defaultValue = "")
	final static String DATA_PROPERTY_INPUT_ANNOTATION_SET_NAME = "input_annotation_set_name";

	@ComponentProperty(description = "Name of the output Annotation Set to find the tokens "
			+ "in. Leave blank for default.", name = "output_annotation_set_name", defaultValue = "")
	final static String DATA_PROPERTY_OUTPUT_ANNOTATION_SET_NAME = "output_annotation_set_name";

	// io

	@ComponentInput(description = "Input GATE document.", name = "document_in")
	public final static String DATA_INPUT_DOC_IN = "gate_document_in";

	@ComponentOutput(description = "Output GATE document.", name = "document_out")
	public final static String DATA_OUTPUT_DOC_OUT = "gate_document_out";

	private SentenceSplitter _splitter = null;

	// ================
	// Constructor(s)
	// ================
	public GATE_SentenceSplitter() {
	}

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public String getDocumentEncoding(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_DOCUMENT_ENCODING);
		return s;
	}

	public String getGazetteerListURL(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_GAZETTEER_LIST_URL);
		return s;
	}

	public String getTransducerRulesURL(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TRANSDUCER_RULES_URL);
		return s;
	}

	public String getInputAnnotationSetName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_INPUT_ANNOTATION_SET_NAME);
		return s;
	}

	public String getOutputAnnotationSetName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_OUTPUT_ANNOTATION_SET_NAME);
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
			params.put(SentenceSplitter.SPLIT_ENCODING_PARAMETER_NAME,
					getDocumentEncoding(ccp));

			// try to convert a gate: url
			String newGazUrl = null;
			String currGazURL = getGazetteerListURL(ccp);
			if (currGazURL.startsWith(GATEInitialiser.GATE_PREFIX))
				newGazUrl = GATEInitialiser.getResourceURL(currGazURL);

			if (newGazUrl != null) {
				_logger
						.info("GATE_SentenceSplitter: gate: URLs are deprecated.  Converting "
								+ currGazURL + " to " + newGazUrl);
				params.put(SentenceSplitter.SPLIT_GAZ_URL_PARAMETER_NAME,
						newGazUrl);
			} else {
				params.put(SentenceSplitter.SPLIT_GAZ_URL_PARAMETER_NAME,
						currGazURL);
			}

			String newTransUrl = null;
			String currTransURL = getTransducerRulesURL(ccp);
			if (currTransURL.startsWith(GATEInitialiser.GATE_PREFIX)) {
				newTransUrl = GATEInitialiser.getResourceURL(currTransURL);
			}
			if (newTransUrl != null) {
				System.out
						.println("GATE_SentenceSplitter(): gate: URLs are deprecated.  Converting "
								+ currTransURL + " to " + newTransUrl);
				params.put(SentenceSplitter.SPLIT_TRANSD_URL_PARAMETER_NAME,
						newTransUrl);
			} else {
				params.put(SentenceSplitter.SPLIT_TRANSD_URL_PARAMETER_NAME,
						currTransURL);
			}

			params.put(SentenceSplitter.SPLIT_INPUT_AS_PARAMETER_NAME,
					getInputAnnotationSetName(ccp));

			params.put(SentenceSplitter.SPLIT_OUTPUT_AS_PARAMETER_NAME,
					getOutputAnnotationSetName(ccp));

			_splitter = (SentenceSplitter) Factory.createResource(
					"gate.creole.splitter.SentenceSplitter", params);
		} catch (Exception e) {
			_logger.info("GATE_SentenceSplitter.initialise() -- " + e);
			e.printStackTrace();
			throw new ComponentExecutionException(e);
		}
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();

		if (getVerbose(ccp)) {
			_logger.info("\nEND EXEC -- GATE_SentenceSplitter -- Docs Ouput: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}

		m_docsProcessed = 0;
		_splitter = null;
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		try {
			gate.Document doc = (gate.Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);

			_splitter.setDocument(doc);
			_splitter.execute();

			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, doc);
			m_docsProcessed++;

			if (getVerbose(ctx)) {
				if (Math.IEEEremainder(m_docsProcessed, 10) == 0) {
					_logger.info("GATE_SentenceSplitter -- Docs Processed: "
									+ m_docsProcessed);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: GATE_SentenceSplitter.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

}
