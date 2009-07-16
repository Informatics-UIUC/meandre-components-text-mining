/*
 * @(#) PropertyToInput.java @VERSION@
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

@Component(name="PropertyToInput",creator="Amit Kumar", description="converts property into an input",
		tags="io property string")
public class PropertyToInput implements ExecutableComponent {


	@ComponentProperty(description="property to print as string", name="propertyValue", defaultValue = "test" )
	final static String DATA_PROPERTY_1 =  "propertyValue";


	//OUTPUT
	@ComponentOutput(description="string value from property", name="stringValue")
	final static String DATA_OUTPUT_1 = "stringValue";


	public void initialize(ComponentContextProperties ccp) {
		// TODO Auto-generated method stub

	}

	public void execute(ComponentContext cc)
			throws ComponentExecutionException, ComponentContextException {
		String propertyValue = cc.getProperty(DATA_PROPERTY_1);
		cc.pushDataComponentToOutput(DATA_OUTPUT_1,  propertyValue);
	}

	public void dispose(ComponentContextProperties ccp) {
		// TODO Auto-generated method stub

	}

}
