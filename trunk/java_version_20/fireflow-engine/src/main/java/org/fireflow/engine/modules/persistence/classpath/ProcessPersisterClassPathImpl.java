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
package org.fireflow.engine.modules.persistence.classpath;

import java.util.List;
import java.util.Map;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessDescriptorProperty;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.model.InvalidModelException;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class ProcessPersisterClassPathImpl implements ProcessPersister {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.Persister#find(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T extends WorkflowEntity> T find(Class<T> entityClz, String entityId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.Persister#list(org.fireflow.engine.WorkflowQuery)
	 */
	@Override
	public <T extends WorkflowEntity> List<T> list(WorkflowQuery<T> q) {
		throw new UnsupportedOperationException("This method is unsupported");
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.Persister#count(org.fireflow.engine.WorkflowQuery)
	 */
	@Override
	public <T extends WorkflowEntity> int count(WorkflowQuery<T> q) {
		throw new UnsupportedOperationException("This method is unsupported");
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.Persister#saveOrUpdate(java.lang.Object)
	 */
	@Override
	public void saveOrUpdate(Object entity) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.Persister#getPersistenceService()
	 */
	@Override
	public PersistenceService getPersistenceService() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.Persister#setPersistenceService(org.fireflow.engine.modules.persistence.PersistenceService)
	 */
	@Override
	public void setPersistenceService(PersistenceService persistenceService) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ProcessPersister#persistProcessToRepository(java.lang.Object, java.util.Map)
	 */
//	@Override
//	public ProcessRepository persistProcessToRepository(Object process,
//			Map<ProcessDescriptorProperty, Object> descriptorKeyValues)
//			throws InvalidModelException {
//		// TODO Auto-generated method stub
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ProcessPersister#findProcessRepositoryByProcessKey(org.fireflow.engine.entity.repository.ProcessKey)
	 */
	@Override
	public ProcessRepository findProcessRepositoryByProcessKey(
			ProcessKey processKey) throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ProcessPersister#findTheLatestVersionOfProcessRepository(java.lang.String, java.lang.String)
	 */
	@Override
	public ProcessRepository findTheLatestVersionOfProcessRepository(
			String processId, String processType) throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ProcessPersister#findProcessDescriptorByProcessKey(org.fireflow.engine.entity.repository.ProcessKey)
	 */
	@Override
	public ProcessDescriptor findProcessDescriptorByProcessKey(
			ProcessKey processKey) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ProcessPersister#findTheLatestVersionOfProcessDescriptor(java.lang.String, java.lang.String)
	 */
	@Override
	public ProcessDescriptor findTheLatestVersionOfProcessDescriptor(
			String processId, String processType) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ProcessPersister#findTheLatestVersion(java.lang.String, java.lang.String)
	 */
	@Override
	public int findTheLatestVersion(String processId, String processType) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ProcessPersister#findTheLatestPublishedVersion(java.lang.String, java.lang.String)
	 */
	@Override
	public int findTheLatestPublishedVersion(String processId,
			String processType) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ProcessPersister#isUseProcessCache()
	 */
	@Override
	public boolean isUseProcessCache() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ProcessPersister#setUseProcessCache(boolean)
	 */
	@Override
	public void setUseProcessCache(boolean b) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ProcessPersister#persistProcessToRepository(java.lang.String, org.fireflow.engine.entity.repository.ProcessDescriptor)
	 */
	@Override
	public ProcessRepository persistProcessToRepository(String processXml,
			ProcessDescriptor descriptor) {
		// TODO Auto-generated method stub
		return null;
	}

}
