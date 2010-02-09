using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Resource
{
    /// <summary>后台程序。</summary>
    public class Application : AbstractResource
    {
        /// <summary>后台程序的类名。</summary>
        private String handler = null;
        public Application(String name)
        {
            this.setName(name);
            // TODO Auto-generated constructor stub
        }
        public String getHandler()
        {
            return handler;
        }
        public void setHandler(String handler)
        {
            this.handler = handler;
        }
    }
}
