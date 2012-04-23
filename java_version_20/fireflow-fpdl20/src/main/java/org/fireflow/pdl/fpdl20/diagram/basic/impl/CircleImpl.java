/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.pdl.fpdl20.diagram.basic.impl;

import org.fireflow.pdl.fpdl20.diagram.basic.Circle;
import org.fireflow.pdl.fpdl20.diagram.basic.Label;
import org.fireflow.pdl.fpdl20.diagram.basic.Point;
import org.fireflow.pdl.fpdl20.diagram.style.FulfilStyle;
import org.fireflow.pdl.fpdl20.diagram.style.LineStyle;
import org.fireflow.pdl.fpdl20.diagram.style.impl.FulfilStyleImpl;
import org.fireflow.pdl.fpdl20.diagram.style.impl.LineStyleImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CircleImpl implements Circle {
	private Label label = null;
	private Point theCenter = null;
	private int radius = 10;
	private LineStyle boundStyle = null;
	private FulfilStyle fulfilStyle = null;

	public CircleImpl(){
		theCenter = new PointImpl(10,10);
		fulfilStyle = new FulfilStyleImpl();
		boundStyle = new LineStyleImpl();
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Circle#getLabel()
	 */
	public Label getLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Circle#setLabel(org.fireflow.pdl.fpdl20.diagram.basic.Label)
	 */
	public void setLabel(Label lb) {
		this.label = lb;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Circle#getTheCenter()
	 */
	public Point getTheCenter() {
		return theCenter;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Circle#setTheCenter(org.fireflow.pdl.fpdl20.diagram.basic.Point)
	 */
	public void setTheCenter(Point p) {
		this.theCenter = p;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Circle#getRadius()
	 */
	public int getRadius() {
		return radius;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Circle#setRadius(int)
	 */
	public void setRadius(int r) {
		radius = r;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Circle#getBoundsStyle()
	 */
	public LineStyle getBoundsStyle() {
		return this.boundStyle;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Circle#setBoundsStyle(org.fireflow.pdl.fpdl20.diagram.style.BoundsStyle)
	 */
	public void setBoundsStyle(LineStyle style) {
		this.boundStyle = style ;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Circle#getFulfilStyle()
	 */
	public FulfilStyle getFulfilStyle() {
		return this.fulfilStyle;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Circle#setFulfilStyle(org.fireflow.pdl.fpdl20.diagram.style.FulfilStyle)
	 */
	public void setFulfilStyle(FulfilStyle style) {
		this.fulfilStyle = style;
	}

}
