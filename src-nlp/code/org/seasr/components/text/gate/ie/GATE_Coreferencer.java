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

import java.io.File;
import java.util.logging.Logger;

// ==============
// Other Imports
// ==============

import gate.*;
import gate.creole.coref.*;

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
 * <p>
 * <b>Overview</b>: <br>
 * Performs pronominal coreference on a GATE document. Stores the found
 * co-references as new features in existing annotations.
 * </p>
 * <p>
 * <b>Detailed Description</b>: <br>
 * Given a document object in the GATE framework, this component will perform
 * pronominal coreference. For example, 'I' can refer to 'Jack' and 'it' can
 * refer to 'car'. The found co-references will be stored as new features in the
 * existing annotations.
 * </p>
 * <p>
 * The particular annotation set to use can be named by the user. If the
 * computed tokens are to be used by other GATE components, either use a name
 * the other components will recognize, or leave it blank so the always-present
 * default annotation set is used.
 * </p>
 * <p>
 * <b>Dependency</b>: <br>
 * The main coreference component can operate sucessfully only if the following
 * components are executed: Gazetteer, Tokenizer, SentenceSplitter, Transducer,
 * OrthoMatcher
 * </p>
 */

@Component(creator = "Duane Searsmith",

description = "<p><b>Overview</b>: <br>"
		+ "Performs pronominal coreference on a GATE document. "
		+ "Stores the found co-references as new features in "
		+ "existing annotations.</p>"

		+ "<p><b>Detailed Description</b>: <br>"
		+ "Given a document object in the GATE framework, "
		+ "this component will perform pronominal coreference. "
		+ "For example, 'I' can refer to 'Jack' and "
		+ "'it' can refer to 'car'."
		+ "The found co-references will be stored as new "
		+ "features in the existing annotations.</p>"

		+ "<p>The particular annotation set to use can be "
		+ "named by the user.  "
		+ "If the computed tokens are to be used by other "
		+ "GATE components, either use a name the other "
		+ "components will recognize, or leave it blank "
		+ "so the always-present default annotation set "
		+ "is used.</p>"

		+ "<p><b>Dependency</b>: <br>"
		+ "The main coreference component can operate sucessfully "
		+ "only if the following components are executed: "
		+ "Gazetteer, Tokenizer, SentenceSplitter, Transducer, OrthoMatcher</p>",

name = "GATE_Coreferencer", tags = "text gate coreference document")
public class GATE_Coreferencer implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private static Logger _logger = Logger.getLogger("GATE_Coreferencer");

	private int m_docsProcessed = 0;
	private long m_start = 0;

	private Coreferencer m_coref = null;

	private final String _resName = "GATE-Home-And-ANNIE-plugin_001";

	// props

	@ComponentProperty(description = "Verbose output? An int value (0 = none, 1 = fine, 2 = finer).", name = "verbose", defaultValue = "0")
	public final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Resolve Pleonastic 'It'? A boolean value (true or false).", name = "resolve_it", defaultValue = "true")
	public final static String DATA_PROPERTY_RESOLVE_IT = "resolve_it";

	@ComponentProperty(description = "Name of the Annotation Set to find the tokens "
			+ "in. Leave blank for default.", name = "token_annotation_set_name", defaultValue = "")
	public final static String DATA_PROPERTY_TOKEN_ANNOTATION_SET_NAME = "token_annotation_set_name";

	// io

	@ComponentInput(description = "Input document.", name = "document_in")
	public final static String DATA_INPUT_DOC_IN = "document_in";

	@ComponentOutput(description = "Output document.", name = "document_out")
	public final static String DATA_OUTPUT_DOC_OUT = "document_out";

	// ================
	// Constructor(s)
	// ================
	public GATE_Coreferencer() {
	}

	// ================
	// Public Methods
	// ================

	public int getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Integer.parseInt(s);
	}

	public boolean getResolveIt(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_RESOLVE_IT);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public String getTokenAnnotationSetName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TOKEN_ANNOTATION_SET_NAME);
		return s;
	}

	public void initialize(ComponentContextProperties ccp)
			throws ComponentExecutionException {
		_logger.fine("initialize() called");

		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();

		try {
			String fname = ((ComponentContext) ccp)
					.getPublicResourcesDirectory();
			if ((!(fname.endsWith("/"))) && (!(fname.endsWith("\\")))) {
				fname += "/";
			}
			GATEInitialiser.init(fname, _resName, fname + _resName,
					(ComponentContext) ccp);

			FeatureMap params = Factory.newFeatureMap();

			params.put(Coreferencer.COREF_ANN_SET_PARAMETER_NAME,
					getTokenAnnotationSetName(ccp));

			m_coref = (Coreferencer) Factory.createResource(
					"gate.creole.coref.Coreferencer", params);
			m_coref.setResolveIt(Boolean.valueOf(getResolveIt(ccp)));
		} catch (Exception e) {
			_logger.info("GATE_Coreferencer.initialise() -- " + e);
			e.printStackTrace();
			throw new ComponentExecutionException(e);
		}
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();

		if (getVerbose(ccp) > 0) {
			_logger.info("\nEND EXEC -- GATE_Coreferencer-- Docs Ouput: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}

		m_docsProcessed = 0;
		m_coref = null;
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		try {
			org.seasr.components.text.datatype.corpora.Document sdoc = (org.seasr.components.text.datatype.corpora.Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);
			if (!GATEInitialiser.checkIfGATEDocumentExists(sdoc)) {
				GATEInitialiser.addNewGATEDocToSEASRDoc(sdoc);
			}
			gate.Document doc = (gate.Document) sdoc
					.getAuxMap()
					.get(
							org.seasr.components.text.datatype.corpora.DocumentConstants.GATE_DOCUMENT);

			m_coref.setDocument(doc);
			m_coref.execute();

			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, sdoc);
			m_docsProcessed++;

			if (getVerbose(ctx) > 0) {
				if (Math.IEEEremainder(m_docsProcessed, 10) == 0) {
					System.out.println("GATE_Coreferencer -- Docs Processed: "
							+ m_docsProcessed);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: GATE_Coreferencer.execute()");
			throw new ComponentExecutionException(ex);
		}
	}
}
