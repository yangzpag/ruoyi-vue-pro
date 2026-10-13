package cn.iocoder.yudao.module.ziwei.service.points;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ziwei.controller.app.points.vo.AppZiweiPointsLogPageReqVO;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.points.ZiweiPointsLogDO;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.points.ZiweiUserPointsDO;

/**
 * 次数权益 Service 接口
 */
public interface ZiweiPointsService {

    /**
     * 获得用户的次数权益（不存在时懒创建，初始 0 次）
     *
     * @param userId 会员用户编号
     * @return 次数权益
     */
    ZiweiUserPointsDO getOrCreatePoints(Long userId);

    /**
     * 获得用户的次数变动流水分页
     *
     * @param userId 会员用户编号
     * @param pageReq 分页请求
     * @return 流水分页
     */
    PageResult<ZiweiPointsLogDO> getPointsLogPage(Long userId, AppZiweiPointsLogPageReqVO pageReq);

    /**
     * 增加次数（购买/奖励/退款/管理员调整）
     *
     * @param userId 会员用户编号
     * @param changeType 变动类型
     * @param points 变动次数（必须 > 0）
     * @param bizNo 关联业务编号
     * @param remark 备注
     */
    void addPoints(Long userId, Integer changeType, Integer points, String bizNo, String remark);

    /**
     * 消耗次数（AI 解读扣减），余额不足时抛出 ZIWEI_POINTS_INSUFFICIENT
     *
     * @param userId 会员用户编号
     * @param points 消耗次数（必须 > 0）
     * @param bizNo 关联业务编号
     * @param remark 备注
     */
    void consumePoints(Long userId, Integer points, String bizNo, String remark);

    /**
     * 退还次数（AI 解读失败退款）：按原消耗流水的业务号退还
     *
     * 安全性：只能退还本人真实存在的一次 CONSUME 流水；同一业务号只能退还一次
     *（退款流水 bizNo = 原 bizNo + ":refund"，幂等锁复用唯一索引）。
     * 因此公开接口不会成为凭空加次数的入口。
     *
     * @param userId 会员用户编号
     * @param originalBizNo 原消耗流水业务号
     * @param remark 备注
     */
    void refundPoints(Long userId, String originalBizNo, String remark);

}