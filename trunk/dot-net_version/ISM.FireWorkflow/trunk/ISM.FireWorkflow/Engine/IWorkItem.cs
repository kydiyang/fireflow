using System;
using System.Collections.Generic;
using System.Text;
using ISM.FireWorkflow.Engine.Taskinstance;

namespace ISM.FireWorkflow.Engine
{
    /// <summary>
    /// 工作项对象。<br/>
    /// 对该对象的状态字段作如下规定：小于5的状态为“活动”状态，大于等于5的状态为“非活动”状态。
    /// 活动状态包括：INITIALIZED,RUNNING,SUSPENDED
    /// 非活动状态包括：COMPLETED,CANCELED
    /// </summary>
    public abstract class IWorkItem
    {
        /// <summary>初始化状态</summary>
        public const int INITIALIZED = 0;

        /// <summary>运行状态</summary>
        public const int RUNNING = 1;

        //被挂起
        //    public const int SUSPENDED = 3;


        /// <summary>已经结束</summary>
        public const int COMPLETED = 7;

        /// <summary>被撤销</summary>
        public const int CANCELED = 9;

        /// <summary>返回工作项的Id</summary>
        public abstract String getId();

        /// <summary>返回工作项的状态</summary>
        public abstract Int32 getState();

        /// <summary>初始化状态</summary>
        public abstract void setComments(String s);

        /// <summary>返回备注信息</summary>
        public abstract String getComments();

        /// <summary>返回创建时间</summary>
        public abstract DateTime getCreatedTime();

        /// <summary>返回签收时间。（改由方法getClaimedTime()完成）</summary>
        public abstract DateTime getSignedTime();

        /// <summary>返回签收时间</summary>
        public abstract DateTime getClaimedTime();

        /// <summary>返回结束时间</summary>
        public abstract DateTime getEndTime();

        /// <summary>返回操作员的Id</summary>
        public abstract String getActorId();

        /// <summary>返回任务实例</summary>
        public abstract ITaskInstance getTaskInstance();

        /// <summary>
        /// 签收工作项。如果任务实例的分配模式是ANY，则同一个任务实例的其他工作项将被删除。
        /// 如果任务是里的分配模式是ALL，则此操作不影响同一个任务实例的其他工作项的状态。<br/>
        /// 该方法命名不恰当，被public void claim()方法替代
        /// @throws org.fireflow.engine.EngineException
        /// @throws org.fireflow.kenel.KenelException
        /// @deprecated 
        /// </summary>
        public abstract void sign();// throws EngineException, KernelException;

        /// <summary>
        /// 签收工作项。如果任务实例的分配模式是ANY，则同一个任务实例的其他工作项将被删除。
        /// 如果任务是里的分配模式是ALL，则此操作不影响同一个任务实例的其他工作项的状态。<br/>
        /// 如果签收成功，则返回一个新的IWorkItem对象，并且更新当前WorkItem对象的状态修改成RUNNING状态，
        /// 更新ClaimedTime属性值。<br/>
        /// 如果签收失败，则返回null，且当前WorkItem的状态被修改为CANCELED<br/>
        /// 例如：同一个TaskInstance被分配给Actor_1和Actor_2，且分配模式是ANY，即便Actor_1和Actor_2同时执行
        /// 签收操作，也必然有一个人签收失败。系统对这种竞争性操作进行了同步。
        /// @throws org.fireflow.engine.EngineException
        /// @throws org.fireflow.kenel.KenelException
        /// </summary>
        /// <returns>如果签收成功，则返回一个新的IWorkItem对象；否则返回null</returns>
        public abstract IWorkItem claim();// throws EngineException, KernelException;

        /// <summary>
        /// 对已经结束的工作项执行取回操作<br/>
        /// 只有满足如下约束才能正确执行取回操作：<br/>
        /// 1) 下一个Activity只有Form类型的Task,没有Tool类型和Subflow类型的Task</br>
        /// 2) 下一个环节的所有WorkItem还没有被签收，都处于Initialized状态，<br/>
        /// 如果在本WorkItem成功执行了jumpTo操作或者loopTo操作，只要满足上述条件，也可以
        /// 成功执行withdraw。<br/>
        /// 该方法和IWorkflowSession.withdrawWorkItem(String workItemId)等价。
        /// @throws org.fireflow.engine.EngineException
        /// @throws org.fireflow.kenel.KenelException
        /// </summary>
        /// <returns>如果取回成功，则创建一个新的WorkItem 并返回该WorkItem</returns>
        public abstract IWorkItem withdraw();//throws EngineException, KernelException;

        /// <summary>
        /// 执行“拒收”操作，可以对已经签收的或者未签收的WorkItem拒收。<br/>
        /// 该操作必须满足如下条件：<br/>
        /// 1、前驱环节中没有没有Tool类型和Subflow类型的Task；<br/>
        /// 2、没有和当前TaskInstance并行的其他TaskInstance；<br/>
        /// 该方法和IWorkflowSession.rejectWorkItem(String workItemId)等价。
        /// @throws EngineException
        /// @throws KernelException
        /// </summary>
        public abstract void reject();//throws EngineException, KernelException;


        /// <summary>
        /// 执行“拒收”操作，可以对已经签收的或者未签收的WorkItem拒收。<br/>
        /// 该操作必须满足如下条件：<br/>
        /// 1、前驱环节中没有没有Tool类型和Subflow类型的Task；<br/>
        /// 2、没有合当前TaskInstance并行的其他TaskInstance；<br/>
        /// 该方法和IWorkflowSession.rejectWorkItem(String workItemId,String comments)等价。
        /// @throws EngineException
        /// @throws KernelException
        /// </summary>
        /// <param name="comments">备注信息，将被写入workItem.comments字段。</param>
        public abstract void reject(String comments);//throws EngineException, KernelException;

        /// <summary>
        /// 结束当前WorkItem；并由工作流引擎根据流程定义决定下一步操作。引擎的执行规则如下<br/>
        /// 1、工作流引擎首先判断该WorkItem对应的TaskInstance是否可以结束。
        /// 如果TaskInstance的assignment策略为ANY，或者，assignment策略为ALL且它所有的WorkItem都已经完成
        /// 则结束当前TaskInstance<br/>
        /// 2、判断TaskInstance对应的ActivityInstance是否可以结束。如果ActivityInstance的complete strategy
        /// 为ANY，或者，complete strategy为ALL且他的所有的TaskInstance都已经结束，则结束当前ActivityInstance<br/>
        /// 3、根据流程定义，启动下一个Activity，并创建相关的TaskInstance和WorkItem
        /// @throws org.fireflow.engine.EngineException
        /// @throws org.fireflow.kenel.KenelException
        /// </summary>
        public abstract void complete();// throws EngineException, KernelException;

        /// <summary>
        /// 结束当前WorkItem；并由工作流引擎根据流程定义决定下一步操作。引擎的执行规则如下<br/>
        /// 1、工作流引擎首先判断该WorkItem对应的TaskInstance是否可以结束。
        /// 如果TaskInstance的assignment策略为ANY，或者，assignment策略为ALL且它所有的WorkItem都已经完成
        /// 则结束当前TaskInstance<br/>
        /// 2、判断TaskInstance对应的ActivityInstance是否可以结束。如果ActivityInstance的complete strategy
        /// 为ANY，或者，complete strategy为ALL且他的所有的TaskInstance都已经结束，则结束当前ActivityInstance<br/>
        /// 3、根据流程定义，启动下一个Activity，并创建相关的TaskInstance和WorkItem
        /// @throws EngineException
        /// @throws KernelException 
        /// </summary>
        /// <param name="comments">备注信息</param>
        public abstract void complete(String comments);//throws EngineException, KernelException;

        /// <summary>
        /// 结束当前WorkItem；并由工作流引擎根据流程定义决定下一步操作。引擎的执行规则如下<br/>
        /// 1、工作流引擎首先判断该WorkItem对应的TaskInstance是否可以结束。
        /// 如果TaskInstance的assignment策略为ANY，或者，assignment策略为ALL且它所有的WorkItem都已经完成
        /// 则结束当前TaskInstance<br/>
        /// 2、判断TaskInstance对应的ActivityInstance是否可以结束。如果ActivityInstance的complete strategy
        /// 为ANY，或者，complete strategy为ALL且他的所有的TaskInstance都已经结束，则结束当前ActivityInstance<br/>
        /// 3、根据流程定义，启动下一个Activity，并创建相关的TaskInstance和WorkItem
        /// @throws EngineException
        /// @throws KernelException
        /// </summary>
        /// <param name="dynamicAssignmentHandler">通过动态分配句柄指定下一个环节的操作者。</param>
        /// <param name="comments">备注信息</param>
        public abstract void complete(DynamicAssignmentHandler dynamicAssignmentHandler, String comments);// throws EngineException, KernelException;

        /// <summary>
        /// 结束当前WorkItem，跳转到指定的Activity<br/>
        /// 只有满足如下条件的情况下，该方法才能成功执行，否则抛出EngineException，流程状态恢复到调用该方法之前的状态。<br/>
        /// 1)当前Activity和即将启动的Acitivty必须在同一个执行线上<br/>
        /// 2)当前Task的assignment为Task.ANY。或者当前Task的assignment为Task.ALL(汇签)，且本WorkItem结束后可以使得TaskInstance结束；与之相反的情况是，
        /// 尚有其他参与汇签的操作者没有完成其工作项，这时engine拒绝跳转操作<br/>
        /// 3)当前TaskInstance结束后,可以使得当前的ActivityInstance结束。与之相反的情况是，当前Activity包含了多个Task，且Activity的Complete Strategy是ALL，
        /// 尚有其他的TaskInstance仍然处于活动状态，这种情况下执行jumpTo操作会被拒绝。
        /// @throws org.fireflow.engine.EngineException 
        /// @throws org.fireflow.kenel.KenelException
        /// </summary>
        /// <param name="targetActivityId">下一个环节的ActivityId</param>
        public abstract void jumpTo(String targetActivityId);// throws EngineException, KernelException;

        /// <summary>
        /// 结束当前WorkItem，跳转到指定的Activity<br/>
        /// 只有满足如下条件的情况下，该方法才能成功执行，否则抛出EngineException，流程状态恢复到调用该方法之前的状态。<br/>
        /// 1)当前Activity和即将启动的Acitivty必须在同一个执行线上<br/>
        /// 2)当前Task的assignment为Task.ANY。或者当前Task的assignment为Task.ALL(汇签)，且本WorkItem结束后可以使得TaskInstance结束；与之相反的情况是，
        /// 尚有其他参与汇签的操作者没有完成其工作项，这时engine拒绝跳转操作<br/>
        /// 3)当前TaskInstance结束后,可以使得当前的ActivityInstance结束。与之相反的情况是，当前Activity包含了多个Task，且Activity的Complete Strategy是ALL，
        /// 尚有其他的TaskInstance仍然处于活动状态，这种情况下执行jumpTo操作会被拒绝。
        /// @throws EngineException
        /// @throws KernelException
        /// </summary>
        /// <param name="targetActivityId">下一个环节的id</param>
        /// <param name="comments">备注信息</param>
        public abstract void jumpTo(String targetActivityId, String comments);// throws EngineException, KernelException;

        /// <summary>
        /// 结束当前WorkItem，跳转到指定的Activity<br/>
        /// 只有满足如下条件的情况下，该方法才能成功执行，否则抛出EngineException，流程状态恢复到调用该方法之前的状态。<br/>
        /// 1)当前Activity和即将启动的Acitivty必须在同一个执行线上<br/>
        /// 2)当前Task的assignment为Task.ANY。或者当前Task的assignment为Task.ALL(汇签)，且本WorkItem结束后可以使得TaskInstance结束；与之相反的情况是，
        /// 尚有其他参与汇签的操作者没有完成其工作项，这时engine拒绝跳转操作<br/>
        /// 3)当前TaskInstance结束后,可以使得当前的ActivityInstance结束。与之相反的情况是，当前Activity包含了多个Task，且Activity的Complete Strategy是ALL，
        /// 尚有其他的TaskInstance仍然处于活动状态，这种情况下执行jumpTo操作会被拒绝。
        /// @throws org.fireflow.engine.EngineException
        /// @throws org.fireflow.kenel.KenelException
        /// </summary>
        /// <param name="targetActivityId">下一个环节的id</param>
        /// <param name="dynamicAssignmentHandler">可以通过该参数指定下一个环节的Actor，如果这个参数不为空，则引擎忽略下一个环节的Task定义中的AssignmentHandler</param>
        /// <param name="comments">备注信息</param>
        public abstract void jumpTo(String targetActivityId, DynamicAssignmentHandler dynamicAssignmentHandler, String comments);// throws EngineException, KernelException;

        /// <summary>
        /// 将工作项委派给其他人，自己的工作项变成CANCELED状态。返回新创建的工作项。
        /// </summary>
        /// <param name="actorId">接受任务的操作员Id</param>
        /// <returns>新创建的工作项</returns>
        public abstract IWorkItem reasignTo(String actorId);// throws EngineException;

        /// <summary>
        /// 将工作项委派给其他人，自己的工作项变成CANCELED状态。返回新创建的工作项
        /// </summary>
        /// <param name="actorId">接受任务的操作员Id</param>
        /// <param name="comments">相关的备注信息</param>
        /// <returns>新创建的工作项</returns>
        public abstract IWorkItem reasignTo(String actorId, String comments);// throws EngineException;

    }
}
