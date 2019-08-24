-- ============================================================
--   名称：20190225_001_DEP_DDL_weiyao2
--   用途：数据包子表-DATA_PACKAGE_SUB
--   审核人：
-- ============================================================

prompt '正在创建Table:DATA_PACKAGE_SUB（数据包子表）'
--drop table DEP.DATA_PACKAGE_SUB;
CREATE TABLE DEP.DATA_PACKAGE_SUB(
    SUB_PACKAGE_ID VARCHAR2(250) NOT NULL, -- 子包id
    PACKAGE_ID VARCHAR2(220) NOT NULL, -- 主包id
    PACKAGE_SIZE NUMBER(12,6) NOT NULL, -- 子数据包大小，单位默认MB
    CREATE_TIME DATE default sysdate NOT NULL, --创建时间
    CONSTRAINT PK_DATA_PACKAGE_SUB PRIMARY KEY  (SUB_PACKAGE_ID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.DATA_PACKAGE_SUB
  is '数据流转进程状态表';
comment on column DEP.DATA_PACKAGE_SUB.SUB_PACKAGE_ID
  is '子包id';
comment on column DEP.DATA_PACKAGE_SUB.PACKAGE_ID
  is '主包id';
comment on column DEP.DATA_PACKAGE_SUB.PACKAGE_SIZE
  is '子包大小';
comment on column DEP.DATA_PACKAGE_SUB.CREATE_TIME
  is '创建时间';

-- DATA_PACKAGE_SUB.PROCESS_ID = DATA_NODE_PROCESS.PROCESS_ID ( N:1 )
CREATE INDEX DEP.DATA_PACKAGE_SUB_PACKAGE_ID
  ON DEP.DATA_PACKAGE_SUB (PACKAGE_ID) TABLESPACE TBS_DEP_IDX;

CREATE INDEX DEP.DATA_PACKAGE_SUB_CREATE
  ON DEP.DATA_PACKAGE_SUB (CREATE_TIME) TABLESPACE TBS_DEP_IDX;

prompt '创建成功==>>Table:DATA_PACKAGE_SUB（数据包子表）'