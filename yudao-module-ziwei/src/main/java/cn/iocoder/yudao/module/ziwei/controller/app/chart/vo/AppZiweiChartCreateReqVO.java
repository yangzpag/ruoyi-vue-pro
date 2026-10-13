package cn.iocoder.yudao.module.ziwei.controller.app.chart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Schema(description = "用户 APP - 保存命盘 Request VO")
@Data
public class AppZiweiChartCreateReqVO {

    @Schema(description = "备注名", requiredMode = Schema.RequiredMode.REQUIRED, example = "1990-05-09 男")
    @NotBlank(message = "备注名不能为空")
    @Size(max = 32, message = "备注名不能超过 32 个字符")
    private String name;

    @Schema(description = "排盘结果 JSON（完整 ZiweiChart）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "排盘数据不能为空")
    @Size(max = 131072, message = "排盘数据过大（超过 128KB）")
    private String chartData;

}