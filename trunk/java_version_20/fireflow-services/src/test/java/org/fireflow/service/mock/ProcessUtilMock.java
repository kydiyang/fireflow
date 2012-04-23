package org.fireflow.service.mock;

import java.io.InputStream;

import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.process.ProcessUtil;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Property;

public class ProcessUtilMock implements ProcessUtil {

	public void setRuntimeContext(RuntimeContext ctx) {
		// TODO Auto-generated method stub

	}

	public RuntimeContext getRuntimeContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public String serializeProcess2Xml(Object process)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object deserializeXml2Process(InputStream inStream)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public ProcessRepository serializeProcess2ProcessRepository(Object process)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceBinding getServiceBinding(ProcessKey processKey,
			String subflowId, String activityId) throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceBinding getResourceBinding(ProcessKey processKey,
			String subflowId, String activityId) throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getActivity(ProcessKey processKey, String subflow, String activityId)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property getProperty(ProcessKey processKey, String processElementId, String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.process.ProcessUtil#getProcessEntryElementId(java.lang.String, int, java.lang.String)
	 */
	public String getProcessEntryId(String workflowProcessId,
			int version, String processType) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.EngineModule#init(org.fireflow.engine.context.RuntimeContext)
	 */
	public void init(RuntimeContext runtimeContext) throws EngineException {
		// TODO Auto-generated method stub
		
	}

}
