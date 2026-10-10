package cn.iocoder.yudao.module.ziwei.dal.mysql.chart;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.chart.ZiweiChartQuotaDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 命盘配额 Mapper
 *
 * 配额方案：每用户一行 used 计数，acquire/release 用原子 UPDATE，
 * 跨方言（PG/MySQL）语义一致，替代 count(*) + advisory lock 组合。
 */
@Mapper
public interface ZiweiChartQuotaMapper extends BaseMapperX<ZiweiChartQuotaDO> {

    /** 懒创建（PostgreSQL） */
    @Insert("INSERT INTO ziwei_chart_quota (member_user_id, used) "
            + "VALUES (#{userId}, 0) ON CONFLICT (member_user_id, tenant_id) DO NOTHING")
    int insertIgnoreByUserId(@Param("userId") Long userId);

    /** 懒创建（MySQL） */
    @Insert("INSERT IGNORE INTO ziwei_chart_quota (member_user_id, used) VALUES (#{userId}, 0)")
    int insertIgnoreByUserIdMysql(@Param("userId") Long userId);

    /**
     * 原子占用一个配额：used 已达上限时返回 0（不更新）
     */
    @Update("UPDATE ziwei_chart_quota SET used = used + 1, update_time = CURRENT_TIMESTAMP "
            + "WHERE member_user_id = #{userId} AND used < #{limit} AND deleted = 0")
    int acquireQuota(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 释放一个配额（删除命盘时）：下限 0
     */
    @Update("UPDATE ziwei_chart_quota SET used = GREATEST(used - 1, 0), update_time = CURRENT_TIMESTAMP "
            + "WHERE member_user_id = #{userId} AND deleted = 0")
    void releaseQuota(@Param("userId") Long userId);

    default ZiweiChartQuotaDO selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<ZiweiChartQuotaDO>()
                .eq(ZiweiChartQuotaDO::getMemberUserId, userId));
    }

}
