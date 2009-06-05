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

package org.seasr.components.text.opennlp.tokenize;

//==============
//Java Imports
//==============

import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import opennlp.tools.lang.english.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.util.Span;

import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.FeatureMap;
import org.seasr.components.text.opennlp.OpenNLPBaseUtilities;
import org.seasr.components.text.util.Factory;

/**
 * @author D. Searsmith
 *
 * TODO: Testing, Unit Tests
 */

@Component(creator = "Duane Searsmith",

		description = "<p>Overview: <br>"
			+ "This component wraps the OpenNLP Tokenizer class.  This class takes text that has been "
			+ "segemented into sentences and paragraphs and tokenizes each word (part).  Each token is "
			+ "recorded as an annotation in the SEASR Document object.",

		name = "OpenNLP_Tokenizer", tags = "tokenize text opennlp document",
		dependency = { "opennlp-english-models.jar" },
        baseURL="meandre://seasr.org/components/")
public class OpenNLP_Tokenizer extends OpenNLPBaseUtilities {

	// ==============
	// Data Members
	// ==============

	// Options
	private int m_docsProcessed = 0;

	private long m_start = 0;

	private TokenizerME _tokenizer = null;

	private static Logger _logger;

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Exclude tokenization of the title? A boolean value (true or false).", name = "exclude_title", defaultValue = "true")
	final static String DATA_PROPERTY_EXCLUDE_TITLE = "exclude_title";

	// io

	@ComponentInput(description = "Input document.", name = "Document")
	public final static String DATA_INPUT_DOC_IN = "Document";

	@ComponentOutput(description = "Output document.", name = "Document")
	public final static String DATA_OUTPUT_DOC_OUT = "Document";


	// ================
	// Constructor(s)
	// ================

	public OpenNLP_Tokenizer() {
	}

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
//		// // set a component property
//		 wflow.setComponentInstanceProp(reader, TextFileToDoc.DATA_PROPERTY_ADD_SPACE_AT_NEW_LINES, "true");
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
//		// set a component property
//		wflow.setComponentInstanceProp(tokenizer, "verbose", "true");
//
//		// // set a component property
//		// wflow.setComponentInstanceProp(sentdetector, "verbose", "true");
//
//		// make a connection between two components
//		wflow.connectComponents(sentdetector,
//				OpenNLP_SentenceDetect.DATA_OUTPUT_DOC_OUT, tokenizer,
//				OpenNLP_Tokenizer.DATA_INPUT_DOC_IN);
//
//		// execute the flow specifying that we want a web UI displayed
//		flowBuilder.execute(wflow, false);
//
//		// For some reason the process does not end without a forced exit.
//		System.exit(0);

	}

	// ================
	// Public Methods
	// ================

	// Property Getters

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getExcludeTitle(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_EXCLUDE_TITLE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public void initializeCallBack(ComponentContextProperties ccp) throws Exception {
		super.initializeCallBack(ccp);

		_logger = console;
		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();

		// Initialize the tokenizer
        try {
            _tokenizer = new Tokenizer(sOpenNLPDir+"tokenize"+File.separator+
                    sLanguage.substring(0,1).toUpperCase()+sLanguage.substring(1)+"Tok.bin.gz");
        }
        catch ( Throwable t ) {
            console.log(Level.SEVERE,"Failed to open tokenizer model for " + sLanguage, t);
            throw new ComponentExecutionException(t);
        }
	}

	public void disposeCallBack(ComponentContextProperties ccp) throws Exception {
		long end = System.currentTimeMillis();
		if (getVerbose(ccp)) {
			_logger.info("\nEND EXEC -- OpenNLP_Tokenizer -- Docs Processed: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}
		m_docsProcessed = 0;
		_tokenizer = null;
	}

	public void executeCallBack(ComponentContext ctx)
			throws Exception {
		try {
			Document idoc = (Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);

			// =============================
			AnnotationSet annots = idoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_SENTENCES);

			AnnotationSet annotsP = idoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);

			for (Iterator<Annotation> itty = annots.iterator(); itty.hasNext();) {
				Annotation annot = itty.next();
				String sent = annot.getContent(idoc);
				if (sent.trim().length() == 0) {
					continue;
				}
				int offset = (int) annot.getStartNodeOffset();
				Span[] spans = _tokenizer.tokenizePos(sent);
				for (int i = 0, n = spans.length; i < n; i++) {
					Span spn = spans[i];
					int posB = spn.getStart() + offset;
					int posE = spn.getEnd() + offset;
					if ((i > 0) && (i < (n - 1))) {
						annotsP.add(posB, posE,
								AnnotationConstants.TOKEN_ANNOT_TYPE, null);
					} else if (i == 0) {
						/**
						 * So this feature is added for cross compatibility issues with
						 * SEASR POS tagger.  But also it is general information that is
						 * useful to know about a token.
						 */
						FeatureMap fm = Factory.newFeatureMap();
						fm
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE_BOOL,
										Boolean.TRUE.toString());
						annotsP.add(posB, posE,
								AnnotationConstants.TOKEN_ANNOT_TYPE, fm);
					} else {
						/**
						 * So this feature is added for cross compatibility issues with
						 * SEASR POS tagger.  But also it is general information that is
						 * useful to know about a token.
						 */
						FeatureMap fm = Factory.newFeatureMap();
						fm
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_END_OF_LINE_BOOL,
										Boolean.TRUE.toString());
						annotsP.add(posB, posE,
								AnnotationConstants.TOKEN_ANNOT_TYPE, fm);
					}
				}
			}

			if ((idoc.getTitle() != null) && (idoc.getTitle().length() > 0)
					&& (!getExcludeTitle(ctx))) {
				String tit = idoc.getTitle();
				Span[] spans = _tokenizer.tokenizePos(tit);
				for (int i = 0, n = spans.length; i < n; i++) {
					Span spn = spans[i];
					FeatureMap fm = Factory.newFeatureMap();
					fm.put(AnnotationConstants.TOKEN_ANNOT_FEAT_INTITLE_BOOL,
							Boolean.TRUE.toString());
					annotsP.add(spn.getStart(), spn.getEnd(),
							AnnotationConstants.TOKEN_ANNOT_TYPE, fm);
				}
			}

			// ============================

			if (getVerbose(ctx)) {

				AnnotationSet toks = idoc
						.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);

				_logger.info("\n\nDocument parsed.  " + toks.size()
						+ " tokens created.\n\n");

				StringBuffer buff = new StringBuffer();
				for (Iterator<Annotation> itty = toks.getAllSortedAsArrayList()
						.iterator(); itty.hasNext();) {
					Annotation tok = itty.next();
					buff.append("Token: " + tok.getContent(idoc));
					FeatureMap fm = tok.getFeatures();
					if (fm.isEmpty()) {
						_logger.info(buff.toString());
						buff.delete(0, buff.length());
					} else {
						for (Iterator<String> itty2 = fm.keySet().iterator(); itty2
								.hasNext();) {
							String key = itty2.next();
							String val = fm.get(key).toString();
							buff.append("  <" + key + ", " + val + ">");
						}
						_logger.info(buff.toString());
						buff.delete(0, buff.length());
					}
				}

			}
			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, idoc);
			m_docsProcessed++;
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: OpenNLP_Tokenizer.execute()");
			throw new ComponentExecutionException(ex);
		}
	}
}
