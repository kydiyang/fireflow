/**
 * Copyright 2007-2008 陈乜云（非也,Chen Nieyun）
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
package org.fireflow.engine.definition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fireflow.engine.EngineException;
import org.fireflow.model.DataField;
import org.fireflow.model.Task;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.io.Dom4JFPDLParser;
import org.fireflow.model.io.Dom4JFPDLSerializer;
import org.fireflow.model.io.FPDLParserException;
import org.fireflow.model.io.FPDLSerializerException;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.Transition;

/**
 *
 * @author chennieyun
 */
public class WorkflowDefinition {

    protected transient WorkflowProcess workflowProcess;

    protected String id;
    protected String processId;
    protected String name;
    protected String displayName;
    protected String description;
    protected Integer version;
    protected Boolean published;//是否发布，备用
    protected String publisher;//发布人
    protected Date publishTime;//发布时间
    protected String processContent;

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

    public String getProcessContent() {
        return processContent;
    }

    public void setProcessContent(String processContent) {
        this.processContent = processContent;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
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

    public WorkflowProcess getWorkflowProcess() throws EngineException{
        if (workflowProcess == null) {
            if (this.processContent != null && !this.processContent.trim().equals("")) {

                ByteArrayInputStream in = null;
                try {
                    Dom4JFPDLParser parser = new Dom4JFPDLParser();
                    in = new ByteArrayInputStream(this.processContent.getBytes("utf-8"));
                    this.workflowProcess = parser.parse(in);

                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                    throw new EngineException(ex.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                    throw new EngineException(ex.getMessage());
                } 
                catch(FPDLParserException ex){
                    Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                    throw new EngineException(ex.getMessage());
                }
                finally {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }
        return workflowProcess;
    }

    public void setWorkflowProcess(WorkflowProcess process) throws  EngineException {
        try {
            this.workflowProcess = process;
            this.processId = workflowProcess.getId();
            this.name = workflowProcess.getName();
            this.displayName = workflowProcess.getDisplayName();
            this.description = workflowProcess.getDescription();

            Dom4JFPDLSerializer ser = new Dom4JFPDLSerializer();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            ser.serialize(workflowProcess, out);

            this.processContent = out.toString("utf-8");
        } catch (FPDLSerializerException ex) {
            Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
            throw new EngineException(ex.toString());
        } catch (IOException ex) {
            Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
            throw new EngineException(ex.toString());
        }
    }

}
