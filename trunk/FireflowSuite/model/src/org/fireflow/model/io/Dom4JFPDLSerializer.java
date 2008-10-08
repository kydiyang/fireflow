package org.fireflow.model.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.fireflow.model.DataField;
import org.fireflow.model.Duration;
import org.fireflow.model.Task;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;
import org.fireflow.model.reference.Application;
import org.fireflow.model.reference.Form;
import org.fireflow.model.reference.Participant;
import org.fireflow.model.reference.SubWorkflowProcess;

public class Dom4JFPDLSerializer implements IFPDLSerializer {

    public static final String DEFAULT_XPDL_VERSION = "1.0";
    public static final String DEFAULT_VENDOR = "\u975E\u4E5F";

    // private static final Log log =
    // LogFactory.getLog(Dom4JFPDLSerializer.class);
    public void serialize(WorkflowProcess workflowProcess, OutputStream out)
            throws IOException, FPDLSerializerException {

        serialize(workflowProcess, new OutputStreamWriter(out));
    }

    public void serialize(WorkflowProcess workflowProcess, Writer out)
            throws IOException, FPDLSerializerException {
        // create the Document object
        Document document = workflowProcessToDom(workflowProcess);

        // write the document to the output stream
        OutputFormat format = new OutputFormat("    ", true);
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(out, format);
        writer.write(document);
        out.flush();
    }

    public Document workflowProcessToDom(WorkflowProcess workflowProcess)
            throws FPDLSerializerException {
        DocumentFactory df = new DocumentFactory();

        // serialize the Package
        Element workflowProcessElement = df.createElement(new QName(
                WORKFLOW_PROCESS, FPDL_NS));
        workflowProcessElement.addNamespace(FPDL_NS_PREFIX, FPDL_URI);
        workflowProcessElement.addNamespace(XSD_NS_PREFIX, XSD_URI);
        workflowProcessElement.addNamespace(XSI_NS_PREFIX, XSI_URI);
        workflowProcessElement.addAttribute(ID, workflowProcess.getId());
        workflowProcessElement.addAttribute(NAME, workflowProcess.getName());
        workflowProcessElement.addAttribute(DISPLAY_NAME, workflowProcess.getDisplayName());
        workflowProcessElement.addAttribute(RESOURCE_FILE, workflowProcess.getResourceFile());
        workflowProcessElement.addAttribute(RESOURCE_MANAGER, workflowProcess.getResourceManager());

        Util4Serializer.addElement(workflowProcessElement, DESCRIPTION,
                workflowProcess.getDescription());

        writeDataFields(workflowProcess.getDataFields(), workflowProcessElement);
        writeStartNode(workflowProcess.getStartNode(), workflowProcessElement);
        writeActivities(workflowProcess.getActivities(), workflowProcessElement);
        writeSynchronizers(workflowProcess.getSynchronizers(),
                workflowProcessElement);
        writeEndNodes(workflowProcess.getEndNodes(), workflowProcessElement);
        writeTransitions(workflowProcess.getTransitions(),
                workflowProcessElement);

        writeExtendedAttributes(workflowProcess.getExtendedAttributes(),
                workflowProcessElement);

        Document document = df.createDocument(workflowProcessElement);
        document.addDocType(WORKFLOW_PROCESS, this.PUBLIC_ID, this.SYSTEM_ID);
        return document;

    }

    public String workflowProcessToXMLString(WorkflowProcess workflowProcess)
            throws IOException, FPDLSerializerException {
        Document dom = workflowProcessToDom(workflowProcess);
        OutputFormat format = new OutputFormat("    ", true);
        format.setEncoding("utf-8");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        XMLWriter writer = new XMLWriter(out, format);

        writer.write(dom);
        return out.toString();

    }

    protected void writeDataFields(List dataFields, Element parent)
            throws FPDLSerializerException {

        if (dataFields == null || dataFields.size() == 0) {
            return;
        }

        Element dataFieldsElement = Util4Serializer.addElement(parent,
                DATA_FIELDS);
        Iterator iter = dataFields.iterator();
        while (iter.hasNext()) {
            DataField dataField = (DataField) iter.next();
            Element dataFieldElement = Util4Serializer.addElement(
                    dataFieldsElement, DATA_FIELD);

            dataFieldElement.addAttribute(ID, dataField.getId());
            dataFieldElement.addAttribute(NAME, dataField.getName());
            dataFieldElement.addAttribute(DISPLAY_NAME, dataField.getDisplayName());
            dataFieldElement.addAttribute(DATA_TYPE, dataField.getDataType());

            dataFieldElement.addAttribute(INITIAL_VALUE,
                    dataField.getInitialValue());

            Util4Serializer.addElement(dataFieldElement, DESCRIPTION, dataField.getDescription());

            writeExtendedAttributes(dataField.getExtendedAttributes(),
                    dataFieldElement);
        }
    }

    protected void writeEndNodes(List endNodes, Element parent) {
        Element endNodesElement = Util4Serializer.addElement(parent, END_NODES);
        Iterator iter = endNodes.iterator();

        while (iter.hasNext()) {
            writeEndNode((EndNode) iter.next(), endNodesElement);
        }
    }

    protected void writeEndNode(EndNode endNode, Element parent) {
        Element endNodeElement = Util4Serializer.addElement(parent, END_NODE);
        endNodeElement.addAttribute(ID, endNode.getId());
        endNodeElement.addAttribute(NAME, endNode.getName());
        endNodeElement.addAttribute(DISPLAY_NAME, endNode.getDisplayName());

        Util4Serializer.addElement(endNodeElement, DESCRIPTION, endNode.getDescription());

        writeExtendedAttributes(endNode.getExtendedAttributes(), endNodeElement);

    }

    protected void writeStartNode(StartNode startNode, Element parent)
            throws FPDLSerializerException {
        if (startNode == null) {
            return;
        }
        Element startElement = Util4Serializer.addElement(parent, START_NODE);
        startElement.addAttribute(ID, startNode.getId());
        startElement.addAttribute(NAME, startNode.getName());

        startElement.addAttribute(DISPLAY_NAME, startNode.getDisplayName());

        Util4Serializer.addElement(startElement, DESCRIPTION, startNode.getDescription());

        writeExtendedAttributes(startNode.getExtendedAttributes(), startElement);
    }

    protected void writeSynchronizers(List synchronizers, Element parent)
            throws FPDLSerializerException {
        if (synchronizers == null || synchronizers.size() == 0) {
            return;
        }
        Element synchronizersElement = Util4Serializer.addElement(parent,
                SYNCHRONIZERS);

        Iterator iter = synchronizers.iterator();

        while (iter.hasNext()) {
            writeSynchronizer((Synchronizer) iter.next(), synchronizersElement);
        }
    }

    protected void writeSynchronizer(Synchronizer synchronizer, Element parent)
            throws FPDLSerializerException {
        Element synchronizerElement = Util4Serializer.addElement(parent,
                SYNCHRONIZER);
        synchronizerElement.addAttribute(ID, synchronizer.getId());
        synchronizerElement.addAttribute(NAME, synchronizer.getName());
        synchronizerElement.addAttribute(DISPLAY_NAME, synchronizer.getDisplayName());

        Util4Serializer.addElement(synchronizerElement, DESCRIPTION,
                synchronizer.getDescription());
        writeExtendedAttributes(synchronizer.getExtendedAttributes(),
                synchronizerElement);
    }

    protected void writeActivities(List activities, Element parent)
            throws FPDLSerializerException {

        if (activities == null || activities.size() == 0) {
            return;
        }

        Element activitiesElement = Util4Serializer.addElement(parent,
                ACTIVITIES);

        Iterator iter = activities.iterator();
        while (iter.hasNext()) {
            writeActivity((Activity) iter.next(), activitiesElement);
        }
    }

    protected void writeActivity(Activity activity, Element parent)
            throws FPDLSerializerException {

        Element activityElement = Util4Serializer.addElement(parent, ACTIVITY);

        activityElement.addAttribute(ID, activity.getId());
        activityElement.addAttribute(NAME, activity.getName());
        activityElement.addAttribute(DISPLAY_NAME, activity.getDisplayName());
        activityElement.addAttribute(COMPLETION_STRATEGY, activity.getCompletionStrategy());

        Util4Serializer.addElement(activityElement, DESCRIPTION, activity.getDescription());

        writeExtendedAttributes(activity.getExtendedAttributes(),
                activityElement);

        writeTasks(activity.getTasks(), activityElement);
    }

    protected void writeTasks(List tasks, Element parent)
            throws FPDLSerializerException {
        Element tasksElement = Util4Serializer.addElement(parent, TASKS);
        Iterator iter = tasks.iterator();

        while (iter.hasNext()) {
            writeTask((Task) iter.next(), tasksElement);
        }
    }

    protected void writeTask(Task task, Element parent)
            throws FPDLSerializerException {
        Element taskElement = Util4Serializer.addElement(parent, TASK);

        taskElement.addAttribute(ID, task.getId());
        taskElement.addAttribute(NAME, task.getName());
        taskElement.addAttribute(DISPLAY_NAME, task.getDisplayName());
        taskElement.addAttribute(TYPE, task.getType());
        taskElement.addAttribute(START_MODE, task.getStartMode());
        taskElement.addAttribute(COMPLETION_STRATEGY, task.getAssignmentStrategy());
        taskElement.addAttribute(DEFAULT_VIEW, task.getDefaultView());
        taskElement.addAttribute(PRIORITY, Integer.toString(task.getPriority()));
        taskElement.addAttribute(EXECUTION, task.getExecution());

        Util4Serializer.addElement(taskElement, DESCRIPTION, task.getDescription());

        writeForm(EDIT_FORM, task.getEditForm(), taskElement);
        writeForm(VIEW_FORM, task.getViewForm(), taskElement);
        writeForm(LIST_FORM, task.getListForm(), taskElement);
        this.writeApplication(task.getApplication(), taskElement);
        this.writeSubWorkflowProcess(task.getSubWorkflowProcess(), taskElement);
        this.writePerformer(task.getPerformer(), taskElement);

        writeDuration(task.getDuration(), taskElement);

        writeExtendedAttributes(task.getExtendedAttributes(), taskElement);
    }

    protected void writeDuration(Duration duration, Element parent) {
        if (duration == null) {
            return;
        }
        Element durationElement = Util4Serializer.addElement(parent, DURATION);
        durationElement.addAttribute(VALUE, Integer.toString(duration.getValue()));
        durationElement.addAttribute(UNIT, duration.getUnit());
        durationElement.addAttribute(IS_BUSINESS_TIME, Boolean.toString(duration.isBusinessTime()));
    }

    protected void writeForm(String formName, Form form, Element parent) {
        if (form == null) {
            return;
        }
        Element editFormElement = Util4Serializer.addElement(parent, formName);
        editFormElement.addAttribute(NAME, form.getName());
        editFormElement.addAttribute(DISPLAY_NAME, form.getDisplayName());

        Util4Serializer.addElement(editFormElement, DESCRIPTION, form.getDescription());
        Util4Serializer.addElement(editFormElement, URI, form.getUri());
    }

    protected void writeTransitions(List transitions, Element parent)
            throws FPDLSerializerException {

        if (transitions == null || transitions.size() == 0) {
            return;
        }

        Element transitionsElement = Util4Serializer.addElement(parent,
                TRANSITIONS);

        Iterator iter = transitions.iterator();
        while (iter.hasNext()) {
            writeTransition((Transition) iter.next(), transitionsElement);
        }
    }

    protected void writeTransition(Transition transition, Element parent)
            throws FPDLSerializerException {

        Element transitionElement = Util4Serializer.addElement(parent,
                TRANSITION);

        transitionElement.addAttribute(ID, transition.getId());
        transitionElement.addAttribute(FROM, transition.getFromNode().getId());
        transitionElement.addAttribute(TO, transition.getToNode().getId());

        transitionElement.addAttribute(NAME, transition.getName());
        transitionElement.addAttribute(DISPLAY_NAME, transition.getDisplayName());

        Util4Serializer.addElement(transitionElement, CONDITION, transition.getCondition());

        writeExtendedAttributes(transition.getExtendedAttributes(),
                transitionElement);
    }

    protected Element writeExtendedAttributes(Map extendedAttributes,
            Element parent) {

        if (extendedAttributes == null || extendedAttributes.size() == 0) {
            return null;
        }

        Element extendedAttributesElement =
                Util4Serializer.addElement(parent,
                EXTENDED_ATTRIBUTES);
//                        parent
//				.addElement(EXTENDED_ATTRIBUTES);

        Iterator keys = extendedAttributes.keySet().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            Object value = extendedAttributes.get(key);

            Element extendedAttributeElement = Util4Serializer.addElement(
                    extendedAttributesElement, EXTENDED_ATTRIBUTE);
            extendedAttributeElement.addAttribute(NAME, key.toString());
            if (value != null) {
                extendedAttributeElement.addAttribute(VALUE, value.toString());
            }

        }

        return extendedAttributesElement;

    }

    protected void writePerformer(Participant participant, Element parent) {
        if (participant == null) {
            return;
        }

        Element participantElement = Util4Serializer.addElement(parent,
                PERFORMER);

        participantElement.addAttribute(NAME, participant.getName());
        participantElement.addAttribute(DISPLAY_NAME, participant.getDisplayName());

        Util4Serializer.addElement(participantElement, DESCRIPTION, participant.getDescription());

        Util4Serializer.addElement(participantElement, ASSIGNMENT_HANDLER,
                participant.getAssignmentHandler());

    }

    protected void writeSubWorkflowProcess(SubWorkflowProcess subWorkflowProcess, Element parent) {
        if (subWorkflowProcess == null) {
            return;
        }
        Element subflowElement = Util4Serializer.addElement(parent,
                SUB_WORKFLOW_PROCESS);

        subflowElement.addAttribute(NAME, subWorkflowProcess.getName());
        subflowElement.addAttribute(DISPLAY_NAME, subWorkflowProcess.getDisplayName());

        Util4Serializer.addElement(subflowElement, DESCRIPTION, subWorkflowProcess.getDescription());

        Util4Serializer.addElement(subflowElement, WORKFLOW_PROCESS_ID,
                subWorkflowProcess.getWorkflowProcessId());

    }

    protected void writeApplication(Application application, Element parent)
            throws FPDLSerializerException {

        if (application == null) {
            return;
        }

        Element applicationElement = Util4Serializer.addElement(parent,
                APPLICATION);

        applicationElement.addAttribute(NAME, application.getName());
        applicationElement.addAttribute(DISPLAY_NAME, application.getDisplayName());

        Util4Serializer.addElement(applicationElement, DESCRIPTION, application.getDescription());

        Util4Serializer.addElement(applicationElement, HANDLER, application.getHandler());

    }
}
