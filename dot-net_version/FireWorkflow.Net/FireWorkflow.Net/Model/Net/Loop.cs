using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Net
{
    /// <summary>循环。</summary>
    public class Loop : Edge
    {
        //    /**
        //     * 循环类型之一：重做。
        //     * 工作流引擎将循环产生的TaskInstance分配给上一次完成该TaskInstance的操作员。
        //     */
        //    public const String REDO = "REDO";
        //
        //    /**
        //     * 循环类型之二：逐级审批。
        //     * 工作流引擎将循环产生的TaskInstance分配给上一级角色
        //     */
        //    public const String UPGRADE = "UPGRADE";
        //
        //    /**
        //     * 循环类型之三：NONE
        //     * 工作流引擎按照FormTask的Performer属性分配工单。
        //     */
        //    public const String NONE = "NONE";
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
        public Loop()
        {
        }

        public Loop(WorkflowProcess workflowProcess, String name, Synchronizer fromNode, Synchronizer toNode)
            :base(workflowProcess, name)
        {
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
}
