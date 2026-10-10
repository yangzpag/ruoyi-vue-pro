package cn.iocoder.yudao.module.ziwei.enums.points;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 次数变动类型枚举
 */
@RequiredArgsConstructor
@Getter
public enum ZiweiPointsChangeTypeEnum implements ArrayValuable<Integer> {

    GRANT(1, "购买/奖励增加"),
    CONSUME(2, "AI 解读消耗"),
    REFUND(3, "退款返还"),
    ADMIN(4, "管理员调整");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ZiweiPointsChangeTypeEnum::getType).toArray(Integer[]::new);

    private final Integer type;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}