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
package org.fireflow.engine.impl;

/**
 * @deprecated 
 * @author chennieyun
 */
public class ProcessInstanceVarPk {
    String processInstanceId = null;
    String name = null;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public boolean equals(Object obj){
        if (obj==this)return true;
        if (!(obj instanceof ProcessInstanceVarPk))return false;
        ProcessInstanceVarPk var = (ProcessInstanceVarPk)obj;
        if (var.getProcessInstanceId()!=null && var.getProcessInstanceId().equals(this.processInstanceId)
                && var.getName()!=null && var.getName().equals(this.name)
                ){
            return true;
        }else{
            return false;
        }
    }
    
    public int hashCode(){
        String compositeId = this.name+this.processInstanceId;
        if (compositeId==null){
            return super.hashCode();
        }else{
            return (compositeId).hashCode();
        }
    }    
}
