package org.seasr.meandre.components.dendrogram.client;

//===============
// Other Imports
//===============

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;

/**
 * <p>Title: Uncaught Exception Handler</p>
 *
 * <p>Description: Catches and displays any uncaught error messages generated
 * by the workbench application.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA, Automated Learning Group</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */
public class MyUncaughtExceptionHandler implements
        UncaughtExceptionHandler {

    public void onUncaughtException(Throwable ex) {
        Window.alert("Uncaught Exception\n" +
                     (ex == null ? "null" : ex.toString()));
    }
}
