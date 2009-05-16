package org.fireflow.engine.definition;

import java.util.Date;

public class WorkflowDefinitionInfo {

    protected String id;
    protected String processId;
    protected String name;
    protected String displayName;
    protected String description;
    protected Integer version;
    protected Boolean state;//是否发布，1=已经发布,0未发布
    protected String uploadUser ;//上载到数据库的操作员
    protected Date uploadTime;//上载到数据库的时间
    protected String publishUser;//发布人
    protected Date publishTime;//发布时间
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean published) {
        this.state = published;
    }

    public String getPublishUser() {
        return publishUser;
    }

    public void setPublishUser(String publisher) {
        this.publishUser = publisher;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

	public String getUploadUser() {
		return uploadUser;
	}

	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

    
}
