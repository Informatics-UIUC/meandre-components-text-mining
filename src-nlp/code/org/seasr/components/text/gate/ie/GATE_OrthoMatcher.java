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

package org.seasr.components.text.gate.ie;

// ==============
// Java Imports
// ==============
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import org.meandre.annotations.Component;

// ===============
// Other Imports
// ===============
import gate.*;
import gate.creole.orthomatcher.*;
import gate.creole.splitter.SentenceSplitter;

import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
import org.meandre.core.ExecutableComponent;
import org.seasr.components.text.gate.util.GATEInitialiser;

/**
 * <p><b>Overview</b>: <br>
 * Performs orthographic coreference on a GATE document. 
 * Stores the found co-references as new features in 
 * existing annotations.</p>
 * <p><b>Detailed Description</b>: <br>
 * Given a document object in the GATE framework, 
 * this component will perform orthographic coreference. 
 * Semantic synonyms such as \"IBM\" and \"IBM Corp.\" 
 * will be labeled as such. Also applies to people, 
 * location, and date.
 * The found co-references will be stored as new 
 * features in the existing annotations.</p>
 * <p>The particular annotation set to use can be 
 * named by the user.  
 * If the computed tokens are to be used by other 
 * GATE components, either use a name the other 
 * components will recognize, or leave it blank 
 * so the always-present default annotation set 
 * is used.</p>
 * <p>
 * The types of annotations to process on can also 
 * be named by the user.  
 * Enter the types delimited by commas.
 * </p>
 * 
 * @author D. Searsmith
 * 
 * TODO: Testing, Unit Tests
 */
@Component(creator = "Duane Searsmith",

description = "<p><b>Overview</b>: <br>"
		+ "Performs orthographic coreference on a GATE document. "
		+ "Stores the found co-references as new features in "
		+ "existing annotations.</p>"

		+ "<p><b>Detailed Description</b>: <br>"
		+ "Given a document object in the GATE framework, "
		+ "this component will perform orthographic coreference. "
		+ "Semantic synonyms such as \"IBM\" and \"IBM Corp.\" "
		+ "will be labeled as such. Also applies to people, "
		+ "location, and date."
		+ "The found co-references will be stored as new "
		+ "features in the existing annotations.</p>"

		+ "<p>The particular annotation set to use can be "
		+ "named by the user.  "
		+ "If the computed tokens are to be used by other "
		+ "GATE components, either use a name the other "
		+ "components will recognize, or leave it blank "
		+ "so the always-present default annotation set " + "is used.</p>"

		+ "<p>The types of annotations to process on can also be "
		+ "named by the user.  " + "Enter the types delimited by commas.</p>",

name = "GATE_OrthoMatcher", tags = "text gate orthomatcher document")
public class GATE_OrthoMatcher implements ExecutableComponent {
	// ==============
	// Data Members
	// ==============

	private static Logger _logger = Logger.getLogger("GATE_OrthoMatcher");
	private int m_docsProcessed = 0;
	private long m_start = 0;

	private OrthoMatcher _matcher = null;

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Whether the orthomatcher should be case sensitive. A boolean value (true or false).", name = "case_sensitive", defaultValue = "false")
	final static String DATA_PROPERTY_CASE_SENSITIVE = "case_sensitive";

	@ComponentProperty(description = "Name of the Annotation Set to find the tokens "
			+ "in. Leave blank for default.", name = "token_annotation_set_name", defaultValue = "")
	final static String DATA_PROPERTY_TOKEN_ANNOTATION_SET_NAME = "token_annotation_set_name";

	@ComponentProperty(description = "Types of entity annotations to match.  Use commas to separate types.", name = "entity_annotation_types", defaultValue = "Organization,Person,Location,Date")
	final static String DATA_PROPERTY_ENTITY_ANNOTATION_TYPES = "entity_annotation_types";

	@ComponentProperty(description = "The 'Person' type.", name = "person_type", defaultValue = "Person")
	final static String DATA_PROPERTY_PERSON_TYPE = "person_type";

	@ComponentProperty(description = "The 'Organization' type.", name = "organization_type", defaultValue = "Organization")
	final static String DATA_PROPERTY_ORGANIZATION_TYPE = "organization_type";

	@ComponentProperty(description = "Use external lists. A boolean value (true or false).", name = "external_lists", defaultValue = "true")
	final static String DATA_PROPERTY_USE_EXTENAL_LISTS = "external_lists";

	// io

	@ComponentInput(description = "Input GATE document.", name = "document_in")
	public final static String DATA_INPUT_DOC_IN = "gate_document_in";

	@ComponentOutput(description = "Output GATE document.", name = "document_out")
	public final static String DATA_OUTPUT_DOC_OUT = "gate_document_out";

	// ================
	// Constructor(s)
	// ================
	public GATE_OrthoMatcher() {
	}

	// ================
	// Public Methods
	// ================
	// ========================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getCaseSensitive(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_CASE_SENSITIVE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getUseExternalLists(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_USE_EXTENAL_LISTS);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public String getTokenSetName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TOKEN_ANNOTATION_SET_NAME);
		return s;
	}

	public String getEntityAnnotationTypes(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_ENTITY_ANNOTATION_TYPES);
		return s;
	}

	public String getPersonType(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_PERSON_TYPE);
		return s;
	}

	public String getOrganizationType(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_ORGANIZATION_TYPE);
		return s;
	}

	public void initialize(ComponentContextProperties ccp)
			throws ComponentExecutionException {
		_logger.fine("initialize() called");
		GATEInitialiser.init();

		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();

		try {
			ArrayList<String> annTypes = new ArrayList();

			StringTokenizer tok = new StringTokenizer(this
					.getEntityAnnotationTypes(ccp), ",");

			while (tok.hasMoreTokens()) {
				annTypes.add(tok.nextToken());
			}

			FeatureMap params = Factory.newFeatureMap();

			params.put(OrthoMatcher.OM_ANN_SET_PARAMETER_NAME, this
					.getTokenSetName(ccp));
			params.put(OrthoMatcher.OM_CASE_SENSITIVE_PARAMETER_NAME, this
					.getCaseSensitive(ccp));
			params.put(OrthoMatcher.OM_ANN_TYPES_PARAMETER_NAME, annTypes);
			params.put(OrthoMatcher.OM_PERSON_TYPE_PARAMETER_NAME, this
					.getPersonType(ccp));
			params.put(OrthoMatcher.OM_ORG_TYPE_PARAMETER_NAME, this
					.getOrganizationType(ccp));
			params.put(OrthoMatcher.OM_EXT_LISTS_PARAMETER_NAME, this
					.getUseExternalLists(ccp));

			_matcher = (OrthoMatcher) Factory.createResource(
					"gate.creole.orthomatcher.OrthoMatcher", params);
		} catch (Exception e) {
			_logger.info("GATE_OrthoMatcher.initialise() -- " + e);
			e.printStackTrace();
			throw new ComponentExecutionException(e);
		}
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();

		if (this.getVerbose(ccp)) {
			System.out
					.println("\nEND EXEC -- GATE_OrthoMatcher -- Docs Ouput: "
							+ m_docsProcessed + " in " + (end - m_start) / 1000
							+ " seconds\n");
		}

		m_docsProcessed = 0;
		_matcher = null;
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		try {
			gate.Document doc = (gate.Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);

			_matcher.setDocument(doc);
			_matcher.execute();

			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, doc);
			m_docsProcessed++;

			if (this.getVerbose(ctx)) {
				if (Math.IEEEremainder(m_docsProcessed, 250) == 0) {
					System.out.println("GATE_OrthoMatcher -- Docs Processed: "
							+ m_docsProcessed);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: GATE_OrthoMatcher.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

}
