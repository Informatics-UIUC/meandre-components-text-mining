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

package org.seasr.components.text.monk.io;

// ==============
// Java Imports
// ==============

import java.util.*;
import java.util.logging.Logger;

// ===============
// Other Imports
// ===============
// import org.meandre.tools.components.*;
// import org.meandre.tools.components.FlowBuilderAPI.WorkingFlow;

import org.meandre.core.*;
import org.meandre.annotations.*;

import org.meandre.components.datatype.table.*;
import org.meandre.components.datatype.table.sparse.*;
import edu.northwestern.at.monk.model.*;

/**
 * Extracts a sparse matrix of lemmas from works stored in the MONK DB.
 * 
 * TODO: Eventually add custom prop editors that will query the MONK DB
 * for possible property settings and provide list boxes for user selection.
 * Then remove these hard values from the component description.
 * 
 * @author dsears TODO: Unit Testing
 */

@Component(creator = "D. Searsmith",

description = "<p>Overview: <br>"
		+ "This class is a query interface to the MONK DB.  It relies on the "
		+ "repository being used having the MONK DB plugin activated.  Making "
		+ "sure this is activated involves adding an entry to the plugins XML "
		+ "config file for the repository.</p>"
		+ "<p>Properties: <br>"
		+ "Container: Corpus, Work, WorkPart, Author.  <br>"
		+ "Feature: Lemma.  <br>"
		+ "Major Word Classes: adjective, adv/conj/pcl/prep, adverb, conjunction, "
		+ "determiner, foreign word, interjection, negative, noun, numeral, particle, "
		+ "preposition, pronoun, punctuation, symbol, undetermined, verb, wh-word.  <br>"
		+ "Corpus: Chaucer (cha), NCF (ncf), Shakespeare (sha), Spenser (spe), Stein (stein).", 
		tags = "monk data access io table text", 
		name = "MonkDBAccessor",
		dependency = { "monk.jar"},
        baseURL="meandre://seasr.org/components/")
public class MonkDBAccessor implements ExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private static Logger _logger = Logger.getLogger("MonkDBAccessor");

	private static String _MONK_Pkg = "edu.northwestern.at.monk.model.";

	// props
	
	@ComponentProperty(description = "Major Word Class tags (comma delimited).", name = "major_word_classes", defaultValue = "noun")
	public final static String DATA_PROPERTY_MAJOR_WORD_CLASSES = "major_word_classes";

	@ComponentProperty(description = "Container for counts.", name = "count_container", defaultValue = "WorkPart")
	public final static String DATA_PROPERTY_COUNT_CONTAINER = "count_container";

	@ComponentProperty(description = "Feature for counts.", name = "count_feature", defaultValue = "Lemma")
	public final static String DATA_PROPERTY_COUNT_FEATURE = "count_feature";

	@ComponentProperty(description = "Corpus tags (comma delimited).", name = "corpus_tags", defaultValue = "cha")
	public final static String DATA_PROPERTY_CORPUS_TAGS = "corpus_tags";

	@ComponentProperty(description = "Work tags (comma delimited).", name = "work_tags", defaultValue = "")
	public final static String DATA_PROPERTY_WORK_TAGS = "work_tags";

	@ComponentProperty(description = "Author tags (semi-colon delimited).", name = "author_tags", defaultValue = "")
	public final static String DATA_PROPERTY_AUTHOR_TAGS = "author_tags";

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	public final static String DATA_PROPERTY_VERBOSE = "verbose";

	// io
	
	@ComponentOutput(description = "Sparse Table", name = "sparse_table")
	public final static String DATA_OUTPUT_SPARSE_TABLE = "sparse_table";


	// ================
	// Public Methods
	// ================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public String getMajorWordClassTags(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_MAJOR_WORD_CLASSES);
		return s;
	}

	public String getCountContainer(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_COUNT_CONTAINER);
		return s;
	}

	public String getCountFeature(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_COUNT_FEATURE);
		return s;
	}

	public String getCorpusTags(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_CORPUS_TAGS);
		return s;
	}

	public String getWorkTags(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_WORK_TAGS);
		return s;
	}

	public String getAuthorTags(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_AUTHOR_TAGS);
		return s;
	}

	// ===========================
	// Interface Implementation: ExecutableComponent
	// ===========================

	public void initialize(ComponentContextProperties ccp) {
		_logger.fine("initialize() called");
	}

	@SuppressWarnings("unchecked")
	public void execute(ComponentContext ctx)
			throws ComponentExecutionException, ComponentContextException {
		_logger.fine("Execute: MonkDBAccessor ...");

		ExampleTable spTab = new SparseTableFactory().createTable()
				.toExampleTable();

		try {
			Map<Lemma, Integer> cols = new HashMap<Lemma, Integer>();
			Map<WorkPart, Integer> rows = new HashMap<WorkPart, Integer>();
			Collection<SearchCriterion> filters = new HashSet<SearchCriterion>();

			Class container = Class.forName(_MONK_Pkg
					+ getCountContainer(ctx).trim());
			Class feature = Class.forName(_MONK_Pkg
					+ getCountFeature(ctx).trim());

			String corpusTags = getCorpusTags(ctx);
			if (corpusTags.trim().length() > 0) {
				StringTokenizer toker = new StringTokenizer(corpusTags.trim(),
						",");
				Set<String> vals = new HashSet<String>();
				while (toker.hasMoreTokens()) {
					vals.add(toker.nextToken().trim());
				}
				filters.add(new CorpusCriterion(vals));
			}

			String wordTags = getMajorWordClassTags(ctx);
			if (wordTags.trim().length() > 0) {
				StringTokenizer toker = new StringTokenizer(wordTags.trim(),
						",");
				Set<String> vals = new HashSet<String>();
				while (toker.hasMoreTokens()) {
					vals.add(toker.nextToken().trim());
				}
				filters.add(new MajorWordClassCriterion(vals));
			}

			String workTags = getWorkTags(ctx);
			if (workTags.trim().length() > 0) {
				StringTokenizer toker = new StringTokenizer(workTags.trim(),
						",");
				Set<String> vals = new HashSet<String>();
				while (toker.hasMoreTokens()) {
					vals.add(toker.nextToken().trim());
				}
				filters.add(new WorkCriterion(vals));
			}

			String authorTags = getAuthorTags(ctx);
			if (authorTags.trim().length() > 0) {
				StringTokenizer toker = new StringTokenizer(authorTags.trim(),
						";");
				Set<String> vals = new HashSet<String>();
				while (toker.hasMoreTokens()) {
					vals.add(toker.nextToken().trim());
				}
				filters.add(new AuthorCriterion(vals));
			}

			Collection<Counter<WorkPart, Lemma>> counters = Counter.find(
					container, feature, filters);
			for (Counter<WorkPart, Lemma> counter : counters) {
				WorkPart workpart = counter.getContainer();
				Lemma lemma = counter.getFeature();

				boolean flag = false;
				int col = -1;
				Integer colI = cols.get(lemma);
				if (colI == null) {
					col = spTab.getNumColumns();
					cols.put(lemma, new Integer(col));
					flag = true;
				} else {
					col = colI.intValue();
				}
				int row = -1;
				Integer rowI = rows.get(workpart);
				if (rowI == null) {
					row = spTab.getNumRows();
					rows.put(workpart, new Integer(row));
				} else {
					row = rowI.intValue();
				}
				double freq = counter.getFreq(CumKind.NON_CUM);
				spTab.setDouble(freq, row, col);
				if (flag) {
					spTab.setColumnLabel(lemma.getTag(), col);
				}
			}
			if (getVerbose(ctx)) {
				_logger.info("Rows: " + rows.size());
				_logger.info("Cols: " + cols.size());
			}
			int[] feats = new int[cols.size()];
			for (int i = 0, n = feats.length; i < n; i++) {
				feats[i] = i;
			}
			spTab.setInputFeatures(feats);
			ctx.pushDataComponentToOutput(DATA_OUTPUT_SPARSE_TABLE, spTab);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComponentExecutionException(e);
		}
	}

	public void dispose(ComponentContextProperties ccp) {
		_logger.fine("dispose: MonkDBAccessor ...");
	}

}
