/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.datamodel;

import cn.bestsolution.tools.resourcesmanager.util.ImageLoader;
import cn.bestsolution.tools.resourcesmanager.util.Utilities;
import java.awt.Image;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.fireflow.designer.actions.DeleteElementAction;
import org.fireflow.designer.actions.NewActivityAction;
import org.fireflow.designer.actions.NewActivityWithFormAction;
import org.fireflow.designer.actions.NewActivityWithSubflowAction;
import org.fireflow.designer.actions.NewActivityWithToolAction;
import org.fireflow.designer.actions.NewDataFieldAction;
import org.fireflow.designer.actions.NewFormTask;
import org.fireflow.designer.actions.NewSubflowTask;
import org.fireflow.designer.actions.NewToolTask;
import org.fireflow.designer.datamodel.element.WorkflowProcessElement;
import org.fireflow.designer.editpanel.WorkflowProcessEditPanel;
import org.fireflow.designer.editpanel.graph.ProcessGraphModel;
import org.fireflow.designer.editpanel.graph.actions.NewEndNodeAction;
import org.fireflow.designer.editpanel.graph.actions.NewStartNodeAction;
import org.fireflow.designer.editpanel.graph.actions.NewSynchronizerAction;
import org.fireflow.designer.simulation.FireflowSimulationWorkspace;
import org.fireflow.designer.simulation.engine.definition.DefinitionService4Simulation;
import org.fireflow.designer.util.DesignerConstant;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.io.FPDLParserException;
import org.fireflow.model.io.FPDLSerializerException;
import org.fireflow.model.io.JAXP_FPDL_Parser;
import org.fireflow.model.io.JAXP_FPDL_Serializer;
import org.fireflow.model.reference.IResourceManager;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.modules.xml.multiview.DesignMultiViewDesc;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.netbeans.modules.xml.multiview.XmlMultiViewDataObject;
import org.netbeans.modules.xml.multiview.XmlMultiViewDataSynchronizer;
import org.openide.explorer.ExplorerManager;
import org.openide.text.DataEditorSupport;
import org.openide.cookies.SaveCookie;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.WindowManager;

public class FPDLDataObject extends XmlMultiViewDataObject {//MultiDataObject {

    private ExplorerManager explorerManager = null;
    private WorkflowProcess workflowProcess = null;
    private Action[] actions = null;
    private ModelSynchronizer modelSynchronizer = null;
    private static final String IMAGE_ICON_BASE = "workflowprocess16.gif";
    private ProcessGraphModel graphModel = null;

    public FPDLDataObject(FileObject pf, FPDLDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        System.out.println("====Inside FPDLDataObject:: ============================-----------------------");
        System.out.println("====the fileobj's name is " + this.getPrimaryFile().getName() + "; path is " + this.getPrimaryFile().getPath());

        //模版文件不进一步解析
        if ("Templates/Other/ProcessDefinition.xml".equals(this.getPrimaryFile().getPath())) {
            return;
        }

        try {
            parseDefinitionFile();
        } catch (Exception ex) {
            //如果发生任何错误，则将WorkflowProcess 初始化成一个空的WorkflowProcess
            this.workflowProcess = new WorkflowProcess("ERROR");
            this.workflowProcess.setDescription(Utilities.errorStackToString(ex));
        }
        
        initExplorerManager();

        initActions(explorerManager);
        
        try {


            graphModel = new ProcessGraphModel(this.explorerManager);

            modelSynchronizer = new ModelSynchronizer(this);

            DefinitionService4Simulation defSrv = (DefinitionService4Simulation) FireflowSimulationWorkspace.beanFactory.getBean("definitionService");
            defSrv.setWorkflowProcess(this.workflowProcess);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void parseDefinitionFile() throws IOException, FPDLParserException {

        java.io.InputStream is = getEditorSupport().getInputStream();

        String encoding = this.getEncodingHelper().detectEncoding(is);

        JAXP_FPDL_Parser parser = new JAXP_FPDL_Parser();

        workflowProcess = parser.parse(is);
    }

    private void initExplorerManager() {
        InstanceContent lookupContent = new InstanceContent();
        AbstractLookup lookup = new AbstractLookup(lookupContent);
        lookupContent.add(this);
        lookupContent.add(workflowProcess);

        WorkflowProcessElement workflowProcessElement = new WorkflowProcessElement(lookup);

        Children.Array array = new Children.Array();
        array.add(new Node[]{workflowProcessElement});
        AbstractNode root = new AbstractNode(array) {

            @Override
            public Action[] getActions(boolean b) {
                return new Action[]{};
//                return actions;
            }
        };

        explorerManager = new ExplorerManager();

//        lookupContent.add(explorerManager);

        explorerManager.setRootContext(root);
    }

    protected void initActions(ExplorerManager explorerManager) {
        actions = new Action[14];
//        ArrayList actionArr = new ArrayList();
        NewStartNodeAction newStartNodeAction = new NewStartNodeAction(explorerManager, "新建开始节点", ImageLoader.getImageIcon("startNode16.gif"));
        actions[0] = newStartNodeAction;

        NewActivityAction newActivityAction = new NewActivityAction(explorerManager, "新建空环节", ImageLoader.getImageIcon("activity16.gif"));
        actions[1] = newActivityAction;

        NewActivityWithFormAction newActivityWithFormAction = new NewActivityWithFormAction(explorerManager, "新建表单环节", ImageLoader.getImageIcon("activity_manual_16.gif"));
        actions[2] = newActivityWithFormAction;

        NewActivityWithSubflowAction newActivityWithSubflowAction = new NewActivityWithSubflowAction(explorerManager, "新建子流程环节", ImageLoader.getImageIcon("activity_subflow_16.gif"));
        actions[3] = newActivityWithSubflowAction;

        NewActivityWithToolAction newActivityWithToolAction = new NewActivityWithToolAction(explorerManager, "新建应用程序环节", ImageLoader.getImageIcon("activity_tool_16.gif"));
        actions[4] = newActivityWithToolAction;

        NewSynchronizerAction newSynchronizerAction = new NewSynchronizerAction(explorerManager, "新建同步器节点", ImageLoader.getImageIcon("synchronizer16.gif"));
        actions[5] = newSynchronizerAction;

        NewEndNodeAction newEndNodeAction = new NewEndNodeAction(explorerManager, "新建结束节点", ImageLoader.getImageIcon("endNode16.gif"));
        actions[6] = newEndNodeAction;

        NewDataFieldAction newDatafieldAction = new NewDataFieldAction(explorerManager, "新建流程变量", ImageLoader.getImageIcon("datafield16.gif"));
        actions[7] = newDatafieldAction;

        //insert seperator
        actions[8] = null;

        NewFormTask newFormTask = new NewFormTask(explorerManager, "新建表单", ImageLoader.getImageIcon("manual_task_16.gif"));
        actions[9] = newFormTask;

        NewSubflowTask newSubflowTask = new NewSubflowTask(explorerManager, "新建子流程", ImageLoader.getImageIcon("subflow_task_16.gif"));
        actions[10] = newSubflowTask;

        NewToolTask newToolTask = new NewToolTask(explorerManager, "新建应用程序", ImageLoader.getImageIcon("tool_task_16.gif"));
        actions[11] = newToolTask;

        //insert one seperator 
        actions[12] = null;

        DeleteElementAction deleteElementAction = new DeleteElementAction(explorerManager, "删除", ImageLoader.getImageIcon("delete16.gif"));
        actions[13] = deleteElementAction;

    }

    @Override
    protected Node createNodeDelegate() {
        return new FPDLDataNode(this, getLookup());
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public ProcessGraphModel getProcessGraphModel() {
        return this.graphModel;
    }

    public Action[] getActions() {

        return actions;
    }

    public void modelUpdatedFromUI() {
        modelSynchronizer.requestUpdateData();
    }

//    public WorkflowProcessElement getWorkflowProcessElement() {
//        return workflowProcessElement;
//    }
//
//    public void setWorkflowProcessElement(WorkflowProcessElement workflowProcessElement) {
//        this.workflowProcessElement = workflowProcessElement;
//    }
    /*
    private class MySaveCookieImpl implements SaveCookie {
    public void save() throws IOException {
    setModified(false);
    }
    }
     * */
    @Override
    protected DesignMultiViewDesc[] getMultiViewDesc() {

        return new DesignMultiViewDesc[]{new MyDesignMultiViewDesc(this), new MySimulatorMultiViewDesc(this)};
//        return new DesignMultiViewDesc[]{new MyDesignMultiViewDesc(this)};
    }

    @Override
    protected String getPrefixMark() {
//        throw new UnsupportedOperationException("Not supported yet.");
        return null;
    }

    private class MyDesignMultiViewDesc extends DesignMultiViewDesc {

        public MyDesignMultiViewDesc(FPDLDataObject fpdlDataObject) {
            super(fpdlDataObject, "Design");

        }

        @Override
        public MultiViewElement createElement() {
            return new WorkflowProcessEditPanel((FPDLDataObject) this.getDataObject());
        }

        @Override
        public Image getIcon() {

            return ImageLoader.getImage(IMAGE_ICON_BASE);
        }

        @Override
        public String preferredID() {
            return "Process_MultiView_Design";
        }
    }

    private class MySimulatorMultiViewDesc extends DesignMultiViewDesc {

        public MySimulatorMultiViewDesc(FPDLDataObject fpdlDataObject) {
            super(fpdlDataObject, "Simulator");
        }

        @Override
        public MultiViewElement createElement() {
            return new FireflowSimulationWorkspace((FPDLDataObject) this.getDataObject());
        }

        @Override
        public Image getIcon() {

            return ImageLoader.getImage(IMAGE_ICON_BASE);
        }

        @Override
        public String preferredID() {
            return "Process_MultiView_Simulator";
        }
    }

    private class ModelSynchronizer extends XmlMultiViewDataSynchronizer {

        public ModelSynchronizer(FPDLDataObject dataObj) {
            super(dataObj, 500);
        }

        @Override
        protected boolean mayUpdateData(boolean arg0) {
            return true;
//            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        protected void updateDataFromModel(Object arg0, FileLock arg1, boolean arg2) {
            try {
                ExplorerManager explorerManager = (ExplorerManager) arg0;
                WorkflowProcessElement workflowProcessElement = (WorkflowProcessElement) explorerManager.getRootContext().getChildren().getNodes()[0];
                WorkflowProcess workflowProcess = (WorkflowProcess) workflowProcessElement.getContent();
//                Dom4JFPDLSerializer ser = new Dom4JFPDLSerializer();
                JAXP_FPDL_Serializer ser = new JAXP_FPDL_Serializer();
                StringWriter out = new StringWriter();
//                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
//                BufferedOutputStream out = new BufferedOutputStream();
                ser.serialize(workflowProcess, out);
                out.close();

                getDataCache().setData(arg1, out.toString(), arg2);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (FPDLSerializerException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        protected Object getModel() {
            return explorerManager;
        }

        @Override
        protected void reloadModelFromData() {
//            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private class MySaveCookieImpl implements SaveCookie {

        public void save() throws IOException {
            modelUpdatedFromUI();
        }
    }

    public IResourceManager getResourceManager() {

        if ((workflowProcess.getResourceFile() == null || workflowProcess.getResourceFile().trim().equals(""))) {
            return null;
        }

        if (DesignerConstant.RUN_ENV == 1) {

            Project project = FileOwnerQuery.getOwner(this.getPrimaryFile());

            ClassPath classpath = ClassPath.getClassPath(project.getProjectDirectory().getFileObject("src"),
                    ClassPath.SOURCE);

            String resourceFileName = workflowProcess.getResourceFile();
            if (resourceFileName.startsWith("/")) {
                resourceFileName = resourceFileName.substring(1);
            }

            FileObject fo1 = classpath.findResource(resourceFileName);

            if (fo1 == null) {
                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),
                        "File [" + workflowProcess.getResourceFile() + "] Not Exist!", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            try {
                IResourceManager resourceManager = ResourceManagersPool.getInstance().getResourceManager(
                        workflowProcess.getResourceFile(), fo1.lastModified(), fo1.getInputStream());
                return resourceManager;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),
                        e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } else if (DesignerConstant.RUN_ENV == 2) {
        }

        return null;

    }
}
