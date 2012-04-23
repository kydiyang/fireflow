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

import org.fireflow.pdl.fpdl20.diagram.basic.Bounds;
import org.fireflow.pdl.fpdl20.diagram.basic.Label;
import org.fireflow.pdl.fpdl20.diagram.basic.Rectangle;
import org.fireflow.pdl.fpdl20.diagram.style.FulfilStyle;
import org.fireflow.pdl.fpdl20.diagram.style.impl.FulfilStyleImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class RectangleImpl implements Rectangle {
	private Label mainLabel = null;
	private Label minorLabel = null;
	private Bounds bounds = null;
	private FulfilStyle fulfilStyle = null;
	
	public RectangleImpl(){
		mainLabel = new LabelImpl();
		bounds = new BoundsImpl();
		fulfilStyle = new FulfilStyleImpl();
	}
	
	public Label getTitle(){
		return mainLabel;
	}
	public void setTitle(Label lb){
		this.mainLabel = lb;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Node#getBounds()
	 */
	public Bounds getBounds() {
		return bounds;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Node#setBounds(org.fireflow.pdl.fpdl20.diagram.basic.Bounds)
	 */
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Node#getFulfillStyle()
	 */
	public FulfilStyle getFulfilStyle() {
		return fulfilStyle;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Node#setFulfillStyle(org.fireflow.pdl.fpdl20.diagram.style.FulfillStyle)
	 */
	public void setFulfilStyle(FulfilStyle style) {
		fulfilStyle = style;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Rectangle#getMinorLabel()
	 */
	public Label getContent() {
		return this.minorLabel;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Rectangle#setMinorLabel(org.fireflow.pdl.fpdl20.diagram.basic.Label)
	 */
	public void setContent(Label lb) {
		this.minorLabel = lb;		
	}

}
