package cn.iocoder.yudao.module.ziwei.controller.app.points;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ziwei.controller.app.points.vo.AppZiweiPointsLogPageReqVO;
import cn.iocoder.yudao.module.ziwei.controller.app.points.vo.AppZiweiPointsLogRespVO;
import cn.iocoder.yudao.module.ziwei.controller.app.points.vo.AppZiweiPointsRespVO;
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

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.object.BeanUtils.toBean;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 次数权益查询接口（面向最终用户，只读）。
 *
 * 写操作（consume/refund）在 {@link AppZiweiPointsInternalController}，
 * 仅供 BFF 服务端携带内部密钥调用——不对最终用户开放，
 * 防止用户在 AI 解读成功后自行退款绕过计费。
 */
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

    @GetMapping("/page")
    @Operation(summary = "获得次数变动流水分页")
    public CommonResult<PageResult<AppZiweiPointsLogRespVO>> getPointsLogPage(@Valid AppZiweiPointsLogPageReqVO pageReq) {
        PageResult<ZiweiPointsLogDO> pageResult = pointsService.getPointsLogPage(getLoginUserId(), pageReq);
        return success(toBean(pageResult, AppZiweiPointsLogRespVO.class));
    }

}
