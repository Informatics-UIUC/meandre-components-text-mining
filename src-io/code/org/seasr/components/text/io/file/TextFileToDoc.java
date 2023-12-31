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

package org.seasr.components.text.io.file;

// ==============
// Java Imports
// ==============

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.*;

// ===============
// Other Imports
// ===============

// import org.meandre.tools.components.*;
// import org.meandre.tools.components.FlowBuilderAPI.WorkingFlow;

import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.FeatureMap;
import org.seasr.components.text.util.Factory;
import org.meandre.core.*;
import org.meandre.annotations.*;
import org.meandre.tools.webdav.*;

/**
 * Takes the input text file name and reads the file to create a document object
 * which is output for later processing.
 *
 * @author D. Searsmith
 *
 * TODO: WebDav Support, Testing, Unit Tests TODO: Add option to save file date
 * as document date.
 */
@Component(creator = "Duane Searsmith",

description = "<p>Overview:<br>"
		+ "Takes the input text file location and reads the file to create a document "
		+ "object which is output for later processing. </p>"
		+ "<p>If retain 'new line' property is true then 'new line' characters are not "
		+ "discarded when the file is read.</p>"
		+ "<p> Set 'Add Space at Newline' to true if line termination characters are all "
		+ "that may separate the last word on one line from the first word on the next line.</p>"
		+ "<p>There is also an option to have the parent directory name stored in the document's "
		+ "feature map under the key 'Label'.</p>"
		+ "<p>Writes the file name to the document title and ID.  No date information is "
		+ "recorded.</p>",

name = "Text_File_To_Document", tags = "io read file text",
dependency = {"meandre-webdav-1.4.0.jar"},
baseURL="meandre://seasr.org/components/")
public class TextFileToDoc implements ExecutableComponent {
	// ==============
	// Data Members
	// ==============

	private String m_fileName = null;

	private int m_docsProcessed = 0;

	private long m_start = 0;

	private BufferedReader m_reader = null;

	private String _newLine = System.getProperty("line.separator");

	private static Logger _logger = Logger.getLogger("TextFileToDoc");

	// props

	@ComponentProperty(description = "Store the dir name in the feature map under 'label'. A boolean value (true or false).", name = "store_dir_name", defaultValue = "false")
	public final static String DATA_PROPERTY_STORE_DIR_NAME = "store_dir_name";

	@ComponentProperty(description = "Retain newline characters? A boolean value (true or false).", name = "retain_new_lines", defaultValue = "false")
	public final static String DATA_PROPERTY_RETAIN_NEW_LINES = "retain_new_lines";

	@ComponentProperty(description = "Add space at new lines? A boolean value (true or false).", name = "add_space_at_new_lines", defaultValue = "false")
	public final static String DATA_PROPERTY_ADD_SPACE_AT_NEW_LINES = "add_space_at_new_lines";

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	public final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Treat source as webdav?", name = "webdav", defaultValue = "false")
	public final static String DATA_PROPERTY_WEBDAV = "webdav";

	// io

	@ComponentInput(description = "File name.", name = "file_name")
	public final static String DATA_INPUT_FILE_NAME = "file_name";

	@ComponentOutput(description = "Document object.", name = "Document")
	public final static String DATA_OUTPUT_FILE_DOC = "Document";

	// ================
	// Constructor(s)
	// ================

	public TextFileToDoc() {
	}

	// ================
	// Public Methods
	// ================

	public boolean getSaveDirName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_STORE_DIR_NAME);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getRetainNewlineChars(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_RETAIN_NEW_LINES);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getAddSpaceAtNewlineChars(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_ADD_SPACE_AT_NEW_LINES);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public void initialize(ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();
		m_reader = null;
	}

	public boolean getWebdav(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_WEBDAV);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();
		_logger.info("\nEND EXEC -- TextFileToDoc -- Docs Ouput: "
				+ m_docsProcessed + " in " + (end - m_start) / 1000
				+ " seconds\n");
		m_docsProcessed = 0;
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		_logger.fine("execute() called");

		// props =================================
		boolean verbose = this.getVerbose(ctx);
		boolean webdav = this.getWebdav(ctx);
		boolean addSpace = this.getAddSpaceAtNewlineChars(ctx);
		boolean retNL = this.getRetainNewlineChars(ctx);
		boolean saveDir = this.getSaveDirName(ctx);
		//========================================

		try {
			StringBuffer body = new StringBuffer();
			m_fileName = (String) ctx
					.getDataComponentFromInput(DATA_INPUT_FILE_NAME);

			File fvar = null;
			byte[] barr = null;
			if (webdav) {
				barr = new WebdavClient(m_fileName)
						.getResourceAsByteArray(m_fileName);
			} else {
			    if (m_fileName.startsWith("file:/"))
			        m_fileName = m_fileName.substring(5);

				fvar = new File(m_fileName);
			}
			if ((fvar != null) && (!fvar.exists())) {
				throw new Exception("Input file does not exist: "
						+ fvar.getPath());
			}
			if (webdav && barr == null) {
				throw new Exception("Webdav file does not exist: "
						+ m_fileName);
			}
			String dirName = null;
			String label = null;
			if (!webdav){
			dirName = fvar.getPath().substring(0,
					fvar.getPath().lastIndexOf(File.separator));
			label = dirName.substring(dirName
					.lastIndexOf(File.separator) + 1, dirName.length());
			} else {
				dirName = m_fileName.substring(0,
						m_fileName.lastIndexOf("/"));
				label = dirName.substring(dirName
						.lastIndexOf("/") + 1, dirName.length());
			}
			if (fvar != null) {
				m_reader = new BufferedReader(new FileReader(fvar));
			} else {
				m_reader = new BufferedReader(new InputStreamReader(
						new ByteArrayInputStream(barr)));
			}
			String s = m_reader.readLine();
			body.append(s);
			if (retNL) {
				body.append(_newLine);
			}
			while ((s = m_reader.readLine()) != null) {
				body.append(s);
				if (addSpace) {
					body.append(" ");
				}
				if (retNL) {
					body.append(_newLine);
				}
			}
			Document doc = Factory.newDocument();
			doc.setContent(body.toString());
			String name = (webdav)?m_fileName:fvar.getName();
			doc.setTitle(name);
			doc.setDocID(name);
			if (verbose) {
				_logger.info("TFTD: " + doc.getDocID());
				_logger.info("TFTD: " + doc.getTitle());
			}
			if (saveDir) {
				FeatureMap features = Factory.newFeatureMap();
				String key = "Label";
				features.put(key, label);
				doc.setFeatures(features);
			}
			ctx.pushDataComponentToOutput(DATA_OUTPUT_FILE_DOC, doc);
			m_docsProcessed++;
			if (Math.IEEEremainder(m_docsProcessed, 100) == 0) {
				_logger.fine("TextFileToDoc -- Docs Processed: "
						+ m_docsProcessed);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: TextFileToDoc.execute()");
			throw new ComponentExecutionException(ex);
		} finally {
			if (m_reader != null) {
				try {
					m_reader.close();
				} catch (IOException ioe) {
					_logger.severe(ioe.getMessage());
				}
			}
			m_reader = null;
		}
	}
}
