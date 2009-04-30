/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.properties.editor;

import javax.swing.JPanel;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.net.EndNode;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author chennieyun
 */
public class EndNodeEditor extends EntityEditor{
    public EndNodeEditor(AbstractNode node,EndNode endnode,FPDLDataObject fpdlDataObj){
        super(node,endnode,fpdlDataObj);
        init();
    }
    
    @Override
    protected void init(){
        super.init();
        
        gbc.anchor = gbc.NORTHWEST;
        gbc.fill = gbc.BOTH;
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 2;
        JPanel fulfilPanel = new JPanel();
        gridbagLayout.setConstraints(fulfilPanel, gbc);
        this.add(fulfilPanel);        
        
        this.revert();
        
        this.addAllEventListeners();
    }

}
