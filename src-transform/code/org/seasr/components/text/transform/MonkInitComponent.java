/*
 * @(#) MonkInitComponent.java @VERSION@
 *
 * Copyright (c) 2007+ Amit Kumar
 *
 * The software is released under ASL 2.0, Please
 * read License.txt
 *
 */
package org.seasr.components.text.transform;

import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentExecutionException;
import org.meandre.core.ExecutableComponent;
import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.core.ComponentContextProperties;

import edu.northwestern.at.monk.model.ModelException;
import edu.northwestern.at.monk.model.ModelInit;

@Component(creator="Amit Kumar", description="Connect to the monk database and initialize static classes",
tags="monk database initialize", name="MonkInitComponent")
public class MonkInitComponent implements ExecutableComponent {


	@ComponentProperty(defaultValue="com.mysql.jdbc.Driver", description="jdbc driver", name="driver")
	final static String DATA_PROPERTY_1 =  "driver";
	@ComponentProperty(defaultValue="jdbc:mysql://monk.lis.uiuc.edu/monkdbv1?characterEncoding=UTF-8", description="jdbc url", name="jdbcURL")
	final static String DATA_PROPERTY_2 =  "jdbcURL";
	@ComponentProperty(defaultValue="user", description="database user", name="user")
	final static String DATA_PROPERTY_3 =  "user";
	@ComponentProperty(defaultValue="password", description="jdbc password", name="password")
	final static String DATA_PROPERTY_4 =  "password";
	@ComponentProperty(defaultValue="100", description="Concurrent connection", name="concurrentConnection")
	final static String DATA_PROPERTY_5 =  "concurrentConnection";

	@ComponentOutput(description="Monk Database status connection", name="status")
	final static String DATA_OUTPUT_1 = "status";

	public void initialize(ComponentContextProperties ccp) {
		// TODO Auto-generated method stub
	}


	/**
	 *
	 */
	public void execute(ComponentContext cc)
		throws ComponentExecutionException, ComponentContextException {
		System.out.println("Execute: MonkInitComponent...");
		String driver = (String)cc.getProperty(DATA_PROPERTY_1);
		String jdbcURL = (String)cc.getProperty(DATA_PROPERTY_2);
		String user = (String)cc.getProperty(DATA_PROPERTY_3);
		String password = (String)cc.getProperty(DATA_PROPERTY_4);
		String concurrentConnection = (String)cc.getProperty(DATA_PROPERTY_5);
		int connection = 100;
		

		boolean success = false;

		try{
		connection = Integer.parseInt(concurrentConnection);
		}catch (Exception e){
		connection = 100;
		}
		try {
			ModelInit.init(driver,jdbcURL,user,password,connection);
			success = true;
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cc.pushDataComponentToOutput(DATA_OUTPUT_1, success);
		System.out.println("Done executing: MonkInitComponent...");
	}

	public void dispose(ComponentContextProperties ccp) {
		// TODO Auto-generated method stub

	}

}
