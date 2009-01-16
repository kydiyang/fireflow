/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.engine.ou;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.fireflow.engine.EngineException;
import org.fireflow.kenel.KenelException;

/**
 *
 * @author chennieyun
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
