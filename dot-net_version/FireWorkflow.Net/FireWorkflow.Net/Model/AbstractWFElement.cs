using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace FireWorkflow.Net.Model
{
    [Serializable]
    public abstract class AbstractWFElement : IWFElement
    {

        /// <summary>元素序列号，请不要在业务代码里面使用该属性的信息。因为这个属性的值是变化的。</summary>
        private String sn = null;



        /// <summary>父元素</summary>
        private IWFElement parentElement;



        /// <summary>名称</summary>
        private String name;


        /// <summary>显示名称</summary>
        private String displayName;


        /// <summary>描述</summary>
        private String description;



        /// <summary>事件监听器</summary>
        private List<EventListener> eventListeners = new List<EventListener>();



        /// <summary>扩展属性</summary>
        private Dictionary<String, String> extendedAttributes;

        public AbstractWFElement()
        {

        }



        /// <summary></summary>
        /// <param name="parentElement">父流程元素</param>
        /// <param name="name">本流程元素的名称</param>
        public AbstractWFElement(IWFElement parentElement, String name)
        {
            this.parentElement = parentElement;
            setName(name);
        }

        //public Boolean Equals(Object obj) {
        //    return ((obj in IWFElement) &&
        //            this.getId().Equals(((AbstractWFElement) obj).getId()));
        //}

        //public int hashCode() {
        //    return this.getId().hashCode();
        //}

        public override String ToString() {
            return (String.IsNullOrEmpty(displayName)) ? this.getName() : displayName;
        }


        #region IWFElement 成员

        /// <summary>
        /// 返回元素的序列号
        /// 业务系统无须关心该序列号。
        /// return 元素序列号
        /// </summary>
        public string getSn()
        {
            return sn;
        }

        /// <summary>设置元素序列号</summary>
        /// <param name="s">元素序列号</param>
        public void setSn(string s)
        {
            sn = s;
        }

        /// <summary>
        /// 返回工作流元素的Id
        /// 工作流元素的Id采用“父Id.自身Name”的方式组织。
        /// </summary>
        /// <returns>元素Id</returns>
        public string getId()
        {
            if (parentElement == null)
            {
                return this.getName();
            }
            else
            {
                return parentElement.getId() + "." + this.getName();
            }
        }

        /// <summary>返回工作流元素的名称</summary>
        /// <returns>元素名称</returns>
        public virtual string getName()
        {
            return name;
        }

        public virtual void setName(string name)
        {
            if (name == null)
            {
                throw new NullReferenceException("name cannot be null");
            }

            this.name = name;
        }

        /// <summary>返回工作流元素的显示名</summary>
        /// <returns>显示名</returns>
        public virtual string getDisplayName()
        {
            return displayName;
        }

        public virtual void setDisplayName(string displayName)
        {
            this.displayName = displayName;
        }

        /// <summary>返回流程元素的描述</summary>
        /// <returns>流程元素描述</returns>
        public virtual string getDescription()
        {
            return description;
        }

        public virtual void setDescription(string description)
        {
            this.description = description;
        }

        /// <summary>返回父元素</summary>
        /// <returns>父元素</returns>
        public IWFElement getParent()
        {
            return parentElement;
        }

        public void setParent(IWFElement parent)
        {
            this.parentElement = parent;
        }

        /// <summary>返回事件监听器列表</summary>
        /// <returns>事件监听器列表</returns>
        public List<EventListener> getEventListeners()
        {
            return this.eventListeners;
        }

        /// <summary>返回扩展属性Map</summary>
        public Dictionary<string, string> getExtendedAttributes()
        {
            if (extendedAttributes == null)
            {
                extendedAttributes = new Dictionary<String, String>();
            }
            return extendedAttributes;
        }

        #endregion

    }
}
