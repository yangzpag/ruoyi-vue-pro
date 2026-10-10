-- ----------------------------
-- 紫微权益模块表结构（MySQL 8）
-- 模块：yudao-module-ziwei
-- 说明：幂等脚本，可重复执行。PG 版见 sql/postgresql/ziwei.sql
-- 注意：幂等唯一索引 uk_ziwei_points_log_biz 在 PG 为部分唯一索引
--       （WHERE deleted=0 AND biz_no<>''）；MySQL 不支持部分索引，
--       采用 (member_user_id, biz_no, deleted) 唯一索引近似等价——
--       同一 (用户, 业务号) 仅允许一条未删除流水，软删历史后可重新处理。
-- ----------------------------

-- 1. 用户次数权益表（一个用户一条记录，懒创建）
CREATE TABLE IF NOT EXISTS `ziwei_user_points`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `member_user_id` bigint NOT NULL COMMENT '会员用户编号（member_user.id）',
  `balance` int NOT NULL DEFAULT 0 COMMENT '当前剩余次数',
  `total_granted` int NOT NULL DEFAULT 0 COMMENT '累计获得次数',
  `total_consumed` int NOT NULL DEFAULT 0 COMMENT '累计消耗次数',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  `creator` varchar(64) NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_ziwei_points_user` (`member_user_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户AI解读次数权益表';

-- 2. 次数变动流水表
CREATE TABLE IF NOT EXISTS `ziwei_points_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `member_user_id` bigint NOT NULL COMMENT '会员用户编号',
  `change_type` tinyint NOT NULL COMMENT '变动类型：1购买/奖励 2解读消耗 3退款返还 4管理员调整',
  `points` int NOT NULL COMMENT '变动次数（正负数）',
  `balance_after` int NOT NULL COMMENT '变动后余额',
  `biz_no` varchar(64) NULL DEFAULT '' COMMENT '关联业务编号（如支付订单号），用于幂等去重',
  `remark` varchar(255) NULL DEFAULT '' COMMENT '备注',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  `creator` varchar(64) NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_ziwei_points_log_biz` (`member_user_id`, `biz_no`, `deleted`) USING BTREE,
  INDEX `idx_ziwei_points_log_user`(`member_user_id`, `create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '次数变动流水表';
