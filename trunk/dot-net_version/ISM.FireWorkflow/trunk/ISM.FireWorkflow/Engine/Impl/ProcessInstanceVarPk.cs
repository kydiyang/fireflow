using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISM.FireWorkflow.Engine.Impl
{
    public class ProcessInstanceVarPk
    {
        String processInstanceId = null;
        String name = null;
        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getProcessInstanceId()
        {
            return processInstanceId;
        }

        public void setProcessInstanceId(String processInstanceId)
        {
            this.processInstanceId = processInstanceId;
        }

        public new Boolean Equals(Object obj)
        {
            if (obj == this) return true;
            if (!(obj is ProcessInstanceVarPk)) return false;
            ProcessInstanceVarPk var = (ProcessInstanceVarPk)obj;
            if (var.getProcessInstanceId() != null && var.getProcessInstanceId().Equals(this.processInstanceId)
                    && var.getName() != null && var.getName().Equals(this.name)
                    )
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        public int hashCode()
        {
            String compositeId = this.name + this.processInstanceId;
            if (compositeId == null)
            {
                return String.Empty.GetHashCode();
            }
            else
            {
                return (compositeId).GetHashCode();
            }
        }
    }
}
