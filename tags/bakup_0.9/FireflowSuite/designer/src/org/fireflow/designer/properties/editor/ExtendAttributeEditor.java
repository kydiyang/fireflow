package org.fireflow.designer.properties.editor;

import cn.bestsolution.tools.resourcesmanager.util.Utilities;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.IWFElement;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ExtendAttributeEditor
      extends JPanel {
   private IWFElement element;
   JTable jTable1 = null;
   DefaultTableModel model;
   FPDLDataObject fpdlDataObject = null;

   public ExtendAttributeEditor(IWFElement element,
                                FPDLDataObject fpdlDataObj) {
      this.element = element;
      fpdlDataObject = fpdlDataObj;
      try {
         jbInit();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void jbInit() throws Exception {

      BorderLayout layout = new BorderLayout();
      this.setLayout(layout);

      Vector<String> vColumnName = new Vector<String>(2);
      vColumnName.add("Extend Attribute Name");
      vColumnName.add("Extend Attribute Value");

      Map attrs = element.getExtendedAttributes();
      Iterator it = attrs.keySet().iterator();
      Vector<Object> vdata = new Vector<Object>();
      while (it.hasNext()) {
         Object key = it.next();
         if ( ( (String) key).startsWith("BSFLW.")) {
            continue;
         }
         Object value = attrs.get(key);
         Vector<Object> v = new Vector<Object>();
         v.add(key);
         v.add(value);
         vdata.add(v);
      }

      model = new DefaultTableModel(vdata, vColumnName);
      jTable1 = new MyTable(model,
                            element.getExtendedAttributes());
      jTable1.getTableHeader().setFont(Utilities.
                                       windowsUIFont);
      JScrollPane scrollPane = new JScrollPane(jTable1);
      jTable1.setPreferredScrollableViewportSize(new
                                                 Dimension(
            500, 70));
      this.add(scrollPane, BorderLayout.CENTER);

      JPanel panel = new JPanel();

      JButton addButton = new JButton("Add");
      addButton.setFont(Utilities.windowsUIFont);
      addButton.addActionListener(new
                                  AddButton_actionAdapter(this));
      panel.add(addButton);

      JButton delButton = new JButton("Delete");
      delButton.setFont(Utilities.windowsUIFont);
      panel.add(delButton);
      delButton.addActionListener(new
                                  DelButton_actionAdapter(this));

      this.add(panel, BorderLayout.SOUTH);

   }

   void addButton_actionPerformed(ActionEvent e) {
      String[] content = new String[2];
      int i = model.getRowCount();
      while (element.getExtendedAttributes().containsKey(
            "attribute" + i)) {
         i++;
      }
      content[0] = "attribute" + i;
      content[1] = "value" + i;
      model.insertRow(model.getRowCount(), content);
      element.getExtendedAttributes().put(content[0],
                                          content[1]);
      fpdlDataObject.setModified(true);
   }

   void delButton_actionPerformed(ActionEvent e) {
      int row = jTable1.getSelectedRow();
      if (row == -1) {
         return;
      }
      String key = model.getValueAt(row, 0).toString();
      model.removeRow(row);
      element.getExtendedAttributes().remove(key);
      fpdlDataObject.setModified(true);
   }

}

class AddButton_actionAdapter
      implements java.awt.event.ActionListener {
   ExtendAttributeEditor adaptee;

   AddButton_actionAdapter(ExtendAttributeEditor adaptee) {
      this.adaptee = adaptee;
   }

   public void actionPerformed(ActionEvent e) {
      adaptee.addButton_actionPerformed(e);
   }
}

class DelButton_actionAdapter
      implements java.awt.event.ActionListener {
   ExtendAttributeEditor adaptee;

   DelButton_actionAdapter(ExtendAttributeEditor adaptee) {
      this.adaptee = adaptee;
   }

   public void actionPerformed(ActionEvent e) {
      adaptee.delButton_actionPerformed(e);
   }
}

class MyTable
      extends JTable {
   Map<String,String> xAttributes;
   String oldKey = "";
   public MyTable(DefaultTableModel model,
                  Map<String,String> argxAttributes) {
      super(model);
      xAttributes = argxAttributes;
   }

   @Override
   public void tableChanged(TableModelEvent e) {

      super.tableChanged(e);
      int row = e.getFirstRow();
      int column = this.getSelectedColumn();
      if (e.getType() == TableModelEvent.UPDATE && row >= 0 &&
          row < this.getRowCount() && column >= 0 &&
          column < this.getColumnCount()) {
         String key = (String)this.getValueAt(row, 0);
         String value = (String)this.getValueAt(row, 1);
         xAttributes.remove(oldKey);
         xAttributes.put(key, value);
      }
   }

   public void editingStopped(ChangeEvent e) {
      int row = this.getSelectedRow();
      int column = this.getSelectedColumn();
      if (row >= 0 && row < this.getRowCount() &&
          column >= 0 && column < this.getColumnCount()) {
         oldKey = (String)this.getValueAt(row, 0);
      }
      super.editingStopped(e);
   }
}