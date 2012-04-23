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
import org.fireflow.pdl.fpdl20.diagram.basic.Point;
import org.fireflow.pdl.fpdl20.diagram.style.BoundsStyle;
import org.fireflow.pdl.fpdl20.diagram.style.impl.BoundsStyleImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class BoundsImpl implements Bounds {
	protected Point upperLeftCorner = new PointImpl(0,0);
	
	protected int height = 10;
	protected int weight = 10;
	
	protected BoundsStyle boundsStyle = new BoundsStyleImpl();
	


	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Bounds#getWidth()
	 */
	public int getWidth() {
		return this.weight;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Bounds#setWidth(int)
	 */
	public void setWidth(int w) {
		this.weight = w;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Bounds#getHeight()
	 */
	public int getHeight() {
		
		return this.height;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Bounds#setHeight(int)
	 */
	public void setHeight(int h) {
		this.height = h;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Bounds#getBoundsStyle()
	 */
	public BoundsStyle getBoundsStyle() {
		
		return this.boundsStyle;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Bounds#setBoundsStyle(org.fireflow.pdl.fpdl20.diagram.style.BoundsStyle)
	 */
	public void setBoundsStyle(BoundsStyle style) {
		this.boundsStyle = style;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Bounds#getUpperLeftCorner()
	 */
	public Point getUpperLeftCorner() {
		return upperLeftCorner;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Bounds#setUpperLeftCorner(org.fireflow.pdl.fpdl20.diagram.basic.Point)
	 */
	public void setUpperLeftCorner(Point p) {
		this.upperLeftCorner = p;
		
	}

}
