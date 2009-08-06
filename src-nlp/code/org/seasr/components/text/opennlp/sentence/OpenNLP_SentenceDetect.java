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

package org.seasr.components.text.opennlp.sentence;

//==============
//Java Imports
//==============
import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

import opennlp.tools.lang.english.SentenceDetector;

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
import org.seasr.components.text.opennlp.OpenNLPBaseUtilities;


/**
 * @author D. Searsmith
 *
 * TODO: Testing, Unit Tests
 */

@Component(creator = "Duane Searsmith",

		description = "<p>Overview: <br>"
			+ "This component wraps the SentenceDetector from the OpenNLP package.</p>",
		name = "OpenNLP_SentenceDetect",
		tags = "sentence text opennlp document",
		dependency={"maxent-models.jar","trove-2.0.3.jar", "components-foundry-support.jar"},
        baseURL="meandre://seasr.org/components/")
public class OpenNLP_SentenceDetect extends OpenNLPBaseUtilities {

	// ==============
	// Data Members
	// ==============

	// Options
	private int m_docsProcessed = 0;

	private SentenceDetector _sentDetector = null;

	private static Logger _logger;

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	public final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentInput(description = "Input document.", name = "Document")
	public final static String DATA_INPUT_DOC_IN = "Document";

	@ComponentOutput(description = "Output document.", name = "Document")
	public final static String DATA_OUTPUT_DOC_OUT = "Document";

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	@Override
    public void initializeCallBack(ComponentContextProperties ccp) throws Exception {
	    super.initializeCallBack(ccp);

		_logger = console;
		m_docsProcessed = 0;

		// Write model file to disk if it doesn't already
		// exist.
        try {
            _sentDetector = new SentenceDetector(sOpenNLPDir+"sentdetect"+File.separator+
                    sLanguage.substring(0,1).toUpperCase()+sLanguage.substring(1)+"SD.bin.gz");
        }
        catch ( Throwable t ) {
            console.severe("Failed to open tokenizer model for " + sLanguage);
            throw new ComponentExecutionException(t);
        }
	}

	@Override
    public void dispose(ComponentContextProperties ccp) {
		long end = System.currentTimeMillis();
		_logger.info("END EXEC -- OpenNLP_SentenceDetect -- Docs Processed: "
					+ m_docsProcessed);
		m_docsProcessed = 0;
		_sentDetector = null;
	}

	@Override
    public void executeCallBack(ComponentContext ctx)
			throws Exception {

			Document idoc = (Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);

			// =============================
			AnnotationSet annots = idoc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_SENTENCES);
			String s = idoc.getContent();
			int[] starts = _sentDetector.sentPosDetect(idoc.getContent());

			if (starts.length == 0) {
				annots.add(0, s.length(),
						AnnotationConstants.SENTENCE_ANNOT_TYPE, null);
			} else {

				boolean leftover = starts[starts.length - 1] != s.length();
				annots.add(0, starts[0],
						AnnotationConstants.SENTENCE_ANNOT_TYPE, null);
				for (int si = 1; si < starts.length; si++) {
					annots.add(starts[si - 1], starts[si],
							AnnotationConstants.SENTENCE_ANNOT_TYPE, null);
				}

				if (leftover) {
					annots.add(starts[starts.length - 1], s.length(),
							AnnotationConstants.SENTENCE_ANNOT_TYPE, null);
				}
			}

			// ============================

			if (getVerbose(ctx)) {
				AnnotationSet sentences = idoc
						.getAnnotations(AnnotationConstants.ANNOTATION_SET_SENTENCES);
				Iterator<Annotation> itty = sentences.iterator();
				int i = 0;
				while (itty.hasNext()) {
					Annotation ann = itty.next();
					_logger.info("Sent "
							+ i
							+ ": "
							+ s.substring((int) ann.getStartNodeOffset(),
									(int) ann.getEndNodeOffset()));
					i++;
				}
			}

			_logger.info("Document parsed.  "
						+ idoc.getAnnotations(AnnotationConstants.ANNOTATION_SET_SENTENCES).get(
								AnnotationConstants.SENTENCE_ANNOT_TYPE).size()
						+ " tokens created.");
			
			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, idoc);
			m_docsProcessed++;
	}
}
