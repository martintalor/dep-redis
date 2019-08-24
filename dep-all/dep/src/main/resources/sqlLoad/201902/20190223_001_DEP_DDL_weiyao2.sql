-- ============================================================
--   名称：20190223_001_DEP_DDL_weiyao2
--   用途：创建数据包信息表-DATA_PACKAGE
--   审核人：
-- ============================================================

prompt '正在创建Table:DATA_PACKAGE（数据包信息表）'
drop table DEP.DATA_PACKAGE;
CREATE TABLE DEP.DATA_PACKAGE(
    PACKAGE_ID VARCHAR2(220) NOT NULL, -- 数据包名
    PACKAGE_SIZE NUMBER(12,6) NOT NULL, -- 数据包大小，单位默认MB
    APP_ID_FROM VARCHAR2(20) NOT NULL, -- 发送应用id
    APP_ID_TO VARCHAR2(200) NOT NULL, -- 目标应用id
    FOLDER_PATH VARCHAR2(200) DEFAULT NULL, -- 源文件夹路径
    PACKAGE_PATH VARCHAR2(200) DEFAULT NULL, -- 压缩包路径
    SEND_LEVEL VARCHAR2(1) DEFAULT '3' NOT NULL, -- 传输级别
    CREATE_TIME DATE DEFAULT SYSDATE NOT NULL,    -- 创建时间
    CONSTRAINT  PK_DATA_PACKAGE PRIMARY KEY  (PACKAGE_ID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.DATA_PACKAGE
  is '数据包信息表';
comment on column DEP.DATA_PACKAGE.PACKAGE_ID
  is '数据包名';
comment on column DEP.DATA_PACKAGE.PACKAGE_SIZE
  is '数据包大小';
comment on column DEP.DATA_PACKAGE.APP_ID_FROM
  is '发送应用id';
comment on column DEP.DATA_PACKAGE.APP_ID_TO
  is '目标应用id';
comment on column DEP.DATA_PACKAGE.FOLDER_PATH
  is '源文件夹路径';
comment on column DEP.DATA_PACKAGE.PACKAGE_PATH
  is '压缩包路径';
comment on column DEP.DATA_PACKAGE.SEND_LEVEL
  is '传输级别';
comment on column DEP.DATA_PACKAGE.CREATE_TIME
  is '创建时间 ';
  
--联合索引 发送与目标appid一般一起使用
CREATE INDEX DEP.DATA_PACKAGE_APPID
  ON DEP.DATA_PACKAGE (APP_ID_FROM,APP_ID_TO) TABLESPACE TBS_DEP_IDX;

CREATE INDEX DEP.DATA_PACKAGE_CREATE
  ON DEP.DATA_PACKAGE (CREATE_TIME) TABLESPACE TBS_DEP_IDX;

prompt '创建成功==>>Table:DATA_PACKAGE（数据包信息表）'