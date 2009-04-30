/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.properties.editor;

import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.WorkflowProcess;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author chennieyun
 */
public class WorkflowProcessEditor extends EntityEditor {

    JLabel resoruceFileNameLabel = new JLabel("Resource File URL");
    JTextField resourceFileNameField = new JTextField();
    JLabel resourceMangerNameLabel = new JLabel("Rerouce Manager Class");
    JTextField resourceManagerNameField = new JTextField();

    public WorkflowProcessEditor(AbstractNode node,WorkflowProcess process, FPDLDataObject fpdlDataObj) {
        super(node,process, fpdlDataObj);

        init();

        this.validate();
    }

    @Override
    protected void init() {
        super.init();

        //Resource File URL 
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gridbagLayout.setConstraints(resoruceFileNameLabel, gbc);
        this.add(resoruceFileNameLabel);

        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gridbagLayout.setConstraints(resourceFileNameField, gbc);
        this.add(resourceFileNameField);
        
        
        rows++;

        /*
        //Resource File Manager
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gridbagLayout.setConstraints(resourceMangerNameLabel, gbc);
        this.add(resourceMangerNameLabel);

        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gridbagLayout.setConstraints(resourceManagerNameField, gbc);
        this.add(resourceManagerNameField);

        rows++;
        */
        
        
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

    @Override
    protected void revert() {
        super.revert();

        WorkflowProcess workflowProcess = (WorkflowProcess) this.wfElement;

        resourceFileNameField.setText(workflowProcess.getResourceFile());
        this.resourceManagerNameField.setText(workflowProcess.getResourceManager());
    }

    @Override
    protected void addAllEventListeners() {
        super.addAllEventListeners();
        resourceFileNameField.addKeyListener(this);
        resourceManagerNameField.addKeyListener(this);
    }

    @Override
    protected void elementValueChanged(Object sourceObj) {
        super.elementValueChanged(sourceObj);
        WorkflowProcess workflowProcess = (WorkflowProcess) this.wfElement;
        if (sourceObj == resourceFileNameField) {
            workflowProcess.setResourceFile(resourceFileNameField.getText());
        }
        
        if (sourceObj == resourceManagerNameField){
            workflowProcess.setResourceManager(resourceManagerNameField.getText());
        }
    }
}
