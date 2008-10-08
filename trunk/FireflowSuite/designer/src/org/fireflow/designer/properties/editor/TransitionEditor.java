/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.properties.editor;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.net.Transition;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author chennieyun
 */
public class TransitionEditor extends EntityEditor {

    private JTextField conditionField = null;

    public TransitionEditor(AbstractNode node,Transition transition, FPDLDataObject fpdlDataObj) {
        super(node,transition, fpdlDataObj);
        init();
    }

    @Override
    protected void init() {
        super.init();

        JLabel conditionLabel = new JLabel("Condition");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gridbagLayout.setConstraints(conditionLabel, gbc);
        this.add(conditionLabel);

        conditionField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.weightx = 1;
        gbc.gridwidth = gbc.REMAINDER;
        gridbagLayout.setConstraints(conditionField, gbc);
        this.add(conditionField);

        rows++;

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

        this.revert();

        this.addAllEventListeners();
    }

    protected void revert() {
        super.revert();

        Transition trans = (Transition) this.wfElement;

        conditionField.setText(trans.getCondition());
    }

    protected void elementValueChanged(Object sourceObj) {
        super.elementValueChanged(sourceObj);
        Transition trans = (Transition) this.wfElement;
        if (sourceObj == conditionField) {
            trans.setCondition(conditionField.getText());
        }
    }

    protected void addAllEventListeners() {
        super.addAllEventListeners();
        conditionField.addKeyListener(this);
    }
}
