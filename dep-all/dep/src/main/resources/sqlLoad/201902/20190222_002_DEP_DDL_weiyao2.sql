-- ============================================================
--   名称：20190222_002_DEP_DDL_weiyao2
--   用途：物理服务器节点表
--   审核人：
-- ============================================================

prompt '正在创建Table:MACHINE_NODE（物理服务器节点表）'
--drop table DEP.MACHINE_NODE;
CREATE TABLE DEP.MACHINE_NODE(
    NODE_ID VARCHAR2(5) NOT NULL, -- 物理机器节点,G01 公安局起始节点，S01 司法局起始节点
    MACHINE_IP VARCHAR2(15) NOT NULL, -- 物理机器IP
    SERVER_NODE_ID VARCHAR2(20) NOT NULL, -- 所属逻辑服务节点
    NODE_REMARK VARCHAR2(200) NOT NULL, --节点描述
    NODE_TYPE_DM VARCHAR2(2) NOT NULL, --节点类型：01 文件服务器，02、FTP服务器
    FLAG_ENABLE VARCHAR2(1) NOT NULL, --Y/N,选用标志
    FLAG_DELETE VARCHAR2(1) NOT NULL, --Y/N,有效标志
    CONSTRAINT  PK_MACHINE_NODE PRIMARY KEY  (NODE_ID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.MACHINE_NODE
  is '物理机器节点表';
comment on column DEP.MACHINE_NODE.NODE_ID
  is '物理机器节点,G01 公安局起始节点，S01 司法局起始节点';
comment on column DEP.MACHINE_NODE.MACHINE_IP
  is '物理机器IP';
comment on column DEP.MACHINE_NODE.SERVER_NODE_ID
  is '所属服务节点';
comment on column DEP.MACHINE_NODE.NODE_REMARK
  is '节点描述';
comment on column DEP.MACHINE_NODE.node_type_dm
  is '节点类型：01 文件服务器，02、FTP服务器';
comment on column DEP.MACHINE_NODE.FLAG_ENABLE
  is 'Y/N,选用标志';
comment on column DEP.MACHINE_NODE.FLAG_DELETE
  is 'Y/N,有效标志   ';

CREATE INDEX DEP.IDX_MACHINE_SERVER_NODE_ID
  ON DEP.MACHINE_NODE (SERVER_NODE_ID) TABLESPACE TBS_DEP_IDX;

prompt '创建成功==>>Table:MACHINE_NODE（物理服务器节点表）'