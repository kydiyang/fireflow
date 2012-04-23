package org.fireflow.model.servicedef;

import java.util.List;

import javax.xml.namespace.QName;

public interface InterfaceDef {
//	public String getNamespaceUri();//通过ServiceDef.TargetNamespaceUri表达，2012-03-01
	
	public String getName();
	/**
	 * 返回名称为opName的Operation。
	 * (如果出现同名operation，怎么处理？）
	 * @return
	 */
	public OperationDef getOperation(String opName);
//	public void setOperation(Operation operation);
	
	/**
	 * 返回所有的Operation
	 * @return
	 */
	public List<OperationDef> getOperations();
	
	/**
	 * 返回名字为operName的所有的Operation
	 * @param opName
	 * @return
	 */
	public List<OperationDef> getOperations(String opName);
}
