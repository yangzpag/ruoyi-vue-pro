package cn.iocoder.yudao.module.ziwei.dal.dataobject.chart;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 保存的命盘 DO
 *
 * chart_data 为排盘引擎输出的完整 JSON（前端可直接反序列化回放）
 */
@TableName(value = "ziwei_chart", autoResultMap = true)
@KeySequence("ziwei_chart_seq")
@EqualsAndHashCode(callSuper = true)
@Data
public class ZiweiChartDO extends TenantBaseDO {

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
     * 备注名（如 "1990-05-09 男"）
     */
    private String name;
    /**
     * 排盘结果 JSON（完整 ZiweiChart，含出生信息）
     */
    private String chartData;

}