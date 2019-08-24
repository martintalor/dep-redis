CREATE TABLE DEP_ETL_JOB_RECORDERS_DETAIL(
    ID VARCHAR2(220) NOT NULL,    --主键
    PACKAGE_ID VARCHAR2(220) NOT NULL, -- 数据包名
    job_status NUMBER(3) NOT NULL, -- job执行状态 0正在执行 1成功 2失败
    job_log clob NOT NULL, -- job的执行log
    CREATE_TIME DATE DEFAULT SYSDATE NOT NULL,    -- 创建时间
    CONSTRAINT  detail_id PRIMARY KEY  (ID)
)tablespace TBS_DEP_DAT ;


comment on table DEP_ETL_JOB_RECORDERS_DETAIL
  is 'job执行详情表';
comment on column DEP_ETL_JOB_RECORDERS_DETAIL.ID
  is '主键uuid';
comment on column DEP_ETL_JOB_RECORDERS_DETAIL.PACKAGE_ID
  is '关联PACKAGE_ID';
comment on column DEP_ETL_JOB_RECORDERS_DETAIL.job_status
  is 'job执行状态';
comment on column DEP_ETL_JOB_RECORDERS_DETAIL.job_log
  is 'job执行日志';
comment on column DEP_ETL_JOB_RECORDERS_DETAIL.CREATE_TIME
  is '创建时间';


--索引 package_id
CREATE INDEX job_detail_id
  ON DEP_ETL_JOB_RECORDERS_DETAIL (package_id) TABLESPACE TBS_DEP_IDX;


