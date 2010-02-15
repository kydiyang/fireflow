/*--

 Copyright (C) 2002-2003 Anthony Eden.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows
    these conditions in the documentation and/or other materials
    provided with the distribution.

 3. The names "OBE" and "Open Business Engine" must not be used to
    endorse or promote products derived from this software without prior
    written permission.  For written permission, please contact
    me@anthonyeden.com.

 4. Products derived from this software may not be called "OBE" or
    "Open Business Engine", nor may "OBE" or "Open Business Engine"
    appear in their name, without prior written permission from
    Anthony Eden (me@anthonyeden.com).

 In addition, I request (but do not require) that you include in the
 end-user documentation provided with the redistribution and/or in the
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by
      Anthony Eden (http://www.anthonyeden.com/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.

 For more information on OBE, please see <http://www.openbusinessengine.org/>.
@author Anthony Eden
 updated by nychen2000
 @Revision to .NET 无忧 lwz0721@gmail.com  2010-02 
 */
using System;
using System.Collections.Generic;
using System.IO;
using System.Xml;
using System.Xml.Serialization;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Net;
using FireWorkflow.Net.Model.Resource;

namespace FireWorkflow.Net.Model.Io
{
    public class Dom4JFPDLParser : IFPDLParser
    {
        public override WorkflowProcess parse(Stream srin)
        {
            if (srin == null) return null;
            try
            {
                XmlDocument document = new XmlDocument();
                document.Load(srin);
                WorkflowProcess wp = parse(document);
                return wp;
            }
            catch (Exception e)
            {
                throw new FPDLParserException("Error parsing document.", e);
            }
        }

        protected WorkflowProcess parse(XmlDocument document)
        {
            XmlElement workflowProcessElement = document.DocumentElement;

            WorkflowProcess wp = new WorkflowProcess(workflowProcessElement.GetAttribute(NAME));
            wp.Sn = System.Guid.NewGuid().ToString();

            wp.TaskInstanceCreator=workflowProcessElement.GetAttribute(TASK_INSTANCE_CREATOR);
            wp.FormTaskInstanceRunner=workflowProcessElement.GetAttribute(FORM_TASK_INSTANCE_RUNNER);
            wp.ToolTaskInstanceRunner=workflowProcessElement.GetAttribute(TOOL_TASK_INSTANCE_RUNNER);
            wp.SubflowTaskInstanceRunner=workflowProcessElement.GetAttribute(SUBFLOW_TASK_INSTANCE_RUNNER);
            wp.FormTaskInstanceCompletionEvaluator=workflowProcessElement.GetAttribute(FORM_TASK_INSTANCE_COMPLETION_EVALUATOR);
            wp.ToolTaskInstanceCompletionEvaluator=workflowProcessElement.GetAttribute(TOOL_TASK_INSTANCE_COMPLETION_EVALUATOR);
            wp.SubflowTaskInstanceCompletionEvaluator=workflowProcessElement.GetAttribute(SUBFLOW_TASK_INSTANCE_COMPLETION_EVALUATOR);
            wp.DisplayName = workflowProcessElement.GetAttribute(DISPLAY_NAME);
            wp.ResourceFile=workflowProcessElement.GetAttribute(RESOURCE_FILE);
            wp.ResourceManager=workflowProcessElement.GetAttribute(RESOURCE_MANAGER);

            foreach (XmlNode node in workflowProcessElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: wp.Description=node.InnerText; break;
                    case DATA_FIELDS: loadDataFields(wp, node); break;
                    case START_NODE: loadStartNode(wp, node); break;
                    case TASKS: loadTasks(wp, wp.Tasks, node); break;
                    case ACTIVITIES: loadActivities(wp, node); break;
                    case SYNCHRONIZERS: loadSynchronizers(wp, node); break;
                    case END_NODES: loadEndNodes(wp, node); break;
                    case TRANSITIONS: loadTransitions(wp, node); break;
                    case LOOPS: loadLoops(wp, node); break;

                    case EVENT_LISTENERS: loadEventListeners(wp.EventListeners, node); break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(wp.ExtendedAttributes, (XmlElement)node); break;
                }
            }
            return wp;

        }

        #region 加载Dictionary＜string, string＞类型数据  和  监听数据
        /// <summary>
        /// 加载Dictionary＜string, string＞类型数据
        /// </summary>
        /// <param name="extendedAttributes"></param>
        /// <param name="element"></param>
        protected void loadExtendedAttributes(Dictionary<String, String> extendedAttributes, XmlNode element)
        {
            if (element == null) { return; }
            extendedAttributes.Clear();
            foreach (XmlNode node in element.ChildNodes)
            {
                if (node.LocalName.Equals(EXTENDED_ATTRIBUTE) && node is XmlElement)
                {
                    extendedAttributes.Add(((XmlElement)node).GetAttribute(NAME), ((XmlElement)node).GetAttribute(VALUE));
                }
            }
        }
        /// <summary>
        /// 加载监听数据
        /// </summary>
        /// <param name="listeners"></param>
        /// <param name="element"></param>
        protected void loadEventListeners(List<EventListener> listeners, XmlNode element)
        {
            if (element == null) { return; }
            listeners.Clear();
            foreach (XmlNode node in element.ChildNodes)
            {
                if (node.LocalName.Equals(EVENT_LISTENER) && node is XmlElement)
                {
                    EventListener listener = new EventListener();
                    listener.ClassName=((XmlElement)node).GetAttribute(CLASS_NAME);
                    listeners.Add(listener);
                }
            }
        }
        #endregion

        #region DataFields
        protected void loadDataFields(WorkflowProcess wp, XmlNode element)
        {
            if (element == null) { return; }
            List<DataField> dataFields = wp.DataFields;
            dataFields.Clear();
            foreach (XmlNode node in element.ChildNodes)
            {
                if (node.LocalName.Equals(DATA_FIELD))
                {
                    dataFields.Add(createDataField(wp, (XmlElement)node));
                }
            }
        }

        protected DataField createDataField(WorkflowProcess wp, XmlElement element)
        {
            if (element == null) { return null; }
            String sdataType = element.GetAttribute(DATA_TYPE);
            DataTypeEnum dataType;
            if (String.IsNullOrEmpty(sdataType)) { dataType = DataTypeEnum.STRING; }
            else dataType = (DataTypeEnum)Enum.Parse(typeof(DataTypeEnum), sdataType);

            DataField dataField = new DataField(wp, element.GetAttribute(NAME), dataType);

            dataField.Sn=Guid.NewGuid().ToString();

            dataField.DisplayName=element.GetAttribute(DISPLAY_NAME);
            dataField.InitialValue=element.GetAttribute(INITIAL_VALUE);

            foreach (XmlNode node in element.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: dataField.Description=node.InnerText; break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(dataField.ExtendedAttributes, node); break;
                }
            }
            return dataField;
        }
        #endregion

        #region StartNode
        protected void loadStartNode(WorkflowProcess wp, XmlNode element)
        {
            if (element == null || !(element is XmlElement)) { return; }

            StartNode startNode = new StartNode(wp);
            startNode.Name=((XmlElement)element).GetAttribute(NAME);
            startNode.Sn=Guid.NewGuid().ToString();
            startNode.DisplayName=((XmlElement)element).GetAttribute(DISPLAY_NAME);

            foreach (XmlNode node in element.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: startNode.Description=node.InnerText; break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(startNode.ExtendedAttributes, node); break;
                }
            }
            wp.StartNode=startNode;
        }
        #endregion

        #region Tasks

        protected void loadTasks(IWFElement parent, List<Task> tasks, XmlNode tasksElement)
        {
            if (tasksElement == null) { return; }
            tasks.Clear();
            foreach (XmlNode node in tasksElement.ChildNodes)
            {
                if (node.LocalName == TASK)
                {
                    Task task = createTask(parent, (XmlElement)node);
                    if (task != null) tasks.Add(task);
                }
            }
        }

        protected Task createTask(IWFElement parent, XmlElement taskElement)
        {
            Task task = null;
            TaskTypeEnum type = (TaskTypeEnum)Enum.Parse(typeof(TaskTypeEnum), taskElement.GetAttribute(TYPE));
            switch (type)
            {
                case TaskTypeEnum.FORM:
                    task = new FormTask(parent, taskElement.GetAttribute(NAME));
                    ((FormTask)task).AssignmentStrategy = (FormTaskEnum)Enum.Parse(typeof(FormTaskEnum), taskElement.GetAttribute(COMPLETION_STRATEGY));
                    ((FormTask)task).DefaultView = (DefaultViewEnum)Enum.Parse(typeof(DefaultViewEnum), taskElement.GetAttribute(DEFAULT_VIEW));
                    break;

                case TaskTypeEnum.TOOL:
                    task = new ToolTask(parent, taskElement.GetAttribute(NAME));
                    break;

                case TaskTypeEnum.SUBFLOW:
                    task = new SubflowTask(parent, taskElement.GetAttribute(NAME));
                    break;

                default: return null;
            }

            task.Sn=Guid.NewGuid().ToString();
            task.DisplayName=taskElement.GetAttribute(DISPLAY_NAME);
            task.TaskInstanceCreator=taskElement.GetAttribute(TASK_INSTANCE_CREATOR);
            task.TaskInstanceRunner=taskElement.GetAttribute(TASK_INSTANCE_RUNNER);
            task.TaskInstanceCompletionEvaluator=taskElement.GetAttribute(TASK_INSTANCE_COMPLETION_EVALUATOR);
            task.LoopStrategy = (LoopStrategyEnum)Enum.Parse(typeof(LoopStrategyEnum), taskElement.GetAttribute(LOOP_STRATEGY));

            int priority = 0;
            try { priority = Int32.Parse(taskElement.GetAttribute(PRIORITY)); }
            catch { }
            task.Priority=priority;

            foreach (XmlNode node in taskElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    //FormTask
                    case PERFORMER: if (task is FormTask) ((FormTask)task).Performer=createPerformer((XmlElement)node); break;
                    case EDIT_FORM: if (task is FormTask) ((FormTask)task).EditForm=createForm((XmlElement)node); break;
                    case VIEW_FORM: if (task is FormTask) ((FormTask)task).ViewForm=createForm((XmlElement)node); break;
                    case LIST_FORM: if (task is FormTask) ((FormTask)task).ListForm=createForm((XmlElement)node); break;
                    //ToolTask
                    case APPLICATION: if (task is ToolTask) ((ToolTask)task).Application = createApplication((XmlElement)node); break;
                    //SubflowTask
                    case SUB_WORKFLOW_PROCESS: if (task is SubflowTask) ((SubflowTask)task).SubWorkflowProcess=createSubWorkflowProcess((XmlElement)node); break;
                    //else
                    case DESCRIPTION: task.Description=node.InnerText; break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(task.ExtendedAttributes, node); break;
                    case DURATION: task.Duration=createDuration((XmlElement)node); break;
                    case EVENT_LISTENERS: loadEventListeners(task.EventListeners, node); break;
                }
            }
            return task;

        }
        protected Participant createPerformer(XmlElement performerElement)
        {
            if (performerElement == null) { return null; }
            Participant part = new Participant(performerElement.GetAttribute(NAME));
            part.DisplayName=performerElement.GetAttribute(DISPLAY_NAME);
            foreach (XmlNode node in performerElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: part.Description=node.InnerText; break;
                    case ASSIGNMENT_HANDLER: part.AssignmentHandler=node.InnerText; break;
                }
            }
            return part;
        }

        protected Form createForm(XmlElement formElement)
        {
            if (formElement == null) { return null; }
            Form form = new Form(formElement.GetAttribute(NAME));
            form.DisplayName=formElement.GetAttribute(DISPLAY_NAME);
            foreach (XmlNode node in formElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: form.Description=node.InnerText; break;
                    case URI: form.Uri=node.InnerText; break;
                }
            }
            return form;
        }
        protected Application createApplication(XmlElement applicationElement)
        {
            if (applicationElement == null) { return null; }
            Application app = new Application(applicationElement.GetAttribute(APPLICATION));
            app.DisplayName=applicationElement.GetAttribute(DISPLAY_NAME);
            foreach (XmlNode node in applicationElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: app.Description=node.InnerText; break;
                    case HANDLER: app.Handler=node.InnerText; break;
                }
            }
            return app;
        }
        protected SubWorkflowProcess createSubWorkflowProcess(XmlElement subFlowElement)
        {
            if (subFlowElement == null)
            {
                return null;
            }

            SubWorkflowProcess subFlow = new SubWorkflowProcess(subFlowElement.GetAttribute(NAME));
            subFlow.DisplayName=subFlowElement.GetAttribute(DISPLAY_NAME);
            foreach (XmlNode node in subFlowElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: subFlow.Description=node.InnerText; break;
                    case WORKFLOW_PROCESS_ID: subFlow.WorkflowProcessId=node.InnerText; break;
                }
            }
            return subFlow;
        }
        protected Duration createDuration(XmlElement durationElement)
        {
            if (durationElement == null) { return null; }

            String sValue = durationElement.GetAttribute(VALUE);
            String sIsBusTime = durationElement.GetAttribute(IS_BUSINESS_TIME);
            Boolean isBusinessTime = true;
            int value = 1;
            if (sValue != null)
            {
                try
                {
                    value = Int32.Parse(sValue);
                    isBusinessTime = Boolean.Parse(sIsBusTime);
                }
                catch (Exception)
                {
                    return null;
                }
            }
            Duration duration = new Duration(value, (UnitEnum)Enum.Parse(typeof(UnitEnum), durationElement.GetAttribute(UNIT)));
            duration.IsBusinessTime = isBusinessTime;
            return duration;
        }
        #endregion

        #region Activities
        protected void loadActivities(WorkflowProcess wp, XmlNode element)
        {
            if (element == null) { return; }
            List<Activity> activities = wp.Activities;
            activities.Clear();
            foreach (XmlNode node in element.ChildNodes)
            {
                if (node.LocalName.Equals(ACTIVITY))
                {
                    activities.Add(createActivitie(wp, (XmlElement)node));
                }
            }
        }
        protected Activity createActivitie(WorkflowProcess wp, XmlElement element)
        {
            if (element == null) { return null; }

            Activity activity = new Activity(wp, element.GetAttribute(NAME));
            activity.Sn=Guid.NewGuid().ToString();
            activity.DisplayName=element.GetAttribute(DISPLAY_NAME);
            activity.CompletionStrategy= (FormTaskEnum)Enum.Parse(typeof(FormTaskEnum),element.GetAttribute(COMPLETION_STRATEGY));

            foreach (XmlNode node in element.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: activity.Description=node.InnerText; break;
                    case EVENT_LISTENERS: loadEventListeners(activity.EventListeners, node); break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(activity.ExtendedAttributes, node); break;

                    case TASKS: loadTasks(activity, activity.InlineTasks, node); break;
                    case TASKREFS: loadTaskRefs((WorkflowProcess)activity.Parent, activity, activity.TaskRefs, node); break;
                }
            }
            return activity;
        }

        protected void loadTaskRefs(WorkflowProcess workflowProcess, IWFElement parent, List<TaskRef> taskRefs, XmlNode taskRefsElement)
        {
            if (taskRefsElement == null) { return; }
            taskRefs.Clear();
            foreach (XmlNode node in taskRefsElement.ChildNodes)
            {
                if (node.LocalName.Equals(TASKREF))
                {
                    String taskId = ((XmlElement)node).GetAttribute(REFERENCE);
                    Task task = (Task)workflowProcess.findWFElementById(taskId);
                    if (task != null)
                    {
                        TaskRef taskRef = new TaskRef(parent, task);
                        taskRef.Sn=Guid.NewGuid().ToString();
                        taskRefs.Add(taskRef);
                    }
                }
            }

        }
        #endregion

        #region Synchronizers
        protected void loadSynchronizers(WorkflowProcess wp, XmlNode element)
        {
            if (element == null) { return; }
            List<Synchronizer> synchronizers = wp.Synchronizers;
            synchronizers.Clear();
            foreach (XmlNode node in element.ChildNodes)
            {
                if (node.LocalName.Equals(SYNCHRONIZER))
                {
                    synchronizers.Add(createSynchronizer(wp, (XmlElement)node));
                }
            }
        }
        protected Synchronizer createSynchronizer(WorkflowProcess wp, XmlElement element)
        {
            if (element == null) { return null; }

            Synchronizer synchronizer = new Synchronizer(wp, element.GetAttribute(NAME));
            synchronizer.Sn=Guid.NewGuid().ToString();
            synchronizer.DisplayName=element.GetAttribute(DISPLAY_NAME);
            foreach (XmlNode node in element.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: synchronizer.Description=node.InnerText; break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(synchronizer.ExtendedAttributes, node); break;
                }
            }
            return synchronizer;
        }
        #endregion

        #region EndNodes
        protected void loadEndNodes(WorkflowProcess wp, XmlNode element)
        {
            if (element == null) { return; }
            List<EndNode> endNodes = wp.EndNodes;
            endNodes.Clear();
            foreach (XmlNode node in element.ChildNodes)
            {
                if (node.LocalName.Equals(END_NODE))
                {
                    endNodes.Add(createEndNode(wp, (XmlElement)node));
                }
            }
        }
        protected EndNode createEndNode(WorkflowProcess wp, XmlElement element)
        {
            if (element == null) { return null; }

            EndNode endNode = new EndNode(wp, element.GetAttribute(NAME));
            endNode.Sn=Guid.NewGuid().ToString();
            endNode.DisplayName=element.GetAttribute(DISPLAY_NAME);
            foreach (XmlNode node in element.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: endNode.Description=node.InnerText; break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(endNode.ExtendedAttributes, node); break;
                }
            }
            return endNode;
        }
        #endregion

        #region Transitions

        protected void loadTransitions(WorkflowProcess wp, XmlNode element)
        {
            if (element == null) { return; }
            List<Transition> transitions = wp.Transitions;
            transitions.Clear();
            foreach (XmlNode node in element.ChildNodes)
            {
                if (node.LocalName.Equals(TRANSITION))
                {
                    Transition transition = createTransition(wp, (XmlElement)node);
                    transitions.Add(transition);

                    Node fromNode = transition.FromNode;
                    Node toNode = transition.ToNode;
                    if (fromNode != null && (fromNode is Activity))
                    {
                        ((Activity)fromNode).LeavingTransition=transition;
                    }
                    else if (fromNode != null && (fromNode is Synchronizer))
                    {
                        ((Synchronizer)fromNode).LeavingTransitions.Add(
                                transition);
                    }
                    if (toNode != null && (toNode is Activity))
                    {
                        ((Activity)toNode).EnteringTransition=transition;
                    }
                    else if (toNode != null && (toNode is Synchronizer))
                    {
                        ((Synchronizer)toNode).EnteringTransitions.Add(transition);
                    }
                }
            }
        }
        protected Transition createTransition(WorkflowProcess wp, XmlElement element)
        {
            if (element == null) { return null; }
            String fromNodeId = element.GetAttribute(FROM);
            String toNodeId = element.GetAttribute(TO);
            Node fromNode = (Node)wp.findWFElementById(fromNodeId);
            Node toNode = (Node)wp.findWFElementById(toNodeId);

            Transition transition = new Transition(wp, element.GetAttribute(NAME), fromNode, toNode);
            transition.Sn=Guid.NewGuid().ToString();

            transition.DisplayName=element.GetAttribute(DISPLAY_NAME);
            foreach (XmlNode node in element.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: transition.Description=node.InnerText; break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(transition.ExtendedAttributes, node); break;
                    case CONDITION: transition.Condition=node.InnerText; break;
                }
            }
            return transition;
        }
        #endregion

        #region Loops

        protected void loadLoops(WorkflowProcess wp, XmlNode loopsElement)
        {
            if (loopsElement == null) return;

            List<Loop> loops = wp.Loops;
            loops.Clear();

            foreach (XmlNode node in loopsElement.ChildNodes)
            {
                if (node.LocalName.Equals(LOOP))
                {
                    Loop loop = createLoop(wp, (XmlElement)node);
                    loops.Add(loop);

                    Synchronizer fromNode = (Synchronizer)loop.FromNode;
                    Synchronizer toNode = (Synchronizer)loop.ToNode;

                    fromNode.LeavingLoops.Add(loop);
                    toNode.EnteringLoops.Add(loop);
                }
            }
        }

        protected Loop createLoop(WorkflowProcess wp, XmlElement loopElement)
        {
            if (loopElement == null) { return null; }

            String fromNodeId = loopElement.GetAttribute(FROM);
            String toNodeId = loopElement.GetAttribute(TO);
            Synchronizer fromNode = (Synchronizer)wp.findWFElementById(fromNodeId);
            Synchronizer toNode = (Synchronizer)wp.findWFElementById(toNodeId);

            Loop loop = new Loop(wp, loopElement.GetAttribute(NAME), fromNode, toNode);
            loop.Sn=Guid.NewGuid().ToString();

            loop.DisplayName=loopElement.GetAttribute(DISPLAY_NAME);

            foreach (XmlNode node in loopElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: loop.Description=node.InnerText; break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(loop.ExtendedAttributes, node); break;
                    case CONDITION: loop.Condition=node.InnerText; break;
                }
            }
            return loop;
        }

        #endregion

    }
}
