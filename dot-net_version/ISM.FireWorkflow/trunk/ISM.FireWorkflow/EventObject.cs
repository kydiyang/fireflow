using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISM.FireWorkflow
{
    public class EventObject : EventArgs
    {
        protected Object source;

        public EventObject(Object source)
        {
            this.source = source;
        }
        public Object getSource()
        {
            return source;
        }
    }
}
