package cn.iocoder.yudao.module.ziwei.dal.mysql.chart;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.chart.ZiweiChartDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 保存的命盘 Mapper
 */
@Mapper
public interface ZiweiChartMapper extends BaseMapperX<ZiweiChartDO> {

    /**
     * 会话级咨询锁（PostgreSQL）：createChart 用它对同一用户的创建流程串行化，
     * 保证"查数量 + 插入"的原子性（并发下第二个请求会等待第一个提交/回滚）。
     * 仅支持 PostgreSQL；MySQL 部署需替换为 GET_LOCK 等价实现。
     */
    @Select("SELECT pg_advisory_xact_lock(#{key})")
    boolean advisoryXactLock(@Param("key") Long key);

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
