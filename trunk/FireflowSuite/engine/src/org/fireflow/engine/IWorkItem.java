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
package org.fireflow.engine;

import java.util.Date;
import java.util.List;

import org.fireflow.kenel.KenelException;

/**
 * @author chennieyun
 *
 */
public interface IWorkItem{

    public static final int INITIALIZED = 0;
    public static final int STARTED = 1;
    public static final int COMPLETED = 2;
    public static final int CANCELED = -1;

    public String getId();

    public Integer getState();

    public void setComments(String s);

    public String getComments();

    public Date getCreatedTime();

    public Date getSignedTime();

    public Date getEndTime();

    public String getActorId();

    public ITaskInstance getTaskInstance();

    public void sign() throws EngineException, KenelException;
    
    /**
     * 结束当前WorkItem；并由工作流引擎根据流程定义决定下一步操作。引擎的执行规则如下<br/>
     * 1、工作流引擎首先判断该WorkItem对应的TaskInstance是否可以结束。
     * 如果TaskInstance的assignment策略为ANY，或者，assignment策略为ALL且它所有的WorkItem都已经完成
     * 则结束当前TaskInstance<br/>
     * 2、判断TaskInstance对应的ActivityInstance是否可以结束。如果ActivityInstance的complete strategy
     * 为ANY，或者，complete strategy为ALL且他的所有的TaskInstance都已经结束，则结束当前ActivityInstance<br/>
     * 3、根据流程定义，启动下一个Activity，并创建相关的TaskInstance和WorkItem
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kenel.KenelException
     */
    public void complete() throws EngineException, KenelException;

    /**
     * 结束当前WorkItem，并启动下一个Activity及其Task，将新创建的TaskInstance分配给nextActorIds中的所有Actor。<br/>
     * 该方法只有在如下条件下才能正确执行，否则引擎抛出EngineException，流程状态恢复到调用该方法前的状态。<br/>
     * 1)当前Activity和下一个Activity在同一个“执行线”上<br/>
     * 2)下一个Activity有且只有一个Form类型的Task，其他类型的Task不限定。<br/>
     * 3)如果当前Task的assignment为Task.ALL且本WorkItem结束后仍然不能使得TaskInstance结束，引擎将抛出EngineException异常<br/>
     * 4)如果当前的Activity包含多个TaskInstance，且当前TaskInstance结束后ActivityInstance仍然不能结束，引擎将抛出EngineException异常
     * 
     * 一句话，当前Acitivity和下一个Activity组成一个“简单流程”的情况下，才可以结束当前Activity，并指定下一个Activity的操作人。<br/>
     * @param nextActorIds 指定的下一个Activity操作者ID列表
     * @throws org.fireflow.engine.EngineException 
     * @throws org.fireflow.kenel.KenelException
     */
    public void junpToNextActivity(List<String> nextActorIds) throws EngineException, KenelException;

    /**
     * 结束当前WorkItem，并启动下一个Activity及其Task，将新创建的TaskInstance分配给nextActorIds中的所有Actor。<br/>
     * 该方法只有在如下条件下才能正确执行，否则引擎抛出EngineException，流程状态恢复到调用该方法前的状态。<br/>
     * 1)当前Activity和下一个Activity在同一个“执行线”上<br/>
     * 2)如果当前Task的assignment为Task.ALL且本WorkItem结束后仍然不能使得TaskInstance结束，引擎将抛出EngineException异常<br/>
     * 3)如果当前的Activity包含多个TaskInstance，且当前TaskInstance结束后ActivityInstance仍然不能结束，引擎将抛出EngineException异常
     * 
     * 一句话，当前Acitivity和下一个Activity组成一个“简单流程”的情况下，才可以结束当前Activity，并指定下一个Activity的操作人。<br/>
     * @param nextActorIds
     * @param needSign 是否需要签收
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kenel.KenelException
     */
    public void junpToNextActivity(List<String> nextActorIds,boolean needSign) throws EngineException, KenelException;

    /**
     * 结束当前WorkItem,启动指定的Activity，引擎调用流程设计时指定的AssignmentHandler分配任务。<br/>
     * 只有满足如下条件的情况下，该方法才能成功执行，否则抛出EngineException，流程状态恢复到调用该方法之前的状态。<br/>
     * 1)当前Activity和即将启动的Acitivty必须在同一个执行线上<br/>
     * 2)如果当前Task的assignment为Task.ALL且本WorkItem结束后仍然不能使得TaskInstance结束，引擎将抛出EngineException异常<br/>
     * 3)如果当前的Activity包含多个TaskInstance，且当前TaskInstance结束后ActivityInstance仍然不能结束，引擎将抛出EngineException异常
     * @param activityId 将要被启动的ActivityId
     * @throws org.fireflow.engine.EngineException 
     * @throws org.fireflow.kenel.KenelException
     */
    public void jumpTo(String activityId) throws EngineException, KenelException;

    /**
     * 结束当前WorkItem，启动指定的Activity，引擎将新的TaskInstance分配给nextActorIds<br/>
     * 只有满足如下条件的情况下，该方法才能成功执行，否则抛出EngineException，流程状态恢复到调用该方法之前的状态。<br/>
     * 1)当前Activity和即将启动的Acitivty必须在同一个执行线上<br/>
     * 2)如果当前Task的assignment为Task.ALL且本WorkItem结束后仍然不能使得TaskInstance结束，引擎将抛出EngineException异常<br/>
     * 3)如果当前的Activity包含多个TaskInstance，且当前TaskInstance结束后ActivityInstance仍然不能结束，引擎将抛出EngineException异常
     * @param activityId 将要被启动的ActivityId
     * @param nextActorIds 将新的TaskInstance分配给nextActorIds列表中的Actor
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kenel.KenelException
     */
    public void jumpTo(String activityId, List<String> nextActorIds) throws EngineException, KenelException;

    /**
     * 结束当前WorkItem，启动指定的Activity，引擎将新的TaskInstance分配给nextActorIds<br/>
     * 只有满足如下条件的情况下，该方法才能成功执行，否则抛出EngineException，流程状态恢复到调用该方法之前的状态。<br/>
     * 1)当前Activity和即将启动的Acitivty必须在同一个执行线上<br/>
     * 2)如果当前Task的assignment为Task.ALL且本WorkItem结束后仍然不能使得TaskInstance结束，引擎将抛出EngineException异常<br/>
     * 3)如果当前的Activity包含多个TaskInstance，且当前TaskInstance结束后ActivityInstance仍然不能结束，引擎将抛出EngineException异常
     * @param activityId
     * @param nextActorIds
     * @param needSign 是否需要签收
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kenel.KenelException
     */
    public void jumpTo(String activityId, List<String> nextActorIds,boolean needSign) throws EngineException, KenelException;
    
    
    
    /**
     * 结束当前WorkItem，启动指定的Acitvity，引擎将新的TaskInstance分配给上一次完成该Activity的操作者。</br>
     * 只有满足如下条件的情况下，该方法才能成功执行，否则抛出EngineException，流程状态恢复到调用该方法之前的状态。<br/>
     * 1)当前Activity和即将启动的Acitivty必须在同一个执行线上<br/>
     * 2）即将启动的Activity已经被执行过
     * 3)如果当前Task的assignment为Task.ALL且本WorkItem结束后仍然不能使得TaskInstance结束，引擎将抛出EngineException异常<br/>
     * 4)如果当前的Activity包含多个TaskInstance，且当前TaskInstance结束后ActivityInstance仍然不能结束，引擎将抛出EngineException异常
     * @param activityId
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kenel.KenelException
     */
    public void loopTo(String activityId) throws EngineException, KenelException;
    
    public void reasignTo(String actorId);
}
