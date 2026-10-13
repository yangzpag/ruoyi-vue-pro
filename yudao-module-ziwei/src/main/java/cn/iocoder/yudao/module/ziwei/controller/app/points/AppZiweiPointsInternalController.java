package cn.iocoder.yudao.module.ziwei.controller.app.points;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ziwei.controller.app.points.vo.AppZiweiPointsInternalConsumeReqVO;
import cn.iocoder.yudao.module.ziwei.controller.app.points.vo.AppZiweiPointsInternalRefundReqVO;
import cn.iocoder.yudao.module.ziwei.controller.app.points.vo.AppZiweiPointsRespVO;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.points.ZiweiUserPointsDO;
import cn.iocoder.yudao.module.ziwei.service.points.ZiweiPointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.object.BeanUtils.toBean;
import static cn.iocoder.yudao.module.ziwei.enums.ErrorCodeConstants.ZIWEI_INTERNAL_UNAUTHORIZED;

/**
 * 次数权益内部接口：仅供 Next.js BFF 服务端调用（BFF 在 AI 解读流程中扣减/退还次数）。
 *
 * 鉴权：共享密钥 X-Internal-Key（ziwei.internal-key 配置，两端一致）。
 * 不对最终用户开放——refund 若开放给用户，可在 AI 解读成功后自行退还，绕过计费。
 */
@Tag(name = "内部 - 次数权益")
@RestController
@RequestMapping("/ziwei/points/internal")
@Validated
public class AppZiweiPointsInternalController {

    @Resource
    private ZiweiPointsService pointsService;

    /**
     * 内部接口密钥；为空时视为未配置，拒绝所有请求（fail closed）
     */
    @Value("${ziwei.internal-key:}")
    private String internalKey;

    @PostMapping("/consume")
    @PermitAll // 免登录（BFF 服务端调用），实际鉴权靠 X-Internal-Key
    @Operation(summary = "消耗次数（BFF 服务端调用，bizNo 幂等）")
    public CommonResult<AppZiweiPointsRespVO> consumePoints(
            @RequestHeader(value = "X-Internal-Key", required = false) String key,
            @Valid @RequestBody AppZiweiPointsInternalConsumeReqVO reqVO) {
        checkKey(key);
        pointsService.consumePoints(reqVO.getUserId(), reqVO.getPoints(), reqVO.getBizNo(), reqVO.getRemark());
        return success(toBean(pointsService.getOrCreatePoints(reqVO.getUserId()), AppZiweiPointsRespVO.class));
    }

    @PostMapping("/refund")
    @PermitAll // 免登录（BFF 服务端调用），实际鉴权靠 X-Internal-Key
    @Operation(summary = "退还次数（BFF 服务端调用，仅限本人真实消耗流水）")
    public CommonResult<AppZiweiPointsRespVO> refundPoints(
            @RequestHeader(value = "X-Internal-Key", required = false) String key,
            @Valid @RequestBody AppZiweiPointsInternalRefundReqVO reqVO) {
        checkKey(key);
        pointsService.refundPoints(reqVO.getUserId(), reqVO.getBizNo(), reqVO.getRemark());
        return success(toBean(pointsService.getOrCreatePoints(reqVO.getUserId()), AppZiweiPointsRespVO.class));
    }

    private void checkKey(String key) {
        if (internalKey == null || internalKey.isEmpty() || key == null || key.isEmpty()
                || !java.security.MessageDigest.isEqual(
                        internalKey.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                        key.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
            throw exception(ZIWEI_INTERNAL_UNAUTHORIZED);
        }
    }

}
