package cn.bestsolution.tools.resourcesmanager.util;

import java.util.*;

public class MainFrameI18N {
   private static final ResourceBundle resource = ResourceBundle.getBundle(
         "cn.bestsolution.tools.resourcesmanager.resources.MainFrameResources",
         new Locale("zh", "CN"));

   public MainFrameI18N() {
   }

   public static String getString(String key){
      String value = key;
      try{
         return resource.getString(key);
      }catch(Exception ex){
         return value;
      }

   }
}