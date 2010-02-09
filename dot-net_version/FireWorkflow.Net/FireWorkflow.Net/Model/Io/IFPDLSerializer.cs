using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

namespace FireWorkflow.Net.Model.Io
{
    /// <summary>
    /// FPDL序列化器。将WorkflowProcess对象序列化到一个输出流。
    /// </summary>
    public abstract class IFPDLSerializer : FPDLNames
    {

        /// <summary>
        /// 将WorkflowProcess对象序列化到一个输出流。
        /// </summary>
        /// <param name="workflowProcess">工作流定义</param>
        /// <param name="swout">输出流</param>
        public abstract void serialize(WorkflowProcess workflowProcess, Stream swout);
        /*
	public void serialize(WorkflowProcess workflowProcess, Writer out)
			throws IOException, FPDLSerializerException;
         */

    }
}
