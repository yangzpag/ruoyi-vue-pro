package cn.iocoder.yudao.module.ziwei.service.chart;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ziwei.controller.app.chart.vo.AppZiweiChartCreateReqVO;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.chart.ZiweiChartDO;
import cn.iocoder.yudao.module.ziwei.dal.mysql.chart.ZiweiChartMapper;
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
    private javax.sql.DataSource dataSource;

    /**
     * 是否 PostgreSQL（决定 createChart 是否可用 advisory lock 串行化）
     */
    private boolean postgreSQL;

    @javax.annotation.PostConstruct
    void detectDialect() {
        try (java.sql.Connection conn = dataSource.getConnection()) {
            String product = conn.getMetaData().getDatabaseProductName();
            postgreSQL = product != null && product.contains("PostgreSQL");
        } catch (Exception e) {
            log.warn("[ziwei] 数据库方言检测失败，默认按 PostgreSQL 处理", e);
            postgreSQL = true;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createChart(Long userId, AppZiweiChartCreateReqVO reqVO) {
        // 1. 校验 JSON 严格可解析（isTypeJSON 只看首尾括号，{broken} 也能通过）
        try {
            JSONUtil.parseObj(reqVO.getChartData());
        } catch (Exception e) {
            throw exception(ZIWEI_CHART_DATA_INVALID);
        }
        // 2. 数量上限：PG 用 advisory lock 对同一用户串行化，保证"查数量 + 插入"原子性
        //    （否则 19 个时两个并发请求都能通过检查，突破 20 上限）；MySQL 部署需替换为 GET_LOCK
        if (postgreSQL) {
            chartMapper.advisoryXactLock(userId);
        }
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
