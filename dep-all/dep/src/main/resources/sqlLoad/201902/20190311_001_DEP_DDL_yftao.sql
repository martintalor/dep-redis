-- ============================================================
--   名称：20190311_001_DEP_DDL_yftao
--   用途：数据包全局状态表-PACKAGE_GLOBAL_STATE
--   审核人：
-- ============================================================

prompt '正在创建Table:PACKAGE_GLOBAL_STATE（数据包全局状态表）'
drop table DEP.PACKAGE_GLOBAL_STATE;
CREATE TABLE DEP.PACKAGE_GLOBAL_STATE(
    PACKAGE_ID VARCHAR2(250) NOT NULL, -- 数据包id,pk
    TO_NODE_ID VARCHAR2(5) NOT NULL,-- 目标节点id
    NODE_ID VARCHAR2(5), -- 当前节点id
    GLOBAL_STATE_DM VARCHAR2(2), -- 当前数据包全局状态 00异常 01交换中 02已完成
    CREATE_TIME DATE default sysdate NOT NULL, --创建时间
    UPDATE_TIME DATE default sysdate NOT NULL, --修改时间
    FLAG_CONFIRM_TYPE VARCHAR2(1) default '0' NOT NULL, --数据包状态确认方式 0代码确认 1人工确认
    CONSTRAINT PK_PACKAGE_GLOBAL_STATE PRIMARY KEY  (PACKAGE_ID,TO_NODE_ID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.PACKAGE_GLOBAL_STATE
  is '数据包全局状态表';
comment on column DEP.PACKAGE_GLOBAL_STATE.PACKAGE_ID
  is '数据包id,pk';
comment on column DEP.PACKAGE_GLOBAL_STATE.TO_NODE_ID
  is '目标节点id，pk';
comment on column DEP.PACKAGE_GLOBAL_STATE.NODE_ID
  is '当前节点id';
comment on column DEP.PACKAGE_GLOBAL_STATE.GLOBAL_STATE_DM
  is '当前数据包全局状态 00异常 01交换中 02已完成';
comment on column DEP.PACKAGE_GLOBAL_STATE.CREATE_TIME
  is '创建时间';
comment on column DEP.PACKAGE_CURRENT_STATE.UPDATE_TIME
  is '修改时间';
comment on column DEP.PACKAGE_CURRENT_STATE.FLAG_CONFIRM_TYPE
  is '数据包状态确认方式0代码确认1人工确认';

CREATE INDEX DEP.PACKAGE_GLOBAL_STATE_UPDATE
  ON DEP.PACKAGE_GLOBAL_STATE (UPDATE_TIME) TABLESPACE TBS_DEP_IDX;

prompt '创建成功==>>Table:PACKAGE_GLOBAL_STATE（数据包全局状态表）'