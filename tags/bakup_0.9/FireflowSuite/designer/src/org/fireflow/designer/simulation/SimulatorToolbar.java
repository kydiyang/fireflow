/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.simulation;

import cn.bestsolution.tools.resourcesmanager.util.ImageLoader;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.fireflow.designer.simulation.action.AcceptWorkitemAction;
import org.fireflow.designer.simulation.action.ClearAll;
import org.fireflow.designer.simulation.action.CompleteWorkitemAction;
import org.fireflow.designer.simulation.action.SetWorkflowVariableAction;
import org.fireflow.designer.simulation.action.StartWorkflowProcessAction;
import org.fireflow.designer.simulation.action.StopAction;

/**
 *
 * @author chennieyun
 */
public class SimulatorToolbar extends JToolBar {
    private static Insets MARGIN = new Insets(4, 4, 4, 4);
    FireflowSimulator fireflowSimulator = null;

    public SimulatorToolbar(FireflowSimulator fireflowSimulator) {
        this.fireflowSimulator = fireflowSimulator;
        init();
    }

    private void init() {
        this.addSeparator();

        AbstractAction action = new StartWorkflowProcessAction(
                this.fireflowSimulator, "Run", ImageLoader.getImageIcon("run_step_by_step_16.gif"));
        JButton button = this.add(action);
        button.setToolTipText("单步调试工作流");
        button.setMargin(MARGIN);


//        action = new AutoRunWorkflowProcessAction(
//                this.fireflowSimulator, "AutoRun", ImageLoader.getImageIcon("run_auto_16.gif"));
//        button = this.add(action);
//        button.setToolTipText("自动测试工作流");
//        button.setMargin(MARGIN);


        action = new AcceptWorkitemAction(
                this.fireflowSimulator, "Accept Workitem", ImageLoader.getImageIcon("accept_workitem_16.gif"));
        button = this.add(action);
        button.setToolTipText("签收Workitem");
        button.setMargin(MARGIN);

        action = new CompleteWorkitemAction(
                this.fireflowSimulator, "Complete Workitem", ImageLoader.getImageIcon("complete_workitem_16.gif"));
        button = this.add(action);
        button.setToolTipText("完成Workitem");
        button.setMargin(MARGIN);

//        action = new JumptoAction(
//                this.fireflowSimulator, "Jump to", ImageLoader.getImageIcon("jump_16.gif"));
//        button = this.add(action);
//        button.setToolTipText("跳转");
//        button.setMargin(MARGIN);

        action = new SetWorkflowVariableAction(
                this.fireflowSimulator, "Set varialbe", ImageLoader.getImageIcon("setvariable_16.gif"));
        button = this.add(action);
        button.setToolTipText("设置变量");
        button.setMargin(MARGIN);
        
        

//        action = new SetBreakPointAction(
//                this.fireflowSimulator, "Set Breakpoint", ImageLoader.getImageIcon("break_point_16.gif"));
//        button = this.add(action);
//        button.setToolTipText("设置断点");    
//        button.setMargin(MARGIN);
        
        //停止
        action = new StopAction(this.fireflowSimulator,"Stop",ImageLoader.getImageIcon("stop_16.gif"));
        button = this.add(action);
        button.setToolTipText("停止");
        button.setMargin(MARGIN);
        
        //清除所有测试数据
        action = new ClearAll(this.fireflowSimulator,"Clear All",ImageLoader.getImageIcon("clear_16.gif"));
        button = this.add(action);
        button.setToolTipText("清除所有测试数据");
        button.setMargin(MARGIN);
        
        JComboBox comboBox = new JComboBox(new SimulatorPanelComboBoxModel(fireflowSimulator.getSimulatorPanelList()));
        JPanel panel = new JPanel();
        comboBox.setMaximumSize(new Dimension(150, 24));
        comboBox.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
               JComboBox theComboBox = (JComboBox)e.getSource();
               fireflowSimulator.setCurrentSimulatorPanel((SimulatorPanel)theComboBox.getSelectedItem());
            }
        });
        this.add(comboBox);
    }
}

class SimulatorPanelComboBoxModel extends DefaultComboBoxModel{
    List simulatorPanelList  =null;
    public SimulatorPanelComboBoxModel(List panelList){
        this.simulatorPanelList = panelList;
    }
    
    @Override
    public int getSize(){
        return simulatorPanelList==null?0:simulatorPanelList.size();
    }
    
    @Override
    public Object getElementAt(int idx){
        return simulatorPanelList==null?null:simulatorPanelList.get(idx);
    }
}
