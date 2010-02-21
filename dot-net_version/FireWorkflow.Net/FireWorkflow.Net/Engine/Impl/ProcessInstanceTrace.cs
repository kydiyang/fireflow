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
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Engine.Impl
{
    public enum ProcessInstanceTraceEnum
    {
        TRANSITION_TYPE,// = "Transition";
        LOOP_TYPE,// = "Loop";
        JUMPTO_TYPE,// = "JumpTo";
        WITHDRAW_TYPE,// = "Withdraw";
        REJECT_TYPE// = "Reject";
    }

    [Serializable]
    public class ProcessInstanceTrace
    {
        public String Id { get; set; }

        public String EdgeId { get; set; }

        public String FromNodeId { get; set; }

        public String ProcessInstanceId { get; set; }

        public Int32 StepNumber { get; set; }

        public String ToNodeId { get; set; }

        public ProcessInstanceTraceEnum Type { get; set; }

        public Int32 MinorNumber { get; set; }
    }
}
