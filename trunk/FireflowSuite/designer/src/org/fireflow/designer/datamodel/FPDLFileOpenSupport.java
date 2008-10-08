/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.datamodel;


import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.OpenSupport;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author chennieyun
 */
public class FPDLFileOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {



    public FPDLFileOpenSupport(FPDLDataObject.Entry entry) {
        super(entry);
    }


    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
//        FPDLDataObject dataObj = (FPDLDataObject) entry.getDataObject();
//        WorkflowProcessEditorPane editorPane = new WorkflowProcessEditorPane(dataObj);
//        editorPane.setDisplayName(dataObj.getName());
//
//        return editorPane;
        return null;
    }
}
