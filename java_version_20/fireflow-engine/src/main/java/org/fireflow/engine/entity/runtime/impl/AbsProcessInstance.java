/**
 * Copyright 2007-2010 非也
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
package org.fireflow.engine.entity.runtime.impl;

import java.util.Date;
import java.util.Map;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.WorkflowStatement;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.model.InvalidModelException;

/**
 * @author 非也
 * @version 2.0
 */
public abstract class AbsProcessInstance implements ProcessInstance {
	protected String id = null;
    protected String bizId = null;

    protected String processId = null;
    protected Integer version = null;
    protected String processType = null;
    protected String subflowId = null;
    
    protected String processName = null;
    protected String processDisplayName = null;
    protected String bizCategory = null;
    
    protected String subflowName = null;
    protected String subflowDisplayName = null;
    
    protected ProcessInstanceState state = null;
    protected Boolean suspended = Boolean.FALSE;
    
    protected String creatorId = null;
    protected String creatorName = null;
    protected String creatorOrgId = null;
    protected String creatorOrgName = null;
    
    protected Date createdTime = null;
    protected Date startedTime = null;
    protected Date endTime = null;
    protected Date expiredTime = null;
    
    protected String parentProcessInstanceId = null;
    protected String parentActivityInstanceId = null;
    
    protected String parentScopeId = null;
    
    protected String tokenId = null;
    
    protected String note;
    
    protected long lastUpdateTime = 0;
    
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getId()
	 */
	public String getId() {
		return this.id;
	}
	
	public void setId(String id){
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#bizId()
	 */
	public String getBizId() {
		
		return this.bizId;
	}
	
	public void setBizId(String bizId){
		this.bizId = bizId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getCreatedTime()
	 */
	public Date getCreatedTime() {
		return this.createdTime;
	}
	
	public void setCreatedTime(Date time){
		this.createdTime = time;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getCreatorId()
	 */
	public String getCreatorId() {
		return this.creatorId;
	}
	
	public void setCreatorId(String uid){
		this.creatorId = uid;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getCreatorName()
	 */
	public String getCreatorName() {
		return this.creatorName;
	}
	
	public void setCreatorName(String creatorName){
		this.creatorName = creatorName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getCreatorOrgId()
	 */
	public String getCreatorDeptId() {
		return this.creatorOrgId;
	}
	
	public void setCreatorDeptId(String orgId){
		this.creatorOrgId = orgId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getCreatorOrgName()
	 */
	public String getCreatorDeptName() {
		return this.creatorOrgName;
	}
	
	public void setCreatorDeptName(String orgName){
		this.creatorOrgName = orgName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getDisplayName()
	 */
	public String getProcessDisplayName() {
		return this.processDisplayName;
	}
	
	public void setProcessDisplayName(String displayName){
		this.processDisplayName = displayName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getEndTime()
	 */
	public Date getEndTime() {
		return this.endTime;
	}
	
	public void setEndTime(Date time){
		this.endTime = time;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getExpiredTime()
	 */
	public Date getExpiredTime() {
		return this.expiredTime;
	}
	
	public void setExpiredTime(Date time){
		this.expiredTime = time;
	}



	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getName()
	 */
	public String getProcessName() {
		return this.processName;
	}
	
	public void setProcessName(String name){
		this.processName = name;
	}

	public String getSubflowName(){
		return this.subflowName;
	}
	
	public void setSubflowName(String subflowName){
		this.subflowName = subflowName;
	}
	
	public String getSubflowDisplayName(){
		return this.subflowDisplayName;
	}
	
	public void setSubflowDisplayName(String subflowDisplayName){
		this.subflowDisplayName = subflowDisplayName;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getNote()
	 */
	public String getNote() {
		return this.note;
	}
	
	public void setNote(String note){
		this.note = note;
	}
	
	public long getLastUpdateTime(){
		return this.lastUpdateTime;
	}
	
	public void setLastUpdateTime(long time){
		this.lastUpdateTime = time;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getParentActivityInstanceId()
	 */
	public String getParentActivityInstanceId() {
		
		return this.parentActivityInstanceId;
	}
	
	public void setParentActivityInstanceId(String pActInstId){
		this.parentActivityInstanceId = pActInstId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getParentProcessInstanceId()
	 */
	public String getParentProcessInstanceId() {

		return this.parentProcessInstanceId;
	}
	
	public void setParentProcessInstanceId(String pProcInstId){
		this.parentProcessInstanceId = pProcInstId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getProcessId()
	 */
	public String getProcessId() {
		return this.processId;
	}
	
	public void setProcessId(String processId){
		this.processId = processId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getProcessType()
	 */
	public String getProcessType() {
		return this.processType;
	}

	public void setProcessType(String processType){
		this.processType = processType;
	}
	
    public String getSubflowId(){
    	return this.subflowId;
    }
    
    public void setSubflowId(String subflowId){
    	this.subflowId = subflowId;
    }
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getStartedTime()
	 */
	public Date getStartedTime() {
		return this.startedTime;
	}
	
	public void setStartedTime(Date time){
		this.startedTime = time;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getState()
	 */
	public ProcessInstanceState getState() {
		return this.state;
	}
	
	public void setState(ProcessInstanceState state){
		this.state = state;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getVersion()
	 */
	public Integer getVersion() {
		return this.version;
	}
	
	public void setVersion(Integer v){
		this.version = v;
	}



	public String getBizCategory() {
		return bizCategory;
	}

	public void setBizCategory(String bizCategory) {
		this.bizCategory = bizCategory;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#isSuspended()
	 */
	public Boolean isSuspended() {
		
		return this.suspended;
	}
	
	public void setSuspended(Boolean b){
		this.suspended = b;
	}

	public String getScopeId(){
		return this.id;
	}
	
	public String getProcessElementId(){
		return this.subflowId;
	}
	
	public String getParentScopeId(){
		return this.parentScopeId;
	}
	
	public void setParentScopeId(String pscopeId){
		this.parentScopeId = pscopeId;
	}
	
	

	/**
	 * @return the tokenId
	 */
	public String getTokenId() {
		return tokenId;
	}

	/**
	 * @param tokenId the tokenId to set
	 */
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public Object getVariableValue(WorkflowSession session,String name){
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		return stmt.getVariableValue(this, name);
	}
	public void setVariableValue(WorkflowSession session ,String name ,Object value)throws InvalidOperationException{
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		stmt.setVariableValue(this, name,value);
	}
	public void setVariableValue(WorkflowSession session ,String name ,Object value,Map<String,String> headers)throws InvalidOperationException{
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		stmt.setVariableValue(this, name,value,headers);
	}
	public Map<String,Object> getVariableValues(WorkflowSession session){
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		return stmt.getVariableValues(this);
	}	
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getWorkflowProcess(org.fireflow.engine.WorkflowSession)
	 */
	public Object getWorkflowProcess(WorkflowSession session)
	throws InvalidModelException {
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		ProcessKey pk = new ProcessKey(this.processId,this.version,this.processType);
		return stmt.getWorkflowProcess(pk);
	}

}
