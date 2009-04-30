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
package org.fireflow.kernel;

import java.util.Set;

import org.fireflow.engine.IProcessInstance;

/**
 * @author chennieyun
 *
 */
public interface IJoinPoint {
	public IProcessInstance getProcessInstance();
	public void setProcessInstance(IProcessInstance processInstance);
        public String getProcessInstanceId();
        public void setProcessInstanceId(String id);
	public Integer getValue();
	public void addValue(Integer v);
	public Boolean getAlive();
	public void setAlive(Boolean alive);
	public String getSynchronizerId() ;

	public void setSynchronizerId(String synchronizerId) ;

    public Integer getStepNumber();
    public void setStepNumber(Integer i);

    public String getFromActivityId();
    public void setFromActivityId(String s);
}
