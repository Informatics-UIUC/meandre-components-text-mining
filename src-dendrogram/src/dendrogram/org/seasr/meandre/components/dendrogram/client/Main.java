package org.seasr.meandre.components.dendrogram.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.Window;
import org.gwtwidgets.client.wrap.JsGraphicsPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;


/**
 * <p>Title: Main Meandre Workbench Application Class</p>
 *
 * <p>Description: This is the entry point class for the Meandre Workbench
 * GUI</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
public class Main implements EntryPoint, WindowResizeListener,
        WindowCloseListener {

    //==============
    // Data Members
    //==============


    /* Graphics panel for drawing functions (component connections)*/
    private JsGraphicsPanel _jsgPan = null;

    /*  Controller for the workbench application GUI.*/
    private Controller _controller = null;


    //private VerticalPanel _canvasPan = null;


    //================
    // Public Methods
    //================

    /**
     * Returns the amount of horizontal scroll.
     *
     * return the scroll amount in pixels
     */
    public static native int getScrollLeft() /*-{
     return $doc.body.scrollLeft;
     }-*/;

    /**
     * Returns the amount of horizontal scroll for the given element.
     *
     * @param elem the element
     * @return the scroll amount in pixels
     */
    public static native int getScrollLeft(Element elem) /*-{
     return elem.scrollLeft;
     }-*/;

    /**
     * Returns the amount of vertical scroll.
     *
     * @return the scroll amount in pixels
     */
    public static native int getScrollTop() /*-{
     return $doc.body.scrollTop;
     }-*/;

    /**
     * Returns the amount of vertical scroll for the given element.
     *
     * @param elem the element
     * @return the scroll amount in pixels
     */
    public static native int getScrollTop(Element elem) /*-{
     return elem.scrollTop;
     }-*/;


    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        _jsgPan = new JsGraphicsPanel("g"){
          public void onBrowserEvent(Event evt){
              switch (DOM.eventGetType(evt)) {

//              case Event.ONDBLCLICK: {
//                  if (_controller != null){
//                      int x = DOM.eventGetClientX(evt)-_controller.getGraph().getOffsetX();
//                      int y = DOM.eventGetClientY(evt) - _controller.getGraph().getOffsetY() ;
//                      Controller.ModelNode mn = _controller.findRect(x, y);
//                  }
//                  break;
//              }

              case Event.ONCLICK:
                  if (_controller != null){

                      int x = (DOM.eventGetClientX(evt) + getScrollLeft()) - _controller.getGraph().getOffsetX();
                      int y = (DOM.eventGetClientY(evt) + getScrollTop()) - _controller.getGraph().getOffsetY();
                      Controller.ModelNode mn = _controller.findRect(x, y);
                      if (DOM.eventGetCtrlKey(evt)){
                          if (mn != null) {
                              _controller.redrawFromRoot(mn);
                          } else {
                              _controller.redrawFromRoot(_controller.getRoot());
                          }
                      } else {
                          if (mn != null) {
                              _controller.setTarg(mn);
                          } else {
                              _controller.setTarg(null);
                          }
                          _controller.base2DDrawQuick();
                          if (mn != null){
                              _controller.getTabData(mn);
                          }
                      }
                  }
                  break;

//              case Event.ONCLICK:
//                  if (clickListeners != null) {
//                      clickListeners.fireClick(this);
//                  }
//                  break;
//
//              case Event.ONMOUSEDOWN:
//                  if (mouseListeners != null) {
//                      mouseListeners.fireMouseEvent(this, event);
//                  }
//                  break;
//              case Event.ONMOUSEUP:
//                  if (mouseListeners != null) {
//                      mouseListeners.fireMouseEvent(this, event);
//                  }
//                  break;
//              case Event.ONMOUSEMOVE:
//                  if (mouseListeners != null) {
//                      mouseListeners.fireMouseEvent(this, event);
//                  }
//                  break;
//              case Event.ONMOUSEOUT:
//                  if (mouseListeners != null) {
//                      mouseListeners.fireMouseEvent(this, event);
//                  }
//                  break;

              }

          }
        };

        RootPanel.get().add(_jsgPan);
        DOM.setStyleAttribute(_jsgPan.getElement(), "cursor", "hand");


        _jsgPan.sinkEvents(Event.MOUSEEVENTS);
        _jsgPan.sinkEvents(Event.ONCLICK);
        _jsgPan.sinkEvents(Event.ONDBLCLICK);


        DOM.setStyleAttribute(RootPanel.get().getBodyElement(), "background",
                              "white");

        // Make sure we catch unexpected exceptions in web mode, especially in other browsers
        GWT.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        _controller = new Controller(this, _jsgPan);

        _controller.begin();

        _jsgPan.setSize("100%", "100%");

       

        // Hook the window resize event, so that we can adjust the UI.
        Window.addWindowResizeListener(this);
        Window.addWindowCloseListener(this);



    }

    //===========================================
    // Implement Interface: WindowResizeListener
    //===========================================

    /**
     * This methjod is called when the work bench application window is
     * ewsized.
     *
     * @param width int The width of the work bench application window.
     * @param height int The height of the work bench application window.
     */
    public void onWindowResized(int width, int height) {
        resizeApp();
    }


    //===============================================
    // Interface Implementation: WindowCloseListener
    //===============================================

    /**
     * Method that is called when user clicks to close the work bench
     * application window.
     *
     * @return String A string that displays in a confirm dialog box in
     * response to the user's clicking of the window close icon.
     */
    public String onWindowClosing() {
        return null;
    }

    /**
     *  Method that is called after the work bench application window has been
     *  closed by the user.
     */
    public void onWindowClosed() {
        cleanUp();
    }

    //=================
    // Package Methods
    //=================


    /**
     * Call to close the application.
     */
    void closeApp() {
        cleanUp();
        closeAppJS();
    }

    //=================
    // Private Methods
    //=================

    private void resizeApp() {
        _controller.getGraph().redrawNewDimensions();
    }

    /**
     * Called on application shutdown to clean up any dangling resources.
     */
    private void cleanUp() {
//        _controller.clearCanvas();
    }

    /**
     * Javascript call to close the application window.
     */
    private void closeAppJS() {
        RootPanel.get().clear();
        DOM.setStyleAttribute(RootPanel.get().getBodyElement(), "background",
                              "black");
    }

}
