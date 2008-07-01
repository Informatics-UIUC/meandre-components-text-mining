/**
 * University of Illinois/NCSA
 * Open Source License
 * 
 * Copyright (c) 2008, Board of Trustees-University of Illinois.  
 * All rights reserved.
 * 
 * Developed by: 
 * 
 * Automated Learning Group
 * National Center for Supercomputing Applications
 * http://www.seasr.org
 * 
 *  
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal with the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions: 
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimers. 
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimers in the 
 *    documentation and/or other materials provided with the distribution. 
 * 
 *  * Neither the names of Automated Learning Group, The National Center for
 *    Supercomputing Applications, or University of Illinois, nor the names of
 *    its contributors may be used to endorse or promote products derived from
 *    this Software without specific prior written permission. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * WITH THE SOFTWARE.
 */ 

package org.seasr.components.text.gate.util;


import java.io.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class GATEInitialiser implements Serializable {

    //==============
    // Data Members
    //==============

	private static final long serialVersionUID = 1L;
	static public boolean s_ready = false;
    static private Object _lock = new Object();

    //================
    // Constructor(s)
    //================
    public GATEInitialiser() {
    }

    {
        synchronized (_lock) {

            try {
                if (!s_ready) {
                    String oldGateHome = System.getProperty("gate.home");
                    if (oldGateHome == null) {
                        String cwd = System.getProperty("user.dir");
                        if (!cwd.endsWith(File.separator))
                            cwd += File.separator;
                        System.out.println("gate.home not set.  Attempting to set to " + cwd);
                        System.setProperty("gate.home", cwd);
                    }

                    String oldGatePlugins = System.getProperty("gate.plugins.home");
                    if(oldGatePlugins == null) {
                        String gatehome = System.getProperty("gate.home");
                        System.setProperty("gate.plugins.home", gatehome+"plugins");
                    }

                    gate.Gate.init();
                    System.out.println("Gate inited.");
                    s_ready = true;
                }
            }
            catch (Exception e) {
                System.out.println("GATE INIT ERROR: " + e);
                e.printStackTrace();
            }
        }
    }

    // this maps 2.2 gate resource paths to gate 3.1 resource paths.
    @SuppressWarnings("unchecked")
	static Map conversionMap;

    static {
        conversionMap = new HashMap();
        conversionMap.put("/creole/BengaliNE/", "/ANNIE/resources/BengaliNE/");
        conversionMap.put("/creole/chunker/VP/", "/ANNIE/resources/VP/");
        conversionMap.put("/creole/gazeteer/", "/ANNIE/resources/gazetteer/");
        conversionMap.put("/creole/heptag/", "/ANNIE/resources/heptag/");
        conversionMap.put("/creole/morph/", "/Tools/resources/morph/");
        conversionMap.put("/creole/namematcher/", "/ANNIE/resources/othomatcher/");
        conversionMap.put("/creole/ontology/", "/Ontology_Tools/resources/");
        conversionMap.put("/creole/splitter/", "/ANNIE/resources/sentenceSplitter/");
        conversionMap.put("/creole/tokeniser/", "/ANNIE/resources/tokeniser/");
        conversionMap.put("/creole/transducer/NE/", "/ANNIE/resources/NE/");
    }

    /**
     * Try to convert the path to a resource in gate 2.2 to a valid path for gate 3.1.
     *
     * @param oldPath the existing path for the resource
     * @return the new path, or null if a conversion for the old path was not found
     */
    private static String convertPath(String oldPath) {
        Iterator pathIter = conversionMap.keySet().iterator();

        String newPath = null;
        boolean found = false;

        while (pathIter.hasNext() && !found) {
            String aPath = (String) pathIter.next();
            if (oldPath.startsWith(aPath)) {
                newPath = oldPath.replaceFirst(aPath, (String) conversionMap.get(aPath));
                found = true;
            }
        }

        return newPath;
    }

    /**
     * Try to convert the old gate urls to ones valid for this filesystem.
     * @param oldURL
     * @return
     */
    public static String getResourceURL(String oldURL) {
        // if the path starts with gate: try to convert it.
        String path = oldURL.substring(GATE_PREFIX.length());

        String newPath = convertPath(path);
        if(newPath == null)
            return null;

        // try to append this to gate.home and see if anything exists there..
        String baseDir = System.getProperty("gate.plugins.home");

        baseDir += newPath;
        File theFile = new File(baseDir);
        if(theFile.exists()) {
            try {
                return theFile.toURL().toString();
            }
            catch(Exception ex) {
                return null;
            }
        }

        return null;
    }

    public static final String GATE_PREFIX = "gate:";

    /**
     * Try to convert a resource to a valid url.  If the url starts with gate:, try to load
     * the resource from the classpath.  If the url does not start with gate: but is a valid url,
     * just return the url.
     * @param path the path to convert
     * @return a url for a resource, or null if the resource was not found.
     */
    /*public static String getGateResourceURL(String path) {
        // if the path starts with gate://... try to convert it.
        path = path.substring(GATE_PREFIX.length());

        // now get the url for the resource
        java.net.URL url = Gate.class.getResource(path);
        if(url == null)
            return null;
        return url.toString();
    }*/

}

