using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Io;

namespace FireWorkflow.Net.Engine.Definition
{
    /// <summary>
    /// 流程定义对象
    /// 映射到表T_FF_DF_WORKFLOWDEF
    /// </summary>
    public class WorkflowDefinition : WorkflowDefinitionInfo
    {

        [NonSerialized]
        protected WorkflowProcess workflowProcess;

        protected String processContent;


        public String getProcessContent()
        {
            return processContent;
        }

        public void setProcessContent(String processContent)
        {
            this.processContent = processContent;
        }

        public WorkflowProcess getWorkflowProcess()// throws RuntimeException
        {
            /*
            if (workflowProcess == null) {
                if (this.processContent != null && !this.processContent.trim().Equals("")) {

                    ByteArrayInputStream in = null;
                    try {
                        Dom4JFPDLParser parser = new Dom4JFPDLParser();
                        in = new ByteArrayInputStream(this.processContent.getBytes("utf-8"));
                        this.workflowProcess = parser.parse(in);

                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                        throw new RuntimeException(ex.getMessage());
                    } catch (IOException ex) {
                        Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                        throw new RuntimeException(ex.getMessage());
                    } 
                    catch(FPDLParserException ex){
                        Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                        throw new RuntimeException(ex.getMessage());
                    }
                    finally {
                        try {
                            in.close();
                        } catch (IOException ex) {
                            Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }
            }*/
            return workflowProcess;
        }

        public void setWorkflowProcess(WorkflowProcess process)// throws  RuntimeException 
        {
            try
            {
                this.workflowProcess = process;
                this.processId = workflowProcess.Id;
                this.name = workflowProcess.Name;
                this.displayName = workflowProcess.DisplayName;
                this.description = workflowProcess.Description;

                Dom4JFPDLSerializer ser = new Dom4JFPDLSerializer();
                MemoryStream so = new MemoryStream();

                ser.serialize(workflowProcess, so);

                this.processContent = Encoding.UTF8.GetString(so.ToArray());

            }
            catch (FPDLSerializerException ex)
            {
                //Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                throw new Exception(ex.Message);
            }
            catch (IOException ex)
            {
                //Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                throw new Exception(ex.Message);
            }
        }

    }
}
