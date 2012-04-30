package org.fireflow.engine.entity.repository.impl;

import org.fireflow.engine.entity.repository.ProcessDescriptor;

/**
 * TODO 如何体现“流程族”的概念
 * 
 * 流程定义相关信息对象
 * @author 非也
 *
 */
public class ProcessDescriptorImpl extends AbsRepositoryDescriptorImpl implements ProcessDescriptor{
	//TODO 如何体现“流程族”的概念

    protected String processId;//流程id
    protected String processType = null;//定义文件的语言类型，fpdl,xpdl,bepl...
    protected Integer version;//版本号
    
    
    protected Boolean isTimerStart = Boolean.FALSE;//是否是定时启动的流程
    
    protected Boolean hasCallbackService = Boolean.FALSE;//是否有回调接口，即是否要发布Webservice
    

    
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }    

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ProcessRepositoryDescriptor#getProcessType()
	 */
	public String getProcessType() {
		return processType;
	}
	
	public void setProcessType(String processType){
		this.processType = processType;
	}



    public Boolean getTimerStart(){
    	return this.isTimerStart;
    }
    
    public void setTimerStart(Boolean b){
    	this.isTimerStart = b;
    }
    
    public Boolean getHasCallbackService(){
    	return this.hasCallbackService;
    }
    
    public void setHasCallbackService(Boolean b){
    	this.hasCallbackService = b;
    }
}
