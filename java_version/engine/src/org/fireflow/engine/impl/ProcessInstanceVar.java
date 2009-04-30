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
 * @author 非也,nychen2000@163.com
 */
public class ProcessInstanceVar {
    ProcessInstanceVarPk varPrimaryKey = null;
    Object value = null;

    public ProcessInstanceVarPk getVarPrimaryKey() {
        return varPrimaryKey;
    }

    public void setVarPrimaryKey(ProcessInstanceVarPk varPrimaryKey) {
        this.varPrimaryKey = varPrimaryKey;
    }



    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    

    public boolean equals(Object obj){
        if (this==obj) return true;
        if (!(obj instanceof ProcessInstanceVar))return false;
        ProcessInstanceVar var = (ProcessInstanceVar)obj;
        if (var.getVarPrimaryKey().equals(this.getVarPrimaryKey())){
            return true;
        }else{
            return false;
        }
    }
    
    public int hashCode(){
        if (this.getVarPrimaryKey()==null){
            return super.hashCode();
        }else{
            return this.getVarPrimaryKey().hashCode();
        }
    }
}
