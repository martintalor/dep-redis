-- ============================================================
--   名称：20190222_001_DEP_DDL_weiyao2
--   用途：创建DEP的表空间及用户
--   审核人：
-- ============================================================

prompt '正在创建DEP的表空间及用户'


--创建数据表空间
create tablespace TBS_DEP_DAT
logging
datafile '/opt/Oracle12c/oradata/orcl/DEP_DAT.dbf'
size 32m
autoextend on
next 32m
maxsize 2048m
extent management local;

--创建索引表空间
create tablespace TBS_DEP_IDX
logging
datafile '/opt/Oracle12c/oradata/orcl/DEP_IDX.dbf'
size 32m
autoextend on
next 32m
maxsize 2048m
extent management local;


--创建用户
create user DEP identified by dep
default tablespace TBS_DEP_DAT;


--赋权限
grant connect,resource,dba to DEP;


prompt '创建成功==>>DEP的表空间及用户'