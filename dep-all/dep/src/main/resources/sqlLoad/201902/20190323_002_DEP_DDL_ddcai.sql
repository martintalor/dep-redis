-- ============================================================
--   名称：20190305_002_DEP_DDL_ddcai
--   用途：记录数据包每步执行情况
--   审核人：
-- ============================================================

prompt '正在创建Table:SECTION_STEP_RECORDERS（section 执行情况记录表）'
-- Create table
create table SECTION_STEP_RECORDERS
(
  id                   VARCHAR2(150) not null,
  section_name         VARCHAR2(1000),
  section_param        CLOB,
  doact_result         INTEGER,
  package_id           VARCHAR2(150),
  total_section_number INTEGER,
  create_time          TIMESTAMP(6) default SYSDATE,
  direction            INTEGER default 0
) tablespace TBS_DEP_DAT
/
-- Add comments to the table
comment on table SECTION_STEP_RECORDERS
  is 'section执行步骤';
-- Add comments to the columns
comment on column SECTION_STEP_RECORDERS.id
  is 'ID';
comment on column SECTION_STEP_RECORDERS.section_name
  is 'SECTION名称';
comment on column SECTION_STEP_RECORDERS.section_param
  is 'SECTION参数';
comment on column SECTION_STEP_RECORDERS.doact_result
  is 'SECTION执行结果：0开始执行，1执行完成';
comment on column SECTION_STEP_RECORDERS.package_id
  is '数据包ID';
comment on column SECTION_STEP_RECORDERS.total_section_number
  is '数据包section总数';
comment on column SECTION_STEP_RECORDERS.create_time
  is '创建时间';
comment on column SECTION_STEP_RECORDERS.direction
  is '数据包流转方向：0下载数据，1传数据';
-- Create/Recreate primary, unique and foreign key constraints
alter table SECTION_STEP_RECORDERS
  add constraint PK_SECTION_STEP_RECORDERS primary key (ID) ;

prompt '创建 SECTION_STEP_RECORDERS SUCCESS'
