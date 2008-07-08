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

package org.seasr.components.text.transform;

//==============
//Java Imports
//==============

import java.util.logging.*;

//===============
//Other Imports
//===============

import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.transform.support.DocToRDFModel;

import org.meandre.core.*;
import org.meandre.annotations.*;
//import org.meandre.tools.components.*;
//import org.meandre.tools.components.FlowBuilderAPI.WorkingFlow;

import com.hp.hpl.jena.rdf.model.Model;

/**
* 
* @author D. Searsmith
*
* TODO: Testing, Unit Testing
*
*/

@Component(creator = "Duane Searsmith", 
		description = "Overview: "
+ "This module takes in a Document object that has been tokenized and "
+ "outputs a list of the tokens, or terms, and the number of times each "
+ "token appears (frequency count) as a TermList object. "
+ "Data Type Restrictions: "
+ "The input document must have been tokenized."
+ "Data Handling: "
+ "This module creates a new TermList object for each document."
+ "Scalability: "
+ "This module makes one pass over the token list resulting in linear time complexity "
+ "per the number of tokens.  Memory usage is proportional to the number tokens."
+ "Trigger Criteria: "
+ "Standard.",
	name = "DocumentToRDFModel", tags = "text document rdf jena transform")
public class DocumentToRDFModel implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private int m_docsProcessed = 0;

	private static Logger _logger = Logger.getLogger("DocumentToRDFModel");

	// props
	
	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Debug? A boolean value (true or false).", name = "debug", defaultValue = "false")
	final static String DATA_PROPERTY_DEBUG = "debug";

	@ComponentProperty(description = "Clear the document object? A boolean value (true or false).", name = "free_doc", defaultValue = "true")
	final static String DATA_PROPERTY_FREE_DOC = "free_doc";

	// io
	
	@ComponentInput(description = "Document object.", name = "document")
	public final static String DATA_INPUT_DOCUMENT = "document";

	@ComponentOutput(description = "RDF (Jena) Model.", name = "rdf_model")
	public final static String DATA_OUTPUT_RDF_MODEL = "rdf_model";

	// ================
	// Static Methods
	// ================

	/**
	 * Test
	 */	
	static public void main(String[] args) {

//		// get a flow builder instance
//		FlowBuilderAPI flowBuilder = new FlowBuilderAPI();
//		// get a flow object
//		WorkingFlow wflow = flowBuilder.newWorkingFlow("test");
//		// add a component
//		String pushString = wflow
//				.addComponent("org.seasr.meandre.components.io.PushString");
//		// set a component property
//		wflow.setComponentInstanceProp(pushString, "string",
//				"c:/CLOJURE/effinay/test/sample.txt");
//		// add another component
//		String reader = wflow
//				.addComponent("org.seasr.meandre.components.t2k.io.file.TextFileToDoc");
//
//		// make a connection between two components
//		wflow.connectComponents(pushString, "output_string", reader,
//				TextFileToDoc.DATA_INPUT_FILE_NAME);
//
//		// add another component
//		String sentdetector = wflow
//				.addComponent("org.seasr.meandre.components.t2k.sentence.opennlp.OpenNLP_SentenceDetect");
//
//		// make a connection between two components
//		wflow.connectComponents(reader, TextFileToDoc.DATA_OUTPUT_FILE_DOC,
//				sentdetector, OpenNLP_SentenceDetect.DATA_INPUT_DOC_IN);
//
//		
//		// add another component
//		String doctordf = wflow
//				.addComponent("org.seasr.meandre.components.t2k.convert.DocumentToRDFModel_Comp");
//
//		// make a connection between two components
//		wflow.connectComponents(sentdetector, OpenNLP_SentenceDetect.DATA_OUTPUT_DOC_OUT,
//				doctordf, DocumentToRDFModel_Comp.DATA_INPUT_DOCUMENT);
//
//		
//		wflow.setComponentInstanceProp(sentdetector, OpenNLP_SentenceDetect.DATA_PROPERTY_VERBOSE, "true");
//		
//		// execute the flow specifying that we want a web UI displayed
//		flowBuilder.execute(wflow, false);
//
//		// For some reason the process does not end without a forced exit.
//		System.exit(0);

	}

	
	// ================
	// Constructor(s)
	// ================

	public DocumentToRDFModel() {
	}

	// ================
	// Public Methods
	// ================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getDebug(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_DEBUG);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getFreeDoc(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_FREE_DOC);
		return Boolean.parseBoolean(s.toLowerCase());
	}


	// =====================================
	// Interface Impl: ExecutableComponent
	// =====================================

	/* (non-Javadoc)
	 * @see org.meandre.core.ExecutableComponent#initialize(org.meandre.core.ComponentContextProperties)
	 */
	public void initialize(ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
		m_docsProcessed = 0;
	}

	/* (non-Javadoc)
	 * @see org.meandre.core.ExecutableComponent#dispose(org.meandre.core.ComponentContextProperties)
	 */
	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		if (getVerbose(ccp) || getDebug(ccp)) {
			_logger.info("\nEND EXEC -- DocumentToRDFModel -- Docs Processed: "
							+ m_docsProcessed + "\n");
		}
		m_docsProcessed = 0;
	}

	
	/* 
	 * In frequency include all occurrences of a term even if it only matches
	 * the POS tag criteria for a subset of occurrences.
	 * 
	 * (non-Javadoc)
	 * @see org.meandre.core.ExecutableComponent#execute(org.meandre.core.ComponentContext)
	 */
	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		_logger.fine("execute() called");
		try {
			
			Document doc = (Document)ctx.getDataComponentFromInput(DATA_INPUT_DOCUMENT);
			
			Model mod = DocToRDFModel.getModelFromDocument(doc, getVerbose(ctx));
			ctx.pushDataComponentToOutput(DATA_OUTPUT_RDF_MODEL, mod);
			if (getFreeDoc(ctx)) {
				doc.free();
			}
			m_docsProcessed++;
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: DocumentToRDFModel.execute()");
			throw new ComponentExecutionException(ex);
		}
	}

}