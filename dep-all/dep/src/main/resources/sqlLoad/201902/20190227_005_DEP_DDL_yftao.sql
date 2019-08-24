-- ============================================================
--   名称：20190227_005_DEP_DDL_yftao
--   用途：FTP配置表-FTP_CONFIG
--   审核人：
-- ============================================================

prompt
prompt Creating table FTP_CONFIG
--drop table DEP.FTP_CONFIG;
prompt =========================
prompt
create table FTP_CONFIG
(
  FTP_ID                   VARCHAR2(32) not null,
  NODE_ID                  VARCHAR2(15) not null,
  FTP_IP                   VARCHAR2(15) not null,
  FTP_PORT                 NUMBER(5) not null,
  USERNAME                 VARCHAR2(40) not null,
  PASSWORD                 VARCHAR2(100) not null,
  DATA_PACKAGE_FOLDER_UP   VARCHAR2(200) not null,
  DATA_PACKAGE_FOLDER_DOWN VARCHAR2(200) not null,
  ACK_PACKAGE_FOLDER_UP    VARCHAR2(200) not null,
  ACK_PACKAGE_FOLDER_DOWN  VARCHAR2(200) not null,
  TMP_PACKAGE_FOLDER       VARCHAR2(230) not null,
  CONNECT_MAX              NUMBER(3) not null,
  TIMEOUT                  NUMBER(3) not null,
  HEARTBEAT                NUMBER(3) not null
)
;
comment on table FTP_CONFIG
  is 'FTP配置表';
comment on column FTP_CONFIG.FTP_ID
  is 'ftp节点id';
comment on column FTP_CONFIG.NODE_ID
  is '物理机器节点id';
comment on column FTP_CONFIG.FTP_IP
  is 'ftp IP';
comment on column FTP_CONFIG.FTP_PORT
  is 'ftp端口';
comment on column FTP_CONFIG.USERNAME
  is '用户名';
comment on column FTP_CONFIG.PASSWORD
  is '密码';
comment on column FTP_CONFIG.DATA_PACKAGE_FOLDER_UP
  is '数据包存放的根路径，上传';
comment on column FTP_CONFIG.DATA_PACKAGE_FOLDER_DOWN
  is '数据包存放的根路径，下载';
comment on column FTP_CONFIG.ACK_PACKAGE_FOLDER_UP
  is 'ack包根路径，上传';
comment on column FTP_CONFIG.ACK_PACKAGE_FOLDER_DOWN
  is 'ack包根路径，下载';
comment on column FTP_CONFIG.TMP_PACKAGE_FOLDER
  is '联通性测试包';
comment on column FTP_CONFIG.CONNECT_MAX
  is 'FTP最大连接数，默认20';
comment on column FTP_CONFIG.TIMEOUT
  is '超时时间，单位分钟';
comment on column FTP_CONFIG.HEARTBEAT
  is 'FTP心跳探测间隔，单位秒,默认60秒';
alter table FTP_CONFIG
  add constraint PK_FTP_CONFIG primary key (FTP_ID);