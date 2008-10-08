package org.fireflow.model.io;

import org.dom4j.Namespace;
import org.dom4j.QName;

public interface FPDLNames {
    /** Namespace prefix to use for XSD elements. */
    String XSD_NS_PREFIX = "xsd";
    
    /** The XSD namespace URI. */
    String XSD_URI = "http://www.w3.org/2001/XMLSchema";
    
    /** Namespace prefix to use for XSD elements. */
    String XSI_NS_PREFIX = "xsi";
    /** The XSD namespace URI. */
    String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    
    String XSI_SCHEMA_LOCATION = "schemaLocation";
    
    /** Namespace prefix to use for FPDL elements. */
    String FPDL_NS_PREFIX = "fpdl";
    
    /** The XPDL namespace URI. */
    String FPDL_URI = "http://www.fireflow.org/Fireflow_Process_Definition_Language";
    
    String PUBLIC_ID = "-//Nieyun Chen//ProcessDefinition//CN";
    
    String SYSTEM_ID = "FireFlow_Process_Definition_Language.dtd";
    
    /** The FPDL schema URI. */
    String FPDL_SCHEMA_LOCATION =
        "http://www.fireflow.org/2007/fpdl1.0 fpdl-1.0.xsd";
    
    /** Unique identifier. */
    String ID = "Id";

    /** Entity name. */
    String NAME = "Name";

    /** Tag which defines a brief description of an element. */
    String DESCRIPTION = "Description";
    
    String DISPLAY_NAME="DisplayName";

    String EXTENDED_ATTRIBUTES = "ExtendedAttributes";
    String EXTENDED_ATTRIBUTE = "ExtendedAttribute";
    String VALUE = "Value";

    String VERSION = "Version";

//    String PARTICIPANTS = "Participants";
//    String PARTICIPANT = "Participant";
//    String PARTICIPANT_TYPE = "ParticipantType";
    String ASSIGNMENT_HANDLER = "AssignmentHandler";


//    String EVENT_TYPES = "EventTypes";
//    String EVENT_TYPE = "EventType";
//    String EVENT = "Event";

//    String APPLICATIONS = "Applications";
    String APPLICATION = "Application";
    String HANDLER = "Handler";

//    String WORKFLOW_PROCESSES = "WorkflowProcesses";
    String WORKFLOW_PROCESS = "WorkflowProcess";


    String PRIORITY = "Priority";

    String DURATION = "Duration";

    String START_NODE = "StartNode";
    String END_NODE = "EndNode";
    String END_NODES = "EndNodes";

    String ACTIVITIES = "Activities";
    String ACTIVITY = "Activity";
    

    String SYNCHRONIZERS = "Synchronizers";
    String SYNCHRONIZER = "Synchronizer";

    String TASKS = "Tasks";
    String TASK = "Task";
    String REFERENCE = "Reference";
    
    String SUBFLOW = "SubFlow";
    
    String DATA_FIELDS = "DataFields";
    String DATA_FIELD = "DataField";
    String INITIAL_VALUE = "InitialValue";
    String LENGTH = "Length";

    String PERFORMER = "Performer";
    String START_MODE = "StartMode";
    String FINISH_MODE = "FinishMode";
    String MANUAL = "Manual";
    String AUTOMATIC = "Automatic";

    String TRANSITIONS = "Transitions";
    String TRANSITION = "Transition";
    String FROM = "From";
    String TO = "To";


    String CONDITION = "Condition";

    String TYPE = "Type";
    String DATA_TYPE = "DataType";


    String NAMESPACE = "namespace";

    String EXCEPTION_NAME = "ExceptionName";

    String RESOURCE_FILE = "ResourceFile";
    String RESOURCE_MANAGER = "ResourceManager";
    
    String COMPLETION_STRATEGY = "CompletionStrategy";
    String DEFAULT_VIEW = "DefaultView";
    String EXECUTION = "Execution";
    String EDIT_FORM = "EditForm";
    String VIEW_FORM = "ViewForm";
    String LIST_FORM = "ListForm";
    String URI = "Uri";
    String UNIT = "Unit";
    String IS_BUSINESS_TIME = "IsBusinessTime";
    
    String SUB_WORKFLOW_PROCESS = "SubWorkflowProcess";
    String WORKFLOW_PROCESS_ID = "WorkflowProcessId";

    Namespace XSD_NS = new Namespace(XSD_NS_PREFIX, XSD_URI);
    Namespace XSI_NS = new Namespace(XSI_NS_PREFIX, XSI_URI);
    Namespace FPDL_NS = new Namespace(FPDL_NS_PREFIX, FPDL_URI);

    
}
