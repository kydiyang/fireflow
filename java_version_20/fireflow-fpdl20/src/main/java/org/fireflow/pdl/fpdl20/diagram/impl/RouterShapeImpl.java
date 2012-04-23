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
package org.fireflow.pdl.fpdl20.diagram.impl;

import org.fireflow.pdl.fpdl20.diagram.RouterShape;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.BoundsImpl;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.PointImpl;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.RectangleImpl;
import org.fireflow.pdl.fpdl20.diagram.style.impl.BoundsStyleImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class RouterShapeImpl extends AbsDiagramElement implements RouterShape {
	public RouterShapeImpl(String id){
		this.id = id;
		
		RectangleImpl rect = new RectangleImpl();
		
		BoundsStyleImpl boundsStyle = new BoundsStyleImpl();
		
		BoundsImpl bounds = new BoundsImpl();
		bounds.setBoundsStyle(boundsStyle);
		bounds.setUpperLeftCorner(new PointImpl(100,100));
		bounds.setWidth(35);
		bounds.setHeight(35);
		
		rect.setBounds(bounds);
	}
}
