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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import org.seasr.components.text.gate.util.GATEUtils;

/**
 * <p>
 * <b>Overview</b>: <br>
 * Performs tokenization on a given GATE document. Stores the token annotations
 * in a annotation set of the document.
 * </p>
 * <p>
 * <b>Detailed Description</b>: <br>
 * Given a document object in the GATE framework, this component will perform
 * tokenization using the given tokenizer and grammar rules. The computed tokens
 * (ie, annotations) will be stored in an annotation set associated with the
 * document.
 * </p>
 * <p>
 * The particular annotation set to use can be named by the user. If the
 * computed tokens are to be used by other GATE components, either use a name
 * the other modules will recognize, or leave it blank so the always-present
 * default annotation set is used.
 * </p>
 * 
 * @author D. Searsmith
 * 
 * TODO: Testing, Unit Tests
 */
@Component(creator = "Duane Searsmith",

description = "<p><b>Overview</b>: <br>"
		+ "Performs tokenization on a given GATE document. "
		+ "Stores the token annotations in a annotation set "
		+ "of the document.</p>" + "<p><b>Detailed Description</b>: <br>"
		+ "Given a document object in the GATE framework, "
		+ "this module will perform tokenization using the "
		+ "given tokenizer and grammar rules. "
		+ "The computed tokens (ie, annotations) will be "
		+ "stored in an annotation set associated with the " + "document.</p>"
		+ "<p>The particular annotation set to use can be "
		+ "named by the user. "
		+ "If the computed tokens are to be used by other "
		+ "GATE modules, either use a name the other "
		+ "modules will recognize, or leave it blank "
		+ "so the always-present default annotation set " + "is used.</p>",

name = "GATE_Tokenizer", 
tags = "text gate token tokenize document",
dependency = {"GATE-Home-And-ANNIE-plugin.jar, gate.jar, jasper-compiler-jdt.jar"})
public class GATE_Tokenizer implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private static Logger _logger = Logger.getLogger("GATE_Tokenizer");

	private int m_docsProcessed = 0;
	private long m_start = 0;

	private DefaultTokeniser _toker = null;

	private final String _resName = "GATE-Home-And-ANNIE-plugin_001";

	// props

	@ComponentProperty(description = "Verbose output? An int value (0 = none, 1 = fine, 2 = finer).", name = "verbose", defaultValue = "0")
	public final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Encoding type of the document.", name = "document_encoding", defaultValue = "UTF-8")
	public final static String DATA_PROPERTY_DOCUMENT_ENCODING = "document_encoding";

	@ComponentProperty(description = "URL of the tokenizer rules file in GATE.", name = "tokenizer_rules_url", defaultValue = "/plugins/ANNIE/resources/tokeniser/DefaultTokeniser.rules")
	public final static String DATA_PROPERTY_TOKENIZER_RULES_URL = "tokenizer_rules_url";

	@ComponentProperty(description = "URL of the grammar rules file in GATE.", name = "grammar_rules_url", defaultValue = "/plugins/ANNIE/resources/tokeniser/postprocess.jape")
	public final static String DATA_PROPERTY_GRAMMAR_RULES_URL = "grammar_rules_url";

	@ComponentProperty(description = "Name of the Annotation Set to find the tokens "
			+ "in. Leave blank for default.", name = "annotation_set_name", defaultValue = "")
	public final static String DATA_PROPERTY_ANNOTATION_SET_NAME = "annotation_set_name";

	@ComponentProperty(description = "Show progress?", name = "show_progress", defaultValue = "false")
	public final static String DATA_PROPERTY_SHOW_PROGRESS = "show_progress";

	@ComponentProperty(description = "This property sets the number of documents to process "
			+ "before a print statement is generated to announce "
			+ "the total number records processed to that point.", name = "print_increment", defaultValue = "250")
	public final static String DATA_PROPERTY_PRINT_INCREMENT = "show_progprint_incrementress";

	// io

	@ComponentInput(description = "Input document.", name = "document_in")
	public final static String DATA_INPUT_DOC_IN = "document_in";

	@ComponentOutput(description = "Output document.", name = "document_out")
	public final static String DATA_OUTPUT_DOC_OUT = "document_out";

	// ================
	// Constructor(s)
	// ================
	public GATE_Tokenizer() {
	}

	// ================
	// Public Methods
	// ================

	public int getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Integer.parseInt(s);
	}

	public String getDocumentEncoding(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_DOCUMENT_ENCODING);
		return s;
	}

	public String getTokenizerRulesURL(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TOKENIZER_RULES_URL);
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

		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();

		try {
			String fname = ((ComponentContext) ccp)
					.getPublicResourcesDirectory();
			if ((!(fname.endsWith("/"))) && (!(fname.endsWith("\\")))) {
				fname += "/";
			}
			GATEInitialiser.init(fname, _resName, fname + _resName,
					(ComponentContext) ccp);

			FeatureMap params = Factory.newFeatureMap();
			params.put(SentenceSplitter.SPLIT_ENCODING_PARAMETER_NAME,
					getDocumentEncoding(ccp));

			File prfile = new File(fname);
			String currTokRules = GATEUtils.normalizePathForSEASR(prfile
					.getCanonicalPath(), getTokenizerRulesURL(ccp), _resName);
			params.put(DefaultTokeniser.DEF_TOK_TOKRULES_URL_PARAMETER_NAME,
					currTokRules);
			_logger.info("Tokenization Rules URL: " + currTokRules);

			String currGramRules = GATEUtils.normalizePathForSEASR(prfile.getCanonicalPath(), getGrammarRulesURL(ccp), _resName);
			params.put(DefaultTokeniser.DEF_TOK_GRAMRULES_URL_PARAMETER_NAME,
					currGramRules);
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

		if (getVerbose(ccp) > 0) {
			_logger.info("\nEND EXEC -- GATE_Tokenizer -- Docs Ouput: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}

		m_docsProcessed = 0;
		_toker = null;
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		try {
			
			org.seasr.components.text.datatype.corpora.Document sdoc = (org.seasr.components.text.datatype.corpora.Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);
			if (!GATEUtils.checkIfGATEDocumentExists(sdoc)){
				GATEUtils.addNewGATEDocToSEASRDoc(sdoc);
			}
			gate.Document doc = (gate.Document)sdoc.getAuxMap().get(org.seasr.components.text.datatype.corpora.DocumentConstants.GATE_DOCUMENT);

			int before = -1;
			Map <String, Integer> sMap = null;
			if (getVerbose(ctx) > 0) {
				before = doc.getAnnotations().size();
				sMap = new HashMap<String, Integer>();
				Set<String> sNames = doc.getAnnotationSetNames();
				for (String s : sNames) {
					AnnotationSet annset = doc.getAnnotations().get(s);
					sMap.put(s, annset.size());
				}
			}

			_toker.setDocument(doc);
			if (!(getAnnotationSetName(ctx).trim().length() == 0)) {
				_toker.setAnnotationSetName(getAnnotationSetName(ctx));
			}
			_toker.execute();

			if (getVerbose(ctx) > 1){
				AnnotationSet annset = doc.getAnnotations();
				_logger.info("Annotation set 'DEFAULT' contains " + annset.size() + " annotations.");
				for(Annotation ann:annset){
					_logger.info(doc.getContent().getContent(ann.getStartNode().getOffset(), ann.getEndNode().getOffset()).toString());
				}
				Set<String> anames = doc.getAnnotationSetNames();
				for (String name:anames){
					annset = doc.getAnnotations(name);
					_logger.info("Annotation set " + name + " contains " + annset.size() + " annotations.");
					for(Annotation ann:annset){
						_logger.info(doc.getContent().getContent(ann.getStartNode().getOffset(), ann.getEndNode().getOffset()).toString());
					}
				}
			}

			if (getVerbose(ctx) > 0) {
				_logger
						.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				_logger.info("Annotation set 'DEFAULT'");
				_logger.info("Before run count of annotations in DEFAULT: "
						+ before);
				int after = doc.getAnnotations().size();
				_logger.info("After run count of annotations in DEFAULT: "
						+ after);
				_logger.info("Net addition to DEFAULT: " + (after - before));
				_logger
						.info("-------------------------TYPES----------------------------------------------");
				Set<String> types = doc.getAnnotations().getAllTypes();
				_logger.info("Number of types in 'DEFAULT': "
						+ types.size());
				for (String ts : types) {
					_logger.info(ts);
				}
				_logger
						.info("============================================================================");
				Set<String> sNames = doc.getAnnotationSetNames();
				for (String s : sNames) {
					AnnotationSet annset = doc.getAnnotations().get(s);
					before = sMap.get(s);
					after = annset.size();
					_logger.info("Annotation set '" + s + "'");
					_logger.info("Before run count of annotations in '" + s
							+ "': " + before);
					_logger.info("After run count of annotations in '" + s
							+ "': " + after);
					_logger
							.info("Net addition to DEFAULT: "
									+ (after - before));
					_logger
							.info("-------------------------TYPES----------------------------------------------");
					types = annset.getAllTypes();
					_logger.info("Number of types in '" + s + "': "
							+ types.size());
					for (String ts : types) {
						_logger.info(ts);
					}
					_logger
							.info("============================================================================");
				}
				_logger
						.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			}

			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, sdoc);
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
