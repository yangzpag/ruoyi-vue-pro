package cn.iocoder.yudao.module.ziwei.dal.mysql.chart;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.chart.ZiweiChartDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 保存的命盘 Mapper
 */
@Mapper
public interface ZiweiChartMapper extends BaseMapperX<ZiweiChartDO> {

    /**
     * 分页查询用户的命盘列表（不取 chart_data 大字段）
     */
    default PageResult<ZiweiChartDO> selectPageByUserId(Long userId, cn.iocoder.yudao.framework.common.pojo.PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapperX<ZiweiChartDO>()
                .eq(ZiweiChartDO::getMemberUserId, userId)
                .select(ZiweiChartDO.class, field -> !"chartData".equals(field.getProperty()))
                .orderByDesc(ZiweiChartDO::getId));
    }

    default Long selectCountByUserId(Long userId) {
        return selectCount(new LambdaQueryWrapperX<ZiweiChartDO>()
                .eq(ZiweiChartDO::getMemberUserId, userId));
    }

}
