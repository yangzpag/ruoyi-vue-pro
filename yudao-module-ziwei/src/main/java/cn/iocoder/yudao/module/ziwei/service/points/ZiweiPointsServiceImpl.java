package cn.iocoder.yudao.module.ziwei.service.points;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ziwei.controller.app.points.vo.AppZiweiPointsLogPageReqVO;
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
import static cn.iocoder.yudao.module.ziwei.enums.ErrorCodeConstants.ZIWEI_POINTS_INSUFFICIENT;
import static cn.iocoder.yudao.module.ziwei.enums.ErrorCodeConstants.ZIWEI_POINTS_INVALID_AMOUNT;

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
        // 懒创建：首次查询时初始化 0 次权益
        points = new ZiweiUserPointsDO();
        points.setMemberUserId(userId);
        points.setBalance(0);
        points.setTotalGranted(0);
        points.setTotalConsumed(0);
        try {
            userPointsMapper.insert(points);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            // 并发首次查询：唯一索引兜底，返回已存在记录
            points = userPointsMapper.selectByUserId(userId);
        }
        return points;
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
        getOrCreatePoints(userId); // 确保权益记录存在
        int updated = userPointsMapper.deductBalance(userId, points);
        if (updated == 0) {
            throw exception(ZIWEI_POINTS_INSUFFICIENT);
        }
        ZiweiUserPointsDO after = userPointsMapper.selectByUserId(userId);
        insertLog(userId, ZiweiPointsChangeTypeEnum.CONSUME.getType(), -points, after.getBalance(), bizNo, remark);
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
        pointsLogMapper.insert(log);
    }

}