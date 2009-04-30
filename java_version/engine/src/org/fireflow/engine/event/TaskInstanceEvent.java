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
package org.fireflow.engine.event;

import org.fireflow.engine.ITaskInstance;

/**
 * 任务实例事件
 * @author 非也，nychen2000@163.com
 */
public class TaskInstanceEvent {
    /**
     * 在任务实例即将启动时触发的事件
     */
    public static final int BEFORE_TASK_INSTANCE_START = 2;
    
    /**
     * 在任务实例结束时触发的事件
     */
    public static final int AFTER_TASK_INSTANCE_COMPLETE = 7;
    int eventType = -1;
    ITaskInstance source = null;

    /**
     * 返回事件类型，取值为BEFORE_TASK_INSTANCE_START或者AFTER_TASK_INSTANCE_COMPLETE
     * @return
     */
    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    /**
     * 返回触发该事件的任务实例
     * @return
     */
    public ITaskInstance getSource() {
        return source;
    }

    public void setSource(ITaskInstance source) {
        this.source = source;
    }
    
    
}
