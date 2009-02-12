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
package org.fireflow.engine.ou;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.fireflow.engine.EngineException;
import org.fireflow.kenel.KenelException;

/**
 *
 * @author 非也,nychen2000@163.com
 */
public class DynamicAssignmentHandler implements IAssignmentHandler{
    boolean needSign = false;
    List actorIdsList = null;
    
    public void assign(IAssignable asignable, String performerName) throws EngineException, KenelException {
        if (actorIdsList==null || actorIdsList.size()==0)throw new EngineException("Actor id list can not be empty");
        for (int i=0;i<actorIdsList.size();i++){
            asignable.asignToActor((String)actorIdsList.get(i), needSign);
        }
    }

    public List getActorIdsList() {
        return actorIdsList;
    }

    public void setActorIdsList(List actorIdsList) {
        this.actorIdsList = actorIdsList;
    }

    public boolean isNeedSign() {
        return needSign;
    }

    public void setNeedSign(boolean needSign) {
        this.needSign = needSign;
    }

    
}
