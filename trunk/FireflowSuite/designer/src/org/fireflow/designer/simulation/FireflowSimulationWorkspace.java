/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.simulation;

import java.util.HashSet;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.designer.editpanel.graph.MyViewFactory;
import org.fireflow.designer.editpanel.graph.ProcessGraphModel;
import org.fireflow.designer.simulation.engine.definition.DefinitionService4Simulation;
import org.fireflow.designer.simulation.engine.persistence.MemoryPersistenceService;
import org.fireflow.designer.simulation.output.SimulationDataViewerTopComponent;
import org.fireflow.engine.IFireflowSession;
import org.fireflow.model.WorkflowProcess;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.netbeans.modules.xml.multiview.AbstractMultiViewElement;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author chennieyun
 */
public class FireflowSimulationWorkspace extends AbstractMultiViewElement {
    static ClassPathResource resource = new ClassPathResource(
            "/org/fireflow/designer/simulation/FireflowContext.xml");
    public static XmlBeanFactory beanFactory = new XmlBeanFactory(resource);
    
    Lookup lookup = null;
    JScrollPane designScrollPanel = new JScrollPane();
    SimulatorToolbar simulatorToolbar = null;
    FireflowSimulator fireflowSimulator = null;

    public FireflowSimulationWorkspace(FPDLDataObject argDataObject) {
        super(argDataObject);

        myInitComponents();

        InstanceContent lookupContent = new InstanceContent();
        lookup = new AbstractLookup(lookupContent);
//        lookup = ExplorerUtils.createLookup(((FPDLDataObject) dObj).getExplorerManager(),new ActionMap());
    }

    private void myInitComponents() {
        ProcessGraphModel graphModel = ((FPDLDataObject) this.dObj).getProcessGraphModel();
        GraphLayoutCache layoutCache = new GraphLayoutCache(graphModel, new MyViewFactory(), false);
        HashSet<String> localAttributes = new HashSet<String>();
        localAttributes.add(GraphConstants.BORDERCOLOR);
        localAttributes.add(GraphConstants.FOREGROUND);
        localAttributes.add(GraphConstants.LINECOLOR);
        layoutCache.setLocalAttributes(localAttributes);
        SimulatorPanel graph = new SimulatorPanel(graphModel, layoutCache);

        IFireflowSession fireflowSession = (IFireflowSession) beanFactory.getBean("fireflowSession");
//        StartWorkflowProcessAction startWorkflowProcessAction = 
//                new StartWorkflowProcessAction((WorkflowProcess)graphModel.getWorkflowProcessElement().getContent(),
//                "Start",ImageLoader.getImageIcon("go16.gif"));
//        simulatorToolbar.add(startWorkflowProcessAction);

        fireflowSimulator = new FireflowSimulator(fireflowSession, (WorkflowProcess) graphModel.getWorkflowProcessElement().getContent(), graph, designScrollPanel);

        simulatorToolbar = new SimulatorToolbar(fireflowSimulator);

        this.designScrollPanel.getViewport().add(graph);
        MemoryPersistenceService persistenceService = (MemoryPersistenceService) beanFactory.getBean("persistenceService");
        persistenceService.addStorageChangeListenser(graph);
    }

    public JComponent getVisualRepresentation() {
        return designScrollPanel;
    }

    public JComponent getToolbarRepresentation() {
        return simulatorToolbar;
    }

    public Lookup getLookup() {
        return lookup;
    }

    public void componentShowing() {
        this.callback.updateTitle(dObj.getPrimaryFile().getNameExt());
    }

    public void componentHidden() {

    }

    public void componentActivated() {
        FireflowSimulator.setCurrrentSimulator(fireflowSimulator);
    }

    public void componentDeactivated() {

    }
}
