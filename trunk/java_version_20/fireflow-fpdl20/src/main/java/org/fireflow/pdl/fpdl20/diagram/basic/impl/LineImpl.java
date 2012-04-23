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

import java.util.ArrayList;
import java.util.List;

import org.fireflow.pdl.fpdl20.diagram.Diagram;
import org.fireflow.pdl.fpdl20.diagram.basic.Label;
import org.fireflow.pdl.fpdl20.diagram.basic.Line;
import org.fireflow.pdl.fpdl20.diagram.basic.Point;
import org.fireflow.pdl.fpdl20.diagram.style.LineStyle;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class LineImpl implements Line {
	Label label = null;
	Point labelPosition = null;
	String labelDirection = Diagram.HORIZONAL;
	List<Point> points = new ArrayList<Point>();
	LineStyle lineStyle = null;

	public Label getLabel(){
		return label;
	}
	
	public void setLabel(Label lb){
		this.label = lb;
	}
	
	public Point getLabelPosition(){
		return labelPosition;
	}
	public void setLabelPosition(Point p){
		this.labelPosition = p;
	}
	
	public String getLabelDirection(){
		return this.labelDirection;
	}
	
	public void setLabelDirection(String lbDirection){
		this.labelDirection = lbDirection;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Line#getPoints()
	 */
	public List<Point> getPoints() {
		return points;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Line#getLineStyle()
	 */
	public LineStyle getLineStyle() {
		return lineStyle;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Line#setLineStyle(org.fireflow.pdl.fpdl20.diagram.style.LineStyle)
	 */
	public void setLineStyle(LineStyle style) {
		this.lineStyle = style;

	}

}
