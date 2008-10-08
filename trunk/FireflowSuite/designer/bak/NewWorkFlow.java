package org.fireflow.designer.editpanel.graph.actions;

import javax.swing.ImageIcon;

import org.fireflow.designer.datamodel.IFPDLElement;
//import org.fireflow.designer.outline.FPDLOutlineTree;

import cn.bestsolution.tools.resourcesmanager.actions.*;
import java.awt.event.*;
import cn.bestsolution.tools.resourcesmanager.*;
import javax.swing.AbstractAction;

public class NewWorkFlow extends AbstractAction {

   public NewWorkFlow() {
   }

   public NewWorkFlow(String argTtitle) {
      super(argTtitle);
   }

   public NewWorkFlow(String argTtitle, ImageIcon icon) {
      super(argTtitle, icon);
   }
   public void actionPerformed(ActionEvent evt) {
//      Object obj = argMainMediator.getOutlinePane().getOutlineTree();
//      if (obj==null || !( obj instanceof FPDLOutlineTree))return ;
//      FPDLOutlineTree tree = (FPDLOutlineTree)obj;
//      IFPDLElement node = tree.getSelectedFPDLElement();
//      if (node.getElementName().equals(IFPDLElement.WORKFLOWPROCESSES)){
//    	  IFPDLElement newNode = node.createChild(IFPDLElement.WORKFLOWPROCESS,null);
//      }
   }

   /*
   public void update(MainMediator argMainMediator, Object source){
      if (source==null){//null��ʾˢ��action
         this.setEnabled(false);
         return;
      }
      if (source!=null && source instanceof IFPDLElement){
    	  IFPDLElement fpdlElement = (IFPDLElement)source;
         if (fpdlElement.getName().equals(IFPDLElement.WORKFLOWPROCESSES)) {
            this.setEnabled(true);
         }
         else {
            this.setEnabled(false);
         }
      }
   }
*/
}