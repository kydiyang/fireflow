package org.fireflow.designer.properties.editor;

import javax.swing.JTabbedPane;


import org.fireflow.designer.properties.editor.ExtendAttributeEditor;

import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.WorkflowProcess;
import org.openide.nodes.AbstractNode;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class WorkflowProcessEditorPane
        extends JTabbedPane {

    public WorkflowProcessEditorPane(AbstractNode node,WorkflowProcess argProcess, FPDLDataObject fpdlDataObj) {
        init(node,argProcess, fpdlDataObj);
    }

    void init(AbstractNode node,WorkflowProcess argProcess, FPDLDataObject fpdlDataObj) {
        ExtendAttributeEditor xAttrEdt = new ExtendAttributeEditor(argProcess, fpdlDataObj);
        WorkflowProcessEditor workflowProcessEditor = new WorkflowProcessEditor(node,argProcess, fpdlDataObj);

        this.add(workflowProcessEditor, "WorkflowProcess Attributes");
        this.add(xAttrEdt, "Extend Attributes");

    }
}
