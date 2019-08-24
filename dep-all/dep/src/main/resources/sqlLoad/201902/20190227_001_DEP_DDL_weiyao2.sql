-- ============================================================
--   名称：20190227_001_DEP_DDL_weiyao2
--   用途：数据包当前状态表-PACKAGE_CURRENT_STATE
--   审核人：
-- ============================================================

prompt '正在创建Table:PACKAGE_CURRENT_STATE（数据包当前状态表）'
drop table DEP.PACKAGE_CURRENT_STATE;
CREATE TABLE DEP.PACKAGE_CURRENT_STATE(
    PACKAGE_ID VARCHAR2(250) NOT NULL, -- 数据包id,pk
    NODE_ID VARCHAR2(5) NOT NULL, -- 当前节点id,pk
    TO_NODE_ID VARCHAR2(5) NOT NULL,-- 目标节点id
    PROCESS_ID VARCHAR2(50) , -- 当前进程id
    SEND_STATE_DM VARCHAR2(2), -- 当前流程状态NODE_SEND_STATE.SEND_STATE_DM
    OPERATE_STATE_DM VARCHAR2(10) , -- 当前操作状态
    CREATE_TIME DATE default sysdate NOT NULL, --创建时间
    UPDATE_TIME DATE default sysdate NOT NULL, --修改时间
    CONSTRAINT PK_PACKAGE_CURRENT_STATE PRIMARY KEY  (PACKAGE_ID,NODE_ID,TO_NODE_ID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.PACKAGE_CURRENT_STATE
  is '数据流转进程状态表';
comment on column DEP.PACKAGE_CURRENT_STATE.PACKAGE_ID
  is '数据包id,pk';
comment on column DEP.PACKAGE_CURRENT_STATE.NODE_ID
  is '当前节点id,pk';
comment on column DEP.PACKAGE_CURRENT_STATE.TO_NODE_ID
  is '目标节点id，pk';
comment on column DEP.PACKAGE_CURRENT_STATE.PROCESS_ID
  is '进程id';
comment on column DEP.PACKAGE_CURRENT_STATE.SEND_STATE_DM
  is '当前流程状态NODE_SEND_STATE.NODE_SEND_STATE_DM';
comment on column DEP.PACKAGE_CURRENT_STATE.OPERATE_STATE_DM
  is '当前操作状态';
comment on column DEP.PACKAGE_CURRENT_STATE.CREATE_TIME
  is '创建时间';
comment on column DEP.PACK
AGE_CURRENT_STATE.CREATE_TIME
  is '修改时间';

CREATE INDEX DEP.CURRENT_PROCESS_ID
  ON DEP.PACKAGE_CURRENT_STATE (PROCESS_ID) TABLESPACE TBS_DEP_IDX;

CREATE INDEX DEP.PACKAGE_CURRENT_STATE_UPDATE
  ON DEP.PACKAGE_CURRENT_STATE (UPDATE_TIME) TABLESPACE TBS_DEP_IDX;

prompt '创建成功==>>Table:PACKAGE_CURRENT_STATE（数据包当前状态表）'