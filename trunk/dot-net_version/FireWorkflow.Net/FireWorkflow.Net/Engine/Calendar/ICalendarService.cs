using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Model;

namespace FireWorkflow.Net.Engine.Calendar
{
    /// <summary>
    /// 日历服务
    /// </summary>
    public interface ICalendarService : IRuntimeContextAware
    {

        /// <summary>
        /// 获得fromDate后相隔duration的某个日期
        /// Get the date after the duration
        /// </summary>
        /// <param name="fromDate"></param>
        /// <param name="duration"></param>
        /// <returns></returns>
        DateTime dateAfter(DateTime fromDate, Duration duration);

        /// <summary>
        /// <para>缺省实现，周六周日都是非工作日，其他的都为工作日。</para>
        /// <para>实际应用中，可以在数据库中建立一张非工作日表，将周末以及法定节假日录入其中，</para>
        /// <para>然后在该方法中读该表的数据来判断工作日和非工作日。</para>
        /// </summary>
        /// <param name="d"></param>
        /// <returns></returns>
        Boolean isBusinessDay(DateTime d);

        /// <summary>
        /// 获得系统时间
        /// </summary>
        /// <returns></returns>
        DateTime getSysDate();
    }
}
