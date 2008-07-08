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

// ==============
// Java Imports
// ==============
import java.util.logging.Logger;

// ===============
// Other Imports
// ===============

import gate.*;

import org.seasr.components.text.gate.util.GATEInitialiser;

import org.meandre.core.*;
import org.meandre.annotations.*;

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
		+ "This module takes in a file name and path and uses the GATE document reader "
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

name = "GATE_DocumentReader", tags = "io read file text gate document")
public class GATE_DocumentReader implements ExecutableComponent {
	// ==============
	// Data Members
	// ==============
	// Options
	// -1 by default means all
	private String m_fileName = null;
	protected int m_docsProcessed = 0;
	protected long m_start = 0;

	private static Logger _logger = Logger.getLogger("GATE_DocumentReader");

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	public final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "File encoding?", name = "encoding", defaultValue = "UTF-8")
	public final static String DATA_PROPERTY_FILE_ENCODING = "encoding";

	// IO

	@ComponentInput(description = "File name.", name = "file_name")
	public final static String DATA_INPUT_FILE_NAME = "file_name";

	@ComponentOutput(description = "Document object.", name = "gate_document")
	public final static String DATA_OUTPUT_FILE_GATE_DOC = "gate_document";

	// ================
	// Constructor(s)
	// ================

	public GATE_DocumentReader() {
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

	public void initialize(ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
		GATEInitialiser.init();
		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();
		if (getVerbose(ccp)) {
			System.out
					.println("\nEND EXEC -- GATE_DocumentReader -- Docs Ouput: "
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

			m_fileName = (String) ctx
					.getDataComponentFromInput(DATA_INPUT_FILE_NAME);

			java.net.URL u = new java.io.File(m_fileName).toURL();
			gate.Document doc = readDoc(u, getEncoding(ctx));
			if (doc == null)
				return;
			
			ctx.pushDataComponentToOutput(DATA_OUTPUT_FILE_GATE_DOC, doc);
			
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

	/**
	 * Reads the content in <codE>url</codE> and establishes a Document with
	 * that content.
	 * 
	 * @param url
	 *            URL source for the content of the returned document
	 * @return Document GATE Document containing the content that
	 *         <codE>url</code> refers to.
	 */
	public static Document readDoc(java.net.URL url, String _encoding) {
		try {
			FeatureMap params = Factory.newFeatureMap();
			params.put("sourceUrl", url);
			params.put("preserveOriginalContent", new Boolean(true));
			params.put("collectRepositioningInfo", new Boolean(true));
			params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, _encoding);
			gate.Document doc = (gate.Document) Factory.createResource(
					"gate.corpora.DocumentImpl", params);
			return doc;
		} catch (Exception e) {
			return null;
		}

	}

}
