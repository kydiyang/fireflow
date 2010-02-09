using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model
{
    /// <summary>时间间隔</summary>
    [Serializable]
    public class Duration
    {
        /// <summary>天</summary>
        public const String DAY = "DAY";
        /// <summary>月</summary>
        public const String MONTH = "MONTH";
        /// <summary>年</summary>
        public const String YEAR = "YEAR";
        /// <summary>小时</summary>
        public const String HOUR = "HOUR";
        /// <summary>一分钟</summary>
        public const String MINUTE = "MINUTE";
        /// <summary>秒</summary>
        public const String SECOND = "SECOND";
        /// <summary>周</summary>
        public const String WEEK = "WEEK";//    private const Log log = LogFactory.getLog(Duration.class);
        private int value;
        private String unit;
        private Boolean isBusinessTime = true;


        /// <summary>创建时间间隔对象</summary>
        /// <param name="value">时间值</param>
        /// <param name="unit">时间单位</param>
        public Duration(int value, String unit)
        {
            this.value = value;
            this.unit = unit;

            //        log.debug("Duration(" + value + ", " + unit + ")");
        }

        /// <summary>获取时间值</summary>
        public int getValue()
        {
            return value;
        }

        /// <summary>设置时间值</summary>
        public void setValue(int v)
        {
            value = v;
        }

        /// <summary>获取时间单位</summary>
        public String getUnit()
        {
            return unit;
        }

        /// <summary>设置时间单位</summary>
        public void setUnit(String u)
        {
            unit = u;
        }

        /// <summary>获取时间单位，如果时间单位为null，则返回defaultUnit</summary>
        /// <param name="defaultUnit"></param>
        /// <returns></returns>
        public String getUnit(String defaultUnit)
        {
            if (unit == null)
            {
                return defaultUnit;
            }
            else
            {
                return unit;
            }
        }


        /// <summary>获取换算成毫秒的时间间隔值</summary>
        /// <param name="defaultUnit"></param>
        /// <returns></returns>
        public long getDurationInMilliseconds(String defaultUnit)
        {
            int value = getValue();
            String unit = getUnit(defaultUnit);

            //log.debug("Duration value: " + value);
            //log.debug("Duration unit: " + unit);
            //log.debug("Unit in MS: " + unit.toMilliseconds());

            if (value == 0)
            {
                return value;
            }
            else
            {
                long duration = value * toMilliseconds(unit);
                //log.debug("Duration in MS: " + duration);
                return duration;
            }
        }

        public long toMilliseconds(String unit)
        {
            if (unit == null)
            {
                return 0L;
            }
            else if (unit.Equals(SECOND))
            {
                return 1 * 1000L;
            }
            else if (unit.Equals(MINUTE))
            {
                return 60 * 1000L;
            }
            else if (unit.Equals(HOUR))
            {
                return 60 * 60 * 1000L;
            }
            else if (unit.Equals(DAY))
            {
                return 24 * 60 * 60 * 1000L;
            }
            else if (unit.Equals(MONTH))
            {
                return 30 * 24 * 60 * 60 * 1000L;
            }
            else if (unit.Equals(YEAR))
            {
                return 365 * 30 * 24 * 60 * 60 * 1000L;
            }
            else if (unit.Equals(WEEK))
            {
                return 7 * 24 * 60 * 60 * 1000L;
            }
            else
            {
                return 0L;
            }
        }


        public String toString()
        {
            StringBuilder buffer = new StringBuilder();
            buffer.Append(value);
            if (unit != null)
            {
                buffer.Append(unit);
            }
            return buffer.ToString();
        }

        /// <summary>时间间隔是否指工作时间</summary>
        public Boolean IsBusinessTime()
        {
            return isBusinessTime;
        }

        /// <summary>设置时间间隔的属性，isBusinessTime==true表示时间间隔指工作时间</summary>
        public void setBusinessTime(Boolean isBusinessTime)
        {
            this.isBusinessTime = isBusinessTime;
        }
    }
}
