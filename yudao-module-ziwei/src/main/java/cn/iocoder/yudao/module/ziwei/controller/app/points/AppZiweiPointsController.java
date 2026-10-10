package cn.iocoder.yudao.module.ziwei.controller.app.points;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ziwei.controller.app.points.vo.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.points.ZiweiPointsLogDO;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.points.ZiweiUserPointsDO;
import cn.iocoder.yudao.module.ziwei.service.points.ZiweiPointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.object.BeanUtils.toBean;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 次数权益")
@RestController
@RequestMapping("/ziwei/points")
@Validated
public class AppZiweiPointsController {

    @Resource
    private ZiweiPointsService pointsService;

    @GetMapping("/get")
    @Operation(summary = "获得当前用户的次数权益")
    public CommonResult<AppZiweiPointsRespVO> getPoints() {
        ZiweiUserPointsDO points = pointsService.getOrCreatePoints(getLoginUserId());
        return success(toBean(points, AppZiweiPointsRespVO.class));
    }

    @PostMapping("/consume")
    @Operation(summary = "消耗次数（AI 解读扣减，bizNo 幂等）")
    public CommonResult<AppZiweiPointsRespVO> consumePoints(@Valid @RequestBody AppZiweiPointsConsumeReqVO reqVO) {
        pointsService.consumePoints(getLoginUserId(), reqVO.getPoints(), reqVO.getBizNo(), reqVO.getRemark());
        ZiweiUserPointsDO points = pointsService.getOrCreatePoints(getLoginUserId());
        return success(toBean(points, AppZiweiPointsRespVO.class));
    }

    @PostMapping("/refund")
    @Operation(summary = "退还次数（AI 解读失败退款，仅限本人真实消耗流水）")
    public CommonResult<AppZiweiPointsRespVO> refundPoints(@Valid @RequestBody AppZiweiPointsRefundReqVO reqVO) {
        pointsService.refundPoints(getLoginUserId(), reqVO.getBizNo(), reqVO.getRemark());
        ZiweiUserPointsDO points = pointsService.getOrCreatePoints(getLoginUserId());
        return success(toBean(points, AppZiweiPointsRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得次数变动流水分页")
    public CommonResult<PageResult<AppZiweiPointsLogRespVO>> getPointsLogPage(@Valid AppZiweiPointsLogPageReqVO pageReq) {
        PageResult<ZiweiPointsLogDO> pageResult = pointsService.getPointsLogPage(getLoginUserId(), pageReq);
        return success(toBean(pageResult, AppZiweiPointsLogRespVO.class));
    }

}