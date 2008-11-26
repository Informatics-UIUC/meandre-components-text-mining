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

package org.seasr.components.text.tokenize.brown;

//==============
// Java Imports
//==============

import java.util.*;
import java.util.logging.*;

//===============
// Other Imports
//===============
import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.FeatureMap;
import org.seasr.components.text.tokenize.brown.support.Tokenize;

import org.meandre.core.*;

import org.meandre.annotations.*;


/**
 * @author D. Searsmith
 * 
 * TODO: Testing, Unit Tests
 */
@Component(creator = "Duane Searsmith", 
		description = "<p>Overview: <br>" +
		"This module takes an input Document object, parses the raw text into " +
		"tokens, and stores the list of tokens back into the Document object " +
		"that is outpt for further processing.</p>" +
		"<p>The module uses a parser constructed with JavaCC to create tokens and identify " +
		"sentence splits." +
		"The user can specify a maximum number of tokens to create for any one " +
		"document. The user can also specify to exclude the document title from " +
		"tokenization." +
		"This processing done by this module is often a necessary precondition for many " +
		"other language processing tasks such as part-of-speech tagging.</p>" +
		"<p>References: <br>" +
		"See JavaCC: https://javacc.dev.java.net/</p>" +
		"<p>Data Handling: <br>" +
		"This module will modify (as described above) the document object that is input.</p>" +
		"<p>Scalability: <br>" +
		"This module makes one pass over the document text resulting in linear time complexity " +
		"per size of text.  Memory usage is proportional to the size of the text and complexity of the JavaCC grammar. </p>" +
		"<p>Trigger Criteria: <br>" +
		"Standard.</p>",
	name = "Tokenizer", 
	tags = "token tokenize text")
public class Tokenizer implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	// Options
	private int m_docsProcessed = 0;

	private long m_start = 0;

	private static Logger _logger = Logger.getLogger("Tokenizer");

	// ============
	// Properties
	// ============

	// props
	
	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", 
			name = "verbose", 
			defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Exclude tokenization of the title? A boolean value (true or false).", 
			name = "exclude_title", 
			defaultValue = "true")
	final static String DATA_PROPERTY_EXCLUDE_TITLE = "exclude_title";

	@ComponentProperty(description = "If set to true then the raw text is cleared once " + 
			"tokens have been formed.  This can be used to help manage the heap size" + 
			" so long as the original document text is no longer needed. A boolean value (true or false).", 
			name = "clear_text", 
			defaultValue = "false")
	final static String DATA_PROPERTY_CLEAR_TEXT = "clear_text";

	@ComponentProperty(description = "Show progress? A boolean value (true or false).", 
			name = "show_progress", 
			defaultValue = "false")
	final static String DATA_PROPERTY_SHOW_PROGRESS = "show_progress";

	@ComponentProperty(description = "If set to true then the number of tokens created" +
		" is written into the document's feature map. A boolean value (true or false).", 
		name = "include_token", 
		defaultValue = "false")		
	final static String DATA_PROPERTY_TOKEN_COUNT = "include_token";

	// io
	
	@ComponentInput(description = "Input document.", 
			name = "document_in")
	public final static String DATA_INPUT_DOC_IN = "document_in";

	@ComponentOutput(description = "Output document.", 
			name = "document_out")
	public final static String DATA_OUTPUT_DOC_OUT = "document_out";
	
	// ================
	// Constructor(s)
	// ================

	public Tokenizer() {
	}

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
//				"c:/tmp/ThreeLives.txt");
//		// add another component
//		String reader = wflow
//				.addComponent("org.seasr.meandre.components.t2k.io.file.TextFileToDoc");
//
//		// make a connection between two components
//		wflow.connectComponents(pushString, "output_string", reader,
//				TextFileToDoc.DATA_INPUT_FILE_NAME);
//
//		// add another component
//		String tokenizer = wflow
//				.addComponent("org.seasr.meandre.components.t2k.tokenize.brown.Tokenizer_Comp");
//
//		// set a component property
//		wflow.setComponentInstanceProp(tokenizer, "verbose", "true");
//
//		// make a connection between two components
//		wflow.connectComponents(reader,
//				TextFileToDoc.DATA_OUTPUT_FILE_DOC, tokenizer,
//				OpenNLP_Tokenizer.DATA_INPUT_DOC_IN);
//
//		// execute the flow specifying that we want a web UI displayed
//		flowBuilder.execute(wflow, false);
//
//		// For some reason the process does not end without a forced exit.
//		System.exit(0);

	}

	// ================
	// Public Methods
	// ================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getExcludeTitle(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_EXCLUDE_TITLE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getClearText(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_CLEAR_TEXT);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getShowProgress(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_SHOW_PROGRESS);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getTokenCount(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_TOKEN_COUNT);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public void initialize (ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
		long end = System.currentTimeMillis();
		if (getShowProgress(ccp)) {
			_logger.info("\nEND EXEC -- Tokenizer -- Docs Processed: "
					+ m_docsProcessed + " in " + (end - m_start) / 1000
					+ " seconds\n");
		}
		m_docsProcessed = 0;
	}
	
	public void execute(ComponentContext ctx) 
		throws ComponentExecutionException, ComponentContextException {
		try {
			Document idoc = (Document) ctx.getDataComponentFromInput(DATA_INPUT_DOC_IN);
			idoc = Tokenize.tokenize(idoc, getVerbose(ctx), getExcludeTitle(ctx), getTokenCount(ctx));
			if (getVerbose(ctx)) {
				
				AnnotationSet toks = idoc.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);

				_logger.info("\n\nDocument parsed.  "
						+ toks.size()
						+ " tokens created.\n\n");
				
				StringBuffer buff = new StringBuffer();
				for (Iterator<Annotation> itty = toks.getAllSortedAsArrayList().iterator(); itty.hasNext();){
					Annotation tok = itty.next();
					buff.append("Token: " + tok.getContent(idoc));
					FeatureMap fm = tok.getFeatures();
					if (fm.isEmpty()){
						_logger.info(buff.toString());
						buff.delete(0, buff.length());
					} else {
						for (Iterator<String> itty2 = fm.keySet().iterator(); itty2.hasNext();){
							String key = itty2.next();
							String val = fm.get(key).toString();
							buff.append("  <" + key + ", " + val + ">");
						}
						_logger.info(buff.toString());
						buff.delete(0, buff.length());
					}
				}
				
				
			}
			ctx.pushDataComponentToOutput(DATA_OUTPUT_DOC_OUT, idoc);
			m_docsProcessed++;
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: Tokenizer.execute()");
			throw new ComponentExecutionException(ex);
		}
	}
}
