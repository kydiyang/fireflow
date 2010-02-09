using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model.Net;

namespace FireWorkflow.Net.Kernel
{
    /// <summary>
    /// Synchronizer的行为特征是：消耗掉输入的token并产生输出token
    /// </summary>
    public interface ISynchronizerInstance : INodeInstance
    {

        /// <summary>
        /// volume是同步器的容量
        /// </summary>
        /// <param name="k"></param>
        void setVolume(int k);
        int getVolume();


        // value是同步器当前的token值之和。
        // （已经转移到JoinPoint中，20080215）
        //	public void setValue(int value);
        //	public int getValue();

        Synchronizer getSynchronizer();
    }
}
