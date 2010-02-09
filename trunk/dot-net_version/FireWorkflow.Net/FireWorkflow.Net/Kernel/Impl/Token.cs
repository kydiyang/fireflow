using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Engine;

namespace FireWorkflow.Net.Kernel.Impl
{
    public class Token : IToken
    {
        //	private INodeInstance currentNodeInstance;
        //	private INetInstance currentProcessInstance;
        //	private ITransitionInstance transitionInstance;

        private Boolean alive = false;
        private Int32 value ;
        private String nodeId = null;
        private String id = null;
        private String processInstanceId = null;
        private Int32 stepNumber = 0;

        private String fromActivityId = null;

        [NonSerialized]
        private IProcessInstance processInstance = null;
        /* (non-Javadoc)
         * @see org.fireflow.kenel.IToken#getCurrentNodeInstance()
         */
        //	public INodeInstance getCurrentNodeInstance() {
        //		return currentNodeInstance;
        //	}

        /* (non-Javadoc)
         * @see org.fireflow.kenel.IToken#getCurrentProcessInstance()
         */
        //	public INetInstance getNetInstance() {
        //		return currentProcessInstance;
        //	}
        //	
        //	public void setNetInstance(INetInstance procInst){
        //		this.currentProcessInstance = procInst; 
        //	}
        /* (non-Javadoc)
         * @see org.fireflow.kenel.IToken#getRuntimeContext()
         */
        //	public IRuntimeContext getRuntimeContext() {
        //		// TODO Auto-generated method stub
        //		return null;
        //	}

        /* (non-Javadoc)
         * @see org.fireflow.kenel.IToken#getValue()
         */
        public override Int32 getValue()
        {
            //		if (this.transitionInstance!=null){
            //			return this.transitionInstance.getWeight();
            //		}
            return value;
        }

        /* (non-Javadoc)
         * @see org.fireflow.kenel.IToken#setCurrentNodeInstance(org.fireflow.kenel.INodeInstance)
         */
        //	public void setCurrentNodeInstance(INodeInstance currentNodeInstance) {
        //	
        //		this.currentNodeInstance = currentNodeInstance;
        //	}

        /* (non-Javadoc)
         * @see org.fireflow.kenel.IToken#setRuntimeContext(org.fireflow.kenel.IRuntimeContext)
         */
        //	public void setRuntimeContext(IRuntimeContext rtCtx) {
        //		
        //	}

        //	public void setTransitionInstance(ITransitionInstance transInst){
        //		transitionInstance = transInst;
        //	}
        //	public ITransitionInstance getTransitionInstance(){
        //		return transitionInstance;
        //	}
        public override Boolean isAlive()
        {
            return alive;
        }

        public override void setAlive(Boolean alive)
        {
            this.alive = alive;
        }

        public override IProcessInstance getProcessInstance()
        {
            return processInstance;
        }

        public override void setProcessInstance(IProcessInstance inst)
        {
            processInstance = inst;
            if (this.processInstance != null)
            {
                this.processInstanceId = inst.getId();
            }
            else
            {
                this.processInstanceId = null;
            }
        }

        //	public void setAppointedTransitionNames(Set<String> appointedTransitionNames){
        //		
        //	}
        //	
        //	public Set<String> getAppointedTransitionNames(){
        //		return null;
        //	}
        public override void setValue(Int32 v)
        {
            value = v;
        }

        public override String getNodeId()
        {
            return nodeId;
        }

        public override void setNodeId(String nodeId)
        {
            this.nodeId = nodeId;
        }

        public override String getId()
        {
            return id;
        }

        public override void setId(String id)
        {
            this.id = id;
        }

        public override void setProcessInstanceId(String id)
        {
            this.processInstanceId = id;
        }

        public override String getProcessInstanceId()
        {
            return this.processInstanceId;
        }

        public override Int32 getStepNumber()
        {
            return this.stepNumber;
        }

        public override void setStepNumber(Int32 i)
        {
            this.stepNumber = i;
        }

        public override String getFromActivityId()
        {
            return this.fromActivityId;
        }

        public override void setFromActivityId(String s)
        {
            this.fromActivityId = s;
        }
    }

}
