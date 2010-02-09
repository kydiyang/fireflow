using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Engine;

namespace FireWorkflow.Net.Kernel
{
    public interface IJoinPoint
    {
        IProcessInstance getProcessInstance();
        void setProcessInstance(IProcessInstance processInstance);
        String getProcessInstanceId();
        void setProcessInstanceId(String id);
        Int32? getValue();
        void addValue(Int32 v);
        Boolean getAlive();
        void setAlive(Boolean alive);
        String getSynchronizerId();

        void setSynchronizerId(String synchronizerId);

        Int32 getStepNumber();
        void setStepNumber(Int32 i);

        String getFromActivityId();
        void setFromActivityId(String s);
    }
}
