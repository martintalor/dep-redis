-- ============================================================
--   名称：20190225_009_DEP_DDL_weiyao2
--   用途：节点当前状态表-NODE_LINK_STATE
--   审核人：
-- ============================================================

prompt '正在创建Table:NODE_LINK_STATE（链路节点当前状态表）'
drop table DEP.NODE_LINK_STATE;
CREATE TABLE DEP.NODE_LINK_STATE(
    LINK_ID VARCHAR2(50) NOT NULL, -- DATA_LINK.LINK_ID 1：1
    PROCESS_ID VARCHAR2(50) NOT NULL, -- 进程id
    SEND_STATE_DM NUMBER(1) NOT NULL, -- 节点流转状态,1、已接收或已生成，2、正在发送（若有子节点可下钻查看子节点状态），3、全部发送,4异常
    NODE_ID VARCHAR2(5) NOT NULL, -- 所属物理节点
    CREATE_TIME DATE default sysdate NOT NULL, --创建时间
    UPDATE_TIME DATE default sysdate NOT NULL, --修改时间
    CONSTRAINT PK_NODE_LINK_STATE PRIMARY KEY  (LINK_ID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.NODE_LINK_STATE
  is '数据流转进程状态表';
comment on column DEP.NODE_LINK_STATE.LINK_ID
  is 'DATA_LINK.LINK_ID 节点链路ID';
comment on column DEP.NODE_LINK_STATE.PROCESS_ID
  is '进程id';
comment on column DEP.NODE_LINK_STATE.SEND_STATE_DM
  is '节点流转状态,1、已接收或已生成，2、正在发送（若有子节点可下钻查看子节点状态），3、全部发送,4异常';
comment on column DEP.NODE_LINK_STATE.NODE_ID
  is '当前节点';
comment on column DEP.NODE_LINK_STATE.CREATE_TIME
  is '创建时间';

-- NODE_LINK_STATE.PROCESS_ID = DATA_NODE_PROCESS.PROCESS_ID ( N:1 )
CREATE INDEX DEP.NODE_LINK_STATE_PROCESS_ID
  ON DEP.NODE_LINK_STATE (PROCESS_ID) TABLESPACE TBS_DEP_IDX;

CREATE INDEX DEP.NODE_LINK_STATE_NODE_ID
  ON DEP.NODE_LINK_STATE (NODE_ID) TABLESPACE TBS_DEP_IDX;

CREATE INDEX DEP.NODE_LINK_STATE_UPDATE
  ON DEP.NODE_LINK_STATE (UPDATE_TIME) TABLESPACE TBS_DEP_IDX;

prompt '创建成功==>>Table:NODE_LINK_STATE（链路节点当前状态表）'
