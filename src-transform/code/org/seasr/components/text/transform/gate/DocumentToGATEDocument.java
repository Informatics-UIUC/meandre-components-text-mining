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

package org.seasr.components.text.transform.gate;

// ==============
// Java Imports
// ==============

import java.util.*;
import java.util.logging.Logger;
import java.io.*;

// ===============
// Other Imports
// ===============

import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.FeatureMap;

import org.meandre.core.*;

import org.meandre.annotations.*;

/**
 * <p><b>Overview</b>: <br>
 * This module converts a document object in the SEASR 
 * framework to an equivalent document object in the 
 * GATE framework.</p>
 * <p><b>Detailed Description</b><br>: 
 * Given a document object in the SEASR framework, 
 * this module will convert it to the equivalent 
 * document in the GATE framework.  A copy of the 
 * content, which is a String, is made.  All 
 * associated Annotations and AnnotationSets are 
 * copied as well.  The IDs of the Annotations, Nodes 
 * and AnnotationSets are <i>not</i> preserved. 
 * Everything else such as the FeatureMap mappings 
 * and values are preserved.</p>
 * <p>NOTE: SEASR token information is not converted to GATE 
 * token objects.  If you need GATE token object then you 
 * must run through the GATE Tokenizer.</p>
 * 
 * @author Xiaolei Li
 * @author D. Searmith
 */
@Component(creator = "Duane Searsmith", 
		description = "<p><b>Overview</b>: <br>"
        + "This module converts a document object in the SEASR "
        + "framework to an equivalent document object in the "
        + "GATE framework.</p>"
        + "<p><b>Detailed Description</b><br>: "
        + "Given a document object in the SEASR framework, "
        + "this module will convert it to the equivalent "
        + "document in the GATE framework.  A copy of the "
        + "content, which is a String, is made.  All "
        + "associated Annotations and AnnotationSets are "
        + "copied as well.  The IDs of the Annotations, Nodes "
        + "and AnnotationSets are <i>not</i> preserved."  
        + "Everything else such as the FeatureMap mappings "
        + "and values are preserved.</p>"
        + "<p>NOTE: SEASR token information is not converted to GATE "
        + "token objects.  If you need GATE token object then you "
        + "must run through the GATE Tokenizer.</p>",
	name = "DocumentToGATEDocument", 
	tags = "text gate transform document")
public class DocumentToGATEDocument implements ExecutableComponent
{
    // ==============
    // Data Members
    // ==============
    private int m_docsProcessed = 0;
    private long m_start = 0;
	private static Logger _logger = Logger.getLogger("DocumentToGATEDocument");

	// props
	
	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", 
			name = "verbose", 
			defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Include title in content? Appends the title to the "
		+ "front of the document content so that GATE will process it.", 
		name = "include_title_in_content", 
		defaultValue = "false")
	final static String DATA_PROPERTY_INCLUDE_TITLE_IN_CONTENT = "include_title_in_content";

	// io
	
	@ComponentInput(description = "Input SEASR document.", 
			name = "seasr_document_in")
	public final static String DATA_INPUT_SEASR_DOC_IN = "seasr_document_in";

	@ComponentOutput(description = "Output GATE document.", 
			name = "gate_document_out")
	public final static String DATA_OUTPUT_GATE_DOC_OUT = "gate_document_out";

    // ================
    // Constructor(s)
    // ================
    public DocumentToGATEDocument()
    {
    }

    // ================
    // Public Methods
    // ================
    // ========================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getIncludeTitleInContent(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_INCLUDE_TITLE_IN_CONTENT);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public void initialize(ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
		m_docsProcessed = 0;
		m_start = System.currentTimeMillis();
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose() called");
        long end = System.currentTimeMillis();

        if (this.getVerbose(ccp)) {
            System.out.println("\nEND EXEC -- DocumentToGATEDocument" +
                    "-- Docs Ouput: " + m_docsProcessed + " in " +
                    (end - m_start) / 1000 + " seconds\n");
        }

        m_docsProcessed = 0;
	}
	
	public void execute(ComponentContext ctx) 
		throws ComponentExecutionException, ComponentContextException {
		try {
			Document idoc = (Document) ctx.getDataComponentFromInput(DATA_INPUT_SEASR_DOC_IN);

            // create a GATE document
            gate.Document gate_doc = new gate.corpora.DocumentImpl();

            // copy over the content, which is a string
            String str = null;

            // include the title?
            if (this.getIncludeTitleInContent(ctx)) {
                String shold = idoc.getTitle();
                if (shold == null){
                    shold = "";
                }
                else {
                    shold = " " + shold + ".";
                }
                str = new String(idoc.getContent() + shold);
            }
            else {
                str = new String(idoc.getContent());
            }

            gate.corpora.DocumentContentImpl content = new
                gate.corpora.DocumentContentImpl(str);
            gate_doc.setContent(content);

            // copy feature map
            gate.FeatureMap fm = gate.Factory.newFeatureMap();
            if (idoc.getFeatures() != null) {
                Map<String, String> map = (Map<String, String>) idoc.getFeatures();
                for (Iterator<String> it = map.keySet().iterator(); it.hasNext();){
                    Object key = it.next();
                    fm.put(key, map.get(key));
                }
            }

            try {
                if (idoc.getDocID() != null) {
                    File f = new File(idoc.getDocID());
                    if ( (f != null) && (f.exists())) {
                        gate_doc.setSourceUrl(f.toURL());
                        fm.put("sourceUrl", f.toURL());
                    }
                }
            }
            catch (Exception e) {
                System.out.println(e);
                // e.printStackTrace();
            }

            // set the feature map in the GATE document
            gate_doc.setFeatures(fm);

            /*
			 * Object matchesValue =
			 * gate_doc.getFeatures().get("MatchesAnnots");
			 */

            // variable declarations
            AnnotationSet seasr_annots;
            Annotation seasr_a;
            Iterator<Annotation> seasr_annots_iter;
            gate.AnnotationSet gate_annots;
            Map<String, AnnotationSet> seasr_named_annots;
            Object[] seasr_named_annnots_keys = null;
            int num_sets_to_copy = -1;

            // copy over the annotation sets. each document has 2 types
            // of annotation sets.
            // (1) the default one, which has no name and always exists.
            // one can get to it by calling getAnnotations().
            // (2) the named one(s), which have names and might exist.
            // one can get to them by calling getAnnotations(String name);

            // the following loop copies over the default and the named
            // annotation sets. the for-loop starts at -1 because -1 is
            // reserved for the default annotation set. 0 and after are
            // for the named sets, if they exist.


            seasr_named_annots = idoc.getNamedAnnotationSets();

            if (seasr_named_annots == null)
                num_sets_to_copy = 0;
            else {
                // the set names
                seasr_named_annnots_keys =
                    seasr_named_annots.keySet().toArray();

                num_sets_to_copy = seasr_named_annnots_keys.length;
            }

            // for all the annotation sets (default & named)
            for (int u = -1; u < num_sets_to_copy; u++) {

                if (u == -1) {
                    // get the default set of annotations from the t2k document
                    seasr_annots = idoc.getAnnotations();

                    // get the default set of annotations from the GATE document
                    gate_annots = gate_doc.getAnnotations();
                }
                else {
                    // get the named set of annotations from the t2k document
                    seasr_annots = idoc.getAnnotations((String)
                            seasr_named_annnots_keys[u]);

                    // get the named set of annotations from the GATE
                    // document. note that GATE should create annotation
                    // set in the document if it doesn't exist.
                    gate_annots = gate_doc.getAnnotations((String)
                            seasr_named_annnots_keys[u]);
                }

                // get an iterator over the T2K annotation set
                seasr_annots_iter = seasr_annots.iterator();

                // iterate over annotations in the set
                while (seasr_annots_iter.hasNext()) {

                    // get the annotation
                    seasr_a = (Annotation)
                        seasr_annots_iter.next();

                    // create a GATE FeatureMap
                    gate.FeatureMap gate_fmap = new
                        gate.util.SimpleFeatureMapImpl();

                    // get the original features in the T2K document
                    FeatureMap t2k_fmap =
                        seasr_a.getFeatures();

                    Object[] keys = t2k_fmap.keySet().toArray();

                    // copy over the features (key-value mappings)
                    for (int i = 0; i < keys.length; i++) {
                        gate_fmap.put(keys[i], t2k_fmap.get(keys[i]));
                    }

                    // create a brand new annotation in the GATE document
                    // gate_annots.add(t2k_a.getStartNode().getOffset(),
                    // t2k_a.getEndNode().getOffset(),
                    gate_annots.add(new Long(seasr_a.getStartNodeOffset()),
                                    new Long(seasr_a.getEndNodeOffset()),
                                    seasr_a.getType(),
                                    gate_fmap);
                }
            }

            // push out the converted GATE document
			ctx.pushDataComponentToOutput(DATA_OUTPUT_GATE_DOC_OUT, gate_doc);
			m_docsProcessed++;

            // verbose reporting
            if (this.getVerbose(ctx)) {
                if (Math.IEEEremainder(m_docsProcessed, 10) == 0) {
                    _logger.info("DocumentToGATEDocument -- " +
                        "Docs Processed: " + m_docsProcessed);
                }
            }
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: DocumentToGATEDocument.execute()");
			throw new ComponentExecutionException(ex);
		}
	}
}


