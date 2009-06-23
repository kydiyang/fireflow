package org.fireflow.workflowmanagement.mbeans;

import java.io.InputStream;

import org.fireflow.BasicManagedBean;
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.widget.UIFileUpload;
import org.operamasks.faces.component.widget.fileupload.FileUploadItem;

@ManagedBean(scope=ManagedBeanScope.REQUEST)
public class UploadWorkflowDefinitionBean extends BasicManagedBean {
	
	private UIFileUpload  upFile;

	private Boolean published = Boolean.TRUE;

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public void processUpload(FileUploadItem fileUploadItem) {
		InputStream processbyte;
		String errMsg = null;
		System.out.println("--------------------------------------");
		/*try {
			processbyte = fileUploadItem.openStream();

			Dom4JFPDLParser parser = new Dom4JFPDLParser();

			WorkflowProcess process = parser.parse(processbyte);

			WorkflowDefinition workflowdef = new WorkflowDefinition();

			workflowdef.setWorkflowProcess(process);
			workflowdef.setState(published);

			workflowdef.setUploadUser(SecurityUtilities.getCurrentUser()
					.getName());
			workflowdef.setUploadTime(workflowRuntimeContext
					.getCalendarService().getSysDate());

			if (published) {
				workflowdef.setPublishUser(SecurityUtilities.getCurrentUser()
						.getName());
				workflowdef.setPublishTime(workflowRuntimeContext
						.getCalendarService().getSysDate());
			}
			IPersistenceService persistenceService = this.workflowRuntimeContext
					.getPersistenceService();
			persistenceService.saveOrUpdateWorkflowDefinition(workflowdef);
		} catch (UnsupportedEncodingException e) {
			errMsg = e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			errMsg = e.getMessage();
			e.printStackTrace();
		} catch (FPDLParserException e) {
			errMsg = e.getMessage();
			e.printStackTrace();
		}*/
	}

	@Action
	public void save(){
		System.out.println("==========================================");
		
	}

	public UIFileUpload getUpFile() {
		return upFile;
	}

	public void setUpFile(UIFileUpload upFile) {
		this.upFile = upFile;
	}
}
