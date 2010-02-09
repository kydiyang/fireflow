using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model
{
    /// <summary>任务引用。用于Activity引用全局的Task。</summary>
    public class TaskRef : AbstractWFElement
    {

        /// <summary>被引用的Task</summary>
        Task referencedTask = null;
        public TaskRef(IWFElement parent, Task task)
            : base(parent, task.getName())
        {
            referencedTask = task;
        }

        public TaskRef(Task task)
        {
            referencedTask = task;
        }

        public Task getReferencedTask()
        {
            return referencedTask;
        }


        /// <summary>
        /// TaskRef的name等于被引用的Task的name
        /// AbstractWFElement#getName()
        /// </summary>
        public override String getName()
        {
            return referencedTask.getName();
        }

        public override void setName(String name)
        {

        }

        /// <summary>TaskRef的description等于被引用的Task的description</summary>
        public override String getDescription()
        {
            return referencedTask.getDescription();
        }

        public override void setDescription(String description)
        {

        }



        public override String ToString()
        {
            return referencedTask.ToString();
        }

        /// <summary>TaskRef的显示名等于被引用的Task的显示名</summary>
        public override String getDisplayName()
        {
            return referencedTask.getDisplayName();
        }

        public override void setDisplayName(String label)
        {

        }
    }
}
