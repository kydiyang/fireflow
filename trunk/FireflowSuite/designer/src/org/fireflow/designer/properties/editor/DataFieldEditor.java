/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.properties.editor;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.DataField;
import org.fireflow.model.IWFElement;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author chennieyun
 */
public class DataFieldEditor extends EntityEditor {

    JComboBox dataType = null;
    JTextField initialValue = new JTextField();

    public DataFieldEditor(AbstractNode node,IWFElement element, FPDLDataObject dataObj) {
        super(node,element, dataObj);
        init();
//                System.out.println("====dataObj is null?"+dataObj);
    }

    protected void init() {
        super.init();

        JLabel dataTypeLabel = new JLabel("Data Type");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gridbagLayout.setConstraints(dataTypeLabel, gbc);
        this.add(dataTypeLabel);

        String[] items = new String[]{DataField.STRING, DataField.INTEGER, DataField.FLOAT, DataField.DATETIME, DataField.BOOLEAN};
        dataType = new JComboBox(items);
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.weightx = 1;
        gbc.gridwidth = gbc.REMAINDER;
        gridbagLayout.setConstraints(dataType, gbc);
        this.add(dataType);

        rows++;

        JLabel initValueLabel = new JLabel("Initial Value");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gridbagLayout.setConstraints(initValueLabel, gbc);
        this.add(initValueLabel);

        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.weightx = 1;
        gbc.gridwidth = gbc.REMAINDER;
        gridbagLayout.setConstraints(initialValue, gbc);
        this.add(initialValue);

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

        //赋值
        revert();

        //链接事件监听器
        this.addAllEventListeners();
    }

    protected void revert() {
        super.revert();
        DataField df = (DataField) this.wfElement;

        this.initialValue.setText(df.getInitialValue());
        dataType.setSelectedItem(df.getDataType());
    }

    protected void addAllEventListeners() {
        super.addAllEventListeners();
        initialValue.addKeyListener(this);
        dataType.addActionListener(this);
    }

    protected void elementValueChanged(Object sourceObj) {
        super.elementValueChanged(sourceObj);
        DataField df = (DataField) this.wfElement;
        if (sourceObj == initialValue) {
            df.setInitialValue(initialValue.getText());
        }

        if (sourceObj == dataType) {
            df.setDataType((String) dataType.getSelectedItem());
        }
    }
}
