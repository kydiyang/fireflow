using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Kernel.Event
{
    public class EdgeInstanceEvent : EventObject
    {

        public const int ON_TAKING_THE_TOKEN = 1;
        int eventType = -1;
        private IToken token = null;

        private EdgeInstanceEvent()
            : base(null)
        {
        }

        public EdgeInstanceEvent(Object source)
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
