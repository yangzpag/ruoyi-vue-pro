package cn.iocoder.yudao.module.ziwei.service.chart;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ziwei.controller.app.chart.vo.AppZiweiChartCreateReqVO;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.chart.ZiweiChartDO;
import cn.iocoder.yudao.module.ziwei.dal.mysql.chart.ZiweiChartMapper;
import cn.iocoder.yudao.module.ziwei.dal.mysql.chart.ZiweiChartQuotaMapper;
import cn.iocoder.yudao.module.ziwei.framework.db.ZiweiDbDialect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ziwei.enums.ErrorCodeConstants.ZIWEI_CHART_DATA_INVALID;
import static cn.iocoder.yudao.module.ziwei.enums.ErrorCodeConstants.ZIWEI_CHART_LIMIT;
import static cn.iocoder.yudao.module.ziwei.enums.ErrorCodeConstants.ZIWEI_CHART_NOT_FOUND;

/**
 * 命盘 Service 实现类
 */
@Service
@Validated
@Slf4j
public class ZiweiChartServiceImpl implements ZiweiChartService {

    /**
     * 每用户命盘保存上限
     */
    private static final int CHART_LIMIT_PER_USER = 20;

    @Resource
    private ZiweiChartMapper chartMapper;
    @Resource
    private ZiweiChartQuotaMapper quotaMapper;
    @Resource
    private ZiweiDbDialect dbDialect;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createChart(Long userId, AppZiweiChartCreateReqVO reqVO) {
        // 1. 校验 JSON 严格可解析（isTypeJSON 只看首尾括号，{broken} 也能通过）
        try {
            JSONUtil.parseObj(reqVO.getChartData());
        } catch (Exception e) {
            throw exception(ZIWEI_CHART_DATA_INVALID);
        }
        // 2. 原子占用配额：quota 懒创建 + 原子 UPDATE（used < 上限 才生效），
        //    跨方言严格保证 20 上限（并发下第二个请求 acquire 失败）
        if (dbDialect.isPostgreSQL()) {
            quotaMapper.insertIgnoreByUserId(userId);
        } else {
            quotaMapper.insertIgnoreByUserIdMysql(userId);
        }
        if (quotaMapper.acquireQuota(userId, CHART_LIMIT_PER_USER) == 0) {
            throw exception(ZIWEI_CHART_LIMIT);
        }
        // 3. 插入
        ZiweiChartDO chart = new ZiweiChartDO();
        chart.setMemberUserId(userId);
        chart.setName(reqVO.getName());
        chart.setChartData(reqVO.getChartData());
        chartMapper.insert(chart);
        return chart.getId();
    }

    @Override
    public PageResult<ZiweiChartDO> getChartPage(Long userId, PageParam pageParam) {
        return chartMapper.selectPageByUserId(userId, pageParam);
    }

    @Override
    public ZiweiChartDO getChart(Long userId, Long id) {
        ZiweiChartDO chart = chartMapper.selectById(id);
        if (chart == null || !Objects.equals(chart.getMemberUserId(), userId)) {
            throw exception(ZIWEI_CHART_NOT_FOUND);
        }
        return chart;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChart(Long userId, Long id) {
        ZiweiChartDO chart = chartMapper.selectById(id);
        if (chart == null || !Objects.equals(chart.getMemberUserId(), userId)) {
            throw exception(ZIWEI_CHART_NOT_FOUND);
        }
        int deleted = chartMapper.deleteById(id);
        // 仅在本次确有删除时才释放配额：并发重复删除（第二方 deleteById 命中 0 行）
        // 不能重复 release，否则配额虚低会突破 20 上限
        if (deleted > 0) {
            quotaMapper.releaseQuota(userId);
        }
    }

}
