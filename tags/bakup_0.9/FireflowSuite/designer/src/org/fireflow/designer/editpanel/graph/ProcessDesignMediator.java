/**
 * Copyright 2007-2008,Chen Nieyun��
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
package org.fireflow.designer.editpanel.graph;

import java.util.*;
import org.jgraph.graph.BasicMarqueeHandler;
import cn.bestsolution.tools.resourcesmanager.actions.ActionGroup;
import org.fireflow.designer.editpanel.graph.actions.*;

import cn.bestsolution.tools.resourcesmanager.util.ImageLoader;
import javax.swing.Action;
import org.fireflow.designer.util.WorkFlowDesignerI18N;

public class ProcessDesignMediator {

    private ProcessDesignPanel designPanel = null;
    private ProcessDesignToolBar toolBar = null;
    private ActionGroup processDesignActionGroup = null;
    private BasicMarqueeHandler originalMarqueeHandler = null;

    public BasicMarqueeHandler getOriginalMarqueeHandler() {
        return originalMarqueeHandler;
    }

    public void setOriginalMarqueeHandler(
            BasicMarqueeHandler originalMarqueeHandler) {

        this.originalMarqueeHandler = originalMarqueeHandler;
    }

    public ProcessDesignMediator() {

    }

    // public void setCurrentMarqueeHandler(BasicMarqueeHandler handler){
    // designPanel.setMarqueeHandler(handler);
    // }
    public void registProcessDesignPanel(ProcessDesignPanel panel) {
        designPanel = panel;
        originalMarqueeHandler = designPanel.getMarqueeHandler();
    }

    public void registProcessDesignToolBar(ProcessDesignToolBar argToolBar) {
        toolBar = argToolBar;
    }

    public ProcessDesignPanel getDesignPanel() {
        return designPanel;
    }

    public void refreshToolBar() {
        this.designPanel.setPortsVisible(false);
        this.designPanel.setMarqueeHandler(this.originalMarqueeHandler);
        toolBar.refreshButtonGroup();
    }

    public void mountActions() {

        initActions();

        ArrayList<Action> list = new ArrayList<Action>();
        list.add(processDesignActionGroup);
        toolBar.mountActions(list);


        refreshToolBar();
    }

    private void initActions() {
        processDesignActionGroup = new ActionGroup();
        SelectionAction selectAction = new SelectionAction(WorkFlowDesignerI18N.getString("selectiontip"), ImageLoader.getImageIcon("arrow16.gif"), this);
        processDesignActionGroup.addAction(selectAction);

        StartNodeMarqueeHandlerAction newStartNodeAction = new StartNodeMarqueeHandlerAction(
                "开始节点",
                ImageLoader.getImageIcon("startNode16.gif"), this);
        processDesignActionGroup.addAction(newStartNodeAction);

        ActivityWithFormMarqueeHandlerAction newNoImplActivityAction = new ActivityWithFormMarqueeHandlerAction(
                WorkFlowDesignerI18N.getString("noimplementationtip"),
                ImageLoader.getImageIcon("activity_manual_16.gif"), this);
        processDesignActionGroup.addAction(newNoImplActivityAction);

        ActivityWithSubflowMarqueeHandlerAction newActivityWithSubflowAction = new ActivityWithSubflowMarqueeHandlerAction(
                "子流程环节",
                ImageLoader.getImageIcon("activity_subflow_16.gif"), this);
        processDesignActionGroup.addAction(newActivityWithSubflowAction);

        ActivityWithToolMarqueeHandlerAction newActivityWithToolAction = new ActivityWithToolMarqueeHandlerAction(
                "工具环节",
                ImageLoader.getImageIcon("activity_tool_16.gif"), this);
        processDesignActionGroup.addAction(newActivityWithToolAction);


        ActivityMarqueeHandlerAction newActivityAction = new ActivityMarqueeHandlerAction(
                "环节",
                ImageLoader.getImageIcon("emptyactivity.gif"), this);
        processDesignActionGroup.addAction(newActivityAction);

        SynchronizerMarqueeHandlerAction newSynchronizerAction = new SynchronizerMarqueeHandlerAction(
                "同步器",
                ImageLoader.getImageIcon("synchronizer16.gif"), this);
        processDesignActionGroup.addAction(newSynchronizerAction);

        EndNodeMarqueeHandlerAction newEndNodeAction = new EndNodeMarqueeHandlerAction(
                "结束节点",
                ImageLoader.getImageIcon("endNode16.gif"), this);
        processDesignActionGroup.addAction(newEndNodeAction);


        TransitionMarqueeHandlerAction newTransitionAction = new TransitionMarqueeHandlerAction(
                "转移",
                ImageLoader.getImageIcon("transition16.gif"), this);
        processDesignActionGroup.addAction(newTransitionAction);
    }
}
