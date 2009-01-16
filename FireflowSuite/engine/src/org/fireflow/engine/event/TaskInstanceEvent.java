/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.engine.event;

import org.fireflow.engine.ITaskInstance;

/**
 *
 * @author chennieyun
 */
public class TaskInstanceEvent {
    public static final int BEFORE_TASK_INSTANCE_START = 2;
    public static final int AFTER_TASK_INSTANCE_COMPLETE = 7;
    int eventType = -1;
    ITaskInstance source = null;

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public ITaskInstance getSource() {
        return source;
    }

    public void setSource(ITaskInstance source) {
        this.source = source;
    }
    
    
}
