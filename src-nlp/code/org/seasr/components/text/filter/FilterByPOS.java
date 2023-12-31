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

package org.seasr.components.text.filter;

// ==============
// Java Imports
// ==============

import java.util.*;
import java.util.logging.Logger;

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

/*
 * @author D. Searsmith
 *
 * TODO: Testing, Unit Testing
 *
 */
@Component(creator = "Duane Searsmith",

description = "<p>Overview: <br>"
		+ "This component reads a document object as input and filters the tokens "
		+ "for that document based on part of speech tag information."
		+ "</p>"

		+ "<p>Detailed Description: <br>"
		+ "A document object is taken as input.  The token list is retrieved from the "
		+ "document and only those tokens with part of speech tags that match at least "
		+ "one value in the user defined list are retained. The filtered list of token "
		+ "annotations is placed into the document (replacing the old list) and then "
		+ "the document is output."
		+ "</p>"

		+ "<p>Data Type Restrictions: <br>"
		+ "The input document must have been tokenized."
		+ "</p>"

		+ "<p>Data Handling: <br>"
		+ "This module will modify (as described above) the document object that is input."
		+ "</p>"

		+ "<p>Scalability: <br>"
		+ "This module makes one pass over the token annotation list resulting in linear "
		+ "time complexity per the number of tokens.  Memory usage is proportional to "
		+ "the number tokens." + "</p>"

		+ "<p>Trigger Criteria: <br>" + "Standard." + "</p>",

name = "FilterByPOS", tags = "nlp text document filter pos",
baseURL="meandre://seasr.org/components/")
public class FilterByPOS extends AbstractExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private int m_docsProcessed = 0;

	private Set<String> _tags = null;

	// props

	@ComponentProperty(description = "Comma separated list of POS tags.", name = "tag_list", defaultValue = "NN,NNP,NNPS,NNS,NP,NPS")
	final static String DATA_PROPERTY_TAG_LIST = "tag_list";

	// IO

	@ComponentInput(description = "Document object.", name = "document")
	public final static String DATA_INPUT_DOCUMENT = "document";

	@ComponentOutput(description = "Document object.", name = "document")
	public final static String DATA_OUTPUT_DOCUMENT = "document";

	// ================
	// Public Methods
	// ================

	public String getTagList(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TAG_LIST);
		return s;
	}

	public void initializeCallBack(ComponentContextProperties ccp)
    throws Exception {
		m_docsProcessed = 0;
		if (_tags == null) {
			_tags = new HashSet<String>();
			StringTokenizer toker = new StringTokenizer(getTagList(ccp), ",");
			while (toker.hasMoreElements()) {
				String tag = toker.nextToken();
				_tags.add(tag);
			}
		}
	}

	public void disposeCallBack(ComponentContextProperties ccp)
    throws Exception {
		console.fine("END EXEC -- FilterByPOS -- Docs Processed: "
				+ m_docsProcessed);
		m_docsProcessed = 0;
	}

	public void executeCallBack(ComponentContext ctx)
    throws Exception {

		int toks_selected = 0;

		try {
			Document doc = (Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOCUMENT);

			console.fine(doc.getDocID()
					+ " has "
					+ doc.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS).size()
					+ " num tokens.");

			ArrayList<Annotation> removes = new ArrayList<Annotation>();
			AnnotationSet annots = doc
			.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);
			int origSz = annots.size();
			for (Iterator<Annotation> iter = annots.iterator(); iter.hasNext();) {
				Annotation tok = iter.next();
				if (tok.getType().equals(AnnotationConstants.TOKEN_ANNOT_TYPE)) {
					String postag = tok.getFeatures().get(
							AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
					String tokimg = tok.getContent(doc);
					if ((hasTag(postag))
							&& (Character.isLetter(tokimg.charAt(0)))) {
						toks_selected++;
					} else {
						removes.add(tok);
					}
				}
			}
			for (int i = 0, n = removes.size(); i < n; i++) {
				annots.remove(removes.get(i));
			}

			/**
			 * Filter ngrams -- at least one token in the ngram should match one
			 * of the desired parts of speech.
			 */
			removes = new ArrayList<Annotation>();
			for (Iterator<Annotation> iter = annots.iterator(); iter.hasNext();) {
				Annotation tok = (Annotation) iter.next();
				if (tok.getType().equals(AnnotationConstants.NGRAM_ANNOT_TYPE)) {
					ArrayList<Annotation> list = FeatureValueEncoderDecoder
							.decodeToListofAnnotations(tok
									.getFeatures()
									.get(AnnotationConstants.NGRAM_ANNOT_FEAT_TOKEN_LIST));
					boolean keep = false;
					for (int i = 0, n = list.size(); i < n; i++) {
						Annotation tok2 = list.get(i);
						String postag = tok2.getFeatures().get(
								AnnotationConstants.TOKEN_ANNOT_FEAT_POS);
						String tokimg = tok2.getContent(doc);
						if ((hasTag(postag))
								&& (Character.isLetter(tokimg.charAt(0)))) {
							keep = true;
							break;
						}
					}
					if (keep) {
						toks_selected++;
					} else {
						removes.add(tok);
					}
				}
			}
			for (int i = 0, n = removes.size(); i < n; i++) {
				annots.remove(removes.get(i));
			}

			console.fine(toks_selected
					+ " tokens were selected "
					+ " out of " + origSz);

			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOCUMENT, doc);
			m_docsProcessed++;

		} catch (Exception ex) {
			ex.printStackTrace();
			ctx.getLogger().severe(ex.getMessage());
			ctx.getLogger().severe("ERROR: FilterByPOS.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

	// =================
	// Private Methods
	// =================

	private boolean hasTag(String tag) {
		return _tags.contains(tag);
	}

}
