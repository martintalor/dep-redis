-- ============================================================
--   名称：20190224_003_DEP_DDL_weiyao2
--   用途：物理节点链路表，根据from、to的appid加上节点路由表动态生成【data_package、NODE_LINK】
--   审核人：
-- ============================================================

prompt '正在创建Table:NODE_LINK（物理节点链路表）'
drop table DEP.NODE_LINK;
CREATE TABLE DEP.NODE_LINK(
    LINK_ID VARCHAR2(50) NOT NULL,--自生成主键，UUID
    PACKAGE_ID VARCHAR2(220) NOT NULL,--数据包名 group列
    TO_NODE_ID VARCHAR2(5) NOT NULL, --目标节点id，group列
    LEFT_NODE_ID VARCHAR2(5) NOT NULL, --左节点ID,为空则为"N"
    RIGHT_NODE_ID VARCHAR2(5) NOT NULL, --右节点ID
    ORDER_ID NUMBER(2) NOT NULL, -- 排序id
    CONSTRAINT PK_NODE_LINK PRIMARY KEY  (LINK_ID)
)tablespace TBS_DEP_DAT
/

comment on table DEP.NODE_LINK
  is '节点路由关系表';
comment on column DEP.NODE_LINK.LINK_ID
  is '自生成主键，UUID';
comment on column DEP.NODE_LINK.PACKAGE_ID
  is '数据包名 group列';
comment on column DEP.NODE_LINK.TO_NODE_ID
  is '目标节点 group列';
comment on column DEP.NODE_LINK.LEFT_NODE_ID
  is '左节点ID';
comment on column DEP.NODE_LINK.RIGHT_NODE_ID
  is '右节点ID';
comment on column DEP.NODE_LINK.ORDER_ID
  is '排序ID';

-- 唯一索引
alter table DEP.NODE_LINK
  add constraint UNIQUE_NODE_LINK unique (PACKAGE_ID, TO_NODE_ID, LEFT_NODE_ID, RIGHT_NODE_ID);

  --节点id NODE_LINK.PACKAGE_ID = DATA_PACKAGE.PACKAGE_ID ( N:1 )
CREATE INDEX DEP.NODE_LINK_PACKAGE_ID
  ON DEP.NODE_LINK (PACKAGE_ID) TABLESPACE TBS_DEP_IDX;


prompt '创建成功==>>Table:NODE_LINK（物理节点链路表）'