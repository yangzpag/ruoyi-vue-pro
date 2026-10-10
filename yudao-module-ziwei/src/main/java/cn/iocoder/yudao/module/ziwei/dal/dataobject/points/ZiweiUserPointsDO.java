package cn.iocoder.yudao.module.ziwei.dal.dataobject.points;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户 AI 解读次数权益 DO
 *
 * uk_ziwei_points_user 唯一索引：基于 (member_user_id, tenant_id)
 */
@TableName(value = "ziwei_user_points", autoResultMap = true)
@KeySequence("ziwei_user_points_seq")
@EqualsAndHashCode(callSuper = true)
@Data
public class ZiweiUserPointsDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 会员用户编号
     *
     * 关联 member_user 的 id 字段
     */
    private Long memberUserId;
    /**
     * 当前剩余次数
     */
    private Integer balance;
    /**
     * 累计获得次数
     */
    private Integer totalGranted;
    /**
     * 累计消耗次数
     */
    private Integer totalConsumed;

}