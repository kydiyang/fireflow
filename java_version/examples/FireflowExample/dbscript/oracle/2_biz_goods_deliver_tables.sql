--demo business table
drop table T_Biz_TradeInfo;
create table T_Biz_TradeInfo(
  id varchar2(50),
  sn varchar2(50),
  goods_name varchar2(100),
  goods_type varchar2(50),
  quantity number(5),
  unit_price number(8,2),
  amount number(8,2),
  customer_name varchar2(50),
  customer_mobile varchar2(30),
  customer_phone_fax varchar2(30),
  customer_address varchar2(150),
  state varchar2(15),--payed,delivered
  payed_time date,
  delivered_time date
);

ALTER TABLE T_Biz_TradeInfo ADD CONSTRAINT PK_T_TradeInfo
	PRIMARY KEY (ID)
;
create unique index idx_t_TradeInfo_sn on T_Biz_TradeInfo(sn);

  
  
drop table T_BIZ_GD_TASKINSTANCE;

create table T_BIZ_GD_TASKINSTANCE(
	TASKINSTANCE_ID varchar2(50),
	sn	varchar2(50),
	goods_name varchar2(100),
	quantity number(5),
	customer_name varchar2(50)
);

alter table T_BIZ_GD_TASKINSTANCE add constraint PK_GD_TaskInstance primary key (TASKINSTANCE_ID);

create index idx_gd_taskinstance_sn on T_BIZ_GD_TASKINSTANCE(sn);
 