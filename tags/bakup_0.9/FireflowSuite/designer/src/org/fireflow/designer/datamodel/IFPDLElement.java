/**
 * Copyright 2007-2008 陈乜云,Chen Nieyun��
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
package org.fireflow.designer.datamodel;

import java.awt.Component;
import java.util.Map;
/**
 * @author chennieyun
 *
 */
public interface IFPDLElement {
	   public static final String PACKAGE = "PACKAGE";
	   public static final String ACTIVITY = "ACTIVITY";
	   public static final String ACTIVITIES = "ACTIVITIES";
	   public static final String WORKFLOWPROCESSES = "WORKFLOWPROCESSES";
	   public static final String WORKFLOWPROCESS = "WORKFLOWPROCESS";
	   public static final String APPLICATIONS = "APPLICATIONS";
	   public static final String APPLICATION = "APPLICATION";
	   public static final String PARTICIPANTS = "PARTICIPANTS";
	   public static final String PARTICIPANT = "PARTICIPANT";
	   public static final String DATAFIELDS = "DATAFIELDS";
	   public static final String DATAFIELD = "DATAFIELD";
	   public static final String FORMALPARAMETERS = "FORMALPARAMETERS";
	   public static final String FORMALPARAMETER = "FORMALPARAMETER";
	   public static final String FPDL_GRAPH_NODE = "FPDL_GRAPH_NODE";
	   public static final String START_NODE = "START_NODE";
	   public static final String END_NODE = "END_NODE";
           public static final String END_NODES = "END_NODES";
	   public static final String SYNCHRONIZER = "SYNCHRONIZER";
           public static final String SYNCHRONIZERS = "SYNCHRONIZERS";
	   public static final String TRANSITIONS = "TRANSITIONS";
	   public static final String TRANSITION = "TRANSITION";
	   
	   public static final String FORM_TASK = "FORM_TASK";
	   public static final String SUBFLOW_TASK = "SUBFLOW_TASK";
	   public static final String TOOL_TASK = "TOOL_TASK";
	   
	   public Component getEditor();//�������Ա༭����

//	   public void addContentChangeListner(IContentChangeListener listener);
//	   public List<IContentChangeListener> getContentChangeListenerList();
	   public String getName();
           public String getDisplayName();
           public String getElementType();//
//	   public String toString();
	   
	   public IFPDLElement createChild(String childType,Map<String,String> extendAtributes);
	   public void deleteSelf();
//	   public void deleteChild(IFPDLElement child);
	   public Object getContent();
//	   public List<IFPDLElement> getChildren();
//	   public Component getDiagram();
//	   public void setDiagram(Component argDiagram);
          
}
