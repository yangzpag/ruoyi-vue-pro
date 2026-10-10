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

}