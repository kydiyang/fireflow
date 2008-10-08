package org.fireflow.designer.editpanel.graph;

import org.jgraph.graph.DefaultGraphCell;

import org.jgraph.graph.Port;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.DefaultGraphCell;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ActivityCell extends DefaultGraphCell {

   public ActivityCell(Object userObject){
      super(userObject);
      port = new DefaultPort();
      add(port);
   }


   public Port getPort(){
      return port;
   }
   private DefaultPort port;
}