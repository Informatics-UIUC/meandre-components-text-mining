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

// ==============
// Java Imports
// ==============

import java.util.logging.*;

// ===============
// Other Imports
// ===============

import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentExecutionException;
import org.meandre.core.ExecutableComponent;

// import org.meandre.tools.components.*;
// import org.meandre.tools.components.FlowBuilderAPI.WorkingFlow;

import org.meandre.core.*;
import org.meandre.annotations.*;
import org.seasr.components.text.pos.brill.support.Lexicon;

/**
 * 
 * Overview: This module takes the input filename and reads the file to create a
 * Lexicon object which is output for later processing. The Lexicon object is
 * used by the Brill part-of-speech tagger modules. References: Brill, Eric,
 * <i>Some Advances In Rule-Based Part of Speech Tagging</i>, AAAI, 1994
 * 
 * @author D. Searsmith
 * 
 * TODO: Unit Testing
 * 
 */

@Component(creator = "Duane Searsmith", 
		
		description = "<p>Overview: <br>"
		+ "This module takes the input filename and reads the file to create a Lexicon "
		+ "object which is output for later processing.  "
		+ "The Lexicon object is used by the Brill "
		+ "part-of-speech tagger modules.</p>"
		+ "<p>Reference: <br>"
		+ "Brill, Eric, Some Advances In Rule-Based Part of Speech Tagging, AAAI, 1994 </p>", 
		
		name = "LexiconReader", tags = "io read text pos brill lexicon nlp",
        baseURL="meandre://seasr.org/components/")
public class LexiconReader implements ExecutableComponent {
	// ==============
	// Data Members
	// ==============
	static public final String s_RESID = "LEXICON";

	private static Logger _logger = Logger.getLogger("LexiconReader");

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	// IO

	@ComponentInput(description = "File name.", name = "file_name")
	public final static String DATA_INPUT_FILE_NAME = "file_name";

	@ComponentOutput(description = "Lexicon object.", name = "lexicon")
	public final static String DATA_OUTPUT_LEXICON = "lexicon";

	// ================
	// Constructor(s)
	// ================
	public LexiconReader() {
	}

	// ================
	// Public Methods
	// ================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	/**
	 * put your documentation comment here
	 * 
	 * @exception java.lang.Exception
	 */
	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		_logger.fine("execute() called");
		try {
			String fname = (String) ctx
					.getDataComponentFromInput(DATA_INPUT_FILE_NAME);
			Lexicon lex = new Lexicon(fname, getVerbose(ctx));
			if (getVerbose(ctx)) {
				_logger.info("\n\nLexicon file parsed.  " + lex.size()
						+ " entries created.\n\n");
			}
			ctx.pushDataComponentToOutput(DATA_OUTPUT_LEXICON, lex);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			System.out.println("ERROR: LexiconReader.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

	public void initialize(ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
	}

	/**
	 * put your documentation comment here
	 */
	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
	}

}
