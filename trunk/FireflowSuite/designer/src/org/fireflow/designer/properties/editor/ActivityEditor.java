/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.properties.editor;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.net.Activity;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author chennieyun
 */
public class ActivityEditor extends EntityEditor{

    private JLabel completeStrategyLabel = null;
    private JComboBox completeStrategy  = null;
    public ActivityEditor(AbstractNode node ,Activity activity,FPDLDataObject fpdlDataObj){
        super(node,activity,fpdlDataObj);
        theNode = node;
        init();
    }
    
    @Override
    protected void init(){
        super.init();
        
        completeStrategyLabel = new JLabel("Complete Strategy");
        gbc.gridx = 0;
        gbc.gridy = rows;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gridbagLayout.setConstraints(completeStrategyLabel, gbc);
        this.add(completeStrategyLabel);
        
        String[] completeStrategyItems = new String[]{Activity.ALL,Activity.ANY};
        completeStrategy = new JComboBox(completeStrategyItems);
        gbc.gridx = 1;
        gbc.gridy = rows;
        gbc.weightx = gbc.REMAINDER;
        gbc.gridwidth = 1;
        gridbagLayout.setConstraints(completeStrategy, gbc);
        this.add(completeStrategy);        
        
        rows++;
        
        
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
        
        //赋值
        revert();
        
        //链接事件监听器
        this.addAllEventListeners();
    }
    
    @Override
    protected void revert(){
        super.revert();
        
        Activity activity = (Activity)this.wfElement;
        if (activity.getCompletionStrategy()==null){
            activity.setCompletionStrategy(Activity.ALL);
        }
        if (activity.getCompletionStrategy().equals(Activity.ALL)){
            completeStrategy.setSelectedIndex(0);
        }else{
            completeStrategy.setSelectedIndex(1);
        }
    }
    
    @Override
    protected void elementValueChanged(Object sourceObj){
        super.elementValueChanged(sourceObj);
        
        if (sourceObj==completeStrategy){
            Activity activity = (Activity)this.wfElement;
            activity.setCompletionStrategy((String)completeStrategy.getSelectedItem());
        }
    }
    
    @Override
    protected void addAllEventListeners() {
        super.addAllEventListeners();
        
        completeStrategy.addActionListener(this);
    }
}
