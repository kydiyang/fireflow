using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Kernel.Event
{
    public class NodeInstanceEvent : EventObject
    {
        public const int NODEINSTANCE_TOKEN_ENTERED = 1;
        public const int NODEINSTANCE_FIRED = 2;
        public const int NODEINSTANCE_COMPLETED = 3;
        public const int NODEINSTANCE_LEAVING = 4;

        int eventType = -1;
        private IToken token = null;

        private NodeInstanceEvent()
            : base(null)
        {
        }
        public NodeInstanceEvent(Object source)
            : base(source)
        {

        }

        public IToken getToken()
        {
            return token;
        }

        public void setToken(IToken tk)
        {
            this.token = tk;
        }

        public int getEventType()
        {
            return eventType;
        }

        public void setEventType(int eventType)
        {
            this.eventType = eventType;
        }


    }
}
