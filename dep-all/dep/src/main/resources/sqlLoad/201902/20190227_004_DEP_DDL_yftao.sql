-- ============================================================
--   名称：20190227_004_DEP_DDL_yftao
--   用途：DEP server管理表-DEP_SERVER
--   审核人：
-- ============================================================

prompt '正在创建Table:DEP_SERVER（DEP server管理表）'
--drop table DEP.DEP_SERVER;
CREATE TABLE DEP.DEP_SERVER(
    DEP_SERVER_ID VARCHAR2(32) NOT NULL, -- DEP server ID
    DEP_SERVER_REMARK VARCHAR2(100) NOT NULL , -- DEP server 名称
    SERVER_NODE_ID VARCHAR2(10), -- 归属逻辑节点
    DEP_SERVER_IP VARCHAR2(200) NOT NULL, -- DEP server 服务地址
    FLAG_DELETE VARCHAR2(1) NOT NULL, --Y/N,删除标志
    CONSTRAINT PK_DEP_SERVER PRIMARY KEY  (DEP_SERVER_ID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.DEP_SERVER
  is 'DEP server管理表';
comment on column DEP.DEP_SERVER.DEP_SERVER_ID
  is 'DEP server ID';
comment on column DEP.DEP_SERVER.DEP_SERVER_REMARK
  is 'DEP server 名称';
comment on column DEP.DEP_SERVER.SERVER_NODE_ID
  is '归属逻辑节点';
comment on column DEP.DEP_SERVER.DEP_SERVER_IP
  is 'DEP server 服务地址';
comment on column DEP.DEP_SERVER.FLAG_DELETE
  is '删除标志';


prompt '创建成功==>>Table:DEP_SERVER（DEP server管理表）'