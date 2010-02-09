using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Engine.Event
{
    /**
 * 任务实例事件
 * @author 非也，nychen2000@163.com
 */
public class TaskInstanceEvent {
    /**
     * 在任务实例即将启动时触发的事件
     */
    public const int BEFORE_TASK_INSTANCE_START = 2;
    
    /**
     * 当创建工作项之后
     */
    public const int AFTER_WORKITEM_CREATED = 5;
    
    /**
     * 在任务实例结束时触发的事件
     */
    public const int AFTER_TASK_INSTANCE_COMPLETE = 7;
    int eventType = -1;
    ITaskInstance source = null;
    IWorkflowSession workflowSession = null;
    IProcessInstance processInstance = null;
    IWorkItem workItem = null;

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

	public IWorkItem getWorkItem() {
		return workItem;
	}

	public void setWorkItem(IWorkItem workItem) {
		this.workItem = workItem;
	}

	public IWorkflowSession getWorkflowSession() {
		return workflowSession;
	}

	public void setWorkflowSession(IWorkflowSession workflowSession) {
		this.workflowSession = workflowSession;
	}

	public IProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(IProcessInstance processInstance) {
		this.processInstance = processInstance;
	}
    
    
}
}
