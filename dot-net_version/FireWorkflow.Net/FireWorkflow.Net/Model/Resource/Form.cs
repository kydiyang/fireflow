using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Resource
{
    /// <summary>表单。</summary>
    public class Form : AbstractResource
    {
        /// <summary>表单的地址。工作流引擎不处理该url，所以其格式只要业务系统能够解析即可。</summary>
        private String uri = null;

        public Form(String name)
        {
            this.setName(name);
        }

        public String getUri()
        {
            return uri;
        }

        public void setUri(String uri)
        {
            this.uri = uri;
        }
    }
}
