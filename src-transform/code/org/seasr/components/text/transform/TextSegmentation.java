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

package org.seasr.components.text.transform;

// ==============
// Java Imports
// ==============

import java.util.*;
import java.util.logging.*;

// ===============
// Other Imports
// ===============

// import org.meandre.tools.components.*;
// import org.meandre.tools.components.FlowBuilderAPI.WorkingFlow;

//import org.meandre.components.io.*;
//import org.seasr.components.text.io.file.TextFileToDoc;
//import org.seasr.components.text.opennlp.sentence.OpenNLP_SentenceDetect;
//import org.seasr.components.text.opennlp.tokenize.OpenNLP_Tokenizer;

import org.meandre.core.*;
import org.meandre.annotations.*;
import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.util.Factory;


/**
 * <p>Overview: <br>
 * This component takes in a Document and segments it into documents
 *  of size approximating the input segmentation size in tokens.  This 
 *  component requires that the input document to have been previously 
 *  processed by a sentence splitter and a tokenizer.  Also, the count 
 *  of segments per each document is output.  These counts can be collected 
 *  and used in calculating the total number of new documents (segments) being 
 *  produced.  Some components, down stream, may need to know that they have 
 *  received all documents and will use this output.</p>
 *  <p>Detailed Description: <br>
 *  The output document sizes will be at least greater than 95% of the approximate 
 *  segment size set by the user.  The default is 200 tokens.  Segments always 
 *  and end at sentence boundaries.</p>
 *  <p>This component is capable of segmenting multiple documents in a stream.</p>
 *  <p>Each segment inherits the 'parent' document's title, ID, date, and content. 
 *  However, the original document's feature map is not included.  This can be modified 
 *  in future releases.</p>
 * 
 * @author D. Searsmith
 * 
 * TODO: Unit Tests
 */

@Component(creator = "Duane Searsmith", description = "<p>Overview: <br>"
		+ "This component takes in a Document and segments it into documents " 
		+ "of size approximating the input segmentation size in tokens.  This "
		+ "component requires that the input document to have been previously "
		+ "processed by a sentence splitter and a tokenizer.  Also, the count " 
		+ "of segments per each document is output.  These counts can be collected "
		+ "and used in calculating the total number of new documents (segments) being "
		+ "produced.  Some components, down stream, may need to know that they have "
		+ "received all documents and will use this output.</p>"
		+ "<p>Detailed Description: <br>"
		+ "The output document sizes will be at least greater than 95% of the approximate "
		+ "segment size set by the user.  The default is 200 tokens.  Segments always " 
		+ "and end at sentence boundaries.</p>"
		+ "<p>This component is capable of segmenting multiple documents in a stream.</p>"
		+ "<p>Each segment inherits the 'parent' document's title, ID, date, and content. "
		+ "However, the original document's feature map is not included.  This can be modified "
		+ "in future releases.</p>",
		name = "TextSegmentation", tags = "text document segment")
public class TextSegmentation implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private int _docsProcessed = 0;

	private long _start = 0;

	private static Logger _logger = Logger.getLogger("TextSegmentation");

	// props

	@ComponentProperty(description = "Aproximate segment size in tokens?", name = "segment_size", defaultValue = "500")
	final static String DATA_PROPERTY_SEGMENT_SIZE = "segment_size";

	@ComponentProperty(description = "Verbose output?", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	// io

	@ComponentInput(description = "Input document.", name = "document_in")
	public final static String DATA_INPUT_DOC_IN = "document_in";

	@ComponentOutput(description = "Output document.", name = "document_out")
	public final static String DATA_OUTPUT_DOC_OUT = "document_out";

	@ComponentOutput(description = "Output document segment count.", name = "document_segement_count")
	public final static String DATA_OUTPUT_DOC_SEGMENT_CNT = "document_segement_count";

	// ================
	// Constructor(s)
	// ================
	public TextSegmentation() {
	}

	// ================
	// Static Methods
	// ================

	/**
	 * Test
	 */
	static public void main(String[] args) {
//		// get a flow builder instance
//		FlowBuilderAPI flowBuilder = new FlowBuilderAPI();
//		// get a flow object
//		WorkingFlow wflow = flowBuilder.newWorkingFlow("test");
//		// add a component
//		String pushString = wflow
//				.addComponent("org.meandre.components.io.PushString");
//		// set a component property
//		wflow.setComponentInstanceProp(pushString, "string",
//				"c:/tmp/ThreeLives.txt");
//		// add another component
//		String reader = wflow
//				.addComponent("org.seasr.components.text.io.file.TextFileToDoc");
//
//		// make a connection between two components
//		wflow.connectComponents(pushString, PushString.DATA_OUTPUT_OUTPUT_STRING, reader,
//				TextFileToDoc.DATA_INPUT_FILE_NAME);
//
//		// add another component
//		String sentdetector = wflow
//				.addComponent("org.seasr.components.text.opennlp.sentence.OpenNLP_SentenceDetect");
//
//		// make a connection between two components
//		wflow.connectComponents(reader, TextFileToDoc.DATA_OUTPUT_FILE_DOC,
//				sentdetector, OpenNLP_SentenceDetect.DATA_INPUT_DOC_IN);
//
//		// add another component
//		String tokenizer = wflow
//				.addComponent("org.seasr.components.text.opennlp.tokenize.OpenNLP_Tokenizer");
//
//		// make a connection between two components
//		wflow.connectComponents(sentdetector,
//				OpenNLP_SentenceDetect.DATA_OUTPUT_DOC_OUT, tokenizer,
//				OpenNLP_Tokenizer.DATA_INPUT_DOC_IN);
//
//		// add another component
//		String segmenter = wflow
//				.addComponent("org.seasr.components.text.transform.TextSegmentation");
//
//		// make a connection between two components
//		wflow.connectComponents(tokenizer,
//				OpenNLP_Tokenizer.DATA_OUTPUT_DOC_OUT, segmenter,
//				TextSegmentation.DATA_INPUT_DOC_IN);
//
//		// set a component property
//		wflow.setComponentInstanceProp(segmenter, "verbose", "true");
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

	public int getSegmentSize(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_SEGMENT_SIZE);
		return Integer.parseInt(s);
	}

	// =====================================
	// Interface Impl: ExecutableComponent
	// =====================================

	public void initialize(ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
		_docsProcessed = 0;
		_start = System.currentTimeMillis();
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();
		if (getVerbose(ccp)) {
			_logger.info("TextSegmentation: END EXEC -- File Names Processed: "
					+ _docsProcessed + " in " + (end - _start) / 1000
					+ " seconds\n");
		}
		_docsProcessed = 0;
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		_logger.fine("execute() called");
		long segSz = getSegmentSize(ctx);
		segSz = Math.round(segSz*0.95);
		try {
			Document idoc = (Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);
			Collection<Annotation> annotsSentC = idoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_SENTENCES);
			annotsSentC = ((AnnotationSet) annotsSentC)
					.getAllSortedAsArrayList();
			AnnotationSet annotsTok = idoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);

			Document odoc = Factory.newDocument();
			AnnotationSet annotsOTok = odoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);
			AnnotationSet annotsOSent = odoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_SENTENCES);
			AnnotationSet annotsOSeg = odoc
			.getAnnotations(AnnotationConstants.ANNOTATION_SET_SEGMENTATION);
			
			int tokenCnt = 0;
			odoc.setContent(idoc.getContent());
			odoc.setDate(idoc.getDate());
			odoc.setDocID(idoc.getDocID());
			odoc.setTitle(idoc.getTitle() + " [Segment 1]");
			long segStart = -1;
			int loopcnt = 1;
			int segcnt = 0;
			for (Iterator<Annotation> annotsIT = annotsSentC.iterator(); annotsIT
					.hasNext(); loopcnt++) {
				Annotation sent = annotsIT.next();
				if (segStart == -1){
					segStart = sent.getStartNodeOffset();
				}
				AnnotationSet sentToks = annotsTok.getContained(sent
						.getStartNodeOffset(), sent.getEndNodeOffset());
				
				tokenCnt += sentToks.size();
			
				annotsOTok.addAll(sentToks);
				annotsOSent.add(sent);
				
				if (tokenCnt >= segSz){
					annotsOSeg.add(segStart, sent.getEndNodeOffset(), AnnotationConstants.SEGMENTATION_ANNOT_TYPE, null);
					
//					System.out.println("Segment: " + segcnt);
//					System.out.println("Num Tokens: " + annotsOTok.size());
//					System.out.println("Num Sents: " + annotsOSent.size());
//					System.out.println("\n\n");
							
					
					ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, odoc);

					segcnt++;
					loopcnt = 1;
					tokenCnt = 0;
					segStart = -1;

					odoc = Factory.newDocument();
					annotsOTok = odoc
							.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);
					annotsOSent = odoc
							.getAnnotations(AnnotationConstants.ANNOTATION_SET_SENTENCES);
					annotsOSeg = odoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_SEGMENTATION);
					odoc.setContent(idoc.getContent());
					odoc.setDate(idoc.getDate());
					odoc.setDocID(idoc.getDocID());
					odoc.setTitle(idoc.getTitle() + " [Segment " + (segcnt + 1) + "]");

					if (this.getVerbose(ctx)) {
						if (Math.IEEEremainder(segcnt, 100) == 0) {
							System.out.println("TextSegmentation -- Docs Processed: "
									+ segcnt);
						}
					}

				
				}
			}
			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_SEGMENT_CNT, segcnt);
			_docsProcessed++;
			if (getVerbose(ctx)){
			_logger.info("TextSegmentation :: Doc: " + idoc.getTitle()
					+ " created " + segcnt
					+ " segments.");
			}
			idoc.free();
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: TextSegmentation.execute()");
			throw new ComponentExecutionException(ex);
		}
	}
}
