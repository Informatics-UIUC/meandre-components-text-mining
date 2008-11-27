package org.seasr.meandre.components.dendrogram.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import org.gwtwidgets.client.wrap.JsGraphicsPanel;
import org.gwtwidgets.client.style.Color;

/**
 * <p>
 * Title: Base 2D Graph
 * </p>
 * 
 * <p>
 * Description: A Base Object for Drawing 2D Graphs in GWT
 * </p>
 * <p>
 * Max x and Max Y are required.
 * 
 * 
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Automated Learning Group
 * </p>
 * 
 * @author D, Searsmith
 * @version 1.0
 */
public class Base2DGraph {

	// ==============
	// Data Members
	// ==============

	/* Handle to the controller */
	private Base2DDrawback _cont = null;

	/* The graphics canvas object */
	private JsGraphicsPanel _canvas = null;

	/* Height of the graphics canvas panel */
	private int _chartHeight = -1;

	/* Width of the graphics panel object */
	private int _chartWidth = -1;

	/* Y offset for the graphics canvas panel (to the main window) */
	private int _offsetY = -1;

	/* X offset for the graphics canvas panel (to the main window) */
	private int _offsetX = -1;

	/*
	 * X axis offset in the graphics panel (space between panel bottom and x
	 * axis)
	 */
	private int _leftBuffer = 100;

	/*
	 * Y axis offset in the graphics panel (space between left panel edge and
	 * the y axis)
	 */
	private int _bottomBuffer = 100;

	/* Space between the top of the graph panel and the top of the graph */
	private int _topBuffer = 100;

	/*
	 * Space between the right edge of the graph panel and the right edge of the
	 * graph
	 */
	private int _rightBuffer = 100;

	/* Color to be used for drawing the axis */
	private Color _axisColor = org.gwtwidgets.client.style.Color.BLACK;

	/* Line width of the axis and hashes -- all fixed for now */
	private int _axisStrokeWidth = 2;
	private int _axisMajorHashStrokeWidth = 2;
	private int _axisMinorHashStrokeWidth = 1;

	private int _minY = 0;
	private int _minX = 0;

	private int _maxY = -1;
	private int _maxX = -1;

	private int _majorUnitsPerXHash = 10;
	private int _minorUnitsPerXHash = 5;
	private int _majorUnitsPerYHash = 10;
	private int _minorUnitsPerYHash = 5;

	/* Indicates whether the major X hash marks should be drawn. */
	private boolean _drawMajorXHash = true;

	/* Indicates whether the minor X hash marks should be drawn. */
	private boolean _drawMinorXHash = false;

	/* Indicates whether the major Y hash marks should be drawn. */
	private boolean _drawMajorYHash = true;

	/* Indicates whether the minor Y hash marks should be drawn. */
	private boolean _drawMinorYHash = false;

	private boolean _showXAxis = true;
	private boolean _showYAxis = true;

	// Use the following for fixed scale.
	private boolean _useFixedScale = false;

	private int _pixelsPerUnitX = -1;
	private int _pixelsPerUnitY = -1;

	private boolean _initialized = false;

	// ==============
	// Constructors
	// ==============

	public Base2DGraph() {
	}

	public Base2DGraph(Base2DDrawback cont, JsGraphicsPanel can) {
		_cont = cont;
		_canvas = can;
	}

	// ================
	// Public Methods
	// ================

	public JsGraphicsPanel getGraphicsPanel() {
		return _canvas;
	}

	public int getOffsetX() {
		return this._offsetX;
	}

	public int getOffsetY() {
		return this._offsetY;
	}

	public void setFixedScale(int pixsPerUnitX, int pixsPerUnitY) {
		if ((pixsPerUnitX > 0) && (pixsPerUnitY > 0)) {
			_useFixedScale = true;
			_pixelsPerUnitX = pixsPerUnitX;
			_pixelsPerUnitY = pixsPerUnitY;
		}
	}

	public void setAdjustableScale() {
		_useFixedScale = false;
	}

	public void showXAxis(boolean b) {
		_showXAxis = b;
	}

	public void showYAxis(boolean b) {
		_showYAxis = b;
	}

	public void setLeftBuffer(int b) {
		_leftBuffer = b;
	}

	public void setRightBuffer(int b) {
		_rightBuffer = b;
	}

	public void setTopBuffer(int b) {
		_topBuffer = b;
	}

	public void setBottomBuffer(int b) {
		_bottomBuffer = b;
	}

	public void setMinX(int x) {
		_minX = x;
	}

	public void setMinY(int y) {
		_minY = y;
	}

	public void setMaxX(int x) {
		_maxX = x;
	}

	public void setMaxY(int y) {
		_maxY = y;
	}

	public void setMajorXUnitsPerHash(int u) {
		_majorUnitsPerXHash = u;
	}

	public void setMajorYUnitsPerHash(int u) {
		_majorUnitsPerYHash = u;
	}

	public void setMinorXUnitsPerHash(int u) {
		_minorUnitsPerXHash = u;
	}

	public void setMinorYUnitsPerHash(int u) {
		_minorUnitsPerYHash = u;
	}

	// show hashes ============================================================
	public void showMajorXHash(boolean b) {
		_drawMajorXHash = b;
	}

	public void showMajorYHash(boolean b) {
		_drawMajorYHash = b;
	}

	public void showMinorXHash(boolean b) {
		_drawMinorXHash = b;
	}

	public void showMinorYHash(boolean b) {
		_drawMinorYHash = b;
	}

	// Colors =================================================================

	public void setAxisColor(Color c) {
		this._axisColor = c;
	}

	// Graphing Functions =====================================================

	public void drawLine(int x1, int y1, int x2, int y2, int stroke, Color col,
			boolean dashed) {
		if (col == null) {
			_canvas.setColor(org.gwtwidgets.client.style.Color.BLACK);
		} else {
			_canvas.setColor(col);
		}
		_canvas.setStrokeWidth(stroke);
		if (dashed) {
			_canvas.setStrokeDotted();
		}

		_canvas.drawLine(transX(x1), transY(y1), transX(x2), transY(y2));
		_canvas.paint();
	}

	public void drawRect(int x, int y, int width, int height, int stroke,
			Color col, boolean dashed) {
		if (col == null) {
			_canvas.setColor(org.gwtwidgets.client.style.Color.BLACK);
		} else {
			_canvas.setColor(col);
		}
		_canvas.setStrokeWidth(stroke);
		if (dashed) {
			_canvas.setStrokeDotted();
		}

		_canvas.drawRect(transX(x), transY(y) - (height * _pixelsPerUnitY),
				width * _pixelsPerUnitX, height * _pixelsPerUnitY);
		_canvas.paint();
	}

	public void fillRect(int x, int y, int width, int height, Color col) {
		if (col == null) {
			_canvas.setColor(org.gwtwidgets.client.style.Color.BLACK);
		} else {
			_canvas.setColor(col);
		}

		_canvas.fillRect(transX(x), transY(y) - (height * _pixelsPerUnitY),
				width * _pixelsPerUnitX, height * _pixelsPerUnitY);
		_canvas.paint();
	}

	public void drawEllipse(int x, int y, int width, int height, int stroke,
			Color col, boolean dashed) {
		if (col == null) {
			_canvas.setColor(org.gwtwidgets.client.style.Color.BLACK);
		} else {
			_canvas.setColor(col);
		}
		_canvas.setStrokeWidth(stroke);
		if (dashed) {
			_canvas.setStrokeDotted();
		}

		_canvas.drawEllipse(transX(x), transY(y), width * _pixelsPerUnitX,
				height * _pixelsPerUnitY);
		_canvas.paint();
	}

	public void fillEllipse(int x, int y, int width, int height, Color col) {
		if (col == null) {
			_canvas.setColor(org.gwtwidgets.client.style.Color.BLACK);
		} else {
			_canvas.setColor(col);
		}

		_canvas.fillEllipse(transX(x), transY(y), width * _pixelsPerUnitX,
				height * _pixelsPerUnitY);
		_canvas.paint();
	}

	public void drawString(String s, int x, int y, Color col) {
		if (col == null) {
			_canvas.setColor(org.gwtwidgets.client.style.Color.BLACK);
		} else {
			_canvas.setColor(col);
		}

		_canvas.drawString(s, transX(x), transY(y));
		_canvas.paint();
	}

	public void drawStringRect(String s, int x, int y, int width, int height,
			Color col) {
		if (col == null) {
			_canvas.setColor(org.gwtwidgets.client.style.Color.BLACK);
		} else {
			_canvas.setColor(col);
		}

		_canvas.drawStringRect(s, transX(x), transY(y)
				- (height * _pixelsPerUnitY), width * _pixelsPerUnitX, height
				* _pixelsPerUnitY);
		_canvas.paint();
	}

	// =================
	// Package Methods
	// =================

	void redrawNewDimensions() {
		_canvas.clear();
		initialize();
		drawAxis();
		_cont.base2DDraw();
	}

	void redraw() {
		_canvas.clear();
		drawAxis();
		_cont.base2DDraw();
	}

	// =================
	// Private Methods
	// =================

	private void initialize() {

		_cont.base2DConfig();

		if ((_minX > _maxX) || (_minY > _maxY)) {
			throw new RuntimeException(
					"Max and min axis values have not been set properly.");
		}

		if (_showXAxis && _drawMajorXHash) {
			if (_majorUnitsPerXHash <= 0) {
				throw new RuntimeException(
						"Must have X major tick scale set greater than 0 to be displayed.");
			}
			if (_drawMinorXHash) {
				if (_minorUnitsPerXHash <= 0) {
					throw new RuntimeException(
							"Must have X minor tick scale set greater than 0 to be displayed.");
				}
				if ((this._majorUnitsPerXHash % _minorUnitsPerXHash) != 0) {
					throw new RuntimeException(
							"Minor X tick scale must be multiple of major tick scale.");
				}
			}
		}

		if (_showYAxis && _drawMajorYHash) {
			if (_majorUnitsPerYHash <= 0) {
				throw new RuntimeException(
						"Must have Y major tick scale set greater than 0 to be displayed.");
			}
			if (this._drawMinorYHash) {
				if (_minorUnitsPerYHash <= 0) {
					throw new RuntimeException(
							"Must have Y minor tick scale set greater than 0 to be displayed.");
				}
				if ((_majorUnitsPerXHash % _minorUnitsPerYHash) != 0) {
					throw new RuntimeException(
							"Minor Y tick scale must be multiple of major tick scale.");
				}
			}
		}

		_initialized = true;

		if (!this._useFixedScale) {
			// _canvas.setSize("100%", "100%");
		} else {
			int width = _leftBuffer + _rightBuffer
					+ ((_maxX - _minX) * _pixelsPerUnitX);
			int height = _topBuffer + _bottomBuffer
					+ ((_maxY - _minY) * _pixelsPerUnitY);
			_canvas.setPixelSize(width, height);
		}
		_chartHeight = _canvas.getOffsetHeight();
		_chartWidth = _canvas.getOffsetWidth();
		_offsetX = _canvas.getAbsoluteLeft();
		_offsetY = _canvas.getAbsoluteTop();

		// Calc working origin
		if (!_useFixedScale) {

			_pixelsPerUnitY = (int) Math
					.floor((_chartHeight - _bottomBuffer - _topBuffer)
							/ (_maxY - _minY));
			_pixelsPerUnitX = (int) Math
					.floor((_chartWidth - _leftBuffer - _rightBuffer)
							/ (_maxX - _minX));
		}
		if ((_pixelsPerUnitY == 0) || (_pixelsPerUnitX == 0)) {
			setFixedScale(2, 2);
			int width = _leftBuffer + _rightBuffer
					+ ((_maxX - _minX) * _pixelsPerUnitX);
			int height = _topBuffer + _bottomBuffer
					+ ((_maxY - _minY) * _pixelsPerUnitY);
			_canvas.setPixelSize(width, height);
			_chartHeight = _canvas.getOffsetHeight();
			_chartWidth = _canvas.getOffsetWidth();
			_offsetX = _canvas.getAbsoluteLeft();
			_offsetY = _canvas.getAbsoluteTop();
		}

	}

	public int transX(int x) {
		if ((x < _minX) || (x > _maxX)) {
			throw new RuntimeException(
					"x must lie in the interval minX to maxX.");
		}
		return _offsetX + _leftBuffer + (_pixelsPerUnitX * (x - _minX));
	}

	public int transY(int y) {
		if ((y < _minY) || (y > _maxY)) {
			throw new RuntimeException(
					"y must lie in the interval minY to maxY.");
		}
		return _offsetY + _topBuffer + (_pixelsPerUnitY * (_maxY - y));
	}

	/**
	 * Draw major hashings on both axis.
	 */
	private void drawMajorXHash(int originX, int originY) {

		_canvas.setStrokeWidth(_axisMajorHashStrokeWidth);

		int tickPixs = _majorUnitsPerXHash * _pixelsPerUnitX;

		int maxX = transX(_maxX);
		int cnter = 1;
		int plot = originX + (_majorUnitsPerXHash * _pixelsPerUnitX);
		while (plot <= maxX) {
			_canvas.drawLine(plot, originY + 5, plot, originY - 5);
			_canvas.paint();
			cnter++;
			plot = originX + (cnter * tickPixs);
		}

		int minX = transX(_minX);
		cnter = 1;
		plot = originX - (_majorUnitsPerXHash * _pixelsPerUnitX);
		while (plot >= minX) {
			_canvas.drawLine(plot, originY + 5, plot, originY - 5);
			_canvas.paint();
			cnter++;
			plot = originX - (cnter * tickPixs);
		}

	}

	/**
	 * Draw minor hashings on both axis.
	 */
	private void drawMinorXHash(int originX, int originY) {

		_canvas.setStrokeWidth(_axisMinorHashStrokeWidth);

		int tickPixs = _minorUnitsPerXHash * _pixelsPerUnitX;
		int majorTickPixs = _majorUnitsPerXHash * _pixelsPerUnitX;

		int maxX = transX(_maxX);
		int cnter = 1;
		int plot = originX + (_minorUnitsPerXHash * _pixelsPerUnitX);
		while (plot <= maxX) {
			if ((plot % majorTickPixs) != 0) {
				_canvas.drawLine(plot, originY + 2, plot, originY - 2);
				_canvas.paint();
			}
			cnter++;
			plot = originX + (cnter * tickPixs);
		}

		int minX = transX(_minX);
		cnter = 1;
		plot = originX - (_minorUnitsPerXHash * _pixelsPerUnitX);
		while (plot >= minX) {
			if ((plot % majorTickPixs) != 0) {
				_canvas.drawLine(plot, originY + 2, plot, originY - 2);
				_canvas.paint();
			}
			cnter++;
			plot = originX - (cnter * tickPixs);
		}

	}

	/**
	 * Draw major hashings on both axis.
	 */
	private void drawMajorYHash(int originX, int originY) {

		_canvas.setStrokeWidth(_axisMajorHashStrokeWidth);

		int tickPixs = _majorUnitsPerYHash * _pixelsPerUnitY;

		int maxY = transY(_maxY);
		int cnter = 1;
		int plot = originY - (_majorUnitsPerYHash * _pixelsPerUnitY);
		while (plot >= maxY) {
			_canvas.drawLine(originX + 5, plot, originX - 5, plot);
			_canvas.paint();
			cnter++;
			plot = originY - (cnter * tickPixs);
		}

		int minY = transY(_minY);
		cnter = 1;
		plot = originY + (_majorUnitsPerYHash * _pixelsPerUnitY);
		while (plot <= minY) {
			_canvas.drawLine(originX + 5, plot, originX - 5, plot);
			_canvas.paint();
			cnter++;
			plot = originY + (cnter * tickPixs);
		}

	}

	/**
	 * Draw minor hashings on both axis.
	 */
	private void drawMinorYHash(int originX, int originY) {

		_canvas.setStrokeWidth(_axisMinorHashStrokeWidth);

		int tickPixs = _minorUnitsPerYHash * _pixelsPerUnitY;
		int majorTickPixs = _majorUnitsPerYHash * _pixelsPerUnitY;

		int maxY = transY(_maxY);
		int cnter = 1;
		int plot = originY - (_minorUnitsPerYHash * _pixelsPerUnitY);
		while (plot >= maxY) {
			if ((plot % majorTickPixs) != 0) {
				_canvas.drawLine(originX + 2, plot, originX - 2, plot);
				_canvas.paint();
			}
			cnter++;
			plot = originY - (cnter * tickPixs);
		}

		int minY = transY(_minY);
		cnter = 1;
		plot = originY + (_minorUnitsPerYHash * _pixelsPerUnitY);
		while (plot <= minY) {
			if ((plot % majorTickPixs) != 0) {
				_canvas.drawLine(originX + 2, plot, originX - 2, plot);
				_canvas.paint();
			}
			cnter++;
			plot = originY + (cnter * tickPixs);
		}

	}

	/**
	 * Draw both axis observing the appropriate offsets and buffer spacing.
	 */
	private void drawAxis() {

		if (!_initialized) {
			throw new RuntimeException(
					"Graph must first be intiialized.  Call redraw new dimensions first.");
		}

		int originY;

		if ((0 >= _minY) && (0 <= _maxY)) {
			originY = transY(0);
		} else if (0 < _minY) {
			originY = transY(_minY);
		} else {
			/* 0 > _maxX */
			originY = transY(_maxY);
		}

		int originX;

		if ((0 >= _minX) && (0 <= _maxX)) {
			originX = transX(0);
		} else if (0 < _minX) {
			originX = transX(_minX);
		} else {
			/* 0 > _maxX */
			originX = transX(_maxX);
		}

		// draw x axis
		if (_showXAxis) {

			_canvas.setStrokeWidth(_axisStrokeWidth);
			_canvas.setColor(_axisColor);

			_canvas.drawLine(transX(_minX), originY, transX(_maxX), originY);
			_canvas.paint();

			if (_drawMajorXHash) {
				drawMajorXHash(originX, originY);
				if (_drawMinorXHash) {
					drawMinorXHash(originX, originY);
				}
			}

		}

		if (_showYAxis) {

			_canvas.setStrokeWidth(_axisStrokeWidth);
			_canvas.setColor(_axisColor);

			_canvas.drawLine(originX, transY(_minY), originX, transY(_maxY));
			_canvas.paint();

			if (_drawMajorYHash) {
				drawMajorYHash(originX, originY);
				if (_drawMinorYHash) {
					drawMinorYHash(originX, originY);
				}
			}

		}

	}

}
