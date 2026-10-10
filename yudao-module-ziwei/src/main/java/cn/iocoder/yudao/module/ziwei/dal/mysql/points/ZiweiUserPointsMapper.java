package cn.iocoder.yudao.module.ziwei.dal.mysql.points;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.points.ZiweiUserPointsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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