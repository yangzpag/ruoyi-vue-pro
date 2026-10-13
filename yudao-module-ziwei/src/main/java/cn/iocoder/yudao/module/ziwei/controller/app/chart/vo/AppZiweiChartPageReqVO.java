package cn.iocoder.yudao.module.ziwei.controller.app.chart.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 APP - 我的命盘分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppZiweiChartPageReqVO extends PageParam {

}