-- ============================================================
--   名称：20190225_002_DEP_DDL_weiyao2
--   用途：逻辑服务节点-LOGICAL_SERVER_NODE
--   审核人：
-- ============================================================

prompt '正在创建Table:LOGICAL_SERVER_NODE（逻辑服务节点）'
drop table DEP.LOGICAL_SERVER_NODE;
CREATE TABLE DEP.LOGICAL_SERVER_NODE(
    SERVER_NODE_ID VARCHAR2(10) NOT NULL, -- 逻辑服务节点id
    SERVER_NODE_NAME VARCHAR2(220) NOT NULL, -- 逻辑节点名称
    ORG_TYPE_DM VARCHAR2(2) NOT NULL, -- 机构类型，G公安，J检察院，F法院，S司法局，Z政法委
    SERVER_NODE_TYPE_DM VARCHAR2(2) NOT NULL, --逻辑节点类型 01、中心服务器 02
    CONSTRAINT PK_LOGICAL_SERVER_NODE PRIMARY KEY  (SERVER_NODE_ID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.LOGICAL_SERVER_NODE
  is '数据流转进程状态表';
comment on column DEP.LOGICAL_SERVER_NODE.SERVER_NODE_ID
  is '逻辑服务节点id';
comment on column DEP.LOGICAL_SERVER_NODE.SERVER_NODE_NAME
  is '逻辑节点名称';
comment on column DEP.LOGICAL_SERVER_NODE.ORG_TYPE_DM
  is '机构类型，G公安，J检察院，F法院，S司法局，Z政法委';
comment on column DEP.LOGICAL_SERVER_NODE.SERVER_NODE_TYPE_DM
  is '逻辑节点类型';


prompt '创建成功==>>Table:LOGICAL_SERVER_NODE（逻辑服务节点）'