package org.seasr.meandre.components.dendrogram.client;

//==============
// Java Imports
//==============

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

//===============
// Other Imports
//===============

import org.gwtwidgets.client.wrap.JsGraphicsPanel;
import com.gwt.components.client.Effects;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

/**
 * <p>
 * Title: Controller
 * </p>
 * 
 * <p>
 * Description: This is the controller for the Meandre Workbench GUI
 * </p>
 * 
 * <p>
 * Copyright: UIUC Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company: Automated Learning Group at NCSA, UIUC
 * </p>
 * 
 * @author Duane Searsmith
 * @version 1.0
 */
public class Controller implements Base2DDrawback {

	// ==============
	// Data Members
	// ==============

	/* HTTP Paths */

	static Controller s_controller = null;

	/* Handle to Main instance */
	private Main _main = null;

	/* Graphics panel */
	private JsGraphicsPanel _drawPan = null;

	private Base2DGraph _graph = null;

	private List _nodeList = new ArrayList();
	private List _rects = new ArrayList();

	private String _fragID = "EMPTY";
	private String _fragHost = "EMPTY";
	private String _fragPort = "EMPTY";

	private Map _nodeMap = new HashMap();

	Map _plotted = new HashMap();

	ModelNode _root = null;

	ModelNode _workingRoot = null;

	ModelNode _targ = null;

	// ==================================
	// ==================================
	// ==================================

	// ================
	// Constructor(s)
	// ================

	public Controller(Main main, JsGraphicsPanel dp) {
		s_controller = this;
		_main = main;
		_drawPan = dp;
		_fragID = DOM
				.getElementAttribute(DOM.getElementById("fragid"), "value");
		_fragHost = DOM.getElementAttribute(DOM.getElementById("fraghost"),
				"value");
		_fragPort = DOM.getElementAttribute(DOM.getElementById("fragport"),
				"value");
	}

	public void begin() {
		getData();
	}

	// =================
	// Private Methods
	// =================

	public ModelNode getRoot() {
		return _root;
	}

	/**
	 * Parse the JSON '|' delimited objects returned from the server into
	 * ModelNode objects, and populate the global _nodeList.
	 * 
	 * @param text
	 *            String Model data returned from the server "get".
	 */
	private void parseModel(String text) {
		int pos = text.indexOf("|");
		while (pos >= 0) {
			String val = text.substring(0, pos);
			text = text.substring(pos + 1);
			if ((val != null) && (val.length() > 0)) {
				JSONObject job = (JSONObject) JSONParser.parse(val);
				ModelNode mnode = new ModelNode(job);
				_nodeMap.put(new Double(mnode.getID()), mnode);
				_nodeList.add(mnode);
			}
			pos = text.indexOf("|");
		}
		_root = (ModelNode) _nodeList.get(_nodeList.size() - 1);
		_workingRoot = _root;
	}

	/**
	 * Class for containing cluster node information for nodes in the cluster
	 * tree model.
	 */
	class ModelNode {
		private double _childDistance = -1;
		private double _left = -1;
		private double _right = -1;
		private double _id = -1;

		private int _x1 = -1;
		private int _x2 = -1;
		private int _clustX = -1;
		private int _height = 0;

		ModelNode(JSONObject job) {
			_childDistance = ((JSONNumber) job.get("cd")).getValue();
			JSONNumber jnum = (JSONNumber) job.get("lc");
			_left = (jnum == null) ? -1 : jnum.getValue();
			jnum = (JSONNumber) job.get("rc");
			_right = (jnum == null) ? -1 : jnum.getValue();
			_id = ((JSONNumber) job.get("rid")).getValue();
		}

		void setX1(int i) {
			_x1 = i;
		}

		void setX2(int i) {
			_x2 = i;
		}

		void setClustX(int i) {
			_clustX = i;
		}

		void setHeight(int i) {
			_height = i;
		}

		double getID() {
			return _id;
		}

		double getLeft() {
			return _left;
		}

		double getRight() {
			return _right;
		}

		double getDist() {
			return _childDistance;
		}

		boolean isLeaf() {
			return (_left == -1);
		}

		void setDist(double d) {
			_childDistance = d;
		}
	}

	public void getTabData(ModelNode mn) {
		HTTPRequest.asyncGet("http://" + _fragHost + ":" + _fragPort + "/"
				+ _fragID + "?getrows=" + mn.getID(),
				new ResponseTextHandler() {
					public void onCompletion(String text) {
						new ClusterDetail(text, Controller.this);
					}
				});

	}

	/**
	 * Get the model data from the server, parse it, and start the drawing
	 * process.
	 */
	private void getData() {
		HTTPRequest.asyncGet("http://" + _fragHost + ":" + _fragPort + "/"
				+ _fragID + "?getmod=true", new ResponseTextHandler() {
			public void onCompletion(String text) {
				parseModel(text);
				draw();
			}
		});
	}

	private void normalizeData() {

		// normalize between 0 and 100 inclusive

		// get min and max dist value
		double min = Integer.MAX_VALUE;
		double max = Integer.MIN_VALUE;

		for (int i = 0, n = _rects.size(); i < n; i++) {
			ModelNode mnode = (ModelNode) _rects.get(i);
			double dist = mnode.getDist();
			if (dist < min) {
				min = dist;
			}
			if (dist > max) {
				max = dist;
			}
		}

		double range = max - min;

		for (int i = 0, n = _rects.size(); i < n; i++) {
			ModelNode mnode = (ModelNode) _rects.get(i);
			int dist = (int) Math.round((mnode.getDist() / range) * 300);
			if (dist < 0) {
				dist = 0;
			} else if (dist > 300) {
				dist = 300;
			}
			mnode.setHeight(dist);

		}
	}

	/**
	 * Call the methods to create the graph API and draw the data.
	 */
	private void draw() {
		buildRects();
		normalizeData();
		_graph = new Base2DGraph(this, _drawPan);

		_graph.redrawNewDimensions();
	}

	public void redrawFromRoot(ModelNode mn) {
		_rects.clear();
		_targ = null;
		_workingRoot = mn;
		buildRects();
		normalizeData();
		_graph.redrawNewDimensions();
	}

	private void buildRects() {
		Set plotted = new HashSet();
		postOrderWalk(_workingRoot, 0, plotted);
		for (int i = 0, n = _nodeList.size(); i < n; i++) {
			ModelNode mn = (ModelNode) _nodeList.get(i);
			if (!mn.isLeaf()) {
				if (plotted.contains(mn)) {
					_rects.add(mn);
					if (mn.getID() == _workingRoot.getID()) {
						break;
					}
				}
			}
		}
	}

	private int postOrderWalk(ModelNode root, int x, Set plotted) {
		ModelNode lc = (ModelNode) _nodeMap.get(new Double(root.getLeft()));
		if (lc.isLeaf()) {
			lc.setClustX(x);
			x += 4;
		} else {
			x = postOrderWalk(lc, x, plotted);
		}
		ModelNode rc = (ModelNode) _nodeMap.get(new Double(root.getRight()));
		if (rc.isLeaf()) {
			rc.setClustX(x);
			x += 4;
		} else {
			x = postOrderWalk(rc, x, plotted);
		}
		root.setX1(lc._clustX);
		root.setX2(rc._clustX);

		// set ClustX
		root.setClustX(((int) (root._x2 - root._x1) / 2) + root._x1);
		plotted.add(root);
		return x;
	}

	// ===========================
	// Interface Implementation: Base2DDrawback
	// ===========================

	public void base2DConfig() {
		// Test Values
		_graph.setAxisColor(org.gwtwidgets.client.style.Color.BLACK);
		_graph.showYAxis(false);
		_graph.showMinorXHash(false);
		_graph.showMajorXHash(false);
		_graph.setMinX(0);
		_graph.setMinY(0);
		_graph.setMaxX(_rects.size() * 4);
		_graph.setMaxY(300);
		_graph.setLeftBuffer(25);
		_graph.setBottomBuffer(25);
		_graph.setTopBuffer(25);
		_graph.setRightBuffer(25);
	}

	public void base2DDraw() {
		_graph.getGraphicsPanel().setSize("100%", "100%");
		Effects.Effect("Fade", _graph.getGraphicsPanel(), "{ duration: .25 }")
				.addEffectListener(new Effects.EffectListenerAdapter() {
					public void onAfterFinish(Effects.Effect sender) {
						for (int i = (_rects.size() - 1); i >= 0; i--) {
							ModelNode mnode = (ModelNode) _rects.get(i);
							if (!mnode.isLeaf()) {

								org.gwtwidgets.client.style.Color col = new org.gwtwidgets.client.style.Color(
										(int) Math
												.round(255 - (((double) (mnode._height / 300)) * 255)),
										(int) Math
												.round(((double) mnode._height / 300) * 255),
										0);

								if ((_targ != null)
										&& (_targ.getID() == mnode.getID())) {
									col = org.gwtwidgets.client.style.Color.BLUE;
								}

								_graph.fillRect(mnode._x1, 0, mnode._x2
										- mnode._x1, mnode._height, col);
								_graph.drawRect(mnode._x1, 0, mnode._x2
										- mnode._x1, mnode._height, 2, null,
										false);
							}
						}
						Effects.Effect("Appear", _graph.getGraphicsPanel(),
								"{ duration: .5 }");
					}
				});
	}

	public void base2DDrawQuick() {
		for (int i = (_rects.size() - 1); i >= 0; i--) {
			ModelNode mnode = (ModelNode) _rects.get(i);
			if (!mnode.isLeaf()) {

				org.gwtwidgets.client.style.Color col = new org.gwtwidgets.client.style.Color(
						(int) Math
								.round(255 - (((double) (mnode._height / 300)) * 255)),
						(int) Math.round(((double) mnode._height / 300) * 255),
						0);

				if ((_targ != null) && (_targ.getID() == mnode.getID())) {
					col = org.gwtwidgets.client.style.Color.BLUE;
				}

				_graph.fillRect(mnode._x1, 0, mnode._x2 - mnode._x1,
						mnode._height, col);
				_graph.drawRect(mnode._x1, 0, mnode._x2 - mnode._x1,
						mnode._height, 2, null, false);
			}
		}
	}

	public void setTarg(ModelNode mn) {
		_targ = mn;
	}

	ModelNode findRect(int x, int y) {
		for (int i = 0, n = _rects.size(); i < n; i++) {
			ModelNode mn = (ModelNode) _rects.get(i);
			if ((x >= _graph.transX(mn._x1)) && (x <= _graph.transX(mn._x2))
					&& (y <= _graph.transY(0))
					&& (y >= _graph.transY(mn._height))) {
				return mn;
			}
		}
		return null;
	}

	// ============================
	// Property Getters / Setters
	// ============================

	Base2DGraph getGraph() {
		return _graph;
	}

	/**
	 * Get a handle to the Maion class.
	 * 
	 * @return Main
	 */
	Main getMain() {
		return _main;
	}

	// redirect the browser to the given url
	public static native void redirectOrClose(String url) /*-{
	                                  if ($wnd.opener && !$wnd.opener.closed){
	                                  $wnd.close();
	                              } else {
	                                               $wnd.location = url;
	                              }
	                            }-*/
	;

	// ==========================================================
	// ==========================================================
	// ==========================================================

}
