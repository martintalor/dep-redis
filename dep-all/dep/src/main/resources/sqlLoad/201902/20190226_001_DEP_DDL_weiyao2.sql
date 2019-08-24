-- ============================================================
--   名称：20190226_001_DEP_DDL_weiyao2
--   用途：逻辑节点路由关系表,根据logical_server_node表枚举生成节点路由表，
--   审核人：
-- ============================================================

prompt '正在创建Table:SERVER_ROUTE（逻辑节点路由关系表）'
--drop table DEP.SERVER_ROUTE;
CREATE TABLE DEP.SERVER_ROUTE(
    LEFT_SERVER_NODE_ID VARCHAR2(10) NOT NULL, --左节点ID,为空则为"N"
    RIGHT_SERVER_NODE_ID VARCHAR2(10) NOT NULL, --右节点ID
    constraint PK_SERVER_ROUTE primary key (LEFT_SERVER_NODE_ID, RIGHT_SERVER_NODE_ID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.SERVER_ROUTE
  is '逻辑节点路由关系表';
comment on column DEP.SERVER_ROUTE.LEFT_SERVER_NODE_ID
  is '左节点ID';
comment on column DEP.SERVER_ROUTE.RIGHT_SERVER_NODE_ID
  is '右节点ID';


prompt '创建成功==>>Table:SERVER_ROUTE（逻辑节点路由关系表）'
