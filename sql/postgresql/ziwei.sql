-- ----------------------------
-- 紫微权益模块表结构（PostgreSQL）
-- 模块：yudao-module-ziwei
-- 说明：幂等脚本，可重复执行；部署时随 sql/postgresql/ 其他脚本一并导入
-- ----------------------------

-- 1. 用户次数权益表（一个用户一条记录，懒创建）
CREATE SEQUENCE IF NOT EXISTS ziwei_user_points_seq START 1;
CREATE TABLE IF NOT EXISTS ziwei_user_points (
  id int8 NOT NULL DEFAULT nextval('ziwei_user_points_seq'),
  member_user_id int8 NOT NULL,
  balance int4 NOT NULL DEFAULT 0,
  total_granted int4 NOT NULL DEFAULT 0,
  total_consumed int4 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  CONSTRAINT pk_ziwei_user_points PRIMARY KEY (id),
  CONSTRAINT uk_ziwei_points_user UNIQUE (member_user_id, tenant_id)
);
COMMENT ON TABLE ziwei_user_points IS '用户AI解读次数权益表';
COMMENT ON COLUMN ziwei_user_points.member_user_id IS '会员用户编号（member_user.id）';
COMMENT ON COLUMN ziwei_user_points.balance IS '当前剩余次数';
COMMENT ON COLUMN ziwei_user_points.total_granted IS '累计获得次数';
COMMENT ON COLUMN ziwei_user_points.total_consumed IS '累计消耗次数';

-- 2. 次数变动流水表
CREATE SEQUENCE IF NOT EXISTS ziwei_points_log_seq START 1;
CREATE TABLE IF NOT EXISTS ziwei_points_log (
  id int8 NOT NULL DEFAULT nextval('ziwei_points_log_seq'),
  member_user_id int8 NOT NULL,
  change_type int2 NOT NULL,
  points int4 NOT NULL,
  balance_after int4 NOT NULL,
  biz_no varchar(64) NULL DEFAULT '',
  remark varchar(255) NULL DEFAULT '',
  tenant_id int8 NOT NULL DEFAULT 0,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  CONSTRAINT pk_ziwei_points_log PRIMARY KEY (id)
);
COMMENT ON TABLE ziwei_points_log IS '次数变动流水表';
COMMENT ON COLUMN ziwei_points_log.change_type IS '变动类型：1购买/奖励 2解读消耗 3退款返还 4管理员调整';
COMMENT ON COLUMN ziwei_points_log.points IS '变动次数（正负数）';
COMMENT ON COLUMN ziwei_points_log.balance_after IS '变动后余额';
COMMENT ON COLUMN ziwei_points_log.biz_no IS '关联业务编号（如支付订单号），用于幂等去重';

CREATE INDEX IF NOT EXISTS idx_ziwei_points_log_user ON ziwei_points_log (member_user_id, create_time);

-- 3. 幂等保护：同一用户同一业务号只允许一条有效流水（空 biz_no 不参与）
CREATE UNIQUE INDEX IF NOT EXISTS uk_ziwei_points_log_biz ON ziwei_points_log (member_user_id, biz_no) WHERE deleted = 0 AND biz_no <> '';

-- 4. 保存的命盘表
CREATE SEQUENCE IF NOT EXISTS ziwei_chart_seq START 1;
CREATE TABLE IF NOT EXISTS ziwei_chart (
  id int8 NOT NULL DEFAULT nextval('ziwei_chart_seq'),
  member_user_id int8 NOT NULL,
  name varchar(32) NOT NULL,
  chart_data text NOT NULL,
  tenant_id int8 NOT NULL DEFAULT 0,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  CONSTRAINT pk_ziwei_chart PRIMARY KEY (id)
);
COMMENT ON TABLE ziwei_chart IS '保存的命盘表';
COMMENT ON COLUMN ziwei_chart.name IS '备注名（如 1990-05-09 男）';
COMMENT ON COLUMN ziwei_chart.chart_data IS '排盘引擎输出的完整 JSON（前端反序列化回放）';

CREATE INDEX IF NOT EXISTS idx_ziwei_chart_user ON ziwei_chart (member_user_id, create_time);

-- 5. 命盘配额表：每用户一行，used 计数原子 UPDATE 保证 20 上限跨方言严格成立
CREATE SEQUENCE IF NOT EXISTS ziwei_chart_quota_seq START 1;
CREATE TABLE IF NOT EXISTS ziwei_chart_quota (
  id int8 NOT NULL DEFAULT nextval('ziwei_chart_quota_seq'),
  member_user_id int8 NOT NULL,
  used int4 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  CONSTRAINT pk_ziwei_chart_quota PRIMARY KEY (id),
  CONSTRAINT uk_ziwei_chart_quota_user UNIQUE (member_user_id, tenant_id)
);
COMMENT ON TABLE ziwei_chart_quota IS '命盘保存配额表（used=已用配额）';
