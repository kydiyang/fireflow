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
package org.fireflow.model;

import org.fireflow.model.net.Activity;


/**
 * 工作流任务
 * @author 非也,nychen2000@163.com
 */
public abstract class Task extends AbstractWFElement {
    /**
     * 任务类型之一 ：TOOL类型，即工具类型任务，该任务自动调用java代码完成特定的工作。
     */
    public static final String TOOL = "TOOL";
    
    /**
     * 任务类型之三：SUBFLOW类型，即子流程任务
     */
    public static final String SUBFLOW = "SUBFLOW";
    
    /**
     * 任务类型之一：FORM类型，最常见的一类任务，代表该任务需要操作员填写相关的表单。
     */
    public static final String FORM = "FORM";
    
    /**
     * 任务类型之四：DUMMY类型，该类型暂时没有用到，保留。
     */
    public static final String DUMMY = "DUMMY";

    /**
     * 循环情况下，任务分配指示之一：重做<br>
     * 对于Tool类型和Subflow类型的task会重新执行一遍
     * 对于Form类型的Task，重新执行一遍，且将该任务实例分配给最近一次完成同一任务的操作员。
     */
    public static final String REDO = "REDO";

    /**
     * 循环情况下，任务分配指示之二：重做<br>
     * 循环的情况下该任务将被忽略
     */
    public static final String SKIP = "SKIP";

    /**
     * 循环的情况下，任务分配指示之三：无<br>
     * 对于Tool类型和Subflow类型的task会重新执行一遍
     * 对于Form类型的Task，重新执行一遍，且工作流引擎仍然调用Performer属性设置的handler分配任务
     */
    public static final String NONE = "NONE";
    
    /**
     * 任务类型,取值为FORM,TOOL,SUBFLOW,DUMMY(保留)，缺省值为FORM
     */
    protected String type = FORM;//

    protected Duration duration;


    protected int priority = 1;
    /**
     * 循环情况下，任务分配指示，取值为REDO和NONE
     */
    protected String loopStrategy = REDO;//


    /**
     * 业务子系统定制的任务实例创建器，如果没有设置该Creator，系统将已默认的方式创建流程实例。默认的方式是创建org.fireflow.engine.impl.TaskInstance实力。
     */
    protected String taskInstanceCreator = null;

    /**
     * 任务实例的运行器，如果没有设置runner，则按照默认方式运行，默认运行规则如下：<br>
     * 1、对于FormTask，然后分配工单(WorkItem)<br>
     * 2、对于ToolTask，执行applicationHandler实例<br>
     * 3、对于SubflowTask，创建一个对应的子流程实例<br>
     */
    protected String taskInstanceRunner = null;

    /**
     * 业务子系统定制的任务实例是否可终结评价器。如果没有设置，系统采用默认的规则评价任务实例是否可以结束。规则如下：<br>
     * 1、对于FormTask，检查其是否有活动的WorkItem，如果没有，则可以结束，否则不可以结束。<br>
     * 2、对于ToolTask,applicationHandler调用返回即可结束。<br>
     * 3、对于SubflowTask，因为绝大多数情况下只需要创建一个子流程实例，所以只要子流程实例结束，则SubflowTask实例也结束。所以
     * 对于创建了“并发子流程”的SubflowTask，业务子系统必须自行实现一个taskInstanceEndableEvaluator以检查是否可以终结父TaskInstance。
     *
     */
    protected String taskInstanceCompletionEvaluator = null;
    
    public Task() {
    }

    public Task(IWFElement parent, String name) {
        super(parent, name);
    }
    /**
     * 返回任务的优先级。（引擎暂时没有用到）
     * @return
     */
    public int getPriority() {
        return priority;
    }

    /**
     * 设置任务的优先级。（引擎暂时没有用到）
     * @param priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String toString() {
        return "Task[id='" + getId() + ", name='" + getName() + "']";
    }

    /**
     * 返回任务的类型
     * @return 任务类型，FORM,TOOL,SUBFLOW或者DUMMY
     */
    public String getType() {
        return type;
    }

    /**
     * 设置任务类型，
     * @param taskType 任务类型，FORM,TOOL,SUBFLOW或者DUMMY
     */
    public void setType(String taskType) {
        this.type = taskType;
    }
    /**
     * 返回任务的完成期限
     * @return 以Duration表示的任务的完成期限
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * 设置任务的完成期限
     * @param limit
     */
    public void setDuration(Duration limit) {
        this.duration = limit;
    }

    public String getTaskInstanceCreator() {
        return taskInstanceCreator;
    }

    public void setTaskInstanceCreator(String taskInstanceCreator) {
        this.taskInstanceCreator = taskInstanceCreator;
    }

    public String getTaskInstanceCompletionEvaluator() {
        return taskInstanceCompletionEvaluator;
    }

    public void setTaskInstanceCompletionEvaluator(String taskInstanceCompletionEvaluator) {
        this.taskInstanceCompletionEvaluator = taskInstanceCompletionEvaluator;
    }


    public String getTaskInstanceRunner() {
        return taskInstanceRunner;
    }

    public void setTaskInstanceRunner(String taskInstanceRunner) {
        this.taskInstanceRunner = taskInstanceRunner;
    }

    public String getLoopStrategy() {
        return loopStrategy;
    }

    public void setLoopStrategy(String loopStrategy) {
        this.loopStrategy = loopStrategy;
    }
}