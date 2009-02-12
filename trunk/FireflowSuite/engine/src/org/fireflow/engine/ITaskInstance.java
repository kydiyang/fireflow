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
package org.fireflow.engine;

import java.util.Date;

import org.fireflow.engine.ou.IAssignable;
import org.fireflow.kenel.KenelException;
import org.fireflow.model.Task;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;

/**
 * 任务实例
 * @author  非也,nychen2000@163.com
 *
 */
public interface ITaskInstance {
	public static final int INITIALIZED = 0;
	public static final int STARTED = 1;
	public static final int COMPLETED = 2;
	public static final int CANCELED = -1;
        

        /**
         * 返回任务实例的Id
         * @return
         */
	public String getId();
        
        /**
         * 返回对应的任务Id
         * @return
         */
	public String getTaskId();
        
        /**
         * 返回任务Name
         * @return
         */
	public String getName();
        
        /**
         * 返回任务显示名
         * @return
         */
	public String getDisplayName();
//	public IProcessInstance getProcessInstance();
        
        /**
         * 返回对应的流程实例Id
         * @return
         */
        public String getProcessInstanceId();
        
        /**
         * 返回对应的流程的Id
         * @return
         */
        public String getProcessId();
        
        /**
         * 返回流程的版本
         * @return
         */
        public Integer getVersion();
        
        /**
         * 返回任务实例创建的时间
         * @return
         */
	public Date getCreatedTime();
        
        /**
         * 返回任务实例启动的时间
         * @return
         */
	public Date getStartedTime();
        
        /**
         * 返回任务实例结束的时间
         * @return
         */
	public Date getEndTime();
        
        /**
         * 返回任务实例到期日期
         * @return
         */
	public Date getExpiredTime();//过期时间
        
        /**
         * 返回任务实例的状态，取值为：INITIALIZED(已初始化），STARTED(已启动),COMPLETED(已结束),CANCELD(被取消)
         * @return
         */
	public Integer getState();
        
        /**
         * 返回任务实例的分配策略，取值为 org.fireflow.model.Task.ALL或者org.fireflow.model.Task.ANY
         * @return
         */
	public String getAssignmentStrategy();
        
        /**
         * 返回任务实例所属的环节的Id
         * @return
         */
        public String getActivityId();
        
        /**
         * 返回任务类型，取值为org.fireflow.model.Task.FORM,org.fireflow.model.Task.TOOL,
         * org.fireflow.model.Task.SUBFLOW或者org.fireflow.model.Task.DUMMY
         * @return
         */
        public String getTaskType();
        
        /**
         * 取消该任务（保留，未实现）
         * @throws org.fireflow.engine.EngineException
         * @throws org.fireflow.kenel.KenelException
         */
        public void abort() throws EngineException,KenelException;	
        
        /**
         * 返回任务是里对应的环节
         * @return
         * @throws org.fireflow.engine.EngineException
         */
        public Activity getActivity() throws EngineException;
        
        /**
         * 返回任务实例对应的流程
         * @return
         * @throws org.fireflow.engine.EngineException
         */
        public WorkflowProcess getWorkflowProcess() throws EngineException;
        
        /**
         * 返回任务实例对应的Task对象
         * @return
         * @throws org.fireflow.engine.EngineException
         */
	public Task getTask() throws EngineException;        
//	public Set getWorkItems() ;	
}
