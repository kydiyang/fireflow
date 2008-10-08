package org.fireflow.model.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.fireflow.model.WorkflowProcess;

public interface IFPDLSerializer extends FPDLNames {
	public void serialize(WorkflowProcess workflowProcess, OutputStream out)
			throws IOException, FPDLSerializerException;

	public void serialize(WorkflowProcess workflowProcess, Writer out)
			throws IOException, FPDLSerializerException;
}
