-- ============================================================
--   名称：20190224_001_DEP_DDL_weiyao2
--   用途：应用配置表-NODE_APP
--   审核人：
-- ============================================================

prompt '正在创建Table:NODE_APP（应用配置表）'
--drop table DEP.NODE_APP;
CREATE TABLE DEP.NODE_APP(
    APP_ID VARCHAR2(10) NOT NULL, -- 业务应用id
    NODE_ID VARCHAR2(5) NOT NULL, -- 节点id，文件流转机器
    CAL_URL VARCHAR2(200) NOT NULL, -- 解压完通知应用接口
    APP_NAME VARCHAR2(50) NOT NULL, -- 应用名称
    APP_REMARK VARCHAR2(200) NOT NULL, -- 应用描述
    CONSTRAINT  PK_NODE_APP PRIMARY KEY  (APP_ID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.NODE_APP
  is '应用配置表';
comment on column DEP.NODE_APP.APP_ID
  is '业务应用id';
comment on column DEP.NODE_APP.NODE_ID
  is '节点id，文件流转机器';
comment on column DEP.NODE_APP.CAL_URL
  is '解压完通知应用接口';
comment on column DEP.NODE_APP.APP_NAME
  is '应用名称';
comment on column DEP.NODE_APP.APP_REMARK
  is '应用描述';

--节点id NODE_APP.node_id = MACHINE_NODE.node_id ( N:1 )
CREATE INDEX DEP.NODE_APP_NODE_ID
  ON DEP.NODE_APP (NODE_ID) TABLESPACE TBS_DEP_IDX;

prompt '创建成功==>>Table:NODE_APP（应用配置表）'