package cn.iocoder.yudao.module.ziwei.dal.mysql.points;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.points.ZiweiUserPointsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

/**
 * 用户次数权益 Mapper
 */
@Mapper
public interface ZiweiUserPointsMapper extends BaseMapperX<ZiweiUserPointsDO> {

    default ZiweiUserPointsDO selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<ZiweiUserPointsDO>()
                .eq(ZiweiUserPointsDO::getMemberUserId, userId));
    }

    /**
     * 原子懒创建：ON CONFLICT DO NOTHING，并发下不抛唯一键异常
     * （PG 中语句失败会中止整个事务，因此不能依赖 DuplicateKeyException 兜底）
     */
    @Insert("INSERT INTO ziwei_user_points (member_user_id, balance, total_granted, total_consumed) "
            + "VALUES (#{userId}, 0, 0, 0) ON CONFLICT (member_user_id, tenant_id) DO NOTHING")
    int insertIgnoreByUserId(@Param("userId") Long userId);

    /**
     * 原子扣减：余额不足时返回 0（不更新）
     */
    @Update("UPDATE ziwei_user_points SET balance = balance - #{points}, "
            + "total_consumed = total_consumed + #{points}, update_time = CURRENT_TIMESTAMP "
            + "WHERE member_user_id = #{userId} AND balance >= #{points} AND deleted = 0")
    int deductBalance(@Param("userId") Long userId, @Param("points") Integer points);

    /**
     * 原子增加
     */
    @Update("UPDATE ziwei_user_points SET balance = balance + #{points}, "
            + "total_granted = total_granted + #{points}, update_time = CURRENT_TIMESTAMP "
            + "WHERE member_user_id = #{userId} AND deleted = 0")
    int addBalance(@Param("userId") Long userId, @Param("points") Integer points);

}