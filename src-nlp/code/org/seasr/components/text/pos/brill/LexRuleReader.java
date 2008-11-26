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

package org.seasr.components.text.pos.brill;

//==============
// Java Imports
//==============

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;
import java.util.logging.*;

//===============
// Other Imports
//===============

//import org.meandre.tools.components.*;
//import org.meandre.tools.components.FlowBuilderAPI.WorkingFlow;

import org.meandre.core.*;
import org.meandre.annotations.*;
import org.seasr.components.text.pos.brill.support.LexRule;

/**
 * <p>
 * Overview: This module takes the input filename and reads the file to create a
 * <i>Lexical Rules</i> object which is output for later processing. The <i>Lexical
 * Rules</i> object is used by the Brill part-of-speech tagger modules.
 * </p>
 * 
 * <p>
 * References: Brill, Eric, <i>Some Advances In Rule-Based Part of Speech
 * Tagging</i>, AAAI, 1994
 * </p>
 * 
 * @author D. Searsmith
 * 
 * TODO: Unit Test, Testing
 */

@Component(creator = "Duane Searsmith", 
		
		description = "<p>Overview: <br>"
		+ "This module takes the input filename and reads the file to create a Lexical Rules "
		+ "object which is output for later processing. The Lexical Rules object is used by the Brill "
		+ "part-of-speech tagger modules.</p>"
		+ "<p>References: <br>"
		+ "Brill, Eric, Some Advances In Rule-Based Part of Speech Tagging, AAAI, 1994 </p>", 
		
		name = "LexRuleReader", 
		tags = "brill pos io read file text nlp")
public class LexRuleReader implements ExecutableComponent {
	
	// ==============
	// Data Members
	// ==============

	static public final String s_RESID = "LEXICAL_RULES";

	private static Logger _logger = Logger.getLogger("ReadFileNames");

	// props
	
	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	// io
	
	@ComponentInput(description = "Lexical rule file name.", name = "file_name")
	public final static String DATA_INPUT_FILE_NAME = "file_name";

	@ComponentOutput(description = "Array of rule objects.", name = "lexical_rules")
	public final static String DATA_OUTPUT_LEXICAL_RULES = "lexical_rules";

	// ================
	// Constructor(s)
	// ================
	
	public LexRuleReader() {
	}

	// ================
	// Static Methods
	// ================

	/**
	 * Test
	 */
	static public void main(String[] args) {

		// // get a flow builder instance
		// FlowBuilderAPI flowBuilder = new FlowBuilderAPI();
		// // get a flow object
		// WorkingFlow wflow = flowBuilder.newWorkingFlow("test");
		// // add a component
		// String pushString = wflow
		// .addComponent("org.seasr.meandre.components.io.PushString");
		// // set a component property
		// wflow.setComponentInstanceProp(pushString, "string",
		// "http://norma.ncsa.uiuc.edu/public-dav/capitanu");
		// // add another component
		// String reader = wflow
		// .addComponent("org.seasr.meandre.components.t2k.io.file.ReadFileNames");
		// wflow.setComponentInstanceProp(reader, DATA_PROPERTY_FILTER,
		// ".*\\.xml");
		// wflow.setComponentInstanceProp(reader, DATA_PROPERTY_WEBDAV, "true");
		// // make a connection between two components
		// wflow.connectComponents(pushString, "output_string", reader,
		// DATA_INPUT_DIRNAME);
		//
		// // execute the flow specifying that we want a web UI displayed
		// flowBuilder.execute(wflow, false);
		//
		// // For some reason the process does not end without a forced exit.
		// System.exit(0);

	}

	// ================
	// Public Methods
	// ================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meandre.core.ExecutableComponent#initialize(org.meandre.core.ComponentContextProperties)
	 */
	public void initialize(ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meandre.core.ExecutableComponent#execute(org.meandre.core.ComponentContext)
	 */
	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		_logger.fine("execute() called");
		try {
			String fname = (String) ctx
					.getDataComponentFromInput(DATA_INPUT_FILE_NAME);
			Vector<LexRule> rules = readRules(fname);
			if (getVerbose(ctx)) {
				_logger.info(rules.size() + " rules created.");
			}
			Object[] rls = rules.toArray();
			ctx.pushDataComponentToOutput(DATA_OUTPUT_LEXICAL_RULES, rls);
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: LexRuleReader.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meandre.core.ExecutableComponent#dispose(org.meandre.core.ComponentContextProperties)
	 */
	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
	}

	// =================
	// Private Methods
	// =================
	public static Vector<LexRule> readRules(String filename) {
		LexRule rule = null;
		Vector<LexRule> fv = new Vector<LexRule>();
		int linecnt = 0;
		try {
			String line = null;
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			while ((line = reader.readLine()) != null) {
				linecnt++;
				rule = LexRule.createRule(line);
				if (rule != null) {
					fv.addElement(rule);
				}
			}
			_logger.info(linecnt + " rule definitions read.");
		} catch (Exception e) {
			_logger.fine("ERROR in rule reading process: " + e);
			e.printStackTrace();
		}
		return fv;
	}
}
