/*
 * Copyright 2007-2009 非也
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

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */

package org.fireflow.kernel;

/**
 *
 * @author app
 * @version 1.0
 * Created on Mar 18, 2009
 */
public interface IEdgeInstance {
	public String getId();
	/**
	 * 弧的权
	 * @return
	 */
	public int getWeight();
//	public void setWeight(int i);


	public INodeInstance getLeavingNodeInstance();

	public void setLeavingNodeInstance(INodeInstance nodeInst);

	public INodeInstance getEnteringNodeInstance();

	public void setEnteringNodeInstance(INodeInstance nodeInst);


        /**
         * 接受一个token，并移交给下一个节点
         * @param token
         * @return 返回值是该transition计算出的token的alive值
         * @throws org.fireflow.kenel.KenelException
         */
	public boolean take(IToken token)throws KernelException;
}
