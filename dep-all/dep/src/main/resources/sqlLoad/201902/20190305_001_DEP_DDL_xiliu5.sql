-- ============================================================
--   名称：20190305_001_DEP_DDL_xiliu5
--   用途：服务器节点监控表
--   审核人：
-- ============================================================

prompt '正在创建Table:FTP_MONITOR（服务器节点监控表）'
drop table DEP.FTP_MONITOR;
CREATE TABLE DEP.FTP_MONITOR(
  ftp_id       VARCHAR2(50) not null,
  node_id      VARCHAR2(20) not null,
  machine_ip   VARCHAR2(15) not null,
  probe_time   TIMESTAMP(6) default sysdate not null,
  probe_result VARCHAR2(1) not null,
  CONSTRAINT PK_FTP_MONITOR PRIMARY KEY (FTP_ID)
)tablespace TBS_DEP_DAT
/

comment on column FTP_MONITOR.ftp_id
  is '节点ID';
comment on column FTP_MONITOR.node_id
  is '物理节点ID';
comment on column FTP_MONITOR.machine_ip
  is '冗余存储';
comment on column FTP_MONITOR.probe_time
  is '最近一次探测时间，秒级';
comment on column FTP_MONITOR.probe_result
  is '探测结果，Y为OK，N为ERROR';


prompt '创建成功==>>Table:FTP_MONITOR（服务器节点监控表）'
