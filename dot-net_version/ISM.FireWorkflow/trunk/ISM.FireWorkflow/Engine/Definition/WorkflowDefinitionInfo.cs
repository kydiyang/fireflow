using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISM.FireWorkflow.Engine.Definition
{

    /// <summary>
    /// 流程定义相关信息对象
    /// </summary>
    public class WorkflowDefinitionInfo
    {
        public const String FPDL_PROCESS = "FPDL";
        public const String XPDL_PROCESS = "XPDL";
        public const String BPEL_PROCESS = "BPEL";
        protected String id;
        protected String processId;
        protected String name;
        protected String displayName;
        protected String description;
        protected Int32 version;
        protected Boolean state;//是否发布，1=已经发布,0未发布
        protected String uploadUser;//上载到数据库的操作员
        protected DateTime uploadTime;//上载到数据库的时间
        protected String publishUser;//发布人
        protected DateTime publishTime;//发布时间
        protected String definitionType = FPDL_PROCESS;//定义文件的语言类型，fpdl,xpdl,bepl...

        public String getDisplayName()
        {
            return displayName;
        }

        public void setDisplayName(String displayName)
        {
            this.displayName = displayName;
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
        public DateTime getPublishTime()
        {
            return publishTime;
        }

        public void setPublishTime(DateTime publishTime)
        {
            this.publishTime = publishTime;
        }

        public Boolean getState()
        {
            return state;
        }

        public void setState(Boolean published)
        {
            this.state = published;
        }

        public String getPublishUser()
        {
            return publishUser;
        }

        public void setPublishUser(String publisher)
        {
            this.publishUser = publisher;
        }

        public Int32 getVersion()
        {
            return version;
        }

        public void setVersion(Int32 version)
        {
            this.version = version;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public String getProcessId()
        {
            return processId;
        }

        public void setProcessId(String processId)
        {
            this.processId = processId;
        }

        public String getUploadUser()
        {
            return uploadUser;
        }

        public void setUploadUser(String uploadUser)
        {
            this.uploadUser = uploadUser;
        }

        public DateTime getUploadTime()
        {
            return uploadTime;
        }

        public void setUploadTime(DateTime uploadTime)
        {
            this.uploadTime = uploadTime;
        }

        public String getDefinitionType()
        {
            return definitionType;
        }

        public void setDefinitionType(String definitionType)
        {
            this.definitionType = definitionType;
        }


    }
}
