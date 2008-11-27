package org.seasr.meandre.components.dendrogram.client;

/**
 * <p>Title: Workbench Command</p>
 *
 * <p>Description: A simple command interface for chaining asynchronous
 * actions together.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA, Automated Learning Group</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */
public interface WBCommand {

    //================
    // Public Methods
    //================

    public void execute(Object work);
}
