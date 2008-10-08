/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.editpanel.graph;

import java.util.Date;
import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

/**
 *
 * @author chennieyun
 */
public class MyTestNode extends AbstractNode{
    public MyTestNode(Children children){
        super(children);
    }
protected Sheet createSheet() {

    Sheet sheet = Sheet.createDefault();
    Sheet.Set set = Sheet.createPropertiesSet();
//    APIObject obj = getLookup().lookup(APIObject.class);
//
//    try {
//
//        Property indexProp = new PropertySupport.Reflection(obj, Integer.class, "getIndex", null);
//        Property dateProp = new PropertySupport.Reflection(obj, Date.class, "getDate", null);
//
//        indexProp.setName("index");
//        dateProp.setName("date");
//
//        set.put(indexProp);
//        set.put(dateProp);
//
//    } catch (NoSuchMethodException ex) {
//        ErrorManager.getDefault();
//    }
//
//    sheet.put(set);
    return sheet;

}    
}
