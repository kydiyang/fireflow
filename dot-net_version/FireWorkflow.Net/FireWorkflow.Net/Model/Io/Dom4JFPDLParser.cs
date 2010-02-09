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
            wp.setSn(System.Guid.NewGuid().ToString());

            wp.setTaskInstanceCreator(workflowProcessElement.GetAttribute(TASK_INSTANCE_CREATOR));
            wp.setFormTaskInstanceRunner(workflowProcessElement.GetAttribute(FORM_TASK_INSTANCE_RUNNER));
            wp.setToolTaskInstanceRunner(workflowProcessElement.GetAttribute(TOOL_TASK_INSTANCE_RUNNER));
            wp.setSubflowTaskInstanceRunner(workflowProcessElement.GetAttribute(SUBFLOW_TASK_INSTANCE_RUNNER));
            wp.setFormTaskInstanceCompletionEvaluator(workflowProcessElement.GetAttribute(FORM_TASK_INSTANCE_COMPLETION_EVALUATOR));
            wp.setToolTaskInstanceCompletionEvaluator(workflowProcessElement.GetAttribute(TOOL_TASK_INSTANCE_COMPLETION_EVALUATOR));
            wp.setSubflowTaskInstanceCompletionEvaluator(workflowProcessElement.GetAttribute(SUBFLOW_TASK_INSTANCE_COMPLETION_EVALUATOR));
            wp.setDisplayName(workflowProcessElement.GetAttribute(DISPLAY_NAME));
            wp.setResourceFile(workflowProcessElement.GetAttribute(RESOURCE_FILE));
            wp.setResourceManager(workflowProcessElement.GetAttribute(RESOURCE_MANAGER));

            foreach (XmlNode node in workflowProcessElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: wp.setDescription(node.InnerText); break;
                    case DATA_FIELDS: loadDataFields(wp, node); break;
                    case START_NODE: loadStartNode(wp, node); break;
                    case TASKS: loadTasks(wp, wp.getTasks(), node); break;
                    case ACTIVITIES: loadActivities(wp, node); break;
                    case SYNCHRONIZERS: loadSynchronizers(wp, node); break;
                    case END_NODES: loadEndNodes(wp, node); break;
                    case TRANSITIONS: loadTransitions(wp, node); break;
                    case LOOPS: loadLoops(wp, node); break;

                    case EVENT_LISTENERS: loadEventListeners(wp.getEventListeners(), node); break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(wp.getExtendedAttributes(), (XmlElement)node); break;
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
                    listener.setClassName(((XmlElement)node).GetAttribute(CLASS_NAME));
                    listeners.Add(listener);
                }
            }
        }
        #endregion

        #region DataFields
        protected void loadDataFields(WorkflowProcess wp, XmlNode element)
        {
            if (element == null) { return; }
            List<DataField> dataFields = wp.getDataFields();
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
            String dataType = element.GetAttribute(DATA_TYPE);
            if (String.IsNullOrEmpty(dataType)) { dataType = DataField.STRING; }

            DataField dataField = new DataField(wp, element.GetAttribute(NAME), dataType);

            dataField.setSn(Guid.NewGuid().ToString());

            dataField.setDisplayName(element.GetAttribute(DISPLAY_NAME));
            dataField.setInitialValue(element.GetAttribute(INITIAL_VALUE));

            foreach (XmlNode node in element.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: dataField.setDescription(node.InnerText); break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(dataField.getExtendedAttributes(), node); break;
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
            startNode.setName(((XmlElement)element).GetAttribute(NAME));
            startNode.setSn(Guid.NewGuid().ToString());
            startNode.setDisplayName(((XmlElement)element).GetAttribute(DISPLAY_NAME));

            foreach (XmlNode node in element.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: startNode.setDescription(node.InnerText); break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(startNode.getExtendedAttributes(), node); break;
                }
            }
            wp.setStartNode(startNode);
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
            String type = taskElement.GetAttribute(TYPE);
            switch (type)
            {
                case Task.FORM:
                    task = new FormTask(parent, taskElement.GetAttribute(NAME));
                    ((FormTask)task).setAssignmentStrategy(taskElement.GetAttribute(COMPLETION_STRATEGY));
                    ((FormTask)task).setDefaultView(taskElement.GetAttribute(DEFAULT_VIEW));


                    break;
                case Task.TOOL:
                    task = new ToolTask(parent, taskElement.GetAttribute(NAME));

                    break;
                case Task.SUBFLOW:
                    task = new SubflowTask(parent, taskElement.GetAttribute(NAME));
                    break;
                default: return null;
            }

            task.setSn(Guid.NewGuid().ToString());
            task.setDisplayName(taskElement.GetAttribute(DISPLAY_NAME));
            task.setTaskInstanceCreator(taskElement.GetAttribute(TASK_INSTANCE_CREATOR));
            task.setTaskInstanceRunner(taskElement.GetAttribute(TASK_INSTANCE_RUNNER));
            task.setTaskInstanceCompletionEvaluator(taskElement.GetAttribute(TASK_INSTANCE_COMPLETION_EVALUATOR));
            task.setLoopStrategy(taskElement.GetAttribute(LOOP_STRATEGY));

            int priority = 0;
            try { priority = Int32.Parse(taskElement.GetAttribute(PRIORITY)); }
            catch { }
            task.setPriority(priority);

            foreach (XmlNode node in taskElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    //FormTask
                    case PERFORMER: if (task is FormTask) ((FormTask)task).setPerformer(createPerformer((XmlElement)node)); break;
                    case EDIT_FORM: if (task is FormTask) ((FormTask)task).setEditForm(createForm((XmlElement)node)); break;
                    case VIEW_FORM: if (task is FormTask) ((FormTask)task).setViewForm(createForm((XmlElement)node)); break;
                    case LIST_FORM: if (task is FormTask) ((FormTask)task).setListForm(createForm((XmlElement)node)); break;
                    //ToolTask
                    case APPLICATION: if (task is ToolTask) ((ToolTask)task).setApplication(createApplication((XmlElement)node)); break;
                    //SubflowTask
                    case SUB_WORKFLOW_PROCESS: if (task is SubflowTask) ((SubflowTask)task).setSubWorkflowProcess(createSubWorkflowProcess((XmlElement)node)); break;
                    //else
                    case DESCRIPTION: task.setDescription(node.InnerText); break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(task.getExtendedAttributes(), node); break;
                    case DURATION: task.setDuration(createDuration((XmlElement)node)); break;
                    case EVENT_LISTENERS: loadEventListeners(task.getEventListeners(), node); break;
                }
            }
            return task;

        }
        protected Participant createPerformer(XmlElement performerElement)
        {
            if (performerElement == null) { return null; }
            Participant part = new Participant(performerElement.GetAttribute(NAME));
            part.setDisplayName(performerElement.GetAttribute(DISPLAY_NAME));
            foreach (XmlNode node in performerElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: part.setDescription(node.InnerText); break;
                    case ASSIGNMENT_HANDLER: part.setAssignmentHandler(node.InnerText); break;
                }
            }
            return part;
        }

        protected Form createForm(XmlElement formElement)
        {
            if (formElement == null) { return null; }
            Form form = new Form(formElement.GetAttribute(NAME));
            form.setDisplayName(formElement.GetAttribute(DISPLAY_NAME));
            foreach (XmlNode node in formElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: form.setDescription(node.InnerText); break;
                    case URI: form.setUri(node.InnerText); break;
                }
            }
            return form;
        }
        protected Application createApplication(XmlElement applicationElement)
        {
            if (applicationElement == null) { return null; }
            Application app = new Application(applicationElement.GetAttribute(APPLICATION));
            app.setDisplayName(applicationElement.GetAttribute(DISPLAY_NAME));
            foreach (XmlNode node in applicationElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: app.setDescription(node.InnerText); break;
                    case HANDLER: app.setHandler(node.InnerText); break;
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
            subFlow.setDisplayName(subFlowElement.GetAttribute(DISPLAY_NAME));
            foreach (XmlNode node in subFlowElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: subFlow.setDescription(node.InnerText); break;
                    case WORKFLOW_PROCESS_ID: subFlow.setWorkflowProcessId(node.InnerText); break;
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
            Duration duration = new Duration(value, durationElement.GetAttribute(UNIT));
            duration.setBusinessTime(isBusinessTime);
            return duration;
        }
        #endregion

        #region Activities
        protected void loadActivities(WorkflowProcess wp, XmlNode element)
        {
            if (element == null) { return; }
            List<Activity> activities = wp.getActivities();
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
            activity.setSn(Guid.NewGuid().ToString());
            activity.setDisplayName(element.GetAttribute(DISPLAY_NAME));
            activity.setCompletionStrategy(element.GetAttribute(COMPLETION_STRATEGY));

            foreach (XmlNode node in element.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: activity.setDescription(node.InnerText); break;
                    case EVENT_LISTENERS: loadEventListeners(activity.getEventListeners(), node); break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(activity.getExtendedAttributes(), node); break;

                    case TASKS: loadTasks(activity, activity.getInlineTasks(), node); break;
                    case TASKREFS: loadTaskRefs((WorkflowProcess)activity.getParent(), activity, activity.getTaskRefs(), node); break;
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
                        taskRef.setSn(Guid.NewGuid().ToString());
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
            List<Synchronizer> synchronizers = wp.getSynchronizers();
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
            synchronizer.setSn(Guid.NewGuid().ToString());
            synchronizer.setDisplayName(element.GetAttribute(DISPLAY_NAME));
            foreach (XmlNode node in element.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: synchronizer.setDescription(node.InnerText); break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(synchronizer.getExtendedAttributes(), node); break;
                }
            }
            return synchronizer;
        }
        #endregion

        #region EndNodes
        protected void loadEndNodes(WorkflowProcess wp, XmlNode element)
        {
            if (element == null) { return; }
            List<EndNode> endNodes = wp.getEndNodes();
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
            endNode.setSn(Guid.NewGuid().ToString());
            endNode.setDisplayName(element.GetAttribute(DISPLAY_NAME));
            foreach (XmlNode node in element.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: endNode.setDescription(node.InnerText); break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(endNode.getExtendedAttributes(), node); break;
                }
            }
            return endNode;
        }
        #endregion

        #region Transitions

        protected void loadTransitions(WorkflowProcess wp, XmlNode element)
        {
            if (element == null) { return; }
            List<Transition> transitions = wp.getTransitions();
            transitions.Clear();
            foreach (XmlNode node in element.ChildNodes)
            {
                if (node.LocalName.Equals(TRANSITION))
                {
                    Transition transition = createTransition(wp, (XmlElement)node);
                    transitions.Add(transition);

                    Node fromNode = transition.getFromNode();
                    Node toNode = transition.getToNode();
                    if (fromNode != null && (fromNode is Activity))
                    {
                        ((Activity)fromNode).setLeavingTransition(transition);
                    }
                    else if (fromNode != null && (fromNode is Synchronizer))
                    {
                        ((Synchronizer)fromNode).getLeavingTransitions().Add(
                                transition);
                    }
                    if (toNode != null && (toNode is Activity))
                    {
                        ((Activity)toNode).setEnteringTransition(transition);
                    }
                    else if (toNode != null && (toNode is Synchronizer))
                    {
                        ((Synchronizer)toNode).getEnteringTransitions().Add(transition);
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
            transition.setSn(Guid.NewGuid().ToString());

            transition.setDisplayName(element.GetAttribute(DISPLAY_NAME));
            foreach (XmlNode node in element.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: transition.setDescription(node.InnerText); break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(transition.getExtendedAttributes(), node); break;
                    case CONDITION: transition.setCondition(node.InnerText); break;
                }
            }
            return transition;
        }
        #endregion

        #region Loops

        protected void loadLoops(WorkflowProcess wp, XmlNode loopsElement)
        {
            if (loopsElement == null) return;

            List<Loop> loops = wp.getLoops();
            loops.Clear();

            foreach (XmlNode node in loopsElement.ChildNodes)
            {
                if (node.LocalName.Equals(LOOP))
                {
                    Loop loop = createLoop(wp, (XmlElement)node);
                    loops.Add(loop);

                    Synchronizer fromNode = (Synchronizer)loop.getFromNode();
                    Synchronizer toNode = (Synchronizer)loop.getToNode();

                    fromNode.getLeavingLoops().Add(loop);
                    toNode.getEnteringLoops().Add(loop);
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
            loop.setSn(Guid.NewGuid().ToString());

            loop.setDisplayName(loopElement.GetAttribute(DISPLAY_NAME));

            foreach (XmlNode node in loopElement.ChildNodes)
            {
                switch (node.LocalName)
                {
                    case DESCRIPTION: loop.setDescription(node.InnerText); break;
                    case EXTENDED_ATTRIBUTES: loadExtendedAttributes(loop.getExtendedAttributes(), node); break;
                    case CONDITION: loop.setCondition(node.InnerText); break;
                }
            }
            return loop;
        }

        #endregion

    }
}
