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

package org.seasr.components.text.transform.gate;

// ==============
// Java Imports
// ==============

import java.util.*;
import java.util.logging.Logger;

// ===============
// Other Imports
// ===============

import org.seasr.components.text.util.Factory;
import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.FeatureMap;
import org.seasr.components.text.datatype.pos.PoSTag;

import org.meandre.core.*;

import org.meandre.annotations.*;

/**
 * <p>
 * <b>Overview</b>: <br>
 * This module converts a document object in the GATE framework to an equivalent
 * document object in the SEASR framework.
 * </p>
 * <p>
 * <b>Detailed Description</b>: <br>
 * Given a document object in the GATE framework, this module will convert it to
 * the equivalent document in the SEASR framework. A copy of the content, which
 * is a String, is made. All associated Annotations and Annotation Sets are
 * copied as well. The IDs of the Annotations, Nodes and Annotation Sets are
 * <i>not</i> preserved. Everything else such as the FeatureMap mappings and
 * values are preserved.
 * </p>
 * <p>NOTE: If GATE Document/Annotation feature maps contain non 
 * String keys or values their 'toString()' values 
 * are used in the SEASR feature map.<p>
 * 
 * @author Xiaolei Li
 * @author D. Searsmith
 */
@Component(creator = "Duane Searsmith", description = "<p><b>Overview</b>: <br>"
		+ "This module converts a document object in the GATE "
		+ "framework to an equivalent document object in the "
		+ "SEASR framework.</p>"
		+ "<p><b>Detailed Description</b>: <br>"
		+ "Given a document object in the GATE framework, "
		+ "this module will convert it to the equivalent "
		+ "document in the SEASR framework.  A copy of the "
		+ "content, which is a String, is made.  All "
		+ "associated Annotations and Annotation Sets are "
		+ "copied as well.  The IDs of the Annotations, Nodes "
		+ "and Annotation Sets are <i>not</i> preserved."
		+ "Everything else such as the FeatureMap mappings "
		+ "and values are preserved.</p>" 
		+ "<p>NOTE: If GATE Document/Annotation feature map contain non "
		+ "String keys or values their 'toString()' values "
		+ "are used in the SEASR feature map.<p>", 
		name = "GATEDocumentToDocument", 
		tags = "text gate transform document")
public class GATEDocumentToDocument implements ExecutableComponent {
	// ==============
	// Data Members
	// ==============
	private int m_docsProcessed = 0;
	private long m_start = 0;
	private static Logger _logger = Logger.getLogger("GATEDocumentToDocument");

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Convert over GATE tokens?  If true (default) then "
			+ "convert the gate Token annotations "
			+ "into T2K token annotations.  Otherwise, simply copy the GATE "
			+ "annotations exaclty as they appear in the GATE document. A boolean value (true or false).", 
			name = "convert_tokens", 
			defaultValue = "true")
	final static String DATA_PROPERTY_CONVERT_OVER_TOKENS = "convert_tokens";

	// io

	@ComponentInput(description = "Input GATE document.", name = "gate_document_in")
	public final static String DATA_INPUT_GATE_DOC_IN = "gate_document_in";

	@ComponentOutput(description = "Output SEASR document.", name = "seasr_document_out")
	public final static String DATA_OUTPUT_SEASR_DOC_OUT = "seasr_document_out";

	// ================
	// Constructor(s)
	// ================
	public GATEDocumentToDocument() {
	}

	// ================
	// Public Methods
	// ================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getConvertGATETokens(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_CONVERT_OVER_TOKENS);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public void initialize(ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();
		if (this.getVerbose(ccp)) {
			_logger.info("\nEND EXEC -- GATEDocumentToDocument"
					+ "-- Docs Ouput: " + m_docsProcessed + " in "
					+ (end - m_start) / 1000 + " seconds\n");
		}
		m_docsProcessed = 0;
	}

	
	@SuppressWarnings("unchecked")
	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		try {
			gate.Document gate_doc = (gate.Document) ctx
					.getDataComponentFromInput(DATA_INPUT_GATE_DOC_IN);

			// create a T2K document
			Document seasr_doc = Factory.newDocument();
			// copy over the content, which is a string
			gate.DocumentContent content = (gate.DocumentContent) gate_doc
					.getContent();
			seasr_doc.setContent(content.toString());
			// variable declarations
			AnnotationSet seasr_annots;
			gate.AnnotationSet gate_annots;
			gate.Annotation gate_a;
			Iterator<gate.Annotation> gate_annots_iter;
			Map gate_named_annots;
			Object[] gate_named_annnots_keys = null;
			int num_sets_to_copy = -1;
			// copy feature map
			gate.FeatureMap map = gate_doc.getFeatures();
			FeatureMap fm = Factory.newFeatureMap();
			for (Iterator it = map.keySet().iterator(); it.hasNext();) {
				Object key = it.next();
				fm.put(key.toString(), map.get(key).toString());
			}
			seasr_doc.setFeatures(fm);
			// copy over the annotation sets. each document has 2 types
			// of annotation sets.
			// (1) the default one, which has no name and always exists.
			// one can get to it by calling getAnnotations().
			// (2) the named one(s), which have names and might exist.
			// one can get to them by calling getAnnotations(String name);
			// the following loop copies over the default and the named
			// annotation sets. the for-loop starts at -1 because -1 is
			// reserved for the default annotation set. 0 and after are
			// for the named sets, if they exist.
			gate_named_annots = gate_doc.getNamedAnnotationSets();
			if (gate_named_annots == null)
				num_sets_to_copy = 0;
			else {
				// the set names
				gate_named_annnots_keys = gate_named_annots.keySet().toArray();
				num_sets_to_copy = gate_named_annnots_keys.length;
			}

			// for the default only
			// get the default set of annotations from the GATE document
			gate_annots = gate_doc.getAnnotations();
			// get the default set of annotations from the t2k document
			seasr_annots = seasr_doc.getAnnotations();
			// get an iterator over the GATE annotation set
			gate_annots_iter = gate_annots.iterator();
			// iterate over annotations in the set

			ArrayList splits = new ArrayList();

			while (gate_annots_iter.hasNext()) {
				// get the GATE annotation
				gate_a = (gate.Annotation) gate_annots_iter.next();
				// create a T2K FeatureMap
				FeatureMap seasr_fmap = Factory.newFeatureMap();
				// get the original features in the GATE document
				gate.FeatureMap gate_fmap = gate_a.getFeatures();
				Object[] keys = gate_fmap.keySet().toArray();
				// copy over the features (key-value mappings)
				for (int i = 0; i < keys.length; i++) {
					seasr_fmap.put(keys[i].toString(), gate_fmap.get(keys[i]).toString());
				}
				// create a brand new annotation in the GATE document
				// t2k_annots.add(gate_a.getStartNode().getOffset(),
				// gate_a.getEndNode().getOffset(),
				seasr_annots.add(gate_a.getStartNode().getOffset().longValue(),
						gate_a.getEndNode().getOffset().longValue(), gate_a
								.getType(), seasr_fmap);

				if (this.getConvertGATETokens(ctx)) {
					if (gate_a.getType().equals("Split")) {
						if (((String) gate_a.getFeatures().get("kind"))
								.equals("internal")) {
							splits.add(gate_a);
						}
					}
				}

				if (gate_a.getType().equals("Token")) {
					String img = (String) gate_fmap.get("string");
					if (img == null) {
						System.out
								.println("ERROR: Gate Token didn't have a 'string' feature: "
										+ gate_a);
						continue;
					}
					String tagname = (String) gate_fmap.get("category");
					PoSTag tag = null;
					if (tagname != null) {
						if (tagname.equals("''")) {
							tagname = "\"";
						}
						if (tagname.equals("``")) {
							tagname = "`";
						}
						tag = PoSTag.getPoSTag(tagname);
						if (tag == null) {
							System.out
									.println("ERROR: Gate category not found as PoSTag type: "
											+ tagname
											+ " / "
											+ img
											+ " / "
											+ gate_doc.getFeatures().get(
													"gate.SourceURL")
											+ " / "
											+ gate_a.getStartNode().getOffset());
							continue;
						}
					}
					seasr_fmap = Factory.newFeatureMap();
					if (tag != null) {
						seasr_fmap.put(AnnotationConstants.TOKEN_ANNOT_FEAT_POS,
								tag.toString());
					}

					// t2k_annots.add(gate_a.getStartNode().getOffset(),
					// gate_a.getEndNode().getOffset(),
					seasr_annots.add(gate_a.getStartNode().getOffset()
							.longValue(), gate_a.getEndNode().getOffset()
							.longValue(), AnnotationConstants.TOKEN_ANNOT_TYPE,
							seasr_fmap);

				}

			}

			// process splits
			if (!splits.isEmpty()) {
				seasr_annots = seasr_doc.getAnnotations().get(
						AnnotationConstants.TOKEN_ANNOT_TYPE);
				for (int i = 0, n = splits.size(); i < n; i++) {
					gate_a = (gate.Annotation) splits.get(i);
					AnnotationSet seasrset = seasr_annots
							.getContained(
									gate_a.getStartNode().getOffset()
											.longValue(), gate_a.getEndNode()
											.getOffset().longValue());
					if ((!(seasrset == null)) && (!seasrset.isEmpty())) {
						Iterator iter = seasrset.iterator();
						Annotation t2kannot = (Annotation) iter
								.next();
						t2kannot
								.getFeatures()
								.put(
										AnnotationConstants.TOKEN_ANNOT_FEAT_END_OF_LINE_BOOL,
										Boolean.TRUE.toString());
					} else {
						_logger.info("GATEDocumentToDocument -- token not found corresponding to gate split: "
										+ seasrset + " / " + gate_a);
					}
				}
			}

			// for all the named annotation sets
			for (int u = 0; u < num_sets_to_copy; u++) {
				// get the named set of annotations from the GATE
				// document.
				gate_annots = gate_doc
						.getAnnotations((String) gate_named_annnots_keys[u]);
				// get the named set of annotations from the t2k
				// document. note that the document should create
				// the annotation set in the document if it doesn't
				// exist.
				seasr_annots = seasr_doc
						.getAnnotations((String) gate_named_annnots_keys[u]);
				// get an iterator over the GATE annotation set
				gate_annots_iter = gate_annots.iterator();
				// iterate over annotations in the set
				while (gate_annots_iter.hasNext()) {
					// get the GATE annotation
					gate_a = (gate.Annotation) gate_annots_iter.next();
					// create a T2K FeatureMap
					FeatureMap seasr_fmap = Factory.newFeatureMap();
					// get the original features in the GATE document
					gate.FeatureMap gate_fmap = gate_a.getFeatures();
					Object[] keys = gate_fmap.keySet().toArray();
					// copy over the features (key-value mappings)
					for (int i = 0; i < keys.length; i++) {
						seasr_fmap.put(keys[i].toString(), gate_fmap.get(keys[i]).toString());
					}
					// create a brand new annotation in the GATE document
					// t2k_annots.add(gate_a.getStartNode().getOffset(),
					// gate_a.getEndNode().getOffset(),
					seasr_annots.add(gate_a.getStartNode().getOffset()
							.longValue(), gate_a.getEndNode().getOffset()
							.longValue(), gate_a.getType(), seasr_fmap);
				}
			}

			// push out the converted T2K document
			ctx.pushDataComponentToOutput(DATA_OUTPUT_SEASR_DOC_OUT, seasr_doc);
			m_docsProcessed++;
			// verbose reporting
			if (this.getVerbose(ctx)) {
				if (Math.IEEEremainder(m_docsProcessed, 10) == 0) {
					_logger.info("GATEDocumentToDocument -- "
							+ "Docs Processed: " + m_docsProcessed);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: GATEDocumentToDocument.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

}
