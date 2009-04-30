/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.simulation.action;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.fireflow.designer.simulation.FireflowSimulator;

/**
 *
 * @author chennieyun
 */
public class SetWorkflowVariableAction extends AbstractAction {

    FireflowSimulator fireflowSimulator = null;

    public SetWorkflowVariableAction(FireflowSimulator simulator, String title, Icon image) {
        super(title, image);
        fireflowSimulator = simulator;
    }

    public void actionPerformed(ActionEvent e) {
        VariableDialog dialog = new VariableDialog();
        dialog.setPreferredSize(new Dimension(200, 150));
        dialog.setLocationRelativeTo(null);
        dialog.validate();
        dialog.pack();
        dialog.setVisible(true);
        if (dialog.isOk()) {
            fireflowSimulator.setWorkflowVariable(dialog.getVname(), dialog.getValue());
        }
    }
}

class VariableDialog extends JDialog {

    JPanel contentPanel = new JPanel();
    JPanel buttonPanel = new JPanel(new FlowLayout());
    private boolean ok = false;
    private String vname = null;
    private Object value = null;
    JComboBox typeComboBox = null;
    JTextField nameTextField = null;
    JTextField valueTextField = null;

    public VariableDialog() {
        this.setModal(true);
        init();
    }

    protected void init() {
        this.getContentPane().setLayout(new BorderLayout());

        GridBagLayout theGridBagLayout = new GridBagLayout();
        contentPanel.setLayout(theGridBagLayout);

        GridBagConstraints theGbc = new GridBagConstraints();
        theGbc.anchor = GridBagConstraints.WEST;
        theGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel typeLabel = new JLabel("Type");
        theGbc.gridx = 0;
        theGbc.gridy = 0;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(typeLabel, theGbc);
        contentPanel.add(typeLabel);

        String[] types = {"String", "Integer", "Boolean", "Float", "Date"};
        typeComboBox = new JComboBox(types);
        theGbc.gridx = 1;
        theGbc.gridy = 0;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(typeComboBox, theGbc);
        contentPanel.add(typeComboBox);

        JLabel nameLabel = new JLabel("Name");
        theGbc.gridx = 0;
        theGbc.gridy = 1;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(nameLabel, theGbc);
        contentPanel.add(nameLabel);

        nameTextField = new JTextField();
        theGbc.gridx = 1;
        theGbc.gridy = 1;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(nameTextField, theGbc);
        contentPanel.add(nameTextField);

        JLabel valueLabel = new JLabel("Value");
        theGbc.gridx = 0;
        theGbc.gridy = 2;
        theGbc.weightx = 0;
        theGridBagLayout.setConstraints(valueLabel, theGbc);
        contentPanel.add(valueLabel);

        valueTextField = new JTextField();
        theGbc.gridx = 1;
        theGbc.gridy = 2;
        theGbc.weightx = 1;
        theGridBagLayout.setConstraints(valueTextField, theGbc);
        contentPanel.add(valueTextField);

        JButton okButton = new JButton("OK");
        buttonPanel.add(okButton);

        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                vname = nameTextField.getText();
                String type = (String) typeComboBox.getSelectedItem();

                if (type.equals("String")) {
                    value = valueTextField.getText();
                } else if (type.equals("Integer")) {
                    try {
                        value = Integer.parseInt(valueTextField.getText());
                    } catch (Exception ex) {

                    }
                } else if (type.equals("Float")) {
                    try {
                        value = Float.parseFloat(valueTextField.getText());
                    } catch (Exception ex) {

                    }
                } else if (type.equals("Boolean")) {
                    try {
                        value = Boolean.parseBoolean(valueTextField.getText());
                    } catch (Exception ex) {

                    }
                } else if (type.equals("Date")) {
                    try {
                        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        value = dFormat.parse(valueTextField.getText());
                    } catch (Exception ex) {

                    }
                }
                ok = true;
                dispose();
            }
        });

        this.getContentPane().add(contentPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    public String getVname() {
        return vname;
    }

    public Object getValue() {
        return value;
    }

    public boolean isOk() {
        return this.ok;
    }
}
