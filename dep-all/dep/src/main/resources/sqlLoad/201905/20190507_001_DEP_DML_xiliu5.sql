-- Add/modify columns
--给抽取记录表添加包名字段（不包含扩展名）
ALTER TABLE DEP_ETL_JOB_RECORDERS ADD(PACKAGE_ID  VARCHAR2(220) default null)  ;
comment on column DEP_ETL_JOB_RECORDERS.PACKAGE_ID
  is '数据主包名，不包含扩展名';
  
  --给抽取记录表添加包名字段（不包含扩展名）
ALTER TABLE ETL_TO_WORKFLOW_RECORDERS ADD(PACKAGE_ID  VARCHAR2(220) default null)  ;
comment on column ETL_TO_WORKFLOW_RECORDERS.PACKAGE_ID
  is '数据主包名，不包含扩展名';
  
--是否异常流转视图  根据NODE_ID找APP_ID
CREATE OR REPLACE VIEW  V_IS_UNUSUAL_TRANSFER AS
SELECT p.PACKAGE_ID AS PKG_ID,
	   n.app_id as APP_ID_TO,
	   CASE WHEN p.global_state_dm = '00' THEN 1 ELSE 0
	   END AS IS_UNUSUAL_TRANSFER
  FROM package_global_state p
  left join node_app n on n.node_id=p.to_node_id
   WITH READ ONLY;
  COMMIT;