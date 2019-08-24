-- ============================================================
--   名称：20190510_001_DEP_DDL_yftao
--   用途：NODE_SEND_STATE、NODE_OPERATE_STATE 修改SPEND_TIME字段长度
--   审核人：
-- ============================================================

prompt '正在修改SPEND_TIME字段长度'
ALTER TABLE NODE_SEND_STATE modify(SPEND_TIME  NUMBER(13) default null);
ALTER TABLE NODE_OPERATE_STATE modify(SPEND_TIME  NUMBER(13) default null);
prompt '创建成功==>>修改SPEND_TIME字段长度'