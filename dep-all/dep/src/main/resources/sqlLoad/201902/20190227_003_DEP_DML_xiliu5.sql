-- ============================================================
--   名称：20190227_003_DEP_DML_xiliu5
--   用途：物理节点路由数据初始化-NODE_ROUTE
--   审核人：
-- ============================================================

prompt '物理节点路由数据初始化insert Table NODE_ROUTE'

truncate table DEP.Machine_node;

prompt Importing table DEP.NODE_ROUTE...
set feedback off
set define off
insert into DEP.NODE_ROUTE (left_node_id, right_node_id, left_server_node, right_server_node, route_name, create_time)
values ('N', 'G01', 'N', 'G_SERV', null, null);
insert into DEP.NODE_ROUTE (left_node_id, right_node_id, left_server_node, right_server_node, route_name, create_time)
values ('G01', 'G02', 'G_SERV', 'G_SERV', null, null);
insert into DEP.NODE_ROUTE (left_node_id, right_node_id, left_server_node, right_server_node, route_name, create_time)
values ('G02', 'Z01', 'G_SERV', 'Z_SERV', null, null);
insert into DEP.NODE_ROUTE (left_node_id, right_node_id, left_server_node, right_server_node, route_name, create_time)
values ('N', 'J01', 'N', 'J_SERV', null, null);
insert into DEP.NODE_ROUTE (left_node_id, right_node_id, left_server_node, right_server_node, route_name, create_time)
values ('J01', 'J02', 'J_SERV', 'J_SERV', null, null);
insert into DEP.NODE_ROUTE (left_node_id, right_node_id, left_server_node, right_server_node, route_name, create_time)
values ('J02', 'Z02', 'J_SERV', 'Z_SERV', null, null);
insert into DEP.NODE_ROUTE (left_node_id, right_node_id, left_server_node, right_server_node, route_name, create_time)
values ('N', 'F01', 'N', 'F_SERV', null, null);
insert into DEP.NODE_ROUTE (left_node_id, right_node_id, left_server_node, right_server_node, route_name, create_time)
values ('F01', 'F02', 'F_SERV', 'F_SERV', null, null);
insert into DEP.NODE_ROUTE (left_node_id, right_node_id, left_server_node, right_server_node, route_name, create_time)
values ('F02', 'Z03', 'F_SERV', 'Z_SERV', null, null);
insert into DEP.NODE_ROUTE (left_node_id, right_node_id, left_server_node, right_server_node, route_name, create_time)
values ('N', 'S01', 'N', 'S_SERV', null, null);
insert into DEP.NODE_ROUTE (left_node_id, right_node_id, left_server_node, right_server_node, route_name, create_time)
values ('S01', 'S02', 'S_SERV', 'S_SERV', null, null);
insert into DEP.NODE_ROUTE (left_node_id, right_node_id, left_server_node, right_server_node, route_name, create_time)
values ('S02', 'Z04', 'S_SERV', 'Z_SERV', null, null);

prompt Done.

commit;

prompt '初始化完成：物理节点路由数据初始化insert Table NODE_ROUTE'