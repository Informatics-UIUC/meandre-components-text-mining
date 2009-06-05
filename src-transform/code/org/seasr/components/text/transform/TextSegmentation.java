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

import java.util.Collection;
import java.util.Iterator;

import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.components.abstracts.AbstractExecutableComponent;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
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
		name = "TextSegmentation", tags = "text document segment",
        baseURL="meandre://seasr.org/components/")
public class TextSegmentation extends AbstractExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private int _docsProcessed = 0;

	private long _start = 0;

	// props

	@ComponentProperty(description = "Aproximate segment size in tokens?", name = "segment_size", defaultValue = "500")
	public final static String DATA_PROPERTY_SEGMENT_SIZE = "segment_size";

	// io

	@ComponentInput(description = "Input document.", name = "document_in")
	public final static String DATA_INPUT_DOC_IN = "document_in";

	@ComponentOutput(description = "Output document.", name = "document_out")
	public final static String DATA_OUTPUT_DOC_OUT = "document_out";

	@ComponentOutput(description = "Output document segment count.", name = "document_segement_count")
	public final static String DATA_OUTPUT_DOC_SEGMENT_CNT = "document_segement_count";

	// ================
	// Public Methods
	// ================

	public int getSegmentSize(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_SEGMENT_SIZE);
		return Integer.parseInt(s);
	}

	// =====================================
	// Interface Impl: ExecutableComponent
	// =====================================

	public void initializeCallBack(ComponentContextProperties ccp)
    throws Exception {
		_docsProcessed = 0;
		_start = System.currentTimeMillis();
	}

	public void disposeCallBack(ComponentContextProperties ccp)
    throws Exception {
		long end = System.currentTimeMillis();
		console.info("TextSegmentation: END EXEC -- File Names Processed: "
				+ _docsProcessed + " in " + (end - _start) / 1000
				+ " seconds");
		_docsProcessed = 0;
	}

	public void executeCallBack(ComponentContext cc)
    throws Exception {
		long segSz = getSegmentSize(cc);
		segSz = Math.round(segSz*0.95);
		try {
			Document idoc = (Document) cc
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

					cc.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, odoc);

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

					if (Math.IEEEremainder(segcnt, 100) == 0) {
						console.info("TextSegmentation -- Docs Processed: "
								+ segcnt);
					}
				}
			}
			cc.pushDataComponentToOutput(DATA_OUTPUT_DOC_SEGMENT_CNT, segcnt);
			_docsProcessed++;

			console.info(segcnt + " segments created.");
			idoc.free();
		} catch (Exception ex) {
			ex.printStackTrace();
			cc.getLogger().severe(ex.getMessage());
			cc.getLogger().severe("ERROR: TextSegmentation.execute()");
			throw new ComponentExecutionException(ex);
		}
	}
}
