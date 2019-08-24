
--更新FTP参数
UPDATE ftp_config SET timeout=2, heartbeat=60;

--记录目标节点入库结束时间
ALTER TABLE DEP_ETL_JOB_RECORDERS ADD(DEST_END_TIME  DATE default null)  ;
comment on column DEP_ETL_JOB_RECORDERS.DEST_END_TIME
  is '最后的目标节点入库结束时间';
--记录目标节点回调信息
ALTER TABLE DEP_ETL_JOB_RECORDERS ADD(DEST_CALLBACK_INFO  VARCHAR2(1000) default null)  ;
comment on column DEP_ETL_JOB_RECORDERS.DEST_CALLBACK_INFO
  is '目标节点回调信息（JSON数组，包含以下信息：目标节点appId, jobStatus, endTime）';
commit;