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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.*;

//===============
// Other Imports
//===============

import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.DocumentConstants;
import org.seasr.components.text.datatype.corpora.FeatureMap;
import org.seasr.components.text.datatype.pos.PoSToken;
import org.seasr.components.text.datatype.pos.TokenFlag;
import org.seasr.components.text.util.Factory;

/**
 * @author dsears
 * 
 * @TODO: Unit Testing
 */
public class Tokenize {

	private static Logger _logger = Logger.getLogger("Tokenize");

	/**
	 * put your documentation comment here
	 * 
	 * @param idoc
	 * @return
	 * @exception Exception
	 */
	static public Document tokenize(Document idoc, boolean verbose,
			boolean exclude_title, boolean tok_cnt) throws Exception {
		int cnt = 0;
		// get raw text for body
		String buf = idoc.getContent();
		String buftit = null;
		// parse body text
		BrownStandardParser parser = null;
		ArrayList<PoSToken> list = null;
		if (buf.trim().length() > 0) {
			parser = new BrownStandardParser(new ByteArrayInputStream(buf
					.toString().getBytes()));
			try {
				list = parser.corpus();
			} catch (Exception e) {
				if (verbose) {
					e.printStackTrace();
				}
				list = null;
			}
		}
		ArrayList<PoSToken> list2 = null;
		if ((idoc.getTitle() != null) && (idoc.getTitle().length() > 0)
				&& (!exclude_title)) {
			// parse title text
			buftit = idoc.getTitle();
			parser = new BrownStandardParser(new ByteArrayInputStream((idoc
					.getTitle().toLowerCase() + ". ").getBytes()));
			try {
				list2 = parser.corpus();
			} catch (Exception e) {
				if (verbose) {
					e.printStackTrace();
				}
				list2 = null;
			}
		}

		AnnotationSet annots = idoc.getAnnotations(AnnotationConstants.ANNOTATION_SET_TOKENS);
		// add title tokens
		if (list2 != null) {
			// flag tokens in title as IN_TITLE

			int pos = 0;
			for (Iterator<PoSToken> it = list2.iterator(); it.hasNext();) {
				PoSToken tok = it.next();
				String img = tok.getImage();
				int pos1 = buftit.indexOf(img, pos);
				if (pos1 == -1) {
					if (verbose) {
						_logger
								.info("Tokenizer -- Token image not found in remaining title substring ... should never happen!!! -- "
										+ pos
										+ " -- '"
										+ img
										+ "' --> \n"
										+ buf.substring(pos));
					}
					continue;
				}
				FeatureMap fm = Factory.newFeatureMap();
				fm.put(AnnotationConstants.TOKEN_ANNOT_FEAT_INTITLE_BOOL,
						Boolean.TRUE.toString());
				transfer_flags(fm, tok);
				annots.add(pos1, pos1 + img.length(), AnnotationConstants.TOKEN_ANNOT_TYPE, fm);
				cnt++;
				pos = pos1 + img.length();
			}
		}

		if (list != null) {
			// add body tokens
			int pos = 0;
			for (Iterator<PoSToken> it = list.iterator(); it.hasNext();) {
				PoSToken tok = it.next();
				String img = tok.getImage();
				int pos1 = buf.indexOf(img, pos);
				if (pos1 == -1) {
					if (verbose) {
						_logger
								.info("Tokenizer -- Token image not found in remaining substring ... should never happen!!! -- "
										+ pos
										+ " -- '"
										+ img
										+ "' --> \n"
										+ buf.substring(pos));
					}
					continue;
				}
				FeatureMap fm = Factory.newFeatureMap();
				transfer_flags(fm, tok);
				annots.add((pos1), (pos1 + img.length()),
						AnnotationConstants.TOKEN_ANNOT_TYPE, fm);
				cnt++;
				pos = pos1 + img.length();
			}

		}
		if ((list == null) && (list2 == null)) {
			if (verbose) {
				_logger
						.info("\n\nTokenizer -- WARNING: NO TEXT OR TITLE ... \n\n");
			}
			return idoc;
		}
		if (tok_cnt) {
			idoc.getFeatures().put(DocumentConstants.TOKENIZER_NUM_TOKS_INT,
					Integer.toString(cnt));
		}
		return idoc;
	}
	
	static private void transfer_flags(FeatureMap fm, PoSToken tok) {
		for (Iterator<TokenFlag> iter = tok.getFlags(); iter.hasNext();) {
			TokenFlag flag = iter.next();
			if (flag == TokenFlag.START_OF_LINE) {
				fm.put(AnnotationConstants.TOKEN_ANNOT_FEAT_START_OF_LINE_BOOL,
						Boolean.TRUE.toString());
			} else if (flag == TokenFlag.END_OF_LINE) {
				fm.put(AnnotationConstants.TOKEN_ANNOT_FEAT_END_OF_LINE_BOOL,
						Boolean.TRUE.toString());
			}
		}
	}

}
