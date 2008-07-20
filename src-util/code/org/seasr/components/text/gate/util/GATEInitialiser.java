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

package org.seasr.components.text.gate.util;

import gate.Factory;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import org.meandre.components.util.Unzipper;
import org.meandre.core.ComponentContext;
import org.seasr.components.text.datatype.corpora.*;

@SuppressWarnings("unchecked")
public class GATEInitialiser implements Serializable {

	// ==============
	// Data Members
	// ==============

	private static final long serialVersionUID = 1L;
	static public boolean s_ready = false;
	static private Object _lock_1 = new Object();
	static private Object _lock_2 = new Object();
	private static Logger _logger = Logger.getLogger("GATEInitialiser");

	// ================
	// Constructor(s)
	// ================
	public GATEInitialiser() {
	}

	/**
	 * Initialize the GATE environment for running GATE codes in SEASR
	 * components.  Checks to see if the GATE resources have been installed
	 * and if not installs them.  Sets up a new user session and config file
	 * for each independent execution flows containing GATE components.
	 * @param path
	 * @param resName
	 * @param installPath
	 * @param ctx
	 * @throws Exception
	 */
	static public void init(String path, String resName, String installPath,
			ComponentContext ctx) throws Exception {
		synchronized (_lock_1) {

			if (!s_ready) {

				Unzipper.CheckIfZipFileExistsIfNotInstallFromJarThenUnzipIt(
						path, resName, installPath, ctx);

				gate.Gate.setGateHome(new File(path + resName));
				gate.Gate.setPluginsHome(new File(path + resName + "/plugins"));
				String sid = ctx.getFlowExecutionInstanceID();
				sid = sid.replaceAll("/", "_");
				sid = sid.replaceAll(":", "");
				String userP = path + resName + "/" + sid;
				File f = new File(userP);
				File userConf = new File(userP + "/" + "gate.xml");
				File userSess = new File(userP + "/" + "gate.session");
				if (!f.exists()) {
					f.mkdir();
					userSess.createNewFile();
					userConf.createNewFile();
					gate.Gate.setUserSessionFile(userSess);
					gate.Gate.setUserConfigFile(userConf);
					FileOutputStream fos = new FileOutputStream(userConf);
					OutputStreamWriter writer = new OutputStreamWriter(fos,
							"UTF-8");
					String nl = System.getProperty("line.separator");
					writer
							.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
									+ nl
									+ "<!-- gate.xml: GATE configuration data -->"
									+ nl
									+ "<GATE>"
									+ nl
									+ ""
									+ nl
									+ "<!-- NOTE: the next element may be overwritten by the GUI!!! -->"
									+ nl + "<GATECONFIG/>" + nl + "" + nl
									+ "</GATE>" + nl);
					writer.close();
					gate.Gate.init();
					gate.Gate.writeUserConfig();
				} else {
					gate.Gate.setUserConfigFile(userConf);
					gate.Gate.setUserSessionFile(userSess);
					gate.Gate.init();
				}
				_logger.info("Gate inited.");
				s_ready = true;
			}
		}
	}

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
		synchronized (_lock_2) {
			if (!checkIfGATEDocumentExists(doc)) {
				gate.Document gdoc = gate.Factory.newDocument(doc.getContent());
				doc.getAuxMap().put(DocumentConstants.GATE_DOCUMENT, gdoc);
			}
			return doc;
		}
	}

}
