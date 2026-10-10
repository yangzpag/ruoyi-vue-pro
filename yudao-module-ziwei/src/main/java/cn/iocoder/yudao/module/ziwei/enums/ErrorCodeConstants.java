package cn.iocoder.yudao.module.ziwei.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * ziwei 模块的错误码枚举类
 *
 * 1_030_000_000 号段
 */
public interface ErrorCodeConstants {

    // ========== 次数权益 1_030_000_000 ==========
    ErrorCode ZIWEI_POINTS_INSUFFICIENT = new ErrorCode(1_030_000_000, "AI 解读次数不足");
    ErrorCode ZIWEI_POINTS_INVALID_AMOUNT = new ErrorCode(1_030_000_001, "次数变动量必须大于 0");
    ErrorCode ZIWEI_POINTS_BIZ_REPLAYED = new ErrorCode(1_030_000_002, "该业务已处理过，请刷新查看结果");
    ErrorCode ZIWEI_POINTS_REFUND_NO_CONSUME = new ErrorCode(1_030_000_003, "未找到对应的解读消费记录，无法退款");

    // ========== 命盘 1_030_001_000 ==========
    ErrorCode ZIWEI_CHART_NOT_FOUND = new ErrorCode(1_030_001_000, "命盘不存在或已删除");
    ErrorCode ZIWEI_CHART_LIMIT = new ErrorCode(1_030_001_001, "命盘保存数量已达上限（20 个）");
    ErrorCode ZIWEI_CHART_DATA_INVALID = new ErrorCode(1_030_001_002, "排盘数据格式不正确");

}