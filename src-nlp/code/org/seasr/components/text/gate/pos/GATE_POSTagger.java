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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import org.seasr.components.text.gate.util.GATEUtils;

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

name = "GATE_POSTagger", 
tags = "text gate pos tagger document",
dependency = { "GATE-Home-And-ANNIE-plugin.jar, gate.jar" })
public class GATE_POSTagger implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private static Logger _logger = Logger.getLogger("GATE_POSTagger");

	private POSTagger _tagger = null;

	private int m_docsProcessed = 0;
	private long m_start = 0;

	private final String _resName = "GATE-Home-And-ANNIE-plugin_001";

	// props

	@ComponentProperty(description = "Verbose output? An int value (0 = none, 1 = fine, 2 = finer).", name = "verbose", defaultValue = "0")
	public final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "URL of lexicon file in GATE.", name = "lexicon_url", defaultValue = "/plugins/ANNIE/resources/heptag/lexicon")
	public final static String DATA_PROPERTY_LEXICON_URL = "lexicon_url";

	@ComponentProperty(description = "URL for the parts-of-speech rules file in GATE.", name = "pos_rules_url", defaultValue = "/plugins/ANNIE/resources/heptag/ruleset")
	public final static String DATA_PROPERTY_POS_RULES_URL = "pos_rules_url";

	@ComponentProperty(description = "Name of the Annotation Set to find the tokens in.  "
			+ "Leave blank for default set. The new feature (the POS tag) is added to Token "
			+ "type annotations found in this set.", name = "token_annotation_set_name", defaultValue = "")
	public final static String DATA_PROPERTY_TOKEN_ANNOTATION_SET_NAME = "token_annotation_set_name";

	@ComponentProperty(description = "Name of the Annotation Set that contains the Sentence "
			+ "type annotations. The tokens cannot be tagged if the Document was not processed "
			+ "with a Sentence Splitter.", name = "sentence_annotation_set_name", defaultValue = "")
	public final static String DATA_PROPERTY_SENTENCE_ANNOTATION_SET_NAME = "sentence_annotation_set_name";

	// io

	@ComponentInput(description = "Input document.", name = "document_in")
	public final static String DATA_INPUT_DOC_IN = "document_in";

	@ComponentOutput(description = "Output document.", name = "document_out")
	public final static String DATA_OUTPUT_DOC_OUT = "document_out";

	// ===============
	// Constructor(s)
	// ===============

	public GATE_POSTagger() {
	}

	// ================
	// Public Methods
	// ================

	public int getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Integer.parseInt(s);
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
		String s = ccp.getProperty(DATA_PROPERTY_SENTENCE_ANNOTATION_SET_NAME);
		return s;
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
			File prfile = new File(fname);

			FeatureMap params = Factory.newFeatureMap();

			String currLexURL = GATEUtils.normalizePathForSEASR(prfile
					.getCanonicalPath(), getLexiconURL(ccp), _resName);
			params.put(POSTagger.TAG_LEXICON_URL_PARAMETER_NAME, currLexURL);

			String currRuleURL = GATEUtils.normalizePathForSEASR(prfile
					.getCanonicalPath(), getPoSRulesURL(ccp), _resName);
			params.put(POSTagger.TAG_RULES_URL_PARAMETER_NAME, currRuleURL);

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

		if (getVerbose(ccp) > 0) {
			_logger.info("\nEND EXEC -- GATE_POSTagger -- Docs Ouput: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}

		m_docsProcessed = 0;
		_tagger = null;
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		try {
			org.seasr.components.text.datatype.corpora.Document sdoc = (org.seasr.components.text.datatype.corpora.Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);
			if (!GATEUtils.checkIfGATEDocumentExists(sdoc)) {
				GATEUtils.addNewGATEDocToSEASRDoc(sdoc);
			}
			gate.Document doc = (gate.Document) sdoc
					.getAuxMap()
					.get(
							org.seasr.components.text.datatype.corpora.DocumentConstants.GATE_DOCUMENT);

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

			// making sure that there are tokens to tag
			AnnotationSet tokens;
			String tasName = this.getTokenAnnotationSetName(ctx);
			if (tasName == null || tasName.trim().length() == 0) {
				tokens = doc.getAnnotations().get(
						ANNIEConstants.TOKEN_ANNOTATION_TYPE);
			} else
				tokens = doc.getAnnotations(tasName).get(
						ANNIEConstants.TOKEN_ANNOTATION_TYPE);

			// making sure that there are sentences. if the document is not
			// sentence split then pos tagging fails
			AnnotationSet sentences;
			String sasName = this.getSentenceAnnotationSetName(ctx);
			if (sasName == null || sasName.trim().length() == 0) {
				sentences = doc.getAnnotations().get(
						ANNIEConstants.SENTENCE_ANNOTATION_TYPE);
			} else
				sentences = doc.getAnnotations(sasName).get(
						ANNIEConstants.SENTENCE_ANNOTATION_TYPE);

			if (tokens != null && sentences != null) {
				_tagger.setDocument(doc);
				_tagger.execute();
			} else {
				if (sentences == null) {
					_logger
							.severe("GATE_POSTagger: Input Document must have Sentence type annotations "
									+ "in order to be part of speech tagged. Process the Document "
									+ "with a Sentence Splitter first.");
					throw new Exception(
							"GATE_POSTagger: Input Document must have Sentence type annotations "
									+ "in order to be part of speech tagged. Process the Document "
									+ "with a Sentence Splitter first.");
				}
				if (tokens == null) {
					_logger
							.info("GATE_POSTagger: Input Document has no Token type annotations - "
									+ "nothing to do.");
					throw new Exception(
							"GATE_POSTagger: Input Document has no Token type annotations - "
									+ "nothing to do.");
				}
			}

			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, sdoc);
			m_docsProcessed++;

			if (getVerbose(ctx) > 1) {
				AnnotationSet annset = doc.getAnnotations().get("Token");
				_logger.info("Annotation set 'DEFAULT' contains "
						+ annset.size() + " annotations.");
				for (Annotation ann : annset) {
					_logger.info(ann.toString());
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

			if (this.getVerbose(ctx) > 0) {
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
