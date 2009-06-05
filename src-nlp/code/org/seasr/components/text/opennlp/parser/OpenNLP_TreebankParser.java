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

package org.seasr.components.text.opennlp.parser;

// ==============
// Java Imports
// ==============

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import opennlp.tools.lang.english.TreebankParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.ParserME;

import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
import org.meandre.core.ExecutableComponent;
import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.FeatureMap;
import org.seasr.components.text.util.Factory;
import org.seasr.components.text.util.feature_maps.FeatureValueEncoderDecoder;

/**
 * @author D. Searsmith
 *
 * TODO: Testing, Unit Tests
 */

@Component(creator = "Duane Searsmith",

description = "<p>Usage: TreebankParser <br>"
		+ "Props: <br>"
		+ "Use tag dictionary. <br>"
		+ "Case insensitive tag dictionary. <br>"
		+ "Beam size. <br>"
		+ "Ouput treebank parse format. <br>"
		+ "Advance outcomes in with at least probability mass X. <br>"
		+ "Show the top X parses.  This will also display their log-probablities. <br>"
		+ "The 'ouput in treebank parse format' is neccessary if you intend to do "
		+ "information extraction and use the linker component.  Also, you should"
		+ "choose to parse in sentence order.  You should also choose the default number "
		+ "of parses -- 1. <br>"
		+ "When treebank output format is enabled, the parse information is still stored in "
		+ "SEASR annotations but treebank format is stored in addition for use in later "
		+ "components.</p>",

name = "OpenNLP_TreebankParser", tags = "parser text opennlp document",
dependency = { "maxent-models.jar" },
baseURL="meandre://seasr.org/components/")
public class OpenNLP_TreebankParser implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private int m_docsProcessed = 0;

	private long m_start = 0;

	private ParserME _parser = null;

	private static Logger _logger = Logger.getLogger("TextFileToDoc");

	/**
	 * Maps used to get the correct span coordinates for parse annotations.
	 */
	private Map<Integer, Annotation> _posiToTokenS = null;
	private Map<Integer, Annotation> _posiToTokenE = null;

	// Props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	public final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Use tag dictionary? A boolean value (true or false).", name = "use_tag_dict", defaultValue = "false")
	public final static String DATA_PROPERT_USE_TAG_DICTIONARY = "use_tag_dict";

	@ComponentProperty(description = "Parse in sentence order? A boolean value (true or false).", name = "parse_in_order", defaultValue = "false")
	public final static String DATA_PROPERT_PARSE_IN_ORDER = "parse_in_order";

	@ComponentProperty(description = "Use case insensitive tag dictionary? A boolean value (true or false).", name = "use_case_insensitive_tag_dict", defaultValue = "false")
	public final static String DATA_PROPERT_USE_CASE_INSENSITIVE_TAG_DICTIONARY = "use_case_insensitive_tag_dict";

	@ComponentProperty(description = "Beam size (int).", name = "beam_size", defaultValue = "20")
	public final static String DATA_PROPERT_BEAM_SIZE = "beam_size";

	@ComponentProperty(description = "Number of parses to admit.", name = "num_parses", defaultValue = "1")
	public final static String DATA_PROPERT_NUMBER_OF_PARSES = "num_parses";

	@ComponentProperty(description = "Advance outcomes in with at least X% of the probability mass.", name = "advance_percentage", defaultValue = "0.95")
	public final static String DATA_PROPERT_ADVANCE_PERCENTAGE = "advance_percentage";

	@ComponentProperty(description = "Also output in treebank fmt? A boolean value (true or false).", name = "treebank_fmt", defaultValue = "false")
	public final static String DATA_PROPERTY_TREEBANK_FMT = "treebank_fmt";

	@ComponentProperty(description = "Build model resource file name.", name = "build_model_resource_name", defaultValue = "models/English/parser/build.bin.gz")
	public final static String DATA_PROPERTY_BUILD_MODEL_RESOURCE_NAME = "build_model_resource_name";

	@ComponentProperty(description = "Build model file name.", name = "build_model_filename", defaultValue = "/opennlp/models/English/parser/build.bin.gz")
	public final static String DATA_PROPERTY_BUILD_MODEL_FILENAME = "build_model_filename";

	@ComponentProperty(description = "Check model resource file name.", name = "check_model_resource_name", defaultValue = "models/English/parser/check.bin.gz")
	public final static String DATA_PROPERTY_CHECK_MODEL_RESOURCE_NAME = "check_model_resource_name";

	@ComponentProperty(description = "Check model file name.", name = "check_model_filename", defaultValue = "/opennlp/models/English/parser/check.bin.gz")
	public final static String DATA_PROPERTY_CHECK_MODEL_FILENAME = "check_model_filename";

	@ComponentProperty(description = "Tagger model resource file name.", name = "tagger_model_resource_name", defaultValue = "models/English/parser/tag.bin.gz")
	public final static String DATA_PROPERTY_TAGGER_MODEL_RESOURCE_NAME = "tagger_model_resource_name";

	@ComponentProperty(description = "Tagger model file name.", name = "tagger_model_filename", defaultValue = "/opennlp/models/English/parser/tag.bin.gz")
	public final static String DATA_PROPERTY_TAGGER_MODEL_FILENAME = "tagger_model_filename";

	@ComponentProperty(description = "Tag dictionary resource file name.", name = "tag_dictionary_resource_name", defaultValue = "models/English/parser/tagdict")
	public final static String DATA_PROPERTY_TAG_DICTIONARY_RESOURCE_NAME = "tag_dictionary_resource_name";

	@ComponentProperty(description = "Tag dictionary file name.", name = "tag_dictionary_filename", defaultValue = "/opennlp/models/English/parser/tagdict")
	public final static String DATA_PROPERTY_TAG_DICTIONARY_FILENAME = "tag_dictionary_filename";

	@ComponentProperty(description = "Chunker model resource file name.", name = "chunker_model_resource_name", defaultValue = "models/English/parser/chunk.bin.gz")
	public final static String DATA_PROPERTY_CHUNKER_MODEL_RESOURCE_NAME = "chunker_model_resource_name";

	@ComponentProperty(description = "Chunker model file name.", name = "chunker_model_filename", defaultValue = "/opennlp/models/English/parser/chunk.bin.gz")
	public final static String DATA_PROPERTY_CHUNKER_MODEL_FILENAME = "chunker_model_filename";

	@ComponentProperty(description = "Head rules resource file name.", name = "head_rules_resource_name", defaultValue = "models/English/parser/head_rules")
	public final static String DATA_PROPERTY_HEAD_RULES_RESOURCE_NAME = "head_rules_resource_name";

	@ComponentProperty(description = "Head rules file name.", name = "head_rules_filename", defaultValue = "/opennlp/models/English/parser/head_rules")
	public final static String DATA_PROPERTY_HEAD_RULES_FILENAME = "head_rules_filename";

	// I/O

	@ComponentInput(description = "Input document.", name = "Document")
	public final static String DATA_INPUT_DOC_IN = "Document";

	@ComponentOutput(description = "Output document.", name = "Document")
	public final static String DATA_OUTPUT_DOC_OUT = "Document";

	// ================
	// Constructor(s)
	// ================

	public OpenNLP_TreebankParser() {
	}

	static public void main(String[] args) {

		// // get a flow builder instance
		// FlowBuilderAPI flowBuilder = new FlowBuilderAPI();
		// // get a flow object
		// WorkingFlow wflow = flowBuilder.newWorkingFlow("test");
		// // add a component
		// String pushString = wflow
		// .addComponent("org.seasr.meandre.components.io.PushString");
		// // set a component property
		// wflow.setComponentInstanceProp(pushString, "string",
		// "c:/tmp/sample.txt");
		// // add another component
		// String reader = wflow
		// .addComponent("org.seasr.meandre.components.t2k.io.file.TextFileToDoc");
		//
		// // // set a component property
		// wflow.setComponentInstanceProp(reader,
		// TextFileToDoc.DATA_PROPERTY_ADD_SPACE_AT_NEW_LINES, "true");
		//
		// // make a connection between two components
		// wflow.connectComponents(pushString, "output_string", reader,
		// TextFileToDoc.DATA_INPUT_FILE_NAME);
		//
		// // add another component
		// String sentdetector = wflow
		// .addComponent("org.seasr.meandre.components.t2k.sentence.opennlp.OpenNLP_SentenceDetect");
		//
		// // make a connection between two components
		// wflow.connectComponents(reader, TextFileToDoc.DATA_OUTPUT_FILE_DOC,
		// sentdetector, OpenNLP_SentenceDetect.DATA_INPUT_DOC_IN);
		//
		// // add another component
		// // String tokenizer = wflow
		// //
		// .addComponent("org.seasr.meandre.components.t2k.tokenize.opennlp.OpenNLP_Tokenizer");
		//
		// // add another component
		// String tokenizer = wflow
		// .addComponent("org.seasr.meandre.components.t2k.tokenize.brown.Tokenizer_Comp");
		//
		// // make a connection between two components
		// wflow.connectComponents(sentdetector,
		// OpenNLP_SentenceDetect.DATA_OUTPUT_DOC_OUT, tokenizer,
		// Tokenizer_Comp.DATA_INPUT_DOC_IN);
		//
		// // add another component
		// String parser = wflow
		// .addComponent("org.seasr.meandre.components.t2k.parser.opennlp.OpenNLP_TreebankParser");
		//
		// // make a connection between two components
		// wflow.connectComponents(tokenizer,
		// Tokenizer_Comp.DATA_OUTPUT_DOC_OUT, parser,
		// DATA_INPUT_DOC_IN);
		//
		// // set a component property
		// wflow.setComponentInstanceProp(parser,
		// OpenNLP_TreebankParser.DATA_PROPERT_PARSE_IN_ORDER, "true");
		//
		// // set a component property
		// // wflow.setComponentInstanceProp(parser,
		// // OpenNLP_TreebankParser.DATA_PROPERTY_VERBOSE, "true");
		//
		// wflow.setComponentInstanceProp(parser,
		// OpenNLP_TreebankParser.DATA_PROPERTY_TREEBANK_FMT, "true");
		//
		// // wflow.setComponentInstanceProp(parser,
		// // OpenNLP_TreebankParser.DATA_PROPERT_NUMBER_OF_PARSES, "5");
		//
		// // execute the flow specifying that we want a web UI displayed
		// flowBuilder.execute(wflow, false);
		//
		// // For some reason the process does not end without a forced exit.
		// System.exit(0);

	}

	// ================
	// Public Methods
	// ================

	// Prop Getters

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getParseInOrder(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERT_PARSE_IN_ORDER);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getUseTagDictionary(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERT_USE_TAG_DICTIONARY);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getUseCaseSensitiveTagDictionary(
			ComponentContextProperties ccp) {
		String s = ccp
				.getProperty(DATA_PROPERT_USE_CASE_INSENSITIVE_TAG_DICTIONARY);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public int getBeamSize(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERT_BEAM_SIZE);
		return Integer.parseInt(s);
	}

	public int getNumParses(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERT_NUMBER_OF_PARSES);
		return Integer.parseInt(s);
	}

	public float getAdvancePercentage(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERT_ADVANCE_PERCENTAGE);
		return Float.parseFloat(s);
	}

	public boolean getOutputTreebankFmt(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TREEBANK_FMT);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public String getBuildModelResourceName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_BUILD_MODEL_RESOURCE_NAME);
		return s;
	}

	public String getBuildModelFilename(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_BUILD_MODEL_FILENAME);
		return s;
	}

	public String getCheckModelResourceName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_CHECK_MODEL_RESOURCE_NAME);
		return s;
	}

	public String getCheckModelFilename(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_CHECK_MODEL_FILENAME);
		return s;
	}

	public String getTaggerModelResourceName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TAGGER_MODEL_RESOURCE_NAME);
		return s;
	}

	public String getTaggerModelFilename(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TAGGER_MODEL_FILENAME);
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

	public String getChunkerModelResourceName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_CHUNKER_MODEL_RESOURCE_NAME);
		return s;
	}

	public String getChunkerModelFilename(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_CHUNKER_MODEL_FILENAME);
		return s;
	}

	public String getHeadRulesResourceName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_HEAD_RULES_RESOURCE_NAME);
		return s;
	}

	public String getHeadRulesFilename(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_HEAD_RULES_FILENAME);
		return s;
	}

	public void initialize(ComponentContextProperties ccp) {
	    throw new RuntimeException("This component has not been transitioned to use the maxent-models.jar model file yet!");
//		_logger = ccp.getLogger();
//		_logger.fine("initialize() called");
//		m_docsProcessed = 0;
//		m_start = System.currentTimeMillis();
//
//		_posiToTokenS = new HashMap<Integer, Annotation>();
//		_posiToTokenE = new HashMap<Integer, Annotation>();
//
//		// Write model files to disk if they don't already
//		// exist, and then instantiate a parser.
//
//		try {
//			File buildModelFile = MeandreJarFileReaderUtil
//					.findAndInstallFileResource(getBuildModelResourceName(ccp),
//							getBuildModelFilename(ccp), (ComponentContext) ccp);
//			File checkModelFile = MeandreJarFileReaderUtil
//					.findAndInstallFileResource(getCheckModelResourceName(ccp),
//							getCheckModelFilename(ccp), (ComponentContext) ccp);
//			File taggerModelFile = MeandreJarFileReaderUtil
//					.findAndInstallFileResource(
//							getTaggerModelResourceName(ccp),
//							getTaggerModelFilename(ccp), (ComponentContext) ccp);
//			File chunkerModelFile = MeandreJarFileReaderUtil
//					.findAndInstallFileResource(
//							getChunkerModelResourceName(ccp),
//							getChunkerModelFilename(ccp),
//							(ComponentContext) ccp);
//			File hrulesModelFile = MeandreJarFileReaderUtil
//					.findAndInstallFileResource(getHeadRulesResourceName(ccp),
//							getHeadRulesFilename(ccp), (ComponentContext) ccp);
//			if (getUseTagDictionary(ccp)) {
//				File tagDictionaryFile = MeandreJarFileReaderUtil
//						.findAndInstallFileResource(
//								getTagDictionaryResourceName(ccp),
//								getTagDictionaryFilename(ccp),
//								(ComponentContext) ccp);
//
//				_parser = new ParserME(new SuffixSensitiveGISModelReader(
//						buildModelFile).getModel(),
//						new SuffixSensitiveGISModelReader(checkModelFile)
//								.getModel(), new ParserTagger(taggerModelFile
//								.getCanonicalPath(), tagDictionaryFile
//								.getCanonicalPath(),
//								getUseCaseSensitiveTagDictionary(ccp)),
//						new ParserChunker(chunkerModelFile.getCanonicalPath()),
//						new HeadRules(hrulesModelFile.getCanonicalPath()),
//						getBeamSize(ccp), getAdvancePercentage(ccp));
//			} else {
//				_parser = new ParserME(new SuffixSensitiveGISModelReader(
//						buildModelFile).getModel(),
//						new SuffixSensitiveGISModelReader(checkModelFile)
//								.getModel(), new ParserTagger(taggerModelFile
//								.getCanonicalPath(), null), new ParserChunker(
//								chunkerModelFile.getCanonicalPath()),
//						new HeadRules(hrulesModelFile.getCanonicalPath()),
//						getBeamSize(ccp), getAdvancePercentage(ccp));
//			}
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//			throw new RuntimeException(ioe);
//		}
	}

	public void dispose(ComponentContextProperties ccp) {
		long end = System.currentTimeMillis();
		if (getVerbose(ccp)) {
			_logger
					.info("\nEND EXEC -- OpenNLP_TreebankParser -- Docs Processed: "
							+ m_docsProcessed
							+ " in "
							+ (end - m_start)
							/ 1000
							+ " seconds\n");
		}
		m_docsProcessed = 0;
		_parser = null;
		if (this._posiToTokenE != null) {
			_posiToTokenE.clear();
		}
		_posiToTokenE = null;
		if (this._posiToTokenS != null) {
			_posiToTokenS.clear();
		}
		_posiToTokenS = null;
	}

	@SuppressWarnings("unchecked")
	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		try {
			Document idoc = (Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);

			// =============================
			Collection<Annotation> annotsSentC = idoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_SENTENCES);
			if (getParseInOrder(ctx)) {
				annotsSentC = ((AnnotationSet) annotsSentC)
						.getAllSortedAsArrayList();
			}
			AnnotationSet annotsTok = idoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);
			AnnotationSet annotsParse = idoc
					.getAnnotations(AnnotationConstants.PARSE_ANNOT_TYPE);
			int aps = annotsParse.size();
			for (Iterator<Annotation> annotsIT = annotsSentC.iterator(); annotsIT
					.hasNext();) {
				_posiToTokenS.clear();
				_posiToTokenE.clear();
				Annotation sent = annotsIT.next();
				AnnotationSet sentToks = annotsTok.getContained(sent
						.getStartNodeOffset(), sent.getEndNodeOffset());
				List<Annotation> sentToksList = sentToks
						.getAllSortedAsArrayList();
				StringBuffer buff = new StringBuffer();
				for (int i = 0, n = sentToksList.size(); i < n; i++) {
					int posi = buff.length();
					buff.append(
							convertToken(sentToksList.get(i).getContent(idoc)))
							.append(" ");
					_posiToTokenS.put(posi, sentToksList.get(i));
					_posiToTokenE.put(buff.length() - 1, sentToksList.get(i));
				}
				Parse[] parses = TreebankParser.parseLine(buff.toString()
						.trim(), _parser, getNumParses(ctx));
				// add parse annotations to document
				addSentenceParsesToDoc(parses, sent, annotsParse);

				if (getOutputTreebankFmt(ctx)) {
					for (int pi = 0, pn = parses.length; pi < pn; pi++) {
						StringBuffer sbuff = new StringBuffer("");
						if (getNumParses(ctx) > 1) {
							sbuff.append(pi + " " + parses[pi].getProb() + " ");
						}
						parses[pi].show(sbuff);
						Set<String> sentPTBP = FeatureValueEncoderDecoder
								.decodeToSet(sent
										.getFeatures()
										.get(
												AnnotationConstants.SENTENCE_PENNTREEBANK_FMT_ANNOT_PARSES_SET));
						if (sentPTBP == null) {
							sentPTBP = new HashSet<String>();
						}
						sentPTBP.add(sbuff.toString());
						sent
								.getFeatures()
								.put(
										AnnotationConstants.SENTENCE_PENNTREEBANK_FMT_ANNOT_PARSES_SET,
										FeatureValueEncoderDecoder
												.encodeSet(sentPTBP));
					}
				}

				if (getVerbose(ctx)) {
					prettyPrintParses(parses, buff.toString().trim());
				}
			}
			_logger.info("OpenNLP_TreelinkParser :: Doc: " + idoc.getTitle()
					+ " created " + (annotsParse.size() - aps)
					+ " parse annotations.");

			// ============================

			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, idoc);
			m_docsProcessed++;
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: OpenNLP_TreebankParser.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

	/**
	 * Sometimes the OpenNLP parser parses things like "(yards)" as "(yards )"
	 * (i.e. 2 tokens instead of three. To compensate for this error I am simply
	 * deleting the "untokenized" paren and allowing the parse to proceed.
	 *
	 * TODO: Detect untokenized parens and treat them as separate tokens for the
	 * purposes of the parser. This still means the actual tokenization is
	 * flawed but that is a tokenizer issue.
	 *
	 * NOTE: SEASR tokenizer (brown) doesn't seem to have this issue being rule
	 * based.
	 *
	 * @param token
	 * @return
	 */
	private static String convertToken(String token) {
		if (token.equals("(")) {
			return "-LRB-";
		} else if (token.equals(")")) {
			return "-RRB-";
		} else if (token.equals("{")) {
			return "-LCB-";
		} else if (token.equals("}")) {
			return "-RCB-";
		}
		token = token.replaceAll("[(){}]", "");
		return token;
	}

	/**
	 * The top level parses are passed to the annotation parse builder. Note:
	 * each Parse object at this level denotes a separate unique parse. Also
	 * note that no parse objects indicates that the parser could find no parse,
	 * and a single parse annotation is created with type "NO PARSE".
	 *
	 * @param parses
	 * @param sent
	 * @param parseSet
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void addSentenceParsesToDoc(Parse[] parses, Annotation sent,
			AnnotationSet parseSet) throws Exception {
		if (parses.length == 0) {
			FeatureMap fm = Factory.newFeatureMap();
			fm.put(AnnotationConstants.PARSE_ANNOT_SOFA_INT, Integer.toString(sent.getId()));
			fm
					.put(
							AnnotationConstants.PARSE_ANNOT_PARSER,
							AnnotationConstants.ANNOTATION_PARSER_IMPL_OpenNLP_TreebankParser);
			fm.put(AnnotationConstants.PARSE_ANNOT_TYPE,
					AnnotationConstants.ANNOTATION_PARSER_NO_PARSE);

			int id = parseSet.add(sent.getStartNodeOffset(), sent
					.getEndNodeOffset(), AnnotationConstants.PARSE_ANNOT_TYPE,
					fm);

			// Add parse to sentence annot
			Set<String> s =  FeatureValueEncoderDecoder.decodeToSet(sent.getFeatures().get(
					AnnotationConstants.SENTENCE_ANNOT_PARSES_SET));
			if (s == null) {
				s = new HashSet<String>();
			}
			s.add(Integer.toString(id));
			sent.getFeatures().put(
					AnnotationConstants.SENTENCE_ANNOT_PARSES_SET, FeatureValueEncoderDecoder.encodeSet(s));
		} else {
			// Add parse to sentence annot
			Set<String> s = FeatureValueEncoderDecoder.decodeToSet(sent.getFeatures().get(
					AnnotationConstants.SENTENCE_ANNOT_PARSES_SET));
			if (s == null) {
				s = new HashSet<String>();
				sent.getFeatures().put(
						AnnotationConstants.SENTENCE_ANNOT_PARSES_SET, FeatureValueEncoderDecoder.encodeSet(s));
			}
			for (int i = 0, n = parses.length; i < n; i++) {
				List<Parse> plist = new ArrayList<Parse>();
				plist.add(parses[i]);
				addSentenceParseToDoc(plist, parseSet, null, sent);
			}
		}
	}

	/**
	 * Given a set of parses (the children of some parent, or 1 top level
	 * parse), for each parse, create an annotation. Add the annotation to the
	 * set of parses. If this parse has a parent, set the parent parse
	 * annotation ID, and set this as a ,e,ber of the parent's children.
	 * Children are sets of annotation ID's.
	 *
	 * The parse annotation span in the subject of analysis (SOFA), document in
	 * most cases, needs to be calculated using the actual start and end token
	 * annotations of the document. So, two maps are used to store the token
	 * annotation starts in the string that was used for parsing with the token
	 * annotation so that we can get the actual token positions from the
	 * document. Note: we can't just use the span info from the parse of the
	 * sentence because extra spaces were added to the sentence from the
	 * document to conform to the needed input format for OpenNLP
	 * TreelinkParser.
	 *
	 *
	 * @param parses
	 * @param parseSet
	 * @param par
	 * @param sent
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void addSentenceParseToDoc(List<Parse> parses,
			AnnotationSet parseSet, Annotation par, Annotation sent)
			throws Exception {
		long start = -1;
		long end = -1;
		for (int i = 0, n = parses.size(); i < n; i++) {
			List<Parse> children = new ArrayList<Parse>();
			Parse p = parses.get(i);
			start = _posiToTokenS.get(p.getSpan().getStart())
					.getStartNodeOffset();
			end = _posiToTokenE.get(p.getSpan().getEnd()).getEndNodeOffset();

			// build feature map for new annotation
			FeatureMap fm = Factory.newFeatureMap();
			fm.put(AnnotationConstants.PARSE_ANNOT_SOFA_INT, Integer.toString(sent.getId()));
			fm.put(AnnotationConstants.PARSE_ANNOT_PARSER, AnnotationConstants.ANNOTATION_PARSER_IMPL_OpenNLP_TreebankParser);
			fm.put(AnnotationConstants.PARSE_ANNOT_TYPE, p.getType());
			fm.put(AnnotationConstants.PARSE_ANNOT_PROB_DOUBLE, Double.toString(p.getProb()));
			fm.put(AnnotationConstants.PARSE_ANNOT_CONSTITUENT_TYPE, p.getLabel());
			if (par != null) {
				fm.put(AnnotationConstants.PARSE_ANNOT_PARENT_INT, Integer.toString(par.getId()));
			}
			// add annotation to parse set
			int id = parseSet.add(start, end,
					AnnotationConstants.PARSE_ANNOT_TYPE, fm);
			// get the newly created annotation
			Annotation a = parseSet.get(id);
			// if we have a parent add this annotation to the parent's set of
			// children
			if (par != null) {
				Set<String> parch =  FeatureValueEncoderDecoder.decodeToSet(par.getFeatures().get(AnnotationConstants.PARSE_ANNOT_CHILDREN_SET));
				if (parch == null) {
					parch = new HashSet<String>();
				}
				parch.add(Integer.toString(id));
				par.getFeatures().put(AnnotationConstants.SENTENCE_ANNOT_PARSES_SET, FeatureValueEncoderDecoder.encodeSet(parch));
			}
			// create a list of child parses
			Parse[] childarr = p.getChildren();
			for (Parse ch : childarr) {
				children.add(ch);
			}
			if (!children.isEmpty()) {
				// recurse on children
				addSentenceParseToDoc(children, parseSet, a, sent);
			}
		}
	}

	/**
	 * For each sentence parse call the print routines.
	 *
	 * @param parses
	 * @param sent
	 */
	private void prettyPrintParses(Parse[] parses, String sent) {

		int strlen = sent.length();

		if (parses.length == 0) {
			System.out.println();
			System.out.println(getPrintSub("-", strlen, " NO PARSE "));
		} else {
			for (int i = 0, n = parses.length; i < n; i++) {
				List<Parse> plist = new ArrayList<Parse>();
				plist.add(parses[i]);
				prettyPrintParse(plist, strlen);
				System.out.println(sent + "\n");
			}
		}
		System.out.println();
	}

	/**
	 * For each level of the parse tree, construct the the String to print for
	 * that line and print it to standard out.
	 *
	 * @param parses
	 * @param len
	 */
	private void prettyPrintParse(List<Parse> parses, int len) {
		List<Parse> children = new ArrayList<Parse>();
		StringBuffer sb1 = new StringBuffer();
		int start = 0;
		int end = -1;
		for (int i = 0, n = parses.size(); i < n; i++) {
			Parse p = parses.get(i);
			start = p.getSpan().getStart();
			end = p.getSpan().getEnd();
			String s = getPrintSub("-", end - start, p.getType());
			int lastEnd = sb1.length();
			int sp = start - lastEnd;
			if (sp < 0) {
				sb1.append(s);
			} else {
				sb1.append(getSpaces(start - lastEnd) + s);
			}
			Parse[] childarr = p.getChildren();
			for (Parse ch : childarr) {
				children.add(ch);
			}
		}
		System.out.println(sb1);
		if (!children.isEmpty()) {
			prettyPrintParse(children, len);
		}
	}

	/**
	 * Build a part of a String to be printed that contains one annotation type.
	 * Potentially many calls to this method will be concatenated to make one
	 * line of parse tree output.
	 *
	 * @param prtChar
	 *            String Character to use to denote span of this annotation.
	 *
	 * @param strlen
	 *            int Length of sentence.
	 *
	 * @param msg
	 *            String Type value to display for this annotation.
	 *
	 * @return A string the length of the Span of the annotation with annotation
	 *         type in the center and prtChar printed out to either side.
	 */
	private String getPrintSub(String prtChar, int strlen, String msg) {
		StringBuffer sb1 = new StringBuffer("");
		String end = "";
		int len = msg.length() + 2;
		if (strlen > len) {
			int lp = (int) Math.floor((strlen - len) / 2);
			for (int i = 0; i < lp; i++) {
				sb1.append(prtChar);
			}
			if (((strlen - len) % 2) == 0) {
				end = prtChar;
			}
		}
		String ret = sb1 + msg + sb1 + end;
		return ret;
	}

	/**
	 * Produce a string of spaces of length x.
	 *
	 * @param x
	 *            int Length.
	 * @return String String of space chars.
	 */
	private String getSpaces(int x) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < x; i++) {
			buff.append(" ");
		}
		return buff.toString();
	}

}
