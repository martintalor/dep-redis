-- ============================================================
--   名称：20190225_005_DEP_DDL_yftao
--   用途：机构类型表
--   审核人：
-- ============================================================

prompt '正在创建Table:ORG_TYPE（机构类型表）'
drop table DEP.ORG_TYPE;
CREATE TABLE DEP.ORG_TYPE(
    ORG_TYPE_DM VARCHAR2(10) NOT NULL, -- 机构类型代码
    ORG_TYPE_MC VARCHAR2(20) NOT NULL, --机构类型名称
    CONSTRAINT  PK_ORG_TYPE PRIMARY KEY  (ORG_TYPE_DM)
)tablespace TBS_DEP_DAT
/

comment on table DEP.ORG_TYPE
  is '机构类型表';
comment on column DEP.ORG_TYPE.ORG_TYPE_DM
  is '机构类型代码';
comment on column DEP.ORG_TYPE.ORG_TYPE_MC
  is '机构类型名称';

prompt '创建成功==>>Table:ORG_TYPE（机构类型表）'