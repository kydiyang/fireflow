using System;
using System.Collections.Generic;
using System.Xml;
using System.Xml.Serialization;
using System.Text;

namespace FireWorkflow.Net.Model
{

    /// <summary>流程变量</summary>
    public class DataField : AbstractWFElement
    {
        /// <summary>字符串类型</summary>
        public const String STRING = "STRING";

        /// <summary>浮点类型</summary>
        public const String FLOAT = "FLOAT";

        /// <summary>双精度类型</summary>
        public const String DOUBLE = "DOUBLE";

        /// <summary>整数类型</summary>
        public const String INTEGER = "INTEGER";

        /// <summary>长整型</summary>
        public const String LONG = "LONG";

        /// <summary>日期时间类型</summary>
        public const String DATETIME = "DATETIME";

        /// <summary>布尔类型</summary>
        public const String BOOLEAN = "BOOLEAN";

        /// <summary>数据类型</summary>
        [XmlAttribute(AttributeName = "DataType")]
        public String dataType;

        /// <summary>初始值</summary>
        [XmlAttribute(AttributeName = "InitialValue")]
        public String initialValue;

        /// <summary>数据格式</summary>
        private String dataPattern;


        public DataField()
        {
            this.setDataType(STRING);
        }

        public DataField(WorkflowProcess workflowProcess, String name, String dataType)
            : base(workflowProcess, name)
        {
            setDataType(dataType);
        }

        /// <summary>返回流程变量的数据类型</summary>
        /// <returns>数据类型</returns>
        public String getDataType()
        {
            return dataType;
        }

        /// <summary>设置数据类型，其取值只能是
        /// DataField.STRING, DataField.FLOAT, DataField.DOUBLE, DataField.INTEGER,
        /// DataField.LONG,DataField.DATETIME, DataField.BOOLEAN
        /// </summary>
        /// <param name="dataType">数据类型</param>
        public void setDataType(String dataType)
        {
            if (dataType == null)
            {
                throw new NullReferenceException("Data type cannot be null");
            }
            this.dataType = dataType;
        }

        /// <summary>返回初始值
        /// <returns>初始值</returns>
        public String getInitialValue()
        {
            return initialValue;
        }

        /// <summary>设置初始值</summary>
        /// <param name="initialValue">初始值</param>
        public void setInitialValue(String initialValue)
        {
            this.initialValue = initialValue;
        }

        /// <summary>返回数据的pattern，目前主要用于日期类型。如 yyyyMMdd 等等。</summary>
        /// <returns>返回数据的pattern</returns>
        public String getDataPattern()
        {
            return dataPattern;
        }

        /// <summary>设置数据的pattern，目前主要用于日期类型。如 yyyyMMdd 等等。</summary>
        /// <param name="dataPattern">数据</param>
        public void setDataPattern(String dataPattern)
        {
            this.dataPattern = dataPattern;
        }
    }
}
