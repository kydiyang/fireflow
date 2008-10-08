/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.properties.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.Duration;
import org.fireflow.model.Task;
import org.fireflow.model.reference.Application;
import org.fireflow.model.reference.Form;
import org.fireflow.model.reference.IResourceManager;
import org.fireflow.model.reference.Participant;
import org.fireflow.model.reference.SubWorkflowProcess;
import org.openide.nodes.AbstractNode;
import org.openide.windows.WindowManager;

/**
 *
 * @author chennieyun
 */
public class TaskEditor extends EntityEditor {

    //FormTask
    JComboBox startModel = null;
    JComboBox assignmentStrategy = null;
    JTextField performer = null;
    JTextField duration = null;
    JComboBox isBusinessDuration = null;
    JComboBox durationUnit = null;
    JComboBox defaultView = null;
    JTextField editForm = null;
    JTextField viewForm = null;
    JTextField listForm = null;
    //ToolTask 
    JComboBox execution = null;
    JTextField application = null;
    
    //SubflowTask
    JTextField subWorkflow = null;
//    JPanel formTaskEditor = new JPanel();
//    JPanel toolTaskEditor = new JPanel();
//    JPanel subflowTaskEditor = new JPanel();

    //
    public TaskEditor(AbstractNode node,Task task, FPDLDataObject fpdlDataObj) {
        super(node,task, fpdlDataObj);
        init();
    }

    @Override
    protected void init() {
        super.init();

        Task task = (Task) this.wfElement;
        if (task.getType() != null && task.getType().equals(Task.FORM)) {
            initFormTaskEditor();
        } else if (task.getType() != null && task.getType().equals(Task.TOOL)) {
            this.initToolTaskEditor();
        } else if (task.getType() != null && task.getType().equals(Task.SUBFLOW)) {
            this.initSubflowTaskEditor();
        }

        gbc.anchor = gbc.NORTHWEST;
        gbc.fill = gbc.BOTH;
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 2;
        JPanel fulfilPanel = new JPanel();
        gridbagLayout.setConstraints(fulfilPanel, gbc);
        this.add(fulfilPanel);


        revert();

        this.addAllEventListeners();
    }

    private void initFormTaskEditor() {
//        GridBagLayout gridBagLayout1 = new GridBagLayout();
//        this.formTaskEditor.setLayout(gridBagLayout1);

//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(1, 1, 1, 1);
//        gbc.anchor = GridBagConstraints.WEST;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
        Insets inserts_tmp = new Insets(1, 1, 1, 1);

        //0 start model
        JLabel startModelLabel = new JLabel("Start Model");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gridbagLayout.setConstraints(startModelLabel, gbc);
        this.add(startModelLabel);

        String[] items = new String[]{Task.MANUAL, Task.AUTOMATIC};
        startModel = new JComboBox(items);
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gridbagLayout.setConstraints(startModel, gbc);
        this.add(startModel);

        rows++;


        //1 performer
        JLabel performerLabel = new JLabel("Performer");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gridbagLayout.setConstraints(performerLabel, gbc);
        this.add(performerLabel);

        performer = new JTextField();
        performer.setEditable(false);
        JButton performerEditButton = new JButton("...");
        performerEditButton.setMargin(inserts_tmp);
        performerEditButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showPerformerEditor(performer);
            }
        });
        JPanel performerPanel = new JPanel(new BorderLayout());
        performerPanel.add(performer, BorderLayout.CENTER);
        performerPanel.add(performerEditButton, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gridbagLayout.setConstraints(performerPanel, gbc);
        this.add(performerPanel);

        rows++;

        //2 Assignment strategy
        JLabel completeStrategyLabel = new JLabel("Assignment");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gridbagLayout.setConstraints(completeStrategyLabel, gbc);
        this.add(completeStrategyLabel);

        String[] items_completeStrategy = new String[]{Task.ANY, Task.ALL};
        assignmentStrategy = new JComboBox(items_completeStrategy);
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gridbagLayout.setConstraints(assignmentStrategy, gbc);
        this.add(assignmentStrategy);

        rows++;

        //3 duration
        JLabel durationLabel = new JLabel("Duration");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gridbagLayout.setConstraints(durationLabel, gbc);
        this.add(durationLabel);


        String[] items_durationUnit = new String[]{Duration.DAY, Duration.HOUR, Duration.WEEK, Duration.YEAR,
            Duration.MONTH, Duration.MINUTE, Duration.SECOND
        };
        String[] items_isBusiness = new String[]{"BUSI", ""};
        duration = new JTextField();
        durationUnit = new JComboBox(items_durationUnit);
        isBusinessDuration = new JComboBox(items_isBusiness);
        GridBagLayout layout_tmp = new GridBagLayout();
        JPanel temp_panel = new JPanel(layout_tmp);
        GridBagConstraints gbc_tmp = new GridBagConstraints();
        gbc_tmp.anchor = GridBagConstraints.WEST;
        gbc_tmp.fill = GridBagConstraints.HORIZONTAL;
        gbc_tmp.gridy = 0;
        gbc_tmp.gridx = 0;
        gbc_tmp.gridwidth = 1;
        gbc_tmp.weightx = 1;
        layout_tmp.setConstraints(duration, gbc_tmp);
        temp_panel.add(duration);

        gbc_tmp.gridx = 1;
        gbc_tmp.weightx = 0;
        layout_tmp.setConstraints(isBusinessDuration, gbc_tmp);
        temp_panel.add(isBusinessDuration);

        gbc_tmp.gridx = 2;
        layout_tmp.setConstraints(durationUnit, gbc_tmp);
        temp_panel.add(durationUnit);

        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gridbagLayout.setConstraints(temp_panel, gbc);
        this.add(temp_panel);

        rows++;

        //4 defaultView
        JLabel defaultViewLabel = new JLabel("Default View");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gridbagLayout.setConstraints(defaultViewLabel, gbc);
        this.add(defaultViewLabel);

        String[] items_defaultView = new String[]{Task.EDITFORM, Task.VIEWFORM, Task.LISTFORM};
        defaultView = new JComboBox(items_defaultView);
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gridbagLayout.setConstraints(defaultView, gbc);
        this.add(defaultView);

        rows++;

        //5 edit form
        JLabel editFormLabel = new JLabel("Edit Form");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gridbagLayout.setConstraints(editFormLabel, gbc);
        this.add(editFormLabel);

        editForm = new JTextField();
        editForm.setEditable(false);
        JButton editFormButton = new JButton("...");
        editFormButton.setMargin(inserts_tmp);
        editFormButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showFormEditor(Task.EDITFORM, editForm);
            }
        });
        JPanel editFormPanel = new JPanel(new BorderLayout());
        editFormPanel.add(editForm, BorderLayout.CENTER);
        editFormPanel.add(editFormButton, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gridbagLayout.setConstraints(editFormPanel, gbc);
        this.add(editFormPanel);

        rows++;

        //7 view form
        JLabel viewFormLabel = new JLabel("View Form");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gridbagLayout.setConstraints(viewFormLabel, gbc);
        this.add(viewFormLabel);

        viewForm = new JTextField();
        viewForm.setEditable(false);
        JButton viewFormButton = new JButton("...");
        viewFormButton.setMargin(inserts_tmp);
        viewFormButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showFormEditor(Task.VIEWFORM, viewForm);
            }
        });
        JPanel viewFormPanel = new JPanel(new BorderLayout());
        viewFormPanel.add(viewForm, BorderLayout.CENTER);
        viewFormPanel.add(viewFormButton, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gridbagLayout.setConstraints(viewFormPanel, gbc);
        this.add(viewFormPanel);

        rows++;

        //7 list form
        JLabel listFormLabel = new JLabel("List Form");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gridbagLayout.setConstraints(listFormLabel, gbc);
        this.add(listFormLabel);

        listForm = new JTextField();
        listForm.setEditable(false);
        JButton listFormButton = new JButton("...");
        listFormButton.setMargin(inserts_tmp);
        listFormButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showFormEditor(Task.LISTFORM, listForm);
            }
        });
        JPanel listFormPanel = new JPanel(new BorderLayout());
        listFormPanel.add(listForm, BorderLayout.CENTER);
        listFormPanel.add(listFormButton, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gridbagLayout.setConstraints(listFormPanel, gbc);
        this.add(listFormPanel);

        rows++;
    }

    protected void showPerformerEditor(JTextField textField) {
        IResourceManager resourceManager = this.fpdlDataObject.getResourceManager();
        PerformerEditor performerEditor = PerformerEditor.getInstance();
        performerEditor.revert(this.fpdlDataObject, (Task) this.wfElement, resourceManager, textField);

        JDialog dialog = performerEditor;
        dialog.setPreferredSize(new Dimension(400, 300));
        dialog.setLocationRelativeTo(null);
        dialog.validate();
        dialog.pack();
        dialog.setVisible(true);
    }

    protected void showApplicationEditor(JTextField textField) {
        IResourceManager resourceManager = this.fpdlDataObject.getResourceManager();
        SubWorkflowEditor subworkflowEditor = SubWorkflowEditor.getInstance();
        subworkflowEditor.revert(this.fpdlDataObject, (Task) this.wfElement,resourceManager, textField);

        JDialog dialog = subworkflowEditor;
        dialog.setPreferredSize(new Dimension(400, 300));
        dialog.setLocationRelativeTo(null);
        dialog.validate();
        dialog.pack();
        dialog.setVisible(true);
    }
    
    protected void showSubflowEditor(JTextField textField){
        IResourceManager resourceManager = this.fpdlDataObject.getResourceManager();
        ApplicationEditor applicationEditor = ApplicationEditor.getInstance();
        applicationEditor.revert(this.fpdlDataObject, (Task) this.wfElement,resourceManager, textField);

        JDialog dialog = applicationEditor;
        dialog.setPreferredSize(new Dimension(400, 300));
        dialog.setLocationRelativeTo(null);
        dialog.validate();
        dialog.pack();
        dialog.setVisible(true);        
    }

    protected void showFormEditor(String formType, JTextField textField) {
        IResourceManager resourceManager = this.fpdlDataObject.getResourceManager();
        FormEditor formEditor = FormEditor.getInstance();
        formEditor.revert(this.fpdlDataObject, (Task) this.wfElement, formType, resourceManager, textField);

        JDialog dialog = formEditor;
        dialog.setPreferredSize(new Dimension(400, 300));
        dialog.setLocationRelativeTo(null);
        dialog.validate();
        dialog.pack();
        dialog.setVisible(true);
    }

    private void initToolTaskEditor() {
        //execution
        JLabel executionLabel = new JLabel("Execution");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gridbagLayout.setConstraints(executionLabel, gbc);
        this.add(executionLabel);

        String[] items = new String[]{Task.SYNCHR, Task.ASYNCHR};
        execution = new JComboBox(items);
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gridbagLayout.setConstraints(execution, gbc);
        this.add(execution);

        rows++;

        //3 duration
        JLabel durationLabel = new JLabel("Duration");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gridbagLayout.setConstraints(durationLabel, gbc);
        this.add(durationLabel);


        String[] items_durationUnit = new String[]{Duration.DAY, Duration.HOUR, Duration.WEEK, Duration.YEAR,
            Duration.MONTH, Duration.MINUTE, Duration.SECOND
        };
        String[] items_isBusiness = new String[]{"BUSI", ""};
        duration = new JTextField();
        durationUnit = new JComboBox(items_durationUnit);
        isBusinessDuration = new JComboBox(items_isBusiness);
        GridBagLayout layout_tmp = new GridBagLayout();
        JPanel temp_panel = new JPanel(layout_tmp);
        GridBagConstraints gbc_tmp = new GridBagConstraints();
        gbc_tmp.anchor = GridBagConstraints.WEST;
        gbc_tmp.fill = GridBagConstraints.HORIZONTAL;
        gbc_tmp.gridy = 0;
        gbc_tmp.gridx = 0;
        gbc_tmp.gridwidth = 1;
        gbc_tmp.weightx = 1;
        layout_tmp.setConstraints(duration, gbc_tmp);
        temp_panel.add(duration);

        gbc_tmp.gridx = 1;
        gbc_tmp.weightx = 0;
        layout_tmp.setConstraints(isBusinessDuration, gbc_tmp);
        temp_panel.add(isBusinessDuration);

        gbc_tmp.gridx = 2;
        layout_tmp.setConstraints(durationUnit, gbc_tmp);
        temp_panel.add(durationUnit);

        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gridbagLayout.setConstraints(temp_panel, gbc);
        this.add(temp_panel);

        rows++;

        //1 application
        JLabel applicationLabel = new JLabel("Application");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gridbagLayout.setConstraints(applicationLabel, gbc);
        this.add(applicationLabel);

        application = new JTextField();
        application.setEditable(false);
        JButton applicationEditButton = new JButton("...");
        applicationEditButton.setMargin(new Insets(1, 1, 1, 1));
        applicationEditButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showApplicationEditor(application);
            }
        });
        JPanel applicationPanel = new JPanel(new BorderLayout());
        applicationPanel.add(application, BorderLayout.CENTER);
        applicationPanel.add(applicationEditButton, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gridbagLayout.setConstraints(applicationPanel, gbc);
        this.add(applicationPanel);

        rows++;
    }

    private void initSubflowTaskEditor() {
        //1 application
        JLabel subflowLabel = new JLabel("SubWorkflow");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gridbagLayout.setConstraints(subflowLabel, gbc);
        this.add(subflowLabel);

        subWorkflow = new JTextField();
        subWorkflow.setEditable(false);
        JButton subflowEditButton = new JButton("...");
        subflowEditButton.setMargin(new Insets(1, 1, 1, 1));
        subflowEditButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showSubflowEditor(subWorkflow);
            }
        });
        JPanel applicationPanel = new JPanel(new BorderLayout());
        applicationPanel.add(subWorkflow, BorderLayout.CENTER);
        applicationPanel.add(subflowEditButton, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gridbagLayout.setConstraints(applicationPanel, gbc);
        this.add(applicationPanel);

        rows++;        
    }

    @Override
    protected void revert() {
        super.revert();
        Task task = (Task) this.wfElement;
        if (task.getType() != null && task.getType().equals(Task.FORM)) {
            revertFormTask();
        } else if (task.getType() != null && task.getType().equals(Task.TOOL)) {
            this.revertTookTask();
        } else if (task.getType() != null && task.getType().equals(Task.SUBFLOW)) {
            this.revertSubflowTask();
        }
    }

    protected void revertFormTask() {
        Task task = (Task) this.wfElement;
        this.startModel.setSelectedItem(task.getStartMode());
        this.assignmentStrategy.setSelectedItem(task.getAssignmentStrategy());
        this.defaultView.setSelectedItem(task.getDefaultView());

        if (task.getPerformer() != null) {
            this.performer.setText(task.getPerformer().getDisplayName());
        }

        if (task.getEditForm() != null) {
            this.editForm.setText(task.getEditForm().getDisplayName());
        }

        if (task.getViewForm() != null) {
            this.viewForm.setText(task.getViewForm().getDisplayName());
        }

        if (task.getListForm() != null) {
            this.listForm.setText(task.getListForm().getDisplayName());
        }
        Duration du = task.getDuration();
        if (du != null) {
            this.duration.setText(Integer.toString(du.getValue()));
            this.durationUnit.setSelectedItem(du.getUnit());
            if (du.isBusinessTime()) {
                this.isBusinessDuration.setSelectedIndex(0);
            } else {
                this.isBusinessDuration.setSelectedIndex(1);
            }
        }
    }

    protected void revertTookTask() {
        Task task = (Task) this.wfElement;
        Duration du = task.getDuration();
        if (du != null) {
            this.duration.setText(Integer.toString(du.getValue()));
            this.durationUnit.setSelectedItem(du.getUnit());
            if (du.isBusinessTime()) {
                this.isBusinessDuration.setSelectedIndex(0);
            } else {
                this.isBusinessDuration.setSelectedIndex(1);
            }
        }

        this.execution.setSelectedItem(task.getExecution());

        if (task.getApplication() != null) {
            this.application.setText(task.getApplication().getDisplayName());
        }
    }

    protected void revertSubflowTask() {
        Task task = (Task) this.wfElement;
        if (task.getSubWorkflowProcess()!=null){
            this.subWorkflow.setText(task.getSubWorkflowProcess().getDisplayName());
        }
    }

    @Override
    protected void addAllEventListeners() {
        super.addAllEventListeners();
        Task task = (Task) this.wfElement;
        if (task.getType() != null && task.getType().equals(Task.FORM)) {
            startModel.addActionListener(this);
            assignmentStrategy.addActionListener(this);
            defaultView.addActionListener(this);
            duration.addKeyListener(this);
            durationUnit.addActionListener(this);
            isBusinessDuration.addActionListener(this);
        } else if (task.getType() != null && task.getType().equals(Task.TOOL)) {
            this.execution.addActionListener(this);

            duration.addKeyListener(this);
            durationUnit.addActionListener(this);
            isBusinessDuration.addActionListener(this);
        } else if (task.getType() != null && task.getType().equals(Task.SUBFLOW)) {

        }
    }

    protected void elementValueChanged(Object sourceObj) {
        super.elementValueChanged(sourceObj);
        Task task = (Task) this.wfElement;
        if (task.getType() != null && task.getType().equals(Task.FORM)) {
            if (sourceObj == startModel) {
                task.setStartMode((String) startModel.getSelectedItem());
            }
            if (sourceObj == assignmentStrategy) {
                task.setAssignmentStrategy((String) assignmentStrategy.getSelectedItem());
            }
            if (sourceObj == defaultView) {
                task.setDefaultView((String) defaultView.getSelectedItem());
            }

            if (sourceObj == duration || sourceObj == durationUnit || sourceObj == isBusinessDuration) {
                String value = duration.getText();
                int iValue = 0;
                if (value != null && !value.trim().equals("")) {
                    try {
                        iValue = Integer.parseInt(value);
                    } catch (Exception e) {
                        duration.setText("");
                        return;
                    }
                } else {
                    task.setDuration(null);
                    duration.setText("");
                    return;
                }


                Duration du = task.getDuration();
                if (du == null) {
                    du = new Duration(iValue, (String) durationUnit.getSelectedItem());
                    if (isBusinessDuration.getSelectedIndex() == 0) {
                        du.setBusinessTime(true);
                    } else {
                        du.setBusinessTime(false);
                    }
                    task.setDuration(du);
                } else {
                    du.setValue(iValue);
                    du.setUnit((String) durationUnit.getSelectedItem());
                    if (isBusinessDuration.getSelectedIndex() == 0) {
                        du.setBusinessTime(true);
                    } else {
                        du.setBusinessTime(false);
                    }
                }
            }

        } else if (task.getType() != null && task.getType().equals(Task.TOOL)) {
            if (sourceObj == this.execution) {
                task.setExecution((String) execution.getSelectedItem());
            }

            if (sourceObj == duration || sourceObj == durationUnit || sourceObj == isBusinessDuration) {
                String value = duration.getText();
                int iValue = 0;
                if (value != null && !value.trim().equals("")) {
                    try {
                        iValue = Integer.parseInt(value);
                    } catch (Exception e) {
                        duration.setText("");
                        return;
                    }
                } else {
                    task.setDuration(null);
                    duration.setText("");
                    return;
                }


                Duration du = task.getDuration();
                if (du == null) {
                    du = new Duration(iValue, (String) durationUnit.getSelectedItem());
                    if (isBusinessDuration.getSelectedIndex() == 0) {
                        du.setBusinessTime(true);
                    } else {
                        du.setBusinessTime(false);
                    }
                    task.setDuration(du);
                } else {
                    du.setValue(iValue);
                    du.setUnit((String) durationUnit.getSelectedItem());
                    if (isBusinessDuration.getSelectedIndex() == 0) {
                        du.setBusinessTime(true);
                    } else {
                        du.setBusinessTime(false);
                    }
                }
            }
        } else if (task.getType() != null && task.getType().equals(Task.SUBFLOW)) {

        }
    }
}

class FormEditor extends JDialog {

    private static FormEditor formEditor = null;
    Task currentTask = null;
    String formType = null;
    FPDLDataObject fpdlDataObject = null;
    JTextField currentTextField = null;
    JTextField resourceId = new JTextField();
    JTextField resourceName = new JTextField();
    JTextField resourceDisplayName = new JTextField();
    JTextField uri = new JTextField();
    JTextArea resourceDescription = new JTextArea();
    JPanel currentFormPanel = null;
    JComboBox availableForm = new JComboBox();

    private FormEditor() {
        super(WindowManager.getDefault().getMainWindow(), "Form Editor", true);
//        oldForm = arg_oldForm;
//        resourceManager = arg_resourceManager;
        init();
    }

    public static FormEditor getInstance() {
        if (formEditor == null) {
            formEditor = new FormEditor();
        }
        return formEditor;
    }

    private void init() {
        this.setLayout(new BorderLayout());


        currentFormPanel = new JPanel();
        TitledBorder titledBorder = new TitledBorder("Current Form");
        currentFormPanel.setBorder(titledBorder);
        GridBagLayout theGridBagLayout = new GridBagLayout();
        currentFormPanel.setLayout(theGridBagLayout);

        GridBagConstraints theGbc = new GridBagConstraints();
        theGbc.anchor = GridBagConstraints.WEST;
        theGbc.fill = GridBagConstraints.HORIZONTAL;

        //ID
        /*
        JLabel resourceIdLabel = new JLabel("ID");
        theGbc.gridx = 0;
        theGbc.gridy = 0;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(resourceIdLabel, theGbc);
        currentFormPanel.add(resourceIdLabel);
        theGbc.gridx = 0;
        theGbc.gridy = 1;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(resourceId, theGbc);
        currentFormPanel.add(resourceId);
         */
        //Name
        JLabel resourceNameLabel = new JLabel("Name");
        theGbc.gridx = 0;
        theGbc.gridy = 1;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(resourceNameLabel, theGbc);
        currentFormPanel.add(resourceNameLabel);

        theGbc.gridx = 1;
        theGbc.gridy = 1;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(resourceName, theGbc);
        currentFormPanel.add(resourceName);

        //DisplayName
        JLabel resourceDisplayNameLabel = new JLabel("Display Name");
        theGbc.gridx = 0;
        theGbc.gridy = 2;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(resourceDisplayNameLabel, theGbc);
        currentFormPanel.add(resourceDisplayNameLabel);

        theGbc.gridx = 1;
        theGbc.gridy = 2;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(resourceDisplayName, theGbc);
        currentFormPanel.add(resourceDisplayName);

        //uri
        JLabel uriLabel = new JLabel("URI");
        theGbc.gridx = 0;
        theGbc.gridy = 3;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(uriLabel, theGbc);
        currentFormPanel.add(uriLabel);

        theGbc.gridx = 1;
        theGbc.gridy = 3;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(uri, theGbc);
        currentFormPanel.add(uri);

        //description
        JLabel descriptionLabel = new JLabel("Description");
        theGbc.gridx = 0;
        theGbc.gridy = 4;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(descriptionLabel, theGbc);
        currentFormPanel.add(descriptionLabel);

        JScrollPane descrptionScroll = new JScrollPane(resourceDescription);
        resourceDescription.setRows(3);
        theGbc.anchor = theGbc.NORTHWEST;
        theGbc.fill = theGbc.BOTH;
        theGbc.gridx = 1;
        theGbc.gridy = 4;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(descrptionScroll, theGbc);
        currentFormPanel.add(descrptionScroll);

        //available Forms Panel
        JLabel availableFormLabel = new JLabel("Availabel Forms");
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.add(availableFormLabel, BorderLayout.WEST);
        availablePanel.add(availableForm, BorderLayout.CENTER);
        availableForm.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Form form = (Form) availableForm.getSelectedItem();
                resourceName.setText(form.getName());
                resourceDisplayName.setText(form.getDisplayName());
                uri.setText(form.getUri());
                resourceDescription.setText(form.getDescription());
            }
        });

        //ButtonPanel 
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Form newForm = null;
                if (resourceName.getText() != null || !resourceName.getText().trim().equals("")) {
                    newForm = new Form(resourceName.getText());
                    newForm.setDisplayName(resourceDisplayName.getText());
                    newForm.setDescription(resourceDescription.getText());
                    newForm.setUri(uri.getText());
                    currentTextField.setText(resourceDisplayName.getText());
                } else {
                    currentTextField.setText("");
                }

                if (formType.equals(Task.EDITFORM)) {
                    currentTask.setEditForm(newForm);
                } else if (formType.equals(Task.VIEWFORM)) {
                    currentTask.setViewForm(newForm);
                } else if (formType.equals(Task.LISTFORM)) {
                    currentTask.setListForm(newForm);
                }

                fpdlDataObject.modelUpdatedFromUI();
                fpdlDataObject.setModified(true);

                fpdlDataObject = null;
                currentTask = null;
                formType = null;
                currentTextField = null;
                setVisible(false);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                currentTask = null;
                formType = null;
                currentTextField = null;
                setVisible(false);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        //组织main panel
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(currentFormPanel, BorderLayout.CENTER);
        this.getContentPane().add(availablePanel, BorderLayout.NORTH);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    public void revert(FPDLDataObject dataObject, Task task, String fmType, IResourceManager arg_resourceManager, JTextField textField) {
        fpdlDataObject = dataObject;
        currentTask = task;
        formType = fmType;
        currentTextField = textField;
        Form oldForm = null;
        if (formType.equals(task.EDITFORM)) {
            oldForm = currentTask.getEditForm();
        } else if (formType.equals(currentTask.VIEWFORM)) {
            oldForm = currentTask.getViewForm();
        } else if (formType.equals(currentTask.LISTFORM)) {
            oldForm = currentTask.getListForm();
        }
        if (oldForm != null) {
            this.resourceName.setText(oldForm.getName());
            this.resourceDisplayName.setText(oldForm.getDisplayName());
            this.uri.setText(oldForm.getUri());
            resourceDescription.setText(oldForm.getDescription());
        } else {
            this.resourceName.setText("");
            this.resourceDisplayName.setText("");
            this.uri.setText("");
            resourceDescription.setText("");
        }
        if (arg_resourceManager != null && arg_resourceManager.getForms() != null) {
            final List formList = arg_resourceManager.getForms();
            availableForm.setModel(new DefaultComboBoxModel() {
//                int currentIdx = 0;
                Form selectedForm = null;
//                public void setSelectedItem(Object anItem) {
//                    selectedForm = (Form)anItem;
//                }
//
//                public Object getSelectedItem() {
//                    return selectedForm;
//                }
                public int getSize() {
                    return formList.size();
                }

                public Object getElementAt(int index) {
                    Form form = (Form) formList.get(index);
                    return form;
                }
            });
        } else {
            availableForm.removeAllItems();
        }
    }
}

class ApplicationEditor extends JDialog {

    private static ApplicationEditor appEditor = null;
    Task currentTask = null;
    FPDLDataObject fpdlDataObject = null;
    JTextField currentTextField = null;
    JTextField resourceId = new JTextField();
    JTextField resourceName = new JTextField();
    JTextField resourceDisplayName = new JTextField();
    JTextField handler = new JTextField();
    JTextArea resourceDescription = new JTextArea();
    JPanel currentFormPanel = null;
    JComboBox availableApp = new JComboBox();

    private ApplicationEditor() {
        super(WindowManager.getDefault().getMainWindow(), "Form Editor", true);
//        oldForm = arg_oldForm;
//        resourceManager = arg_resourceManager;
        init();
    }

    public static ApplicationEditor getInstance() {
        if (appEditor == null) {
            appEditor = new ApplicationEditor();
        }
        return appEditor;
    }

    private void init() {
        this.setLayout(new BorderLayout());


        currentFormPanel = new JPanel();
        TitledBorder titledBorder = new TitledBorder("Current Form");
        currentFormPanel.setBorder(titledBorder);
        GridBagLayout theGridBagLayout = new GridBagLayout();
        currentFormPanel.setLayout(theGridBagLayout);

        GridBagConstraints theGbc = new GridBagConstraints();
        theGbc.anchor = GridBagConstraints.WEST;
        theGbc.fill = GridBagConstraints.HORIZONTAL;

        //ID
        /*
        JLabel resourceIdLabel = new JLabel("ID");
        theGbc.gridx = 0;
        theGbc.gridy = 0;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(resourceIdLabel, theGbc);
        currentFormPanel.add(resourceIdLabel);
        theGbc.gridx = 0;
        theGbc.gridy = 1;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(resourceId, theGbc);
        currentFormPanel.add(resourceId);
         */
        //Name
        JLabel resourceNameLabel = new JLabel("Name");
        theGbc.gridx = 0;
        theGbc.gridy = 1;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(resourceNameLabel, theGbc);
        currentFormPanel.add(resourceNameLabel);

        theGbc.gridx = 1;
        theGbc.gridy = 1;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(resourceName, theGbc);
        currentFormPanel.add(resourceName);

        //DisplayName
        JLabel resourceDisplayNameLabel = new JLabel("Display Name");
        theGbc.gridx = 0;
        theGbc.gridy = 2;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(resourceDisplayNameLabel, theGbc);
        currentFormPanel.add(resourceDisplayNameLabel);

        theGbc.gridx = 1;
        theGbc.gridy = 2;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(resourceDisplayName, theGbc);
        currentFormPanel.add(resourceDisplayName);

        //uri
        JLabel uriLabel = new JLabel("Handler");
        theGbc.gridx = 0;
        theGbc.gridy = 3;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(uriLabel, theGbc);
        currentFormPanel.add(uriLabel);

        theGbc.gridx = 1;
        theGbc.gridy = 3;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(handler, theGbc);
        currentFormPanel.add(handler);

        //description
        JLabel descriptionLabel = new JLabel("Description");
        theGbc.gridx = 0;
        theGbc.gridy = 4;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(descriptionLabel, theGbc);
        currentFormPanel.add(descriptionLabel);

        JScrollPane descrptionScroll = new JScrollPane(resourceDescription);
        resourceDescription.setRows(3);
        theGbc.anchor = theGbc.NORTHWEST;
        theGbc.fill = theGbc.BOTH;
        theGbc.gridx = 1;
        theGbc.gridy = 4;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(descrptionScroll, theGbc);
        currentFormPanel.add(descrptionScroll);

        //available Forms Panel
        JLabel availableFormLabel = new JLabel("Availabel Applications");
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.add(availableFormLabel, BorderLayout.WEST);
        availablePanel.add(availableApp, BorderLayout.CENTER);
        availableApp.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Application app = (Application) availableApp.getSelectedItem();
                resourceName.setText(app.getName());
                resourceDisplayName.setText(app.getDisplayName());
                handler.setText(app.getHandler());
                resourceDescription.setText(app.getDescription());
            }
        });

        //ButtonPanel 
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Application newApp = null;
                if (resourceName.getText() != null || !resourceName.getText().trim().equals("")) {
                    newApp = new Application(resourceName.getText());
                    newApp.setDisplayName(resourceDisplayName.getText());
                    newApp.setDescription(resourceDescription.getText());
                    newApp.setHandler(handler.getText());
                    currentTextField.setText(resourceDisplayName.getText());
                } else {
                    currentTextField.setText("");
                }

                currentTask.setApplication(newApp);

                fpdlDataObject.modelUpdatedFromUI();
                fpdlDataObject.setModified(true);
                currentTask = null;
                currentTextField = null;
                setVisible(false);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                currentTask = null;
                currentTextField = null;
                setVisible(false);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        //组织main panel
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(currentFormPanel, BorderLayout.CENTER);
        this.getContentPane().add(availablePanel, BorderLayout.NORTH);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    public void revert(FPDLDataObject dataObj,Task task,IResourceManager arg_resourceManager, JTextField textField) {
        currentTask = task;
        fpdlDataObject = dataObj;
        currentTextField = textField;
        Application oldApplication = null;
        oldApplication = currentTask.getApplication();

        if (oldApplication != null) {
            this.resourceName.setText(oldApplication.getName());
            this.resourceDisplayName.setText(oldApplication.getDisplayName());
            this.handler.setText(oldApplication.getHandler());
            resourceDescription.setText(oldApplication.getDescription());
        } else {
            this.resourceName.setText("");
            this.resourceDisplayName.setText("");
            this.handler.setText("");
            resourceDescription.setText("");
        }
        if (arg_resourceManager != null && arg_resourceManager.getApplications() != null) {
            final List applicationList = arg_resourceManager.getApplications();
            availableApp.setModel(new DefaultComboBoxModel() {
//                int currentIdx = 0;
//                Form selectedForm = null;
//                public void setSelectedItem(Object anItem) {
//                    selectedForm = (Form)anItem;
//                }
//
//                public Object getSelectedItem() {
//                    return selectedForm;
//                }
                public int getSize() {
                    if (applicationList==null)return 0;
                    return applicationList.size();
                }

                public Object getElementAt(int index) {
                    Application app = (Application) applicationList.get(index);
                    return app;
                }
            });
        } else {
            availableApp.removeAllItems();
        }
    }
}
class SubWorkflowEditor extends JDialog {

    private static SubWorkflowEditor subflowEditor = null;
    Task currentTask = null;
    FPDLDataObject fpdlDataObject = null;
    JTextField currentTextField = null;
    JTextField resourceId = new JTextField();
    JTextField resourceName = new JTextField();
    JTextField resourceDisplayName = new JTextField();
    JTextField referencedWorkflowId = new JTextField();
    JTextArea resourceDescription = new JTextArea();
    JPanel currentSubflowPanel = null;
    JComboBox availableWorkflowProcess = new JComboBox();

    private SubWorkflowEditor() {
        super(WindowManager.getDefault().getMainWindow(), "SubWorkflow Editor", true);
//        oldForm = arg_oldForm;
//        resourceManager = arg_resourceManager;
        init();
    }

    public static SubWorkflowEditor getInstance() {
        if (subflowEditor == null) {
            subflowEditor = new SubWorkflowEditor();
        }
        return subflowEditor;
    }

    private void init() {
        this.setLayout(new BorderLayout());


        currentSubflowPanel = new JPanel();
        TitledBorder titledBorder = new TitledBorder("Current SubWorkflow");
        currentSubflowPanel.setBorder(titledBorder);
        GridBagLayout theGridBagLayout = new GridBagLayout();
        currentSubflowPanel.setLayout(theGridBagLayout);

        GridBagConstraints theGbc = new GridBagConstraints();
        theGbc.anchor = GridBagConstraints.WEST;
        theGbc.fill = GridBagConstraints.HORIZONTAL;

        //ID
        /*
        JLabel resourceIdLabel = new JLabel("ID");
        theGbc.gridx = 0;
        theGbc.gridy = 0;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(resourceIdLabel, theGbc);
        currentFormPanel.add(resourceIdLabel);
        theGbc.gridx = 0;
        theGbc.gridy = 1;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(resourceId, theGbc);
        currentFormPanel.add(resourceId);
         */
        //Name
        JLabel resourceNameLabel = new JLabel("Name");
        theGbc.gridx = 0;
        theGbc.gridy = 1;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(resourceNameLabel, theGbc);
        currentSubflowPanel.add(resourceNameLabel);

        theGbc.gridx = 1;
        theGbc.gridy = 1;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(resourceName, theGbc);
        currentSubflowPanel.add(resourceName);

        //DisplayName
        JLabel resourceDisplayNameLabel = new JLabel("Display Name");
        theGbc.gridx = 0;
        theGbc.gridy = 2;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(resourceDisplayNameLabel, theGbc);
        currentSubflowPanel.add(resourceDisplayNameLabel);

        theGbc.gridx = 1;
        theGbc.gridy = 2;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(resourceDisplayName, theGbc);
        currentSubflowPanel.add(resourceDisplayName);

        //uri
        JLabel uriLabel = new JLabel("Workflow ID");
        theGbc.gridx = 0;
        theGbc.gridy = 3;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(uriLabel, theGbc);
        currentSubflowPanel.add(uriLabel);

        theGbc.gridx = 1;
        theGbc.gridy = 3;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(referencedWorkflowId, theGbc);
        currentSubflowPanel.add(referencedWorkflowId);

        //description
        JLabel descriptionLabel = new JLabel("Description");
        theGbc.gridx = 0;
        theGbc.gridy = 4;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(descriptionLabel, theGbc);
        currentSubflowPanel.add(descriptionLabel);

        JScrollPane descrptionScroll = new JScrollPane(resourceDescription);
        resourceDescription.setRows(3);
        theGbc.anchor = theGbc.NORTHWEST;
        theGbc.fill = theGbc.BOTH;
        theGbc.gridx = 1;
        theGbc.gridy = 4;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(descrptionScroll, theGbc);
        currentSubflowPanel.add(descrptionScroll);

        //available Forms Panel
        JLabel availableWorkflowProcessLabel = new JLabel("Availabel Workflow Processes");
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.add(availableWorkflowProcessLabel, BorderLayout.WEST);
        availablePanel.add(this.availableWorkflowProcess, BorderLayout.CENTER);
        this.availableWorkflowProcess.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SubWorkflowProcess subflow = (SubWorkflowProcess) availableWorkflowProcess.getSelectedItem();
                resourceName.setText(subflow.getName());
                resourceDisplayName.setText(subflow.getDisplayName());
                referencedWorkflowId.setText(subflow.getWorkflowProcessId());
                resourceDescription.setText(subflow.getDescription());
            }
        });

        //ButtonPanel 
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SubWorkflowProcess newSubflow = null;
                if (resourceName.getText() != null || !resourceName.getText().trim().equals("")) {
                    newSubflow = new SubWorkflowProcess(resourceName.getText());
                    newSubflow.setDisplayName(resourceDisplayName.getText());
                    newSubflow.setDescription(resourceDescription.getText());
                    newSubflow.setWorkflowProcessId(referencedWorkflowId.getText());
                    currentTextField.setText(resourceDisplayName.getText());
                } else {
                    currentTextField.setText("");
                }

                currentTask.setSubWorkflowProcess(newSubflow);

                fpdlDataObject.modelUpdatedFromUI();
                fpdlDataObject.setModified(true);
                currentTask = null;
                currentTextField = null;
                setVisible(false);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                currentTask = null;
                currentTextField = null;
                setVisible(false);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        //组织main panel
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(currentSubflowPanel, BorderLayout.CENTER);
        this.getContentPane().add(availablePanel, BorderLayout.NORTH);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    public void revert(FPDLDataObject dataObj,Task task,IResourceManager arg_resourceManager, JTextField textField) {
        currentTask = task;
        fpdlDataObject = dataObj;
        currentTextField = textField;
        SubWorkflowProcess oldSubflow = null;
        oldSubflow = currentTask.getSubWorkflowProcess();

        if (oldSubflow != null) {
            this.resourceName.setText(oldSubflow.getName());
            this.resourceDisplayName.setText(oldSubflow.getDisplayName());
            this.referencedWorkflowId.setText(oldSubflow.getWorkflowProcessId());
            resourceDescription.setText(oldSubflow.getDescription());
        } else {
            this.resourceName.setText("");
            this.resourceDisplayName.setText("");
            this.referencedWorkflowId.setText("");
            resourceDescription.setText("");
        }
        if (arg_resourceManager != null && arg_resourceManager.getApplications() != null) {
            final List applicationList = arg_resourceManager.getApplications();
            availableWorkflowProcess.setModel(new DefaultComboBoxModel() {
//                int currentIdx = 0;
//                Form selectedForm = null;
//                public void setSelectedItem(Object anItem) {
//                    selectedForm = (Form)anItem;
//                }
//
//                public Object getSelectedItem() {
//                    return selectedForm;
//                }
                public int getSize() {
                    if (applicationList==null)return 0;
                    return applicationList.size();
                }

                public Object getElementAt(int index) {
                    Application app = (Application) applicationList.get(index);
                    return app;
                }
            });
        } else {
            availableWorkflowProcess.removeAllItems();
        }
    }
}

class PerformerEditor extends JDialog {

    private static PerformerEditor performerEditor = null;
    Task currentTask = null;
    String formType = null;
    FPDLDataObject fpdlDataObject = null;
    JTextField currentTextField = null;
    JTextField resourceId = new JTextField();
    JTextField resourceName = new JTextField();
    JTextField resourceDisplayName = new JTextField();
    JTextField assignmentHandler = new JTextField();
    JTextArea resourceDescription = new JTextArea();
    JPanel currentFormPanel = null;
    JComboBox availableParticipant = new JComboBox();

    private PerformerEditor() {
        super(WindowManager.getDefault().getMainWindow(), "Performer Editor", true);
//        oldForm = arg_oldForm;
//        resourceManager = arg_resourceManager;
        init();
    }

    public static PerformerEditor getInstance() {
        if (performerEditor == null) {
            performerEditor = new PerformerEditor();
        }
        return performerEditor;
    }

    private void init() {
        this.setLayout(new BorderLayout());


        currentFormPanel = new JPanel();
        TitledBorder titledBorder = new TitledBorder("Current Performer");
        currentFormPanel.setBorder(titledBorder);
        GridBagLayout theGridBagLayout = new GridBagLayout();
        currentFormPanel.setLayout(theGridBagLayout);

        GridBagConstraints theGbc = new GridBagConstraints();
        theGbc.anchor = GridBagConstraints.WEST;
        theGbc.fill = GridBagConstraints.HORIZONTAL;

        //ID
        /*
        JLabel resourceIdLabel = new JLabel("ID");
        theGbc.gridx = 0;
        theGbc.gridy = 0;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(resourceIdLabel, theGbc);
        currentFormPanel.add(resourceIdLabel);
        theGbc.gridx = 0;
        theGbc.gridy = 1;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(resourceId, theGbc);
        currentFormPanel.add(resourceId);
         */
        //Name
        JLabel resourceNameLabel = new JLabel("Name");
        theGbc.gridx = 0;
        theGbc.gridy = 1;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(resourceNameLabel, theGbc);
        currentFormPanel.add(resourceNameLabel);

        theGbc.gridx = 1;
        theGbc.gridy = 1;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(resourceName, theGbc);
        currentFormPanel.add(resourceName);

        //DisplayName
        JLabel resourceDisplayNameLabel = new JLabel("Display Name");
        theGbc.gridx = 0;
        theGbc.gridy = 2;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(resourceDisplayNameLabel, theGbc);
        currentFormPanel.add(resourceDisplayNameLabel);

        theGbc.gridx = 1;
        theGbc.gridy = 2;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(resourceDisplayName, theGbc);
        currentFormPanel.add(resourceDisplayName);

        //uri
        JLabel uriLabel = new JLabel("Assignment Handler");
        theGbc.gridx = 0;
        theGbc.gridy = 3;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(uriLabel, theGbc);
        currentFormPanel.add(uriLabel);

        theGbc.gridx = 1;
        theGbc.gridy = 3;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(assignmentHandler, theGbc);
        currentFormPanel.add(assignmentHandler);

        //description
        JLabel descriptionLabel = new JLabel("Description");
        theGbc.gridx = 0;
        theGbc.gridy = 4;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(descriptionLabel, theGbc);
        currentFormPanel.add(descriptionLabel);

        JScrollPane descrptionScroll = new JScrollPane(resourceDescription);
        resourceDescription.setRows(3);
        theGbc.anchor = theGbc.NORTHWEST;
        theGbc.fill = theGbc.BOTH;
        theGbc.gridx = 1;
        theGbc.gridy = 4;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(descrptionScroll, theGbc);
        currentFormPanel.add(descrptionScroll);

        //available Forms Panel
        JLabel availableFormLabel = new JLabel("Available Participants");
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.add(availableFormLabel, BorderLayout.WEST);
        availablePanel.add(availableParticipant, BorderLayout.CENTER);
        availableParticipant.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Participant part = (Participant) availableParticipant.getSelectedItem();
                resourceName.setText(part.getName());
                resourceDisplayName.setText(part.getDisplayName());
                assignmentHandler.setText(part.getAssignmentHandler());
                resourceDescription.setText(part.getDescription());
            }
        });

        //ButtonPanel 
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Participant newPart = null;
                if (resourceName.getText() != null || !resourceName.getText().trim().equals("")) {
                    newPart = new Participant(resourceName.getText());
                    newPart.setDisplayName(resourceDisplayName.getText());
                    newPart.setDescription(resourceDescription.getText());
                    newPart.setAssignmentHandler(assignmentHandler.getText());
                    currentTextField.setText(resourceDisplayName.getText());
                } else {
                    currentTextField.setText("");
                }

                currentTask.setPerformer(newPart);
                fpdlDataObject.modelUpdatedFromUI();
                fpdlDataObject.setModified(true);

                fpdlDataObject = null;
                currentTask = null;
                formType = null;
                currentTextField = null;
                setVisible(false);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                currentTask = null;
                formType = null;
                currentTextField = null;
                setVisible(false);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        //组织main panel
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(currentFormPanel, BorderLayout.CENTER);
        this.getContentPane().add(availablePanel, BorderLayout.NORTH);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    public void revert(FPDLDataObject dataObj, Task task, IResourceManager arg_resourceManager, JTextField textField) {
        fpdlDataObject = dataObj;
        currentTask = task;
        currentTextField = textField;
        Participant oldPart = null;
        oldPart = currentTask.getPerformer();

        if (oldPart != null) {
            this.resourceName.setText(oldPart.getName());
            this.resourceDisplayName.setText(oldPart.getDisplayName());
            this.assignmentHandler.setText(oldPart.getAssignmentHandler());
            resourceDescription.setText(oldPart.getDescription());
        } else {
            this.resourceName.setText("");
            this.resourceDisplayName.setText("");
            this.assignmentHandler.setText("");
            resourceDescription.setText("");
        }
        if (arg_resourceManager != null && arg_resourceManager.getForms() != null) {
            final List participantList = arg_resourceManager.getParticipants();
            availableParticipant.setModel(new DefaultComboBoxModel() {
//                int currentIdx = 0;
                Form selectedForm = null;
//                public void setSelectedItem(Object anItem) {
//                    selectedForm = (Form)anItem;
//                }
//
//                public Object getSelectedItem() {
//                    return selectedForm;
//                }
                public int getSize() {
                    if (participantList == null) {
                        return 0;
                    }
                    return participantList.size();
                }

                public Object getElementAt(int index) {
                    Participant part = (Participant) participantList.get(index);
                    return part;
                }
            });
        } else {
            availableParticipant.removeAllItems();
        }
    }
}
