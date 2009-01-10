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

package org.seasr.components.text.gate.io;

import java.util.logging.Logger;

// ==============
// Java Imports
// ==============

import java.net.URL;

// ===============
// Other Imports
// ===============

import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.DocumentConstants;
import org.seasr.components.text.util.Factory;

/**
 * <p>
 * Overview: <br>
 * This module takes in a file name and path and uses the GATE document reader
 * to load the document into a GATE document object. The GATE Document
 * properties 'preserveOriginalContent' and 'collectRepositioningInfo' are both
 * set to true.
 * </p>
 * <p>
 * References: <br>
 * GATE: General Architecture for Text Engineering, http://gate.ac.uk/
 * </p>
 * 
 * <p>
 * Data Type Restrictions: <br>
 * The output document will be a GATE Document.
 * </p>
 * 
 * <p>
 * Scalability: <br>
 * Documents are queued in memory and therefore use heap resources accordingly.
 * </p>
 * 
 * <p>
 * Trigger Criteria: <br>
 * Standard.
 * </p>"
 * 
 * 
 * TODO: Testing, Unit Tests
 * 
 * @author D. Searsmith
 */
@Component(creator = "Duane Searsmith",

description = "<p>Overview: <br>"
		+ "This module takes in a url and uses the GATE document reader "
		+ "to load the document into a GATE document object. The GATE Document properties 'preserveOriginalContent' and "
		+ "'collectRepositioningInfo' are both set to true.</p>"
		+ "<p>References: <br>"
		+ "GATE: General Architecture for Text Engineering, http://gate.ac.uk/"
		+ "</p>"

		+ "<p>Data Type Restrictions: <br>"
		+ "The output document will be a GATE Document."
		+ "</p>"

		+ "<p>Scalability: <br>"
		+ "Documents are queued in memory and therefore use heap resources accordingly."
		+ "</p>"

		+ "<p>Trigger Criteria: <br>" + "Standard." + "</p>",

name = "GATE_UrlDocumentReader", tags = "io read file text gate document", 
dependency = { "GATE-Home-And-ANNIE-plugin_001.jar", "gate.jar", "nekohtml-0.9.5.jar", "PDFBox-0.7.2.jar"},
baseURL="meandre://seasr.org/components/")
public class GATE_UrlDocumentReader extends GATE_DocumentReader {

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	public final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "File encoding? A boolean value (true or false).", name = "encoding", defaultValue = "UTF-8")
	public final static String DATA_PROPERTY_FILE_ENCODING = "encoding";

	// io
	
	@ComponentInput(description = "URL.", name = "url_name")
	public final static String DATA_INPUT_FILE_NAME = "url_name";

	@ComponentOutput(description = "Document object.", name = "document")
	public final static String DATA_OUTPUT_DOC = "document";

	private static Logger _logger = Logger.getLogger("GATE_UrlDocumentReader");

	// ================
	// Constructor(s)
	// ================
	public GATE_UrlDocumentReader() {
		super();
	}

	// ============
	// Properties
	// ============

	public String getEncoding(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_FILE_ENCODING);
		return s;
	}

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public void initialize(ComponentContextProperties ccp) throws ComponentExecutionException {
		super.initialize(ccp);
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();
		if (getVerbose(ccp)) {
			_logger.info("\nEND EXEC -- GATE_UrlDocumentReader -- Docs Ouput: "
							+ m_docsProcessed
							+ " in "
							+ (end - m_start)
							/ 1000
							+ " seconds\n");
		}
		m_docsProcessed = 0;
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		_logger.fine("execute() called");
		try {

			String urlS = (String)ctx.getDataComponentFromInput(DATA_INPUT_FILE_NAME);

			URL url = new URL(urlS);
			
			gate.Document gdoc = readDoc(url, getEncoding(ctx));
			if (getVerbose(ctx)){
				_logger.info("Loading from URL: " + url);
			}
			
			if (gdoc == null)
				throw new ComponentExecutionException("Unable to read file into GATE document.");

			Document doc = Factory.newDocument();
			doc.setContent((String)gdoc.getFeatures().get(gate.GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME));
			doc.setTitle(urlS);
			doc.setDocID(urlS);
			
			doc.getAuxMap().put(DocumentConstants.GATE_DOCUMENT, gdoc);
			
			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC, doc);

			m_docsProcessed++;
			if (getVerbose(ctx)) {
				if (Math.IEEEremainder(m_docsProcessed, 10) == 0) {
					_logger.info("GATEDocumentReader -- Docs Processed: "
							+ m_docsProcessed);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: GATE_DocumentReader.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

}
