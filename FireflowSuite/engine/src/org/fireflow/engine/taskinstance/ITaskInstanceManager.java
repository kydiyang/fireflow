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
package org.fireflow.engine.taskinstance;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.kenel.IActivityInstance;
import org.fireflow.kenel.IToken;
import org.fireflow.kenel.KenelException;
/**
 * @author chennieyun
 *
 */
public interface ITaskInstanceManager extends IRuntimeContextAware{
	/**
	 * 创建taskinstance实例
	 * @param token
	 * @param activityInstance
	 * @throws EngineException
	 */
	public void createTaskInstances(IToken token,IActivityInstance activityInstance)throws EngineException,KenelException;
	
	/**
	 * 将已经完成的taskinstance实例转移到已办表
	 * @param activityInstance
	 * @throws EngineException
	 */
	public void archiveTaskInstances(IActivityInstance activityInstance)throws EngineException,KenelException;
}
