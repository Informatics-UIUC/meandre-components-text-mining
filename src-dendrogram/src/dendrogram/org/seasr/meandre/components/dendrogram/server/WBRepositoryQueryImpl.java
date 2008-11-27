package org.seasr.meandre.components.dendrogram.server;

//==============
// Java Imports
//==============

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.util.Vector;

//===============
// Other Imports
//===============

import com.google.gwt.user.server.rpc.*;
import org.seasr.meandre.components.dendrogram.client.WBRepositoryQuery;
import org.seasr.meandre.components.dendrogram.client.beans.*;

/**
 * <p>Title: Workbench Repository Query Implementation</p>
 *
 * <p>Description: This class implements the Workbench Repository Query
 * interface for talking to the Meandre back-end.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
public class WBRepositoryQueryImpl extends RemoteServiceServlet implements
        WBRepositoryQuery {

    //==============
    // Data Members
    //==============

    /* Active proxy objects per user.*/
    private static Map _proxies = new Hashtable();

    /* Map of concurrent outputs being generated from flow executions.*/
    private static Map _runOutput = new Hashtable();

    /* Singleton self reference. */
    static private WBRepositoryQueryImpl s_instance = null;

    static public final String s_PROXIES_KEY = "meandre_proxies";

    //================
    // Constructor(s)
    //================

    public WBRepositoryQueryImpl() {
        s_instance = this;
    }

    //================
    // Static Methods
    //================



    //===================================
    // Interface Impl: WBRepositoryQuery
    //===================================

//    /**
//     * Get a list of all running flows and their webui url's.
//     * @param sid String session id
//     * @gwt.typeArgs <org.seasr.meandre.components.dendrogram.client.beans.WBRunningFlow>
//     * @return Set Returns set of running flow beans.
//     */
//    synchronized public Set listRunningFlows(String sid){
//        return null;
//    }




}
