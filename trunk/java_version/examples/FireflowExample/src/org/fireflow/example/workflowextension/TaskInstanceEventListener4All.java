package org.fireflow.example.workflowextension;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.taskinstance.DefaultTaskInstanceEventListener;

public class TaskInstanceEventListener4All extends
		DefaultTaskInstanceEventListener {
    protected void afterWorkItemCreated(IWorkflowSession currentSession,
			IProcessInstance processInstance,ITaskInstance taskInstance,IWorkItem workItem)throws EngineException{
    	System.out.println("======WorkItem创建成功，给操作员"+workItem.getActorId()+"短信。。。。。。");
    	System.out.println("Inside ..example..TaskInstanceEventListener4All:: WorkItem is "+workItem);
    	System.out.println("Inside ..example..TaskInstanceEventListener4All:: currentSession is "+currentSession);
    	System.out.println("Inside ..example..TaskInstanceEventListener4All:: processInstance is "+processInstance);
    	System.out.println("Inside ..example..TaskInstanceEventListener4All:: taskInstance is "+taskInstance);
    }
}
