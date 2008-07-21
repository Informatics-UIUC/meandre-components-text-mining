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

/**
 * @author D. Searsmith
 */
package org.seasr.components.text.gate.util;

import gate.creole.ResourceInstantiationException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.seasr.components.text.datatype.corpora.DocumentConstants;

public class GATEUtils {

	//==============	
	// Data Members
	//==============

	static private Object _lock = new Object();
	private static Logger _logger = Logger.getLogger("GATEInitialiser");

	/**
	 * Create a path to the GATE resource within the SEASR context
	 * on the machine that is executing this component.
	 * 
	 * @param prPath Path to the root of the SEASR resource repository.
	 * @param resPath Path from the resource name to the target file/dir.
	 * @param resName GATE Resource name, directly follows the prPath.
	 * @return
	 * @throws IOException
	 */
	static public String normalizePathForSEASR(String prPath, String resPath,
			String resName) throws IOException {
		File prFile = new File(prPath);
		return "file:/" + prFile.getCanonicalPath() + "/" + resName + resPath;
	}

	/**
	 * Check if a GATE document object exists in the SEASR document object's
	 * auxiliary map.
	 * 
	 * @param doc SEASR document object to check.
	 * @return A boolean value indicating that a GATE document does or doesn't
	 * exist in the SEASR document's auxiliary map.
	 */
	static public boolean checkIfGATEDocumentExists(
			org.seasr.components.text.datatype.corpora.Document doc) {
		if (doc.getAuxMap().get(DocumentConstants.GATE_DOCUMENT) == null) {
			return false;
		}
		return true;
	}

	/**
	 * Add a newly created GATE document to the auxiliary feature map of the 
	 * SEASR document only if it does not already exist.  This adding process
	 * takes place within a sync block where a check of pre-existence is
	 * performed to ensure that only one distinct GATE document object is ever
	 * created and added in this method to the SEARS document.
	 * 
	 * @param doc SEASR document
	 * @return SEASR document
	 * @throws ResourceInstantiationException
	 */
	static public org.seasr.components.text.datatype.corpora.Document addNewGATEDocToSEASRDoc(
			org.seasr.components.text.datatype.corpora.Document doc)
			throws ResourceInstantiationException {
		synchronized (_lock) {
			if (!checkIfGATEDocumentExists(doc)) {
				_logger.info("Adding GATE document to SEASR document " + doc.getDocID());
				gate.Document gdoc = gate.Factory.newDocument(doc.getContent());
				doc.getAuxMap().put(DocumentConstants.GATE_DOCUMENT, gdoc);
			}
			return doc;
		}
	}

	
}
