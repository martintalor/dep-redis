-- ============================================================
--   名称：20190224_005_DEP_DDL_weiyao2
--   用途：数据流转进程状态表-NODE_SEND_STATE
--        每到一个节点产生一个新的任务进程，
--        每个进程在一个节点上可能会产生新的状态
--   审核人：
-- ============================================================

prompt '正在创建Table:NODE_SEND_STATE（数据流转进程状态表）'
drop table DEP.NODE_SEND_STATE;
CREATE TABLE DEP.NODE_SEND_STATE(
    UUID VARCHAR2(50) NOT NULL, -- 无意义主键
    PROCESS_ID VARCHAR2(50) NOT NULL, -- 进程id
    SEND_STATE_DM VARCHAR2(2) NOT NULL, -- 节点流转状态,1、已接收或已生成，2、正在发送（若有子节点可下钻查看子节点状态），3、全部发送,4异常
    ORDER_ID NUMBER(3) NOT NULL, -- 当前进程下的流转状态计数器
    CREATE_TIME DATE default sysdate NOT NULL, --创建时间
    UPDATE_TIME DATE default sysdate NOT NULL, --修改时间
    CONSTRAINT PK_NODE_SEND_STATE PRIMARY KEY  (UUID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.NODE_SEND_STATE
  is '数据流转进程状态表';
comment on column DEP.NODE_SEND_STATE.UUID
  is '无意义主键';
comment on column DEP.NODE_SEND_STATE.PROCESS_ID
  is '进程id';
comment on column DEP.NODE_SEND_STATE.SEND_STATE_DM
  is '节点流转状态,1、已接收或已生成，2、正在发送（若有子节点可下钻查看子节点状态），3、全部发送,4异常';
comment on column DEP.NODE_SEND_STATE.ORDER_ID
  is ' 当前进程下的流转状态计数器';
comment on column DEP.NODE_SEND_STATE.CREATE_TIME
  is '创建时间';

-- NODE_SEND_STATE.PROCESS_ID = DATA_NODE_PROCESS.PROCESS_ID ( N:1 )
CREATE INDEX DEP.NODE_SEND_STATE_PROCESS_ID
  ON DEP.NODE_SEND_STATE (PROCESS_ID) TABLESPACE TBS_DEP_IDX;

CREATE INDEX DEP.NODE_SEND_STATE_CREATE
  ON DEP.NODE_SEND_STATE (CREATE_TIME) TABLESPACE TBS_DEP_IDX;

prompt '创建成功==>>Table:NODE_SEND_STATE（数据流转进程状态表）'

