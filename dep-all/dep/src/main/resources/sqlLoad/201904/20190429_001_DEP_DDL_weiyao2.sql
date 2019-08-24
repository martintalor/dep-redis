-- ============================================================
--   名称：20190429_001_DEP_DDL_weiyao2
--   用途：删除node_route配置表，添加route_type字段，修正node_route含义
--   审核人：
-- ============================================================

prompt '正在备份node_route表'
create table node_route_bak  as select * from node_route;

prompt '删除node_route表'
drop table node_route;

-- Create table
create table NODE_ROUTE
(
  left_node_id      VARCHAR2(5) not null,
  right_node_id     VARCHAR2(5) not null,
  left_server_node  VARCHAR2(10),
  right_server_node VARCHAR2(10),
  route_name        VARCHAR2(50),
  route_type        VARCHAR2(2),
  create_time       DATE default SYSDATE,
  constraint PK_NODE_ROUTE primary key (LEFT_NODE_ID, RIGHT_NODE_ID)
) tablespace TBS_DEP_DAT;
-- Add comments to the columns
comment on column NODE_ROUTE.left_node_id
  is '左节点';
comment on column NODE_ROUTE.right_node_id
  is '右节点';
comment on column NODE_ROUTE.left_server_node
  is '左节点所属服务';
comment on column NODE_ROUTE.right_server_node
  is '右节点所属服务';
comment on column NODE_ROUTE.route_name
  is '路由名称';
comment on column NODE_ROUTE.route_type
  is '路由类型';
comment on column NODE_ROUTE.create_time
  is '创建时间';


prompt '创建成功==>>node_route'


