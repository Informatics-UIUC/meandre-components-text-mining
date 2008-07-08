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
import java.util.logging.Logger;

import org.meandre.annotations.Component;

// ===============
// Other Imports
// ===============
import gate.*;
import gate.creole.gazetteer.*;

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
 * Runs the GATE Default Gazetteer on a given GATE document. 
 * Annotates items such as people, cities, organzations, 
 * dates, etc.
 * </p>
 * <p><b>Detailed Description</b>: <br>
 * Given a document object in the GATE framework, 
 * this component will annotate proper items such 
 * as people, cities, organzations, money, date, etc. 
 * using the given rules in the list file. 
 * The found annotations will be 
 * stored in an annotation set associated with the 
 * document.
 * </p>
 * <p>
 * The particular annotation set to use can be 
 * named by the user. 
 * If they are to be used by other 
 * GATE modules, either use a name the other 
 * modules will recognize, or leave it blank 
 * so the always-present default annotation set 
 * is used. 
 * Furthermore, if there are other GATE modules 
 * preceding this one in the itinerary, the annotation 
 * set name must agree with theirs.
 * </p>
 * 
 * @author D. Searsmith
 * 
 * TODO: Testing, Unit Tests
 */
@Component(creator = "Duane Searsmith",

description = "<p><b>Overview</b>: <br>"
+ "Runs the GATE Default Gazetteer on a given GATE document.  "
+ "Annotates items such as people, cities, organzations, "
+ "dates, etc.</p>"

+ "<p><b>Detailed Description</b>: <br>"
+ "Given a document object in the GATE framework, "
+ "this component will annotate proper items such "
+ "as people, cities, organzations, money, date, etc.  "
+ "using the given rules in the list file. "
+ "The found annotations will be "
+ "stored in an annotation set associated with the "
+ "document.</p>"
+ "<p>The particular annotation set to use can be "
+ "named by the user.  "
+ "If they are to be used by other "
+ "GATE modules, either use a name the other "
+ "modules will recognize, or leave it blank "
+ "so the always-present default annotation set "
+ "is used.  "
+ "Furthermore, if there are other GATE modules "
+ "preceding this one in the itinerary, the annotation "
+ "set name must agree with theirs.</p>",

name = "GATE_Gazetteer", tags = "text gate gazetteer document")
public class GATE_Gazetteer implements ExecutableComponent {
	// ==============
	// Data Members
	// ==============

	private static Logger _logger = Logger.getLogger("GATE_Gazetteer");

	private int m_docsProcessed = 0;
	private long m_start = 0;

	private DefaultGazetteer _gaz = null;

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Encoding type of the document.", name = "document_encoding", defaultValue = "UTF-8")
	final static String DATA_PROPERTY_DOCUMENT_ENCODING = "document_encoding";

	@ComponentProperty(description = "URL of the gazetteer lists definition file in GATE.", name = "gazetteer_defintion_list_url", defaultValue = "gate:/creole/gazeteer/lists.def")
	final static String DATA_PROPERTY_GAZETTEER_DEFINITION_LIST_URL = "gazetteer_defintion_list_url";

	@ComponentProperty(description = "Whether the gazetteer should be case sensitive. A boolean value (true or false).", name = "case_sensitive", defaultValue = "true")
	final static String DATA_PROPERTY_CASE_SENSITIVE = "case_sensitive";

	@ComponentProperty(description = "Name of the annotation set to store the new annotations. "
			+ "Leave blank for default., ", name = "annotation_set_name", defaultValue = "")
	final static String DATA_PROPERTY_ANNOTATION_SET_NAME = "annotation_set_name";

	// io

	@ComponentInput(description = "Input GATE document.", name = "document_in")
	public final static String DATA_INPUT_DOC_IN = "gate_document_in";

	@ComponentOutput(description = "Output GATE document.", name = "document_out")
	public final static String DATA_OUTPUT_DOC_OUT = "gate_document_out";

	// ================
	// Constructor(s)
	// ================
	public GATE_Gazetteer() {
	}

	// ================
	// Public Methods
	// ================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getCaseSensitive(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_CASE_SENSITIVE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public String getDocumentEncoding(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_DOCUMENT_ENCODING);
		return s;
	}

	public String getGazetteerDefnListURL(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_GAZETTEER_DEFINITION_LIST_URL);
		return s;
	}

	public String getAnnotationSetName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_ANNOTATION_SET_NAME);
		return s;
	}

	public void initialize(ComponentContextProperties ccp)
			throws ComponentExecutionException {
		_logger.fine("initialize() called");
		GATEInitialiser.init();

		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();

		try {
			FeatureMap params = Factory.newFeatureMap();

			String newListUrl = null;
			String currGazDefnList = this.getGazetteerDefnListURL(ccp);
			if (currGazDefnList.startsWith(GATEInitialiser.GATE_PREFIX)) {
				newListUrl = GATEInitialiser.getResourceURL(currGazDefnList);
			}
			if (newListUrl != null) {
				System.out
						.println("GATE_Gazetteer: gate: URLs are deprecated.  Converting "
								+ currGazDefnList + " to " + newListUrl);
				params.put(DefaultGazetteer.DEF_GAZ_LISTS_URL_PARAMETER_NAME,
						newListUrl);
			} else {
				params.put(DefaultGazetteer.DEF_GAZ_LISTS_URL_PARAMETER_NAME,
						currGazDefnList);
			}

			params.put(DefaultGazetteer.DEF_GAZ_ENCODING_PARAMETER_NAME, this
					.getDocumentEncoding(ccp));
			params.put(DefaultGazetteer.DEF_GAZ_CASE_SENSITIVE_PARAMETER_NAME,
					this.getCaseSensitive(ccp));

			_gaz = (DefaultGazetteer) Factory.createResource(
					"gate.creole.gazetteer.DefaultGazetteer", params);
		} catch (Exception e) {
			_logger.info("GATE_Gazetteer.initialise() -- " + e);
			e.printStackTrace();
			throw new ComponentExecutionException(e);
		}
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();

		if (this.getVerbose(ccp)) {
			System.out.println("\nEND EXEC -- GATE_Gazetteer -- Docs Ouput: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}

		m_docsProcessed = 0;
		_gaz = null;
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		try {
			gate.Document doc = (gate.Document) ctx
					.getDataComponentFromInput(DATA_INPUT_DOC_IN);

			_gaz.setDocument(doc);
			if (getAnnotationSetName(ctx).trim().length() > 0){
				_gaz.setAnnotationSetName(this.getAnnotationSetName(ctx));
			}
			_gaz.execute();

			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, doc);
			m_docsProcessed++;

			if (this.getVerbose(ctx)) {
				if (Math.IEEEremainder(m_docsProcessed, 250) == 0) {
					System.out.println("GATE_Gazetteer -- Docs Processed: "
							+ m_docsProcessed);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: GATE_Gazetteer.execute()");
			throw new ComponentExecutionException(ex);
		}
	}
}
