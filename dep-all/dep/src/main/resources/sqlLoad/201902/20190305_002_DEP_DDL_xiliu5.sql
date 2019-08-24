-- ============================================================
--   名称：20190305_002_DEP_DDL_xiliu5
--   用途：DEP_SERVER监控表
--   审核人：
-- ============================================================

prompt '正在创建Table:SERVER_MONITOR（DEP_SERVER监控表）'
drop table DEP.SERVER_MONITOR;
CREATE TABLE DEP.SERVER_MONITOR(
  dep_server_id VARCHAR2(32) not null,
  dep_server_ip VARCHAR2(200),
  probe_time    TIMESTAMP(6) default SYSDATE,
  probe_result  VARCHAR2(1),
  CONSTRAINT PK_DEP_SERVER_ID PRIMARY KEY (DEP_SERVER_ID)
)tablespace TBS_DEP_DAT
/

comment on column SERVER_MONITOR.dep_server_id
  is '交换平台服务节点ID';
comment on column SERVER_MONITOR.dep_server_ip
  is '数据交换平台服务器IP地址，冗余存储';
comment on column SERVER_MONITOR.probe_time
  is '主动探测取到的时间';
comment on column SERVER_MONITOR.probe_result
  is '探测结果，Y为OK，N为ERROR';


prompt '创建成功==>>Table:SERVER_MONITOR（服务器节点监控表）'
