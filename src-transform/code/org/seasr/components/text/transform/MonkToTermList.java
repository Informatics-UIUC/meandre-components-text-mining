/*
 * @(#) MonkToTermList.java @VERSION@
 * 
 * Copyright (c) 2009+ Amit Kumar
 * 
 * The software is released under ASL 2.0, Please
 * read License.txt
 *
 */
package org.seasr.components.text.transform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
import org.meandre.core.ExecutableComponent;
import org.seasr.components.text.datatype.termlist.TermListLite;


import edu.northwestern.at.monk.model.*;

@Component(creator = "Amit Kumar", description = "Takes monk document id and returns a document",
		name = "MonkToTermList", tags = "monk")
public class MonkToTermList implements ExecutableComponent {
	
	
	
	@ComponentInput(description = "work or workpart id", name = "tag")
	private static final String DATA_IN_1 ="tag";
	
	@ComponentOutput(description = "returns termlist object", name = "termList")
	private static final String DATA_OUT_1 ="termList";


	private Logger logger = null;
	
	public void initialize(ComponentContextProperties ccp)
			throws ComponentExecutionException, ComponentContextException {
		logger = ccp.getLogger();
		logger.setLevel(Level.INFO);
		logger.info("Initialized "+ this.getClass().getName());
	}

	public void dispose(ComponentContextProperties ccp)
			throws ComponentExecutionException, ComponentContextException {
		logger.info("Disposed "+ this.getClass().getName());
	}

	public void execute(ComponentContext cc)
			throws ComponentExecutionException, ComponentContextException {
		SearchCriterion searchCriterion = null;
		String tag = (String) cc.getDataComponentFromInput(DATA_IN_1);
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
			throw new ComponentExecutionException(tag + " is neither a work or a workpart");
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
		TermListLite tlist= new TermListLite();
		if(isWork){
			Collection<Counter<Work, Spelling>> annots = Counter.find(Work.class,Spelling.class, searchCriterion);
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
		
		}else{
			Collection<Counter<WorkPart, Spelling>> annots = Counter.find(WorkPart.class,Spelling.class, searchCriterion);
			for (Counter<WorkPart, Spelling> tok: annots) {
					Spelling spelling = tok.getFeature();
					int count = (int) tok.getCount(CumKind.CUM);
					String tokimg =  spelling.getTag();
					ArrayList<String> alist=new ArrayList<String>();
					alist.add(tokimg);
					tlist.addTerm(tokimg, count, alist);
					tokens_processed++;
			}
			tlist.setDocID(workPart.getTag());
			tlist.setTitle(workPart.getTitle());
			tlist.setDate(workPart.getWork().getCirculationYear());
		}
		
		if (verbose || debug) {
			logger.info(tokens_processed
					+ " tokens were processed for this document -- "
					+ tlist.getTitle());
			logger.info("# terms in list: " + tlist.getSize() + "\n\n");
		}
		cc.pushDataComponentToOutput(DATA_OUT_1, tlist);
		
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.severe(ex.getMessage());
			logger.severe("ERROR: MonkToTermList.execute()");
			throw new ComponentExecutionException(ex);
		}
		
		 /****/
	}
	
	// =============
	// Inner Class
	// =============

	private class TLCont {
		String _img = null;
		int _cnt = -1;
		Set<String> _orig_imgs = null;
		boolean _inTitle = Boolean.FALSE;
		int _occurence_multiplier = -1;
	}

	public class TokElementComparator implements Comparator<TLCont>,Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * 
		 * put your documentation comment here
		 * 
		 */
		public TokElementComparator() {
		}

		// ======================
		// Interface: Comparator
		// ======================

		public int compare(TLCont o1, TLCont o2) {
			int pos1 = o1._cnt;
			int pos2 = o2._cnt;
			if (pos1 < pos2) {
				return 1;
			} else if (pos1 > pos2) {
				return -1;
			} else {
				return o1._img.compareTo(o2._img);
			}
		}

		/**
		 * 
		 * put your documentation comment here
		 * 
		 * @param o
		 * 
		 * @return
		 * 
		 */
		public boolean equals(Object o) {
			return this.equals(o);
		}
	}

}
