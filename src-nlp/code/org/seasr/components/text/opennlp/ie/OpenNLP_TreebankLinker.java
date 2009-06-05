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

//==============
//Java Imports
//==============

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.Linker;
import opennlp.tools.coref.LinkerMode;
import opennlp.tools.coref.mention.DefaultParse;
import opennlp.tools.coref.mention.MentionContext;
import opennlp.tools.lang.english.TreebankLinker;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.ParserME;
import opennlp.tools.util.Span;

import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
import org.seasr.components.text.opennlp.OpenNLPBaseUtilities;

//import net.didion.jwnl.*;

/**
 * @author D. Searsmith
 *
 * TODO: Testing, Unit Tests
 * TODO: This class has a bug write now interfacing with wordnet libraries which are required
 * for the OpenNLPlinker to function.  This class is currently NON-FUNCTIONAL. FIX THIS.
 */

@Component(creator = "Duane Searsmith",

		description = "<p>Overview: <br>"
			+ "This class is currently non-functional.  Do not use.",

		name = "OpenNLP_TreebankLinker", tags = "sentence text opennlp document",
		dependency = { "opennlp-english-models.jar" },
        baseURL="meandre://seasr.org/components/")
public class OpenNLP_TreebankLinker extends OpenNLPBaseUtilities {

	// ==============
	// Data Members
	// ==============

	Linker _linker = null;

	private int m_docsProcessed = 0;

	private long m_start = 0;

	private String _version = "1.0";

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Model files for v1.3.0.",
			name = "mod_files",
			defaultValue = "plmodel.bin.gz," +
			"sim.bin.gz," +
			"cmodel.nr.bin.gz," +
			"imodel.nr.bin.gz," +
			"fmodel.nr.bin.gz," +
			"pnmodel.bin.gz," +
			"num.bin.gz," +
			"pmodel.nr.bin.gz," +
			"gen.fem," +
			"plmodel.nr.bin.gz," +
			"defmodel.bin.gz," +
			"gen.bin.gz," +
			"tmodel.nr.bin.gz," +
			"defmodel.nr.bin.gz," +
			"pnmodel.nr.bin.gz," +
			"cmodel.bin.gz," +
			"imodel.bin.gz," +
			"pmodel.bin.gz," +
			"gen.mas," +
			"fmodel.bin.gz," +
			"tmodel.bin.gz," +
			"acronyms")
	final static String DATA_PROPERTY_MODEL_FILES_VER_1_3_0 = "mod_files";

	// io

	@ComponentInput(description = "Input document.", name = "Document")
	public final static String DATA_INPUT_DOC_IN = "Document";

	@ComponentOutput(description = "Output document.", name = "Document")
	public final static String DATA_OUTPUT_DOC_OUT = "Document";

	private static Logger _logger = Logger.getLogger("OpenNLP_TreebankLinker");

	// ================
	// Constructor(s)
	// ================

	public OpenNLP_TreebankLinker() {
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
//				"c:/tmp/sample.txt");
//		// add another component
//		String reader = wflow
//				.addComponent("org.seasr.meandre.components.t2k.io.file.TextFileToDoc");
//
//		// // set a component property
//		wflow.setComponentInstanceProp(reader,
//				TextFileToDoc.DATA_PROPERTY_ADD_SPACE_AT_NEW_LINES, "true");
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
////		String tokenizer = wflow
////				.addComponent("org.seasr.meandre.components.t2k.tokenize.opennlp.OpenNLP_Tokenizer");
//
//		// add another component
//		String tokenizer = wflow
//				.addComponent("org.seasr.meandre.components.t2k.tokenize.brown.Tokenizer_Comp");
//
//		// make a connection between two components
//		wflow.connectComponents(sentdetector,
//				OpenNLP_SentenceDetect.DATA_OUTPUT_DOC_OUT, tokenizer,
//				Tokenizer_Comp.DATA_INPUT_DOC_IN);
//
//		// add another component
//		String parser = wflow
//				.addComponent("org.seasr.meandre.components.t2k.parser.opennlp.OpenNLP_TreebankParser");
//
//		// make a connection between two components
//		wflow.connectComponents(tokenizer,
//				Tokenizer_Comp.DATA_OUTPUT_DOC_OUT, parser,
//				DATA_INPUT_DOC_IN);
//
//		// set a component property
//		wflow.setComponentInstanceProp(parser,
//				OpenNLP_TreebankParser.DATA_PROPERT_PARSE_IN_ORDER, "true");
//
//		// set a component property
//		// wflow.setComponentInstanceProp(parser,
//		// OpenNLP_TreebankParser.DATA_PROPERTY_VERBOSE, "true");
//
//		wflow.setComponentInstanceProp(parser,
//		OpenNLP_TreebankParser.DATA_PROPERTY_TREEBANK_FMT, "true");
//
////		wflow.setComponentInstanceProp(parser,
////				OpenNLP_TreebankParser.DATA_PROPERT_NUMBER_OF_PARSES, "5");
//
//		// add another component
//		String finder = wflow
//				.addComponent("org.seasr.meandre.components.t2k.ie.opennlp.OpenNLP_NameFinder");
//
//		// make a connection between two components
//		wflow.connectComponents(parser,
//				OpenNLP_TreebankParser.DATA_OUTPUT_DOC_OUT, finder,
//				OpenNLP_NameFinder.DATA_INPUT_DOC_IN);
//
//		// set a component property
//		wflow.setComponentInstanceProp(finder, "verbose", "true");
//
//		// set a component property
//		wflow.setComponentInstanceProp(finder, OpenNLP_NameFinder.DATA_PROPERTY_USE_TREEBANK, "true");
//
//		// add another component
//		String linker = wflow
//				.addComponent("org.seasr.meandre.components.t2k.ie.opennlp.OpenNLP_TreebankLinker");
//
//		// make a connection between two components
//		wflow.connectComponents(finder,
//				DATA_OUTPUT_DOC_OUT, linker,
//				DATA_INPUT_DOC_IN);
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

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public String getModelFiles(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_MODEL_FILES_VER_1_3_0);
		return s;
	}

	public void initializeCallBack(ComponentContextProperties ccp) throws Exception {
	    super.initializeCallBack(ccp);

		System.getProperties().put("WNSEARCHDIR", "c:/tmp/DICT1.5");
//		try {
//			JWNL.initialize(new FileInputStream("c:/JAVA/jwnl14-rc1/config/file_properties.xml"));
//		} catch(Exception e){}
		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();

		// Write model files to disk if they don't already
		// exist.

		StringTokenizer toker = new StringTokenizer(getModelFiles(ccp), ",");
		int numFiles = toker.countTokens();
		String[] files = new String[numFiles];
		for (int i = 0, n = numFiles; i < n; i++) {
			files[i] = toker.nextToken().trim();
		}
		try {
			File modelFile = null;
			for (int i = 0; i < numFiles; i++) {
				String modelName = files[i];
				modelFile = new File(sOpenNLPDir + "coref/" + modelName);
				if (!modelFile.exists()) {
					throw new RuntimeException(
							"Unable to find resource file: "
									+ modelFile.toString());
				}
				_logger.info("Finished installing ... "
						+ modelFile.getCanonicalPath());
			}
			_linker = new TreebankLinker(modelFile.getParentFile().getCanonicalPath(), LinkerMode.TEST);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new RuntimeException(ioe);
		}
	}

	public void disposeCallBack(ComponentContextProperties ccp) throws Exception {
		long end = System.currentTimeMillis();
		if (getVerbose(ccp)) {
			_logger.info("\nEND EXEC -- OpenNLP_TreebankLinker -- Docs Processed: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}
		m_docsProcessed = 0;
	}


	@SuppressWarnings("unchecked")
	public void executeCallBack(ComponentContext ctx)
			throws Exception {
		try {
			throw new ComponentExecutionException("This class is currently non functional. Please do not use.");
//			Document idoc = (Document) ctx
//					.getDataComponentFromInput(DATA_INPUT_DOC_IN);
//
//			// =============================
//			AnnotationSet neSet = idoc
//			.getAnnotations(AnnotationConstants.ANNOTATION_SET_ENTITIES);
//			AnnotationSet annotsSent = idoc
//					.getAnnotations(AnnotationConstants.ANNOTATION_SET_SENTENCES);
//			List<Annotation> sents = annotsSent.getAllSortedAsArrayList();
//			AnnotationSet annotsTok = idoc
//					.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);
//			for (Iterator<Annotation> annotsIT = sents.iterator(); annotsIT
//					.hasNext();) {
//				Annotation sent = annotsIT.next();
//				/*
//				 * I believe the following if statement is supposed to fire on
//				 * paragraph boundaries. I don't think that in this instance it
//				 * will ever fire because we don't mark paragraph boundaries as
//				 * (empty) sentence annotations.
//				 *
//				 * TODO: When we include paragraph annotations, then use those
//				 * to trigger this clearing of the previous token maps as well
//				 * as the occurrence of empty sentence annotations.
//				 */
//// if (sentToks.isEmpty()) {
//// clearPrevTokenMaps(prevTokenMaps);
//// continue;
//// }
//
//					Set<String> ptbAnnots = (Set<String>) sent
//							.getFeatures()
//							.get(
//									AnnotationConstants.SENTENCE_PENNTREEBANK_FMT_ANNOT_PARSES);
//					if (ptbAnnots == null) {
//						_logger
//								.info("Sentence has no Penn Treebank format parse data.");
//						continue;
//					}
//
//				    int sentenceNumber = 0;
//				    List document = new ArrayList();
//				    List parses = new ArrayList();
//				    for (String line : ptbAnnots) {
//				      if (line.equals("")) {
//				        DiscourseEntity[] entities = _linker.getEntities((Mention[]) document.toArray(new Mention[document.size()]));
//				        // showEntities(entities);
//				        new CorefParse(parses,entities).show();
//				        sentenceNumber=0;
//				        document.clear();
//				        parses.clear();
//				      }
//				      else {
//				        Parse p = Parse.parseParse(line);
//				        parses.add(p);
//				        Mention[] extents = _linker.getMentionFinder().getMentions(new DefaultParse(p,sentenceNumber));
//				        // construct new parses for mentions which don't have
//						// constituents.
//				        for (int ei=0,en=extents.length;ei<en;ei++) {
//				          // System.err.println("PennTreebankLiner.main:
//							// "+ei+" "+extents[ei]);
//
//				          if (extents[ei].getParse() == null) {
//				            Parse snp = new Parse(p.getText(),extents[ei].getSpan(),"NML",1.0);
//				            p.insert(snp);
//				            extents[ei].setParse(new DefaultParse(snp,sentenceNumber));
//				          }
//
//				        }
//				        document.addAll(Arrays.asList(extents));
//				        sentenceNumber++;
//				      }
//				    }
//				    if (document.size() > 0) {
//				      DiscourseEntity[] entities = _linker.getEntities((Mention[]) document.toArray(new Mention[document.size()]));
//				      // showEntities(entities);
//				      (new CorefParse(parses,entities)).show();
//				    }
//			}
//			// ============================
//
//// if (getVerbose(ctx)) {
//// AnnotationSet entities = idoc
//// .getAnnotations(AnnotationConstants.ANNOTATION_SET_ENTITIES);
//// Iterator<Annotation> itty = entities.iterator();
//// while (itty.hasNext()) {
//// Annotation ann = itty.next();
//// _logger.info("Entity: " + ann.getContent(idoc) + " <"
//// + ann.getType() + ">");
//// }
//// }
////
//// if (getVerbose(ctx)) {
//// _logger.info("\n\nDocument parsed. "
//// + idoc.getAnnotations(
//// AnnotationConstants.ANNOTATION_SET_ENTITIES)
//// .get().size() + " entity tokens created.\n\n");
//// }
//			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, idoc);
//			m_docsProcessed++;
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: OpenNLP_TreebankLinker.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

	class CorefParse {

		  private Map parseMap;
		  private List parses;

		  public CorefParse(List parses, DiscourseEntity[] entities) {
		    this.parses = parses;
		    parseMap = new HashMap();
		    for (int ei=0,en=entities.length;ei<en;ei++) {
		      if (entities[ei].getNumMentions() > 1) {
		        for (Iterator mi=entities[ei].getMentions();mi.hasNext();) {
		          MentionContext mc = (MentionContext) mi.next();
		          Parse mentionParse = ((DefaultParse) mc.getParse()).getParse();
		          parseMap.put(mentionParse,new Integer(ei+1));
		        }
		      }
		    }
		  }

		  public void show() {
		    for (int pi=0,pn=parses.size();pi<pn;pi++) {
		      Parse p = (Parse) parses.get(pi);
		      show(p);
		      System.out.println();
		    }
		  }

		  private void show(Parse p) {
		    int start;
		    start = p.getSpan().getStart();
		    if (!p.getType().equals(ParserME.TOK_NODE)) {
		      System.out.print("(");
		      System.out.print(p.getType());
		      if (parseMap.containsKey(p)) {
		        System.out.print("#"+parseMap.get(p));
		      }
		      // System.out.print(p.hashCode()+"-"+parseMap.containsKey(p));
		      System.out.print(" ");
		    }
		    Parse[] children = p.getChildren();
		    for (int pi=0,pn=children.length;pi<pn;pi++) {
		      Parse c = children[pi];
		      Span s = c.getSpan();
		      if (start < s.getStart()) {
		        System.out.print(p.getText().substring(start, s.getStart()));
		      }
		      show(c);
		      start = s.getEnd();
		    }
		    System.out.print(p.getText().substring(start, p.getSpan().getEnd()));
		    if (!p.getType().equals(ParserME.TOK_NODE)) {
		      System.out.print(")");
		    }
		  }
		}
}
