-- ============================================================
--   名称：20190315_001_DEP_DDL_xiliu5
--   用途：process表添加上一节点id
--   审核人：
-- ============================================================

prompt '正在process表添加上一节点id'

ALTER TABLE data_node_process ADD(FROM_NODE_ID  VARCHAR2(5) default null)  ;

comment on column data_node_process.FROM_NODE_ID
  is '发送节点id';

prompt '创建成功==>>process表添加上一节点id'