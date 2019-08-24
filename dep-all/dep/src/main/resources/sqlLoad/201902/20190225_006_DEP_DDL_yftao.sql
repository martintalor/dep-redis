-- ============================================================
--   名称：20190225_006_DEP_DDL_yftao
--   用途：服务器类型表
--   审核人：
-- ============================================================

prompt '正在创建Table:SERVER_NODE_TYPE（服务器类型表）'
drop table DEP.SERVER_NODE_TYPE;
CREATE TABLE DEP.SERVER_NODE_TYPE(
    SERVER_NODE_TYPE_DM VARCHAR2(10) NOT NULL, -- 服务器类型代码
    SERVER_NODE_TYPE_MC VARCHAR2(20) NOT NULL, --服务器类型名称
    CONSTRAINT  PK_SERVER_NODE_TYPE PRIMARY KEY  (SERVER_NODE_TYPE_DM)
)tablespace TBS_DEP_DAT
/

comment on table DEP.SERVER_NODE_TYPE
  is '服务器类型表';
comment on column DEP.SERVER_NODE_TYPE.SERVER_NODE_TYPE_DM
  is '服务器类型代码';
comment on column DEP.SERVER_NODE_TYPE.SERVER_NODE_TYPE_MC
  is '服务器类型名称';

prompt '创建成功==>>Table:SERVER_NODE_TYPE（服务器类型表）'