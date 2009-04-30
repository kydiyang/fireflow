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
package org.fireflow.kernel;

import java.util.List;
import java.util.Set;

import org.fireflow.engine.IProcessInstance;
/**
 * 
 * token的生命周期开始与一个synchronizer（包括startnode 和 endnode)，结束于另一个synchronizer
 *
 * @author 非也
 *
 */
public interface IToken {

    public static final String FROM_ACTIVITY_ID_SEPARATOR = "&";
    public static final String FROM_START_NODE = "FROM_START_NODE";
	//token因该没有和外部运行环境(RuntimeContext)沟通的职责
//	public void setRuntimeContext(IRuntimeContext rtCtx);
//	public IRuntimeContext getRuntimeContext();
	
	public IProcessInstance getProcessInstance();
	public void setProcessInstance(IProcessInstance inst);
        
        public void setProcessInstanceId(String id);
        public String getProcessInstanceId();
	
//	public INetInstance getNetInstance();
//	public void setNetInstance(INetInstance netInst); 
	
//	public INodeInstance getCurrentNodeInstance();
//	public void setCurrentNodeInstance(INodeInstance currentNodeInstance);
//	
	public String getNodeId();
	public void setNodeId(String nodeId);
	
//	public void setTransitionInstance(ITransitionInstance transInst);
//	public ITransitionInstance getTransitionInstance();
	
	public void setValue(Integer v);
	
	public Integer getValue();
	
	//通过alive标志来判断nodeinstance是否要fire
	public Boolean isAlive();
	
	public void setAlive(Boolean b);
	
//	public void setAppointedTransitionNames(Set<String> appointedTransitionNames);
//	
//	public Set<String> getAppointedTransitionNames();
	
	public String getId();
	public void setId(String id);

    public Integer getStepNumber();

    public void setStepNumber(Integer i);

    /**
     * 获得前驱Activity的Id,如果有多个，则用"&"分割
     * @return
     */
    public String getFromActivityId();
    public void setFromActivityId(String s);
}
