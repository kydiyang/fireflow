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

import org.fireflow.pdl.fpdl20.diagram.style.LineStyle;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class LineStyleImpl implements LineStyle {
	protected String color = "#000000";
	protected int thick = 2;
	protected String lineType = LineStyle.LINETYPE_SOLID;
	
	protected int space = 0;
	
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.Style#getColor()
	 */
	public String getColor() {
		return color;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.Style#setColor(java.lang.String)
	 */
	public void setColor(String cl) {
		this.color = cl;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.LineStyle#getThick()
	 */
	public int getThick() {
		return thick;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.LineStyle#setThick(int)
	 */
	public void setThick(int thick) {
		this.thick = thick;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.LineStyle#getLineType()
	 */
	public String getLineType() {
		return this.lineType;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.LineStyle#setLineType(java.lang.String)
	 */
	public void setLineType(String type) {
		this.lineType = type;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.LineStyle#getSpace()
	 */
	public int getSpace() {
		return this.space;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.LineStyle#setSpace(int)
	 */
	public void setSpace(int space) {
		this.space = space;

	}

}
