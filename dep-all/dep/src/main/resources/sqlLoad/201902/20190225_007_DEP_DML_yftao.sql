-- ============================================================
--   名称：20190225_007_DEP_DML_yftao
--   用途：机构类型数据初始化-ORG_TYPE
--   审核人：
-- ============================================================

prompt '机构类型数据初始化insert Table ORG_TYPE'

truncate table DEP.ORG_TYPE;

prompt Importing table DEP.ORG_TYPE...
set feedback off
set define off
insert into DEP.ORG_TYPE (ORG_TYPE_DM, ORG_TYPE_MC)
values ('G', '公安局');

insert into DEP.ORG_TYPE (ORG_TYPE_DM, ORG_TYPE_MC)
values ('J', '检察院');

insert into DEP.ORG_TYPE (ORG_TYPE_DM, ORG_TYPE_MC)
values ('F', '法院');

insert into DEP.ORG_TYPE (ORG_TYPE_DM, ORG_TYPE_MC)
values ('S', '司法局');


insert into DEP.ORG_TYPE (ORG_TYPE_DM, ORG_TYPE_MC)
values ('Z', '政法委');


prompt Done.

commit;

prompt '初始化完成：物理节点路由数据初始化insert Table ORG_TYPE'