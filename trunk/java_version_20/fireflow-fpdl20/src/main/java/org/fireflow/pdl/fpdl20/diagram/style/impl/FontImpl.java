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

import org.fireflow.pdl.fpdl20.diagram.style.Font;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class FontImpl implements Font {
	protected int size = 12;
	protected String color = "#000000";
	protected String weight = Font.FONT_WEIGHT_NORMAL;
	
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.Font#getSize()
	 */
	public int getSize() {
		return size;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.Font#setSize(int)
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.Font#getColor()
	 */
	public String getColor() {
		return this.color;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.Font#setColor(java.lang.String)
	 */
	public void setColor(String cl) {
		this.color = cl;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.Font#getWeight()
	 */
	public String getWeight() {
		return this.weight;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.style.Font#setWeight(java.lang.String)
	 */
	public void setWeight(String wt) {
		this.weight = wt;

	}

}
