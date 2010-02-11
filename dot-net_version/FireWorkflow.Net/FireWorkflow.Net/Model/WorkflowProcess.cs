/* 
 * @author 非也,nychen2000@163.com
 * @Revision 无忧 lwz0721@gmail.com
 */
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model.Net;

namespace FireWorkflow.Net.Model
{
    //[XmlRootAttribute(Namespace = "http://www.fireflow.org/Fireflow_Process_Definition_Language")]
    public class WorkflowProcess : AbstractWFElement
    {
        #region 子元素
        //    private List formalParameters = new ArrayList();

        /// <summary>流程数据项，运行时转换为流程变量进行存储。</summary>
        private List<DataField> dataFields = new List<DataField>();

        /// <summary>全局Task。</summary>
        private List<Task> tasks = new List<Task>();

        /// <summary>流程环节</summary>
        private List<Activity> activities = new List<Activity>();

        /// <summary>转移</summary>
        private List<Transition> transitions = new List<Transition>();

        /// <summary>循环</summary>
        private List<Loop> loops = new List<Loop>();

        /// <summary>同步器</summary>
        private List<Synchronizer> synchronizers = new List<Synchronizer>();

        /// <summary>开始节点</summary>
        private StartNode startNode = null;

        /// <summary>结束节点</summary>
        private List<EndNode> endNodes = new List<EndNode>();

        #endregion

        #region 其他属性

        /// <summary>资源文件（在1.0中暂时未使用）</summary>
        private String resourceFile = null;

        /// <summary>资源管理器（在1.0中暂时未使用）</summary>
        private String resourceManager = null;

        /// <summary>
        /// 本流程全局的任务实例创建器。
        /// 如果没有设置，引擎将使用DefaultTaskInstanceCreator来创建TaskInstance。
        /// </summary>
        protected String taskInstanceCreator = null;

        /// <summary>
        /// 本流程全局的FormTask Instance运行器。
        /// 如果没有设置，引擎将使用DefaultFormTaskInstanceRunner来运行TaskInstance。
        /// </summary>
        protected String formTaskInstanceRunner = null;

        /// <summary>
        /// 本流程全局的ToolTask Instance运行器。
        /// 如果没有设置，引擎将使用DefaultToolTaskInstanceRunner来运行TaskInstance。
        /// </summary>
        protected String toolTaskInstanceRunner = null;


        /// <summary>
        /// 本流程全局的SubflowTask Instance运行器。
        /// 如果没有设置，引擎将使用DefaultSubflowTaskInstanceRunner来运行TaskInstance。
        /// </summary>
        protected String subflowTaskInstanceRunner = null;


        /// <summary>
        /// 本流程全局的FormTask Instance 终结评价器，用于告诉引擎该实例是否可以结束。<br/>
        /// 如果没有设置，引擎使用缺省实现DefaultFormTaskInstanceCompletionEvaluator。
        /// </summary>
        protected String formTaskInstanceCompletionEvaluator = null;

        /// <summary>
        /// 本流程全局的ToolTask Instance 终结评价器，用于告诉引擎该实例是否可以结束。<br/>
        /// 如果没有设置，引擎使用缺省实现DefaultToolTaskInstanceCompletionEvaluator。
        /// </summary>
        protected String toolTaskInstanceCompletionEvaluator = null;

        /// <summary>
        /// 本流程全局的SubflowTask Instance 终结评价器，用于告诉引擎该实例是否可以结束。<br/>
        /// 如果没有设置，引擎使用缺省实现DefaultSubflowTaskInstanceCompletionEvaluator。
        /// </summary>
        protected String subflowTaskInstanceCompletionEvaluator = null;

        //    private int version = 1;//version在流程定义中不需要，只有在流程存储中需要，每次updatge数据库，都需要增加Version值

        #endregion

        //构造函数
        public WorkflowProcess(String name)
            : base(null, name)
        {
        }
        //    public List getFormalParameters() {
        //        return formalParameters;
        //    }

        /// <summary>返回所有的流程数据项</summary>
        /// <returns></returns>
        public List<DataField> getDataFields()
        {
            return dataFields;
        }

        /// <summary>返回所有的环节</summary>
        public List<Activity> getActivities()
        {
            return activities;
        }

        /// <summary>返回所有的循环</summary>
        public List<Loop> getLoops()
        {
            return loops;
        }

        /// <summary>返回所有的转移</summary>
        public List<Transition> getTransitions()
        {
            return transitions;
        }

        //    public int getVersion() {
        //        return version;
        //    }
        //
        //    public void setVersion(int version) {
        //        this.version = version;
        //    }

        /// <summary>返回开始节点</summary>
        public StartNode getStartNode()
        {
            return startNode;
        }

        public void setStartNode(StartNode startNode)
        {
            this.startNode = startNode;
        }

        /// <summary>返回所有的结束节点</summary>
        public List<EndNode> getEndNodes()
        {
            return endNodes;
        }

        /// <summary>返回所有的同步器</summary>
        public List<Synchronizer> getSynchronizers()
        {
            return synchronizers;
        }

        /// <summary>返回所有的全局Task</summary>
        public List<Task> getTasks()
        {
            return this.tasks;
        }


        //保留
        public String getResourceFile()
        {
            return resourceFile;
        }

        //保留
        public void setResourceFile(String resourceFile)
        {
            this.resourceFile = resourceFile;
        }

        //保留
        public String getResourceManager()
        {
            return resourceManager;
        }

        //保留
        public void setResourceManager(String resourceMgr)
        {
            this.resourceManager = resourceMgr;
        }

        /// <summary>通过ID查找该流程中的任意元素</summary>
        /// <param name="id">元素的Id</param>
        /// <returns>流程元素，如：Activity,Task,Synchronizer等等</returns>
        public IWFElement findWFElementById(String id)
        {
            if (this.getId().Equals(id))
            {
                return this;
            }

            List<Task> tasksList = this.getTasks();
            for (int i = 0; i < tasksList.Count; i++)
            {
                Task task = (Task)tasksList[i];
                if (task.getId().Equals(id))
                {
                    return task;
                }
            }

            List<Activity> activityList = this.getActivities();
            for (int i = 0; i < activityList.Count; i++)
            {
                IWFElement wfElement = (IWFElement)activityList[i];
                if (wfElement.getId().Equals(id))
                {
                    return wfElement;
                }
                List<Task> taskList = ((Activity)wfElement).getTasks();
                for (int j = 0; j < taskList.Count; j++)
                {
                    IWFElement wfElement2 = (IWFElement)taskList[j];
                    if (wfElement2.getId().Equals(id))
                    {
                        return wfElement2;
                    }
                }
            }
            if (this.getStartNode().getId().Equals(id))
            {
                return this.getStartNode();
            }
            List<Synchronizer> synchronizerList = this.getSynchronizers();
            for (int i = 0; i < synchronizerList.Count; i++)
            {
                IWFElement wfElement = (IWFElement)synchronizerList[i];
                if (wfElement.getId().Equals(id))
                {
                    return wfElement;
                }
            }

            List<EndNode> endNodeList = this.getEndNodes();
            for (int i = 0; i < endNodeList.Count; i++)
            {
                IWFElement wfElement = (IWFElement)endNodeList[i];
                if (wfElement.getId().Equals(id))
                {
                    return wfElement;
                }
            }

            List<Transition> transitionList = this.getTransitions();
            for (int i = 0; i < transitionList.Count; i++)
            {
                IWFElement wfElement = (IWFElement)transitionList[i];
                if (wfElement.getId().Equals(id))
                {
                    return wfElement;
                }
            }

            List<DataField> dataFieldList = this.getDataFields();
            for (int i = 0; i < dataFieldList.Count; i++)
            {
                IWFElement wfElement = (IWFElement)dataFieldList[i];
                if (wfElement.getId().Equals(id))
                {
                    return wfElement;
                }
            }

            List<Loop> loopList = this.getLoops();
            for (int i = 0; i < loopList.Count; i++)
            {
                IWFElement wfElement = (IWFElement)loopList[i];
                if (wfElement.getId().Equals(id))
                {
                    return wfElement;
                }
            }
            return null;
        }

        /// <summary>通过Id查找任意元素的序列号</summary>
        /// <param name="id">流程元素的id</param>
        /// <returns>流程元素的序列号</returns>
        public String findSnById(String id)
        {
            IWFElement elem = this.findWFElementById(id);
            if (elem != null)
            {
                return elem.getSn();
            }
            return null;
        }

        /// <summary>验证workflow process是否完整正确。</summary>
        /// <returns>null表示流程正确；否则表示流程错误，返回值是错误原因</returns>
        public String validate()
        {
            String errHead = "Workflow process is invalid：";
            if (this.getStartNode() == null)
            {
                return errHead + "must have one start node";
            }
            if (this.getStartNode().getLeavingTransitions().Count == 0)
            {
                return errHead + "start node must have leaving transitions.";
            }


            List<Activity> activities = this.getActivities();
            for (int i = 0; i < activities.Count; i++)
            {
                Activity activity = activities[i];
                String theName = (String.IsNullOrEmpty(activity.getDisplayName())) ? activity.getName() : activity.getDisplayName();
                if (activity.getEnteringTransition() == null)
                {
                    return errHead + "activity[" + theName + "] must have entering transition.";
                }
                if (activity.getLeavingTransition() == null)
                {
                    return errHead + "activity[" + theName + "] must have leaving transition.";
                }

                //check tasks
                List<Task> taskList = activity.getTasks();
                for (int j = 0; j < taskList.Count; j++)
                {
                    Task task = (Task)taskList[j];
                    if (task.getType() == null)
                    {
                        return errHead + "task[" + task.getId() + "]'s taskType can Not be null.";
                    }
                    else if (task.getType().Equals(Task.FORM))
                    {
                        FormTask formTask = (FormTask)task;
                        if (formTask.getPerformer() == null)
                        {
                            return errHead + "FORM-task[id=" + task.getId() + "] must has a performer.";
                        }
                    }
                    else if (task.getType().Equals(Task.TOOL))
                    {
                        ToolTask toolTask = (ToolTask)task;
                        if (toolTask.getApplication() == null)
                        {
                            return errHead + "TOOL-task[id=" + task.getId() + "] must has a application.";
                        }
                    }
                    else if (task.getType().Equals(Task.SUBFLOW))
                    {
                        SubflowTask subflowTask = (SubflowTask)task;
                        if (subflowTask.getSubWorkflowProcess() == null)
                        {
                            return errHead + "SUBFLOW-task[id=" + task.getId() + "] must has a subflow.";
                        }
                    }
                    else
                    {
                        return errHead + " unknown task type of task[" + task.getId() + "]";
                    }
                }
            }

            List<Synchronizer> synchronizers = this.getSynchronizers();
            for (int i = 0; i < synchronizers.Count; i++)
            {
                Synchronizer synchronizer = synchronizers[i];
                String theName = (synchronizer.getDisplayName() == null || synchronizer.getDisplayName().Equals("")) ? synchronizer.getName() : synchronizer.getDisplayName();
                if (synchronizer.getEnteringTransitions().Count == 0)
                {
                    return errHead + "synchronizer[" + theName + "] must have entering transition.";
                }
                if (synchronizer.getLeavingTransitions().Count == 0)
                {
                    return errHead + "synchronizer[" + theName + "] must have leaving transition.";
                }
            }

            List<EndNode> endnodes = this.getEndNodes();
            for (int i = 0; i < endnodes.Count; i++)
            {
                EndNode endnode = endnodes[i];
                String theName = (endnode.getDisplayName() == null || endnode.getDisplayName().Equals("")) ? endnode.getName() : endnode.getDisplayName();
                if (endnode.getEnteringTransitions().Count == 0)
                {
                    return errHead + "end node[" + theName + "] must have entering transition.";
                }
            }

            List<Transition> transitions = this.getTransitions();
            for (int i = 0; i < transitions.Count; i++)
            {
                Transition transition = transitions[i];
                String theName = (transition.getDisplayName() == null || transition.getDisplayName().Equals("")) ? transition.getName() : transition.getDisplayName();
                if (transition.getFromNode() == null)
                {
                    return errHead + "transition[" + theName + "] must have from node.";

                }
                if (transition.getToNode() == null)
                {
                    return errHead + "transition[" + theName + "] must have to node.";
                }
            }

            //check datafield
            List<DataField> dataFieldList = this.getDataFields();
            for (int i = 0; i < dataFieldList.Count; i++)
            {
                DataField df = (DataField)dataFieldList[i];
                if (df.getDataType() == null)
                {
                    return errHead + "unknown data type of datafield[" + df.getId() + "]";
                }
            }

            return null;
        }

        /// <summary>判断两个Activity是否在同一个执行线上</summary>
        /// <returns>true表示在同一个执行线上，false表示不在同一个执行线上</returns>
        public Boolean isInSameLine(String activityId1, String activityId2)
        {
            Node node1 = (Node)this.findWFElementById(activityId1);
            Node node2 = (Node)this.findWFElementById(activityId2);
            if (node1 == null || node2 == null) return false;
            List<Node> connectableNodes4Activity1 = new List<Node>();
            connectableNodes4Activity1.Add(node1);
            connectableNodes4Activity1.AddRange(getReachableNodes(activityId1));
            connectableNodes4Activity1.AddRange(getEnterableNodes(activityId1));

            List<Node> connectableNodes4Activity2 = new List<Node>();
            connectableNodes4Activity2.Add(node2);
            connectableNodes4Activity2.AddRange(getReachableNodes(activityId2));
            connectableNodes4Activity2.AddRange(getEnterableNodes(activityId2));
            /*
            System.out.println("===Inside WorkflowProcess.isInSameLine()::connectableNodes4Activity1.Count="+connectableNodes4Activity1.Count);
            System.out.println("===Inside WorkflowProcess.isInSameLine()::connectableNodes4Activity2.Count="+connectableNodes4Activity2.Count);
            System.out.println("-----------------------activity1--------------");
            for (int i=0;i<connectableNodes4Activity1.Count;i++){
                Node node = (Node)connectableNodes4Activity1[i];
                System.out.println("node.id of act1 is "+node.getId());
            }
        
            System.out.println("---------------------activity2--------------------");
            for (int i=0;i<connectableNodes4Activity2.Count;i++){
                Node node = (Node)connectableNodes4Activity2[i];
                System.out.println("node.id of act2 is "+node.getId());
            }
             */

            if (connectableNodes4Activity1.Count != connectableNodes4Activity2.Count)
            {
                return false;
            }


            for (int i = 0; i < connectableNodes4Activity1.Count; i++)
            {
                Node node = (Node)connectableNodes4Activity1[i];
                Boolean find = false;
                for (int j = 0; j < connectableNodes4Activity2.Count; j++)
                {
                    Node tmpNode = (Node)connectableNodes4Activity2[j];
                    if (node.getId().Equals(tmpNode.getId()))
                    {
                        find = true;
                        break;
                    }
                }
                if (!find) return false;
            }
            return true;
        }

        public List<Node> getReachableNodes(String nodeId)
        {
            List<Node> reachableNodesList = new List<Node>();
            Node node = (Node)this.findWFElementById(nodeId);
            if (node is Activity)
            {
                Activity activity = (Activity)node;
                Transition leavingTransition = activity.getLeavingTransition();
                if (leavingTransition != null)
                {
                    Node toNode = (Node)leavingTransition.getToNode();
                    if (toNode != null)
                    {
                        reachableNodesList.Add(toNode);
                        reachableNodesList.AddRange(getReachableNodes(toNode.getId()));
                    }
                }
            }
            else if (node is Synchronizer)
            {
                Synchronizer synchronizer = (Synchronizer)node;
                List<Transition> leavingTransitions = synchronizer.getLeavingTransitions();
                for (int i = 0; leavingTransitions != null && i < leavingTransitions.Count; i++)
                {
                    Transition leavingTransition = (Transition)leavingTransitions[i];
                    if (leavingTransition != null)
                    {
                        Node toNode = (Node)leavingTransition.getToNode();
                        if (toNode != null)
                        {
                            reachableNodesList.Add(toNode);
                            reachableNodesList.AddRange(getReachableNodes(toNode.getId()));
                        }

                    }
                }
            }

            List<Node> tmp = new List<Node>();
            Boolean alreadyInTheList = false;
            for (int i = 0; i < reachableNodesList.Count; i++)
            {
                Node nodeTmp = (Node)reachableNodesList[i];
                alreadyInTheList = false;
                for (int j = 0; j < tmp.Count; j++)
                {
                    Node nodeTmp2 = (Node)tmp[j];
                    if (nodeTmp2.getId().Equals(nodeTmp.getId()))
                    {
                        alreadyInTheList = true;
                        break;
                    }
                }
                if (!alreadyInTheList)
                {
                    tmp.Add(nodeTmp);
                }
            }
            reachableNodesList = tmp;
            return reachableNodesList;
        }

        public List<Node> getEnterableNodes(String nodeId)
        {
            List<Node> enterableNodesList = new List<Node>();
            Node node = (Node)this.findWFElementById(nodeId);
            if (node is Activity)
            {
                Activity activity = (Activity)node;
                Transition enteringTransition = activity.getEnteringTransition();
                if (enteringTransition != null)
                {
                    Node fromNode = (Node)enteringTransition.getFromNode();
                    if (fromNode != null)
                    {
                        enterableNodesList.Add(fromNode);
                        enterableNodesList.AddRange(getEnterableNodes(fromNode.getId()));
                    }
                }
            }
            else if (node is Synchronizer)
            {
                Synchronizer synchronizer = (Synchronizer)node;
                List<Transition> enteringTransitions = synchronizer.getEnteringTransitions();
                for (int i = 0; enteringTransitions != null && i < enteringTransitions.Count; i++)
                {
                    Transition enteringTransition = (Transition)enteringTransitions[i];
                    if (enteringTransition != null)
                    {
                        Node fromNode = (Node)enteringTransition.getFromNode();
                        if (fromNode != null)
                        {
                            enterableNodesList.Add(fromNode);
                            enterableNodesList.AddRange(getEnterableNodes(fromNode.getId()));
                        }

                    }
                }
            }

            List<Node> tmp = new List<Node>();
            Boolean alreadyInTheList = false;
            for (int i = 0; i < enterableNodesList.Count; i++)
            {
                Node nodeTmp = (Node)enterableNodesList[i];
                alreadyInTheList = false;
                for (int j = 0; j < tmp.Count; j++)
                {
                    Node nodeTmp2 = (Node)tmp[j];
                    if (nodeTmp2.getId().Equals(nodeTmp.getId()))
                    {
                        alreadyInTheList = true;
                        break;
                    }
                }
                if (!alreadyInTheList)
                {
                    tmp.Add(nodeTmp);
                }
            }
            enterableNodesList = tmp;
            return enterableNodesList;
        }


        public String getTaskInstanceCreator()
        {
            return taskInstanceCreator;
        }

        public void setTaskInstanceCreator(String taskInstanceCreator)
        {
            this.taskInstanceCreator = taskInstanceCreator;
        }

        public String getFormTaskInstanceCompletionEvaluator()
        {
            return formTaskInstanceCompletionEvaluator;
        }

        public void setFormTaskInstanceCompletionEvaluator(String formTaskInstanceCompletionEvaluator)
        {
            this.formTaskInstanceCompletionEvaluator = formTaskInstanceCompletionEvaluator;
        }

        public String getFormTaskInstanceRunner()
        {
            return formTaskInstanceRunner;
        }

        public void setFormTaskInstanceRunner(String formTaskInstanceRunner)
        {
            this.formTaskInstanceRunner = formTaskInstanceRunner;
        }

        public String getSubflowTaskInstanceCompletionEvaluator()
        {
            return subflowTaskInstanceCompletionEvaluator;
        }

        public void setSubflowTaskInstanceCompletionEvaluator(String subflowTaskInstanceCompletionEvaluator)
        {
            this.subflowTaskInstanceCompletionEvaluator = subflowTaskInstanceCompletionEvaluator;
        }

        public String getSubflowTaskInstanceRunner()
        {
            return subflowTaskInstanceRunner;
        }

        public void setSubflowTaskInstanceRunner(String subflowTaskInstanceRunner)
        {
            this.subflowTaskInstanceRunner = subflowTaskInstanceRunner;
        }

        public String getToolTaskInstanceRunner()
        {
            return toolTaskInstanceRunner;
        }

        public void setToolTaskInstanceRunner(String toolTaskInstanceRunner)
        {
            this.toolTaskInstanceRunner = toolTaskInstanceRunner;
        }

        public String getToolTaskInstanceCompletionEvaluator()
        {
            return toolTaskInstanceCompletionEvaluator;
        }

        public void setToolTaskInstanceCompletionEvaluator(String toolTaskIntanceCompletionEvaluator)
        {
            this.toolTaskInstanceCompletionEvaluator = toolTaskIntanceCompletionEvaluator;
        }
    }
}
