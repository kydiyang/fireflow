using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model
{
    /// <summary>Fire workflow保留的扩展属性的名称，工作流自定义的扩展属性不要使用这些名字</summary>
    public class ExtendedAttributeNames
    {
        const String BOUNDS_X = "FIRE_FLOW.bounds.x";
        const String BOUNDS_Y = "FIRE_FLOW.bounds.y";
        const String BOUNDS_WIDTH = "FIRE_FLOW.bounds.width";
        const String BOUNDS_HEIGHT = "FIRE_FLOW.bounds.height";

        const String EDGE_POINT_LIST = "FIRE_FLOW.edgePointList";
        const String LABEL_POSITION = "FIRE_FLOW.labelPosition";

        const String ACTIVITY_LOCATION = "FIRE_FLOW.activityLocation";
    }
}
