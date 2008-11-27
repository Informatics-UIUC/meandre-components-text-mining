package org.seasr.meandre.components.dendrogram.client.beans;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class WBCallbackObject implements IsSerializable {

//==============
// Data Members
//==============

    // Indicates success of callback request.
    private boolean _success = true;

    // Any error message for the client.
    private String _errMsg = null;

//==============
// Constructors
//==============

    public WBCallbackObject() {
    }

    public WBCallbackObject(boolean success, String msg) {
        _errMsg = msg;
        _success = success;
    }

//================
// Public Methods
//================

    public boolean getSuccess(){
        return _success;
    }

    public void setSuccess(boolean b){
        _success = b;
    }

    public String getMessage(){
        return _errMsg;
    }

    public void setMessage(String msg){
        _errMsg = msg;
    }


}
