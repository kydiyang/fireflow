using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISM.FireWorkflow.Engine.Impl
{
    [Serializable]
    public class ProcessInstanceTrace
    {
        public const String TRANSITION_TYPE = "Transition";
        public const String LOOP_TYPE = "Loop";
        public const String JUMPTO_TYPE = "JumpTo";
        public const String WITHDRAW_TYPE = "Withdraw";
        public const String REJECT_TYPE = "Reject";
        String id;
        String processInstanceId;
        Int32 stepNumber;
        Int32 minorNumber = 0;
        String type;//Transition, Loop, JumpTo, Withdraw, Reject
        String edgeId;
        String fromNodeId;
        String toNodeId;

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }



        public String getEdgeId()
        {
            return edgeId;
        }

        public void setEdgeId(String edgeId)
        {
            this.edgeId = edgeId;
        }

        public String getFromNodeId()
        {
            return fromNodeId;
        }

        public void setFromNodeId(String fromNodeId)
        {
            this.fromNodeId = fromNodeId;
        }

        public String getProcessInstanceId()
        {
            return processInstanceId;
        }

        public void setProcessInstanceId(String processInstanceId)
        {
            this.processInstanceId = processInstanceId;
        }

        public Int32 getStepNumber()
        {
            return stepNumber;
        }

        public void setStepNumber(Int32 stepNumber)
        {
            this.stepNumber = stepNumber;
        }

        public String getToNodeId()
        {
            return toNodeId;
        }

        public void setToNodeId(String toNodeId)
        {
            this.toNodeId = toNodeId;
        }

        public String getType()
        {
            return type;
        }

        public void setType(String type)
        {
            this.type = type;
        }

        public Int32 getMinorNumber()
        {
            return minorNumber;
        }

        public void setMinorNumber(Int32 minorNumber)
        {
            this.minorNumber = minorNumber;
        }
    }
}
