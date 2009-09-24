	/**
	 * Copyright 2009-2010 wmj2003
	 * All rights reserved. 
	 * 
	 * This program is free software: you can redistribute it and/or modify
	 * it under the terms of the GNU General Public License as published by
	 * the Free Software Foundation。
	 *
	 * This program is distributed in the hope that it will be useful,
	 * but WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 * GNU General Public License for more details.
	 *
	 * You should have received a copy of the GNU General Public License
	 * along with this program. If not, see http://www.gnu.org/licenses. *
	 */
package org.fireflow.engine.persistence.springjdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.engine.impl.ProcessInstanceTrace;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.impl.WorkItem;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.impl.Token;
import org.hibernate.HibernateException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;



/**
 * 配置方式如下：
 *	&lt;bean id="persistenceService" class="org.fireflow.engine.persistence.springjdbc.PersistenceServiceSpringJdbcImpl"&gt;<br/>
 *		&lt;property name="dataSource" ref="dataSource" /&gt;<br/>
 *		&lt;property name="lobHandler" ref="defaltLobHandler" /&gt;<br/>
 *	&lt;/bean&gt;<br/>
 *		&lt;bean id="oracleLobHandler"
 *		class="org.springframework.jdbc.support.lob.OracleLobHandler"
 *		lazy-init="true"&gt;<br/>
 *		&lt;property name="nativeJdbcExtractor" ref="nativeJdbcExtractor" /&gt;<br/>
 *	&lt;/bean&gt;<br/>
 *	&lt;bean id="defaltLobHandler" class="org.springframework.jdbc.support.lob.DefaultLobHandler"
 *		lazy-init="true"&gt;<br/>
 *	&lt;/bean&gt;<br/>	
 * @author wmj2003
 *
 */
public class PersistenceServiceSpringJdbcImpl  extends JdbcDaoSupport implements IPersistenceService{ 
	private static Log log = LogFactory.getLog(PersistenceServiceSpringJdbcImpl.class);
	

	    protected RuntimeContext rtCtx = null;

	    public void setRuntimeContext(RuntimeContext ctx) {
	        this.rtCtx = ctx;
	    }

	    public RuntimeContext getRuntimeContext() {
	        return this.rtCtx;
	    }

	    public java.sql.Date getSqlDate(final java.util.Date date){
	    	if(date == null ){
	    		return null;
	    	}else{
	    		return new java.sql.Date(date.getTime());
	    	}
	    }
//	    private String getSqlDate(final java.util.Date date){
//	    	if(date == null ){
//	    		return null;
//	    	}else{
//	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	    		return sdf.format(date);
//	    	}
//	    }	    
	    
	    /**
	     * 流程实例
	     * Save processInstance
	     * @param processInstance
	     */
	    public void saveOrUpdateProcessInstance(IProcessInstance processInstance) {
	    	//首先判断流程实例ID是否为null，如果为null那么create，否则就update
	    	//同时还要判断流程实例中的变量值，如果存在就更新如果不存在就插入
	    	
	    	String processInstanceId = null;
	    	if(processInstance.getId()==null || processInstance.getId().trim().equals("")){
	    		//向数据库中插入数据
	    		StringBuffer SQL = new StringBuffer();
	    		SQL.append("INSERT ");
	    		SQL.append("INTO    T_FF_RT_PROCESSINSTANCE ");
	    		SQL.append("        ( ");
	    		SQL.append("                id                       , ");
	    		SQL.append("                process_id               , ");
	    		SQL.append("                version                  , ");
	    		SQL.append("                name                     , ");
	    		SQL.append("                display_name             , ");
	    		
	    		SQL.append("                state                    , ");
	    		SQL.append("                suspended                , ");
	    		SQL.append("                creator_id               , ");
	    		SQL.append("                created_time             , ");
	    		SQL.append("                started_time             , ");
	    		
	    		SQL.append("                expired_time             , ");
	    		SQL.append("                end_time                 , ");
	    		SQL.append("                parent_processinstance_id, ");
	    		SQL.append("                parent_taskinstance_id     ");
	    		SQL.append("        ) ");
	    		SQL.append("        VALUES ");
	    		SQL.append("        ( ");
	    		SQL.append("                ?, ");
	    		SQL.append("                ?, ");
	    		SQL.append("                ?, ");
	    		SQL.append("                ?, ");
	    		SQL.append("                ?, ");
	    		
	    		SQL.append("                ?, ");
	    		SQL.append("                ?, ");
	    		SQL.append("                ?, ");
	    		SQL.append("                ?, ");
	    		SQL.append("                ?, ");
	    		
	    		SQL.append("                ?, ");
	    		SQL.append("                ?, ");
	    		SQL.append("                ?, ");
	    		SQL.append("                ? ");
	    		SQL.append("        )");
	    		processInstanceId = java.util.UUID.randomUUID().toString().replace("-", "");
	    		((ProcessInstance)processInstance).setId(processInstanceId); //给流程实例赋值
	    		super.getJdbcTemplate().update(SQL.toString()
	    	    		,new Object[]{
	    			processInstanceId,
	    	    	processInstance.getProcessId(),
	    	    	processInstance.getVersion(),
	    	    	processInstance.getName(),
	    	    	processInstance.getDisplayName(),
	    	    	
	    	    	processInstance.getState(),
	    	    	processInstance.isSuspended()==true?1:0,
	    	    	processInstance.getCreatorId(),
	    	    	getSqlDate(processInstance.getCreatedTime()),
	    	    	getSqlDate(processInstance.getStartedTime()),
	    	    	
	    	    	getSqlDate(processInstance.getExpiredTime()),
	    	    	getSqlDate(processInstance.getEndTime()),
	    	    	processInstance.getParentProcessInstanceId(),
	    	    	processInstance.getParentTaskInstanceId()
	    	    },new int[]{Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,
	    			Types.INTEGER,Types.INTEGER,Types.VARCHAR,Types.TIME,Types.TIME,
	    			Types.TIME,Types.TIME,Types.VARCHAR,Types.VARCHAR
	    		});
	    	}else{
	    		//更新数据
	    	    StringBuffer SQL = new StringBuffer();
	    	    SQL.append("UPDATE T_FF_RT_PROCESSINSTANCE ");
	    	    SQL.append("SET     process_id                = ?, ");
	    	    SQL.append("        version                   = ?, ");
	    	    SQL.append("        name                      = ?, ");
	    	    SQL.append("        display_name              = ?, ");
	    	    SQL.append("        state                     = ?, ");
	    	    
	    	    SQL.append("        suspended                 = ?, ");
	    	    SQL.append("        creator_id                = ?, ");
	    	    SQL.append("        created_time              = ?, ");
	    	    SQL.append("        started_time              = ?, ");
	    	    SQL.append("        expired_time              = ?, ");
	    	    
	    	    SQL.append("        end_time                  = ?, ");
	    	    SQL.append("        parent_processinstance_id = ?, ");
	    	    SQL.append("        parent_taskinstance_id    = ?  ");
	    	    SQL.append("WHERE   ID                        =?");
	    	    
	    	    processInstanceId = processInstance.getId();
	    	    
	    	    super.getJdbcTemplate().update(SQL.toString()
	    	    		,new Object[]{
	    	    	processInstance.getProcessId(),
	    	    	processInstance.getVersion(),
	    	    	processInstance.getName(),
	    	    	processInstance.getDisplayName(),
	    	    	processInstance.getState(),
	    	    	
	    	    	processInstance.isSuspended()==true?1:0,
	    	    	processInstance.getCreatorId(),
	    	    	getSqlDate(processInstance.getCreatedTime()),
	    	    	getSqlDate(processInstance.getStartedTime()),	    	    	
	    	    	getSqlDate(processInstance.getExpiredTime()),
	    	    	
	    	    	getSqlDate(processInstance.getEndTime()),	  
	    	    	processInstance.getParentProcessInstanceId(),
	    	    	processInstance.getParentTaskInstanceId(),
	    	    	processInstance.getId()
	    	    	
	    	    },new int[]{
	    	    	Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.INTEGER,
	    	    	Types.INTEGER,Types.VARCHAR,Types.TIME,Types.TIME,Types.TIME,
	    	    	
	    	    	Types.TIME,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR
	    	    });
	    	}
	    	//操作流程实例变量
	    	Map<String,Object> map = processInstance.getProcessInstanceVariables();
	    	
	    	if(map!=null && map.keySet().size()>0){//map不为空，且map中有值
	   
	    		Iterator<String> iterator = map.keySet().iterator();

	    		while(iterator.hasNext()){
		    		String name = iterator.next();
		    		Object value = map.get(name);
		    		saveOrUpdateProcessInstanceVar(processInstanceId,name,value.getClass().getName()+"#"+value);
	    		}
	    	}
	    	
	    }
	    
	    /**
	     * 保存或者更新流程实例变量
	     * @param processInstanceId
	     * @param name
	     * @param value
	     */
	    private void saveOrUpdateProcessInstanceVar(String processInstanceId,String name,Object value){
	    	//首先根据processINstanceId和name查询是否存在对应的记录，不存在插入，存在更新
	    	if(super.getJdbcTemplate().queryForInt("select count(*) from t_ff_rt_procinst_var where processinstance_id=? and name=? "
	    			,new Object[]{processInstanceId,name} ,new int[] {Types.VARCHAR,Types.VARCHAR})==0){
	    		//插入
	    		try{
		       		super.getJdbcTemplate().update("insert into t_ff_rt_procinst_var(processinstance_id,name,value )values (?,?,?)"
		    	    		,new Object[]{processInstanceId,name,value});
	    		}catch(Exception e){
	    			e.printStackTrace();
	    			//TODO  先插入，然后再查询，然后在插入，这个时候事务并没有提交，查询的结果==0为什么？
	    		}
	    	}else{
	    		//更新
	       		super.getJdbcTemplate().update("update t_ff_rt_procinst_var set value=? where processinstance_id=? and name=? "
	    	    		,new Object[]{value,processInstanceId,name});	    		
	    	}
	    }
	    
	    //更新任务实例表
	    
	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#saveTaskInstance(org.fireflow.engine.ITaskInstance)
	     */
	    public void saveOrUpdateTaskInstance(ITaskInstance taskInstance) {

	    	//TODO wmj2003 taskinstance中的biz_type从何而来？既没有set也没有get，那么只能够不操作这个字段
	    	//can_be_withdrawn  是不是也从来没有被用到过？
	    	String taskInstanceId = null;
	    	if(taskInstance.getId()==null || taskInstance.getId().trim().equals("")){

		    	StringBuffer SQL = new StringBuffer();
		    	SQL.append("INSERT ");
		    	SQL.append("INTO    t_ff_rt_taskinstance ");
		    	SQL.append("        ( ");
		    	SQL.append("                id                 , ");
		    	SQL.append("                biz_type           , ");//从哪里获取？
		    	SQL.append("                task_id            , ");
		    	SQL.append("                activity_id        , ");
		    	SQL.append("                name               , ");
		    	
		    	SQL.append("                display_name       , ");
		    	SQL.append("                state              , ");
		    	SQL.append("                suspended          , ");
		    	SQL.append("                task_type          , ");
		    	SQL.append("                created_time       , ");
		    	
		    	SQL.append("                started_time       , ");
		    	SQL.append("                expired_time       , ");
		    	SQL.append("                end_time           , ");
		    	SQL.append("                assignment_strategy, ");
		    	SQL.append("                processinstance_id , ");
		    	
		    	SQL.append("                process_id         , ");		    	
		    	SQL.append("                version            , ");
		    	SQL.append("                target_activity_id   , ");
		    	SQL.append("                from_activity_id   , ");
		    	SQL.append("                step_number        , ");
		    	
		    	SQL.append("                can_be_withdrawn ");
		    	SQL.append("        ) ");
		    	SQL.append("        VALUES ");
		    	SQL.append("        ( ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,?  )");
		    	taskInstanceId = java.util.UUID.randomUUID().toString().replace("-", "");
		    	
		    	((TaskInstance)taskInstance).setId(taskInstanceId);//给taskinstance设置id
		    	super.getJdbcTemplate().update(SQL.toString(),new Object[]{
		    		taskInstanceId,
		    		"",//TODO  biz_type 暂时设置为空
		    		taskInstance.getTaskId(),
		    		taskInstance.getActivityId(),
		    		taskInstance.getName(),
		    		
		    		taskInstance.getDisplayName(),		    		
		    		taskInstance.getState(),
		    		taskInstance.isSuspended()==true?1:0,
    				taskInstance.getTaskType(),
	    	    	getSqlDate(taskInstance.getCreatedTime()),
	    	    	
	    	    	getSqlDate(taskInstance.getStartedTime()),	    	    	
	    	    	getSqlDate(taskInstance.getExpiredTime()),
	    	    	getSqlDate(taskInstance.getEndTime()), 
	    	    	taskInstance.getAssignmentStrategy(),
    				taskInstance.getProcessInstanceId(),
    		
    				taskInstance.getProcessId(),		    		
    				taskInstance.getVersion(),		
    				taskInstance.getTargetActivityId(),
    				((TaskInstance)taskInstance).getFromActivityId(), 
    				taskInstance.getStepNumber(),
    				
    				((TaskInstance)taskInstance).getCanBeWithdrawn()==true?1:0
		    	},new int[]{
		    		Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,
	    	    	
	    	    	Types.VARCHAR,Types.INTEGER,Types.INTEGER,Types.VARCHAR,Types.TIME,
	    	    	
	    	    	Types.TIME,Types.TIME,Types.TIME,Types.VARCHAR,Types.VARCHAR,
	    	    	
	    	    	Types.VARCHAR,Types.INTEGER,Types.VARCHAR,Types.VARCHAR,Types.INTEGER,
	    	    	
	    	    	Types.INTEGER	    	
	    	    	
	    	    });
	    	}else{
	    		StringBuffer SQL = new StringBuffer();
	    		SQL.append("UPDATE t_ff_rt_taskinstance ");
	    		SQL.append("SET  ");
	    		SQL.append("        biz_type            = ?, ");
	    		SQL.append("        task_id             = ?, ");
	    		SQL.append("        activity_id         = ?, ");
	    		SQL.append("        name                = ?, ");
	    		
	    		SQL.append("        display_name        = ?, ");
	    		SQL.append("        state               = ?, ");
	    		SQL.append("        suspended           = ?, ");
	    		SQL.append("        task_type           = ?, ");
	    		SQL.append("        created_time        = ?, ");
	    		
	    		SQL.append("        started_time        = ?, ");
	    		SQL.append("       	expired_time        = ?, ");
	    		SQL.append("        end_time            = ?, ");
	    		SQL.append("        assignment_strategy = ?, ");
	    		SQL.append("        processinstance_id  = ?, ");
	    		
	    		SQL.append("        process_id          = ?, ");	    		
	    		SQL.append("        version             = ?, ");
	    		SQL.append("        target_activity_id  = ?, ");
	    		SQL.append("        from_activity_id    = ?, ");
	    		SQL.append("        step_number         = ?, ");
	    		
	    		SQL.append("        can_be_withdrawn    = ? ");
	    		
	    		SQL.append("WHERE   ID                  = ? ");
	    		
	    		super.getJdbcTemplate().update(SQL.toString(),new Object[]{
	    			"",
	    			taskInstance.getTaskId(),
	    			taskInstance.getActivityId(),
	    			taskInstance.getName(),
		    		
	    			taskInstance.getDisplayName(),		    		
	    			taskInstance.getState(),
	    			taskInstance.isSuspended()==true?1:0,
					taskInstance.getTaskType(),
					getSqlDate(taskInstance.getCreatedTime()),
	    	    	
	    	    	getSqlDate(taskInstance.getStartedTime()),	    	    	
	    	    	getSqlDate(taskInstance.getExpiredTime()),
	    	    	getSqlDate(taskInstance.getEndTime()), 
					taskInstance.getAssignmentStrategy(),
					taskInstance.getProcessInstanceId(),
	
					taskInstance.getProcessId(),		    		
					taskInstance.getVersion(),		
					taskInstance.getTargetActivityId(), 
					((TaskInstance)taskInstance).getFromActivityId(),
					taskInstance.getStepNumber(),
    		
					((TaskInstance)taskInstance).getCanBeWithdrawn()==true?1:0,
					taskInstance.getId()
		    	},new int[]{
	    			Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,
	    	    	
	    	    	Types.VARCHAR,Types.INTEGER,Types.INTEGER,Types.VARCHAR,Types.TIME,
	    	    	
	    	    	Types.TIME,Types.TIME,Types.TIME,Types.VARCHAR,Types.VARCHAR,
	    	    	
	    	    	Types.VARCHAR,Types.INTEGER,Types.VARCHAR,Types.VARCHAR,Types.INTEGER,
	    	    	
	    	    	Types.INTEGER,	Types.VARCHAR    	
	    	    	
	    	    });
	    	}
	    	
	    }


	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#saveOrUpdateWorkItem(org.fireflow.engine.IWorkItem)
	     */
	    public void saveOrUpdateWorkItem(IWorkItem workitem) {
	    	String workItemId = null;
	    	if(workitem.getId()==null  || workitem.getId().trim().equals("")){
	    		StringBuffer SQL = new StringBuffer();
	    		SQL.append("INSERT ");
	    		SQL.append("INTO    t_ff_rt_workitem ");
	    		SQL.append("        ( ");
	    		SQL.append("                id          , ");
	    		SQL.append("                state       , ");
	    		SQL.append("                created_time, ");
	    		SQL.append("                claimed_time, ");
	    		SQL.append("                end_time    , ");
	    		
	    		SQL.append("                actor_id    , ");
	    		SQL.append("                taskinstance_id, ");
	    		SQL.append("                comments ");
	    		SQL.append("        ) ");
	    		SQL.append("        VALUES ");
	    		SQL.append("        ( ?,?,?,?,? ,?,?,?)");
	    		workItemId = java.util.UUID.randomUUID().toString().replace("-", "");
	    		((WorkItem)workitem).setId(workItemId);
	    		super.getJdbcTemplate().update(SQL.toString(),new Object[]{
	    			workItemId,
	    			workitem.getState(),
	    			getSqlDate(workitem.getCreatedTime()),	    	    	
	    	    	getSqlDate(workitem.getClaimedTime()),	   
	    	    	getSqlDate(workitem.getEndTime()), 	 
	
	    			
	    			workitem.getActorId(),
	    			workitem.getTaskInstance().getId(),
	    			workitem.getComments()
		    	},new int[]{
	    	    	Types.VARCHAR,Types.INTEGER,Types.TIME,Types.TIME,Types.TIME,
	    	    	
	    	    	Types.VARCHAR,Types.VARCHAR,Types.VARCHAR
	    	    	
	    	    });
	    		
	    	}else{
	    		StringBuffer SQL = new StringBuffer();
	    		SQL.append("UPDATE t_ff_rt_workitem ");
	    		SQL.append("SET     state           = ?, ");
	    		SQL.append("        created_time    = ?, ");
	    		SQL.append("        claimed_time    = ?, ");
	    		SQL.append("        end_time        = ?, ");
	    		SQL.append("        actor_id        = ?, ");
	    		SQL.append("        taskinstance_id = ?, ");
	    		SQL.append("        comments = ? ");
	    		SQL.append("WHERE   ID              = ? ");
	    		super.getJdbcTemplate().update(SQL.toString(),new Object[]{
	    			workitem.getState(),
	    			getSqlDate(workitem.getCreatedTime()),	    	    	
	    	    	getSqlDate(workitem.getClaimedTime()),	   
	    	    	getSqlDate(workitem.getEndTime()), 	
	    			
	    			workitem.getActorId(),
	    			workitem.getTaskInstance().getId(),
	    			workitem.getComments(),
	    			workitem.getId()
		    	},new int[]{
	    	    	Types.INTEGER,Types.TIME,Types.TIME,Types.TIME,
	    	    	
	    	    	Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR
	    	    	
	    	    });
	    	}
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#saveOrUpdateToken(org.fireflow.kernel.IToken)
	     */
	    public void saveOrUpdateToken(IToken token) {
	    	String tokenId = null;
	    	if(token.getId()==null || token.getId().trim().equals("")){
	    		StringBuffer SQL = new StringBuffer();
	    		SQL.append("INSERT ");
	    		SQL.append("INTO    t_ff_rt_token ");
	    		SQL.append("        ( ");
	    		SQL.append("                id                , ");
	    		SQL.append("                alive             , ");
	    		SQL.append("                value             , ");
	    		SQL.append("                node_id           , ");
	    		SQL.append("                processinstance_id, ");
	    		
	    		SQL.append("                step_number       , ");
	    		SQL.append("                from_activity_id ");
	    		SQL.append("        ) ");
	    		SQL.append("        VALUES ");
	    		SQL.append("        ( ?,?,?,?,? ,?,? )");
	    		tokenId = java.util.UUID.randomUUID().toString().replace("-", "");
	    		((Token)token).setId(tokenId);
	    		super.getJdbcTemplate().update(SQL.toString(),new Object[]{
	    			tokenId,
	    			token.isAlive()==true?1:0,
	    			token.getValue(),
	    			token.getNodeId(),
	    			token.getProcessInstanceId(),
	    			
	    			token.getStepNumber(),
	    			token.getFromActivityId()
		    	},new int[]{
	    			Types.VARCHAR,Types.INTEGER,Types.INTEGER,Types.VARCHAR,Types.VARCHAR,
	    	    	
	    	    	Types.INTEGER,Types.VARCHAR
	    	    	
	    	    });
	    	}else{
	    		StringBuffer SQL = new StringBuffer();
	    		SQL.append("UPDATE t_ff_rt_token   ");
	    		SQL.append("SET     alive				= ?, ");
	    		SQL.append("        value   			= ?, ");
	    		SQL.append("        node_id    			= ?, ");
	    		SQL.append("        processinstance_id	= ?, ");
	    		SQL.append("        step_number        	= ?, ");
	    		
	    		SQL.append("        from_activity_id 	= ?  ");
	    		SQL.append("WHERE   ID = ? ");
	    		
	    		super.getJdbcTemplate().update(SQL.toString(),new Object[]{

	    			token.isAlive()==true?1:0,
	    			token.getValue(),
	    			token.getNodeId(),
	    			token.getProcessInstanceId(),
	    			token.getStepNumber(),
	    			
	    			token.getFromActivityId(),	    			
	    			token.getId()
		    	},new int[]{
	    			Types.INTEGER,Types.INTEGER,Types.VARCHAR,Types.VARCHAR,Types.INTEGER,
	    			
	    			Types.VARCHAR,Types.VARCHAR
	    	    	
	    	    });
	    	}
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#getAliveTokenCountForNode(java.lang.String, java.lang.String)
	     */
	    public Integer getAliveTokenCountForNode(final String processInstanceId, final String nodeId) {
	    	
	    	int result = super.getJdbcTemplate().queryForInt("select count(*) from T_FF_RT_TOKEN where alive=1 and processinstance_id=? and node_id =?",
	    			new Object[]{processInstanceId,nodeId});
	    	return new Integer(result);
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#getCompletedTaskInstanceCountForTask(java.lang.String, java.lang.String)
	     */
	    public Integer getCompletedTaskInstanceCountForTask(final String processInstanceId, final String taskId) {
	    	int result = super.getJdbcTemplate().queryForInt("select count(*) from T_FF_RT_TASKINSTANCE where state="+ITaskInstance.COMPLETED
	    			+" and task_id=? and processinstance_id=? ",new Object[]{
	    			taskId,processInstanceId
	    	});
	    	return new Integer(result);	    	

	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#getAliveTaskInstanceCountForActivity(java.lang.String, java.lang.String)
	     */
	    public Integer getAliveTaskInstanceCountForActivity(final String processInstanceId, final String activityId) {
	    	int result = super.getJdbcTemplate().queryForInt("select count(*) from T_FF_RT_TASKINSTANCE where " +
	    			" (state="+ITaskInstance.INITIALIZED +" or state="+ITaskInstance.RUNNING +")"
	    			+" and activity_id=? and processinstance_id=? ",new Object[]{
	    			activityId,processInstanceId
	    	});
	    	return new Integer(result);	  
	    }


	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findTaskInstancesForProcessInstance(java.lang.String, java.lang.String)
	     */
	    @SuppressWarnings("unchecked")
		public List<ITaskInstance> findTaskInstancesForProcessInstance(final java.lang.String processInstanceId,
	            final String activityId) {
	    	
	    	StringBuffer sb = new StringBuffer("");
	    	sb.append(" select * from t_ff_rt_taskinstance ");
	    	sb.append(" where processinstance_id=? ");
            if (activityId != null && !activityId.trim().equals("")) {
                sb.append("  and activity_id=? ");
                return super.getJdbcTemplate().query(sb.toString(),new Object[]{processInstanceId,activityId}, new TaskInstanceRowMapper() );
            }else{
            	return super.getJdbcTemplate().query(sb.toString(),new Object[]{processInstanceId}, new TaskInstanceRowMapper() );
            }
           
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findTaskInstancesForProcessInstanceByStepNumber(java.lang.String, java.lang.Integer)
	     */
	    @SuppressWarnings("unchecked")
		public List<ITaskInstance> findTaskInstancesForProcessInstanceByStepNumber(final String processInstanceId, final Integer stepNumber) {
	    	
	    	StringBuffer sb = new StringBuffer("");
	    	sb.append(" select * from t_ff_rt_taskinstance ");
	    	sb.append(" where processinstance_id=? ");
            if (stepNumber != null ) {
                sb.append("  and step_number=? ");
                return super.getJdbcTemplate().query(sb.toString(),new Object[]{processInstanceId,stepNumber}, new TaskInstanceRowMapper() );
            }else{
            	return super.getJdbcTemplate().query(sb.toString(),new Object[]{processInstanceId}, new TaskInstanceRowMapper() );
            }
           
	    }
	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#lockTaskInstance(java.lang.String)
	     */
	    public void lockTaskInstance(String taskInstanceId){
	    	//TODO  这里使用的是sqlserver 的代码
	    	String sql = "select * from t_ff_rt_taskinstance with (updlock, rowlock) where id=? ";
	    	super.getJdbcTemplate().queryForObject(sql,new Object[]{taskInstanceId}, new TaskInstanceRowMapper() );
	    }
	    
	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findTokenById(java.lang.String)
	     */
	    public IToken findTokenById(String id) {
	    	String sql = "select * from t_ff_rt_token where id=? ";
	    	return (IToken)super.getJdbcTemplate().queryForObject(sql,new Object[]{id}, new TokenRowMapper() );
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#deleteTokensForNodes(java.lang.String, java.util.List)
	     */
	    public void deleteTokensForNodes(final String processInstanceId, final List<String> nodeIdsList) {
	    	
	    	super.getJdbcTemplate().batchUpdate(
	                "delete from t_ff_rt_token where processinstance_id = ? and node_id=? ",
	                new BatchPreparedStatementSetter() {
	                    public void setValues(PreparedStatement ps, int i) throws SQLException {
	                        ps.setString(1, processInstanceId);
	                        ps.setString(2, nodeIdsList.get(i));
	                    }

	                    public int getBatchSize() {
	                        return nodeIdsList.size();
	                    }
	                } );
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#deleteTokensForNode(java.lang.String, java.lang.String)
	     */
	    public void deleteTokensForNode(final String processInstanceId, final String nodeId) {
	    	String sql = "delete from t_ff_rt_token where processinstance_id = ? and node_id=? ";
	    	super.getJdbcTemplate().update(sql,new Object[]{processInstanceId,nodeId});
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#deleteToken(org.fireflow.kernel.IToken)
	     */
	    public void deleteToken(IToken token) {
	    	String sql = "delete from t_ff_rt_token where id=? ";
	    	super.getJdbcTemplate().update(sql,new Object[]{token.getId()});
	    }


	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findTokensForProcessInstance(java.lang.String, java.lang.String)
	     */
	    @SuppressWarnings("unchecked")
		public List<IToken> findTokensForProcessInstance(final String processInstanceId, final String nodeId) {
	    	String sql = "select * from t_ff_rt_token where processinstance_id=? ";
            if (nodeId != null && !nodeId.trim().equals("")) {
                sql += " and node_id=? ";
                return super.getJdbcTemplate().query(sql,new Object[]{processInstanceId,nodeId}, new TokenRowMapper() );
            }else{
            	return super.getJdbcTemplate().query(sql,new Object[]{processInstanceId}, new TokenRowMapper() );
            }

	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findWorkItemById(java.lang.String)
	     */
	    public IWorkItem findWorkItemById(String id) {
	    	String workItemSql = " select * from t_ff_rt_workitem where id=? ";

	    	WorkItem  workItem = (WorkItem)super.getJdbcTemplate().queryForObject(workItemSql,new Object[]{id}, new WorkItemRowMapper() );
	    	String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";
	    	TaskInstance taskInstance = (TaskInstance)super.getJdbcTemplate().queryForObject(taskInstanceSql,new Object[]{workItem.getTaskInstanceId()}, new TaskInstanceRowMapper() );
	    	workItem.setTaskInstance(taskInstance);
	    	return workItem;
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findAliveTaskInstanceById(java.lang.String)
	     */
	    public ITaskInstance findAliveTaskInstanceById(final String id) {
	    	String sql ="select * from t_ff_rt_taskinstance where id=? and  (state="+ITaskInstance.INITIALIZED+" or state="+ITaskInstance.RUNNING+" )";
	    	return (TaskInstance)super.getJdbcTemplate().queryForObject(sql,new Object[]{id}, new TaskInstanceRowMapper() );
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findTaskInstanceById(java.lang.String)
	     */
	    public ITaskInstance findTaskInstanceById(String id) {
	    	String sql ="select * from t_ff_rt_taskinstance where id=? ";
	    	return (TaskInstance)super.getJdbcTemplate().queryForObject(sql,new Object[]{id}, new TaskInstanceRowMapper() );
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#abortTaskInstance(org.fireflow.engine.impl.TaskInstance)
	     */
	    public void abortTaskInstance(final TaskInstance taskInstance) {
	    	String sql = "update t_ff_rt_taskinstance set state=? ,end_time=? where id=? and (state=0 or state=1)";
	    	super.getJdbcTemplate().update(sql,new Object[]{IWorkItem.CANCELED,getSqlDate(rtCtx.getCalendarService().getSysDate()),taskInstance.getId()});
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#getAliveWorkItemCountForTaskInstance(java.lang.String)
	     */
	    public Integer getAliveWorkItemCountForTaskInstance(final String taskInstanceId) {
	    	String sql ="select count(*) from t_ff_rt_workitem where taskinstance_id=? and (state=0 or state=1 or state=3)";
	    	return super.getJdbcTemplate().queryForInt(sql,new Object[]{taskInstanceId});
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findCompletedWorkItemsForTaskInstance(java.lang.String)
	     */
	    @SuppressWarnings("unchecked")
		public List<IWorkItem> findCompletedWorkItemsForTaskInstance(final String taskInstanceId) {
	    	String sql = " select * from t_ff_rt_workitem where taskinstance_id=? and state="+IWorkItem.COMPLETED ;

	    	List<IWorkItem> l =  (List<IWorkItem>)super.getJdbcTemplate().query(sql,new Object[]{taskInstanceId}, new WorkItemRowMapper());
	    	String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";
	    	TaskInstance taskInstance = (TaskInstance)super.getJdbcTemplate().queryForObject(taskInstanceSql,new Object[]{taskInstanceId}, new TaskInstanceRowMapper() );
	    	if(l==null){
	    		return null;
	    	}else{
	    		for(IWorkItem workItem:l){
	    			((WorkItem)workItem).setTaskInstance(taskInstance);
	    		}
	    		return l;
	    	}
	    	
	    }
	    
	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findWorkItemsForTaskInstance(java.lang.String)
	     */
	    @SuppressWarnings("unchecked")
		public List<IWorkItem> findWorkItemsForTaskInstance(final String taskInstanceId){
	    	String sql = " select * from t_ff_rt_workitem where taskinstance_id=? " ;

	    	List<IWorkItem> l =  (List<IWorkItem>)super.getJdbcTemplate().query(sql,new Object[]{taskInstanceId}, new WorkItemRowMapper());
	    	String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";
	    	TaskInstance taskInstance = (TaskInstance)super.getJdbcTemplate().queryForObject(taskInstanceSql,new Object[]{taskInstanceId}, new TaskInstanceRowMapper() );
	    	if(l==null){
	    		return null;
	    	}else{
	    		for(IWorkItem workItem:l){
	    			((WorkItem)workItem).setTaskInstance(taskInstance);
	    		}
	    		return l;
	    	}
  	
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findWorkItemsForTask(java.lang.String)
	     */
	    @SuppressWarnings("unchecked")
		public List<IWorkItem> findWorkItemsForTask(final String taskid) {
	    	String sql = " select a.* from t_ff_rt_workitem a,t_ff_rt_taskinstance b where a.taskinstance_id=b.id and b.task_id=? " ;
	    	List<IWorkItem> l =  (List<IWorkItem>)super.getJdbcTemplate().query(sql,new Object[]{taskid}, new WorkItemRowMapper());
	    	if(l==null){
	    		return null;
	    	}else{
	    		String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

	    		for(IWorkItem workItem:l){
			    	TaskInstance taskInstance = (TaskInstance)super.getJdbcTemplate().queryForObject(taskInstanceSql,new Object[]{((WorkItem)workItem).getTaskInstanceId()}, new TaskInstanceRowMapper() );
			    		    			
	    			((WorkItem)workItem).setTaskInstance(taskInstance);
	    		}
	    		return l;
	    	}
	    }

	    private Map<String,Object> getVarMap(String processInstanceId) {
	    	String varSql  = "select * from t_ff_rt_procinst_var where processinstance_id=? ";
	    	List l = super.getJdbcTemplate().query(varSql,new Object[]{processInstanceId},new ProcessInstanceVarRowMapper());
	    	
	    	Map<String,Object> resultMap = new HashMap<String,Object>();
	    	
	    	if(l!=null && l.size()>0){//map不为空，且map中有值
	    		int LEN = l.size();
	    		for(int i=0;i<LEN;i++){
	    			ProcessInstanceVar  processInstanceVar =  (ProcessInstanceVar)l.get(i);
		    		
		    		resultMap.put(processInstanceVar.getName(), getObject(processInstanceVar.getValue()));
		    		if(log.isDebugEnabled()){
		    			log.debug(processInstanceVar.getName()+"="+processInstanceVar.getValue());
		    		}
	    		}
	    	}else{
	    		if(log.isDebugEnabled()){
	    			log.debug("流程实例"+processInstanceId+"获取到的流程变量为空！");
	    		}
	    	}
	    	
	    	return resultMap;
	    }
	    
	    public static void main(String[] args){
	    	PersistenceServiceSpringJdbcImpl t = new PersistenceServiceSpringJdbcImpl();
	    	System.out.println(t.getObject("java.lang.String#000100"));
	    	System.out.println(t.getSqlDate(new java.util.Date()));
	    	//测试通过后，需要将这两个方法设置为private类型。
	    }
	    
	    public Object getObject(String value){
	    	if (value == null)
				return null;
			int index = value.indexOf("#");
			if (index == -1) {
				return null;
			}
			String type = value.substring(0, index);
			String strValue = value.substring(index + 1);
			if (type.equals(String.class.getName())) {
				return strValue;
			}
			if (strValue == null || strValue.trim().equals("")) {
				return null;
			}
			if (type.equals(Integer.class.getName())) {
				return new Integer(strValue);
			} else if (type.equals(Long.class.getName())) {
				return new Long(strValue);
			} else if (type.equals(Float.class.getName())) {
				return new Float(strValue);
			} else if (type.equals(Double.class.getName())) {
				return new Double(strValue);
			} else if (type.equals(Boolean.class.getName())) {
				return new Boolean(strValue);
			} else if (type.equals(java.util.Date.class.getName())) {
				return new java.util.Date(new Long(strValue));
			} else {
				throw new HibernateException("Fireflow不支持数据类型" + type);
			}
	    }
	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findProcessInstancesByProcessId(java.lang.String)
	     */
	    @SuppressWarnings("unchecked")
		public List<IProcessInstance> findProcessInstancesByProcessId(final String processId) {
	    	
	    	String sql = "select * from t_ff_rt_processinstance where process_id=? order by created_time ";
	    	List<IProcessInstance> l = new ArrayList<IProcessInstance>();
	    	l = (List<IProcessInstance> )super.getJdbcTemplate().query(sql,new Object[]{processId}, new ProcessInstanceRowMapper());
	    	if(l==null || l.size()<1){
	    		return null;
	    	}
	    	
	    	for(IProcessInstance iProcessInstance:l){
	    		iProcessInstance.setProcessInstanceVariables(getVarMap(iProcessInstance.getId()));
	    	}
	    	
	    	return l;

	    }
	    

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findProcessInstancesByProcessIdAndVersion(java.lang.String, java.lang.Integer)
	     */
	    @SuppressWarnings("unchecked")
		public List<IProcessInstance> findProcessInstancesByProcessIdAndVersion(final String processId, final Integer version) {
	  
	    	String sql = "select * from t_ff_rt_processinstance where process_id=? and version=? order by created_time ";
	    	List<IProcessInstance> l = new ArrayList<IProcessInstance>();
	    	l = (List<IProcessInstance> ) super.getJdbcTemplate().query(sql,new Object[]{processId,version}, new ProcessInstanceRowMapper());
	    	if(l==null || l.size()<1){
	    		return null;
	    	}
	    	
	    	for(IProcessInstance iProcessInstance:l){
	    		iProcessInstance.setProcessInstanceVariables(getVarMap(iProcessInstance.getId()));
	    	}
	    	
	    	return l;

	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findProcessInstanceById(java.lang.String)
	     */
	    public IProcessInstance findProcessInstanceById(String id) {
	    	String sql = "select * from t_ff_rt_processinstance where id=?  ";
	    	
	    	IProcessInstance iProcessInstance = (IProcessInstance) super.getJdbcTemplate().queryForObject(sql,new Object[]{id}, new ProcessInstanceRowMapper());
	    	iProcessInstance.setProcessInstanceVariables(getVarMap(iProcessInstance.getId()));
	    	return iProcessInstance;
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findAliveProcessInstanceById(java.lang.String)
	     */
	    public IProcessInstance findAliveProcessInstanceById(final String id) {
	
	    	String sql = "select * from t_ff_rt_processinstance where id=? and ( state="+IProcessInstance.INITIALIZED 
	    	+" or state="+IProcessInstance.RUNNING+")";
	    	
	    	IProcessInstance iProcessInstance = (IProcessInstance) super.getJdbcTemplate().queryForObject(sql,new Object[]{id}, new ProcessInstanceRowMapper());
	    	
	    	iProcessInstance.setProcessInstanceVariables(getVarMap(iProcessInstance.getId()));
	    	return iProcessInstance;
	    }

	    private LobHandler lobHandler;  //用来操作lob大字段
	    public LobHandler getLobHandler() { 
	      return lobHandler; 
	    } 
	    public void setLobHandler(LobHandler lobHandler) { 
	      this.lobHandler = lobHandler; 
	    } 


//	    <bean id="lobHandler"  
//	    	class="org.springframework.jdbc.support.lob.DefaultLobHandler"  
//	    	lazy-init="true"/> 
	    	 

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#saveOrUpdateWorkflowDefinition(org.fireflow.engine.definition.WorkflowDefinition)
	     */
	    public void saveOrUpdateWorkflowDefinition(final WorkflowDefinition workflowDef) {

	        if (workflowDef.getId() == null || workflowDef.getId().equals("")) {
	            Integer latestVersion = findTheLatestVersionNumberIgnoreState(workflowDef.getProcessId());
	            if (latestVersion != null) {
	            	workflowDef.setVersion(new Integer(latestVersion.intValue() + 1));
	            } else {
	            	workflowDef.setVersion(new Integer(1));
	            }
	        }

	       // this.getHibernateTemplate().saveOrUpdate(workflowDef);
	        
	        if(workflowDef.getId()==null){
	        	StringBuffer sql = new StringBuffer();
	        	sql.append(" INSERT INTO t_ff_df_workflowdef(" );
	        	sql.append("id,definition_type,process_id,name,display_name,");
	        	sql.append("description,version,state,upload_user,upload_time,");
	        	sql.append("publish_user,publish_time,process_content )") ;
	        	sql.append(" VALUES(?,?,?,?,?, ?,?,?,?,?, ?,?,?)"); 
	            super.getJdbcTemplate().execute(sql.toString(), 
	              new AbstractLobCreatingPreparedStatementCallback(this.lobHandler) { 
	                  protected void setValues(PreparedStatement ps,LobCreator lobCreator) 
	                              throws SQLException { 
	                    ps.setString(1, java.util.UUID.randomUUID().toString().replace("-", ""));
	                    ps.setString(2,workflowDef.getDefinitionType());
	                    ps.setString(3,workflowDef.getProcessId());
	                    ps.setString(4,workflowDef.getName());
	                    ps.setString(5,workflowDef.getDisplayName());
	                    
	                    ps.setString(6,workflowDef.getDescription());
	                    ps.setInt(7,workflowDef.getVersion());
	                    ps.setInt(8, workflowDef.getState()==true?1:0);
	                    ps.setString(9,workflowDef.getUploadUser());
	                    ps.setDate(10,(java.sql.Date)workflowDef.getUploadTime());

	                    ps.setString(11,workflowDef.getPublishUser());
	                    ps.setDate(12,(java.sql.Date)workflowDef.getPublishTime());
	                    lobCreator.setClobAsString(ps, 13, workflowDef.getProcessContent()); 
	                  } 
	                }); 

	        }else{
	        	StringBuffer sql = new StringBuffer();
	        	sql.append(" update t_ff_df_workflowdef " );
	        	sql.append("set definition_type=?,process_id=?,name=?,display_name=?,");
	        	sql.append("description=?,version=?,state=?,upload_user=?,upload_time=?,");
	        	sql.append("publish_user=?,publish_time=?,process_content=? ") ;
	        	sql.append(" where id=? "); 
	            super.getJdbcTemplate().execute(sql.toString(), 
	              new AbstractLobCreatingPreparedStatementCallback(this.lobHandler) { 
	                  protected void setValues(PreparedStatement ps,LobCreator lobCreator) 
	                              throws SQLException { 
	                	  
	                    ps.setString(1,workflowDef.getDefinitionType());
	                    ps.setString(2,workflowDef.getProcessId());
	                    ps.setString(3,workflowDef.getName());
	                    ps.setString(4,workflowDef.getDisplayName());
	                    
	                    ps.setString(5,workflowDef.getDescription());
	                    ps.setInt(6,workflowDef.getVersion());
	                    ps.setInt(7, workflowDef.getState()==true?1:0);
	                    ps.setString(8,workflowDef.getUploadUser());
	                    ps.setDate(9,(java.sql.Date)workflowDef.getUploadTime());

	                    ps.setString(10,workflowDef.getPublishUser());
	                    ps.setDate(11,(java.sql.Date)workflowDef.getPublishTime());
	                    lobCreator.setClobAsString(ps, 12, workflowDef.getProcessContent()); 
	                    
	                    ps.setString(13, workflowDef.getId());
	                  } 
	                }); 
	        }
	        
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findTheLatestVersionNumber(java.lang.String)
	     */
	    public Integer findTheLatestVersionNumber(final String processId) {
	    	String sql =" select max(version) from t_ff_df_workflowdf where process_id=? and state=1 ";
	    	return super.getJdbcTemplate().queryForInt(sql,new Object[]{processId});

	    }
	    
	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findTheLatestVersionNumberIgnoreState(java.lang.String)
	     */
	    public Integer findTheLatestVersionNumberIgnoreState(final String processId){
	    	String sql =" select max(version) from t_ff_df_workflowdf where process_id=? ";
	    	return super.getJdbcTemplate().queryForInt(sql,new Object[]{processId});  	
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findWorkflowDefinitionById(java.lang.String)
	     */
	    public WorkflowDefinition findWorkflowDefinitionById(String id) {
	    	String sql =" select * from t_f_df_workflowdf where id=? ";
	    	
	    	return (WorkflowDefinition)super.getJdbcTemplate().queryForObject(sql,new Object[]{id},new  RowMapper() { 
	            public Object mapRow(ResultSet rs, int rowNum) throws SQLException { 
 
					WorkflowDefinition workFlowDefinition  = new WorkflowDefinition();
					workFlowDefinition.setId(rs.getString("id"));
					workFlowDefinition.setDefinitionType(rs.getString("definition_type"));
					workFlowDefinition.setProcessId(rs.getString("process_id"));
					workFlowDefinition.setName(rs.getString("name"));
					workFlowDefinition.setDisplayName(rs.getString("display_name"));
					
					workFlowDefinition.setDescription(rs.getString("description"));
					workFlowDefinition.setVersion(rs.getInt("version"));
					workFlowDefinition.setState(rs.getInt("state")==1?true:false);
					workFlowDefinition.setUploadUser(rs.getString("upload_user"));
					workFlowDefinition.setUploadTime(rs.getDate("upload_time"));
		
					workFlowDefinition.setPublishUser(rs.getString("publish_user"));
					workFlowDefinition.setPublishTime(rs.getDate("publish_time"));
					//读取blob大字段
					workFlowDefinition.setProcessContent(lobHandler.getClobAsString(rs, "process_content"));
					return workFlowDefinition;
			    	}
	            }); 

	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findWorkflowDefinitionByProcessIdAndVersionNumber(java.lang.String, int)
	     */
	    public WorkflowDefinition findWorkflowDefinitionByProcessIdAndVersionNumber(final String processId, final int version) {
	    	

	    	String sql =" select * from t_f_df_workflowdf where process_id=? and version=? ";
	    	
	    	return (WorkflowDefinition)super.getJdbcTemplate().queryForObject(sql,new Object[]{processId,version},new  RowMapper() { 
	            public Object mapRow(ResultSet rs, int rowNum) throws SQLException { 
 
					WorkflowDefinition workFlowDefinition  = new WorkflowDefinition();
					workFlowDefinition.setId(rs.getString("id"));
					workFlowDefinition.setDefinitionType(rs.getString("definition_type"));
					workFlowDefinition.setProcessId(rs.getString("process_id"));
					workFlowDefinition.setName(rs.getString("name"));
					workFlowDefinition.setDisplayName(rs.getString("display_name"));
					
					workFlowDefinition.setDescription(rs.getString("description"));
					workFlowDefinition.setVersion(rs.getInt("version"));
					workFlowDefinition.setState(rs.getInt("state")==1?true:false);
					workFlowDefinition.setUploadUser(rs.getString("upload_user"));
					workFlowDefinition.setUploadTime(rs.getDate("upload_time"));
		
					workFlowDefinition.setPublishUser(rs.getString("publish_user"));
					workFlowDefinition.setPublishTime(rs.getDate("publish_time"));
					//读取blob大字段
					workFlowDefinition.setProcessContent(lobHandler.getClobAsString(rs, "process_content"));
					return workFlowDefinition;
			    	}
	            }); 
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findTheLatestVersionOfWorkflowDefinitionByProcessId(java.lang.String)
	     */
	    public WorkflowDefinition findTheLatestVersionOfWorkflowDefinitionByProcessId(String processId) {
	        Integer latestVersion = this.findTheLatestVersionNumber(processId);
	        return this.findWorkflowDefinitionByProcessIdAndVersionNumber(processId, latestVersion);
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findWorkflowDefinitionsByProcessId(java.lang.String)
	     */
	    @SuppressWarnings("unchecked")
		public List<WorkflowDefinition> findWorkflowDefinitionsByProcessId(final String processId) {
	    	String sql =" select * from t_f_df_workflowdf where process_id=?  ";
	    	
	    	return super.getJdbcTemplate().query(sql,new Object[]{processId},new  RowMapper() { 
	            public Object mapRow(ResultSet rs, int rowNum) throws SQLException { 
 
					WorkflowDefinition workFlowDefinition  = new WorkflowDefinition();
					workFlowDefinition.setId(rs.getString("id"));
					workFlowDefinition.setDefinitionType(rs.getString("definition_type"));
					workFlowDefinition.setProcessId(rs.getString("process_id"));
					workFlowDefinition.setName(rs.getString("name"));
					workFlowDefinition.setDisplayName(rs.getString("display_name"));
					
					workFlowDefinition.setDescription(rs.getString("description"));
					workFlowDefinition.setVersion(rs.getInt("version"));
					workFlowDefinition.setState(rs.getInt("state")==1?true:false);
					workFlowDefinition.setUploadUser(rs.getString("upload_user"));
					workFlowDefinition.setUploadTime(rs.getDate("upload_time"));
		
					workFlowDefinition.setPublishUser(rs.getString("publish_user"));
					workFlowDefinition.setPublishTime(rs.getDate("publish_time"));
					//读取blob大字段
					workFlowDefinition.setProcessContent(lobHandler.getClobAsString(rs, "process_content"));
					return workFlowDefinition;
			    	}
	            }); 
	    	

	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findAllTheLatestVersionsOfWorkflowDefinition()
	     */
	    @SuppressWarnings("unchecked")
		public List<WorkflowDefinition> findAllTheLatestVersionsOfWorkflowDefinition() {
	    	
	    	String sql = " select distinct process_id from t_ff_df_workflowdef ";
	    	List<String> l = super.getJdbcTemplate().queryForList(sql);
	    	if(l==null || l.size()<1){
	    		return null;
	    	}
	    	List<WorkflowDefinition> _result = new ArrayList<WorkflowDefinition>();
	    	for(String str:l){
	    		WorkflowDefinition wfDef = findTheLatestVersionOfWorkflowDefinitionByProcessId(str);
	    		_result.add(wfDef);
	    	}
	    	return _result;
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findTodoWorkItems(java.lang.String)
	     */
	    public List<IWorkItem> findTodoWorkItems(final String actorId) {
	    	if(actorId==null || actorId.trim().equals("")){
	    		throw new NullPointerException("工单操作员（actorId）不能为空！");
	    	}
	    	
	    	String workItemSql = " select * from t_ff_rt_workitem where  (state="
	    		+IWorkItem.INITIALIZED+" or state="+IWorkItem.RUNNING 
	    		+" ) and actor_id=?  ";
	    	

	        List<IWorkItem> l =  (List<IWorkItem>)super.getJdbcTemplate().query(workItemSql,
	        		new Object[]{actorId}, new WorkItemRowMapper());
	    	if(l==null){
	    		return null;
	    	}else{
	    		String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

	    		for(IWorkItem workItem:l){
			    	TaskInstance taskInstance = (TaskInstance)super.getJdbcTemplate().queryForObject(taskInstanceSql,new Object[]{((WorkItem)workItem).getTaskInstanceId()}, new TaskInstanceRowMapper() );
			    		    			
	    			((WorkItem)workItem).setTaskInstance(taskInstance);
	    		}
	    		return l;
	    	}
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findTodoWorkItems(java.lang.String, java.lang.String)
	     */
	    @SuppressWarnings("unchecked")
		public List<IWorkItem> findTodoWorkItems(final String actorId, final String processInstanceId) {

	    	if(actorId==null || actorId.trim().equals("")){
	    		throw new NullPointerException("工单操作员（actorId）不能为空！");
	    	}
	    	if(processInstanceId==null || processInstanceId.trim().equals("")){
	    		throw new NullPointerException("流程实例ID（processInstanceId）不能为空！");
	    	}
	    	
	    	String workItemSql = " select a.* from t_ff_rt_workitem a,t_ff_rt_taskinstance b where a.taskinstance_id=b.id and (a.state="
	    		+IWorkItem.INITIALIZED+" or a.state="+IWorkItem.RUNNING 
	    		+" ) and actor_id=? and processinstance_id=?  ";
	    	

	        List<IWorkItem> l =  (List<IWorkItem>)super.getJdbcTemplate().query(workItemSql,
	        		new Object[]{actorId,processInstanceId}, new WorkItemRowMapper());
	    	if(l==null){
	    		return null;
	    	}else{
	    		String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

	    		for(IWorkItem workItem:l){
			    	TaskInstance taskInstance = (TaskInstance)super.getJdbcTemplate().queryForObject(taskInstanceSql,new Object[]{((WorkItem)workItem).getTaskInstanceId()}, new TaskInstanceRowMapper() );
			    		    			
	    			((WorkItem)workItem).setTaskInstance(taskInstance);
	    		}
	    		return l;
	    	}
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findTodoWorkItems(java.lang.String, java.lang.String, java.lang.String)
	     */
	    @SuppressWarnings("unchecked")
		public List<IWorkItem> findTodoWorkItems(final String actorId, final String processId, final String taskId) {
	    	
	    	if(actorId==null || actorId.trim().equals("")){
	    		throw new NullPointerException("工单操作员（actorId）不能为空！");
	    	}
	    	if(processId==null || processId.trim().equals("")){
	    		throw new NullPointerException("流程ID（processId）不能为空！");
	    	}
	    	if(taskId==null || taskId.trim().equals("")){
	    		throw new NullPointerException("任务ID（taskId）不能为空！");
	    	}	    	
	    	
	    	String workItemSql = " select a.* from t_ff_rt_workitem a,t_ff_rt_taskinstance b where a.taskinstance_id=b.id and (a.state="
	    		+IWorkItem.INITIALIZED+" or a.state="+IWorkItem.RUNNING 
	    		+" ) and actor_id=? and process_id=? and task_id=?  ";
	    	

	        List<IWorkItem> l =  (List<IWorkItem>)super.getJdbcTemplate().query(workItemSql,
	        		new Object[]{actorId,processId,taskId}, new WorkItemRowMapper());
	    	if(l==null){
	    		return null;
	    	}else{
	    		String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

	    		for(IWorkItem workItem:l){
			    	TaskInstance taskInstance = (TaskInstance)super.getJdbcTemplate().queryForObject(taskInstanceSql,new Object[]{((WorkItem)workItem).getTaskInstanceId()}, new TaskInstanceRowMapper() );
			    		    			
	    			((WorkItem)workItem).setTaskInstance(taskInstance);
	    		}
	    		return l;
	    	}

	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findHaveDoneWorkItems(java.lang.String)
	     */
	    public List<IWorkItem> findHaveDoneWorkItems(final String actorId) {
	    	if(actorId==null || actorId.trim().equals("")){
	    		throw new NullPointerException("工单操作员（actorId）不能为空！");
	    	}
	    	
	    	String workItemSql = " select * from t_ff_rt_workitem where (state="
	    		+IWorkItem.COMPLETED+" or state="+IWorkItem.CANCELED 
	    		+" ) and actor_id=? ";
	    	

	        List<IWorkItem> l =  (List<IWorkItem>)super.getJdbcTemplate().query(workItemSql,
	        		new Object[]{actorId},new int[]{Types.VARCHAR}, new WorkItemRowMapper());
	    	if(l==null){
	    		return null;
	    	}else{
	    		String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

	    		for(IWorkItem workItem:l){
			    	TaskInstance taskInstance = (TaskInstance)super.getJdbcTemplate().queryForObject(taskInstanceSql,new Object[]{((WorkItem)workItem).getTaskInstanceId()}, new TaskInstanceRowMapper() );
			    		    			
	    			((WorkItem)workItem).setTaskInstance(taskInstance);
	    		}
	    		return l;
	    	}
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findHaveDoneWorkItems(java.lang.String, java.lang.String)
	     */
	    @SuppressWarnings("unchecked")
		public List<IWorkItem> findHaveDoneWorkItems(final String actorId, final String processInstanceId) {

	    	if(actorId==null || actorId.trim().equals("")){
	    		throw new NullPointerException("工单操作员（actorId）不能为空！");
	    	}
	    	if(processInstanceId==null || processInstanceId.trim().equals("")){
	    		throw new NullPointerException("流程实例ID（processInstanceId）不能为空！");
	    	}
	    	
	    	String workItemSql = " select a.* from t_ff_rt_workitem a,t_ff_rt_taskinstance b where a.taskinstance_id=b.id and (a.state="
	    		+IWorkItem.COMPLETED+" or a.state="+IWorkItem.CANCELED 
	    		+" ) and actor_id=? and processinstance_id=?  ";
	    	

	        List<IWorkItem> l =  (List<IWorkItem>)super.getJdbcTemplate().query(workItemSql,
	        		new Object[]{actorId,processInstanceId}, new WorkItemRowMapper());
	    	if(l==null){
	    		return null;
	    	}else{
	    		String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

	    		for(IWorkItem workItem:l){
			    	TaskInstance taskInstance = (TaskInstance)super.getJdbcTemplate().queryForObject(taskInstanceSql,new Object[]{((WorkItem)workItem).getTaskInstanceId()}, new TaskInstanceRowMapper() );
			    		    			
	    			((WorkItem)workItem).setTaskInstance(taskInstance);
	    		}
	    		return l;
	    	}
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findHaveDoneWorkItems(java.lang.String, java.lang.String, java.lang.String)
	     */
	    @SuppressWarnings("unchecked")
		public List<IWorkItem> findHaveDoneWorkItems(final String actorId, final String processId, final String taskId) {
	    	
	    	if(actorId==null || actorId.trim().equals("")){
	    		throw new NullPointerException("工单操作员（actorId）不能为空！");
	    	}
	    	if(processId==null || processId.trim().equals("")){
	    		throw new NullPointerException("流程ID（processId）不能为空！");
	    	}
	    	if(taskId==null || taskId.trim().equals("")){
	    		throw new NullPointerException("任务ID（taskId）不能为空！");
	    	}	    	
	    	
	    	String workItemSql = " select a.* from t_ff_rt_workitem a,t_ff_rt_taskinstance b where a.taskinstance_id=b.id and (a.state="
	    		+IWorkItem.COMPLETED+" or a.state="+IWorkItem.CANCELED 
	    		+" ) and actor_id=? and process_id=? and task_id=?  ";
	    	

	        List<IWorkItem> l =  (List<IWorkItem>)super.getJdbcTemplate().query(workItemSql,
	        		new Object[]{actorId,processId,taskId}, new WorkItemRowMapper());
	    	if(l==null){
	    		return null;
	    	}else{
	    		String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

	    		for(IWorkItem workItem:l){
			    	TaskInstance taskInstance = (TaskInstance)super.getJdbcTemplate().queryForObject(taskInstanceSql,new Object[]{((WorkItem)workItem).getTaskInstanceId()}, new TaskInstanceRowMapper() );
			    		    			
	    			((WorkItem)workItem).setTaskInstance(taskInstance);
	    		}
	    		return l;
	    	}
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#deleteWorkItemsInInitializedState(java.lang.String)
	     */
	    public void deleteWorkItemsInInitializedState(final String taskInstanceId) {
	    	String sql = " delete from t_ff_rt_workitem where taskinstance_id=? and  state="+IWorkItem.INITIALIZED;
	    	super.getJdbcTemplate().update(sql,new Object[]{taskInstanceId},new int[]{Types.VARCHAR});
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#getAliveProcessInstanceCountForParentTaskInstance(java.lang.String)
	     */
	    public Integer getAliveProcessInstanceCountForParentTaskInstance(final String taskInstanceId) {
	    	String sql =" select count(*) from t_ff_rt_processinstance where parent_taskinstance_id=? and" +
	    			" (state="+IProcessInstance.INITIALIZED +" or state="+IProcessInstance.RUNNING+")";
	    	return super.getJdbcTemplate().queryForInt(sql,new Object[]{taskInstanceId});
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#suspendProcessInstance(org.fireflow.engine.impl.ProcessInstance)
	     */
	    public void suspendProcessInstance(final ProcessInstance processInstance) {
	    	String sql = " update t_ff_rt_taskinstance set suspended=1 where processinstance_id=? ";
	    	super.getJdbcTemplate().queryForInt(sql,new Object[]{processInstance.getId()});
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#restoreProcessInstance(org.fireflow.engine.impl.ProcessInstance)
	     */
	    public void restoreProcessInstance(final ProcessInstance processInstance) {
	    	String sql = " update t_ff_rt_taskinstance set suspended=0 where processinstance_id=? ";
	    	super.getJdbcTemplate().queryForInt(sql,new Object[]{processInstance.getId()});	    
	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#abortProcessInstance(org.fireflow.engine.impl.ProcessInstance)
	     */
	    public void abortProcessInstance(final ProcessInstance processInstance) {
	    	//更新流程状态，设置为canceled
	    	Date now = rtCtx.getCalendarService().getSysDate();
	    	String processSql = " update t_ff_rt_processinstance set state="+IProcessInstance.CANCELED +",end_time=? where id=? ";
	    	super.getJdbcTemplate().update(processSql,new Object[]{getSqlDate(now),processInstance.getId()});
	    	
	    	//更新所有的任务实例状态为canceled
	    	String taskSql = " update t_ff_rt_taskinstance set state="+ITaskInstance.CANCELED+",end_time=?,can_be_withdrawn=0 "+
	    	"  where processinstance_id=? and (state=0 or state=1)";
	    	super.getJdbcTemplate().update(taskSql,new Object[]{getSqlDate(now),processInstance.getId()});
	    	//更新所有工作项的状态为canceled
	    	String workItemSql = " update t_ff_rt_workitem set state="+IWorkItem.CANCELED+",end_time=?  "
	    	+" where taskinstance_id in (select a.id  from t_ff_rt_taskinstance a,t_ff_rt_workitem b where a.id=b.taskinstance_id and a.processinstance_id=? ) and (state=0 or state=1) ";
	    	super.getJdbcTemplate().update(workItemSql,new Object[]{getSqlDate(now),processInstance.getId()});
	    	//删除所有的token
	    	String tokenSql =" delete form t_ff_rt_token where processinstance_id=?  ";
	    	super.getJdbcTemplate().update(tokenSql,new Object[]{processInstance.getId()});

	    }

	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#saveOrUpdateProcessInstanceTrace(org.fireflow.engine.impl.ProcessInstanceTrace)
	     */
	    public void saveOrUpdateProcessInstanceTrace(ProcessInstanceTrace processInstanceTrace) {
	    	String processInstanceTraceId = null;
	        if(processInstanceTrace.getId()==null){
	        	StringBuffer SQL = new StringBuffer();
	        	SQL.append("INSERT ");
	        	SQL.append("INTO    t_ff_hist_trace ");
	        	SQL.append("        ( ");
	        	SQL.append("                id                , ");
	        	SQL.append("                processinstance_id, ");
	        	SQL.append("                step_number       , ");
	        	SQL.append("                minor_number      , ");
	        	SQL.append("                type              , ");
	        	
	        	SQL.append("                edge_id           , ");
	        	SQL.append("                from_node_id      , ");
	        	SQL.append("                to_node_id ");
	        	SQL.append("        ) ");
	        	SQL.append("        VALUES ");
	        	SQL.append("        ( ");
	        	SQL.append("                ? , ");
	        	SQL.append("                ? , ");
	        	SQL.append("                ? , ");
	        	SQL.append("                ? , ");
	        	SQL.append("                ? , ");
	        	
	        	SQL.append("                ? , ");
	        	SQL.append("                ? , ");
	        	SQL.append("                ? ");
	        	SQL.append("        )");
	        	
	        	processInstanceTraceId = java.util.UUID.randomUUID().toString().replace("-", "");
	        	processInstanceTrace.setId(processInstanceTraceId);
	        	super.getJdbcTemplate().update(SQL.toString(),new Object[]{
	        		processInstanceTraceId,
	        		processInstanceTrace.getProcessInstanceId(),
	        		processInstanceTrace.getStepNumber(),
	        		processInstanceTrace.getMinorNumber(),
	        		processInstanceTrace.getType(),
	        		
	        		processInstanceTrace.getEdgeId(),	        		
	        		processInstanceTrace.getFromNodeId(),
	        		processInstanceTrace.getToNodeId()
	        	},new int[]{
	    			Types.VARCHAR,Types.VARCHAR,Types.INTEGER,Types.INTEGER,Types.VARCHAR,
	    			
	    			Types.VARCHAR,Types.VARCHAR,Types.VARCHAR
	    	    	
	    	    });
	        }else{
	        	StringBuffer SQL = new StringBuffer();
	        	SQL.append("UPDATE t_ff_hist_trace ");
	        	SQL.append("SET     processinstance_id = ?, ");
	        	SQL.append("        step_number        = ?, ");
	        	SQL.append("        minor_number       = ?, ");
	        	SQL.append("        type               = ?, ");
	        	SQL.append("        edge_id            = ?, ");
	        	
	        	SQL.append("        from_node_id       = ?, ");
	        	SQL.append("        to_node_id         = ?  ");
	        	
	        	SQL.append("WHERE   ID                 = ?  ");
	        	
	        	super.getJdbcTemplate().update(SQL.toString(),new Object[]{

	        		processInstanceTrace.getProcessInstanceId(),
	        		processInstanceTrace.getStepNumber(),
	        		processInstanceTrace.getMinorNumber(),
	        		processInstanceTrace.getType(),	        		
	        		processInstanceTrace.getEdgeId(),	 
	        		
	        		processInstanceTrace.getFromNodeId(),
	        		processInstanceTrace.getToNodeId(),
	        		processInstanceTrace.getId()
	        		
	        	},new int[]{
	    			Types.VARCHAR,Types.INTEGER,Types.INTEGER,Types.VARCHAR,Types.VARCHAR,
	    			
	    			Types.VARCHAR,Types.VARCHAR,Types.VARCHAR
	    	    	
	    	    });
	        }
	    }
	    
	    /* (non-Javadoc)
	     * @see org.fireflow.engine.persistence.IPersistenceService#findProcessInstanceTraces(java.lang.String)
	     */
	    @SuppressWarnings("unchecked")
		public List<ProcessInstanceTrace> findProcessInstanceTraces(final String processInstanceId){
	    	String sql = " select * from t_ff_hist_trace where processinstance_id=? order by step_number,minor_number ";
	    	
			return super.getJdbcTemplate().query(sql,new Object[]{processInstanceId}, new ProcessInstanceTraceRowMapper());
	    }
	}

class ProcessInstanceVar {
	private String processInstanceId;
	private String name;
	private String value;
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}

class ProcessInstanceVarRowMapper implements RowMapper{
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		ProcessInstanceVar processInstanceVar = new ProcessInstanceVar();		

		processInstanceVar.setProcessInstanceId(rs.getString("processinstance_id"));
		
		processInstanceVar.setName(rs.getString("name"));
		processInstanceVar.setValue(rs.getString("value"));
		
		return processInstanceVar;
		
	}
}

/**
 * 共14个字段
 * @author wmj2003
 *
 */
class ProcessInstanceTraceRowMapper implements RowMapper{
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		ProcessInstanceTrace processInstanceTrace = new ProcessInstanceTrace();
		
		processInstanceTrace.setId(rs.getString("id"));
		processInstanceTrace.setProcessInstanceId(rs.getString("processinstance_id"));
		processInstanceTrace.setStepNumber(rs.getInt("step_number"));
		processInstanceTrace.setMinorNumber(rs.getInt("minor_number"));
		processInstanceTrace.setType(rs.getString("type"));
    	
		processInstanceTrace.setEdgeId(rs.getString("edge_id"));
		processInstanceTrace.setFromNodeId(rs.getString("from_node_id"));
		processInstanceTrace.setToNodeId(rs.getString("to_node_id"));
		
		return processInstanceTrace;
		
	}
}

/**
 * 共14个字段
 * @author wmj2003
 *
 */
class ProcessInstanceRowMapper implements RowMapper{
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		ProcessInstance processInstance = new ProcessInstance();
		
		processInstance.setId(rs.getString("id"));
		processInstance.setProcessId(rs.getString("process_id"));
    	processInstance.setVersion(rs.getInt("version"));
    	processInstance.setName(rs.getString("name"));
    	processInstance.setDisplayName(rs.getString("display_name"));
    	
    	processInstance.setState(rs.getInt("state"));
    	processInstance.setSuspended(rs.getInt("suspended")==1?true:false);
    	processInstance.setCreatorId(rs.getString("creator_id"));
    	processInstance.setCreatedTime(rs.getDate("created_time"));
    	processInstance.setStartedTime(rs.getDate("started_time"));
    	
    	processInstance.setExpiredTime(rs.getDate("expired_time"));
    	processInstance.setEndTime(rs.getDate("end_time"));
    	processInstance.setParentProcessInstanceId(rs.getString("parent_processinstance_id"));
    	processInstance.setParentTaskInstanceId(rs.getString("parent_taskinstance_id"));
		
		return processInstance;
		
	}
}
/**
 * taskinstance 映射(共21个字段)
 * 
 * @author wmj2003
 *
 */
class TaskInstanceRowMapper implements RowMapper {

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		TaskInstance taskInstance = new TaskInstance();
		taskInstance.setId(rs.getString("id"));
		//TODO 20090922 wmj2003 没有给biz_type赋值 是否需要给基于jdbc的数据增加 setBizType()方法？
		taskInstance.setTaskId(rs.getString("task_id"));
		taskInstance.setActivityId(rs.getString("activity_id"));
		taskInstance.setName(rs.getString("name"));
		
		taskInstance.setDisplayName(rs.getString("display_name"));
		taskInstance.setState(rs.getInt("state")) 		;
		taskInstance.setSuspended(rs.getInt("suspended")==1?true:false);
		taskInstance.setTaskType(rs.getString("task_type"));		
		taskInstance.setCreatedTime(rs.getDate("created_time"));
		
		taskInstance.setStartedTime(rs.getDate("started_time"));		
		taskInstance.setEndTime(rs.getDate("end_time"));	    		
		taskInstance.setAssignmentStrategy(rs.getString("assignment_strategy"));
		taskInstance.setProcessInstanceId(rs.getString("processinstance_id"));		
		taskInstance.setProcessId(rs.getString("process_id"));
		
		taskInstance.setVersion(rs.getInt("version"));		
		taskInstance.setTargetActivityId(rs.getString("target_activity_id"));
		taskInstance.setFromActivityId(rs.getString("from_activity_id"));
		taskInstance.setStepNumber(rs.getInt("step_number"));
		taskInstance.setCanBeWithdrawn(rs.getInt("can_be_withdrawn")==1?true:false);

		return taskInstance;
	}
}
/**
 * 共8个字段
 * @author wmj2003
 *
 */
class WorkItemRowMapper implements RowMapper {

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		WorkItem workItem = new WorkItem();
		
		workItem.setId(rs.getString("id"));
		workItem.setState(rs.getInt("state"));
		workItem.setCreatedTime(rs.getDate("created_time"));
		workItem.setClaimedTime(rs.getDate("claimed_time"));
		workItem.setEndTime(rs.getDate("end_time"));
		
		workItem.setActorId(rs.getString("actor_id"));
		workItem.setTaskInstanceId(rs.getString("taskinstance_id"));
		workItem.setComments(rs.getString("comments"));
		return workItem;
	}
}


/**
 * 共7个字段
 * @author wmj2003
 *
 */
class TokenRowMapper implements RowMapper {

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		Token token = new Token();
		token.setId(rs.getString("id"));
		token.setAlive(rs.getInt("alive")==1?true:false);
		token.setValue(rs.getInt("value"));
		token.setNodeId(rs.getString("node_id"));
		token.setProcessInstanceId(rs.getString("processinstance_id"));
		
		token.setStepNumber(rs.getInt("step_number"));		
		token.setFromActivityId(rs.getString("from_activity_id"));
		
		return token;
	}
}

/**
 * 工作流定义。（共13个字段） 暂时没有使用到，都是使用匿名类实现的，否则无法读取lob
 * @author wmj2003
 *
 */
class WorkFlowDefinitionInfoRowMapper implements RowMapper{
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		WorkflowDefinition workFlowDefinition  = new WorkflowDefinition();
		workFlowDefinition.setId(rs.getString("id"));
		workFlowDefinition.setDefinitionType(rs.getString("definition_type"));
		workFlowDefinition.setProcessId(rs.getString("process_id"));
		workFlowDefinition.setName(rs.getString("name"));
		workFlowDefinition.setDisplayName(rs.getString("display_name"));
		
		workFlowDefinition.setDescription(rs.getString("description"));
		workFlowDefinition.setVersion(rs.getInt("version"));
		workFlowDefinition.setState(rs.getInt("state")==1?true:false);
		workFlowDefinition.setUploadUser(rs.getString("upload_user"));
		workFlowDefinition.setUploadTime(rs.getDate("upload_time"));

		workFlowDefinition.setPublishUser(rs.getString("publish_user"));
		workFlowDefinition.setPublishTime(rs.getDate("publish_time"));
		//这里不保存process_context
		
		return workFlowDefinition;
	}
}



