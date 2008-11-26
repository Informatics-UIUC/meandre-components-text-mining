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

package org.seasr.components.text.util.vocabulary;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * This class contains the basic repository vocabulary for SEASR 
 * text document objects.
 * 
 * @author D. Searsmith
 * 
 */
public class DocumentVocabulary {

	/** The RDF model that holds the vocabulary terms */
	private static Model m_model = ModelFactory.createDefaultModel();

	/** The namespace of the vocabulary as a string */
	public static final String s_NS = "http://www.t2k.org/ontology/document/";

	/** The namespace of the vocabulary as a string */
	public static String getNamespaceURI() {
		return s_NS;
	}

	/** The namespace of the vocabulary as a resource */
	public static final Resource NAMESPACE = m_model.createResource(s_NS);

	/** The document */
	public static final Resource executable_component = m_model
			.createResource(s_NS + "t2k_document");

	/** The feature set */
	public static final Property feature_set = m_model.createProperty(s_NS
			+ "feature_set");

	/** The feature */
	public static final Resource feature = m_model.createResource(s_NS
			+ "feature");

	/** The key of an item */
	public static final Property key = m_model.createProperty(s_NS + "key");

	/** The item value */
	public static final Property value = m_model.createProperty(s_NS + "value");

	/** The document feature set */
	public static final Property annotation_sets = m_model.createProperty(s_NS
			+ "annotation_sets");

	/** The annotation set */
	public static final Resource annotation_set = m_model.createResource(s_NS
			+ "annotation_set");

	/** The annotation set name */
	public static final Property annotation_set_name = m_model
			.createProperty(s_NS + "annotation_set_name");

	/** The annotation */
	public static final Resource annotation = m_model.createResource(s_NS
			+ "annotation");

	/** The annotation ID */
	public static final Property annotation_id = m_model.createProperty(s_NS
			+ "annotation_id");

	/** The annotation type */
	public static final Property annotation_type = m_model.createProperty(s_NS
			+ "annotation_type");

	/** The annotation start offset */
	public static final Property annotation_start = m_model.createProperty(s_NS
			+ "annotation_start");

	/** The annotation end offset */
	public static final Property annotation_end = m_model.createProperty(s_NS
			+ "annotation_end");
}