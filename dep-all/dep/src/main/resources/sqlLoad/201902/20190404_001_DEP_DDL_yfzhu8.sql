
-- ============================================================
--   名称：20190404_001_DEP_DDL_yfzhu8
--   用途：node_operate_state、package_current_state 更改operate_state_dm字段大小
--   审核人：
-- ============================================================

prompt '正在node_operate_state修改operate_state_dm字段大小'
alter table dep.node_operate_state modify operate_state_dm varchar2(10);
alter table dep.package_current_state modify operate_state_dm varchar2(10);
prompt '修改node_operate_state的operate_state_dm字段大小完成'