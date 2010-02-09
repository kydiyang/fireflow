using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model
{
    public class EventListener
    {
        /// <summary></summary>
        private String className;

        /// <summary>返回监听器的实现类名称</summary>
        /// <returns>监听器的实现类名称</returns>
        public String getClassName()
        {
            return className;
        }

        /// <summary>设置监听器的实现类名称</summary>
        /// <param name="className">监听器的实现类名称</param>
        public void setClassName(String className)
        {
            this.className = className;
        }
    }
}
