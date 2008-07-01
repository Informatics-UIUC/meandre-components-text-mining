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

package org.seasr.components.text.opennlp.pos;

//==============
//Java Imports
//==============

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.*;

//===============
//Other Imports
//===============

import opennlp.tools.lang.english.PosTagger;
import opennlp.tools.ngram.Dictionary;
import opennlp.tools.postag.POSDictionary;

//import org.meandre.tools.components.*;
//import org.meandre.tools.components.FlowBuilderAPI.WorkingFlow;

import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.FeatureMap;
import org.meandre.components.util.MeandreJarFileReaderUtil;
import org.meandre.core.*;
import org.meandre.annotations.*;


/**
 * @author D. Searsmith
 * 
 * TODO: Unit Tests
 */

@Component(creator = "Duane Searsmith", 
		
		description = "<p>Overview: <br>"
			+ "This component wrapts the POSTagger class from OpenNLP. Part-of-Speech "
			+ "that are generated for each token are stored as annotations.</p>", 
		
		name = "OpenNLP_PosTagger", tags = "pos text opennlp document", dependency = { "maxent-models.jar" })
public class OpenNLP_PosTagger implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	// Options
	private int m_docsProcessed = 0;

	private long m_start = 0;

	private PosTagger _tagger = null;
	
	private static Logger _logger = Logger.getLogger("TextFileToDoc");
	
	// Properties

	@ComponentProperty(description = "Verbose output?", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Resource model file name.", name = "model_resource_name", defaultValue = "models/English/parser/tag.bin.gz")
	final static String DATA_PROPERTY_MODEL_RESOURCE_NAME = "model_resource_name";

	@ComponentProperty(description = "Model file name.", name = "model_filename", defaultValue = "/opennlp/models/English/parser/tag.bin.gz")
	final static String DATA_PROPERTY_MODEL_FILENAME = "model_filename";

	@ComponentProperty(description = "Resource dictionary file name.", name = "dict_resource_name", defaultValue = "models/English/parser/dict.bin.gz")
	final static String DATA_PROPERTY_DICTIONARY_RESOURCE_NAME = "dict_resource_name";

	@ComponentProperty(description = "Dictionary file name.", name = "dict_filename", defaultValue = "/opennlp/models/English/parser/dict.bin.gz")
	final static String DATA_PROPERTY_DICTIONARY_FILENAME = "dict_filename";

	@ComponentProperty(description = "Resource tag dictionary file name.", name = "tag_dict_resource_name", defaultValue = "models/English/parser/tagdict")
	final static String DATA_PROPERTY_TAG_DICTIONARY_RESOURCE_NAME = "tag_dict_resource_name";

	@ComponentProperty(description = "Tag dictionary file name.", name = "tag_dict_filename", defaultValue = "/opennlp/models/English/parser/tagdict")
	final static String DATA_PROPERTY_TAG_DICTIONARY_FILENAME = "tag_dict_filename";

	// I/O
	
	@ComponentInput(description = "Input document.", name = "document_in")
	public final static String DATA_INPUT_DOC_IN = "document_in";

	@ComponentOutput(description = "Output document.", name = "document_out")
	public final static String DATA_OUTPUT_DOC_OUT = "document_out";

	// ================
	// Constructor(s)
	// ================

	public OpenNLP_PosTagger() {
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
//		// make a connection between two components
//		wflow.connectComponents(sentdetector,
//				OpenNLP_SentenceDetect.DATA_OUTPUT_DOC_OUT, tokenizer,
//				OpenNLP_Tokenizer.DATA_INPUT_DOC_IN);
//
//		// add another component
//		String tagger = wflow
//				.addComponent("org.seasr.meandre.components.t2k.pos.opennlp.OpenNLP_PosTagger");
//
//		// make a connection between two components
//		wflow.connectComponents(tokenizer,
//				OpenNLP_Tokenizer.DATA_OUTPUT_DOC_OUT, tagger,
//				OpenNLP_PosTagger.DATA_INPUT_DOC_IN);
//
//		// set a component property
//		wflow.setComponentInstanceProp(tagger, "verbose", "true");
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

	public String getModelResourceName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_MODEL_RESOURCE_NAME);
		return s;
	}

	public String getModelFilename(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_MODEL_FILENAME);
		return s;
	}

	public String getDictionaryResourceName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_DICTIONARY_RESOURCE_NAME);
		return s;
	}

	public String getDictionaryFilename(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_DICTIONARY_FILENAME);
		return s;
	}

	public String getTagDictionaryResourceName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TAG_DICTIONARY_RESOURCE_NAME);
		return s;
	}

	public String getTagDictionaryFilename(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TAG_DICTIONARY_FILENAME);
		return s;
	}

	public void initialize(ComponentContextProperties ccp) {
		_logger = ccp.getLogger();
		_logger.fine("initialize() called");
		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();

		// Write model file to disk if it doesn't already
		// exist.

		try {
			File modelFile = MeandreJarFileReaderUtil
					.findAndInstallFileResource(getModelResourceName(ccp),
							getModelFilename(ccp), (ComponentContext)ccp);
			File dictFile = MeandreJarFileReaderUtil
					.findAndInstallFileResource(getDictionaryResourceName(ccp),
							getDictionaryFilename(ccp), (ComponentContext)ccp);
			File tagFile = MeandreJarFileReaderUtil.findAndInstallFileResource(
					getTagDictionaryResourceName(ccp),
					getTagDictionaryFilename(ccp), (ComponentContext)ccp);
			_tagger = new PosTagger(modelFile.getCanonicalPath(),
					new Dictionary(dictFile.getCanonicalPath()),
					new POSDictionary(tagFile.getCanonicalPath(), true));
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new RuntimeException(ioe);
		}
	}

	public void dispose(ComponentContextProperties ccp) {
		long end = System.currentTimeMillis();
		if (getVerbose(ccp)) {
			_logger.info("\nEND EXEC -- OpenNLP_PosTagger -- Docs Processed: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}
		m_docsProcessed = 0;
		_tagger = null;
	}

	@SuppressWarnings("unchecked")
	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		try {
			Document idoc = (Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);

			// =============================
			AnnotationSet annotsSent = idoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_SENTENCES);
			AnnotationSet annotsTok = idoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);
			for (Iterator<Annotation> annotsIT = annotsSent.iterator(); annotsIT
					.hasNext();) {
				Annotation sent = annotsIT.next();
				AnnotationSet sentToks = annotsTok.getContained(sent
						.getStartNodeOffset(), sent.getEndNodeOffset());
				List<Annotation> sentToksList = sentToks
						.getAllSortedAsArrayList();
				List<String> sentToksArr = new ArrayList<String>();
				for (int i = 0, n = sentToksList.size(); i < n; i++) {
					sentToksArr.add(sentToksList.get(i).getContent(idoc));
				}
				List<String> pos = _tagger.tag(sentToksArr);
				for (int i = 0, n = sentToksArr.size(); i < n; i++) {
					sentToksList.get(i).getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
							pos.get(i));
				}
			}

			// ============================
			if (getVerbose(ctx)) {

				AnnotationSet toks = idoc
						.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);

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

			if (getVerbose(ctx)) {
				_logger.info("\n\nDocument parsed:  "
						+ idoc.getAnnotations(
								AnnotationConstants.ANNOTATION_SET_TOKENS).get(
								AnnotationConstants.TOKEN_ANNOT_TYPE).size()
						+ " tokens created.\n\n");
			}
			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, idoc);
			m_docsProcessed++;
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: OpenNLP_PosTagger.");
			throw new ComponentExecutionException(ex);
		}
	}
}
