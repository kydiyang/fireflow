/* Copyright 2009 无忧lwz0721@gmail.com
 * @author 无忧lwz0721@gmail.com
 */
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.OracleClient;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Engine.Definition;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Kernel.Impl;
using FireWorkflow.Net.Engine.Persistence;
using FireWorkflow.Net.Model;

namespace FireWorkflow.Net.Persistence.OracleDAL
{
    public class OracleDataReaderToInfo
    {
        /// <summary>
        /// 返回ProcessInstance，共14个字段
        /// </summary>
        /// <param name="dr"></param>
        /// <returns></returns>
        public static ProcessInstance GetProcessInstance(IDataReader dr)
        {
            ProcessInstance processInstance = new ProcessInstance();

            processInstance.setId(Convert.ToString(dr["id"]));
            processInstance.setProcessId(Convert.ToString(dr["process_id"])); 
            processInstance.setVersion(Convert.ToInt32(dr["version"])); 
            processInstance.setName(Convert.ToString(dr["name"])); 
            processInstance.setDisplayName(Convert.ToString(dr["display_name"])); 

            processInstance.setState(Convert.ToInt32(dr["state"])); 
            processInstance.setSuspended(Convert.ToInt32(dr["suspended"]) == 1); 
            processInstance.setCreatorId(Convert.ToString(dr["creator_id"])); 
            if (!(dr["created_time"] is DBNull)) processInstance.setCreatedTime(Convert.ToDateTime(dr["created_time"])); 
            if (!(dr["started_time"] is DBNull)) processInstance.setStartedTime(Convert.ToDateTime(dr["started_time"])); 

            if (!(dr["expired_time"] is DBNull)) processInstance.setExpiredTime(Convert.ToDateTime(dr["expired_time"])); 
            if (!(dr["end_time"] is DBNull)) processInstance.setEndTime(Convert.ToDateTime(dr["end_time"])); 

            processInstance.setParentProcessInstanceId(Convert.ToString(dr["parent_processinstance_id"]));
            processInstance.setParentTaskInstanceId(Convert.ToString(dr["parent_taskinstance_id"]));

            return processInstance;
        }

        /// <summary>
        /// 返回taskinstance (共21个字段)
        /// </summary>
        /// <param name="rs"></param>
        /// <param name="rowNum"></param>
        /// <returns></returns>
        public static TaskInstance GetTaskInstance(IDataReader dr)
        {
            TaskInstance taskInstance = new TaskInstance();
            taskInstance.setId(Convert.ToString(dr["id"]));
            // 20090922 wmj2003 没有给biz_type赋值 是否需要给基于jdbc的数据增加 setBizType()方法？
            taskInstance.setTaskId(Convert.ToString(dr["task_id"]));
            taskInstance.setActivityId(Convert.ToString(dr["activity_id"]));
            taskInstance.setName(Convert.ToString(dr["name"]));

            taskInstance.setDisplayName(Convert.ToString(dr["display_name"]));
            taskInstance.State=(TaskInstanceStateEnum)Convert.ToInt32(dr["state"]);
            taskInstance.setSuspended(Convert.ToInt32(dr["suspended"]) == 1 ? true : false);
            taskInstance.setTaskType((TaskTypeEnum)Enum.Parse(typeof(TaskTypeEnum),Convert.ToString(dr["task_type"])));
            if (!(dr["created_time"] is DBNull)) taskInstance.setCreatedTime(Convert.ToDateTime(dr["created_time"]));

            if (!(dr["started_time"] is DBNull)) taskInstance.setStartedTime(Convert.ToDateTime(dr["started_time"]));
            if (!(dr["end_time"] is DBNull)) taskInstance.setEndTime(Convert.ToDateTime(dr["end_time"]));
            taskInstance.setAssignmentStrategy((FormTaskEnum)Enum.Parse(typeof(FormTaskEnum), Convert.ToString((dr["assignment_strategy"]))));
            taskInstance.setProcessInstanceId(Convert.ToString(dr["processinstance_id"]));
            taskInstance.setProcessId(Convert.ToString(dr["process_id"]));

            taskInstance.setVersion(Convert.ToInt32(dr["version"]));
            taskInstance.setTargetActivityId(Convert.ToString(dr["target_activity_id"]));
            taskInstance.setFromActivityId(Convert.ToString(dr["from_activity_id"]));
            taskInstance.setStepNumber(Convert.ToInt32(dr["step_number"]));
            taskInstance.setCanBeWithdrawn(Convert.ToInt32(dr["can_be_withdrawn"]) == 1 ? true : false);

            return taskInstance;
        }


        /// <summary>
        /// 返回WorkItem 共8个字段
        /// </summary>
        public static WorkItem GetWorkItem(IDataReader dr)
        {
            WorkItem workItem = new WorkItem();
            workItem.setId(Convert.ToString(dr["id"]));
            workItem.setState(Convert.ToInt32(dr["state"]));
            if (!(dr["created_time"] is DBNull)) workItem.setCreatedTime(Convert.ToDateTime(dr["created_time"]));
            if (!(dr["claimed_time"] is DBNull)) workItem.setClaimedTime(Convert.ToDateTime(dr["claimed_time"]));
            if (!(dr["end_time"] is DBNull)) workItem.setEndTime(Convert.ToDateTime(dr["end_time"]));
            workItem.setActorId(Convert.ToString(dr["actor_id"]));
            workItem.setTaskInstanceId(Convert.ToString(dr["taskinstance_id"]));
            workItem.setComments(Convert.ToString(dr["comments"]));
            return workItem;
        }

        public static Token GetToken(IDataReader dr)
        {
            Token token = new Token();
            token.Id=Convert.ToString(dr["id"]);
            token.IsAlive=Convert.ToInt32(dr["alive"]) == 1 ? true : false;
            token.Value=Convert.ToInt32(dr["value"]);
            token.NodeId=Convert.ToString(dr["node_id"]);
            token.ProcessInstanceId=Convert.ToString(dr["processinstance_id"]);
            token.StepNumber=Convert.ToInt32(dr["step_number"]);
            token.FromActivityId=Convert.ToString(dr["from_activity_id"]);

            return token;
        }

        public static WorkflowDefinition GetWorkflowDefinition(IDataReader dr)
        {
            WorkflowDefinition workFlowDefinition = new WorkflowDefinition();
            workFlowDefinition.Id=Convert.ToString(dr["id"]);
            workFlowDefinition.DefinitionType=Convert.ToString(dr["definition_type"]);
            workFlowDefinition.ProcessId=Convert.ToString(dr["process_id"]);
            workFlowDefinition.Name=Convert.ToString(dr["name"]);
            workFlowDefinition.DisplayName=Convert.ToString(dr["display_name"]);

            workFlowDefinition.Description=Convert.ToString(dr["description"]);
            workFlowDefinition.Version=Convert.ToInt32(dr["version"]);
            workFlowDefinition.State=Convert.ToInt32(dr["state"]) == 1 ? true : false;
            workFlowDefinition.UploadUser=Convert.ToString(dr["upload_user"]);
            if (!(dr["upload_time"] is DBNull)) workFlowDefinition.UploadTime=Convert.ToDateTime(dr["upload_time"]);

            workFlowDefinition.PublishUser=Convert.ToString(dr["publish_user"]);
            if (!(dr["publish_time"] is DBNull)) workFlowDefinition.PublishTime=Convert.ToDateTime(dr["publish_time"]);
            // 读取blob大字段
            workFlowDefinition.ProcessContent=Convert.ToString(dr["process_content"]);
            return workFlowDefinition;
        }

        public static ProcessInstanceTrace GetProcessInstanceTrace(IDataReader dr)
        {
                 ProcessInstanceTrace processInstanceTrace = new ProcessInstanceTrace(); 
  
                 processInstanceTrace.setId(Convert.ToString(dr["id"]));
                 processInstanceTrace.setProcessInstanceId(Convert.ToString(dr["processinstance_id"]));
                 processInstanceTrace.setStepNumber(Convert.ToInt32(dr["step_number"]));
                 processInstanceTrace.setMinorNumber(Convert.ToInt32(dr["minor_number"]));
                 processInstanceTrace.setType(Convert.ToString(dr["type"]));
  
                 processInstanceTrace.setEdgeId(Convert.ToString(dr["edge_id"]));
                 processInstanceTrace.setFromNodeId(Convert.ToString(dr["from_node_id"]));
                 processInstanceTrace.setToNodeId(Convert.ToString(dr["to_node_id"]));
  
                 return processInstanceTrace; 
  
         } 
 
    }
}
