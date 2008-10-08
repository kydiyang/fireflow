package org.fireflow.designer.properties.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.bestsolution.tools.resourcesmanager.util.Utilities;
import java.awt.event.ActionListener;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.IWFElement;
import org.fireflow.model.WorkflowProcess;
import org.openide.nodes.AbstractNode;

public class EntityEditor
        extends JPanel implements KeyListener, ActionListener {
    protected AbstractNode theNode = null;
    protected IWFElement wfElement;
    protected FPDLDataObject fpdlDataObject = null;
    protected GridBagConstraints gbc = new GridBagConstraints();
    protected GridBagLayout gridbagLayout = new GridBagLayout();
    private DefaultMutableTreeNode parentNode;
    private JLabel idLabel;
    private JTextField idField;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel displayNameLabel;
    private JTextField displayNameField;
    private JLabel descriptionLabel;
    private JTextArea descriptionArea;
    protected int rows = 0;
//   private String entityTypeName = "";
//   private boolean idCanbeModified = false;
    public EntityEditor(AbstractNode node ,IWFElement element, FPDLDataObject fpdlDataObj) {
        this.theNode = node;
        //init();
        wfElement = element;
        fpdlDataObject = fpdlDataObj;
//      setElement(element);
    }

    public IWFElement getElement() {
        return wfElement;
    }

    public void setElement(IWFElement element) {
        this.wfElement = element;

    }

    public String getId() {
        return idField.getText();
    }

    public String getName() {
        return nameField.getText();
    }

    public String getDescription() {
        return descriptionArea.getText();
    }

    /*
    public Map getExtendedAttributes(Map extendedAttributes){
    }
     */

//   public boolean save() {
//      if (element != null) {
//         element.setName(nameField.getText());
//         element.setDescription(descriptionArea.getText());
//         //element.setExtendedAttributes();
//      }
//      return true;
//   }
    protected void revert() {
        if (wfElement != null) {
            idField.setText(wfElement.getId());
            nameField.setText(wfElement.getName());
            this.displayNameField.setText(wfElement.getDisplayName());
            descriptionArea.setText(wfElement.getDescription());

        }
    }

    protected void elementValueChanged(Object sourceObj) {
        if (sourceObj == nameField) {
            String oldValue = wfElement.getName();
            if (oldValue == null ||
                    !oldValue.equals(nameField.getText())) {
                wfElement.setName(nameField.getText());
                this.idField.setText(wfElement.getId());
            }
        }

        if (sourceObj == this.displayNameField) {
            String oldValue = wfElement.getDisplayName();
            if (oldValue == null || !oldValue.equals(this.displayNameField.getText())) {
                wfElement.setDisplayName(this.displayNameField.getText());
            }
        } else if (sourceObj == descriptionArea) {
            String oldValue = wfElement.getDescription();
            if (oldValue == null ||
                    !oldValue.equals(descriptionArea.getText())) {
                wfElement.setDescription(descriptionArea.getText());

            }
        }
        fpdlDataObject.modelUpdatedFromUI();
        fpdlDataObject.setModified(true);
        theNode.setDisplayName(this.wfElement.toString());
        theNode.setName(this.wfElement.getName());
    }

    protected void init() {
        setLayout(gridbagLayout);

//      EntityEditorKeyListener keyListener = new
//            EntityEditorKeyListener(this);
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;


        idLabel = new JLabel("ID");
        idLabel.setFont(Utilities.windowsUIFont);
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = rows;
        gridbagLayout.setConstraints(idLabel, gbc);
        add(idLabel);

        idField = new JTextField();
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 1;
        gbc.gridy = rows;
        gridbagLayout.setConstraints(idField, gbc);
        add(idField);
        idField.setEditable(false);

        rows++;


        nameLabel = new JLabel("Name");
        nameLabel.setFont(Utilities.windowsUIFont);
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = rows;
        gridbagLayout.setConstraints(nameLabel, gbc);
        add(nameLabel);

        nameField = new JTextField();
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 1;
        gbc.gridy = rows;
        gridbagLayout.setConstraints(nameField, gbc);
        add(nameField);


        rows++;

        displayNameLabel = new JLabel("Display Name");
        displayNameLabel.setFont(Utilities.windowsUIFont);
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = rows;
        gridbagLayout.setConstraints(displayNameLabel, gbc);
        add(displayNameLabel);

        displayNameField = new JTextField();
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 1;
        gbc.gridy = rows;
        gridbagLayout.setConstraints(displayNameField, gbc);
        add(displayNameField);


        rows++;

        descriptionLabel = new JLabel("Description");
        descriptionLabel.setFont(Utilities.windowsUIFont);
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gridbagLayout.setConstraints(descriptionLabel, gbc);
        add(descriptionLabel);

        descriptionArea = new JTextArea();
        descriptionArea.setRows(4);
        descriptionArea.setColumns(32);
        JScrollPane sp = new JScrollPane(descriptionArea);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;

        gridbagLayout.setConstraints(sp, gbc);
        add(sp);

        rows++;

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    }

    protected void addAllEventListeners() {
        idField.addKeyListener(this);
        nameField.addKeyListener(this);
        displayNameField.addKeyListener(this);
        descriptionArea.addKeyListener(this);
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {
    }

    public void keyReleased(KeyEvent keyEvent) {
        this.elementValueChanged(keyEvent.getSource());
    }

    public void actionPerformed(ActionEvent e) {
        this.elementValueChanged(e.getSource());
    }
}

//class EntityEditorKeyListener
//      implements KeyListener {
//   EntityEditor adaptee;
//
//}

