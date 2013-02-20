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
package org.fireflow.pdl.fpdl20.diagram.figure.part;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class FulfilStyleImpl implements FulfilStyle {
	private String color = "#FFFFFF";//缺省为白色
	private String gradientStyle = FulfilStyle.GRADIENT_STYLE_NONE;

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.FulfilStyle#getColor()
	 */
	@Override
	public String getColor() {
		return color;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.FulfilStyle#setColor(java.lang.String)
	 */
	@Override
	public void setColor(String color) {
		this.color = color;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.FulfilStyle#getGradientStyle()
	 */
	@Override
	public String getGradientStyle() {
		return gradientStyle;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.FulfilStyle#setGradientStyle(java.lang.String)
	 */
	@Override
	public void setGradientStyle(String style) {
		gradientStyle = style;
		
	}


}
