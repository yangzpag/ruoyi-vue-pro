package cn.iocoder.yudao.module.ziwei.controller.app.chart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 APP - 命盘信息 Response VO")
@Data
public class AppZiweiChartRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "备注名", requiredMode = Schema.RequiredMode.REQUIRED, example = "1990-05-09 男")
    private String name;

    @Schema(description = "排盘结果 JSON（列表页为 null，详情页返回）", example = "{...}")
    private String chartData;

    @Schema(description = "保存时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}