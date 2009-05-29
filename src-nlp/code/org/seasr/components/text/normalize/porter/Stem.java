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

package org.seasr.components.text.normalize.porter;

// ==============
// Java Imports
// ==============

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import org.seasr.components.text.normalize.porter.support.*;

// ===============
// Other Imports
// ===============

// import org.meandre.tools.components.*;
// import org.meandre.tools.components.FlowBuilderAPI.WorkingFlow;

import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.meandre.components.abstracts.AbstractExecutableComponent;
import org.meandre.core.*;
import org.meandre.annotations.*;
import org.seasr.components.text.util.feature_maps.FeatureValueEncoderDecoder;

/**
 *
 * <p>
 * Overview: <br>
 * This component transforms terms into their word stems. In this way, different
 * forms of the same word (plurals etc...) will be recognized as the same term.
 * The algorithm used is the Porter stemming method.
 * </p>
 * <p>
 * References: <br>
 * See: http://www.tartarus.org/~martin/PorterStemmer/
 * </p>
 * <p>
 * Data Type Restrictions: <br>
 * The input document must have been tokenized.
 * </p>
 * <p>
 * Data Handling: <br>
 * This component will modify (as described above) the document object that is
 * input.
 * </p>
 * <p>
 * Scalability: <br>
 * This component makes one pass over the token list resulting in linear time
 * complexity per the number of tokens. Memory usage is proportional to the
 * number tokens.
 * </p>
 * <p>
 * Trigger Criteria: <br>
 * All.
 * </p>
 *
 *
 * @author D. Searsmith
 *
 * TODO: Testing, Unit Tests
 *
 */
@Component(creator = "Duane Searsmith",

description = "<p>Overview: <br>"
		+ "This component transforms terms into their word stems. In this way, "
		+ "different forms of the same word (plurals etc...) will be recognized as the same term."
		+ "The algorithm used is the Porter stemming method."
		+ "</p>"

		+ "<p>References: <br>"
		+ "See: http://www.tartarus.org/~martin/PorterStemmer/"
		+ "</p>"

		+ "<p>Data Type Restrictions: <br>"
		+ "The input document must have been tokenized."
		+ "</p>"

		+ "<p>Data Handling: <br>"
		+ "This component will modify (as described above) the document object that is input."
		+ "</p>"

		+ "<p>Scalability: <br>"
		+ "This compnent makes one pass over the token list resulting in linear time complexity "
		+ "per the number of tokens. Memory usage is proportional to the number tokens."
		+ "</p>"

		+ "<p>Trigger Criteria: <br>" + "All." + "</p>",

name = "Stem", tags = "nlp text document normalize stem",
baseURL="meandre://seasr.org/components/")
public class Stem  extends AbstractExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private int m_docsProcessed = 0;
	private PorterStemmer _stemmer = null;

	private static Logger _logger = Logger.getLogger("Stem");

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	// IO

	@ComponentInput(description = "Document object.", name = "document")
	public final static String DATA_INPUT_DOCUMENT = "document";

	@ComponentOutput(description = "Document object.", name = "document")
	public final static String DATA_OUTPUT_DOCUMENT = "document";

	// ================
	// Constructor(s)
	// ================
	public Stem() {
	}

	// ================
	// Public Methods
	// ================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public void initializeCallBack(ComponentContextProperties ccp)
    throws Exception {
		m_docsProcessed = 0;
		_stemmer = null;
	}

	public void disposeCallBack(ComponentContextProperties ccp)
    throws Exception {
		componentConsoleHandler.whenLogLevelOutput("info","\nEND EXEC -- Stem -- Docs Processed: "
				+ m_docsProcessed + "\n");
		m_docsProcessed = 0;
		_stemmer = null;
	}

	public void executeCallBack(ComponentContext ctx)
    throws Exception {
		// props ==============================
		boolean verbose = this.getVerbose(ctx);
		//=====================================

		try {
			if (_stemmer == null) {
				_stemmer = new PorterStemmer();
			}
			Document doc = (Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOCUMENT);

			AnnotationSet annots = doc.getAnnotations();

			for (Iterator<Annotation> iter = annots.iterator(); iter.hasNext();) {
				Annotation tok = iter.next();
				if (tok.getType().equals(AnnotationConstants.TOKEN_ANNOT_TYPE)) {
					String img = tok.getContent(doc);
					String stem = _stemmer.normalizeTerm(img);
					tok.getFeatures().put(
							AnnotationConstants.TOKEN_ANNOT_FEAT_NORM_IMAGE,
							stem);
				}
			}

			// set the image feature for ngrams
			for (Iterator<Annotation> iter = annots.iterator(); iter.hasNext();) {
				Annotation tok = iter.next();
				if (tok.getType().equals(AnnotationConstants.NGRAM_ANNOT_TYPE)) {
					ArrayList<?> list = FeatureValueEncoderDecoder
							.decodeToListofAnnotations(tok
									.getFeatures()
									.get(
											AnnotationConstants.NGRAM_ANNOT_FEAT_TOKEN_LIST));
					StringBuffer s = new StringBuffer("");
					for (int i = 0, n = list.size(); i < n; i++) {
						Annotation tok2 = (Annotation) list.get(i);
						String t = (String) tok2
								.getFeatures()
								.get(
										AnnotationConstants.TOKEN_ANNOT_FEAT_NORM_IMAGE);
						if (t == null) {
							s.append(tok2.getContent(doc) + " ");
						} else {
							s.append(" ");
						}
					}
					tok.getFeatures().put(
							AnnotationConstants.NGRAM_ANNOT_FEAT_NORM_IMAGE,
							s.toString().trim());
				}
			}

			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOCUMENT, doc);
			m_docsProcessed++;
			if ((m_docsProcessed % 20 == 0)) {
				componentConsoleHandler.whenLogLevelOutput("info", m_docsProcessed+" are processed.");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: Stem.execute()");
			throw new ComponentExecutionException(ex);
		}
	}
}
