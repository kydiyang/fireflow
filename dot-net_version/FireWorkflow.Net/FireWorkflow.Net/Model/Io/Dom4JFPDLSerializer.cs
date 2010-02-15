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
 @Revision to .NET 无忧 lwz0721@gmail.com 2010-02
 */
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

            workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), workflowProcess.Id));
            workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), workflowProcess.Name));
            workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), workflowProcess.DisplayName));
            workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(RESOURCE_FILE), workflowProcess.ResourceFile));
            workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(RESOURCE_MANAGER), workflowProcess.ResourceManager));

            workflowProcessElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), workflowProcess.Description));

            if (!String.IsNullOrEmpty(workflowProcess.TaskInstanceCreator))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TASK_INSTANCE_CREATOR), workflowProcess.TaskInstanceCreator));
            }
            if (!String.IsNullOrEmpty(workflowProcess.FormTaskInstanceRunner))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(FORM_TASK_INSTANCE_RUNNER), workflowProcess.FormTaskInstanceRunner));
            }
            if (!String.IsNullOrEmpty(workflowProcess.ToolTaskInstanceRunner))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TOOL_TASK_INSTANCE_RUNNER), workflowProcess.ToolTaskInstanceRunner));
            }
            if (!String.IsNullOrEmpty(workflowProcess.SubflowTaskInstanceRunner))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(SUBFLOW_TASK_INSTANCE_RUNNER), workflowProcess.SubflowTaskInstanceRunner));
            }
            if (!String.IsNullOrEmpty(workflowProcess.FormTaskInstanceCompletionEvaluator))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(FORM_TASK_INSTANCE_COMPLETION_EVALUATOR), workflowProcess.FormTaskInstanceCompletionEvaluator));
            }
            if (!String.IsNullOrEmpty(workflowProcess.ToolTaskInstanceCompletionEvaluator))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TOOL_TASK_INSTANCE_COMPLETION_EVALUATOR), workflowProcess.ToolTaskInstanceCompletionEvaluator));
            }
            if (!String.IsNullOrEmpty(workflowProcess.SubflowTaskInstanceCompletionEvaluator))
            {
                workflowProcessElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(SUBFLOW_TASK_INSTANCE_COMPLETION_EVALUATOR), workflowProcess.SubflowTaskInstanceCompletionEvaluator));
            }
            writeDataFields(workflowProcess.DataFields, workflowProcessElement);
            writeStartNode(workflowProcess.StartNode, workflowProcessElement);

            writeTasks(workflowProcess.Tasks, workflowProcessElement);

            writeActivities(workflowProcess.Activities, workflowProcessElement);
            writeSynchronizers(workflowProcess.Synchronizers, workflowProcessElement);
            writeEndNodes(workflowProcess.EndNodes, workflowProcessElement);
            writeTransitions(workflowProcess.Transitions, workflowProcessElement);

            writeLoops(workflowProcess.Loops, workflowProcessElement);

            writeEventListeners(workflowProcess.EventListeners, workflowProcessElement);

            writeExtendedAttributes(workflowProcess.ExtendedAttributes, workflowProcessElement);

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
                eventListenerElm.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(CLASS_NAME), listener.ClassName));
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

                dataFieldElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), dataField.Id));
                dataFieldElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), dataField.Name));
                dataFieldElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), dataField.DisplayName));
                dataFieldElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DATA_TYPE), dataField.DataType.ToString()));

                dataFieldElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(INITIAL_VALUE), dataField.InitialValue));

                dataFieldElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), dataField.Description));


                writeExtendedAttributes(dataField.ExtendedAttributes, dataFieldElement);

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

            startElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), startNode.Id));
            startElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), startNode.Name));
            startElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), startNode.DisplayName));


            startElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), startNode.Description));


            writeExtendedAttributes(startNode.ExtendedAttributes, startElement);
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

            taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), task.Id));
            taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), task.Name));
            taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), task.DisplayName));
            taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TYPE), task.TaskType.ToString()));

            TaskTypeEnum type = task.TaskType;
            if (task is FormTask)
            {
                this.writePerformer(((FormTask)task).Performer, taskElement);

                taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(COMPLETION_STRATEGY), ((FormTask)task).AssignmentStrategy.ToString()));
                taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DEFAULT_VIEW), ((FormTask)task).DefaultView.ToString()));

                writeForm(EDIT_FORM, ((FormTask)task).EditForm, taskElement);
                writeForm(VIEW_FORM, ((FormTask)task).ViewForm, taskElement);
                writeForm(LIST_FORM, ((FormTask)task).ListForm, taskElement);
            }
            else if (task is ToolTask)
            {

                this.writeApplication(((ToolTask)task).Application, taskElement);
                //taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(EXECUTION), ((ToolTask)task).Execution.ToString()));

            }
            else if (task is SubflowTask)
            {
                this.writeSubWorkflowProcess(((SubflowTask)task).SubWorkflowProcess, taskElement);
            }

            taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(PRIORITY), task.Priority.ToString()));

            writeDuration(task.Duration, taskElement);

            taskElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), task.Description));


            if (!String.IsNullOrEmpty(task.TaskInstanceCreator))
            {
                taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TASK_INSTANCE_CREATOR), task.TaskInstanceCreator));
            }
            if (!String.IsNullOrEmpty(task.TaskInstanceRunner))
            {
                taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TASK_INSTANCE_RUNNER), task.TaskInstanceRunner));

            }
            if (!String.IsNullOrEmpty(task.TaskInstanceCompletionEvaluator))
            {
                taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TASK_INSTANCE_COMPLETION_EVALUATOR), task.TaskInstanceCompletionEvaluator));

            }

            taskElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(LOOP_STRATEGY), task.LoopStrategy.ToString()));

            writeEventListeners(task.EventListeners, taskElement);
            writeExtendedAttributes(task.ExtendedAttributes, taskElement);

            parent.AppendChild(taskElement);
        }

        protected void writePerformer(Participant participant, XmlElement parent)
        {
            if (participant == null) { return; }

            XmlElement participantElement = doc.CreateElement(FPDL_NS_PREFIX, PERFORMER, FPDL_URI);

            participantElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), participant.Name));
            participantElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), participant.DisplayName));

            participantElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), participant.Description));
            participantElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, ASSIGNMENT_HANDLER, FPDL_URI), participant.AssignmentHandler));

            parent.AppendChild(participantElement);
        }

        protected void writeForm(String formName, Form form, XmlElement parent)
        {
            if (form == null) { return; }
            XmlElement editFormElement = doc.CreateElement(FPDL_NS_PREFIX, formName, FPDL_URI);

            editFormElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), form.Name));
            editFormElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), form.DisplayName));

            editFormElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), form.Description));
            editFormElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, URI, FPDL_URI), form.Uri));

            parent.AppendChild(editFormElement);
        }

        protected void writeApplication(Application application, XmlElement parent)
        {
            if (application == null) { return; }
            XmlElement applicationElement = doc.CreateElement(FPDL_NS_PREFIX, APPLICATION, FPDL_URI);

            applicationElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), application.Name));
            applicationElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), application.DisplayName));

            applicationElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), application.Description));
            applicationElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, HANDLER, FPDL_URI), application.Handler));

            parent.AppendChild(applicationElement);
        }

        protected void writeSubWorkflowProcess(SubWorkflowProcess subWorkflowProcess, XmlElement parent)
        {
            if (subWorkflowProcess == null) { return; }
            XmlElement subflowElement = doc.CreateElement(FPDL_NS_PREFIX, SUB_WORKFLOW_PROCESS, FPDL_URI);

            subflowElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), subWorkflowProcess.Name));
            subflowElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), subWorkflowProcess.DisplayName));

            subflowElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), subWorkflowProcess.Description));
            subflowElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, WORKFLOW_PROCESS_ID, FPDL_URI), subWorkflowProcess.WorkflowProcessId));

            parent.AppendChild(subflowElement);
        }

        protected void writeDuration(Duration duration, XmlElement parent)
        {
            if (duration == null) { return; }

            XmlElement durationElement = doc.CreateElement(FPDL_NS_PREFIX, DURATION, FPDL_URI);

            durationElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(VALUE), duration.Value.ToString()));
            durationElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(UNIT), duration.Unit.ToString()));
            durationElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(IS_BUSINESS_TIME), duration.IsBusinessTime.ToString()));

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

            activityElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), activity.Id));
            activityElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), activity.Name));
            activityElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), activity.DisplayName));
            activityElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(COMPLETION_STRATEGY), activity.CompletionStrategy.ToString()));

            activityElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), activity.Description));

            writeEventListeners(activity.EventListeners, activityElement);
            writeExtendedAttributes(activity.ExtendedAttributes, activityElement);

            writeTasks(activity.InlineTasks, activityElement);
            writeTaskRefs(activity.TaskRefs, activityElement);

            parent.AppendChild(activityElement);
        }

        protected void writeTaskRefs(List<TaskRef> taskRefs, XmlElement parent)
        {
            if (taskRefs == null || taskRefs.Count <= 0) { return; }


            XmlElement taskRefsElement = doc.CreateElement(FPDL_NS_PREFIX, TASKREFS, FPDL_URI);

            foreach (TaskRef taskRef in taskRefs)
            {
                XmlElement taskRefElement = doc.CreateElement(FPDL_NS_PREFIX, TASKREF, FPDL_URI);

                taskRefElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(REFERENCE), taskRef.ReferencedTask.Id));
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

            synchronizerElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), synchronizer.Id));
            synchronizerElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), synchronizer.Name));
            synchronizerElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), synchronizer.DisplayName));

            synchronizerElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), synchronizer.Description));

            writeExtendedAttributes(synchronizer.ExtendedAttributes, synchronizerElement);
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

            endNodeElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), endNode.Id));
            endNodeElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), endNode.Name));
            endNodeElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), endNode.DisplayName));

            endNodeElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), endNode.Description));

            writeExtendedAttributes(endNode.ExtendedAttributes, endNodeElement);

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

            transitionElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), transition.Id));
            transitionElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(FROM), transition.FromNode.Id));
            transitionElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TO), transition.ToNode.Id));
            transitionElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), transition.Name));
            transitionElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), transition.DisplayName));

            transitionElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, CONDITION, FPDL_URI), transition.Condition));
            //transitionElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, DESCRIPTION, FPDL_URI), transition.Description)); ??是否需要

            writeExtendedAttributes(transition.ExtendedAttributes, transitionElement);

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

                loopElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(ID), loop.Id));
                loopElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(FROM), loop.FromNode.Id));
                loopElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(TO), loop.ToNode.Id));
                loopElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(NAME), loop.Name));
                loopElement.SetAttributeNode(this.SetAttributeNode(doc.CreateAttribute(DISPLAY_NAME), loop.DisplayName));

                loopElement.AppendChild(this.SetElement(doc.CreateElement(FPDL_NS_PREFIX, CONDITION, FPDL_URI), loop.Condition));

                writeExtendedAttributes(loop.ExtendedAttributes, loopElement);

                transitionsElement.AppendChild(loopElement);

            }
            parent.AppendChild(transitionsElement);
        }
        #endregion

    }
}
