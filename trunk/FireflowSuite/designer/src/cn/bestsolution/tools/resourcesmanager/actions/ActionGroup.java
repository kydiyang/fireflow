package cn.bestsolution.tools.resourcesmanager.actions;

import javax.swing.ImageIcon;
import javax.swing.Action;
import java.util.List;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.AbstractAction;


public class ActionGroup extends AbstractAction {
   private List<Action> childActions = new ArrayList<Action>();
   private boolean needButtonGroup = false;

   public ActionGroup() {
   }

   public ActionGroup(String title){
      super(title);
   }

   public ActionGroup(String title, ImageIcon icon){
      super(title,icon);
   }

   public List getChildActions(){
      return childActions;
   }

   public void addAction(Action act){
      childActions.add(act);
   }

   public void addAction(int index, Action act){
      childActions.add(index,act);
   }

   public void setAction(int index,Action act){
      childActions.set(index,act);
   }

   public void removeAction(int index){
      if (index>=0 && index<childActions.size()){
         childActions.remove(index);
      }
   }

   public Action getAction(int index){
      return childActions.get(index);
   }

   public void actionPerformed(ActionEvent evt){

   }


   public void setNeedButtonGroup(){
      needButtonGroup = true;
   }

   public boolean needButtonGroup(){
      return needButtonGroup;
   }
}