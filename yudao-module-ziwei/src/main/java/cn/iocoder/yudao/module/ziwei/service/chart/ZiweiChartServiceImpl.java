package cn.iocoder.yudao.module.ziwei.service.chart;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ziwei.controller.app.chart.vo.AppZiweiChartCreateReqVO;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.chart.ZiweiChartDO;
import cn.iocoder.yudao.module.ziwei.dal.mysql.chart.ZiweiChartMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    @Override
    public Long createChart(Long userId, AppZiweiChartCreateReqVO reqVO) {
        // 1. 校验 JSON 可解析（防止存入垃圾数据导致回放失败）
        if (!JSONUtil.isTypeJSON(reqVO.getChartData())) {
            throw exception(ZIWEI_CHART_DATA_INVALID);
        }
        // 2. 校验数量上限
        if (chartMapper.selectCountByUserId(userId) >= CHART_LIMIT_PER_USER) {
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
    public void deleteChart(Long userId, Long id) {
        ZiweiChartDO chart = chartMapper.selectById(id);
        if (chart == null || !Objects.equals(chart.getMemberUserId(), userId)) {
            throw exception(ZIWEI_CHART_NOT_FOUND);
        }
        chartMapper.deleteById(id);
    }

}
