-- ============================================================
--   名称：20190225_008_DEP_DML_yftao
--   用途：服务器类型数据初始化-SERVER_NODE_TYPE
--   审核人：
-- ============================================================

prompt '服务器类型数据初始化insert Table SERVER_NODE_TYPE'

truncate table DEP.SERVER_NODE_TYPE;

prompt Importing table DEP.SERVER_NODE_TYPE...
set feedback off
set define off
insert into DEP.SERVER_NODE_TYPE (SERVER_NODE_TYPE_DM, SERVER_NODE_TYPE_MC)
values ('01', '中心服务器');

insert into DEP.SERVER_NODE_TYPE (SERVER_NODE_TYPE_DM, SERVER_NODE_TYPE_MC)
values ('02', '分支服务器');

insert into DEP.SERVER_NODE_TYPE (SERVER_NODE_TYPE_DM, SERVER_NODE_TYPE_MC)
values ('03', '中继服务器');

prompt Done.

commit;

prompt '初始化完成：物理节点路由数据初始化insert Table SERVER_NODE_TYPE'