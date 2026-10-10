package cn.iocoder.yudao.module.ziwei.dal.dataobject.chart;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 命盘配额 DO：每用户一行，used 为已用配额（原子 UPDATE 保证 20 上限跨方言严格成立）
 */
@TableName(value = "ziwei_chart_quota", autoResultMap = true)
@KeySequence("ziwei_chart_quota_seq")
@EqualsAndHashCode(callSuper = true)
@Data
public class ZiweiChartQuotaDO extends TenantBaseDO {

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
     * 已使用配额（当前命盘数）
     */
    private Integer used;

}
