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
package org.fireflow.pdl.fpdl20.diagram.style.impl;

import org.fireflow.pdl.fpdl20.diagram.style.BoundsStyle;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class BoundsStyleImpl extends LineStyleImpl implements
		org.fireflow.pdl.fpdl20.diagram.style.BoundsStyle {
	String cornerStyle = BoundsStyle.CORNER_STYLE_RIGHT_ANGLE;
	int radius = 5;

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.BoundsStyle#getCornerStyle()
	 */
	public String getCornerType() {
		return cornerStyle;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.BoundsStyle#setCornerStyle(java.lang.String)
	 */
	public void setCornerType(String corner) {
		cornerStyle = corner;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.BoundsStyle#getRadius()
	 */
	public int getRadius() {
		return radius;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.BoundsStyle#setRadius(int)
	 */
	public void setRadius(int r) {
		radius = r;

	}

}
