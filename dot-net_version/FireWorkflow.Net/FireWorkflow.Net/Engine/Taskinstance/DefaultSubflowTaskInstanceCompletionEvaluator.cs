using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Engine.Taskinstance
{
    public class DefaultSubflowTaskInstanceCompletionEvaluator : ITaskInstanceCompletionEvaluator
    {

        public Boolean taskInstanceCanBeCompleted(IWorkflowSession currentSession, RuntimeContext runtimeContext,
                IProcessInstance processInstance, ITaskInstance taskInstance)//throws EngineException ,KernelException
        {
            //在Fire Workflow 中，系统默认每个子流程仅创建一个实例，所以当子流程实例完成后，SubflowTaskInstance都可以被completed
            //所以，应该直接返回true;
            return true;

            //如果系统动态创建了多个并发子流程实例，则需要检查是否存在活动的子流程实例，如果存在则返回false，否则返回true。
            //可以用下面的代码实现
            //        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
            //        Int32 count = persistenceService.getAliveProcessInstanceCountForParentTaskInstance(taskInstance.getId());
            //        if (count>0){
            //            return false;
            //        }else{
            //            return true;
            //        }

        }

    }
}
