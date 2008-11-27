package org.seasr.meandre.components.dendrogram.bootstrap.jetty;

//==============
// Java Imports
//==============

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

//===============
// Other Imports
//===============

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import java.io.File;


/** Bootstraps a Meandre Workbench Application.
 *
 * @author Xavier Llor&agrave;
 * @modified Amit Kumar -Added MeandreSecurityManager
 * @modified D. Searsmith -Adapted to workbench
 */
public class Bootstrapper {

    /** The version */
    public final static String VERSION = "0.2 pre-alpha";

    /** The base Meandre port */
    public static final int BASE_PORT = 1712;

    /** The base directory for Jetty */
    public static final String JETTY_HOME = ".";

    /** The logger for the bootstrapper */
    private static Logger log = null;

    /** The basic handler for all the loggers */
    public static Handler handler = null;

    // Initializing the logger and its handlers
    static {
        log = Logger.getLogger(Bootstrapper.class.getName());
        log.setLevel(Level.FINEST);
        try {
            log.addHandler(handler = new FileHandler(
                    "meandre-workbench-log.xml"));
        } catch (SecurityException e) {
            System.err.println("Could not initialize meandre-workbench-log.xml");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Could not initialize meandre-workbench-log.xml");
            System.exit(1);
        }

        handler.setLevel(Level.FINEST);
    }

    /** Boostraps the Meandre execution engine.
     *
     * @param args Command line arguments
     * @throws Exception Something went wrong, really wrong.
     */
    public static void main(String[] args) throws Exception {
        log.config("Bootstrapping Meandre Workbench");

//		log.config("Installing MeandreSecurityManager");
//		if( System.getSecurityManager() == null )
//		{
//		    System.setSecurityManager( new MeandreSecurityManager() );
//		}

        log.config("Start Jetty server");
        runEmbeddedJetty();

    }


    /** Run the embedded jetty server.
     *
     * @throws Exception Jetty could not be started
     */
    private static void runEmbeddedJetty() throws Exception {
        Server server = new Server();

        Connector connector = new SelectChannelConnector();
        connector.setPort(Integer.getInteger("jetty.port", BASE_PORT).intValue());
        server.setConnectors(new Connector[] {connector});

//        String jetty_home = System.getProperty("user.dir");
//        jetty_home = (jetty_home==null)?JETTY_HOME:jetty_home;

        System.out.println("Jetty Home = " + JETTY_HOME);

        File f = new File(".");

        System.out.println("Working Dir = " + f.getAbsolutePath());


        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(JETTY_HOME + "/webapp/war");
        webapp.setDefaultsDescriptor(JETTY_HOME +
                                     "/webapp/bootstrap/meandre-web.xml");
        server.setHandler(webapp);
        server.start();
        server.join();
    }


}
