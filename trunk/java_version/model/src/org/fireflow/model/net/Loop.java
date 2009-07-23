/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.model.net;


import org.fireflow.model.WorkflowProcess;


/**
 * 循环。
 * @author 非也
 * @version 1.0
 * Created on Mar 15, 2009
 */
public class Loop extends Edge{
//    /**
//     * 循环类型之一：重做。<br>
//     * 工作流引擎将循环产生的TaskInstance分配给上一次完成该TaskInstance的操作员。
//     */
//    public static final String REDO = "REDO";
//
//    /**
//     * 循环类型之二：逐级审批。<br>
//     * 工作流引擎将循环产生的TaskInstance分配给上一级角色
//     */
//    public static final String UPGRADE = "UPGRADE";
//
//    /**
//     * 循环类型之三：NONE<br>
//     * 工作流引擎按照FormTask的Performer属性分配工单。
//     */
//    public static final String NONE = "NONE";
//
//
//    /**
//     * 循环类型
//     */
//    String loopType = REDO;
//
//    /**
//     * 按照从低到高级别组织的参与者列表。
//     * 该列表不包含Task第一次执行的参与者。
//     * 当loopType=UPGRADE时，引擎按照这个参与者列表的顺序给每次循环分配工单
//     */
//    List<Participant> performersList = new ArrayList<Participant>();
    public Loop() {
    }

    public Loop(WorkflowProcess workflowProcess, String name, Synchronizer fromNode, Synchronizer toNode) {
        super(workflowProcess, name);
        this.fromNode = fromNode;
        this.toNode = toNode;
    }


//    public String getLoopType() {
//        return loopType;
//    }
//
//    public void setLoopType(String loopType) {
//        this.loopType = loopType;
//    }
//
//    public List<Participant> getPerformersList() {
//        return performersList;
//    }
    
}
