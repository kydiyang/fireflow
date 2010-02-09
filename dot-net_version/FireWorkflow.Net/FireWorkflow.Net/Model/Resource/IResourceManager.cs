using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Resource
{
    /// <summary>资源管理器（1.0未用到）</summary>
    public interface IResourceManager
    {
        List<Application> getApplications();

        List<Participant> getParticipants();

        List<Form> getForms();
    }
}
