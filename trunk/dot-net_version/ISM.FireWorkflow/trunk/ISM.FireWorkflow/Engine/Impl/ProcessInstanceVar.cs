using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISM.FireWorkflow.Engine.Impl
{
    public class ProcessInstanceVar
    {
        ProcessInstanceVarPk varPrimaryKey = null;
        Object value = null;

        public ProcessInstanceVarPk getVarPrimaryKey()
        {
            return varPrimaryKey;
        }

        public void setVarPrimaryKey(ProcessInstanceVarPk varPrimaryKey)
        {
            this.varPrimaryKey = varPrimaryKey;
        }



        public Object getValue()
        {
            return value;
        }

        public void setValue(Object value)
        {
            this.value = value;
        }


        public new Boolean Equals(Object obj)
        {
            if (this == obj) return true;
            if (!(obj is ProcessInstanceVar)) return false;
            ProcessInstanceVar var = (ProcessInstanceVar)obj;
            if (var.getVarPrimaryKey().Equals(this.getVarPrimaryKey()))
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
            if (this.getVarPrimaryKey() == null)
            {
                return String.Empty.GetHashCode();
            }
            else
            {
                return this.getVarPrimaryKey().GetHashCode();
            }
        }
    }
}
