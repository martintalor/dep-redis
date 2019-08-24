-- ============================================================
--   名称：20190412_001_DEP_DDL_yftao
--   用途：FTP_CONFIG 添加 NET_BRAKE_TYPE
--   审核人：
-- ============================================================

prompt '正在FTP_CONFIG表添加网闸类型字段NET_BRAKE_TYPE'

ALTER TABLE FTP_CONFIG ADD(NET_BRAKE_TYPE  VARCHAR2(2) DEFAULT '01');

comment on column FTP_CONFIG.NET_BRAKE_TYPE
  is '网闸类型,01无网闸，02单通，03双通';

prompt '创建成功==>>FTP_CONFIG表添加网闸类型字段NET_BRAKE_TYPE'



prompt '正在PACKAGE_GLOBAL_STATE表添加数据包状态确认方式字段FLAG_CONFIRM_TYPE'

ALTER TABLE PACKAGE_GLOBAL_STATE ADD(FLAG_CONFIRM_TYPE VARCHAR2(1) default '0' NOT NULL);
comment on column PACKAGE_GLOBAL_STATE.FLAG_CONFIRM_TYPE
  is '数据包状态确认方式,0代码确认, 1人工确认';

prompt '创建成功==>>PACKAGE_GLOBAL_STATE表添加数据包状态确认方式字段FLAG_CONFIRM_TYPE'


insert into Machine_node (NODE_ID, MACHINE_IP, SERVER_NODE_ID, NODE_REMARK, NODE_TYPE_DM, FLAG_ENABLE, FLAG_DELETE)
values ('Z05', '10.123.16.29', 'Z_SERV', '中心（政法委-检察院2）前置机', '02', 'Y', 'Y');
