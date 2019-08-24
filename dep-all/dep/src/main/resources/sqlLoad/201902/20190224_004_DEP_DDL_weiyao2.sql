-- ============================================================
--   名称：20190224_004_DEP_DDL_weiyao2
--   用途：数据流转进程表-DATA_NODE_PROCESS
--        每到一个节点产生一个新的任务进程，
--        一个package_id就是一个任务id，有分包时一个link对应多个process
--   审核人：
-- ============================================================

prompt '正在创建Table:DATA_NODE_PROCESS（数据流转进程表）'
drop table DEP.DATA_NODE_PROCESS;
CREATE TABLE DEP.DATA_NODE_PROCESS(
    PROCESS_ID VARCHAR2(50) NOT NULL, -- 流转进程id，PK
    PACKAGE_ID VARCHAR2(220) NOT NULL, --数据包名 group列
    NODE_ID VARCHAR2(5) NOT NULL, -- 所属物理节点 group 列
    TO_NODE_ID VARCHAR2(5) NOT NULL,-- 目标节点id
    CREATE_TIME DATE default sysdate NOT NULL, --创建时间
    CONSTRAINT PK_DATA_NODE_PROCESS PRIMARY KEY  (PROCESS_ID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.DATA_NODE_PROCESS
  is '节点路由关系表';
comment on column DEP.DATA_NODE_PROCESS.PROCESS_ID
  is '流转进程id';
comment on column DEP.DATA_NODE_PROCESS.PACKAGE_ID
  is '数据包名 group列';
comment on column DEP.DATA_NODE_PROCESS.NODE_ID
  is '所属物理节点 group 列';
comment on column DEP.DATA_NODE_PROCESS.TO_NODE_ID
  is '目标节点id';
comment on column DEP.DATA_NODE_PROCESS.CREATE_TIME
  is '创建时间';

-- 唯一索引
alter table DEP.DATA_NODE_PROCESS
  add constraint UNIQUE_DATA_NODE_PROCESS unique (PACKAGE_ID, NODE_ID, TO_NODE_ID);

CREATE INDEX DEP.DATA_NODE_PROCESS_CREATE
  ON DEP.DATA_NODE_PROCESS (CREATE_TIME) TABLESPACE TBS_DEP_IDX;

prompt '创建成功==>>Table:DATA_NODE_PROCESS（数据流转进程表）'