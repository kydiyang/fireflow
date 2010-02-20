/**
 * Copyright 2003-2008 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 * @author 非也,nychen2000@163.com
 * @Revision to .NET 无忧 lwz0721@gmail.com 2010-02
 */
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
    /// 从文件系统读取流程定义文件，该类忽略流程定义文件的版本，主要用于开发阶段
    /// </summary>
    public class DefinitionService4FileSystem : IDefinitionService
    {
        public RuntimeContext RuntimeContext { get; set; }
        /// <summary>流程名到流程定义的id</summary>
        protected Dictionary<String, WorkflowDefinition> workflowDefinitionMap = new Dictionary<String, WorkflowDefinition>();// 流程名到流程定义的id
        protected Dictionary<String, String> latestVersionKeyMap = new Dictionary<String, String>();

        public void setDefinitionFiles(List<String> workflowProcessFileNames)// throws IOException, FPDLParserException,EngineException 
        {
            if (workflowProcessFileNames != null)
            {
                Dom4JFPDLParser parser = new Dom4JFPDLParser();
                //            JAXP_FPDL_Parser parser = new JAXP_FPDL_Parser();
                for (int i = 0; i < workflowProcessFileNames.Count; i++)
                {
                    Stream inStream = new FileStream(
                            workflowProcessFileNames[i].Trim(),FileMode.Open);
                    if (inStream == null)
                    {
                        throw new IOException("没有找到名称为" + workflowProcessFileNames[i] + "的流程定义文件");
                    }


                    WorkflowProcess workflowProcess = parser.parse(inStream);

                    WorkflowDefinition workflowDef = new WorkflowDefinition();
                    workflowDef.Version=1;

                    workflowDef.setWorkflowProcess(workflowProcess);

                    String latestVersionKey = workflowProcess.Id + "_V_" + workflowDef.Version;
                    workflowDefinitionMap.Add(latestVersionKey, workflowDef);
                    latestVersionKeyMap.Add(workflowProcess.Id, latestVersionKey);
                }
            }
        }

        #region 实现IDefinitionService
        /// <summary>返回所有流程的最新版本</summary>
        /// <returns></returns>
        public List<WorkflowDefinition> GetAllLatestVersionsOfWorkflowDefinition()
        {
            return new List<WorkflowDefinition>(workflowDefinitionMap.Values);
        }


        /// <summary>根据流程Id和版本号查找流程定义</summary>
        public WorkflowDefinition GetWorkflowDefinitionByProcessIdAndVersionNumber(String processId, Int32 version)
        {
            return this.workflowDefinitionMap[processId + "_V_" + version];
        }

        /// <summary>通过流程Id查找其最新版本的流程定义</summary>
        public WorkflowDefinition GetTheLatestVersionOfWorkflowDefinition(String processId)
        {
            return this.workflowDefinitionMap[this.latestVersionKeyMap[processId]];
        }

        #endregion

        #region 构造
        public DefinitionService4FileSystem() { }
        public DefinitionService4FileSystem(RuntimeContext rc) { this.RuntimeContext = rc; }
        #endregion
    }
}
