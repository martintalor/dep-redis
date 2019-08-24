CREATE TABLE CA_STEP_RECORDERS(
    biz_sn VARCHAR2(220) NOT NULL,    --主键
    package_id VARCHAR2(220) NOT NULL, -- 数据包名
	file_url VARCHAR2(220) , -- 源文件路径
	efs_url VARCHAR2(220) , -- 加解密后文件路径
	callback_url VARCHAR2(220) , --回调地址 
    "MODE" VARCHAR2(220) NOT NULL,	-- 加密解密模式 encrypt：文件加密 decrypt：文件解密
    call_status NUMBER(3) NOT NULL, -- 调用接口状态 1调用成功 0调用失败
    back_status VARCHAR2(220) , -- 回调加密解密成功还是失败 success  error
    container_name VARCHAR2(220) , --容器名
    receiver VARCHAR2(220) , --加密文件接收方标识 如有多个接收方，参数已“;” 分号隔开，入参形式为：例：1;2;3;4
    CREATE_TIME DATE DEFAULT SYSDATE NOT NULL,    -- 创建时间
    CONSTRAINT  ca_id PRIMARY KEY  (biz_sn)
)tablespace TBS_DEP_DAT ;


comment on table CA_STEP_RECORDERS
  is 'ca加密解密当前状态记录表';
comment on column CA_STEP_RECORDERS.biz_sn
  is '主键uuid';
comment on column CA_STEP_RECORDERS.PACKAGE_ID
  is '关联PACKAGE_ID';
comment on column CA_STEP_RECORDERS.file_url
  is '源文件路径';
comment on column CA_STEP_RECORDERS.efs_url
  is '加解密后文件路径';
  comment on column CA_STEP_RECORDERS.callback_url
  is '回调地址';
  comment on column CA_STEP_RECORDERS."MODE"
  is '加密解密模式 encrypt：文件加密 decrypt：文件解密';
  comment on column CA_STEP_RECORDERS.call_status
  is '调用接口状态 1调用成功 0调用失败';
  comment on column CA_STEP_RECORDERS.container_name
  is '对原始数据进行数字签名使用得密钥标识,由CA服务器提供；注：mode为encrypt时必填';
  comment on column CA_STEP_RECORDERS.receiver
  is '公安：1，检察院：2，法院：3，司法：4，政法委：5';
    comment on column CA_STEP_RECORDERS.back_status
  is '回调加密解密成功还是失败 success  error';
comment on column CA_STEP_RECORDERS.CREATE_TIME
  is '创建时间';

  
--索引 package_id
CREATE INDEX ca_package_id
  ON CA_STEP_RECORDERS (package_id) TABLESPACE TBS_DEP_IDX;
  
  
