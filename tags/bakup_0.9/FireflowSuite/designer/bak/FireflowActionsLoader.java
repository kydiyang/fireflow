/**
 * Copyright 2007-2008 ��ؿ�ƣ���Ҳ,Chen Nieyun��
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

//import org.fireflow.designer.actions.NewApplication;
import org.fireflow.designer.editpanel.graph.actions.NewDataField;
//import org.fireflow.designer.actions.NewParticipant;
import org.fireflow.designer.editpanel.graph.actions.NewWorkFlow;

import cn.bestsolution.tools.resourcesmanager.util.ImageLoader;
import org.fireflow.designer.util.WorkFlowDesignerI18N;
//import cn.bestsolution.tools.resourcesmanager.MainMediator;
import cn.bestsolution.tools.resourcesmanager.actions.ActionGroup;

/**
 * @author chennieyun
 * 
 */
public class FireflowActionsLoader{// implements IActionsLoader {

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.bestsolution.tools.resourcesmanager.actions.IActionsLoader#initActions(cn.bestsolution.tools.resourcesmanager.MainMediator)
	 */
	public void initActions() {//MainMediator mainMediator
		// TODO Auto-generated method stub
		ActionGroup workFlowActionGroup = new ActionGroup("������");// needI18N
		ActionGroup workFlowToolBarGroup = new ActionGroup();

		NewWorkFlow newWorkFlow = new NewWorkFlow(WorkFlowDesignerI18N
				.getString("newworkflowprocess"), ImageLoader
				.getImageIcon("workflowprocess16.gif"));
//		newWorkFlow.setToolTip(WorkFlowDesignerI18N
//				.getString("newworkflowprocesstip"));
		newWorkFlow.setEnabled(false);
		workFlowActionGroup.addAction(newWorkFlow);
		workFlowToolBarGroup.addAction(newWorkFlow);

//		NewParticipant newParticipant = new NewParticipant(WorkFlowDesignerI18N
//				.getString("newparticipant"), ImageLoader
//				.getImageIcon("participant16.gif"));
//		newParticipant.setToolTip(WorkFlowDesignerI18N
//				.getString("newparticipanttip"));
//		newParticipant.setEnabled(false);
//		workFlowActionGroup.addAction(newParticipant);
//		workFlowToolBarGroup.addAction(newParticipant);

//		NewApplication newApplication = new NewApplication(WorkFlowDesignerI18N
//				.getString("newapplication"), ImageLoader
//				.getImageIcon("application16.gif"));
//		newApplication.setToolTip(WorkFlowDesignerI18N
//				.getString("newapplicationtip"));
//		newApplication.setEnabled(false);
//		workFlowActionGroup.addAction(newApplication);
//		workFlowToolBarGroup.addAction(newApplication);

		NewDataField newDataField = new NewDataField(WorkFlowDesignerI18N
				.getString("newdatafield"), ImageLoader
				.getImageIcon("datafield16.gif"));
//		newDataField.setToolTip(WorkFlowDesignerI18N
//				.getString("newdatafieldtip"));
		newDataField.setEnabled(false);
		workFlowActionGroup.addAction(newDataField);
		workFlowToolBarGroup.addAction(newDataField);
		
		NewManualTask newManualTask = new NewManualTask("Manual Task", ImageLoader
				.getImageIcon("manual_task_16.gif"));
//		newManualTask.setToolTip("Manual Task");
		newManualTask.setEnabled(false);
		workFlowActionGroup.addAction(newManualTask);
		workFlowToolBarGroup.addAction(newManualTask);	
		
		NewToolTask newToolTask = new NewToolTask("Tool Task", ImageLoader
				.getImageIcon("tool_task_16.gif"));
//		newToolTask.setToolTip("Tool Task");
		newToolTask.setEnabled(false);
		workFlowActionGroup.addAction(newToolTask);
		workFlowToolBarGroup.addAction(newToolTask);	
		
		NewSubflowTask newSubflowTask = new NewSubflowTask("Subflow Task", ImageLoader
				.getImageIcon("subflow_task_16.gif"));
//		newSubflowTask.setToolTip("Subflow Task");
		newSubflowTask.setEnabled(false);
		workFlowActionGroup.addAction(newSubflowTask);
		workFlowToolBarGroup.addAction(newSubflowTask);		


		
//		mainMediator.getBusinessMenuGroupMap().put("fpdl", workFlowActionGroup);
//		mainMediator.getBusinessToolBarGroupMap().put("fpdl",
//				workFlowToolBarGroup);
	}

}
