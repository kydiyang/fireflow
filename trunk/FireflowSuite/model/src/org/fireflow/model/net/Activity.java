/**
 * Copyright 2007-2008 非也
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
 * @author 非也,nychen2000@163.com
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

    /**
     * 返回该环节所有的任务
     * @return
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * 返回环节的结束策略，取值为ALL或者ANY，缺省值为ALL<br>
     * 如果取值为ALL,则只有其所有任务实例结束了，环节实例才可以结束。<br>
     * 如果取值为ANY，则只要任何一个任务实例结束后，环节实例就可以结束。
     * 环节实例的结束操作仅执行一遍，因此后续任务实例的结束不会触发环节实例的结束操作再次执行。
     * @return
     */
    public String getCompletionStrategy() {
        return completionStrategy;
    }

    public void setCompletionStrategy(String strategy) {
        this.completionStrategy = strategy;
    }

    /**
     * 返回环节的输入Transition。一个环节有且只有一个输入Transition
     * @return 转移
     */
    public Transition getEnteringTransition() {
        return enteringTransition;
    }

    public void setEnteringTransition(Transition enteringTransition) {
        this.enteringTransition = enteringTransition;
    }

    /**
     * 返回环节的输出Transition。一个环节有且只有一个输出Transition
     * @return
     */
    public Transition getLeavingTransition() {
        return leavingTransition;
    }

    public void setLeavingTransition(Transition leavingTransition) {
        this.leavingTransition = leavingTransition;
    }
}
