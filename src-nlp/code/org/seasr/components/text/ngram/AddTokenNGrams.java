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

package org.seasr.components.text.ngram;

// ==============
// Java Imports
// ==============

import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;

// ===============
// Other Imports
// ===============

// import org.meandre.tools.components.*;
// import org.meandre.tools.components.FlowBuilderAPI.WorkingFlow;

import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.FeatureMap;
import org.seasr.components.text.datatype.pos.PoSTag;
import org.seasr.components.text.util.Factory;
import org.meandre.core.*;

/**
 * <p>
 * Overview: <br>
 * This component reads a document object as input and in accordance with the user's
 * choice of number of tokens per ngram (arity), a sliding window of size arity is used
 * to create ngram annotation objects.
 * </p>
 * Data Type Restrictions: <br>
 * The input document must have been tokenized.
 * </p>
 * <p>
 * Data Handling: <br>
 * This module will modify (as described above) the document object that is
 * input.
 * </p>
 * <p>
 * Trigger Criteria: <br>
 * All.
 * </p>
 * 
 * @author D. Searsmith
 * 
 * TODO: Testing, Unit Testing
 * 
 */
@Component(creator = "Duane Searsmith",

description = "<p>Overview: <br>"
	+ "This component reads a document object as input and in accordance with the user's"
	+ "choice of number of tokens per ngram (arity), a sliding window of size arity is used"
	+ "to create ngram annotation objects."
    + "</p>"
    + "<p>Data Type Restrictions: <br>" 
    + "The input document must have been tokenized."
    + "</p>"
    + "<p>"
    + "Data Handling: <br>"
    + "This module will modify (as described above) the document object that is input."
    + "</p>"
    + "<p>"
    + "Trigger Criteria: <br>"
    + "All."
    + "</p>",

name = "AddTokenNGrams", tags = "nlp text document ngram pos")
public class AddTokenNGrams implements ExecutableComponent {
	// ==============
	// Data Members
	// ==============

	private int m_docsProcessed = 0;
	private long m_start = 0;

	private static Logger _logger = Logger.getLogger("AddTokenNGrams");

	// props

	@ComponentProperty(description = "Verbose output?", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Number of tokens per ngram?", name = "arity", defaultValue = "false")
	final static String DATA_PROPERTY_ARITY = "arity";

	// io

	@ComponentInput(description = "Document object.", name = "document")
	public final static String DATA_INPUT_DOCUMENT = "document";

	@ComponentOutput(description = "Document object.", name = "document")
	public final static String DATA_OUTPUT_DOCUMENT = "document";

	// ================
	// Public Methods
	// ================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public int getArity(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_ARITY);
		return Integer.parseInt(s);
	}

	public void initialize(ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();

		if (getVerbose(ccp)){
		_logger.info("\nEND EXEC -- AddTokenNGrams -- Docs Processed: "
				+ m_docsProcessed + " in " + (end - m_start) / 1000
				+ " seconds\n");
		m_docsProcessed = 0;
		}
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		_logger.fine("execute() called");


		try {
			Document doc = (Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOCUMENT);

			AnnotationSet annots = doc.getAnnotations();
			Set<Annotation> tset = new TreeSet<Annotation>(new Annot_Comparator());
			for (Iterator<Annotation> iter = annots.iterator(); iter.hasNext();) {
				Annotation tok = iter.next();
				if (tok.getType().equals(AnnotationConstants.TOKEN_ANNOT_TYPE)) {
					tset.add(tok);
				}
			}

			ArrayList<Annotation> q = new ArrayList<Annotation>();
			for (Iterator<Annotation> iter = tset.iterator(); iter.hasNext();) {
				Annotation tok = iter.next();
				String img = tok.getContent(doc);
				PoSTag postag = (PoSTag) tok.getFeatures().get(
						AnnotationConstants.TOKEN_ANNOT_FEAT_POS);

				if ((PoSTag.isSymbol(postag)) || (img.trim().equals("/"))
						|| (img.trim().equals("%"))
						|| (PoSTag.PoS_CD == postag)) {
					if ((postag == PoSTag.PoS_PERIOD)
							|| (img.trim().equals("?"))
							|| (img.trim().equals("!"))) {
						q.clear();
					}
					continue;
				}

				q.add(tok);

				if (q.size() == getArity(ctx)) {
					FeatureMap fm = Factory.newFeatureMap();
					fm.put(AnnotationConstants.NGRAM_ANNOT_FEAT_TOKEN_LIST,
							new ArrayList<Annotation>(q));
					fm.put(AnnotationConstants.NGRAM_ANNOT_FEAT_ARITY,
							new Integer(getArity(ctx)));
					String nimg = "";
					for (int i = 0, n = q.size(); i < n; i++) {
						nimg += q.get(i).getContent(doc)
								+ " ";
					}
					if (this.getVerbose(ctx)) {
						_logger.info(nimg.trim());
					}
					long beg = ((Annotation) q.get(0)).getStartNodeOffset();
					long end = ((Annotation) q.get(q.size() - 1))
							.getEndNodeOffset();
					annots.add(beg, end, AnnotationConstants.NGRAM_ANNOT_TYPE,
							fm);
					q.remove(0);
				}
			}
			if (getVerbose(ctx)) {
				_logger.info("\n\n\n================\n\n\n");
			}

			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOCUMENT, doc);
			m_docsProcessed++;

		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: AddTokenNGrams.doit()");
			throw new ComponentExecutionException(ex);
		}
	}

	//=================
	// Private Methods
	//=================

	private class Annot_Comparator implements Comparator<Annotation> {

		public Annot_Comparator() {
		}

		// ======================
		// Interface: Comparator
		// ======================
		public int compare(Annotation ann1, Annotation ann2) {
			if (ann1.getStartNodeOffset() < ann2.getStartNodeOffset()) {
				return -1;
			} else if (ann1.getStartNodeOffset() > ann2.getStartNodeOffset()) {
				return 1;
			}
			return 1;
		}

		public boolean equals(Object o) {
			return this.equals(o);
		}
	}

}
