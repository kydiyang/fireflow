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

import org.fireflow.pdl.fpdl20.diagram.AssociationShape;
import org.fireflow.pdl.fpdl20.diagram.DiagramElement;
import org.fireflow.pdl.fpdl20.diagram.basic.Label;
import org.fireflow.pdl.fpdl20.diagram.basic.Line;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.LabelImpl;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.LineImpl;
import org.fireflow.pdl.fpdl20.diagram.style.LineStyle;
import org.fireflow.pdl.fpdl20.diagram.style.impl.LineStyleImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class AssociationShapeImpl extends AbsDiagramElement implements
		AssociationShape {
	DiagramElement fromDiagramElement = null;
	DiagramElement toDiagramElement = null;

	public AssociationShapeImpl(String id){
		this.id = id;
		
		LineImpl line = new LineImpl();
		LineStyleImpl style = new LineStyleImpl();
		style.setLineType(LineStyle.LINETYPE_DASHED);
		line.setLineStyle(style);
		
		Label lb = new LabelImpl();
		line.setLabel(lb);
		
		this.shape = line;
	}
	
	public void setLabel(String lb){
		((Line)this.shape).getLabel().setText(lb);
	}
	public String getLabel(){
		return ((Line)this.shape).getLabel().getText();
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.AssociationShape#getFromDiagramElement()
	 */
	public DiagramElement getFromDiagramElement() {
		return fromDiagramElement;
	}
	
	public void setFromDiagramElement(DiagramElement from){
		this.fromDiagramElement = from;
	}
	
	public void setToDiagramElement(DiagramElement to){
		this.toDiagramElement = to;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.AssociationShape#getToDiagramElement()
	 */
	public DiagramElement getToDiagramElement() {
		return toDiagramElement;
	}

}
