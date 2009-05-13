drop table T_FF_DF_WORKFLOWDEF cascade constraints;
drop table T_FF_RT_JOINPOINT cascade constraints;
drop table T_FF_RT_PROCESSINSTANCE cascade constraints;
drop table T_FF_RT_PROCINST_VAR cascade constraints;
drop table T_FF_RT_TASKINSTANCE cascade constraints;
drop table T_FF_RT_TOKEN cascade constraints;
drop table T_FF_RT_WORKITEM cascade constraints;
create table T_FF_DF_WORKFLOWDEF (ID varchar2(50 char) not null, PROCESS_ID varchar2(100 char) not null, NAME varchar2(100 char) not null, DISPLAY_NAME varchar2(128 char), DESCRIPTION varchar2(1024 char), VERSION number(10,0) not null, PROCESS_CONTENT clob, PUBLISHED number(1,0), PUBLISHER varchar2(50 char), PUBLISH_TIME timestamp, primary key (ID));
create table T_FF_RT_JOINPOINT (ID varchar2(50 char) not null, SYNCHRONIZER_ID varchar2(200 char) not null, VALUE number(10,0), ALIVE number(1,0), PROCESSINSTANCE_ID varchar2(50 char) not null, primary key (ID));
create table T_FF_RT_PROCESSINSTANCE (ID varchar2(50 char) not null, PROCESS_ID varchar2(100 char) not null, VERSION number(10,0) not null, NAME varchar2(100 char), DISPLAY_NAME varchar2(128 char), STATE number(10,0), CREATED_TIME timestamp, STARTED_TIME timestamp, EXPIRED_TIME timestamp, END_TIME timestamp, PARENT_PROCESSINSTANCE_ID varchar2(50 char), PARENT_TASKINSTANCE_ID varchar2(50 char), primary key (ID));
create table T_FF_RT_PROCINST_VAR (PROCESSINSTANCE_ID varchar2(50 char) not null, VALUE varchar2(255 char), NAME varchar2(255 char) not null, primary key (PROCESSINSTANCE_ID, NAME));
create table T_FF_RT_TASKINSTANCE (ID varchar2(50 char) not null, BIZ_TYPE varchar2(250 char) not null, TASK_ID varchar2(300 char) not null, ACTIVITY_ID varchar2(200 char) not null, NAME varchar2(100 char) not null, DISPLAY_NAME varchar2(128 char), STATE number(10,0), TASK_TYPE varchar2(10 char), CREATED_TIME timestamp not null, STARTED_TIME timestamp, EXPIRED_TIME timestamp, END_TIME timestamp, ASSIGNMENT_STRATEGY varchar2(10 char) not null, PROCESSINSTANCE_ID varchar2(50 char) not null, PROCESS_ID varchar2(100 char) not null, VERSION number(10,0) not null, primary key (ID));
create table T_FF_RT_TOKEN (ID varchar2(50 char) not null, ALIVE number(1,0) not null, VALUE number(10,0) not null, NODE_ID varchar2(200 char) not null, PROCESSINSTANCE_ID varchar2(50 char) not null, primary key (ID));
create table T_FF_RT_WORKITEM (ID varchar2(50 char) not null, STATE number(10,0) not null, CREATED_TIME timestamp not null, SIGNED_TIME timestamp, END_TIME timestamp, ACTOR_ID varchar2(50 char), COMMENTS varchar2(1024 char), TASKINSTANCE_ID varchar2(50 char) not null, primary key (ID));
alter table T_FF_RT_PROCINST_VAR add constraint FKD79C420D7AF471D8 foreign key (PROCESSINSTANCE_ID) references T_FF_RT_PROCESSINSTANCE;
alter table T_FF_RT_WORKITEM add constraint FK4131554DE2527DDC foreign key (TASKINSTANCE_ID) references T_FF_RT_TASKINSTANCE;

-- Add comments to the T_FF_RT_PROCESSINSTANCE columns 
comment on column T_FF_RT_PROCESSINSTANCE.ID
  is '流程实例Id';
comment on column T_FF_RT_PROCESSINSTANCE.PROCESS_ID
  is '流程Id';
comment on column T_FF_RT_PROCESSINSTANCE.VERSION
  is '流程版本';
comment on column T_FF_RT_PROCESSINSTANCE.NAME
  is '流程名称';
comment on column T_FF_RT_PROCESSINSTANCE.DISPLAY_NAME
  is '流程显示名';
comment on column T_FF_RT_PROCESSINSTANCE.STATE
  is '流程实例状态';
comment on column T_FF_RT_PROCESSINSTANCE.CREATED_TIME
  is '流程实例创建时间';
comment on column T_FF_RT_PROCESSINSTANCE.STARTED_TIME
  is '流程实例启动时间';
comment on column T_FF_RT_PROCESSINSTANCE.EXPIRED_TIME
  is '流程实例完成期限';
comment on column T_FF_RT_PROCESSINSTANCE.END_TIME
  is '流程实例结束时间';
comment on column T_FF_RT_PROCESSINSTANCE.PARENT_PROCESSINSTANCE_ID
  is '父流程实例Id';
comment on column T_FF_RT_PROCESSINSTANCE.PARENT_TASKINSTANCE_ID
  is '对应的父任务实例Id';
  
  
-- Add comments to the T_FF_RT_TASKINSTANCE columns 
comment on column T_FF_RT_TASKINSTANCE.ID
  is '任务实例Id';
comment on column T_FF_RT_TASKINSTANCE.BIZ_TYPE
  is '业务类别，用于支持hibernate等orm工具进行父子对象映射';
comment on column T_FF_RT_TASKINSTANCE.TASK_ID
  is '任务Id';
comment on column T_FF_RT_TASKINSTANCE.ACTIVITY_ID
  is '环节Id';
comment on column T_FF_RT_TASKINSTANCE.NAME
  is '名称';
comment on column T_FF_RT_TASKINSTANCE.DISPLAY_NAME
  is '显示名';
comment on column T_FF_RT_TASKINSTANCE.STATE
  is '任务实例状态';
comment on column T_FF_RT_TASKINSTANCE.TASK_TYPE
  is '任务类型，取值 FORM,TOOL,SUBFLOW';
comment on column T_FF_RT_TASKINSTANCE.CREATED_TIME
  is '任务实例创建时间';
comment on column T_FF_RT_TASKINSTANCE.STARTED_TIME
  is '任务实例启动时间';
comment on column T_FF_RT_TASKINSTANCE.EXPIRED_TIME
  is '任务实例完成期限';
comment on column T_FF_RT_TASKINSTANCE.END_TIME
  is '任务实例结束时间';
comment on column T_FF_RT_TASKINSTANCE.ASSIGNMENT_STRATEGY
  is '任务分配策略，取值 ALL,ANY';
comment on column T_FF_RT_TASKINSTANCE.PROCESSINSTANCE_ID
  is '流程实例Id（到T_FF_RT_ProcessInstance的外键）';
comment on column T_FF_RT_TASKINSTANCE.PROCESS_ID
  is '流程Id（冗余字段）';
comment on column T_FF_RT_TASKINSTANCE.VERSION
  is '流程版本（冗余字段）';  
  
-- Add comments to the T_FF_RT_WORKITEM columns 
comment on column T_FF_RT_WORKITEM.ID
  is '工单Id';
comment on column T_FF_RT_WORKITEM.STATE
  is '工单状态';
comment on column T_FF_RT_WORKITEM.CREATED_TIME
  is '工单创建时间';
comment on column T_FF_RT_WORKITEM.SIGNED_TIME
  is '工单签收时间';
comment on column T_FF_RT_WORKITEM.END_TIME
  is '工单结束时间';
comment on column T_FF_RT_WORKITEM.ACTOR_ID
  is '操作员Id';
comment on column T_FF_RT_WORKITEM.COMMENTS
  is '备注';
comment on column T_FF_RT_WORKITEM.TASKINSTANCE_ID
  is '任务实例Id（到T_FF_RT_TaskInstance的外键）';
  
  
-- Add comments to the T_FF_RT_PROCINST_VAR columns 
comment on column T_FF_RT_PROCINST_VAR.PROCESSINSTANCE_ID
  is '流程实例ID（到T_FF_RT_ProcessInstance的外键）';
comment on column T_FF_RT_PROCINST_VAR.VALUE
  is '实例变量值';
comment on column T_FF_RT_PROCINST_VAR.NAME
  is '实例变量名称';
  
  
-- Add comments to the T_FF_DF_WORKFLOWDEF columns 
comment on column T_FF_DF_WORKFLOWDEF.ID
  is '本记录的Id';
comment on column T_FF_DF_WORKFLOWDEF.PROCESS_ID
  is '流程Id';
comment on column T_FF_DF_WORKFLOWDEF.NAME
  is '流程名称';
comment on column T_FF_DF_WORKFLOWDEF.DISPLAY_NAME
  is '流程显示名';
comment on column T_FF_DF_WORKFLOWDEF.DESCRIPTION
  is '流程描述';
comment on column T_FF_DF_WORKFLOWDEF.VERSION
  is '流程版本';
comment on column T_FF_DF_WORKFLOWDEF.PROCESS_CONTENT
  is '流程定义文件';
comment on column T_FF_DF_WORKFLOWDEF.PUBLISHED
  is '发布状态';
comment on column T_FF_DF_WORKFLOWDEF.PUBLISHER
  is '发布人';
comment on column T_FF_DF_WORKFLOWDEF.PUBLISH_TIME
  is '发布时间';
  

