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
package org.fireflow.pdl.fpdl20.diagram.style;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface LineStyle extends Style{
	public static final String LINETYPE_SOLID = "SOLID";
	public static final String LINETYPE_DASHED = "DASHED";
	public static final String LINETYPE_DASHDOTTED = "DASHDOTTED";
	public static final String LINETYPE_DOTTED = "DOTTED";
	
	
	public int getThick();
	public void setThick(int thick);
	
	/**
	 * 线条类型：实线，虚线，点画线，点线
	 * @return
	 */
	public String getLineType();
	public void setLineType(String type);
	
	/**
	 * 虚线、点线、点画线的间距
	 * @return
	 */
	public int getSpace();
	public void setSpace(int space);
}
