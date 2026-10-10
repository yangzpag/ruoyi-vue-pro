package cn.iocoder.yudao.module.ziwei.controller.app.points.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 APP - 次数权益信息 Response VO")
@Data
public class AppZiweiPointsRespVO {

    @Schema(description = "当前剩余次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer balance;

    @Schema(description = "累计获得次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer totalGranted;

    @Schema(description = "累计消耗次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer totalConsumed;

}