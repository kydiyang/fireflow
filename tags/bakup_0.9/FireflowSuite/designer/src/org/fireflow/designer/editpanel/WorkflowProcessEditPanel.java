/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.editpanel;

import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.designer.editpanel.graph.MyViewFactory;
import org.fireflow.designer.editpanel.graph.ProcessDesignMediator;
import org.fireflow.designer.editpanel.graph.ProcessDesignPanel;
import org.fireflow.designer.editpanel.graph.ProcessDesignToolBar;
import org.fireflow.designer.editpanel.graph.ProcessGraphModel;
import org.jgraph.graph.GraphLayoutCache;
import org.netbeans.modules.xml.multiview.AbstractMultiViewElement;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.Lookup;

/**
 *
 * @author chennieyun
 */
public class WorkflowProcessEditPanel extends AbstractMultiViewElement {

    Lookup lookup = null;
    JScrollPane designScrollPanel = new JScrollPane();
    ProcessDesignToolBar processToolbar = null;

    public WorkflowProcessEditPanel(FPDLDataObject argDataObject) {
        super(argDataObject);

        myInitComponents();

        lookup = ExplorerUtils.createLookup(((FPDLDataObject) dObj).getExplorerManager(), new ActionMap());
    }

    private void myInitComponents() {
        ProcessDesignMediator md = new ProcessDesignMediator();
        ProcessGraphModel graphModel = ((FPDLDataObject)this.dObj).getProcessGraphModel();
        GraphLayoutCache layoutCache = new GraphLayoutCache(graphModel,new MyViewFactory(),false );
        ProcessDesignPanel graph = new ProcessDesignPanel(graphModel,layoutCache);
        graphModel.setDesignPanel(graph);

        processToolbar = new ProcessDesignToolBar(md);
        md.registProcessDesignPanel(graph);
        md.registProcessDesignToolBar(processToolbar);
        md.mountActions();

        this.designScrollPanel.getViewport().add(graph);
    }

    public JComponent getVisualRepresentation() {
        return designScrollPanel;
    }

    public JComponent getToolbarRepresentation() {
        return processToolbar;
    }

    public Lookup getLookup() {
        return lookup;
    }

    public void componentShowing() {
        if (this.dObj.isModified()) {
            this.callback.updateTitle(dObj.getPrimaryFile().getNameExt() + "*");
        } else {
            this.callback.updateTitle(dObj.getPrimaryFile().getNameExt());
        }
    }

    public void componentHidden() {

    }

    public void componentActivated() {

    }

    public void componentDeactivated() {

    }
}
