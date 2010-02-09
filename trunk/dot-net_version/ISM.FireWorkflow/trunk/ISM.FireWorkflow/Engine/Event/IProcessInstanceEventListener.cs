using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISM.FireWorkflow.Engine.Event
{
    /**
 * 流程实例事件监听接口
 * @author 非也,nychen2000@163.com
 *
 */
public interface IProcessInstanceEventListener {
	/**
	 * 响应流程实例的事件。通过e.getEventType来判断事件的类型。
	 * 流程实例有两种事件：<br/>
	 * BEFORE_PROCESS_INSTANCE_RUN (= 2):在即将启动流程实例的时候触发的事件<br/>
	 * AFTER_PROCESS_INSTANCE_COMPLETE (= 7):在流程实例结束后触发的事件
	 * @param e 流程实例事件
	 * @throws EngineException
	 */
    void onProcessInstanceEventFired(ProcessInstanceEvent e);// throws EngineException;
}
}
