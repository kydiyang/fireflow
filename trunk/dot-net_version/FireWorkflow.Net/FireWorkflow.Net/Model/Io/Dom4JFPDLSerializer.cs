using System;
using System.Collections.Generic;
using System.Xml;
using System.Xml.Serialization;
using System.IO;
using System.Text;
using FireWorkflow.Net.Model.Net;
using FireWorkflow.Net.Model.Resource;

namespace FireWorkflow.Net.Model.Io
{
    /// <summary>
    /// FPDL序列化器。将WorkflowProcess对象序列化到一个输出流。
    /// </summary>
    public class Dom4JFPDLSerializer : IFPDLSerializer
    {
        XmlDocument doc = new XmlDocument();
        public override void serialize(WorkflowProcess workflowProcess, Stream swout)
        {
            if (swout == null) return;
            XmlDocument document = workflowProcessToDom(workflowProcess);
            XmlWriterSettings settings = new XmlWriterSettings();
            settings.Indent = true;
            settings.OmitXmlDeclaration = true;
            settings.NewLineOnAttributes = true;
            settings.Encoding = Encoding.UTF8;


            XmlWriter xw = XmlWriter.Create(swout, settings);

            document.Save(xw);
            swout.Flush();
        }


        public XmlAttribute SetAttributeNode(XmlAttribute attr, string value)
        {
            attr.Value = value;
            return attr;
        }

        public XmlElement SetElement(XmlElement xe, string value)
        {
            xe.InnerText = value;
            return xe;
        }

        public XmlDocument workflowProcessToDom(WorkflowProcess workflowProcess)
        {
            XmlElement workflowProcessElement = doc.CreateElement(FPDL_NS_PREFIX, WORKFLOW_PROCESS, FPDL_URI);

            workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), workflowProcess.getId()));
            workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), workflowProcess.getName()));
            workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), workflowProcess.getDisplayName()));
            workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(RESOURCE_FILE), workflowProcess.getResourceFile()));
            workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(RESOURCE_MANAGER), workflowProcess.getResourceManager()));

            workflowProcessElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), workflowProcess.getDescription()));

            if (!String.IsNullOrEmpty(workflowProcess.getTaskInstanceCreator()))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TASK_INSTANCE_CREATOR), workflowProcess.getTaskInstanceCreator()));
            }
            if (!String.IsNullOrEmpty(workflowProcess.getFormTaskInstanceRunner()))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(FORM_TASK_INSTANCE_RUNNER), workflowProcess.getFormTaskInstanceRunner()));
            }
            if (!String.IsNullOrEmpty(workflowProcess.getToolTaskInstanceRunner()))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TOOL_TASK_INSTANCE_RUNNER), workflowProcess.getToolTaskInstanceRunner()));
            }
            if (!String.IsNullOrEmpty(workflowProcess.getSubflowTaskInstanceRunner()))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(SUBFLOW_TASK_INSTANCE_RUNNER), workflowProcess.getSubflowTaskInstanceRunner()));
            }
            if (!String.IsNullOrEmpty(workflowProcess.getFormTaskInstanceCompletionEvaluator()))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(FORM_TASK_INSTANCE_COMPLETION_EVALUATOR), workflowProcess.getFormTaskInstanceCompletionEvaluator()));
            }
            if (!String.IsNullOrEmpty(workflowProcess.getToolTaskInstanceCompletionEvaluator()))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TOOL_TASK_INSTANCE_COMPLETION_EVALUATOR), workflowProcess.getToolTaskInstanceCompletionEvaluator()));
            }
            if (!String.IsNullOrEmpty(workflowProcess.getSubflowTaskInstanceCompletionEvaluator()))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(SUBFLOW_TASK_INSTANCE_COMPLETION_EVALUATOR), workflowProcess.getSubflowTaskInstanceCompletionEvaluator()));
            }
            writeDataFields(workflowProcess.getDataFields(), workflowProcessElement);
            writeStartNode(workflowProcess.getStartNode(), workflowProcessElement);

            writeTasks(workflowProcess.getTasks(), workflowProcessElement);

            writeActivities(workflowProcess.getActivities(), workflowProcessElement);
            writeSynchronizers(workflowProcess.getSynchronizers(), workflowProcessElement);
            writeEndNodes(workflowProcess.getEndNodes(), workflowProcessElement);
            writeTransitions(workflowProcess.getTransitions(), workflowProcessElement);

            writeLoops(workflowProcess.getLoops(), workflowProcessElement);

            writeEventListeners(workflowProcess.getEventListeners(), workflowProcessElement);

            writeExtendedAttributes(workflowProcess.getExtendedAttributes(), workflowProcessElement);

            //        Document document = df.createDocument(workflowProcessElement);
            //        document.addDocType(FPDL_NS_PREFIX + ":" + WORKFLOW_PROCESS, PUBLIC_ID, SYSTEM_ID);
            //        return document;

            //doc.LoadXml("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            doc.AppendChild(doc.CreateXmlDeclaration("1.0", "UTF-8", ""));
            //doc.AppendChild(doc.CreateDocumentType(FPDL_NS_PREFIX + ":" + WORKFLOW_PROCESS, PUBLIC_ID, SYSTEM_ID, null));
            doc.AppendChild(workflowProcessElement);
            return doc;
        }

        //    public String workflowProcessToXMLString(WorkflowProcess workflowProcess)
        //            throws IOException, FPDLSerializerException {
        //        Document dom = workflowProcessToDom(workflowProcess);
        //        OutputFormat format = new OutputFormat("    ", true);
        //        format.setEncoding("utf-8");
        //        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //        XMLWriter writer = new XMLWriter(out, format);

        //        writer.write(dom);

        //  }

        protected void writeEventListeners(List<EventListener> eventListeners, XmlElement parentElement)
        {
            if (eventListeners == null || eventListeners.Count <= 0) { return; }

            XmlElement eventListenersElm = doc.CreateElement(FPDL_NS_PREFIX, EVENT_LISTENERS, FPDL_URI);

            foreach (EventListener listener in eventListeners)
            {
                XmlElement eventListenerElm = doc.CreateElement(FPDL_NS_PREFIX, EVENT_LISTENER, FPDL_URI);
                eventListenerElm.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(CLASS_NAME), listener.getClassName()));
                eventListenersElm.AppendChild(eventListenerElm);
            }
            parentElement.AppendChild(eventListenersElm);
        }

        #region 序列化Dictionary＜string, string＞类型数据

        /// <summary>序列化Dictionary＜string, string＞类型数据</summary>
        /// <param name="extendedAttributes"></param>
        /// <param name="parent"></param>
        protected void writeExtendedAttributes(Dictionary<string, string> extendedAttributes, XmlElement parent)
        {

            if (extendedAttributes == null || extendedAttributes.Count <= 0)
            {
                return;
            }

            XmlElement extendedAttributesElement = doc.CreateElement(FPDL_NS_PREFIX, EXTENDED_ATTRIBUTES, FPDL_URI);

            foreach (String key in extendedAttributes.Keys)
            {
                String value = extendedAttributes[key];

                XmlElement extendedAttributeElement = doc.CreateElement(FPDL_NS_PREFIX, EXTENDED_ATTRIBUTE, FPDL_URI);

                extendedAttributeElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), key));
                extendedAttributeElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(VALUE), value));
                extendedAttributesElement.AppendChild(extendedAttributeElement);
            }

            parent.AppendChild(extendedAttributesElement);

        }
        #endregion

        #region DataField
        protected void writeDataFields(List<DataField> dataFields, XmlElement parent)
        {
            if (dataFields == null || dataFields.Count <= 0)
            {
                return;
            }
            XmlElement dataFieldsElement = doc.CreateElement(FPDL_NS_PREFIX, DATA_FIELDS, FPDL_URI);

            foreach (DataField dataField in dataFields)
            {
                XmlElement dataFieldElement = dataFieldsElement.OwnerDocument.CreateElement(FPDL_NS_PREFIX, DATA_FIELD, FPDL_URI);

                dataFieldElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), dataField.getId()));
                dataFieldElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), dataField.getName()));
                dataFieldElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), dataField.getDisplayName()));
                dataFieldElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DATA_TYPE), dataField.getDataType()));

                dataFieldElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(INITIAL_VALUE), dataField.getInitialValue()));

                dataFieldElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), dataField.getDescription()));


                writeExtendedAttributes(dataField.getExtendedAttributes(), dataFieldElement);

                dataFieldsElement.AppendChild(dataFieldElement);
            }
            parent.AppendChild(dataFieldsElement);
        }
        #endregion

        #region StartNode
        protected void writeStartNode(StartNode startNode, XmlElement parent)
        {
            if (startNode == null) { return; }
            XmlElement startElement = doc.CreateElement(FPDL_NS_PREFIX, START_NODE, FPDL_URI);

            startElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), startNode.getId()));
            startElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), startNode.getName()));
            startElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), startNode.getDisplayName()));


            startElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), startNode.getDescription()));


            writeExtendedAttributes(startNode.getExtendedAttributes(), startElement);
            parent.AppendChild(startElement);

        }
        #endregion

        #region Tasks
        protected void writeTasks(List<Task> tasks, XmlElement parent)
        {
            XmlElement tasksElement = doc.CreateElement(FPDL_NS_PREFIX, TASKS, FPDL_URI);

            foreach (Task item in tasks)
            {
                writeTask(item, tasksElement);
            }
            parent.AppendChild(tasksElement);
        }

        protected void writeTask(Task task, XmlElement parent)
        {
            XmlElement taskElement = doc.CreateElement(FPDL_NS_PREFIX, TASK, FPDL_URI);

            taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), task.getId()));
            taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), task.getName()));
            taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), task.getDisplayName()));
            taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TYPE), task.getType()));

            String type = task.getType();
            if (task is FormTask)
            {
                this.writePerformer(((FormTask)task).getPerformer(), taskElement);

                taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(COMPLETION_STRATEGY), ((FormTask)task).getAssignmentStrategy()));
                taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DEFAULT_VIEW), ((FormTask)task).getDefaultView()));

                writeForm(EDIT_FORM, ((FormTask)task).getEditForm(), taskElement);
                writeForm(VIEW_FORM, ((FormTask)task).getViewForm(), taskElement);
                writeForm(LIST_FORM, ((FormTask)task).getListForm(), taskElement);
            }
            else if (task is ToolTask)
            {

                this.writeApplication(((ToolTask)task).getApplication(), taskElement);
                taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(EXECUTION), ((ToolTask)task).getExecution()));

            }
            else if (task is SubflowTask)
            {
                this.writeSubWorkflowProcess(((SubflowTask)task).getSubWorkflowProcess(), taskElement);
            }

            taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(PRIORITY), task.getPriority().ToString()));

            writeDuration(task.getDuration(), taskElement);

            taskElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), task.getDescription()));


            if (!String.IsNullOrEmpty(task.getTaskInstanceCreator()))
            {
                taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TASK_INSTANCE_CREATOR), task.getTaskInstanceCreator()));
            }
            if (!String.IsNullOrEmpty(task.getTaskInstanceRunner()))
            {
                taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TASK_INSTANCE_RUNNER), task.getTaskInstanceRunner()));

            }
            if (!String.IsNullOrEmpty(task.getTaskInstanceCompletionEvaluator()))
            {
                taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TASK_INSTANCE_COMPLETION_EVALUATOR), task.getTaskInstanceCompletionEvaluator()));

            }

            if (!String.IsNullOrEmpty(task.getLoopStrategy()))
            {
                taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(LOOP_STRATEGY), task.getLoopStrategy()));

            }

            writeEventListeners(task.getEventListeners(), taskElement);
            writeExtendedAttributes(task.getExtendedAttributes(), taskElement);

            parent.AppendChild(taskElement);
        }

        protected void writePerformer(Participant participant, XmlElement parent)
        {
            if (participant == null) { return; }

            XmlElement participantElement = doc.CreateElement(FPDL_NS_PREFIX, PERFORMER, FPDL_URI);

            participantElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), participant.getName()));
            participantElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), participant.getDisplayName()));

            participantElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), participant.getDescription()));
            participantElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, ASSIGNMENT_HANDLER, FPDL_URI), participant.getAssignmentHandler()));

            parent.AppendChild(participantElement);
        }

        protected void writeForm(String formName, Form form, XmlElement parent)
        {
            if (form == null) { return; }
            XmlElement editFormElement = doc.CreateElement(FPDL_NS_PREFIX, formName, FPDL_URI);

            editFormElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), form.getName()));
            editFormElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), form.getDisplayName()));

            editFormElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), form.getDescription()));
            editFormElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, URI, FPDL_URI), form.getUri()));

            parent.AppendChild(editFormElement);
        }

        protected void writeApplication(Application application, XmlElement parent)
        {
            if (application == null) { return; }
            XmlElement applicationElement = doc.CreateElement(FPDL_NS_PREFIX, APPLICATION, FPDL_URI);

            applicationElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), application.getName()));
            applicationElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), application.getDisplayName()));

            applicationElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), application.getDescription()));
            applicationElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, HANDLER, FPDL_URI), application.getHandler()));

            parent.AppendChild(applicationElement);
        }

        protected void writeSubWorkflowProcess(SubWorkflowProcess subWorkflowProcess, XmlElement parent)
        {
            if (subWorkflowProcess == null) { return; }
            XmlElement subflowElement = doc.CreateElement(FPDL_NS_PREFIX, SUB_WORKFLOW_PROCESS, FPDL_URI);

            subflowElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), subWorkflowProcess.getName()));
            subflowElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), subWorkflowProcess.getDisplayName()));

            subflowElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), subWorkflowProcess.getDescription()));
            subflowElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, WORKFLOW_PROCESS_ID, FPDL_URI), subWorkflowProcess.getWorkflowProcessId()));

            parent.AppendChild(subflowElement);
        }

        protected void writeDuration(Duration duration, XmlElement parent)
        {
            if (duration == null) { return; }

            XmlElement durationElement = doc.CreateElement(FPDL_NS_PREFIX, DURATION, FPDL_URI);

            durationElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(VALUE), duration.getValue().ToString()));
            durationElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(UNIT), duration.getUnit()));
            durationElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(IS_BUSINESS_TIME), duration.IsBusinessTime().ToString()));

            parent.AppendChild(durationElement);
        }
        #endregion

        #region Activitie
        protected void writeActivities(List<Activity> activities, XmlElement parent)
        {

            if (activities == null || activities.Count <= 0) { return; }

            XmlElement activitiesElement = doc.CreateElement(FPDL_NS_PREFIX, ACTIVITIES, FPDL_URI);

            foreach (Activity item in activities)
            {

                writeActivity(item, activitiesElement);
            }
            parent.AppendChild(activitiesElement);

        }

        protected void writeActivity(Activity activity, XmlElement parent)
        {

            if (activity == null) { return; }


            XmlElement activityElement = doc.CreateElement(FPDL_NS_PREFIX, ACTIVITY, FPDL_URI);

            activityElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), activity.getId()));
            activityElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), activity.getName()));
            activityElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), activity.getDisplayName()));
            activityElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(COMPLETION_STRATEGY), activity.getCompletionStrategy()));

            activityElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), activity.getDescription()));

            writeEventListeners(activity.getEventListeners(), activityElement);
            writeExtendedAttributes(activity.getExtendedAttributes(), activityElement);

            writeTasks(activity.getInlineTasks(), activityElement);
            writeTaskRefs(activity.getTaskRefs(), activityElement);

            parent.AppendChild(activityElement);
        }

        protected void writeTaskRefs(List<TaskRef> taskRefs, XmlElement parent)
        {
            if (taskRefs == null || taskRefs.Count <= 0) { return; }


            XmlElement taskRefsElement = doc.CreateElement(FPDL_NS_PREFIX, TASKREFS, FPDL_URI);

            foreach (TaskRef taskRef in taskRefs)
            {
                XmlElement taskRefElement = doc.CreateElement(FPDL_NS_PREFIX, TASKREF, FPDL_URI);

                taskRefElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(REFERENCE), taskRef.getReferencedTask().getId()));
                taskRefsElement.AppendChild(taskRefElement);
            }
            parent.AppendChild(taskRefsElement);

        }
        #endregion

        #region Synchronizer
        protected void writeSynchronizers(List<Synchronizer> synchronizers, XmlElement parent)
        {

            if (synchronizers == null || synchronizers.Count <= 0) { return; }


            XmlElement synchronizersElement = doc.CreateElement(FPDL_NS_PREFIX, SYNCHRONIZERS, FPDL_URI);

            foreach (Synchronizer item in synchronizers)
            {
                writeSynchronizer(item, synchronizersElement);
            }
            parent.AppendChild(synchronizersElement);
        }

        protected void writeSynchronizer(Synchronizer synchronizer, XmlElement parent)
        {
            if (synchronizer == null) { return; }

            XmlElement synchronizerElement = doc.CreateElement(FPDL_NS_PREFIX, SYNCHRONIZER, FPDL_URI);

            synchronizerElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), synchronizer.getId()));
            synchronizerElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), synchronizer.getName()));
            synchronizerElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), synchronizer.getDisplayName()));

            synchronizerElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), synchronizer.getDescription()));

            writeExtendedAttributes(synchronizer.getExtendedAttributes(), synchronizerElement);
            parent.AppendChild(synchronizerElement);
        }
        #endregion

        #region EndNode
        protected void writeEndNodes(List<EndNode> endNodes, XmlElement parent)
        {
            if (endNodes == null || endNodes.Count <= 0) { return; }

            XmlElement endNodesElement = doc.CreateElement(FPDL_NS_PREFIX, END_NODES, FPDL_URI);

            foreach (EndNode item in endNodes)
            {
                writeEndNode(item, endNodesElement);
            }
            parent.AppendChild(endNodesElement);
        }

        protected void writeEndNode(EndNode endNode, XmlElement parent)
        {
            if (endNode == null) { return; }

            XmlElement endNodeElement = doc.CreateElement(FPDL_NS_PREFIX, END_NODE, FPDL_URI);

            endNodeElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), endNode.getId()));
            endNodeElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), endNode.getName()));
            endNodeElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), endNode.getDisplayName()));

            endNodeElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), endNode.getDescription()));

            writeExtendedAttributes(endNode.getExtendedAttributes(), endNodeElement);

            parent.AppendChild(endNodeElement);
        }
        #endregion

        #region Transitions
        protected void writeTransitions(List<Transition> transitions, XmlElement parent)
        {
            if (transitions == null || transitions.Count <= 0) { return; }

            XmlElement transitionsElement = doc.CreateElement(FPDL_NS_PREFIX, TRANSITIONS, FPDL_URI);

            foreach (Transition item in transitions)
            {
                writeTransition(item, transitionsElement);
            }
            parent.AppendChild(transitionsElement);
        }

        protected void writeTransition(Transition transition, XmlElement parent)
        {
            if (transition == null) { return; }

            XmlElement transitionElement = doc.CreateElement(FPDL_NS_PREFIX, TRANSITION, FPDL_URI);

            transitionElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), transition.getId()));
            transitionElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(FROM), transition.getFromNode().getId()));
            transitionElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TO), transition.getToNode().getId()));
            transitionElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), transition.getName()));
            transitionElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), transition.getDisplayName()));

            transitionElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, CONDITION, FPDL_URI), transition.getCondition()));
            //transitionElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), transition.getDescription())); ??是否需要

            writeExtendedAttributes(transition.getExtendedAttributes(), transitionElement);

            parent.AppendChild(transitionElement);
        }
        #endregion

        #region Loops
        protected void writeLoops(List<Loop> loops, XmlElement parent)
        {
            if (loops == null || loops.Count <= 0) { return; }
            XmlElement transitionsElement = doc.CreateElement(FPDL_NS_PREFIX, LOOPS, FPDL_URI);

            foreach (Loop loop in loops)
            {
                XmlElement loopElement = doc.CreateElement(FPDL_NS_PREFIX, LOOP, FPDL_URI);

                loopElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), loop.getId()));
                loopElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(FROM), loop.getFromNode().getId()));
                loopElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TO), loop.getToNode().getId()));
                loopElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), loop.getName()));
                loopElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), loop.getDisplayName()));

                loopElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, CONDITION, FPDL_URI), loop.getCondition()));

                writeExtendedAttributes(loop.getExtendedAttributes(), loopElement);

                transitionsElement.AppendChild(loopElement);

            }
            parent.AppendChild(transitionsElement);
        }
        #endregion

    }
}
