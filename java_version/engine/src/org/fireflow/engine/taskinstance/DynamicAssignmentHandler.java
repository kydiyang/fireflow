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
package org.fireflow.engine.taskinstance;

import org.fireflow.engine.taskinstance.IAssignmentHandler;
import java.util.List;
import org.fireflow.engine.EngineException;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.kernel.KernelException;

/**
 *
 * @author 非也,nychen2000@163.com
 */
public class DynamicAssignmentHandler implements IAssignmentHandler{
    boolean needClaim = false;
    List actorIdsList = null;
    
    public void assign(IAssignable asignable, String performerName) throws EngineException, KernelException {
        if (actorIdsList==null || actorIdsList.size()==0){
            TaskInstance taskInstance = (TaskInstance)asignable;
            throw new EngineException(taskInstance.getProcessInstanceId(),taskInstance.getWorkflowProcess(),
                    taskInstance.getTaskId(),"Actor id list can not be empty");
        }
        for (int i=0;i<actorIdsList.size();i++){
            asignable.asignToActor((String)actorIdsList.get(i), needClaim);
        }
    }

    public List getActorIdsList() {
        return actorIdsList;
    }

    public void setActorIdsList(List actorIdsList) {
        this.actorIdsList = actorIdsList;
    }

    public boolean isNeedClaim() {
        return needClaim;
    }

    public void setNeedClaim(boolean needSign) {
        this.needClaim = needSign;
    }

    
}
