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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.meandre.components.abstracts.AbstractExecutableComponent;
import org.meandre.core.*;
import org.meandre.annotations.*;

/**
 * @author D. Searsmith
 *
 */
@Component(creator = "Duane Searsmith",

description = "<p>Overview: "
		+ "This component takes a document object and stop word list and removes the "
		+ "stop words from the document.  By default, each term in the document is converted "
		+ "to lower case before the comparison to stop words is made."
		+ "</p>"

		+ "<p>Data Type Restrictions: <br>"
		+ "The input document must have been tokenized."
		+ "</p>"

		+ "<p>Data Handling: <br>"
		+ "This module will modify (as described above) the document object's that is input."
		+ "</p>"

		+ "<p>Scalability: <br>"
		+ "This module makes one pass over the token list resulting in linear time complexity "
		+ "per the number of tokens.  Memory usage is proportional to the number tokens."
		+ "</p>"

		+ "<p>Trigger Criteria: <br>" + "Any." + "</p>",

name = "FilterStopWords",
tags = "nlp text document filter stops stopwords",
firingPolicy = Component.FiringPolicy.any,
baseURL="meandre://seasr.org/components/")
public class FilterStopWords extends AbstractExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private List<Document> _docs = null;

	private int m_docsProcessed = 0;

	private Set<?> m_stops = null;

	// IO

	@ComponentInput(description = "Stop word set.", name = "stops_set")
	public final static String DATA_INPUT_STOP_WORD_SET = "stops_set";

	@ComponentInput(description = "Document object.", name = "document")
	public final static String DATA_INPUT_DOCUMENT = "document";

	@ComponentOutput(description = "Document object.", name = "document")
	public final static String DATA_OUTPUT_DOCUMENT = "document";

	// ================
	// Public Methods
	// ================

	public void initializeCallBack(ComponentContextProperties ccp)
    throws Exception {
		_docs = new ArrayList<Document>();
		m_docsProcessed = 0;
	}

	public void disposeCallBack(ComponentContextProperties ccp)
    throws Exception {
		console.info("END EXEC -- FilterStopWords -- Docs Processed: "
						+ m_docsProcessed);
		m_docsProcessed = 0;
		if (m_stops != null) {
			m_stops.clear();
			m_stops = null;
		}
		if (_docs != null) {
			_docs.clear();
			_docs = null;
		}
	}

	public void executeCallBack(ComponentContext cc)
    throws Exception  {
		try {
			if (cc.isInputAvailable(DATA_INPUT_STOP_WORD_SET)) {
				m_stops = (Set<?>) cc
						.getDataComponentFromInput(DATA_INPUT_STOP_WORD_SET);
			}

			if (cc.isInputAvailable(DATA_INPUT_DOCUMENT)) {
				_docs.add((Document) cc
						.getDataComponentFromInput(DATA_INPUT_DOCUMENT));
			}

			if ((m_stops != null) && (!_docs.isEmpty())) {

				for (int i = 0, n = _docs.size(); i < n; i++) {

					Document doc = _docs.get(i);
					AnnotationSet annots = doc
					.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);
					int cnt = 0;

					ArrayList<Annotation> removes = new ArrayList<Annotation>();

					for (Iterator<Annotation> iter = annots.iterator(); iter
							.hasNext();) {
						Annotation tok = iter.next();
						if (tok.getType().equals(
								AnnotationConstants.TOKEN_ANNOT_TYPE)) {

							String img = tok.getContent(doc);
							if (m_stops.contains(img.toLowerCase())) {
								removes.add(tok);
								cnt++;
							}
						}
					}
					for (int i2 = 0, n2 = removes.size(); i2 < n2; i2++) {
						annots.remove(removes.get(i2));
					}

					console.fine("Number of stop words removed for "
							+ doc.getTitle() + ": " + cnt);

					cc.pushDataComponentToOutput(DATA_OUTPUT_DOCUMENT, doc);
					m_docsProcessed++;

				}
				_docs.clear();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			cc.getLogger().severe(ex.getMessage());
			cc.getLogger().severe("ERROR: FilterStopWords.execute()");
			throw new ComponentExecutionException(ex);
		}
	}
}
