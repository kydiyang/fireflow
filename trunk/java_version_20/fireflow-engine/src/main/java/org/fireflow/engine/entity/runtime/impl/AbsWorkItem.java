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

import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class AbsWorkItem implements WorkItem{
	protected String id = null;

	protected WorkItemState state = WorkItemState.INITIALIZED;
	
	/**
	 * 等于activityInstance.displayName
	 */
	protected String workItemName = null;
	/**
	 * 工作项摘要
	 */
	protected String subject = null;//
	
	/**
	 * 产生该工作项的业务系统名称
	 */
	protected String originalSystemName = null;
	
	/**
	 * 流程创建者的姓名。TODO 上一步操作者需要展示出来吗？
	 */
	protected String procInstCreatorName = null;
	
	protected String bizId = null;
	
	/**
	 * 创建时间
	 */
	protected Date createdTime;
	
	/**
	 * 到期时间，等于ActivityInstance.expiredTime
	 */
	protected Date expiredTime;
    /**
     * 签收时间
     */
	protected Date claimedTime;
	protected Date endTime;

	protected String ownerId;
	protected String ownerName;
	protected String ownerDeptId;
	protected String ownerDeptName;
	protected String ownerType;
	
	protected String responsiblePersonId;
	protected String responsiblePersonName;
	protected String responsiblePersonOrgId;
	protected String responsiblePersonOrgName;
	
	protected String approvalId;
	protected String note;
	
	/**
	 * 表单Url
	 */
	protected String formUrl;
	protected String mobileFormUrl;

	/**
	 * 流程引擎地址
	 */
	protected String workflowEngineLocation;//用于远程操作
	
	/**
	 * work item 类型，LOCAL表示本地WorkItem，REMOTE表示远程workitem，NOT_FF表示非FF workitem；，如果为远程workitem则需要应用到workflowEngineLocation
	 */
	protected String workItemType = WORKITEM_TYPE_LOCAL;//是否为远程workitem
	
	protected String parentWorkItemId = WorkItem.NO_PARENT_WORKITEM;
	protected String reassignType;
	
	protected ActivityInstance activityInstance;
    protected WorkItemAssignmentStrategy assignmentStrategy = WorkItemAssignmentStrategy.ASSIGN_TO_ANY;
	
    protected Date lastUpdateTime = null;
    
    
    //////////////////////////////////////
    ///////////  冗余字段 便于查询 /////////
    /////////////////////////////////////
    protected String processId = null;
    protected int version = 0;
    protected String processType = null;
    protected String subProcessId = null;
    protected String processInstanceId = null;
    protected String activityId = null;
    protected int stepNumber = -1;
    
    
    /**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the state
	 */
	public WorkItemState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(WorkItemState state) {
		this.state = state;
	}

	/**
	 * @return the createdTime
	 */
	public Date getCreatedTime() {
		return createdTime;
	}

	/**
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	/**
	 * @return the claimedTime
	 */
	public Date getClaimedTime() {
		return claimedTime;
	}

	/**
	 * @param claimedTime the claimedTime to set
	 */
	public void setClaimedTime(Date claimedTime) {
		this.claimedTime = claimedTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the userId
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setOwnerId(String userId) {
		this.ownerId = userId;
	}

	/**
	 * @return the userName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setOwnerName(String userName) {
		this.ownerName = userName;
	}

	/**
	 * @return the userOrgId
	 */
	public String getOwnerDeptId() {
		return ownerDeptId;
	}

	/**
	 * @param userOrgId the userOrgId to set
	 */
	public void setOwnerDeptId(String userOrgId) {
		this.ownerDeptId = userOrgId;
	}

	/**
	 * @return the userOrgName
	 */
	public String getOwnerDeptName() {
		return ownerDeptName;
	}

	/**
	 * @param userOrgName the userOrgName to set
	 */
	public void setOwnerDeptName(String userOrgName) {
		this.ownerDeptName = userOrgName;
	}
	
	public String getOwnerType(){
		return this.ownerType;
	}
	
	public void setOwnerType(String ownerType){
		this.ownerType = ownerType;
	}

	/**
	 * @return the responsiblePersonId
	 */
	public String getResponsiblePersonId() {
		return responsiblePersonId;
	}

	/**
	 * @param responsiblePersonId the responsiblePersonId to set
	 */
	public void setResponsiblePersonId(String responsiblePersonId) {
		this.responsiblePersonId = responsiblePersonId;
	}

	/**
	 * @return the responsiblePersonName
	 */
	public String getResponsiblePersonName() {
		return responsiblePersonName;
	}

	/**
	 * @param responsiblePersonName the responsiblePersonName to set
	 */
	public void setResponsiblePersonName(String responsiblePersonName) {
		this.responsiblePersonName = responsiblePersonName;
	}

	/**
	 * @return the responsiblePersonOrgId
	 */
	public String getResponsiblePersonDeptId() {
		return responsiblePersonOrgId;
	}

	/**
	 * @param responsiblePersonOrgId the responsiblePersonOrgId to set
	 */
	public void setResponsiblePersonDeptId(String responsiblePersonOrgId) {
		this.responsiblePersonOrgId = responsiblePersonOrgId;
	}

	/**
	 * @return the responsiblePersonOrgName
	 */
	public String getResponsiblePersonDeptName() {
		return responsiblePersonOrgName;
	}

	/**
	 * @param responsiblePersonOrgName the responsiblePersonOrgName to set
	 */
	public void setResponsiblePersonDeptName(String responsiblePersonOrgName) {
		this.responsiblePersonOrgName = responsiblePersonOrgName;
	}

	/**
	 * @return the approvalId
	 */
	public String getApprovalId() {
		return approvalId;
	}

	/**
	 * @param approvalId the approvalId to set
	 */
	public void setApprovalId(String commentId) {
		this.approvalId = commentId;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String commentDetail) {
		this.note = commentDetail;
	}

	/**
	 * @return the parentWorkItemId
	 */
	public String getParentWorkItemId() {
		return parentWorkItemId;
	}

	/**
	 * @param parentWorkItemId the parentWorkItemId to set
	 */
	public void setParentWorkItemId(String parentWorkItemId) {
		this.parentWorkItemId = parentWorkItemId;
	}

	/**
	 * @return the reassignType
	 */
	public String getReassignType() {
		return reassignType;
	}

	/**
	 * @param reassignType the reassignType to set
	 */
	public void setReassignType(String reassignType) {
		this.reassignType = reassignType;
	}

	/**
	 * @return the activityInstance
	 */
	public ActivityInstance getActivityInstance() {
		return activityInstance;
	}

	public String getFormUrl() {
		return formUrl;
	}

	public void setFormUrl(String formUrl) {
		this.formUrl = formUrl;
	}

	public String getMobileFormUrl(){
		return this.mobileFormUrl;
	}
	
	public void setMobileFormUrl(String url){
		this.mobileFormUrl = url;
	}
	/**
	 * @param activityInstance the activityInstance to set
	 */
	public void setActivityInstance(ActivityInstance activityInstance) {
		this.activityInstance = activityInstance;
	}
	

	public WorkItemAssignmentStrategy getAssignmentStrategy() {
		
		return this.assignmentStrategy;
	}
	
	public void setAssignmentStrategy(WorkItemAssignmentStrategy assignmentStrategy){
		this.assignmentStrategy = assignmentStrategy;
	}
	
	
	
	
	public String getWorkItemName() {
		return workItemName;
	}

	public void setWorkItemName(String workItemName) {
		this.workItemName = workItemName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String description) {
		this.subject = description;
	}

	public String getOriginalSystemName() {
		return originalSystemName;
	}

	public void setOriginalSystemName(String originalSystemName) {
		this.originalSystemName = originalSystemName;
	}

	public String getProcInstCreatorName() {
		return procInstCreatorName;
	}

	public void setProcInstCreatorName(String procInstCreatorName) {
		this.procInstCreatorName = procInstCreatorName;
	}

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public Date getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}

	public String getResponsiblePersonOrgId() {
		return responsiblePersonOrgId;
	}

	public void setResponsiblePersonOrgId(String responsiblePersonOrgId) {
		this.responsiblePersonOrgId = responsiblePersonOrgId;
	}

	public String getResponsiblePersonOrgName() {
		return responsiblePersonOrgName;
	}

	public void setResponsiblePersonOrgName(String responsiblePersonOrgName) {
		this.responsiblePersonOrgName = responsiblePersonOrgName;
	}

	public String getWorkflowEngineLocation() {
		return workflowEngineLocation;
	}

	public void setWorkflowEngineLocation(String workflowEngineLocation) {
		this.workflowEngineLocation = workflowEngineLocation;
	}

	public String getWorkItemType() {
		return workItemType;
	}

	public void setWorkItemType(String workItemType) {
		this.workItemType = workItemType;
	}
	
	public Date getLastUpdateTime(){
		return this.lastUpdateTime;
	}
	
	public void setLastUpdateTime(Date lastUpdateTime){
		this.lastUpdateTime = lastUpdateTime;
	}
	
	

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getSubProcessId() {
		return subProcessId;
	}

	public void setSubProcessId(String subProcessId) {
		this.subProcessId = subProcessId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	
	

	public int getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}

	public WorkItem clone(){
		AbsWorkItem wi = null;
		if (this instanceof WorkItemImpl){
			wi = new WorkItemImpl();
		}else if (this instanceof WorkItemHistory){
			wi = new WorkItemHistory();
		}else {
			return null;
		}
		wi.setWorkItemName(this.workItemName);
		wi.setWorkItemType(this.workItemType);
		wi.setWorkflowEngineLocation(this.workflowEngineLocation);
		wi.setBizId(this.bizId);
		wi.setExpiredTime(this.expiredTime);
		wi.setOriginalSystemName(this.originalSystemName);
		wi.setSubject(this.subject);
		wi.setProcInstCreatorName(this.procInstCreatorName);
		
		
		wi.setActivityInstance(activityInstance);
		wi.setAssignmentStrategy(assignmentStrategy);
		wi.setClaimedTime(claimedTime);
		wi.setNote(note);
		wi.setApprovalId(approvalId);
		wi.setCreatedTime(createdTime);
		wi.setEndTime(endTime);
		wi.setParentWorkItemId(parentWorkItemId);
		wi.setReassignType(reassignType);
		wi.setResponsiblePersonDeptId(responsiblePersonOrgId);
		wi.setResponsiblePersonDeptName(responsiblePersonOrgName);
		wi.setResponsiblePersonId(responsiblePersonId);
		wi.setResponsiblePersonName(responsiblePersonName);
		wi.setState(state);
		wi.setOwnerDeptId(ownerDeptId);
		wi.setOwnerDeptName(ownerDeptName);
		wi.setOwnerId(ownerId);
		wi.setOwnerName(ownerName);
		wi.setFormUrl(formUrl);
		
		wi.setStepNumber(this.stepNumber);
		wi.setProcessId(this.processId);
		wi.setSubProcessId(subProcessId);
		wi.setActivityId(activityId);
		wi.setVersion(version);
		wi.setProcessType(processType);
		wi.setProcessInstanceId(processInstanceId);
		
		return wi;
	}
}
