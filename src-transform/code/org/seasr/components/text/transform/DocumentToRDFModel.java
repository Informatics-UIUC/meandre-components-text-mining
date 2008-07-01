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

//==============
// Java Imports
//==============

import java.util.logging.*;

//===============
// Other Imports
//===============

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.util.vocabulary.DocumentVocabulary;

/**
 * @author D. Searsmith
 * 
 * TODO: Testing, Unit Testing
 */
public class DocumentToRDFModel {

	// ==============
	// Data Members
	// ==============

	private static Logger _logger = Logger.getLogger("DocumentToRDFModel");

	private static long s_lastID = System.currentTimeMillis();

	private static Object s_lock = new Object();

	// ================
	// Static Methods
	// ================

	/**
	 * Takes the document object and builds an RDF model which represents the
	 * metadata captured in the document.
	 * 
	 * @param doc Document object to transform.
	 * @param verbose 
	 * @return Jena Model Object
	 */
	static public Model getModelFromDocument(Document doc, boolean verbose) {
		return getModelFromDocument(doc, verbose, true);
	}

	/**
	 * Takes the document object and builds an RDF model which represents the
	 * metadata captured in the document.
	 * 
	 * @param doc Document object to transform.
	 * @param verbose 
	 * @param create_resource_id Build a new resource ID or use the document ID.
	 * @return Jena Model Object
	 */
	static public Model getModelFromDocument(Document doc, boolean verbose,
			boolean create_resource_id) {
		Model model = ModelFactory.createDefaultModel();

		// Setting the name spaces
		model.setNsPrefix("", DocumentVocabulary.getNamespaceURI());
		model.setNsPrefix("xsd", XSD.getURI());
		model.setNsPrefix("rdf", RDF.getURI());
		model.setNsPrefix("rdfs", RDFS.getURI());
		model.setNsPrefix("dc", DC.getURI());

		Resource root = null;
		String resID = null;
		if (create_resource_id) {
			resID = DocumentVocabulary.getNamespaceURI() + getNextID() + "_"
					+ getLocalAddr();
			root = model.createResource(resID);
		} else {
			resID = DocumentVocabulary.getNamespaceURI() + doc.getDocID();
			root = model.createResource(resID);
		}

		// Plain properties
		if (doc.getTitle() != null) {
			root
					.addProperty(DC.title, model.createTypedLiteral(doc
							.getTitle()));
		}
		if (doc.getDate() > 0) {
			Date d = new Date(doc.getDate());
			root.addProperty(DC.date, model.createTypedLiteral(
					new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(d),
					XSDDatatype.XSDdateTime));
		}
		if (doc.getDocID() != null) {
			root.addProperty(DC.identifier, model.createTypedLiteral(doc
					.getDocID()));
		}
		// Adding document features
		Map<String, Object> features = doc.getFeatures();
		for (String sKey : features.keySet()) {
			String sValue = features.get(sKey).toString();
			root.addProperty(DocumentVocabulary.feature_set, model
					.createResource(resID + "/feature/" + sKey).addProperty(
							RDF.type, DocumentVocabulary.feature).addProperty(
							DocumentVocabulary.key,
							model.createTypedLiteral(sKey)).addProperty(
							DocumentVocabulary.value,
							model.createTypedLiteral(sValue)));
		}

		// create the default annotations
		AnnotationSet def = doc.getAnnotations();
		if (!def.isEmpty()) {
			Resource defset = model.createResource(resID
					+ "/annotation_set/default");
			root.addProperty(DocumentVocabulary.annotation_sets, defset
					.addProperty(DocumentVocabulary.annotation_set_name,
							model.createTypedLiteral("default")).addProperty(
							RDF.type, DocumentVocabulary.annotation_set));

			for (Annotation annot : def) {
				Resource annR = model.createResource(resID + "/annotation/"
						+ annot.getId());
				annR.addProperty(RDF.type, DocumentVocabulary.annotation)
						.addProperty(DocumentVocabulary.annotation_id,
								model.createTypedLiteral(annot.getId()))
						.addProperty(
								DocumentVocabulary.annotation_start,
								model.createTypedLiteral(annot
										.getStartNodeOffset())).addProperty(
								DocumentVocabulary.annotation_end,
								model.createTypedLiteral(annot
										.getEndNodeOffset())).addProperty(
								DocumentVocabulary.annotation_type,
								model.createTypedLiteral(annot.getType()));

				for (String sKey : annot.getFeatures().keySet()) {
					String sValue = features.get(sKey).toString();
					annR.addProperty(DocumentVocabulary.feature_set, model
							.createResource(resID + "/feature/" + sKey)
							.addProperty(RDF.type, DocumentVocabulary.feature)
							.addProperty(DocumentVocabulary.key,
									model.createTypedLiteral(sKey))
							.addProperty(DocumentVocabulary.value,
									model.createTypedLiteral(sValue)));
				}
			}

		}

		Map<String, AnnotationSet> asets = doc.getNamedAnnotationSets();
		if (asets != null) {
			for (String asName : asets.keySet()) {
				AnnotationSet aset = asets.get(asName);

				if (!aset.isEmpty()) {
					Resource defset = model.createResource(resID
							+ "/annotation_set/" + asName);
					root.addProperty(DocumentVocabulary.annotation_sets, defset
							.addProperty(
									DocumentVocabulary.annotation_set_name,
									model.createTypedLiteral("default"))
							.addProperty(RDF.type,
									DocumentVocabulary.annotation_set));

					for (Annotation annot : aset) {
						Resource annR = model.createResource(resID
								+ "/annotation/" + annot.getId());
						annR.addProperty(RDF.type,
								DocumentVocabulary.annotation).addProperty(
								DocumentVocabulary.annotation_id,
								model.createTypedLiteral(annot.getId()))
								.addProperty(
										DocumentVocabulary.annotation_start,
										model.createTypedLiteral(annot
												.getStartNodeOffset()))
								.addProperty(
										DocumentVocabulary.annotation_end,
										model.createTypedLiteral(annot
												.getEndNodeOffset()))
								.addProperty(
										DocumentVocabulary.annotation_type,
										model.createTypedLiteral(annot
												.getType()));

						for (String sKey : annot.getFeatures().keySet()) {
							String sValue = features.get(sKey).toString();
							annR
									.addProperty(
											DocumentVocabulary.feature_set,
											model
													.createResource(
															resID + "/feature/"
																	+ sKey)
													.addProperty(
															RDF.type,
															DocumentVocabulary.feature)
													.addProperty(
															DocumentVocabulary.key,
															model
																	.createTypedLiteral(sKey))
													.addProperty(
															DocumentVocabulary.value,
															model
																	.createTypedLiteral(sValue)));
						}
					}
				}
			}
		}

		if (verbose) {
			_logger.info("RDF model dump of: " + doc.getDocID());
			model.write(System.out);
		}

		return model;
	}

	static private String getLocalAddr() {
		String s = null;
		try {
			s = java.net.InetAddress.getLocalHost().getHostAddress();
			if (s == null) {
				s = "" + s_lock.hashCode();
			}
		} catch (UnknownHostException uhe) {
			s = "" + s_lock.hashCode();
		}
		return s;
	}

	static private long getNextID() {
		long time = -1;
		synchronized (s_lock) {
			time = System.currentTimeMillis();
			if (time <= s_lastID) {
				time += (s_lastID - time + 1);
				s_lastID = time;
			}
		}
		return time;
	}
	
	// ==============
	// Constructor(s)
	// ==============

	public DocumentToRDFModel() {
	}

}
