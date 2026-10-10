package cn.iocoder.yudao.module.ziwei.dal.dataobject.points;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 次数变动流水 DO
 */
@TableName(value = "ziwei_points_log", autoResultMap = true)
@KeySequence("ziwei_points_log_seq")
@EqualsAndHashCode(callSuper = true)
@Data
public class ZiweiPointsLogDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 会员用户编号
     */
    private Long memberUserId;
    /**
     * 变动类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.ziwei.enums.points.ZiweiPointsChangeTypeEnum}
     */
    private Integer changeType;
    /**
     * 变动次数（正负数）
     */
    private Integer points;
    /**
     * 变动后余额
     */
    private Integer balanceAfter;
    /**
     * 关联业务编号（如支付订单号）
     */
    private String bizNo;
    /**
     * 备注
     */
    private String remark;

}