using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Resource
{
    public class AbstractResource : IResource
    {
        /// <summary>资源的名称</summary>
        private String name;

        /// <summary>资源的显示名称</summary>
        private String displayName;

        /// <summary>资源的描述</summary>
        private String description;

        /// <summary>返回资源的名称</summary>
        /// <returns>资源的名称</returns>
        public String getName()
        {
            return name;
        }

        /// <summary>设置资源的名称</summary>
        /// <param name="name">资源的名称</param>
        public void setName(String name)
        {
            this.name = name;
        }

        /// <summary>返回资源的显示名称</summary>
        /// <returns>资源的显示名称</returns>
        public String getDisplayName()
        {
            return displayName;
        }

        /// <summary>设置资源的显示名称</summary>
        /// <param name="displayName">资源的显示名称</param>
        public void setDisplayName(String displayName)
        {
            this.displayName = displayName;
        }


        /// <summary>返回资源的描述</summary>
        /// <returns>资源的描述</returns>
        public String getDescription()
        {
            return description;
        }

        /// <summary>设置资源的描述</summary>
        /// <param name="description">资源的描述</param>
        public void setDescription(String description)
        {
            this.description = description;
        }


        public override String ToString()
        {
            if (!String.IsNullOrEmpty(displayName))
            {
                return this.displayName;
            }
            else
            {
                return this.name;
            }
        }
    }
}
