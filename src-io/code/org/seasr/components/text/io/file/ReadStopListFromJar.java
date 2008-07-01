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

import java.util.Set;
import java.util.logging.Logger;

// ===============
// Other Imports
// ===============

import org.meandre.core.*;
import org.meandre.annotations.*;
import org.meandre.components.util.MeandreReadJarredTextFileListIntoHashSet;

/**
 * <p>
 * Overview: <br>
 * This component takes the resource and file name and reads the text file to
 * create a <i>HashSet</i> (of strings from the text list) object which is
 * output. This <i>HashSet</i> object is used by the <i>Filter Stops</i>
 * component for example.
 * </p>
 * 
 * TODO: Testing, Unit Tests
 * 
 * @author D. Searsmith
 */
@Component(creator = "Duane Searsmith",

description = "<p>Overview: <br>"
		+ "This component takes the input resource and file name and reads the text file to create a <i>HashSet</i>"
		+ "(of strings from the text list) object which is output.  This <i>HashSet</i> object is used "
		+ "by the <i>Filter Stops</i> component for example." + "</p>",

name = "ReadStopListFromJar", tags = "text read io file stops stopwords nlp", dependency={"SEASR_Brill.jar.jar"})
public class ReadStopListFromJar implements ExecutableComponent {
	// ==============
	// Data Members
	// ==============

	private static Logger _logger = Logger.getLogger("ReadStopListFromJar");

	// props

	@ComponentProperty(description = "Verbose output?", name = "verbose", defaultValue = "false")
	public final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Stop list resource file name.", name = "resource_name", defaultValue = "brill/common_words")
	final static String DATA_PROPERTY_RESOURCE_NAME = "resource_name";

	@ComponentProperty(description = "Stop list file name.", name = "filename", defaultValue = "/seasr/text/stops/common_words")
	final static String DATA_PROPERTY_FILENAME = "filename";

	// IO

	@ComponentOutput(description = "Set of strings.", name = "set_of_strings")
	public final static String DATA_OUTPUT_SET_OF_STRINGS = "set_of_strings";

	// ================
	// Constructor(s)
	// ================
	public ReadStopListFromJar() {
	}

	// ================
	// Public Methods
	// ================

	// Props

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public String getResourceName(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_RESOURCE_NAME);
		return s;
	}

	public String getFilename(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_FILENAME);
		return s;
	}

	// ====================================
	// ExecutableComponent Interface Impl
	// ====================================

	public void initialize(ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
	}

	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		_logger.fine("execute() called");
		try {
			Set<String> stops = MeandreReadJarredTextFileListIntoHashSet
					.getSet(getResourceName(ctx), getFilename(ctx), 1600, ctx);

			if (getVerbose(ctx)) {
				_logger.info(stops.size() + "stop words read.");
			}
			ctx.pushDataComponentToOutput(DATA_OUTPUT_SET_OF_STRINGS, stops);
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: ReadStopListFromJar.doit()");
			throw new ComponentExecutionException(ex);
		}
	}

}
