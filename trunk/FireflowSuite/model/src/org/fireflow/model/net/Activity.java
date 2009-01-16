/**
 * Copyright 2007-2008 陈乜云（非也,Chen Nieyun）
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.model.net;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.Task;
import org.fireflow.model.WorkflowProcess;

/**
 * @author chennieyun
 *
 */
public class Activity extends Node {

    public static final String ALL = "ALL";
    public static final String ANY = "ANY";
    private Transition enteringTransition;//输入弧	
    private Transition leavingTransition;//输出弧
    private List<Task> tasks = new ArrayList<Task>();
    private String completionStrategy = ALL;

    public Activity(){
        
    }
    public Activity(WorkflowProcess workflowProcess, String name) {
        super(workflowProcess, name);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public String getCompletionStrategy() {
        return completionStrategy;
    }

    public void setCompletionStrategy(String strategy) {
        this.completionStrategy = strategy;
    }

    public Transition getEnteringTransition() {
        return enteringTransition;
    }

    public void setEnteringTransition(Transition enteringTransition) {
        this.enteringTransition = enteringTransition;
    }

    public Transition getLeavingTransition() {
        return leavingTransition;
    }

    public void setLeavingTransition(Transition leavingTransition) {
        this.leavingTransition = leavingTransition;
    }
}
