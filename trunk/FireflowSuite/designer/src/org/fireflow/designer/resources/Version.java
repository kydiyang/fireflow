package org.fireflow.designer.resources;

import java.io.*;
import java.util.*;

/**
 * Reads the Version from the file version.properies
 *
 * @author sven.luzar
 *
 */
public class Version {
   /** The Resource Bundle handles the version file
    */
   private static ResourceBundle versionResourceBundle;

   /** The currentPoint version number
    */
   private static String version = "";

   /** Initializer loads the ResourceBundle and sets the const value VERSION
    *
    */
   static {
      try {
         InputStream is =
               Version.class.getResourceAsStream("version.properties");
         if (is != null) {
            versionResourceBundle = new PropertyResourceBundle(is);
//				String rc =
//					versionResourceBundle.getString("version.number.rc");
//				if (rc != null) {
//					if (rc.equals("0") || rc.equals("")) {
//						rc = "";
//					} else {
//						if (rc.equals("999")) {
//							rc = " Final" ;
//						} else {
//							rc = "-RC" + rc;
//						}
//					}
//				} else {
//					rc = "";
//				}
            version =
                  versionResourceBundle.getString("version.number.1")
                  + "."
                  + versionResourceBundle.getString("version.number.2")
                  + "."
                  + versionResourceBundle.getString("version.number.3");
         }
      }
      catch (java.io.IOException e) {
         System.err.println(e.getMessage());
         version = "{File version.properties not available.}";
      }
   }

   /** Returns the currentPoint version number
    */
   public static String getVersion() {
      return version;
   }
}
