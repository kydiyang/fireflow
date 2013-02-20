/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.engine.entity;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.engine.entity.runtime.impl.ActivityInstanceHistory;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceHistory;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.WorkItemHistory;
import org.fireflow.engine.entity.runtime.impl.WorkItemImpl;
import org.fireflow.misc.DateTimeXmlAdapter;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlType(name="absWorkflowEntityType",propOrder={"id","lastUpdateTime"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ProcessInstanceImpl.class,ProcessInstanceHistory.class,
	ActivityInstanceImpl.class,ActivityInstanceHistory.class,
	WorkItemImpl.class,WorkItemHistory.class})
public abstract class AbsWorkflowEntity implements WorkflowEntity {
	@XmlElement(name="entityId")
	protected String id = null;
	
	@XmlElement(name="lastUpdateTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
	protected Date lastUpdateTime = null;
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.WorkflowEntity#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.WorkflowEntity#getLastUpdateTime()
	 */
	@Override
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	
}
