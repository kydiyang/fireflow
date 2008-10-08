package org.fireflow.designer.editpanel.graph.actions;

import javax.swing.Action;

public interface IDelegateActionHandler {
   public static int DELETE_ACTION = 0;
   public static int COPY_ACTION = 1;
   public static int PASTE_ACTION = 2;
   public static int CUT_ACTION = 3;
   public static int UNDO_ACTION = 4;
   public static int REDO_ACTION = 5;

   public Action getAction(int actionType);
}