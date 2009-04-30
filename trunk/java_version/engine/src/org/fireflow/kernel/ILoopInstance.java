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

import org.fireflow.model.net.Loop;

/**
 *
 * @author app
 * @version 1.0
 * Created on Mar 18, 2009
 */
public interface ILoopInstance extends IEdgeInstance{
    public Loop getLoop();

    /**
     * 计算循环条件的值，如果计算结果为true则执行该循环，否则不执行循环。<br>
     * 如果循环条件表达式为null或者""值则其结果定义为false。
     * @return
     */
//    public boolean evaluateCondition();
}
