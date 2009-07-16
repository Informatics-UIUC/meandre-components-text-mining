/*
 * @(#) SerializeDocument.java @VERSION@
 * 
 * Copyright (c) 2009+ Amit Kumar
 * 
 * The software is released under ASL 2.0, Please
 * read License.txt
 *
 */
package org.seasr.components.text.transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
import org.meandre.core.ExecutableComponent;
import org.seasr.components.text.datatype.corpora.Document;


@Component(creator = "Amit Kumar", description = "Serialize the document to file system",
		name = "SerializeDocument", tags = "serialize document")
public class SerializeDocument implements ExecutableComponent {
	
	@ComponentProperty(defaultValue = "/tmp", description = "temporary folder where the serialized files will be stored",
			name = "tempFolder")
	private static final String DATA_PROP_1 ="tempFolder";
	
	
	@ComponentInput(description = "Document object for serialization", name = "documentIn")
	private static final String DATA_IN_1 = "documentIn";
	
	@ComponentOutput(description = "Document object", name = "documentOut")
	private static final String DATA_OUT_1 ="documentOut";
	
	@ComponentOutput(description = "file name", name = "fileName")
	private static final String DATA_OUT_2 ="fileName";
	
	

	static int count=0;
	
	public void initialize(ComponentContextProperties ccp)
			throws ComponentExecutionException, ComponentContextException {
		count=0;

	}
	
	public void execute(ComponentContext cc)
			throws ComponentExecutionException, ComponentContextException {
		Document document = (Document)cc.getDataComponentFromInput(DATA_IN_1);
		String tempFolder = cc.getProperty(DATA_PROP_1);
		count++;
		
		String filePath = tempFolder+File.separator+ count+".ser";
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(filePath);
			ObjectOutputStream outStream =  new ObjectOutputStream(fos);
		    outStream.writeObject(document);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		
		
		cc.pushDataComponentToOutput(DATA_OUT_1, document);
		cc.pushDataComponentToOutput(DATA_OUT_2, filePath);
		
   }


	public void dispose(ComponentContextProperties ccp)
			throws ComponentExecutionException, ComponentContextException {

	}

	

}
