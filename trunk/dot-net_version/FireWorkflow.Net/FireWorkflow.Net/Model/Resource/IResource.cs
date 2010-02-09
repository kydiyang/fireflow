using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Resource
{
    /// <summary>工作流引用的资源。</summary>
    public interface IResource
    {
        /// <summary>返回资源的名称</summary>
        /// <returns>资源的名称</returns>
        String getName();

        /// <summary>设置资源的名称</summary>
        /// <param name="name">资源的名称</param>
        void setName(String name);

        /// <summary>返回资源的显示名称</summary>
        /// <returns>资源的显示名称</returns>
        String getDisplayName();

        /// <summary>设置资源的显示名称</summary>
        /// <param name="displayName">资源的显示名称</param>
        void setDisplayName(String displayName);


        /// <summary>返回资源的描述</summary>
        /// <returns>资源的描述</returns>
        String getDescription();

        /// <summary>设置资源的描述</summary>
        /// <param name="description">资源的描述</param>
        void setDescription(String description);
    }
}
