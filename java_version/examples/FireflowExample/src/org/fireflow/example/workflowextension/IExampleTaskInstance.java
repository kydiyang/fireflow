package org.fireflow.example.workflowextension;

import java.util.List;

import org.fireflow.engine.IWorkItem;

public interface IExampleTaskInstance {
	public String getBizInfo();
	public List<IWorkItem> getWorkItems();
	public void setWorkItems(List<IWorkItem> workItems);
}
