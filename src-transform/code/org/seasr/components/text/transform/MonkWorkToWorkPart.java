/*
 * @(#) MonkWorkToWorkPart.java @VERSION@
 * 
 * Copyright (c) 2009+ Amit Kumar
 * 
 * The software is released under ASL 2.0, Please
 * read License.txt
 *
 */
package org.seasr.components.text.transform;

import java.util.Collection;
import java.util.logging.Logger;

import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
import org.meandre.core.ExecutableComponent;


import edu.northwestern.at.monk.model.CumKind;
import edu.northwestern.at.monk.model.Work;
import edu.northwestern.at.monk.model.WorkPart;

@Component(creator = "Amit Kumar", description = "For a Work Returns the Workparts one at a time", 
		name = "MonkWorkToWorkPart", tags = "monk")
public class MonkWorkToWorkPart implements ExecutableComponent {
	
	@ComponentInput(description = "Work Tag", name = "workTag")
	private static final String DATA_IN_1 = "workTag";
	
	@ComponentOutput(description = "WorkPart Tag", name = "workPartTag")
	private static final String DATA_OUT_1 = "workPartTag";
	
	@ComponentOutput(description = "Number of workparts", name = "numWorkParts")
	private static final String DATA_OUT_2 = "numWorkParts";
	
	
	@ComponentProperty(defaultValue = "false", description = "display work part tags", name = "verbose")
	private static final String DATA_PROP_1 = "verbose";

	private Logger logger;
	private boolean verbose;
	
	public void initialize(ComponentContextProperties ccp)
			throws ComponentExecutionException, ComponentContextException {
		logger = ccp.getLogger();
		try{
		verbose = Boolean.parseBoolean(ccp.getProperty(DATA_PROP_1));
		}catch(Exception ex){
			logger.warning("Invalid verbose property -resetting to TRUE: " +ccp.getProperty(DATA_PROP_1) );
			verbose = Boolean.TRUE;
		}
	}
	
	

	public void execute(ComponentContext cc)
			throws ComponentExecutionException, ComponentContextException {
		String workTag = (String)cc.getDataComponentFromInput(DATA_IN_1);
		boolean noDecendentsFound = Boolean.TRUE;
		Work work =Work.get(workTag);
		if(work==null){
			throw new ComponentExecutionException("No such work: " + workTag);
		}
		Collection<WorkPart> wpList=work.getDescendants();
		int numWorkPartsPushed=0;
		for(WorkPart wp: wpList){
			if(!wp.getTag().equalsIgnoreCase(workTag)){
				
				if(wp.getNumWords(CumKind.NON_CUM)>0){
				numWorkPartsPushed++;
				noDecendentsFound = Boolean.FALSE;
				if(verbose){
					logger.info("sending: " + wp.getTag());
				}
				cc.pushDataComponentToOutput(DATA_OUT_1, wp.getTag());
				}
			}
		}
		cc.pushDataComponentToOutput(DATA_OUT_2,  numWorkPartsPushed);
		
		if(noDecendentsFound ){
			throw new ComponentExecutionException("Work " + workTag + " has no descendents" );
		}
		
		

	}


	public void dispose(ComponentContextProperties ccp)
			throws ComponentExecutionException, ComponentContextException {
		logger.info("Disposing: " + this.getClass().getName());
	}


}
