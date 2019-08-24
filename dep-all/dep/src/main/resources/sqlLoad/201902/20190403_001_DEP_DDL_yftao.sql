-- ============================================================
--   名称：20190403_001_DEP_DDL_yftao
--   用途：NODE_SEND_STATE、NODE_OPERATE_STATE 添加SPEND_TIME
--   审核人：
-- ============================================================

prompt '正在NODE_SEND_STATE表添加上一节点SPEND_TIME'

ALTER TABLE NODE_SEND_STATE ADD(SPEND_TIME  NUMBER(6) default null);

comment on column NODE_SEND_STATE.SPEND_TIME
  is '环节耗时';

prompt '创建成功==>>NODE_SEND_STATE表添加上一节点SPEND_TIME'




prompt '正在NODE_OPERATE_STATE表添加上一节点SPEND_TIME'

ALTER TABLE NODE_OPERATE_STATE ADD(SPEND_TIME  NUMBER(6) default null);

comment on column NODE_OPERATE_STATE.SPEND_TIME
  is '环节耗时';

prompt '创建成功==>>NODE_OPERATE_STATE表添加上一节点SPEND_TIME'

