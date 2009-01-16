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
package org.fireflow.engine.kenelextensions;

import org.fireflow.kenel.impl.StartNodeInstance;;

/**
 * @author chennieyun
 *
 */
public class StartNodeInstanceExtension extends SynchronizerInstanceExtension {
    
	public String getExtentionPointName() {
		// TODO Auto-generated method stub
		return StartNodeInstance.Extension_Point_NodeInstanceEventListener;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.kenel.plugin.IKenelExtension#getExtentionTargetName()
	 */
	public String getExtentionTargetName() {
		// TODO Auto-generated method stub
		return StartNodeInstance.Extension_Target_Name;
	}
}
