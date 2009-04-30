package org.fireflow.designer.util;

import java.util.*;

public class WorkFlowDesignerI18N {
   public WorkFlowDesignerI18N() {
   }
   private static final ResourceBundle resource = ResourceBundle.getBundle(
         "cn.bestsolution.tools.resourcesmanager.resources.ApplicationResources",
         new Locale("zh", "CN"));


   public static String getString(String key){
      String value = key;
      try{
         return resource.getString(key);
      }catch(Exception ex){
         return value;
      }

   }
}
