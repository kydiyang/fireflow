using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Base;

/*
工作流元素的抽象接口，工作流元素主要包括:
1)业务流程 WorkflowProcess，这是顶层元素
2)任务(Task)
3)开始节点(StartNode)、结束节点(EndNode)、同步器(Synchronizer)、环节(Activity)
4)转移(Transition)和循环(Loop)
5)流程数据项(DataField)
*/
namespace FireWorkflow.Net.Model
{
    public interface IWFElement
    {
        /// <summary>
        /// 返回元素的序列号
        /// 业务系统无须关心该序列号。
        /// return 元素序列号
        /// </summary>
        String getSn();

        /// <summary>设置元素序列号</summary>
        /// <param name="s">元素序列号</param>
        void setSn(String s);

        /// <summary>
        /// 返回工作流元素的Id
        /// 工作流元素的Id采用“父Id.自身Name”的方式组织。
        /// </summary>
        /// <returns>元素Id</returns>
        String getId();

        /// <summary>返回工作流元素的名称</summary>
        /// <returns>元素名称</returns>
        String getName();


        /// <summary></summary>
        void setName(String name);

        /// <summary>返回工作流元素的显示名</summary>
        /// <returns>显示名</returns>
        String getDisplayName();

        /// <summary></summary>
        void setDisplayName(String displayName);

        /// <summary>返回流程元素的描述</summary>
        /// <returns>流程元素描述</returns>
        String getDescription();

        /// <summary></summary>
        void setDescription(String description);

        /// <summary>返回父元素</summary>
        /// <returns>父元素</returns>
        IWFElement getParent();

        /// <summary></summary>
        void setParent(IWFElement parent);

        /// <summary>返回事件监听器列表</summary>
        /// <returns>事件监听器列表</returns>
        List<EventListener> getEventListeners();

        /// <summary>返回扩展属性Map</summary>
        Dictionary<String, String> getExtendedAttributes();
    }
}
