package cn.iocoder.yudao.module.ziwei.controller.app.points.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Schema(description = "内部 - 退还次数 Request VO")
@Data
public class AppZiweiPointsInternalRefundReqVO {

    @Schema(description = "会员用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "101")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "原消耗流水的业务编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "AI-20261010-xxx")
    @NotBlank(message = "业务编号不能为空")
    @Size(max = 64, message = "业务编号过长")
    private String bizNo;

    @Schema(description = "备注", example = "AI 解读失败退款")
    @Size(max = 255, message = "备注过长")
    private String remark;

}
