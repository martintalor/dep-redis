-- ============================================================
--   名称：20190224_006_DEP_DDL_weiyao2
--   用途：数据包操作状态表-NODE_OPERATE_STATE
--        每到一个节点产生一个新的任务进程，
--        每个文件服务器节点上一般都会数据包的操作状态
--   审核人：
-- ============================================================

prompt '正在创建Table:NODE_OPERATE_STATE（数据包操作状态表）'
drop table DEP.NODE_OPERATE_STATE;
CREATE TABLE DEP.NODE_OPERATE_STATE(
    UUID VARCHAR2(50) NOT NULL, -- 无意义主键
    PROCESS_ID VARCHAR2(50) NOT NULL, -- 进程id
    OPERATE_STATE_DM VARCHAR2(10) NOT NULL, -- 操作状态,A状态起始节点独有，A1压缩（含拆包）、A2加密，B状态中心节点独有，B1解密、B2加密，C节点目标节点独有，C1解密、C2解压，，普通传输节点（一般为FTP前置节点）无操作状态，只有流转状态
    ORDER_ID NUMBER(3) NOT NULL, -- 当前进程下的流转状态计数器
    CREATE_TIME DATE default sysdate NOT NULL, --创建时间
    UPDATE_TIME DATE default sysdate NOT NULL, --修改时间
    CONSTRAINT PK_NODE_OPERATE_STATE PRIMARY KEY  (UUID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.NODE_OPERATE_STATE
  is '数据流转进程状态表';
comment on column DEP.NODE_OPERATE_STATE.UUID
  is '无意义主键';
comment on column DEP.NODE_OPERATE_STATE.PROCESS_ID
  is '进程id';
comment on column DEP.NODE_OPERATE_STATE.OPERATE_STATE_DM
  is '数据包操作状态,A状态起始节点独有，A1压缩（含拆包）、A2加密，B状态中心节点独有，B1解密、B2加密，C节点目标节点独有，C1解密、C2解压，，普通传输节点（一般为FTP前置节点）无操作状态，只有流转状态';
comment on column DEP.NODE_OPERATE_STATE.ORDER_ID
  is ' 当前进程下的流转状态计数器';
comment on column DEP.NODE_OPERATE_STATE.CREATE_TIME
  is '创建时间';

-- NODE_OPERATE_STATE.PROCESS_ID = DATA_NODE_PROCESS.PROCESS_ID ( N:1 )
CREATE INDEX DEP.NODE_OPERATE_STATE_PROCESS_ID
  ON DEP.NODE_OPERATE_STATE (PROCESS_ID) TABLESPACE TBS_DEP_IDX;

CREATE INDEX DEP.NODE_OPERATE_STATE_CREATE
  ON DEP.NODE_OPERATE_STATE (CREATE_TIME) TABLESPACE TBS_DEP_IDX;

prompt '创建成功==>>Table:NODE_OPERATE_STATE（数据包操作状态表）'