-- ============================================================
--   名称：20190429_002_DEP_DML_weiyao2
--   用途：初始化NODE_ROUTE表
--   审核人：
-- ============================================================
prompt Importing table node_route...
set feedback off
set define off
insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('F01', 'F02', 'F_SERV', 'F_SERV', '法院服务器->前置', 'F', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('F02', 'Z03', 'F_SERV', 'Z_SERV', '法院前置->中心前置', 'F', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('Z03', 'ZSE01', 'Z_SERV', 'Z_SERV', '（法院-中心）前置机->中心服务器', 'F', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('G01', 'G02', 'G_SERV', 'G_SERV', '公安服务器->前置', 'G', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('G02', 'Z01', 'G_SERV', 'Z_SERV', '公安前置->中心前置', 'G', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('Z01', 'ZSE01', 'Z_SERV', 'Z_SERV', '（公安-中心）前置->中心服务器', 'G', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('J01', 'J02', 'J_SERV', 'J_SERV', '检察院服务器->检察院前置机', 'J', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('J02', 'Z02', 'J_SERV', 'Z_SERV', '检察院前置机->中心前置机', 'J', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('Z02', 'ZSE01', 'Z_SERV', 'Z_SERV', '（检察院-中心）前置机->中心服务器', 'J', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('S01', 'S02', 'S_SERV', 'S_SERV', '司法局服务器->司法局前置机', 'S', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('S02', 'Z04', 'S_SERV', 'Z_SERV', '司法局前置机->中心前置机', 'S', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('Z04', 'ZSE01', 'Z_SERV', 'Z_SERV', '（司法局-中心）前置机->中心服务器', 'S', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('J11', 'J12', 'J2_SERV', 'J2_SERV', '检察院服务器2->检察院前置机2', 'J2', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('J12', 'Z05', 'J2_SERV', 'Z_SERV', '检察院前置机2->中心前置机', 'J2', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

insert into node_route (LEFT_NODE_ID, RIGHT_NODE_ID, LEFT_SERVER_NODE, RIGHT_SERVER_NODE, ROUTE_NAME, ROUTE_TYPE, CREATE_TIME)
values ('Z05', 'ZSE01', 'Z_SERV', 'Z_SERV', '（检察院2-中心）前置机->中心服务器', 'J2', to_date('29-04-2019 23:00:26', 'dd-mm-yyyy hh24:mi:ss'));

commit;
prompt Done.



