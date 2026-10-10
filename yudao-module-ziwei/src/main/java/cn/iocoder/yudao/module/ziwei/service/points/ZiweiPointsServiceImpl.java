package cn.iocoder.yudao.module.ziwei.service.points;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ziwei.controller.app.points.vo.AppZiweiPointsLogPageReqVO;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.points.ZiweiPointsLogDO;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.points.ZiweiUserPointsDO;
import cn.iocoder.yudao.module.ziwei.dal.mysql.points.ZiweiPointsLogMapper;
import cn.iocoder.yudao.module.ziwei.dal.mysql.points.ZiweiUserPointsMapper;
import cn.iocoder.yudao.module.ziwei.enums.points.ZiweiPointsChangeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ziwei.enums.ErrorCodeConstants.ZIWEI_POINTS_BIZ_REPLAYED;
import static cn.iocoder.yudao.module.ziwei.enums.ErrorCodeConstants.ZIWEI_POINTS_INSUFFICIENT;
import static cn.iocoder.yudao.module.ziwei.enums.ErrorCodeConstants.ZIWEI_POINTS_INVALID_AMOUNT;
import static cn.iocoder.yudao.module.ziwei.enums.ErrorCodeConstants.ZIWEI_POINTS_REFUND_NO_CONSUME;

/**
 * 次数权益 Service 实现类
 */
@Service
@Validated
@Slf4j
public class ZiweiPointsServiceImpl implements ZiweiPointsService {

    @Resource
    private ZiweiUserPointsMapper userPointsMapper;
    @Resource
    private ZiweiPointsLogMapper pointsLogMapper;

    @Override
    public ZiweiUserPointsDO getOrCreatePoints(Long userId) {
        ZiweiUserPointsDO points = userPointsMapper.selectByUserId(userId);
        if (points != null) {
            return points;
        }
        // 懒创建：ON CONFLICT DO NOTHING 原子插入，PG 下并发安全（不能用 DuplicateKeyException
        // 兜底——PG 语句失败会中止整个事务，后续查询全部报错）
        userPointsMapper.insertIgnoreByUserId(userId);
        return userPointsMapper.selectByUserId(userId);
    }

    @Override
    public PageResult<ZiweiPointsLogDO> getPointsLogPage(Long userId, AppZiweiPointsLogPageReqVO pageReq) {
        return pointsLogMapper.selectPageByUserId(userId, pageReq);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPoints(Long userId, Integer changeType, Integer points, String bizNo, String remark) {
        if (points == null || points <= 0) {
            throw exception(ZIWEI_POINTS_INVALID_AMOUNT);
        }
        if (isReplayed(userId, bizNo)) {
            return; // 幂等：同一业务号已处理过，直接返回成功
        }
        getOrCreatePoints(userId); // 确保权益记录存在
        userPointsMapper.addBalance(userId, points);
        ZiweiUserPointsDO after = userPointsMapper.selectByUserId(userId);
        insertLog(userId, changeType, points, after.getBalance(), bizNo, remark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void consumePoints(Long userId, Integer points, String bizNo, String remark) {
        if (points == null || points <= 0) {
            throw exception(ZIWEI_POINTS_INVALID_AMOUNT);
        }
        if (isReplayed(userId, bizNo)) {
            return; // 幂等：同一业务号已处理过，直接返回成功
        }
        getOrCreatePoints(userId); // 确保权益记录存在
        int updated = userPointsMapper.deductBalance(userId, points);
        if (updated == 0) {
            throw exception(ZIWEI_POINTS_INSUFFICIENT);
        }
        ZiweiUserPointsDO after = userPointsMapper.selectByUserId(userId);
        insertLog(userId, ZiweiPointsChangeTypeEnum.CONSUME.getType(), -points, after.getBalance(), bizNo, remark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundPoints(Long userId, String originalBizNo, String remark) {
        // 1. 找到本人对应的 CONSUME 流水（退款必须对应真实消耗，防止公开接口被用于凭空加次数）
        ZiweiPointsLogDO consumeLog = pointsLogMapper.selectOne(new LambdaQueryWrapperX<ZiweiPointsLogDO>()
                .eq(ZiweiPointsLogDO::getMemberUserId, userId)
                .eq(ZiweiPointsLogDO::getBizNo, originalBizNo)
                .eq(ZiweiPointsLogDO::getChangeType, ZiweiPointsChangeTypeEnum.CONSUME.getType())
                .last("LIMIT 1"));
        if (consumeLog == null) {
            throw exception(ZIWEI_POINTS_REFUND_NO_CONSUME);
        }
        // 2. 退还：bizNo 加 :refund 后缀形成独立幂等锁（重复退款会被唯一索引拦截 → BIZ_REPLAYED）
        addPoints(userId, ZiweiPointsChangeTypeEnum.REFUND.getType(),
                -consumeLog.getPoints(), originalBizNo + ":refund", remark);
    }

    private void insertLog(Long userId, Integer changeType, Integer points,
                           Integer balanceAfter, String bizNo, String remark) {
        ZiweiPointsLogDO log = new ZiweiPointsLogDO();
        log.setMemberUserId(userId);
        log.setChangeType(changeType);
        log.setPoints(points);
        log.setBalanceAfter(balanceAfter);
        log.setBizNo(Objects.toString(bizNo, ""));
        log.setRemark(Objects.toString(remark, ""));
        try {
            pointsLogMapper.insert(log);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            // 与并发同 bizNo 请求竞态：唯一索引 uk_ziwei_points_log_biz 拦截，事务回滚保证不重复记账
            throw exception(ZIWEI_POINTS_BIZ_REPLAYED);
        }
    }

    private boolean isReplayed(Long userId, String bizNo) {
        return bizNo != null && !bizNo.isEmpty()
                && pointsLogMapper.selectByBizNo(userId, bizNo) != null;
    }

}