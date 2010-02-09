using System;
using System.Collections.Generic;
using System.Text;

namespace FireWorkflow.Net.Model.Net
{
    /// <summary>环节。</summary>
    public class Activity : Node
    {
        /// <summary>环节实例结束策略之一：他的所有的任务实例结束后才可以结束。</summary>
        public const String ALL = "ALL";

        /// <summary>环节实例结束策略之二：任何一个Task实例结束后环节实例可以结束。</summary>
        public const String ANY = "ANY";

        /// <summary>输入转移</summary>
        private Transition enteringTransition;//输入弧

        /// <summary>输出转移</summary>
        private Transition leavingTransition;//输出弧

        /// <summary>对全局Task的引用的列表</summary>
        private List<TaskRef> taskRefs = new List<TaskRef>();

        /// <summary>局部的task列表</summary>
        private List<Task> inlineTasks = new List<Task>();

        /// <summary>环节实例结束策略，缺省为ALL</summary>
        private String completionStrategy = ALL;

        public Activity()
        {

        }
        public Activity(WorkflowProcess workflowProcess, String name)
            : base(workflowProcess, name)
        {
        }

        public List<Task> getInlineTasks()
        {
            return this.inlineTasks;
        }

        public List<TaskRef> getTaskRefs()
        {
            return taskRefs;
        }

        /// <summary>
        /// 返回该环节所有的Task。
        /// 这些Task是inlineTask列表和taskRef列表解析后的所有的Task的和。
        /// </summary>
        public List<Task> getTasks()
        {
            List<Task> tasks = new List<Task>();
            tasks.AddRange(this.inlineTasks);
            for (int i = 0; i < this.taskRefs.Count; i++)
            {
                TaskRef taskRef = taskRefs[i];
                tasks.Add(taskRef.getReferencedTask());
            }
            return tasks;
        }

        /// <summary>
        /// 返回环节的结束策略，取值为ALL或者ANY，缺省值为ALL
        /// 如果取值为ALL,则只有其所有任务实例结束了，环节实例才可以结束。
        /// 如果取值为ANY，则只要任何一个任务实例结束后，环节实例就可以结束。
        /// 环节实例的结束操作仅执行一遍，因此后续任务实例的结束不会触发环节实例的结束操作再次执行。
        /// </summary>
        public String getCompletionStrategy()
        {
            return completionStrategy;
        }

        public void setCompletionStrategy(String strategy)
        {
            this.completionStrategy = strategy;
        }

        /// <summary>返回环节的输入Transition。一个环节有且只有一个输入Transition</summary>
        /// <returns>转移</returns>
        public Transition getEnteringTransition()
        {
            return enteringTransition;
        }

        public void setEnteringTransition(Transition enteringTransition)
        {
            this.enteringTransition = enteringTransition;

        }

        /// <summary>返回环节的输出Transition。一个环节有且只有一个输出Transition</summary>
        public Transition getLeavingTransition()
        {
            return leavingTransition;
        }

        public void setLeavingTransition(Transition leavingTransition)
        {
            this.leavingTransition = leavingTransition;
        }
    }
}
