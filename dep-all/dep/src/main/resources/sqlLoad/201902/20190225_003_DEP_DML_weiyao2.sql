-- ============================================================
--   名称：20190225_003_DEP_DDL_weiyao2
--   用途：物理节点数据初始化-MACHINE_NODE
--   审核人：
-- ============================================================

prompt '物理节点数据初始化insert Table MACHINE_NODE'

truncate table DEP.Machine_node;

prompt Importing table DEP.Machine_node...
set feedback off
set define offf

insert into DEP.Machine_node (NODE_ID, MACHINE_IP, SERVER_NODE_ID, NODE_REMARK, NODE_TYPE_DM, FLAG_ENABLE, FLAG_DELETE)
values ('G01', '192.168.1.10', 'G_SERV', '公安文档服务器', '01', 'Y', 'Y');

insert into DEP.Machine_node (NODE_ID, MACHINE_IP, SERVER_NODE_ID, NODE_REMARK, NODE_TYPE_DM, FLAG_ENABLE, FLAG_DELETE)
values ('G02', '192.168.1.11', 'G_SERV', '公安前置机', '02', 'Y', 'Y');

insert into DEP.Machine_node (NODE_ID, MACHINE_IP, SERVER_NODE_ID, NODE_REMARK, NODE_TYPE_DM, FLAG_ENABLE, FLAG_DELETE)
values ('Z01', '192.168.2.10', 'Z_SERV', '中心（政法委-公安）前置机', '01', 'Y', 'Y');

insert into DEP.Machine_node (NODE_ID, MACHINE_IP, SERVER_NODE_ID, NODE_REMARK, NODE_TYPE_DM, FLAG_ENABLE, FLAG_DELETE)
values ('Z02', '192.168.2.11', 'Z_SERV', '中心（政法委-检察院）前置机', '01', 'Y', 'Y');

insert into DEP.Machine_node (NODE_ID, MACHINE_IP, SERVER_NODE_ID, NODE_REMARK, NODE_TYPE_DM, FLAG_ENABLE, FLAG_DELETE)
values ('Z03', '192.168.2.13', 'Z_SERV', '中心（政法委-法院）前置机', '01', 'Y', 'Y');

insert into DEP.Machine_node (NODE_ID, MACHINE_IP, SERVER_NODE_ID, NODE_REMARK, NODE_TYPE_DM, FLAG_ENABLE, FLAG_DELETE)
values ('Z04', '192.168.2.14', 'Z_SERV', '中心（政法委-司法局）前置机', '01', 'Y', 'Y');

insert into DEP.Machine_node (NODE_ID, MACHINE_IP, SERVER_NODE_ID, NODE_REMARK, NODE_TYPE_DM, FLAG_ENABLE, FLAG_DELETE)
values ('S01', '192.168.3.11', 'S_SERV', '司法局文档服务器', '01', 'Y', 'Y');

insert into DEP.Machine_node (NODE_ID, MACHINE_IP, SERVER_NODE_ID, NODE_REMARK, NODE_TYPE_DM, FLAG_ENABLE, FLAG_DELETE)
values ('S02', '192.168.3.12', 'S_SERV', '司法局前置机', '02', 'Y', 'Y');

insert into DEP.Machine_node (NODE_ID, MACHINE_IP, SERVER_NODE_ID, NODE_REMARK, NODE_TYPE_DM, FLAG_ENABLE, FLAG_DELETE)
values ('J01', '192.168.3.11', 'J_SERV', '检察院文档服务器', '01', 'Y', 'Y');

insert into DEP.Machine_node (NODE_ID, MACHINE_IP, SERVER_NODE_ID, NODE_REMARK, NODE_TYPE_DM, FLAG_ENABLE, FLAG_DELETE)
values ('J02', '192.168.3.12', 'J_SERV', '检察院前置机', '02', 'Y', 'Y');

insert into DEP.Machine_node (NODE_ID, MACHINE_IP, SERVER_NODE_ID, NODE_REMARK, NODE_TYPE_DM, FLAG_ENABLE, FLAG_DELETE)
values ('F01', '192.168.4.11', 'F_SERV', '司法局文档服务器', '01', 'Y', 'Y');

insert into DEP.Machine_node (NODE_ID, MACHINE_IP, SERVER_NODE_ID, NODE_REMARK, NODE_TYPE_DM, FLAG_ENABLE, FLAG_DELETE)
values ('F02', '192.168.5.12', 'F_SERV', '司法局前置机', '02', 'Y', 'Y');

prompt Done.

commit;

prompt '初始化完成：物理节点数据初始化insert Table MACHINE_NODE'