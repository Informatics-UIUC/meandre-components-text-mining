/*
 * @(#) TestMonkToTermList.java @VERSION@
 * 
 * Copyright (c) 2009+ Amit Kumar
 * 
 * The software is released under ASL 2.0, Please
 * read License.txt
 *
 */
package org.seasr.components.text.transform;


import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.junit.*;
import org.meandre.core.ComponentExecutionException;
import org.seasr.components.text.datatype.termlist.TermListLite;

import edu.northwestern.at.monk.model.Counter;
import edu.northwestern.at.monk.model.CumKind;
import edu.northwestern.at.monk.model.ModelException;
import edu.northwestern.at.monk.model.ModelInit;
import edu.northwestern.at.monk.model.SearchCriterion;
import edu.northwestern.at.monk.model.Spelling;
import edu.northwestern.at.monk.model.Work;
import edu.northwestern.at.monk.model.WorkCriterion;
import edu.northwestern.at.monk.model.WorkPart;
import edu.northwestern.at.monk.model.WorkPartCriterion;

public class TestMonkToTermList {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		System.out.println("Initializing Monk Database...");
		String driver="com.mysql.jdbc.Driver";
		String jdbcURL = "jdbc:mysql://gautam.lis.illinois.edu/monkPub1019?characterEncoding=UTF-8&amp;autoReconnect=true&amp;autoReconnectForPools=true";
		String user="monk-fedora";
		String password="treem0nkey";
		int connection = 100;
		try {
			ModelInit.init(driver,jdbcURL,user,password,connection);
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testTermList() throws Exception{
		Logger logger = Logger.getAnonymousLogger();
		SearchCriterion searchCriterion = null;
		String tag = "sha-lov";
		boolean isWork = true;
		boolean isWorkPart = false;
		Work work = null;
		WorkPart workPart = null;
		work=Work.get(tag);
		if(work==null){
			isWork = false;
			workPart = WorkPart.get(tag);
			if(workPart==null){
				isWorkPart = false;
			}else{
				isWorkPart = true;
				try {
					searchCriterion = new WorkPartCriterion(tag);
				} catch (ModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			try {
				searchCriterion = new WorkCriterion(tag);
			} catch (ModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!isWork && !isWorkPart){
			throw new Exception(tag + ": is neither a work or a workpart");
		}
		
		/*****/
		
		logger.fine("execute() called");
		//props ==============
		boolean verbose = true;
		int toklim = -1;
		boolean debug = true;
		int titwt = 0;
		String feature = "spelling";
		//====================
		int tokens_processed = 0;
		try {
			Collection<Counter<Work, Spelling>> annots = Counter.find(Work.class,Spelling.class, searchCriterion);
			TermListLite tlist = new TermListLite();
			for (Counter<Work, Spelling> tok: annots) {
			
					Spelling spelling = tok.getFeature();
					int count = (int) tok.getCount(CumKind.CUM);
					String tokimg =  spelling.getTag();
					
					ArrayList<String> alist=new ArrayList<String>();
					alist.add(tokimg);
					tlist.addTerm(tokimg, count, alist);
					tokens_processed++;
			}


			tlist.setDocID(work.getTag());
			tlist.setTitle(work.getTitle());
			tlist.setDate(work.getCirculationYear());
		
			
			if (verbose || debug) {
				logger.info(tokens_processed
						+ " tokens were processed for this document -- "
						+ tlist.getTitle());
				logger.info("# terms in list: " + tlist.getSize() + "\n\n");
			}
			//cc.pushDataComponentToOutput(DATA_OUT_1, tlist);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.severe(ex.getMessage());
			logger.severe("ERROR: MonkToTermList.execute()");
			throw new ComponentExecutionException(ex);
		}
	}
	
	
	

}
