using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Engine.Event
{
    /**
 * 任务实例事件监听接口
 * @author 非也,nychen2000@163.com
 */
public interface ITaskInstanceEventListener {
    /**
     * 响应任务实例的事件。通过e.getEventType区分事件的类型。
     * 
     * @param e 任务实例的事件。
     * @throws EngineException
     */
    void onTaskInstanceEventFired(TaskInstanceEvent e);// throws EngineException;
}
}
