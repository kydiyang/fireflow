using System;
using System.Collections.Generic;
using System.Xml;
using System.Xml.Serialization;
using System.Text;
using FireWorkflow.Net.Model.Resource;

namespace FireWorkflow.Net.Model
{
    /// <summary>表单类型的Task，即人工任务。</summary>
    public class FormTask : Task
    {
        /// <summary>可编辑表单</summary>
        public const String EDITFORM = "EDITFORM";

        /// <summary>只读表单</summary>
        public const String VIEWFORM = "VIEWFORM";

        /// <summary>列表表单</summary>
        public const String LISTFORM = "LISTFORM";

        /// <summary>
        /// 任务分配策略之一：ALL。任务分配给角色中的所有人，只有在所有工单结束结束的情况下，任务实例才结束。
        /// 用于实现会签。
        /// </summary>
        public const String ALL = "ALL";

        /// <summary>任务分配策略之二：ANY。任何一个操作角签收该任务的工单后，其他人的工单被取消掉。</summary>
        public const String ANY = "ANY";

        /// <summary>deprecated</summary>
        public const String MANUAL = "MANUAL";

        /// <summary>deprecated</summary>
        public const String AUTOMATIC = "AUTOMATIC";

        //----------Form Task 的属性

        /// <summary>操作者</summary>
        protected Participant performer;//引用participant

        /// <summary>该任务的工作项分配策略。取值为FormTask.ANY,FormTask.ALL。</summary>
        [XmlAttribute(AttributeName = "CompletionStrategy")]
        public String assignmentStrategy = ANY;//

        /// <summary>缺省表单。</summary>
        protected String defaultView = VIEWFORM;//缺省视图是view form

        /// <summary>可编辑表单</summary>
        protected Form editForm = null;

        /// <summary>只读表单</summary>
        protected Form viewForm = null;

        /// <summary>列表表单</summary>
        protected Form listForm = null;


        //    protected String startMode = MANUAL ;//启动模式，启动模式没有意义，application和subflow自动启动，Form一般情况下签收时启动，如果需要自动启动则在assignable接口中实现。

        public FormTask()
        {
            this.setType(FORM);
        }

        public FormTask(IWFElement parent, String name)
            : base(parent, name)
        {
            this.setType(FORM);
        }

        // model.reference.Participant
        /// <summary>返回任务的操作员。只有FORM类型的任务才有操作员。</summary>
        /// <returns>操作员</returns>
        public Participant getPerformer()
        {
            return performer;
        }

        /// <summary>设置任务的操作员</summary>
        /// <param name="performer">参与者</param>
        public void setPerformer(Participant performer)
        {
            this.performer = performer;
        }

        /// <summary>返回任务的分配策略，只对FORM类型的任务有意义。取值为ALL或者ANY</summary>
        /// <returns>任务分配策略值</returns>
        public String getAssignmentStrategy()
        {
            return assignmentStrategy;
        }

        /// <summary>设置任务分配策略，只对FORM类型的任务有意义。取值为ALL或者ANY</summary>
        /// <param name="argAssignmentStrategy">任务分配策略值</param>
        public void setAssignmentStrategy(String argAssignmentStrategy)
        {
            this.assignmentStrategy = argAssignmentStrategy;
        }

        /// <summary>
        /// 返回任务的缺省表单的类型，取值为EDITFORM、VIEWFORM或者LISTFORM。
        /// 只有FORM类型的任务此方法才有意义。该方法的主要作用是方便系统开发，引擎不会用到该方法。
        /// </summary>
        public String getDefaultView()
        {
            return defaultView;
        }

        public void setDefaultView(String defaultView)
        {
            this.defaultView = defaultView;
        }

        public Form getEditForm()
        {
            return editForm;
        }

        public void setEditForm(Form editForm)
        {
            this.editForm = editForm;
        }

        public Form getViewForm()
        {
            return viewForm;
        }

        public void setViewForm(Form viewForm)
        {
            this.viewForm = viewForm;
        }

        public Form getListForm()
        {
            return listForm;
        }

        public void setListForm(Form listForm)
        {
            this.listForm = listForm;
        }
    }
}
