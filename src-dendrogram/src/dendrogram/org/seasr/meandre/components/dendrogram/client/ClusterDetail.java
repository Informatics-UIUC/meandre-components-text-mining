package org.seasr.meandre.components.dendrogram.client;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.DOM;

/**
 * <p>Title: Component Properties Dialog </p>
 *
 * <p>Description: This class implements a popup panel that is a dialog box
 * used for editing properties of meandre components.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
public class ClusterDetail extends DialogBox {

    //==============
    // Data Members
    //==============


    HTML _h = new HTML();

    ScrollPanel _sp = null;

    Controller _cont = null;

    //==============
    // Constructors
    //==============

    public ClusterDetail(String txt, Controller cont) {
        super(false, false);

        _cont = cont;

        buildPanel(txt);
        setVisible(false);

        DOM.appendChild(DOM.getParent(cont.getGraph().getGraphicsPanel().getElement()), this.getElement());

        show();
        setVisible(true);
        setPopupPosition(100 + _cont.getMain().getScrollLeft(), 100 + _cont.getMain().getScrollTop());
//        setPopupPosition((Window.getClientWidth() / 2) -
//                         (this.getOffsetWidth() / 2),
//                         (Window.getClientHeight() / 2) -
//                         (this.getOffsetHeight() / 2));
    }

    //=================
    // Private Methods
    //=================

    /**
     * Build this dialog panel.
     */
    private void buildPanel(String htm) {
        clear();
        VerticalPanel vp = new VerticalPanel();
        _h.setHTML(htm);
        vp.add(_h);
        Button ok = new Button("OK");
        ok.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                clear();
                hide();
            }
        });
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(ok);
        hp.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
        vp.add(hp);
        vp.setCellHorizontalAlignment(hp, vp.ALIGN_RIGHT);
        vp.setWidth("100%");
        ScrollPanel sp = new ScrollPanel(vp);
        sp.setAlwaysShowScrollBars(false);
        setWidget(sp);
        //sp.setHeight("100%");
        setText("Dendrogram Visualization");
        DOM.setStyleAttribute(this.getElement(), "background", "#EAEAAE");
        DOM.setStyleAttribute(this.getElement(), "border-style", "solid");
        DOM.setStyleAttribute(this.getElement(), "border-width", "thin");
        DOM.setStyleAttribute(this.getElement(), "border-color", "#ffffff");
    }

}
