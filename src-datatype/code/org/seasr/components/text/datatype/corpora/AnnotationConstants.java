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

package org.seasr.components.text.datatype.corpora;

/**
 * 
 * @author D. Searsmith
 * @version 1.0
 */

public interface AnnotationConstants {

	// === ANNOTATION TYPES =================================================
	
	public static final String TOKEN_ANNOT_TYPE = "$@token";

	public static final String PARSE_ANNOT_TYPE = "$@parse";

	public static final String SENTENCE_ANNOT_TYPE = "$@sentence";

	public static final String NGRAM_ANNOT_TYPE = "$@ngram";
	
	// === TOKEN ATTRIBUTES =================================================
	
	/**
	 * boolean
	 */
	public static final String TOKEN_ANNOT_FEAT_INTITLE_BOOL = "$@in_title";

	public static final String TOKEN_ANNOT_FEAT_POS = "$@pos_tag";

	public static final String TOKEN_ANNOT_FEAT_NORM_IMAGE = "$@normalized_form";

	/**
	 * boolean
	 */
	public static final String TOKEN_ANNOT_FEAT_PRETAGGED_BOOL = "$@pretagged";

	/**
	 * boolean
	 */
	public static final String TOKEN_ANNOT_FEAT_LEXTAGGED_BOOL = "$@lextagged";

	/*
	 * boolean
	 */
	public static final String TOKEN_ANNOT_FEAT_CONTAGGED_BOOL = "$@contagged";

	/**
	 * boolean
	 */
	public static final String TOKEN_ANNOT_FEAT_START_OF_LINE_BOOL = "$@start_of_line";

	/**
	 * boolean
	 */
	public static final String TOKEN_ANNOT_FEAT_END_OF_LINE_BOOL = "$@end_of_line";

	public static final String TOKEN_ANNOT_FEAT_PRETAGGED_DESC = "$@pretagged_desc";

	public static final String TOKEN_ANNOT_FEAT_LEXTAGGED_DESC = "$@lextagged_desc";

	public static final String TOKEN_ANNOT_FEAT_CONTAGGED_DESC = "$@contagged_desc";

	public static final String TOKEN_ANNOT_FEAT_OCCURENCE_MULTIPLIER = "$@token_weight_mult";

	public static final String TOKEN_ANNOT_FEAT_ANTECEDENT = "$@antecedent_offset";

	// === NGRAMS ATTRIBUTES ========================================================
	
	public static final String NGRAM_ANNOT_FEAT_NORM_IMAGE = "$@ngram_norm_img";
	
	/**
	 * An integer value.
	 */
	public static final String NGRAM_ANNOT_FEAT_ARITY_INT = "$@ngram_arity";

	/**
	 * List of Annotation Objects
	 */
	public static final String NGRAM_ANNOT_FEAT_TOKEN_LIST = "$@ngram_tok_lst";
	
	// === SENTENCES ATTRIBUTES =====================================================

	/**
	 * Set of Strings (representing ints)
	 */
	public static final String SENTENCE_ANNOT_PARSES_SET = "$@sent_parses";

	/**
	 * Set of String
	 */
	public static final String SENTENCE_PENNTREEBANK_FMT_ANNOT_PARSES_SET = "$@penntreebank_fmt_parses";
	
	// === PARSE ATTRIBUTES =========================================================
	
	public static final String PARSE_ANNOT_CONSTITUENT_TYPE = "$@parse_cons_type";
	
	/**
	 * Integer
	 * 
	 * Annotation ID of parent.
	 */
	public static final String PARSE_ANNOT_PARENT_INT = "$@parse_parent";
	
	/**
	 * Set of Strings (representing ints)
	 * 
	 * Annotations ID's of children.
	 */
	public static final String PARSE_ANNOT_CHILDREN_SET = "$@parse_children";

	/**
	  * Double
	  * Probability = for statistical parsers this is the prob associated with this type.
	  */
	public static final String PARSE_ANNOT_PROB_DOUBLE = "$@parse_prob";

	/**
	 * Integer
	 * 
	 * Subject of Analysis - usually a sentence annotation ID
	 */
	public static final String PARSE_ANNOT_SOFA_INT = "$@parse_sofa";
	
	public static final String PARSE_ANNOT_PARSER = "$@parse_parser";
	
	//=== ANNOTATION SETS ===========================================================
	
	public static final String ANNOTATION_SET_SENTENCES = "$@sentences";

	public static final String ANNOTATION_SET_TOKENS = "$@tokens";

	public static final String ANNOTATION_SET_ENTITIES = "$@entities";

	public static final String ANNOTATION_SET_PARSES = "$@parses";
	
	// === PARSER CONSTANTS =========================================================

	public static final String ANNOTATION_PARSER_IMPL_OpenNLP_TreebankParser = "OpenNLP_TreebankParser";
	
	public static final String ANNOTATION_PARSER_IMPL_Link_Grammar = "Link_Grammar";
	
	public static final String ANNOTATION_PARSER_NO_PARSE = "NO PARSE";

}
