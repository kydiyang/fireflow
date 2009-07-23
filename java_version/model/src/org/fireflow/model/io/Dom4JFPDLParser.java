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

 */
package org.fireflow.model.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.UUID;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.fireflow.model.DataField;
import org.fireflow.model.Duration;
import org.fireflow.model.EventListener;
import org.fireflow.model.FormTask;
import org.fireflow.model.IWFElement;
import org.fireflow.model.SubflowTask;
import org.fireflow.model.Task;
import org.fireflow.model.TaskRef;
import org.fireflow.model.ToolTask;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.Loop;
import org.fireflow.model.net.Node;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;
import org.fireflow.model.resource.Application;
import org.fireflow.model.resource.Form;
import org.fireflow.model.resource.Participant;
import org.fireflow.model.resource.SubWorkflowProcess;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Anthony Eden
 * updated by nychen2000
 */
public class Dom4JFPDLParser implements IFPDLParser {

//    String encoding = "utf-8";
//    public Dom4JFPDLParser() {
//    }
//
//    public void setEncoding(String ecd) {
//        this.encoding = ecd;
//    }
    public WorkflowProcess parse(InputStream in) throws IOException,
            FPDLParserException {
        try {
            SAXReader reader = new SAXReader(new DocumentFactory());
            reader.setEntityResolver(new EntityResolver() {

                String emptyDtd = "";
                ByteArrayInputStream bytels = new ByteArrayInputStream(emptyDtd.getBytes());

                public InputSource resolveEntity(String publicId,
                        String systemId) throws SAXException, IOException {
                    return new InputSource(bytels);
                }
            });
            Document document = reader.read(in);

            WorkflowProcess wp = parse(document);
            return wp;
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new FPDLParserException("Error parsing document.", e);
        } finally {
        }
//        return parse(new InputStreamReader(in));
    }

    protected WorkflowProcess parse(Document document) throws FPDLParserException {
        Element workflowProcessElement = document.getRootElement();

        WorkflowProcess wp = new WorkflowProcess(workflowProcessElement.attributeValue(NAME));
        wp.setSn(UUID.randomUUID().toString());
        wp.setDescription(Util4Parser.elementAsString(workflowProcessElement,
                DESCRIPTION));

        wp.setTaskInstanceCreator(workflowProcessElement.attributeValue(TASK_INSTANCE_CREATOR));

        wp.setFormTaskInstanceRunner(workflowProcessElement.attributeValue(FORM_TASK_INSTANCE_RUNNER));

        wp.setToolTaskInstanceRunner(workflowProcessElement.attributeValue(TOOL_TASK_INSTANCE_RUNNER));

        wp.setSubflowTaskInstanceRunner(workflowProcessElement.attributeValue(SUBFLOW_TASK_INSTANCE_RUNNER));

        wp.setFormTaskInstanceCompletionEvaluator(workflowProcessElement.attributeValue(FORM_TASK_INSTANCE_COMPLETION_EVALUATOR));

        wp.setToolTaskInstanceCompletionEvaluator(workflowProcessElement.attributeValue(TOOL_TASK_INSTANCE_COMPLETION_EVALUATOR));
        wp.setSubflowTaskInstanceCompletionEvaluator(workflowProcessElement.attributeValue(SUBFLOW_TASK_INSTANCE_COMPLETION_EVALUATOR));

        wp.setDisplayName(workflowProcessElement.attributeValue(DISPLAY_NAME));
        wp.setResourceFile(workflowProcessElement.attributeValue(RESOURCE_FILE));
        wp.setResourceManager(workflowProcessElement.attributeValue(RESOURCE_MANAGER));


        this.loadDataFields(wp, wp.getDataFields(), Util4Parser.child(
                workflowProcessElement, this.DATA_FIELDS));
        loadStartNode(wp, Util4Parser.child(workflowProcessElement, START_NODE));

        loadTasks(wp, wp.getTasks(),Util4Parser.child(
                    workflowProcessElement, TASKS) );

        loadActivities(wp, wp.getActivities(), Util4Parser.child(
                workflowProcessElement, ACTIVITIES));
        loadSynchronizers(wp, wp.getSynchronizers(), Util4Parser.child(
                workflowProcessElement, SYNCHRONIZERS));
        loadEndNodes(wp, wp.getEndNodes(), Util4Parser.child(
                workflowProcessElement, END_NODES));
        loadTransitions(wp, Util4Parser.child(workflowProcessElement,
                TRANSITIONS));

        loadLoops(wp,Util4Parser.child(workflowProcessElement, LOOPS));

        loadEventListeners(wp.getEventListeners(), Util4Parser.child(workflowProcessElement, EVENT_LISTENERS));

        Map<String, String> extAttrs = wp.getExtendedAttributes();
        loadExtendedAttributes(extAttrs, Util4Parser.child(
                workflowProcessElement, EXTENDED_ATTRIBUTES));

        return wp;

    }

    /*
    public WorkflowProcess parse(InputSource in) throws IOException, FPDLParserException {
    String oldSigletonClassName = System.getProperty("org.dom4j.DocumentFactory.singleton.strategy");
    try {
    //            java.util.logging.LogManager.getLogManager().getLogger(Dom4JFPDLSerializer.class.getName()).log(Level.WARNING, "=======================检查是否是新版本");

    System.setProperty("org.dom4j.DocumentFactory.singleton.strategy", "org.fireflow.model.io.MySigleton4DocumentFactory");
    SAXReader reader = new SAXReader(new DocumentFactory());
    reader.setEntityResolver(new EntityResolver() {

    String emptyDtd = "";
    ByteArrayInputStream bytels = new ByteArrayInputStream(emptyDtd.getBytes());

    public InputSource resolveEntity(String publicId,
    String systemId) throws SAXException, IOException {
    return new InputSource(bytels);
    }
    });
    reader.setEncoding(encoding);
    Document document = reader.read(in);

    WorkflowProcess wp = parse(document);
    return wp;
    } catch (DocumentException e) {
    e.printStackTrace();
    throw new FPDLParserException("Error parsing document.", e);
    } finally {
    if (oldSigletonClassName != null && !oldSigletonClassName.equals("")) {
    System.setProperty("org.dom4j.DocumentFactory.singleton.strategy", oldSigletonClassName);
    }
    }
    }
     */

    /*
    public WorkflowProcess parse(Reader in) throws IOException,
    FPDLParserException {
    String oldSigletonClassName = System.getProperty("org.dom4j.DocumentFactory.singleton.strategy");
    try {
    //            java.util.logging.LogManager.getLogManager().getLogger(Dom4JFPDLSerializer.class.getName()).log(Level.WARNING, "=======================检查是否是新版本");

    System.setProperty("org.dom4j.DocumentFactory.singleton.strategy", "org.fireflow.model.io.MySigleton4DocumentFactory");
    SAXReader reader = new SAXReader(new DocumentFactory());
    reader.setEntityResolver(new EntityResolver() {

    String emptyDtd = "";
    ByteArrayInputStream bytels = new ByteArrayInputStream(emptyDtd.getBytes());

    public InputSource resolveEntity(String publicId,
    String systemId) throws SAXException, IOException {
    return new InputSource(bytels);
    }
    });
    reader.setEncoding(encoding);
    Document document = reader.read(in);

    WorkflowProcess wp = parse(document);
    return wp;
    } catch (DocumentException e) {
    e.printStackTrace();
    throw new FPDLParserException("Error parsing document.", e);
    } finally {
    if (oldSigletonClassName != null && !oldSigletonClassName.equals("")) {
    System.setProperty("org.dom4j.DocumentFactory.singleton.strategy", oldSigletonClassName);
    }

    }
    }
     */
    protected void loadEventListeners(List listeners, Element element) {
        listeners.clear();
        if (element == null) {
            return;
        }
        if (element == null) {
            return;
        }
        List listenerElms = Util4Parser.children(element, EVENT_LISTENER);
        Iterator iter = listenerElms.iterator();
        while (iter.hasNext()) {
            Element elm = (Element) iter.next();
            EventListener listener = new EventListener();
            listener.setClassName(elm.attributeValue(CLASS_NAME));

            listeners.add(listener);
        }
    }

    protected void loadStartNode(WorkflowProcess wp, Element element)
            throws FPDLParserException {
        if (element == null) {
            return;
        }
        StartNode startNode = new StartNode(wp);
        startNode.setSn(UUID.randomUUID().toString());
        startNode.setDescription(Util4Parser.elementAsString(element,
                DESCRIPTION));
        startNode.setDisplayName(element.attributeValue(DISPLAY_NAME));
        loadExtendedAttributes(startNode.getExtendedAttributes(), Util4Parser.child(element, EXTENDED_ATTRIBUTES));
        wp.setStartNode(startNode);
    }

    protected void loadEndNodes(WorkflowProcess wp, List<EndNode> endNodes,
            Element element) throws FPDLParserException {
        endNodes.clear();
        if (element == null) {
            return;
        }
        List endNodesElms = Util4Parser.children(element, END_NODE);
        Iterator iter = endNodesElms.iterator();
        while (iter.hasNext()) {
            Element elm = (Element) iter.next();
            EndNode endNode = new EndNode(wp, elm.attributeValue(NAME));
            endNode.setSn(UUID.randomUUID().toString());
            endNode.setDescription(Util4Parser.elementAsString(element,
                    DESCRIPTION));
            endNode.setDisplayName(element.attributeValue(DISPLAY_NAME));
            loadExtendedAttributes(endNode.getExtendedAttributes(), Util4Parser.child(elm, EXTENDED_ATTRIBUTES));
            endNodes.add(endNode);
        }
    }

    protected void loadSynchronizers(WorkflowProcess wp, List<Synchronizer> synchronizers,
            Element element) throws FPDLParserException {
        synchronizers.clear();
        if (element == null) {
            return;
        }
        List synchronizerElms = Util4Parser.children(element, SYNCHRONIZER);
        Iterator iter = synchronizerElms.iterator();
        while (iter.hasNext()) {
            Element elm = (Element) iter.next();
            Synchronizer synchronizer = new Synchronizer(wp, elm.attributeValue(NAME));
            synchronizer.setSn(UUID.randomUUID().toString());
            synchronizer.setDescription(Util4Parser.elementAsString(element,
                    DESCRIPTION));
            synchronizer.setDisplayName(element.attributeValue(DISPLAY_NAME));

            loadExtendedAttributes(synchronizer.getExtendedAttributes(),
                    Util4Parser.child(elm, EXTENDED_ATTRIBUTES));

            synchronizers.add(synchronizer);
        }
    }

    protected void loadActivities(WorkflowProcess wp, List<Activity> activities,
            Element element) throws FPDLParserException {

        if (element == null) {
            // log.debug("Activites element was null");
            return;
        }

        List activitElements = Util4Parser.children(element, ACTIVITY);
        activities.clear();
        Iterator iter = activitElements.iterator();
        while (iter.hasNext()) {
            Element activityElement = (Element) iter.next();

            Activity activity = new Activity(wp, activityElement.attributeValue(NAME));
            activity.setSn(UUID.randomUUID().toString());
            activity.setDisplayName(activityElement.attributeValue(DISPLAY_NAME));
            activity.setDescription(Util4Parser.elementAsString(
                    activityElement, DESCRIPTION));
            activity.setCompletionStrategy(activityElement.attributeValue(COMPLETION_STRATEGY));
            loadEventListeners(activity.getEventListeners(), Util4Parser.child(activityElement, EVENT_LISTENERS));
            loadExtendedAttributes(activity.getExtendedAttributes(),
                    Util4Parser.child(activityElement, EXTENDED_ATTRIBUTES));

            loadTasks(activity, activity.getInlineTasks(), Util4Parser.child(
                    activityElement, TASKS));
            loadTaskRefs((WorkflowProcess)activity.getParent(),activity,activity.getTaskRefs(),Util4Parser.child(activityElement, TASKREFS));
            activities.add(activity);
        }
    }

    protected void loadTaskRefs(WorkflowProcess workflowProcess,IWFElement parent,List<TaskRef> taskRefs,Element taskRefsElement){
        taskRefs.clear();
        if (taskRefsElement==null){
            return;
        }
        List taskRefElems = Util4Parser.children(taskRefsElement, TASKREF);
        Iterator iter = taskRefElems.iterator();
        while(iter.hasNext()){
            Element elm = (Element)iter.next();
            String taskId = elm.attributeValue(REFERENCE);
            Task task = (Task)workflowProcess.findWFElementById(taskId);
            if (task!=null){
                TaskRef taskRef = new TaskRef(parent,task);
                taskRef.setSn(UUID.randomUUID().toString());
                taskRefs.add(taskRef);
            }
        }
    }

    protected void loadTasks(IWFElement parent, List<Task> tasks, Element tasksElement)
            throws FPDLParserException {
        tasks.clear();
        if (tasksElement == null) {
            return;
        }
        List tasksElms = Util4Parser.children(tasksElement, TASK);
        Iterator iter = tasksElms.iterator();
        while (iter.hasNext()) {
            Element elm = (Element) iter.next();
            Task task = createTask(parent, elm);
            if (task!=null)tasks.add(task);
        }
    }

    protected Task createTask(IWFElement parent, Element taskElement)
            throws FPDLParserException {
        Task task = null;
        String type = taskElement.attributeValue(TYPE);
        if (Task.FORM.equals(type)) {
            task = new FormTask(parent, taskElement.attributeValue(NAME));
            ((FormTask) task).setAssignmentStrategy(taskElement.attributeValue(COMPLETION_STRATEGY));
            ((FormTask) task).setDefaultView(taskElement.attributeValue(DEFAULT_VIEW));

            ((FormTask) task).setPerformer(createPerformer(Util4Parser.child(taskElement,
                    PERFORMER)));
            ((FormTask) task).setEditForm(createForm(Util4Parser.child(taskElement, EDIT_FORM)));
            ((FormTask) task).setViewForm(createForm(Util4Parser.child(taskElement, VIEW_FORM)));
            ((FormTask) task).setListForm(createForm(Util4Parser.child(taskElement, LIST_FORM)));

        } else if (Task.TOOL.equals(type)) {
            task = new ToolTask(parent, taskElement.attributeValue(NAME));

            ((ToolTask) task).setApplication(createApplication(Util4Parser.child(taskElement,
                    APPLICATION)));
        } else if (Task.SUBFLOW.equals(type)) {
            task = new SubflowTask(parent, taskElement.attributeValue(NAME));
            ((SubflowTask) task).setSubWorkflowProcess(createSubWorkflowProcess(Util4Parser.child(taskElement, SUB_WORKFLOW_PROCESS)));

        } else {
            return null;
        }

        task.setSn(UUID.randomUUID().toString());
        task.setDisplayName(taskElement.attributeValue(DISPLAY_NAME));
        task.setDescription(Util4Parser.elementAsString(taskElement, DESCRIPTION));
        String sPriority = taskElement.attributeValue(PRIORITY);
        int priority = 0;
        if (sPriority != null) {
            try {
                priority = Integer.parseInt(sPriority);
            } catch (Exception e) {
            }
        }
        task.setPriority(priority);
        task.setDuration(createDuration(Util4Parser.child(taskElement, DURATION)));

        task.setTaskInstanceCreator(taskElement.attributeValue(TASK_INSTANCE_CREATOR));
        task.setTaskInstanceRunner(taskElement.attributeValue( TASK_INSTANCE_RUNNER));
        task.setTaskInstanceCompletionEvaluator(taskElement.attributeValue(TASK_INSTANCE_COMPLETION_EVALUATOR));
        task.setLoopStrategy(taskElement.attributeValue(LOOP_STRATEGY));

        loadEventListeners(task.getEventListeners(), Util4Parser.child(taskElement, EVENT_LISTENERS));
        loadExtendedAttributes(task.getExtendedAttributes(),
                Util4Parser.child(taskElement, EXTENDED_ATTRIBUTES));
        return task;

    }

    protected Participant createPerformer(Element performerElement) {
        if (performerElement == null) {
            return null;
        }
        Participant part = new Participant(performerElement.attributeValue(NAME));
        part.setDisplayName(performerElement.attributeValue(DISPLAY_NAME));
        part.setDescription(Util4Parser.elementAsString(performerElement,
                DESCRIPTION));
        part.setAssignmentHandler(Util4Parser.elementAsString(performerElement,
                this.ASSIGNMENT_HANDLER));
        return part;
    }

    protected SubWorkflowProcess createSubWorkflowProcess(Element subFlowElement) {
        if (subFlowElement == null) {
            return null;
        }

        SubWorkflowProcess subFlow = new SubWorkflowProcess(subFlowElement.attributeValue(NAME));
        subFlow.setDisplayName(subFlowElement.attributeValue(DISPLAY_NAME));
        subFlow.setDescription(Util4Parser.elementAsString(subFlowElement,
                DESCRIPTION));
        subFlow.setWorkflowProcessId(Util4Parser.elementAsString(subFlowElement,
                this.WORKFLOW_PROCESS_ID));

        return subFlow;
    }

    protected Application createApplication(Element applicationElement) {
        if (applicationElement == null) {
            return null;
        }
        Application app = new Application(applicationElement.attributeValue(APPLICATION));
        app.setDisplayName(applicationElement.attributeValue(DISPLAY_NAME));
        app.setDescription(Util4Parser.elementAsString(applicationElement,
                DESCRIPTION));
        app.setHandler(Util4Parser.elementAsString(applicationElement,
                HANDLER));
        return app;
    }

    protected Form createForm(Element formElement) {
        if (formElement == null) {
            return null;
        }
        Form form = new Form(formElement.attributeValue(NAME));
        form.setDisplayName(formElement.attributeValue(DISPLAY_NAME));
        form.setDescription(Util4Parser.elementAsString(formElement,
                DESCRIPTION));
        form.setUri(Util4Parser.elementAsString(formElement, URI));
        return form;
    }

    protected Duration createDuration(Element durationElement) {
        if (durationElement == null) {
            return null;
        }
        String sValue = durationElement.attributeValue(VALUE);
        String sIsBusTime = durationElement.attributeValue(this.IS_BUSINESS_TIME);
        boolean isBusinessTime = true;
        int value = 1;
        if (sValue != null) {
            try {
                value = Integer.parseInt(sValue);
                isBusinessTime = Boolean.parseBoolean(sIsBusTime);
            } catch (Exception ex) {
                return null;
            }
        }
        Duration duration = new Duration(value, durationElement.attributeValue(UNIT));
        duration.setBusinessTime(isBusinessTime);
        return duration;
    }

    protected Loop createLoop(WorkflowProcess wp, Element loopElement)
            throws FPDLParserException {
        String fromNodeId = loopElement.attributeValue(FROM);
        String toNodeId = loopElement.attributeValue(TO);
        Synchronizer fromNode = (Synchronizer) wp.findWFElementById(fromNodeId);
        Synchronizer toNode = (Synchronizer) wp.findWFElementById(toNodeId);

        Loop loop = new Loop(wp,
                loopElement.attributeValue(NAME), fromNode,
                toNode);
        loop.setSn(UUID.randomUUID().toString());

        loop.setDisplayName(loopElement.attributeValue(DISPLAY_NAME));
        loop.setDescription(Util4Parser.elementAsString(loopElement,
                DESCRIPTION));
        Element conditionElement = Util4Parser.child(loopElement, CONDITION);
        loop.setCondition(conditionElement == null ? ""
                : conditionElement.getStringValue());

        // load extended attributes
        Map<String, String> extAttrs = loop.getExtendedAttributes();
        loadExtendedAttributes(extAttrs, Util4Parser.child(loopElement,
                EXTENDED_ATTRIBUTES));

        return loop;
    }

     protected void loadLoops(WorkflowProcess wp ,Element loopsElement)throws FPDLParserException{
         if (loopsElement==null)return;
         List loopElementList = Util4Parser.children(loopsElement, LOOP);

        List<Loop> loops = wp.getLoops();



        loops.clear();

        Iterator iter = loopElementList.iterator();
        while (iter.hasNext()) {
            Element loopElement = (Element) iter.next();
            Loop loop = createLoop(wp, loopElement);
            loops.add(loop);



            Synchronizer fromNode = (Synchronizer)loop.getFromNode();
            Synchronizer toNode = (Synchronizer)loop.getToNode();

            fromNode.getLeavingLoops().add(loop);
            toNode.getEnteringLoops().add(loop);
        }
     }


    protected void loadTransitions(WorkflowProcess wp, Element transitionsElement)
            throws FPDLParserException {

        if (transitionsElement == null) {
            return;
        }

        loadTransitions(wp, Util4Parser.children(transitionsElement, TRANSITION));
    }

    protected void loadTransitions(WorkflowProcess wp, List<Element> elements)
            throws FPDLParserException {
        List<Transition> transitions = wp.getTransitions();

        transitions.clear();

        Iterator iter = elements.iterator();
        while (iter.hasNext()) {
            Element transitionElement = (Element) iter.next();
            Transition transition = createTransition(wp, transitionElement);
            transitions.add(transition);
            Node fromNode = transition.getFromNode();
            Node toNode = transition.getToNode();
            if (fromNode != null && (fromNode instanceof Activity)) {
                ((Activity) fromNode).setLeavingTransition(transition);
            } else if (fromNode != null && (fromNode instanceof Synchronizer)) {
                ((Synchronizer) fromNode).getLeavingTransitions().add(
                        transition);
            }
            if (toNode != null && (toNode instanceof Activity)) {
                ((Activity) toNode).setEnteringTransition(transition);
            } else if (toNode != null && (toNode instanceof Synchronizer)) {
                ((Synchronizer) toNode).getEnteringTransitions().add(transition);
            }
        }
    }

    protected Transition createTransition(WorkflowProcess wp, Element element)
            throws FPDLParserException {
        String fromNodeId = element.attributeValue(FROM);
        String toNodeId = element.attributeValue(TO);
        Node fromNode = (Node) wp.findWFElementById(fromNodeId);
        Node toNode = (Node) wp.findWFElementById(toNodeId);

        Transition transition = new Transition(wp,
                element.attributeValue(NAME), fromNode,
                toNode);
        transition.setSn(UUID.randomUUID().toString());

        transition.setDisplayName(element.attributeValue(DISPLAY_NAME));
        transition.setDescription(Util4Parser.elementAsString(element,
                DESCRIPTION));
        Element conditionElement = Util4Parser.child(element, CONDITION);
        transition.setCondition(conditionElement == null ? ""
                : conditionElement.getStringValue());

        // load extended attributes
        Map<String, String> extAttrs = transition.getExtendedAttributes();
        loadExtendedAttributes(extAttrs, Util4Parser.child(element,
                EXTENDED_ATTRIBUTES));

        return transition;
    }

    protected void loadDataFields(WorkflowProcess wp, List<DataField> dataFields,
            Element element) throws FPDLParserException {

        if (element == null) {
            return;
        }

        List datafieldsElement = Util4Parser.children(element, DATA_FIELD);
        dataFields.clear();
        Iterator iter = datafieldsElement.iterator();
        while (iter.hasNext()) {
            Element dataFieldElement = (Element) iter.next();
            dataFields.add(createDataField(wp, dataFieldElement));
        }
    }

    protected DataField createDataField(WorkflowProcess wp, Element element)
            throws FPDLParserException {
        if (element == null) {
            return null;
        }
        String dataType = element.attributeValue(DATA_TYPE);
        if (dataType == null) {
            dataType = DataField.STRING;
        }

        DataField dataField = new DataField(wp, element.attributeValue(NAME),
                dataType);
        dataField.setSn(UUID.randomUUID().toString());

        dataField.setDisplayName(element.attributeValue(DISPLAY_NAME));
        dataField.setInitialValue(element.attributeValue(INITIAL_VALUE));
        dataField.setDescription(Util4Parser.elementAsString(element,
                DESCRIPTION));

        loadExtendedAttributes(dataField.getExtendedAttributes(), Util4Parser.child(element, EXTENDED_ATTRIBUTES));

        return dataField;
    }

    protected void loadExtendedAttributes(Map<String, String> extendedAttributes,
            Element element) throws FPDLParserException {

        if (element == null) {
            return;
        }
        extendedAttributes.clear();
        List extendAttributeElementsList = Util4Parser.children(element,
                EXTENDED_ATTRIBUTE);
        Iterator iter = extendAttributeElementsList.iterator();
        while (iter.hasNext()) {
            Element extAttrElement = (Element) iter.next();
            String name = extAttrElement.attributeValue(NAME);
            String value = extAttrElement.attributeValue(VALUE);

            extendedAttributes.put(name, value);

        }
    }
}
