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

package org.seasr.components.text.opennlp.ie;

// ==============
// Java Imports
// ==============

// ===============
// Other Imports
// ===============
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import opennlp.maxent.io.BinaryGISModelReader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.parser.Parse;
import opennlp.tools.util.Span;

import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextProperties;
import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.opennlp.OpenNLPBaseUtilities;
import org.seasr.components.text.util.feature_maps.FeatureValueEncoderDecoder;

/**
 * @author D. Searsmith
 *
 * TODO: Testing, Unit Tests
 */

@Component(creator = "Duane Searsmith",

description = "<p>Overview:<br> This component wraps the OpenNLP NameFinder class.  The "
		+ "NameFinder class does name entity extraction.  The NameFinder can be run "
		+ "Part-of-Speech tagged text or text that has been run through the OpenNLP Treebank "
		+ "parser (use the 'use_treebank' option).</p>",

name = "OpenNLP_NameFinder", tags = "sentence text opennlp document",
dependency = { "opennlp-english-models.jar" },
baseURL="meandre://seasr.org/components/")
public class OpenNLP_NameFinder extends OpenNLPBaseUtilities {

	// ==============
	// Data Members
	// ==============

	// Options
	private int m_docsProcessed = 0;

	private long m_start = 0;


	volatile private NameFinderME[] _finders = null;

	volatile private String[] _types = null;

	// ============
	// Properties
	// ============

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Use treebank parse format for input? A boolean value (true or false).", name = "use_treebank", defaultValue = "false")
	final static String DATA_PROPERTY_USE_TREEBANK = "use_treebank";

	@ComponentProperty(description = "Entity types (comma delimited list).", name = "entities", defaultValue = "person,organization,location,time,money,percentage,date")
	final static String DATA_PROPERTY_ENTITIES = "entities";

	// io

	@ComponentInput(description = "Input document.", name = "Document")
	public final static String DATA_INPUT_DOC_IN = "Document";

	@ComponentOutput(description = "Output document.", name = "Document")
	public final static String DATA_OUTPUT_DOC_OUT = "Document";

	private static Logger _logger;

	// ================
	// Constructor(s)
	// ================

	public OpenNLP_NameFinder() {
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
		// // add another component
		// String finder = wflow
		// .addComponent("org.seasr.meandre.components.t2k.ie.opennlp.OpenNLP_NameFinder");
		//
		// // make a connection between two components
		// wflow.connectComponents(parser,
		// OpenNLP_TreebankParser.DATA_OUTPUT_DOC_OUT, finder,
		// OpenNLP_NameFinder.DATA_INPUT_DOC_IN);
		//
		// // set a component property
		// wflow.setComponentInstanceProp(finder, "verbose", "true");
		//
		// // set a component property
		// wflow.setComponentInstanceProp(finder,
		// OpenNLP_NameFinder.DATA_PROPERTY_USE_TREEBANK, "true");
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

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getUseTreebankFmt(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_USE_TREEBANK);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public String getEntityTypes(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_ENTITIES);
		return s;
	}

	public void initializeCallBack(ComponentContextProperties ccp) throws Exception {
	    super.initializeCallBack(ccp);

	    _logger = console;
		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();

		// Write model files to disk if they don't already
		// exist.

		StringTokenizer toker = new StringTokenizer(getEntityTypes(ccp), ",");
		int numTypes = toker.countTokens();
		_types = new String[numTypes];
		for (int i = 0, n = numTypes; i < n; i++) {
			_types[i] = toker.nextToken().trim();
		}
		try {
			_finders = new NameFinderME[numTypes];
			for (int i = 0; i < numTypes; i++) {
				String modelName = _types[i];
				File modelFile = new File(sOpenNLPDir + "namefind"+File.separator+ modelName + ".bin.gz");
				if (!modelFile.exists()) {
					throw new RuntimeException(
							"Unable to find resource file: "
									+ modelFile.toString());
				}
				_logger.info("Loading ... " + modelFile.getCanonicalPath());
				_finders[i] = new NameFinderME(new BinaryGISModelReader(
						modelFile).getModel());
				_logger.info("Finished loading ... "
						+ modelFile.getCanonicalPath());
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new RuntimeException(ioe);
		}
	}

	public void disposeCallBack(ComponentContextProperties ccp) throws Exception {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();
		if (getVerbose(ccp)) {
			_logger.info("\nEND EXEC -- OpenNLP_NameFinder -- Docs Processed: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}
		m_docsProcessed = 0;
		_types = null;
		_finders = null;
	}

	@SuppressWarnings("unchecked")
	public void executeCallBack(ComponentContext ctx)
			throws Exception {

			Document idoc = (Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);

			// =============================
			AnnotationSet neSet = idoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_ENTITIES);
			AnnotationSet annotsSent = idoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_SENTENCES);
			List<Annotation> sents = annotsSent.getAllSortedAsArrayList();
			AnnotationSet annotsTok = idoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);
			Map<Object, String>[] prevTokenMaps = createPrevTokenMaps(_finders);
			String[][] finderTags = new String[_finders.length][];
			for (Iterator<Annotation> annotsIT = sents.iterator(); annotsIT
					.hasNext();) {
				Annotation sent = annotsIT.next();
				AnnotationSet sentToks = annotsTok.getContained(sent
						.getStartNodeOffset(), sent.getEndNodeOffset());
				/*
				 * I believe the following if statement is supposed to fire on
				 * paragraph boundaries. I don't think that in this instance it
				 * will ever fire because we don't mark paragraph boundaries as
				 * (empty) sentence annotations.
				 *
				 * TODO: When we include paragraph annotations, then use those
				 * to trigger this clearing of the previous token maps as well
				 * as the occurrence of empty sentence annotations.
				 */
				if (sentToks.isEmpty()) {
					clearPrevTokenMaps(prevTokenMaps);
					continue;
				}

				if (getUseTreebankFmt(ctx)) {
					Set<String> ptbAnnots = FeatureValueEncoderDecoder
							.decodeToSet(sent
									.getFeatures()
									.get(
											AnnotationConstants.SENTENCE_PENNTREEBANK_FMT_ANNOT_PARSES_SET));
					if (ptbAnnots == null) {
						_logger
								.info("Sentence has no Penn Treebank format parse data.");
						continue;
					}
					Set<String> newSet = new HashSet<String>();
					for (String ptbAnnot : ptbAnnots) {
						Parse p = Parse.parseParse(ptbAnnot);
						Parse[] tokens = p.getTagNodes();
						for (int fi = 0, fl = _finders.length; fi < fl; fi++) {
							finderTags[fi] = _finders[fi].find(tokens,
									prevTokenMaps[fi]);
						}
						updatePrevTokenMaps(prevTokenMaps, tokens, finderTags);
						for (int fi = 0, fl = _finders.length; fi < fl; fi++) {
							int start = -1;
							List names = new ArrayList(5);
							for (int ti = 0, tl = tokens.length; ti < tl; ti++) {
								if ((finderTags[fi][ti]
										.equals(NameFinderME.START) || finderTags[fi][ti]
										.equals(NameFinderME.OTHER))) {
									if (start != -1) {
										names.add(new Span(start, ti - 1));
									}
									start = -1;
								}
								if (finderTags[fi][ti]
										.equals(NameFinderME.START)) {
									start = ti;
								}
							}
							if (start != -1) {
								names.add(new Span(start, tokens.length - 1));
							}
							addNames(_types[fi], names, tokens);
						}
						StringBuffer sbuff = new StringBuffer();
						p.show(sbuff);
						newSet.add(sbuff.toString());
						System.out.println(sbuff);
					}
					ptbAnnots.clear();
					sent
							.getFeatures()
							.put(
									AnnotationConstants.SENTENCE_PENNTREEBANK_FMT_ANNOT_PARSES_SET,
									FeatureValueEncoderDecoder
											.encodeSet(newSet));
				}
				List<Annotation> sentToksList = sentToks
						.getAllSortedAsArrayList();
				String[] sentToksArr = new String[sentToksList.size()];
				for (int i = 0, n = sentToksList.size(); i < n; i++) {
					sentToksArr[i] = sentToksList.get(i).getContent(idoc);
				}
				for (int i = 0, n = _types.length; i < n; i++) {
					finderTags[i] = _finders[i].find(sentToksArr,
							prevTokenMaps[i]);
				}
				updatePrevTokenMaps(prevTokenMaps, sentToksArr, finderTags);

				int[] start = new int[_finders.length];
				for (int i = 0, n = start.length; i < n; i++) {
					start[i] = -1;
				}
				for (int ti = 0, tl = sentToksArr.length; ti < tl; ti++) {
					for (int fi = 0, fl = _finders.length; fi < fl; fi++) {
						// check for end tags
						if ((ti != 0) && (start[fi] != -1)) {
							if ((finderTags[fi][ti].equals(NameFinderME.START) || finderTags[fi][ti]
									.equals(NameFinderME.OTHER))
									&& (finderTags[fi][ti - 1]
											.equals(NameFinderME.START) || finderTags[fi][ti - 1]
											.equals(NameFinderME.CONTINUE))) {
								neSet.add(sentToksList.get(start[fi])
										.getStartNodeOffset(), sentToksList
										.get(ti - 1).getEndNodeOffset(),
										_types[fi], null);
							}
						}
					}
					// check for start tags
					for (int fi = 0, fl = _finders.length; fi < fl; fi++) {
						if (finderTags[fi][ti].equals(NameFinderME.START)) {
							start[fi] = ti;
						}
					}
				}
				// final end tags
				if (sentToksArr.length != 0) {
					for (int fi = 0, fl = _finders.length; fi < fl; fi++) {
						if (finderTags[fi][sentToksArr.length - 1]
								.equals(NameFinderME.START)
								|| finderTags[fi][sentToksArr.length - 1]
										.equals(NameFinderME.CONTINUE)) {
							neSet.add(sentToksList.get(start[fi])
									.getStartNodeOffset(), sentToksList.get(
									sentToksArr.length - 1).getEndNodeOffset(),
									_types[fi], null);
						}
					}
				}
			}

			// ============================

			if (getVerbose(ctx)) {
				AnnotationSet entities = idoc
						.getAnnotations(AnnotationConstants.ANNOTATION_SET_ENTITIES);
				Iterator<Annotation> itty = entities.iterator();
				while (itty.hasNext()) {
					Annotation ann = itty.next();
					_logger.info("Entity: " + ann.getContent(idoc) + " <"
							+ ann.getType() + ">");
					ctx.getOutputConsole().println("Entity: " + ann.getContent(idoc) + " :"
							+ ann.getType());
				}
			}

			if (getVerbose(ctx)) {
				_logger.info("\n\nDocument parsed.  "
						+ idoc.getAnnotations(
								AnnotationConstants.ANNOTATION_SET_ENTITIES)
								.get().size() + " entity tokens created.\n\n");
				ctx.getOutputConsole().println("Document parsed.  "
						+ idoc.getAnnotations(
								AnnotationConstants.ANNOTATION_SET_ENTITIES)
								.get().size() + " entity tokens created.");
			}
			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, idoc);
			m_docsProcessed++;
	}

	@SuppressWarnings("unchecked")
	private Map<Object, String>[] createPrevTokenMaps(NameFinderME[] finders) {
		HashMap<Object, String>[] prevTokenMaps = new HashMap[finders.length];
		for (int i = 0, n = finders.length; i < n; i++) {
			prevTokenMaps[i] = new HashMap<Object, String>();
		}
		return prevTokenMaps;
	}

	private void clearPrevTokenMaps(Map<Object, String>[] prevTokenMaps) {
		for (int mi = 0, ml = prevTokenMaps.length; mi < ml; mi++) {
			prevTokenMaps[mi].clear();
		}
	}

	private void updatePrevTokenMaps(Map<Object, String>[] prevTokenMaps,
			Object[] tokens, String[][] finderTags) {
		for (int mi = 0, ml = prevTokenMaps.length; mi < ml; mi++) {
			for (int ti = 0, tn = tokens.length; ti < tn; ti++) {
				prevTokenMaps[mi].put(tokens[ti], finderTags[mi][ti]);
			}
		}
	}

	private void addNames(String tag, List<Span> names, Parse[] tokens) {
		for (int ni = 0, nn = names.size(); ni < nn; ni++) {
			Span nameTokenSpan = (Span) names.get(ni);
			Parse startToken = tokens[nameTokenSpan.getStart()];
			Parse endToken = tokens[nameTokenSpan.getEnd()];
			Parse commonParent = startToken.getCommonParent(endToken);
			if (commonParent != null) {
				Span nameSpan = new Span(startToken.getSpan().getStart(),
						endToken.getSpan().getEnd());
				if (nameSpan.equals(commonParent.getSpan())) {
					commonParent.insert(new Parse(commonParent.getText(),
							nameSpan, tag, 1.0));
				} else {
					Parse[] kids = commonParent.getChildren();
					boolean crossingKids = false;
					for (int ki = 0, kn = kids.length; ki < kn; ki++) {
						if (nameSpan.crosses(kids[ki].getSpan())) {
							crossingKids = true;
						}
					}
					if (!crossingKids) {
						commonParent.insert(new Parse(commonParent.getText(),
								nameSpan, tag, 1.0));
					} else {
						if (commonParent.getType().equals("NP")) {
							Parse[] grandKids = kids[0].getChildren();
							if (grandKids.length > 1
									&& nameSpan
											.contains(grandKids[grandKids.length - 1]
													.getSpan())) {
								commonParent.insert(new Parse(commonParent
										.getText(), commonParent.getSpan(),
										tag, 1.0));
							}
						}
					}
				}
			}
		}
	}
}
