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

// ==============
// Java Imports
// ==============

import java.util.*;
import java.util.logging.Logger;

// ===============
// Other Imports
// ===============

import org.meandre.core.*;
import org.meandre.annotations.*;

// import org.meandre.tools.components.*;
// import org.meandre.tools.components.FlowBuilderAPI.WorkingFlow;

import org.seasr.components.text.datatype.termlist.TermList;
import org.seasr.components.text.datatype.termlist.TermListLite;
import org.seasr.components.text.datatype.termmap.TermMap;
import org.meandre.components.abstracts.AbstractExecutableComponent;
import org.meandre.components.datatype.table.*;

/**
 *
 * @author D. Searsmith
 *
 * TODO: Testing, Unit Testing
 *
 */

@Component(creator = "Duane Searsmith",

description = "<p>Overview: <br>"
		+ "This component takes lists of terms and their frequency counts and transforms "
		+ "them into a table data structure that is used for learning.  Each row of the "
		+ "table corresponds to a document and each unique term corresponds to a column. "
		+ "The properties of the Document object are also added as separate columns."
		+ "</p>"
		+ "<p>"
		+ "A special implementation of table called a SparseTable is used for text data. "
		+ "For any given row (document) in the table only a small subset of the total "
		+ "number of features will typically have a non-zero frequency.  Such a data "
		+ "record is commonly referred to as being sparse.  To conserve on memory usage "
		+ "only those columns with actual data are stored in memory.  Because of the very "
		+ "large number of features (tens of thousands) typically encountered in text "
		+ "learning, storing only non-zero values greatly reduces the amount of memory "
		+ "needed for the table."
		+ "</p>"
		+ "<p>Properties: <br>"
		+ "The property 'verbose' if set to true, the component will generate additional output "
		+ "to the console. The default is false.<br>"
		+ "The property 'allow_empty_term_lists' if set to true, the component will process "
		+ "term lists with no terms which only results in an extra row being added to the "
		+ "table to represent that document which may be important for a given flow. "
		+ "The default is set to false.<br>"
		+ "The property 'save_term_map' if set to true, the component will save the term map "
		+ "which is a struture that for each table built, holds additional data about "
		+ "each term such as its orginal form(s) if normalized, etc... The default value for "
		+ "this property is true.  Making it false will save memory but may prevent other "
		+ "components in the floe from getting key information.<br>"
		+ "The property 'add_term_count_column' if set to true, will add an additional column "
		+ "to the table where for each document processed, the number of terms total for that "
		+ "document is recorded. The default value for this property is false."
		+ "<p>Scalability: <br>"
		+ "This component creates a table that conatins at least as many numerical entries as "
		+ "term list entries."
		+ "</p>"
		+ "<p>Trigger Criteria: <br>"
		+ "Any. <br>"
		+ "The count of term lists to expect is read only once.  Term lists are read and "
		+ "processed until the number told to expect is reached.  At that time the "
		+ "SparseTable is completed and pushed to output." + "</p>",

name = "TermListsToTable", tags = "text termlist transform table",
firingPolicy = Component.FiringPolicy.any,
baseURL="meandre://seasr.org/components/")
public class TermListsToTable extends AbstractExecutableComponent {

	// ==============
	// Data Members
	// ==============

	private int m_docsProcessed = 0;
	private int _cnter = 0;
	private long m_start = 0;
	private List<TermList> _docs = null;

	/**
	 * Label for column for term counts per term list processed.
	 */
	public static final String NUM_TERMS = "Number Of Terms";

	private TableFactory _fact = null;

	// make a new GlobalTermMap (class scope)
	HashMap<String, Integer> m_gtm = null;

	// SparseExampleTable
	ExampleTable _termTable = null;

	int m_count = 0;

	private int m_numRecs = Integer.MAX_VALUE;

	private TermMap _tmap = null;
	private ArrayList<Map<String, String>> _propList = null;

	public static HashMap<ExampleTable, TermMap> _termMaps = null;

	private static Logger _logger = Logger.getLogger("TermListsToTable");

	// props

	@ComponentProperty(description = "Verbose output? A boolean value (true or false).", name = "verbose", defaultValue = "false")
	final static String DATA_PROPERTY_VERBOSE = "verbose";

	@ComponentProperty(description = "Allow empty term lists? A boolean value (true or false).", name = "allow_empty_term_lists", defaultValue = "false")
	final static String DATA_PROPERTY_ALLOW_EMPTY_TERMLISTS = "allow_empty_term_lists";

	@ComponentProperty(description = "Save a map with additional data for each term encountered? A boolean value (true or false).", name = "save_term_map", defaultValue = "true")
	final static String DATA_PROPERTY_SAVE_TERM_MAP = "save_term_map";

	@ComponentProperty(description = "Add extra column for term counts per document? A boolean value (true or false).", name = "add_term_count_column", defaultValue = "false")
	final static String DATA_PROPERTY_ADD_TERM_COUNT_COLUMN = "add_term_count_column";

	// io

	@ComponentInput(description = "Term list object.", name = "termlist")
	public final static String DATA_INPUT_TERMLIST = "termlist";

	@ComponentInput(description = "Number of TermList objects expected.", name = "num_termlist")
	public final static String DATA_INPUT_NUMBER_OF_TERMLIST = "num_termlist";

	@ComponentInput(description = "TableFactory object.", name = "table_factory")
	public final static String DATA_INPUT_TABLE_FACTORY = "table_factory";

	@ComponentOutput(description = "Table object.", name = "document")
	public final static String DATA_OUPUT_TABLE = "document";

	// ================
	// Constructor(s)
	// ================
	public TermListsToTable() {
	}

	// ================
	// Public Methods
	// ================

	public boolean getVerbose(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_VERBOSE);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getProcessEmptyTermLists(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_ALLOW_EMPTY_TERMLISTS);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getSaveTermMap(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_SAVE_TERM_MAP);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public boolean getAddColumnForTermCounts(ComponentContextProperties ccp) {
		String s = ccp.getProperty(DATA_PROPERTY_ADD_TERM_COUNT_COLUMN);
		return Boolean.parseBoolean(s.toLowerCase());
	}

	public void initializeCallBack(ComponentContextProperties ccp)
    throws Exception {
		_docs = new ArrayList<TermList>();
		_termMaps = new HashMap<ExampleTable, TermMap>();
		_fact = null;

		_tmap = new TermMap();
		m_gtm = new HashMap<String, Integer>();
		_propList = new ArrayList<Map<String, String>>();

		m_docsProcessed = 0;
		_cnter = 0;
		m_numRecs = Integer.MAX_VALUE;
		m_start = System.currentTimeMillis();
	}

	public void disposeCallBack(ComponentContextProperties ccp)
    throws Exception {
		_fact = null;
		_propList = null;
		long end = System.currentTimeMillis();
		componentConsoleHandler.whenLogLevelOutput("info","\nEND EXEC -- TermListsToTable -- Docs Processed: "
				+ m_docsProcessed + " in " + (end - m_start) / 1000
				+ " seconds\n");
		componentConsoleHandler.whenLogLevelOutput("info","\nEND EXEC -- TermListsToTable -- Docs Output: "
				+ m_count + " in " + (end - m_start) / 1000 + " seconds\n");

		m_docsProcessed = 0;
		_cnter = 0;
		m_numRecs = Integer.MAX_VALUE;
		_termMaps = null;
		if (_docs != null) {
			_docs.clear();
			_docs = null;
		}
	}

	public void executeCallBack(ComponentContext ctx)
    throws Exception {
		try {

			if (ctx.isInputAvailable(DATA_INPUT_TABLE_FACTORY)) {
				_fact = (TableFactory) ctx
						.getDataComponentFromInput(DATA_INPUT_TABLE_FACTORY);
				_termTable = _fact.createTable().toExampleTable();
			}

			if (ctx.isInputAvailable(DATA_INPUT_TERMLIST)) {
				_docs.add((TermList) ctx
						.getDataComponentFromInput(DATA_INPUT_TERMLIST));
			}

			if (ctx.isInputAvailable(DATA_INPUT_NUMBER_OF_TERMLIST)) {
				m_numRecs = ((Integer) ctx
						.getDataComponentFromInput(DATA_INPUT_NUMBER_OF_TERMLIST))
						.intValue();
				if (getVerbose(ctx)) {
					componentConsoleHandler.whenLogLevelOutput("info","TermListsToTable: Number of records was told to expect: "
									+ m_numRecs);
				}
			}

			if ((_termTable != null) && (!_docs.isEmpty())) {
				for (int i = 0, n = _docs.size(); i < n; i++) {

					TermListLite tl = (TermListLite) _docs.get(i);
					m_docsProcessed++;
					_cnter++;
					if ((tl.getSize() == 0) && (!getProcessEmptyTermLists(ctx))) {
						componentConsoleHandler.whenLogLevelOutput("info","Termlist had no terms -- discarding: "
								+ tl.getDocID() + " " + tl.getTitle());
					} else {
						int row = _termTable.getNumRows();
						_termTable.addRows(1);
						for (Iterator<String> it = tl.getTerms(); it.hasNext();) {
							String term = (String) it.next();

							Integer colobj = (Integer) m_gtm.get(term);
							int col = 0;
							boolean flag = false;
							if (colobj == null) {
								col = _termTable.getNumColumns();
								m_gtm.put(term, new Integer(col));
								flag = true;
							} else {
								col = colobj.intValue();
							}
							int freq = tl.getTermFreqByImage(term);

							// set the value in the table
							_termTable.setDouble((double) freq, row, col);
							if (flag) {
								_termTable.setColumnLabel(term, col);
							}
							ArrayList<String> oforms = tl
									.getTermOrigFormsByImage(term);

							// added by Bei Yu to avoid the null pointer problem
							// caused by empty oforms
							if (oforms == null) {
								oforms = new ArrayList<String>();
							}
							_tmap.addTermData(_termTable.getColumnLabel(col),
									term, oforms);
						}
						_propList.add(tl.getProperties());

					}
					tl.free();
				}
				_docs.clear();
			}

			if (_cnter >= m_numRecs) {

				m_count = _termTable.getNumRows();
				componentConsoleHandler.whenLogLevelOutput("info", m_count + " rows added.");

				HashMap<String, Integer> colprops = new HashMap<String, Integer>();
				// add column for each document property
				int row = _termTable.getNumRows();
				int col = _termTable.getNumColumns();
				for (int i = 0; i < row; i++) {
					Map<String, String> m = _propList.get(i);
					for (Iterator<String> it = m.keySet().iterator(); it
							.hasNext();) {
						String key = (String) it.next();
						String ob = m.get(key);
						Integer icol = colprops.get(key);
						boolean flag = false;
						if (icol == null) {
							icol = new Integer(_termTable.getNumColumns());
							colprops.put(key, icol);
							flag = true;
						}
						_termTable.setString(ob, i, icol.intValue());
						if (flag) {
							_termTable.setColumnLabel(key + "_DOCPROP", icol
									.intValue());
						}
					}
				}
				// set remaining features as input features
				int[] feats = new int[col];
				for (int i = 0, n = feats.length; i < n; i++) {
					feats[i] = i;
				}
				_termTable.setInputFeatures(feats);

				if (this.getAddColumnForTermCounts(ctx)) {
					Column numTermsColumn = _fact
							.createColumn(ColumnTypes.INTEGER);
					numTermsColumn.setLabel(NUM_TERMS);
					numTermsColumn.addRows(_termTable.getNumRows());
					int numTermIdx = _termTable.getNumColumns();
					_termTable.addColumn(numTermsColumn);

					for (int i = 0; i < _termTable.getNumRows(); i++) {
						int numTerms = 0;
						for (int j = 0; j < feats.length; j++) {
							if (_termTable.getDouble(i, feats[j]) != 0)
								numTerms++;
						}
						_termTable.setInt(numTerms, i, numTermIdx);
					}
				}// end of if adding num terms column

				ctx.pushDataComponentToOutput(DATA_OUPUT_TABLE, _termTable);
				if (this.getSaveTermMap(ctx)) {
					_termMaps.put(_termTable, _tmap);
				}
				m_numRecs = Integer.MAX_VALUE;
				_termTable = _fact.createTable().toExampleTable();
				componentConsoleHandler.whenLogLevelOutput("info","GlobalTermMap contains "
						+ m_gtm.size() + " terms.");
				_tmap = new TermMap();
				m_gtm = new HashMap<String, Integer>();
				_propList = new ArrayList<Map<String, String>>();
				_cnter = 0;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.severe(ex.getMessage());
			_logger.severe("ERROR: TermListsToTable.execute()");
			throw new ComponentExecutionException(ex);
		}
	}
}
