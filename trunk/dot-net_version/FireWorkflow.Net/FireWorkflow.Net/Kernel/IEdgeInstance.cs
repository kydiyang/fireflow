using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Kernel
{
    public interface IEdgeInstance
    {
        String getId();

        /// <summary>
        /// 弧的权
        /// </summary>
        /// <returns></returns>
        int getWeight();
        //	public void setWeight(int i);


        INodeInstance getLeavingNodeInstance();

        void setLeavingNodeInstance(INodeInstance nodeInst);

        INodeInstance getEnteringNodeInstance();

        void setEnteringNodeInstance(INodeInstance nodeInst);

        /// <summary>
        /// 接受一个token，并移交给下一个节点
        /// </summary>
        /// <param name="token"></param>
        /// <returns>返回值是该transition计算出的token的alive值</returns>
        Boolean take(IToken token);// throws KernelException;
    }
}
