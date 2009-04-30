/**
 * Copyright 2007-2008 ,Chen Nieyun
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation��
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.designer.editpanel.graph.actions;

import org.fireflow.designer.editpanel.graph.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.designer.util.DesignerConstant;
import org.fireflow.model.ExtendedAttributeNames;
import org.jgraph.graph.BasicMarqueeHandler;


public class SynchronizerMarqueeHandler extends BasicMarqueeHandler {
	private ProcessDesignPanel designPanel = null;
	private ProcessDesignMediator mediator = null;

	public SynchronizerMarqueeHandler(ProcessDesignMediator md,
			ProcessDesignPanel designPanel) {
		mediator = md;
		this.designPanel = designPanel;
	}

	public boolean isForceMarqueeEvent(MouseEvent event) {
		return true;
	}

	public void mousePressed(MouseEvent mouseEvent) {

		if (mouseEvent.getButton() == mouseEvent.BUTTON3) {
			mediator.refreshToolBar();
			return;
		}

		// ��������
		Map<String,String> attributeMap = new HashMap<String,String>();
		attributeMap.put(ExtendedAttributeNames.BOUNDS_X,
				Integer.toString(mouseEvent.getPoint().x));
		attributeMap.put(ExtendedAttributeNames.BOUNDS_Y,
				Integer.toString(mouseEvent.getPoint().y));
		attributeMap.put(ExtendedAttributeNames.BOUNDS_WIDTH, DesignerConstant.DEFAULT_SYNCHRONIZER_RADIUS);
		attributeMap.put(ExtendedAttributeNames.BOUNDS_HEIGHT, DesignerConstant.DEFAULT_SYNCHRONIZER_RADIUS);

		ProcessGraphModel graphModel = (ProcessGraphModel) designPanel
				.getModel();

                IFPDLElement newElement = graphModel.getWorkflowProcessElement().createChild(IFPDLElement.SYNCHRONIZER, attributeMap);
	}
}
