package cn.iocoder.yudao.module.ziwei.controller.app.points.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Schema(description = "用户 APP - 消耗次数 Request VO")
@Data
public class AppZiweiPointsConsumeReqVO {

    @Schema(description = "消耗次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "消耗次数不能为空")
    @Min(value = 1, message = "消耗次数必须大于 0")
    @Max(value = 100, message = "单次消耗次数过大")
    private Integer points;

    @Schema(description = "关联业务编号（全局唯一，用于幂等与退款）", requiredMode = Schema.RequiredMode.REQUIRED, example = "AI-20261010-xxx")
    @NotBlank(message = "业务编号不能为空")
    @Size(max = 64, message = "业务编号过长")
    private String bizNo;

    @Schema(description = "备注", example = "AI 解读消费")
    @Size(max = 255, message = "备注过长")
    private String remark;

}