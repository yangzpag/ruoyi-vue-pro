package cn.iocoder.yudao.module.ziwei.service.chart;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ziwei.controller.app.chart.vo.AppZiweiChartCreateReqVO;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.chart.ZiweiChartDO;

/**
 * 命盘 Service 接口
 */
public interface ZiweiChartService {

    /**
     * 保存命盘（每用户上限 20 个）
     *
     * @param userId 会员用户编号
     * @param reqVO 保存请求
     * @return 命盘编号
     */
    Long createChart(Long userId, AppZiweiChartCreateReqVO reqVO);

    /**
     * 获得用户的命盘分页（不含 chart_data 大字段）
     */
    PageResult<ZiweiChartDO> getChartPage(Long userId, cn.iocoder.yudao.framework.common.pojo.PageParam pageParam);

    /**
     * 获得用户的单个命盘（含 chart_data），归属校验
     */
    ZiweiChartDO getChart(Long userId, Long id);

    /**
     * 删除用户的命盘（归属校验）
     */
    void deleteChart(Long userId, Long id);

}