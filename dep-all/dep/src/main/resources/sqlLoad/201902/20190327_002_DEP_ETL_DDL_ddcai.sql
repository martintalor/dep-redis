/*==============================================================*/
/* Table: DEP_ETL_JOB_RECORDERS                                 */
/*==============================================================*/
drop table DEP_ETL_JOB_RECORDERS;
create table DEP_ETL_JOB_RECORDERS
(
   ID                   VARCHAR2(150)        not null,
   JOB_ID               VARCHAR2(150)        not null,
   JOB_NAME             VARCHAR2(150)        not null,
   JOB_TYPE             SMALLINT             not null,
   JOB_PARAM            clob             DEFAULT NULL,
   START_TIME           DATE                 not null,
   END_TIME             DATE,
   JOB_STATUS           SMALLINT             not null,
   constraint PK_DEP_ETL_JOB_RECORDERS primary key (ID)
);

comment on column DEP_ETL_JOB_RECORDERS.ID is
'主键';

comment on column DEP_ETL_JOB_RECORDERS.JOB_ID is
'JOBID';

comment on column DEP_ETL_JOB_RECORDERS.JOB_NAME is
'JOB名称';

comment on column DEP_ETL_JOB_RECORDERS.JOB_TYPE is
'JOB类型(0-outJob,1-inJob)';

comment on column DEP_ETL_JOB_RECORDERS.JOB_PARAM is
'JOB参数';

comment on column DEP_ETL_JOB_RECORDERS.START_TIME is
'开始时间';

comment on column DEP_ETL_JOB_RECORDERS.END_TIME is
'结束时间';

comment on column DEP_ETL_JOB_RECORDERS.JOB_STATUS is
'job状态(0-RUNNING,1-DONE,2-FAILED)';
/

--生成JOB.SEQ文件 用来记录定时JOB的执行顺序
ALTER TABLE DEP_ETL_JOB_RECORDERS ADD(PRE_JOB_KEY  VARCHAR2(150) default null)  ;
ALTER TABLE DEP_ETL_JOB_RECORDERS ADD(CURR_JOB_KEY  VARCHAR2(150) default null)  ;

-- Add/modify columns
alter table DEP_ETL_JOB_RECORDERS add is_empty_element INTEGER default 0;
-- Add comments to the columns
comment on column DEP_ETL_JOB_RECORDERS.is_empty_element
  is '是否是空包(0-非空包,1-空包)';

-- Add/modify columns
alter table DEP_ETL_JOB_RECORDERS add job_result_info clob;
-- Add comments to the columns
comment on column DEP_ETL_JOB_RECORDERS.job_result_info
  is 'JOB执行结果信息';
/


/*==============================================================*/
/* Table: DIC_DEP_ETL_JOB                                       */
/*==============================================================*/
drop table DIC_DEP_ETL_JOB;
create table DIC_DEP_ETL_JOB
(
   JOB_ID               VARCHAR2(150)        not null,
   JOB_NAME             VARCHAR2(150)        not null,
   CREATE_TIME          DATE                 not null,
   SCHEDULED            VARCHAR2(2)   DEFAULT '0'  not null ,
   SCHEDULED_CRON       VARCHAR2(30),
   SCHEDULED_PARAM      VARCHAR2(1000) DEFAULT null,
   constraint PK_DIC_DEP_ETL_JOB primary key (JOB_ID)
);

comment on column DIC_DEP_ETL_JOB.JOB_ID is
'主键';

comment on column DIC_DEP_ETL_JOB.JOB_NAME is
'JOB名称';

comment on column DIC_DEP_ETL_JOB.CREATE_TIME is
'创建时间';

comment on column DIC_DEP_ETL_JOB.SCHEDULED is
'是否定时任务(0-不是定时；1-定时)';

comment on column DIC_DEP_ETL_JOB.SCHEDULED_CRON is
'定时执行的表达式cron';

comment on column DIC_DEP_ETL_JOB.SCHEDULED_PARAM is
'定时执行时所需要的参数';
/

ALTER TABLE dic_dep_etl_job ADD(JOB_REMARK VARCHAR2(250) default null);

-- Add/modify columns
alter table DIC_DEP_ETL_JOB add fixed_delay number default 2000000;
-- Add comments to the columns
comment on column DIC_DEP_ETL_JOB.fixed_delay
  is 'job执行间隔';

DELETE FROM DIC_DEP_ETL_JOB;
insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('1', 'out-director_synchronize', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '同步承办人-出库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('4', 'in-director_synchronize', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '同步承办人-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('5', 'in-suspect_synchronize', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, 'D同步嫌疑人-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('6', 'in-criminal_record_synchronize', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, 'D同步前科劣迹-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('7', 'out-case_exchange_cancel', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '案件流转撤销-出库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('9', 'out-legal_aid', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '法律援助-出库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('10', 'out-court_second_instance', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '法院二审结案-出库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('11', 'out-court_first_instance', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '法院一审结案-出库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('12', 'out-ratify_the_arrest', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '公安提请逮捕至检察院-出库', 3600000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('13', 'out-proc_to_court', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '检察院将案件结案到法院-出库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('14', 'out-proc_to_police', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '检察院送案公安-出库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('16', 'out-node_change', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '节点变更-出库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('17', 'out-appeal_for_prosecution', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '提请申请起诉意见-出库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('38', 'out-data_collect', to_date('08-04-2019', 'dd-mm-yyyy'), '1', '0 */30 * * * ?', '{"G_SERV":{"appIdFrom":"APP-G1","appIdTo":"APP-Z1"},"J_SERV":{"appIdFrom":"APP-J1","appIdTo":"APP-Z1"},"J2_SERV":{"appIdFrom":"APP-J2","appIdTo":"APP-Z1"},"F_SERV":{"appIdFrom":"APP-F1","appIdTo":"APP-Z1"},"S_SERV":{"appIdFrom":"APP-S1","appIdTo":"APP-Z1"}}', 'D中心平台数据汇集-出库', 3600000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('21', 'out-return_correction', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '退回补正-出库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('24', 'in-case_exchange_cancel', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '案件流转撤销-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('25', 'in-formula_document_exchange', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, 'D定时流转公示文书-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('26', 'in-legal_aid', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '法律援助-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('27', 'in-court_second_instance', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '法院二审结案--入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('28', 'in-court_first_instance', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '法院一审结案-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('29', 'in-ratify_the_arrest', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '公安提请逮捕至检察院-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('30', 'in-proc_to_court', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '检察院将案件结案到法院-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('31', 'in-proc_to_police', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '检察院送案公安-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('32', 'in-comm_parole_verdict', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, 'D减刑假释裁定书-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('33', 'in-node_change', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '节点变更-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('34', 'in-appeal_for_prosecution', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '提请申请起诉意见-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('35', 'in-return_correction', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '退回补正-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('36', 'in-released_from_prison', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, 'D刑满释放数据流转-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('37', 'in-opera_manage', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, 'D运营管理平台数据-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('42', 'in-data_collect', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, 'D中心平台数据汇集-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('43', 'in-comm_parole_exchange', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '减刑假释案件卷宗材料流转-入库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('44', 'out-comm_parole_exchange', to_date('01-04-2019', 'dd-mm-yyyy'), '0', null, null, '减刑假释案件卷宗材料流转-出库', 300000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('2', 'out-suspect_synchronize', to_date('01-04-2019', 'dd-mm-yyyy'), '1', '0 */30 * * * ?', '{"G_SERV":{"appIdFrom":"APP-G1","appIdTo":"APP-J1,APP-J2,APP-F1,APP-S1"},"J_SERV":{},"J2_SERV":{},"F_SERV":{},"S_SERV":{}}', 'D同步嫌疑人-出库', 3600000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('3', 'out-criminal_record_synchronize', to_date('01-04-2019', 'dd-mm-yyyy'), '1', '0 */30 * * * ?', '{"G_SERV":{},"J_SERV":{},"J2_SERV":{},"F_SERV":{"appIdFrom":"APP-F1","appIdTo":"APP-G1,APP-J1,APP-J2,APP-S1"},"S_SERV":{}}', 'D同步前科劣迹-出库', 3600000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('8', 'out-formula_document_exchange', to_date('01-04-2019', 'dd-mm-yyyy'), '1', '0 */30 * * * ?', '{"G_SERV":{},"J_SERV":{},"J2_SERV":{},"F_SERV":{"appIdFrom":"APP-F1","appIdTo":"APP-S1"},"S_SERV":{"appIdFrom":"APP-S1","appIdTo":"APP-F1"}}', 'D定时流转公示文书-入库', 3600000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('15', 'out-comm_parole_verdict', to_date('01-04-2019', 'dd-mm-yyyy'), '1', '0 */30 * * * ?', '{"G_SERV":{},"J_SERV":{},"J2_SERV":{},"F_SERV":{"appIdFrom":"APP-F1","appIdTo":"APP-S1"},"S_SERV":{"appIdFrom":"APP-S1","appIdTo":"APP-F1"}}', 'D减刑假释裁定书-出库', 3600000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('22', 'out-released_from_prison', to_date('01-04-2019', 'dd-mm-yyyy'), '1', '0 */30 * * * ?', '{"G_SERV":{"appIdFrom":"APP-G1","appIdTo":"APP-S1"},"J_SERV":{},"J2_SERV":{},"F_SERV":{},"S_SERV":{"appIdFrom":"APP-S1","appIdTo":"APP-G1"}}', 'D刑满释放数据流转-出库', 3600000);

insert into dic_dep_etl_job (JOB_ID, JOB_NAME, CREATE_TIME, SCHEDULED, SCHEDULED_CRON, SCHEDULED_PARAM, JOB_REMARK, FIXED_DELAY)
values ('23', 'out-opera_manage', to_date('01-04-2019', 'dd-mm-yyyy'), '1', '0 */30 * * * ?', '{"G_SERV":{"appIdFrom":"APP-G1","appIdTo":"APP-J1,APP-J2,APP-F1,APP-S1"},"J_SERV":{},"J2_SERV":{},"F_SERV":{},"S_SERV":{}}', 'D运营管理平台数据-出库', 3600000);


commit;