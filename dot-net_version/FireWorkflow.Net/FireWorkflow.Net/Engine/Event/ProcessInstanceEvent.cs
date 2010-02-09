using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Engine.Event
{
    /**
 * 流程实例事件
 * @author 非也,nychen2000@163.com
 *
 */
public class ProcessInstanceEvent {

    /**
     * 在即将启动流程实例的时候触发的事件
     */
    public const int BEFORE_PROCESS_INSTANCE_RUN = 2;
    
    /**
     * 在流程实例结束后触发的事件
     */
    public const int AFTER_PROCESS_INSTANCE_COMPLETE = 7;
    int eventType = -1;
    IProcessInstance source = null;

    /**
     * 返回触发事件的流程实例
     * @return
     */
    public IProcessInstance getSource() {
        return source;
    }

    public void setSource(IProcessInstance source) {
        this.source = source;
    }

    /**
     * 返回事件类型，取值为BEFORE_PROCESS_INSTANCE_RUN或者AFTER_PROCESS_INSTANCE_COMPLETE
     * @return
     */
    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
}
}
