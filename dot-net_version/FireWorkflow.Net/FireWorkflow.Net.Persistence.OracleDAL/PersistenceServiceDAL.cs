using System;
using System.Collections.Generic;
using System.Text;
using System.Data;
using System.Data.OracleClient;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Engine.Definition;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Engine.Persistence;

namespace FireWorkflow.Net.Persistence.OracleDAL
{
    public class PersistenceServiceDAL : IPersistenceService
    {
        string connectionString = "User Id=ISS;Password=webiss;Data Source=ism";
        #region IRuntimeContextAware 成员
        protected RuntimeContext rtCtx = null;
        public void setRuntimeContext(RuntimeContext ctx)
        {
            this.rtCtx = ctx;
        }

        public RuntimeContext getRuntimeContext()
        {
            return this.rtCtx;
        }

        #endregion

        /******************************************************************************/
        /************                                                        **********/
        /************            Process instance 相关的持久化方法            **********/
        /************            Persistence methods for process instance    **********/
        /************                                                        **********/
        /******************************************************************************/
        //插入或者更新ProcessInstance流程实例
        public bool saveOrUpdateProcessInstance(IProcessInstance processInstance)
        {
            
            if (String.IsNullOrEmpty(processInstance.getId()))
            {
                String processInstanceId = Guid.NewGuid().ToString().Replace("-","");
                string insert = "INSERT INTO T_FF_RT_PROCESSINSTANCE ("+
                    "ID, PROCESS_ID, VERSION, NAME, DISPLAY_NAME, "+
                    "STATE, SUSPENDED, CREATOR_ID, CREATED_TIME, STARTED_TIME, "+
                    "EXPIRED_TIME, END_TIME, PARENT_PROCESSINSTANCE_ID, PARENT_TASKINSTANCE_ID"+
                    ") VALUES(:1,:2,:3,:4,:5, :6,:7,:8,:9,:10, :11,:12,:13,:14)";
    			OracleParameter[] insertParms = { 
    				OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 50, processInstanceId), 
    				OracleHelper.NewOracleParameter(":2", OracleType.VarChar, 100, processInstance.getProcessId()), 
    				OracleHelper.NewOracleParameter(":3", OracleType.Int32, processInstance.getVersion()), 
    				OracleHelper.NewOracleParameter(":4", OracleType.VarChar, 100, processInstance.getName()), 
    				OracleHelper.NewOracleParameter(":5", OracleType.VarChar, 128, processInstance.getDisplayName()), 
    				OracleHelper.NewOracleParameter(":6", OracleType.Int32, processInstance.getState()), 
    				OracleHelper.NewOracleParameter(":7", OracleType.Int16, OracleHelper.OraBit(processInstance.IsSuspended())), 
    				OracleHelper.NewOracleParameter(":8", OracleType.VarChar, 50, processInstance.getCreatorId()), 
    				OracleHelper.NewOracleParameter(":9", OracleType.DateTime, 11, processInstance.getCreatedTime()), 
    				OracleHelper.NewOracleParameter(":10", OracleType.DateTime, 11, processInstance.getStartedTime()), 
    				OracleHelper.NewOracleParameter(":11", OracleType.DateTime, 11, processInstance.getExpiredTime()), 
    				OracleHelper.NewOracleParameter(":12", OracleType.DateTime, 11, processInstance.getEndTime()), 
    				OracleHelper.NewOracleParameter(":13", OracleType.VarChar, 50, processInstance.getParentProcessInstanceId()), 
    				OracleHelper.NewOracleParameter(":14", OracleType.VarChar, 50, processInstance.getParentTaskInstanceId())
    			};
    			if (OracleHelper.ExecuteNonQuery(connectionString, CommandType.Text, insert, insertParms) != 1)
    				return false;
    			else return true;
            }
            else
            {
               string update = "UPDATE T_FF_RT_PROCESSINSTANCE SET "+
                    "PROCESS_ID=:2, VERSION=:3, NAME=:4, DISPLAY_NAME=:5, STATE=:6, "+
                    "SUSPENDED=:7, CREATOR_ID=:8, CREATED_TIME=:9, STARTED_TIME=:10, EXPIRED_TIME=:11, "+
                    "END_TIME=:12, PARENT_PROCESSINSTANCE_ID=:13, PARENT_TASKINSTANCE_ID=:14 WHERE ID=:1";
    			OracleParameter[] updateParms = { 
    				OracleHelper.NewOracleParameter(":2", OracleType.VarChar, 100, processInstance.getProcessId()), 
    				OracleHelper.NewOracleParameter(":3", OracleType.Int32, processInstance.getVersion()), 
    				OracleHelper.NewOracleParameter(":4", OracleType.VarChar, 100, processInstance.getName()), 
    				OracleHelper.NewOracleParameter(":5", OracleType.VarChar, 128, processInstance.getDisplayName()), 
    				OracleHelper.NewOracleParameter(":6", OracleType.Int32, processInstance.getState()), 
    				OracleHelper.NewOracleParameter(":7", OracleType.Int16, OracleHelper.OraBit(processInstance.IsSuspended())), 
    				OracleHelper.NewOracleParameter(":8", OracleType.VarChar, 50, processInstance.getCreatorId()), 
    				OracleHelper.NewOracleParameter(":9", OracleType.DateTime, 11, processInstance.getCreatedTime()), 
    				OracleHelper.NewOracleParameter(":10", OracleType.DateTime, 11, processInstance.getStartedTime()), 
    				OracleHelper.NewOracleParameter(":11", OracleType.DateTime, 11, processInstance.getExpiredTime()), 
    				OracleHelper.NewOracleParameter(":12", OracleType.DateTime, 11, processInstance.getEndTime()), 
    				OracleHelper.NewOracleParameter(":13", OracleType.VarChar, 50, processInstance.getParentProcessInstanceId()), 
    				OracleHelper.NewOracleParameter(":14", OracleType.VarChar, 50, processInstance.getParentTaskInstanceId()),
    				OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 50, processInstance.getId())
    			};
    			if (OracleHelper.ExecuteNonQuery(connectionString, CommandType.Text, update, updateParms) != 1)
    				return false;
    			else return true;
            }
        }

        //通过ID获得“活的”ProcessInstance对象。
        //“活的”是指ProcessInstance.state=INITIALIZED Or ProcessInstance.state=STARTED Or ProcessInstance=SUSPENDED的流程实例
        public IProcessInstance findAliveProcessInstanceById(String id) {
            string select = "SELECT * FROM T_FF_RT_PROCESSINSTANCE WHERE ID=:1 and ( state=" + IProcessInstance.INITIALIZED
                    + " or state=" + IProcessInstance.RUNNING + ")";
            OracleConnection conn = new OracleConnection(connectionString);
            OracleDataReader reader = null;
            try
            {
                OracleParameter[] selectParms = { 
				OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 50, id)
				};
                reader = OracleHelper.ExecuteReader(conn, CommandType.Text, select, selectParms);
                if (reader.Read())
                {
                    IProcessInstance iProcessInstance = OracleHelper.ReaderToInfo<ProcessInstance>(reader);
                    return iProcessInstance;
                }
            }
            catch
            {
                throw;
            }
            finally
            {
                if (reader != null) reader.Close();
                if (conn.State != ConnectionState.Closed)
                {
                    conn.Close();
                    conn.Dispose();
                }
            }
            return null;        
        }

         // 通过ID获得ProcessInstance对象。
         // (Engine没有引用到该方法，提供给业务系统使用，20090303)
        public IProcessInstance findProcessInstanceById(String id)
        {
            string select = "SELECT * FROM T_FF_RT_PROCESSINSTANCE WHERE ID=:1";
            OracleConnection conn = new OracleConnection(connectionString);
            OracleDataReader reader = null;
            try
            {
                OracleParameter[] selectParms = { 
				    OracleHelper.NewOracleParameter(":1", OracleType.VarChar,100, id)
				};
                reader = OracleHelper.ExecuteReader(conn, CommandType.Text, select, selectParms);
                if (reader.Read())
                {
                    IProcessInstance iProcessInstance = OracleHelper.ReaderToInfo<ProcessInstance>(reader);
                    return iProcessInstance;
                }
            }
            catch
            {
                throw;
            }
            finally
            {
                if (reader != null) reader.Close();
                if (conn.State != ConnectionState.Closed)
                {
                    conn.Close();
                    conn.Dispose();
                }
            }
            return null;  
        }


        /**
         * 查找并返回同一个业务流程的所有实例
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param processId The id of the process definition.
         * @return A list of processInstance
         */
        public List<IProcessInstance> findProcessInstancesByProcessId(String processId)
        {
            List<IProcessInstance> processInstances = new List<IProcessInstance>();
            string select = "select * from t_ff_rt_processinstance order by created_time";
            OracleConnection connection = new OracleConnection(this.connectionString);
            OracleParameter[] selectParms = { 
				//OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 100, processId)
		    };
            OracleDataReader reader = null;

            try
            {
                reader = OracleHelper.ExecuteReader(connection, CommandType.Text, select, selectParms);
                if (reader != null)
                {
                    while (reader.Read())
                    {
                        IProcessInstance processInstance = OracleHelper.ReaderToInfo<ProcessInstance>(reader);
                        processInstances.Add(processInstance);
                    }
                }
            }
            finally
            {
                if (reader != null)
                {
                    reader.Close();
                    reader = null;
                }
                if (connection.State != ConnectionState.Closed)
                {
                    connection.Close();
                    connection = null;
                }
            }
            return processInstances;

        }


        /**
         * 查找并返回同一个指定版本业务流程的所有实例
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param processId The id of the process definition.
         * @return A list of processInstance
         */
        public List<IProcessInstance> findProcessInstancesByProcessIdAndVersion(String processId, int version) { throw new NotImplementedException(); }

        /**
         * 计算活动的子流程实例的数量
         * @param taskInstanceId 父TaskInstance的Id
         * @return
         */
        public int getAliveProcessInstanceCountForParentTaskInstance(String taskInstanceId) { throw new NotImplementedException(); }


        /**
         * 终止流程实例。将流程实例、活动的TaskInstance、活动的WorkItem的状态设置为CANCELED；并删除所有的token
         * @param processInstanceId
         */
        public bool abortProcessInstance(ProcessInstance processInstance) { throw new NotImplementedException(); }

        /**
         * 挂起流程实例
         * @param processInstance
         */
        public bool suspendProcessInstance(ProcessInstance processInstance) { throw new NotImplementedException(); }

        /**
         * 恢复流程实例
         * @param processInstance
         */
        public bool restoreProcessInstance(ProcessInstance processInstance) { throw new NotImplementedException(); }





        /******************************************************************************/
        /************                                                        **********/
        /************            task instance 相关的持久化方法               **********/
        /************            Persistence methods for task instance       **********/
        /************                                                        **********/
        /******************************************************************************/
        /**
         * 插入或者更新TaskInstance。<br/>
         * Save or update task instance. If the taskInstance.id is null then insert a new task instance record
         * and generate a new id for it { throw new NotImplementedException(); }
         * otherwise update the existent one. 
         * @param taskInstance
         */
        public bool saveOrUpdateTaskInstance(ITaskInstance taskInstance)
        {
            if (String.IsNullOrEmpty(taskInstance.getId()))
            {
                string taskInstanceId = Guid.NewGuid().ToString().Replace("-", "");
                string insert = "INSERT INTO T_FF_RT_TASKINSTANCE (" +
				"ID, BIZ_TYPE, TASK_ID, ACTIVITY_ID, NAME, " +
				"DISPLAY_NAME, STATE, SUSPENDED, TASK_TYPE, CREATED_TIME, " +
				"STARTED_TIME, EXPIRED_TIME, END_TIME, ASSIGNMENT_STRATEGY, PROCESSINSTANCE_ID, " +
				"PROCESS_ID, VERSION, TARGET_ACTIVITY_ID, FROM_ACTIVITY_ID, STEP_NUMBER, " +
				"CAN_BE_WITHDRAWN )VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    			OracleParameter[] insertParms = { 
    				OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 50, taskInstanceId), 
    				OracleHelper.NewOracleParameter(":2", OracleType.VarChar, 250, taskInstance.GetType().Name), 
    				OracleHelper.NewOracleParameter(":3", OracleType.VarChar, 300, taskInstance.getTaskId()), 
    				OracleHelper.NewOracleParameter(":4", OracleType.VarChar, 200, taskInstance.getActivityId()), 
    				OracleHelper.NewOracleParameter(":5", OracleType.VarChar, 100, taskInstance.getName()), 
    				OracleHelper.NewOracleParameter(":6", OracleType.VarChar, 128, taskInstance.getDisplayName()), 
    				OracleHelper.NewOracleParameter(":7", OracleType.Int32, taskInstance.getState()), 
    				OracleHelper.NewOracleParameter(":8", OracleType.Int16, OracleHelper.OraBit(taskInstance.IsSuspended())), 
    				OracleHelper.NewOracleParameter(":9", OracleType.VarChar, 10, taskInstance.getTaskType()), 
    				OracleHelper.NewOracleParameter(":10", OracleType.Timestamp, 11, taskInstance.getCreatedTime()), 
    				OracleHelper.NewOracleParameter(":11", OracleType.Timestamp, 11, taskInstance.getStartedTime()), 
    				OracleHelper.NewOracleParameter(":12", OracleType.Timestamp, 11, taskInstance.getExpiredTime()), 
    				OracleHelper.NewOracleParameter(":13", OracleType.Timestamp, 11, taskInstance.getEndTime()), 
    				OracleHelper.NewOracleParameter(":14", OracleType.VarChar, 10, taskInstance.getAssignmentStrategy()), 
    				OracleHelper.NewOracleParameter(":15", OracleType.VarChar, 50, taskInstance.getProcessInstanceId()), 
    				OracleHelper.NewOracleParameter(":16", OracleType.VarChar, 100, taskInstance.getProcessId()), 
    				OracleHelper.NewOracleParameter(":17", OracleType.Int32, taskInstance.getVersion()), 
    				OracleHelper.NewOracleParameter(":18", OracleType.VarChar, 100, taskInstance.getTargetActivityId()), 
    				OracleHelper.NewOracleParameter(":19", OracleType.VarChar, 600, ((TaskInstance) taskInstance).getFromActivityId()), 
    				OracleHelper.NewOracleParameter(":20", OracleType.Int32, taskInstance.getStepNumber()), 
    				OracleHelper.NewOracleParameter(":21", OracleType.Int16, OracleHelper.OraBit(((TaskInstance) taskInstance).getCanBeWithdrawn()))
    			};
    			if (OracleHelper.ExecuteNonQuery(connectionString, CommandType.Text, insert, insertParms) != 1)
    				return false;
    			else return true;
            }
            else
            {
                string update = "UPDATE T_FF_RT_TASKINSTANCE SET " +
                "BIZ_TYPE=?, TASK_ID=?, ACTIVITY_ID=?, NAME=?, DISPLAY_NAME=?, " +
                "STATE=?, SUSPENDED=?, TASK_TYPE=?, CREATED_TIME=?, STARTED_TIME=?, " +
                "EXPIRED_TIME=?, END_TIME=?, ASSIGNMENT_STRATEGY=?, PROCESSINSTANCE_ID=?, PROCESS_ID=?, " +
                "VERSION=?, TARGET_ACTIVITY_ID=?, FROM_ACTIVITY_ID=?, STEP_NUMBER=?, CAN_BE_WITHDRAWN=?" +
                " WHERE ID=?";
                OracleParameter[] updateParms = { 
    				OracleHelper.NewOracleParameter(":2", OracleType.VarChar, 250, taskInstance.GetType().Name), 
    				OracleHelper.NewOracleParameter(":3", OracleType.VarChar, 300, taskInstance.getTaskId()), 
    				OracleHelper.NewOracleParameter(":4", OracleType.VarChar, 200, taskInstance.getActivityId()), 
    				OracleHelper.NewOracleParameter(":5", OracleType.VarChar, 100, taskInstance.getName()), 
    				OracleHelper.NewOracleParameter(":6", OracleType.VarChar, 128, taskInstance.getDisplayName()), 
    				OracleHelper.NewOracleParameter(":7", OracleType.Int32, taskInstance.getState()), 
    				OracleHelper.NewOracleParameter(":8", OracleType.Int16, OracleHelper.OraBit(taskInstance.IsSuspended())), 
    				OracleHelper.NewOracleParameter(":9", OracleType.VarChar, 10, taskInstance.getTaskType()), 
    				OracleHelper.NewOracleParameter(":10", OracleType.Timestamp, 11, taskInstance.getCreatedTime()), 
    				OracleHelper.NewOracleParameter(":11", OracleType.Timestamp, 11, taskInstance.getStartedTime()), 
    				OracleHelper.NewOracleParameter(":12", OracleType.Timestamp, 11, taskInstance.getExpiredTime()), 
    				OracleHelper.NewOracleParameter(":13", OracleType.Timestamp, 11, taskInstance.getEndTime()), 
    				OracleHelper.NewOracleParameter(":14", OracleType.VarChar, 10, taskInstance.getAssignmentStrategy()), 
    				OracleHelper.NewOracleParameter(":15", OracleType.VarChar, 50, taskInstance.getProcessInstanceId()), 
    				OracleHelper.NewOracleParameter(":16", OracleType.VarChar, 100, taskInstance.getProcessId()), 
    				OracleHelper.NewOracleParameter(":17", OracleType.Int32, taskInstance.getVersion()), 
    				OracleHelper.NewOracleParameter(":18", OracleType.VarChar, 100, taskInstance.getTargetActivityId()), 
    				OracleHelper.NewOracleParameter(":19", OracleType.VarChar, 600, ((TaskInstance) taskInstance).getFromActivityId()), 
    				OracleHelper.NewOracleParameter(":20", OracleType.Int32, taskInstance.getStepNumber()), 
    				OracleHelper.NewOracleParameter(":21", OracleType.Int16, OracleHelper.OraBit(((TaskInstance) taskInstance).getCanBeWithdrawn())),
    				OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 50, taskInstance.getId())
    			};
                if (OracleHelper.ExecuteNonQuery(connectionString, CommandType.Text, update, updateParms) != 1)
                    return false;
                else return true;
            }
        }

        /**
         * 终止TaskInstance。将任务实例及其所有的“活的”WorkItem变成Canceled状态。<br/>
         * "活的"WorkItem 是指状态等于INITIALIZED、STARTED或者SUSPENDED的WorkItem.
         * @param taskInstanceId
         */
        public bool abortTaskInstance(TaskInstance taskInstance) { throw new NotImplementedException(); }

        /**
         * 返回“活的”TaskInstance。<br/>
         * “活的”是指TaskInstance.state=INITIALIZED Or TaskInstance.state=STARTED 。
         * @param id
         * @return
         */
        public ITaskInstance findAliveTaskInstanceById(String id) { throw new NotImplementedException(); }

        /**
         * 获得activity的“活的”TaskInstance的数量<br/>
         * “活的”是指TaskInstance.state=INITIALIZED Or TaskInstance.state=STARTED 。
         * @param processInstanceId
         * @param activityId
         * @return
         */
        public int getAliveTaskInstanceCountForActivity(String processInstanceId, String activityId) { throw new NotImplementedException(); }

        /**
         * 返回某个Task已经结束的TaskInstance的数量。<br/>
         * “已经结束”是指TaskInstance.state=COMPLETED。
         * @param processInstanceId
         * @param taskId
         * @return
         */
        public int getCompletedTaskInstanceCountForTask(String processInstanceId, String taskId) { throw new NotImplementedException(); }


        /**
         * Find the task instance by id
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param id
         * @return
         */
        public ITaskInstance findTaskInstanceById(String id) { throw new NotImplementedException(); }

        /**
         * 查询流程实例的所有的TaskInstance,如果activityId不为空，则返回该流程实例下指定环节的TaskInstance<br/>
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param processInstanceId  the id of the process instance
         * @param activityId  if the activityId is null, then return all the taskinstance of the processinstance{ throw new NotImplementedException(); }
         * @return
         */
        public List<ITaskInstance> findTaskInstancesForProcessInstance(String processInstanceId, String activityId) { throw new NotImplementedException(); }


        /**
         * 查询出同一个stepNumber的所有TaskInstance实例
         * @param processInstanceId
         * @param stepNumber
         * @return
         */
        public List<ITaskInstance> findTaskInstancesForProcessInstanceByStepNumber(String processInstanceId, Int32 stepNumber) { throw new NotImplementedException(); }


        /**
         * 调用数据库自身的机制所定TaskInstance实例。<br/>
         * 该方法主要用于工单的签收操作，在签收之前先锁定与之对应的TaskInstance。
         * @param taskInstanceId
         * @return
         */
        public bool lockTaskInstance(String taskInstanceId) { throw new NotImplementedException(); }


        /******************************************************************************/
        /************                                                        **********/
        /************            workItem 相关的持久化方法                    **********/
        /************            Persistence methods for workitem            **********/
        /************                                                        **********/
        /******************************************************************************/
        /**
         * 插入或者更新WorkItem<br/>
         * save or update workitem
         * @param workitem
         */
        public bool saveOrUpdateWorkItem(IWorkItem workitem)
        {
            if (String.IsNullOrEmpty(workitem.getId()))
            {
                String workItemId = Guid.NewGuid().ToString().Replace("-", ""); ;

                string insert = "INSERT INTO T_FF_RT_WORKITEM (" +
                "ID, STATE, CREATED_TIME, CLAIMED_TIME, END_TIME, " +
                "ACTOR_ID, COMMENTS, TASKINSTANCE_ID )VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
                OracleParameter[] insertParms = { 
    				OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 50, workItemId), 
    				OracleHelper.NewOracleParameter(":2", OracleType.Int32, workitem.getState()), 
    				OracleHelper.NewOracleParameter(":3", OracleType.Timestamp, 11, workitem.getCreatedTime()), 
    				OracleHelper.NewOracleParameter(":4", OracleType.Timestamp, 11, workitem.getClaimedTime()), 
    				OracleHelper.NewOracleParameter(":5", OracleType.Timestamp, 11, workitem.getEndTime()), 
    				OracleHelper.NewOracleParameter(":6", OracleType.VarChar, 50, workitem.getActorId()), 
    				OracleHelper.NewOracleParameter(":7", OracleType.VarChar, 1024, workitem.getComments()), 
    				OracleHelper.NewOracleParameter(":8", OracleType.VarChar, 50, workitem.getTaskInstance().getId())
    			};
                if (OracleHelper.ExecuteNonQuery(connectionString, CommandType.Text, insert, insertParms) != 1)
                    return false;
                else return true;
            }
            else
            {
                string update = "UPDATE T_FF_RT_WORKITEM SET " +
                "STATE=?, CREATED_TIME=?, CLAIMED_TIME=?, END_TIME=?, ACTOR_ID=?, " +
                "COMMENTS=?, TASKINSTANCE_ID=?" +
                " WHERE ID=?";
                OracleParameter[] updateParms = { 
    				OracleHelper.NewOracleParameter(":2", OracleType.Int32, workitem.getState()), 
    				OracleHelper.NewOracleParameter(":3", OracleType.Timestamp, 11, workitem.getCreatedTime()), 
    				OracleHelper.NewOracleParameter(":4", OracleType.Timestamp, 11, workitem.getClaimedTime()), 
    				OracleHelper.NewOracleParameter(":5", OracleType.Timestamp, 11, workitem.getEndTime()), 
    				OracleHelper.NewOracleParameter(":6", OracleType.VarChar, 50, workitem.getActorId()), 
    				OracleHelper.NewOracleParameter(":7", OracleType.VarChar, 1024, workitem.getComments()), 
    				OracleHelper.NewOracleParameter(":8", OracleType.VarChar, 50, workitem.getTaskInstance().getId()),
    				OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 50, workitem.getId())
    			};
                if (OracleHelper.ExecuteNonQuery(connectionString, CommandType.Text, update, updateParms) != 1)
                    return false;
                else return true;
            }
        }




        /**
         * 返回任务实例的所有"活的"WorkItem的数量。<br>
         * "活的"WorkItem 是指状态等于INITIALIZED、STARTED或者SUSPENDED的WorkItem。
         * @param taskInstanceId
         * @return
         */
        public Int32 getAliveWorkItemCountForTaskInstance(String taskInstanceId) { throw new NotImplementedException(); }

        /**
         * 查询任务实例的所有"已经结束"WorkItem。<br>
         * 
         * 所以必须有关联条件WorkItem.state=IWorkItem.COMPLTED 
         *
         * @param taskInstanceId 任务实例Id
         * @return
         */
        public List<IWorkItem> findCompletedWorkItemsForTaskInstance(String taskInstanceId) { throw new NotImplementedException(); }

        /**
         * 查询某任务实例的所有WorkItem
         * @param taskInstanceId
         * @return
         */
        public List<IWorkItem> findWorkItemsForTaskInstance(String taskInstanceId) { throw new NotImplementedException(); }


        /**
         * 删除处于初始化状态的workitem。
         * 此方法用于签收Workitem时，删除其他Actor的WorkItem
         * @param taskInstanceId
         */
        public bool deleteWorkItemsInInitializedState(String taskInstanceId) { throw new NotImplementedException(); }


        /**
         * Find workItem by id
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param id
         * @return
         */
        public IWorkItem findWorkItemById(String id) { throw new NotImplementedException(); }


        /**
         *
         * Find all workitems for task
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param taskid
         * @return
         */
        public List<IWorkItem> findWorkItemsForTask(String taskid) { throw new NotImplementedException(); }


        /**
         * 根据操作员的Id返回其待办工单。如果actorId==null，则返回系统所有的待办任务<br/>
         * 待办工单是指状态等于INITIALIZED或STARTED工单<br/>
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param actorId
         * @return
         */
        public List<IWorkItem> findTodoWorkItems(String actorId) { throw new NotImplementedException(); }

        /**
         * 查找操作员在某个流程实例中的待办工单。
         * 如果processInstanceId为空，则等价于调用findTodoWorkItems(String actorId)
         * 待办工单是指状态等于INITIALIZED或STARTED工单<br/>
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param actorId
         * @param processInstanceId
         * @return
         */
        public List<IWorkItem> findTodoWorkItems(String actorId, String processInstanceId) { throw new NotImplementedException(); }

        /**
         * 查找操作员在某个流程某个任务上的待办工单。
         * actorId，processId，taskId都可以为空（null或者""）,为空的条件将被忽略
         * 待办工单是指状态等于INITIALIZED或STARTED工单<br/>
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param actorId
         * @param processId
         * @param taskId
         * @return
         */
        public List<IWorkItem> findTodoWorkItems(String actorId, String processId, String taskId) { throw new NotImplementedException(); }

        /**
         * 根据操作员的Id返回其已办工单。如果actorId==null，则返回系统所有的已办任务
         * 已办工单是指状态等于COMPLETED或CANCELED的工单<br/>
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param actorId
         * @return
         */
        public List<IWorkItem> findHaveDoneWorkItems(String actorId) { throw new NotImplementedException(); }

        /**
         * 查找操作员在某个流程实例中的已办工单。
         * 如果processInstanceId为空，则等价于调用findHaveDoneWorkItems(String actorId)
         * 已办工单是指状态等于COMPLETED或CANCELED的工单<br/>
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param actorId
         * @param processInstanceId
         * @return
         */
        public List<IWorkItem> findHaveDoneWorkItems(String actorId, String processInstanceId) { throw new NotImplementedException(); }

        /**
         * 查找操作员在某个流程某个任务上的已办工单。
         * actorId，processId，taskId都可以为空（null或者""）,为空的条件将被忽略
         * 已办工单是指状态等于COMPLETED或CANCELED的工单<br/>
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param actorId
         * @param processId
         * @param taskId
         * @return
         */
        public List<IWorkItem> findHaveDoneWorkItems(String actorId, String processId, String taskId) { throw new NotImplementedException(); }



        /*************************Persistence methods for joinpoint*********************/
        /**
         * Save joinpoint
         *
         * @param joinPoint
         */
        //    public bool saveOrUpdateJoinPoint(IJoinPoint joinPoint){ throw new NotImplementedException(); }

        /**
         * Find the joinpoint id
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param id
         * @return
         */
        //    public IJoinPoint findJoinPointById(String id){ throw new NotImplementedException(); }

        /**
         * Find all the joinpoint of the process instance, and the synchronizerId of the joinpoint must equals to the seconds argument.
         * @param processInstanceId
         * @param synchronizerId if the synchronizerId is null ,then all the joinpoint of the process instance will be returned.
         * @return
         */
        //    public List<IJoinPoint> findJoinPointsForProcessInstance(String processInstanceId, String synchronizerId){ throw new NotImplementedException(); }


        /******************************************************************************/
        /************                                                        **********/
        /************            token 相关的持久化方法                       **********/
        /************            Persistence methods for token               **********/
        /************                                                        **********/
        /******************************************************************************/
        /**
         * Save token
         * @param token
         */
        public bool saveOrUpdateToken(IToken token)
        {
            if (String.IsNullOrEmpty(token.getId()))
            {
                String tokenId = Guid.NewGuid().ToString().Replace("-", "");
                string insert = "INSERT INTO T_FF_RT_TOKEN (" +
                "ID, ALIVE, VALUE, NODE_ID, PROCESSINSTANCE_ID, " +
                "STEP_NUMBER, FROM_ACTIVITY_ID )VALUES(?, ?, ?, ?, ?, ?, ?)";
                OracleParameter[] insertParms = { 
    				OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 50, tokenId), 
    				OracleHelper.NewOracleParameter(":2", OracleType.Int16, OracleHelper.OraBit(token.isAlive())), 
    				OracleHelper.NewOracleParameter(":3", OracleType.Int32, token.getValue()), 
    				OracleHelper.NewOracleParameter(":4", OracleType.VarChar, 200, token.getNodeId()), 
    				OracleHelper.NewOracleParameter(":5", OracleType.VarChar, 50, token.getProcessInstanceId()), 
    				OracleHelper.NewOracleParameter(":6", OracleType.Int32, token.getStepNumber()), 
    				OracleHelper.NewOracleParameter(":7", OracleType.VarChar, 100, token.getFromActivityId())
    			};
                if (OracleHelper.ExecuteNonQuery(connectionString, CommandType.Text, insert, insertParms) != 1)
                    return false;
                else return true;
            }
            else
            {
                string update = "UPDATE T_FF_RT_TOKEN SET " +
                    "ALIVE=?, VALUE=?, NODE_ID=?, PROCESSINSTANCE_ID=?, STEP_NUMBER=?, " +
                    "FROM_ACTIVITY_ID=?" +
                    " WHERE ID=?";
                OracleParameter[] updateParms = { 
					OracleHelper.NewOracleParameter(":2", OracleType.Int16, OracleHelper.OraBit(token.isAlive())), 
					OracleHelper.NewOracleParameter(":3", OracleType.Int32, token.getValue()), 
					OracleHelper.NewOracleParameter(":4", OracleType.VarChar, 200, token.getNodeId()), 
					OracleHelper.NewOracleParameter(":5", OracleType.VarChar, 50, token.getProcessInstanceId()), 
					OracleHelper.NewOracleParameter(":6", OracleType.Int32, token.getStepNumber()), 
					OracleHelper.NewOracleParameter(":7", OracleType.VarChar, 100, token.getFromActivityId()),
					OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 50, token.getId())
				};
                if (OracleHelper.ExecuteNonQuery(connectionString, CommandType.Text, update, updateParms) != 1)
                    return false;
                else return true;
            }
        }

        /**
         * 统计流程任意节点的活动Token的数量。对于Activity节点，该数量只能取值1或者0，大于1表明有流程实例出现异常。
         * @param processInstanceId
         * @param nodeId
         * @return
         */
        public int getAliveTokenCountForNode(String processInstanceId, String nodeId) { throw new NotImplementedException(); }

        /**
         * 查找到状态为Dead的token
         * @param id
         * @return
         */
        //    public IToken findDeadTokenById(String id){ throw new NotImplementedException(); }

        /**
         * (Engine没有引用到该方法，提供给业务系统使用，20090303)
         * @param id
         * @return
         */
        public IToken findTokenById(String id) { throw new NotImplementedException(); }

        /**
         * Find all the tokens for process instance ,and the nodeId of the token must equals to the second argument.
         * @param processInstanceId the id of the process instance
         * @param nodeId if the nodeId is null ,then return all the tokens of the process instance.
         * @return
         */
        public List<IToken> findTokensForProcessInstance(String processInstanceId, String nodeId) { throw new NotImplementedException(); }

        /**
         * 删除某个节点的所有token
         * @param processInstanceId
         * @param nodeId
         */
        public bool deleteTokensForNode(String processInstanceId, String nodeId) { throw new NotImplementedException(); }

        /**
         * 删除某些节点的所有token
         * @param processInstanceId
         * @param nodeIdsList
         */
        public bool deleteTokensForNodes(String processInstanceId, List<String> nodeIdsList) { throw new NotImplementedException(); }

        /**
         * 删除token
         * @param token
         */
        public bool deleteToken(IToken token) { throw new NotImplementedException(); }

        /******************************************************************************/
        /************                                                        **********/
        /************            存取流程定义文件 相关的持久化方法             **********/
        /************            Persistence methods for workflow definition **********/
        /************                                                        **********/
        /******************************************************************************/
        /**
         * Save or update the workflow definition. The version will be increased automatically when insert a new record.<br>
         * 保存流程定义，如果同一个ProcessId的流程定义已经存在，则版本号自动加1。
         * @param workflowDef
         */
        public bool saveOrUpdateWorkflowDefinition(WorkflowDefinition workflowDef) {
            if (String.IsNullOrEmpty(workflowDef.getId()))
            {
                Int32 latestVersion = findTheLatestVersionNumberIgnoreState(workflowDef.getProcessId());
                if (latestVersion != null && latestVersion>0)
                {
                    workflowDef.setVersion(latestVersion + 1);
                }
                else
                {
                    workflowDef.setVersion(1);
                }
            }


            if (String.IsNullOrEmpty(workflowDef.getId()))
            {
                String workflowDefId = Guid.NewGuid().ToString().Replace("-", "");
                string insert = "INSERT INTO T_FF_DF_WORKFLOWDEF (" +
                    "ID, DEFINITION_TYPE, PROCESS_ID, NAME, DISPLAY_NAME, " +
                    "DESCRIPTION, VERSION, STATE, UPLOAD_USER, UPLOAD_TIME, " +
                    "PUBLISH_USER, PUBLISH_TIME, PROCESS_CONTENT )VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                OracleParameter[] insertParms = { 
					OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 50, workflowDefId), 
					OracleHelper.NewOracleParameter(":2", OracleType.VarChar, 50, workflowDef.getDefinitionType()), 
					OracleHelper.NewOracleParameter(":3", OracleType.VarChar, 100, workflowDef.getProcessId()), 
					OracleHelper.NewOracleParameter(":4", OracleType.VarChar, 100, workflowDef.getName()), 
					OracleHelper.NewOracleParameter(":5", OracleType.VarChar, 128, workflowDef.getDisplayName()), 
					OracleHelper.NewOracleParameter(":6", OracleType.VarChar, 1024, workflowDef.getDescription()), 
					OracleHelper.NewOracleParameter(":7", OracleType.Int32, workflowDef.getVersion()), 
					OracleHelper.NewOracleParameter(":8", OracleType.Int16, OracleHelper.OraBit(workflowDef.getState()) ), 
					OracleHelper.NewOracleParameter(":9", OracleType.VarChar, 50, workflowDef.getUploadUser()), 
					OracleHelper.NewOracleParameter(":10", OracleType.Timestamp, 11, workflowDef.getUploadTime()), 
					OracleHelper.NewOracleParameter(":11", OracleType.VarChar, 50, workflowDef.getPublishUser()), 
					OracleHelper.NewOracleParameter(":12", OracleType.Timestamp, 11, workflowDef.getPublishTime()), 
					OracleHelper.NewOracleParameter(":13", OracleType.Clob, workflowDef.getProcessContent())
				};
                if (OracleHelper.ExecuteNonQuery(connectionString, CommandType.Text, insert, insertParms) != 1)
                    return false;
                else return true;
            }
            else
            {
                string update = "UPDATE T_FF_DF_WORKFLOWDEF SET " +
                    "DEFINITION_TYPE=?, PROCESS_ID=?, NAME=?, DISPLAY_NAME=?, DESCRIPTION=?, " +
                    "VERSION=?, STATE=?, UPLOAD_USER=?, UPLOAD_TIME=?, PUBLISH_USER=?, " +
                    "PUBLISH_TIME=?, PROCESS_CONTENT=?" +
                    " WHERE ID=?";
                OracleParameter[] updateParms = { 
    				OracleHelper.NewOracleParameter(":2", OracleType.VarChar, 50, workflowDef.getDefinitionType()), 
    				OracleHelper.NewOracleParameter(":3", OracleType.VarChar, 100, workflowDef.getProcessId()), 
    				OracleHelper.NewOracleParameter(":4", OracleType.VarChar, 100, workflowDef.getName()), 
    				OracleHelper.NewOracleParameter(":5", OracleType.VarChar, 128, workflowDef.getDisplayName()), 
    				OracleHelper.NewOracleParameter(":6", OracleType.VarChar, 1024, workflowDef.getDescription()), 
    				OracleHelper.NewOracleParameter(":7", OracleType.Int32, workflowDef.getVersion()), 
    				OracleHelper.NewOracleParameter(":8", OracleType.Int16, OracleHelper.OraBit(workflowDef.getState())), 
    				OracleHelper.NewOracleParameter(":9", OracleType.VarChar, 50, workflowDef.getUploadUser()), 
    				OracleHelper.NewOracleParameter(":10", OracleType.Timestamp, 11, workflowDef.getUploadTime()), 
    				OracleHelper.NewOracleParameter(":11", OracleType.VarChar, 50, workflowDef.getPublishUser()), 
    				OracleHelper.NewOracleParameter(":12", OracleType.Timestamp, 11, workflowDef.getPublishTime()), 
    				OracleHelper.NewOracleParameter(":13", OracleType.Clob, workflowDef.getProcessContent()),
    				OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 50, workflowDef.getId())
    			};
                if (OracleHelper.ExecuteNonQuery(connectionString, CommandType.Text, update, updateParms) != 1)
                    return false;
                else return true;
            }
        }

        /**
         * Find the workflow definition by id .
         * 根据纪录的ID返回流程定义
         * @param id
         * @return
         */
        public WorkflowDefinition findWorkflowDefinitionById(String id) { throw new NotImplementedException(); }

        /**
         * Find workflow definition by workflow process id and version<br>
         * 根据ProcessId和版本号返回流程定义
         * @param processId
         * @param version
         * @return
         */
        public WorkflowDefinition findWorkflowDefinitionByProcessIdAndVersionNumber(String processId, int version) { throw new NotImplementedException(); }

        /**
         * Find the latest version of the workflow definition.<br>
         * 根据processId返回最新版本的有效流程定义
         * @param processId the workflow process id 
         * @return
         */
        public WorkflowDefinition findTheLatestVersionOfWorkflowDefinitionByProcessId(String processId) { throw new NotImplementedException(); }

        /**
         * Find all the workflow definitions for the workflow process id.<br>
         * 根据ProcessId 返回所有版本的流程定义
         * @param processId
         * @return
         */
        public List<WorkflowDefinition> findWorkflowDefinitionsByProcessId(String processId) { throw new NotImplementedException(); }

        /**
         * Find all of the latest version of workflow definitions.<br>
         * 返回系统中所有的最新版本的有效流程定义
         * @return
         */
        public List<WorkflowDefinition> findAllTheLatestVersionsOfWorkflowDefinition() { throw new NotImplementedException(); }

        /**
         * Find the latest version number <br>
         * 返回最新的有效版本号
         * @param processId
         * @return the version number ,null if there is no workflow definition stored in the DB.
         */
        public int findTheLatestVersionNumber(String processId) { throw new NotImplementedException(); }

        /**
         * 返回最新版本号,
         * @param processId
         * @return
         */
        public int findTheLatestVersionNumberIgnoreState(String processId) { throw new NotImplementedException(); }



        /********************************process instance trace info **********************/
        public bool saveOrUpdateProcessInstanceTrace(ProcessInstanceTrace processInstanceTrace) {
            if (String.IsNullOrEmpty(processInstanceTrace.getId()))
            {
                String processInstanceTraceId = Guid.NewGuid().ToString().Replace("-", "");
                string insert = "INSERT INTO T_FF_HIST_TRACE (" +
                "ID, PROCESSINSTANCE_ID, STEP_NUMBER, MINOR_NUMBER, TYPE, " +
                "EDGE_ID, FROM_NODE_ID, TO_NODE_ID )VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
                OracleParameter[] insertParms = { 
				OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 50, processInstanceTraceId), 
				OracleHelper.NewOracleParameter(":2", OracleType.VarChar, 50, processInstanceTrace.getProcessInstanceId()), 
				OracleHelper.NewOracleParameter(":3", OracleType.Int32, processInstanceTrace.getStepNumber()), 
				OracleHelper.NewOracleParameter(":4", OracleType.Int32, processInstanceTrace.getMinorNumber()), 
				OracleHelper.NewOracleParameter(":5", OracleType.VarChar, 15, processInstanceTrace.getType()), 
				OracleHelper.NewOracleParameter(":6", OracleType.VarChar, 100, processInstanceTrace.getEdgeId()), 
				OracleHelper.NewOracleParameter(":7", OracleType.VarChar, 100, processInstanceTrace.getFromNodeId()), 
				OracleHelper.NewOracleParameter(":8", OracleType.VarChar, 100, processInstanceTrace.getToNodeId())
			};
                if (OracleHelper.ExecuteNonQuery(connectionString, CommandType.Text, insert, insertParms) != 1)
                    return false;
                else return true;
            }
            else
            {
                string update = "UPDATE T_FF_HIST_TRACE SET " +
                "PROCESSINSTANCE_ID=?, STEP_NUMBER=?, MINOR_NUMBER=?, TYPE=?, EDGE_ID=?, " +
                "FROM_NODE_ID=?, TO_NODE_ID=?" +
                " WHERE ID=?";
                OracleParameter[] updateParms = { 
				OracleHelper.NewOracleParameter(":2", OracleType.VarChar, 50, processInstanceTrace.getProcessInstanceId()), 
				OracleHelper.NewOracleParameter(":3", OracleType.Int32, processInstanceTrace.getStepNumber()), 
				OracleHelper.NewOracleParameter(":4", OracleType.Int32, processInstanceTrace.getMinorNumber()), 
				OracleHelper.NewOracleParameter(":5", OracleType.VarChar, 15, processInstanceTrace.getType()), 
				OracleHelper.NewOracleParameter(":6", OracleType.VarChar, 100, processInstanceTrace.getEdgeId()), 
				OracleHelper.NewOracleParameter(":7", OracleType.VarChar, 100, processInstanceTrace.getFromNodeId()), 
				OracleHelper.NewOracleParameter(":8", OracleType.VarChar, 100, processInstanceTrace.getToNodeId()),
				OracleHelper.NewOracleParameter(":1", OracleType.VarChar, 50, processInstanceTrace.getId())
			};
                if (OracleHelper.ExecuteNonQuery(connectionString, CommandType.Text, update, updateParms) != 1)
                    return false;
                else return true;
            }
        }
        public List<String> findProcessInstanceTraces(String processInstanceId) { throw new NotImplementedException(); }


    }
}
