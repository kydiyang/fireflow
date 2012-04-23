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

import org.apache.commons.lang.StringUtils;
import org.fireflow.pdl.fpdl20.diagram.basic.Point;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class PointImpl implements Point {
	private int x = 0;
	private int y = 0;
	
	public PointImpl(int x,int y){
		this.x = x;
		this.y = y;
	}
	
	public PointImpl(){
		
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Point#getX()
	 */
	public int getX() {
		return x;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Point#setX(int)
	 */
	public void setX(int x) {
		this.x = x;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Point#getY()
	 */
	public int getY() {
		return y;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.basic.Point#setY(int)
	 */
	public void setY(int y) {
		this.y = y;

	}
	
	public String toString(){
		return "("+x+","+y+")";
	}
	
	/**
	 * 将格式为"(213,32)"的字符串转换为Point
	 * @param s
	 * @return
	 */
	public static Point fromString(String s){
		if (StringUtils.isEmpty(s)) return null;
		
		int index1 = s.indexOf(",");
		String x = s.substring(1,index1);
		
		String y = s.substring(index1+1, s.length()-1);
		
		return new PointImpl(Integer.parseInt(x),Integer.parseInt(y));
	}
}
