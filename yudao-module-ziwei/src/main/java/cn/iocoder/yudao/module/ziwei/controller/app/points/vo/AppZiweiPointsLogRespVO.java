package cn.iocoder.yudao.module.ziwei.controller.app.points.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 APP - 次数变动流水 Response VO")
@Data
public class AppZiweiPointsLogRespVO {

    @Schema(description = "流水编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "变动类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer changeType;

    @Schema(description = "变动次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "-1")
    private Integer points;

    @Schema(description = "变动后余额", requiredMode = Schema.RequiredMode.REQUIRED, example = "9")
    private Integer balanceAfter;

    @Schema(description = "关联业务编号", example = "PAY-20261010-001")
    private String bizNo;

    @Schema(description = "备注", example = "AI 解读消费")
    private String remark;

    @Schema(description = "变动时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}