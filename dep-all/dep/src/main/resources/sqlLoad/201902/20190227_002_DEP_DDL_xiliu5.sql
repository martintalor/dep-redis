-- ============================================================
--   名称：20190227_002_DEP_DDL_xiliu5
--   用途：节点路由关系表,根据machine_node表枚举生成节点路由表，
--         如机构数为N,机构类型为5，生成节点为（N+1）*5，单组规则为N-G01、G01-G02、G02-Z01 )
--   审核人：
-- ============================================================

prompt '正在创建Table:NODE_ROUTE（节点路由关系表）'
drop table DEP.NODE_ROUTE;
CREATE TABLE DEP.NODE_ROUTE(
    LEFT_NODE_ID VARCHAR2(5) NOT NULL, --左节点ID,为空则为"N"
    RIGHT_NODE_ID VARCHAR2(5) NOT NULL, --右节点ID
    LEFT_SERVER_NODE  VARCHAR2(10),
    RIGHT_SERVER_NODE VARCHAR2(10),
    ROUTE_NAME        VARCHAR2(20),
    CREATE_TIME       TIMESTAMP(6),
    CONSTRAINT PK_NODE_ROUTE PRIMARY KEY (LEFT_NODE_ID, RIGHT_NODE_ID)
)tablespace TBS_DEP_DAT
/

comment on table NODE_ROUTE
  is '节点路由关系表';
comment on column NODE_ROUTE.LEFT_NODE_ID
  is '左节点ID';
comment on column NODE_ROUTE.RIGHT_NODE_ID
  is '右节点ID';
comment on column NODE_ROUTE.LEFT_SERVER_NODE
  is '左节点逻辑服务节点ID';
comment on column NODE_ROUTE.RIGHT_SERVER_NODE
  is '右节点逻辑服务节点ID';
comment on column NODE_ROUTE.ROUTE_NAME
  is '路由名称';
comment on column NODE_ROUTE.CREATE_TIME
  is '创建时间';


prompt '创建成功==>>Table:NODE_ROUTE（节点路由关系表）'
