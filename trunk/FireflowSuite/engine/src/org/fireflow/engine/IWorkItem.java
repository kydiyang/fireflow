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

import java.util.List;

import org.fireflow.kenel.KenelException;

/**
 * @author chennieyun
 *
 */
public interface IWorkItem {
	public static final int INITIALIZED = 0;
	public static final int STARTED = 1;
	public static final int COMPLETED = 2;
	public static final int CANCELED = -1;
	
	public String getId();
	public Integer getState();
	public void setComments(String s);
	public String getComments();
	public void accept()throws  EngineException,KenelException;
	public void complete()throws  EngineException,KenelException;
	public void complete(List<String> nextTransitionNames)throws  EngineException,KenelException;
	public void complete(String nextTransitionName,List<String> nextActorIds,boolean asignToEveryone)throws  EngineException,KenelException;
//	public void setTaskInstance(ITaskInstance taskInstance);
	public ITaskInstance getTaskInstance();
	public void reasign(String actorId);
//	public boolean isAsignToEveryone();
//	public void setAsignToEveryone(boolean b);
	public String getActorId() ;

	public void setActorId(String actorId);
}
