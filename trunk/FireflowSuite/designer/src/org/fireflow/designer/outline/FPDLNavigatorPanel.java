/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.outline;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.designer.properties.PropertiesEditPaneTopComponent;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.Lookup;
//import org.fireflow.designer.properties.
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author chennieyun
 */
public class FPDLNavigatorPanel implements NavigatorPanel, LookupListener {

    private JComponent panelUI = null;
    private static final Lookup.Template MY_DATA = new Lookup.Template(FPDLDataObject.class);
    /** current context to work on */
    private Lookup.Result curContext;
    private ExplorerManager explorerManager = null;
    private FPDLDataObject fpdlDataObj = null;

    public String getDisplayName() {
        return "FPDLNavigator";

    }

    public String getDisplayHint() {
        return "FPDL Navigator";
    }

    public JComponent getComponent() {
        panelUI = MyNavigatorComponent.getInstance();
        return panelUI;
    }

    public void resultChanged(LookupEvent arg0) {
        Lookup.Result result = (Lookup.Result) arg0.getSource();
        Collection c = result.allInstances();
        setNewContent(c);
    }

    public void panelActivated(Lookup context) {

        curContext = context.lookup(MY_DATA);
        curContext.addLookupListener(this);
        Collection data = curContext.allInstances();
        setNewContent(data);
    }

    public void panelDeactivated() {
        curContext.removeLookupListener(this);
        curContext = null;
        explorerManager = null;
        fpdlDataObj = null;
        panelUI = null;
    }

    public Lookup getLookup() {

        if (explorerManager != null) {
            return ExplorerUtils.createLookup(explorerManager, new ActionMap());
        } else {
            return null;
        }
    }

    private void setNewContent(Collection newData) {
        Iterator it = newData.iterator();
        System.out.println("==================Inside setNewContent,newData size=" + newData.size());
        if (it.hasNext()) {
            fpdlDataObj = (FPDLDataObject) it.next();
            System.out.println("==================FPDLDataObject=" + fpdlDataObj);
//            explorerManager = new ExplorerManager();
//            explorerManager.setRootContext(fpdlDataObj.getWorkflowProcessElement());
            explorerManager = fpdlDataObj.getExplorerManager();

            MyNavigatorComponent naviComp = (MyNavigatorComponent) this.getComponent();
            naviComp.setExplorerManager(explorerManager);
            naviComp.repaint();

            PropertiesEditPaneTopComponent propertiesEditPane = PropertiesEditPaneTopComponent.findInstance();
            explorerManager.removePropertyChangeListener(propertiesEditPane);
            explorerManager.addPropertyChangeListener(propertiesEditPane);

            propertiesEditPane.open();


        }
    }
}

class MyNavigatorComponent extends JPanel implements ExplorerManager.Provider {

    private static MyNavigatorComponent navigatorComponent = null;
    BeanTreeView treeView = null;
    JScrollPane contentScroll = new JScrollPane();
    ExplorerManager explorerManager = null;
//        JPopupMenu popMenu = null;
    private MyNavigatorComponent() {
        this.setLayout(new BorderLayout());
        this.add(contentScroll, BorderLayout.CENTER);

    }

    public static MyNavigatorComponent getInstance() {
        if (navigatorComponent == null) {
            navigatorComponent = new MyNavigatorComponent();
        }
        return navigatorComponent;
    }

    public void setExplorerManager(ExplorerManager explorerMgr) {
        explorerManager = explorerMgr;

        if (treeView != null) {
            contentScroll.getViewport().remove(treeView);
        }
        treeView = null;

        treeView = new BeanTreeView();
        contentScroll.getViewport().add(treeView, BorderLayout.CENTER);
//        treeView.expandAll();
        treeView.setRootVisible(false);
        this.validate();
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }
}
