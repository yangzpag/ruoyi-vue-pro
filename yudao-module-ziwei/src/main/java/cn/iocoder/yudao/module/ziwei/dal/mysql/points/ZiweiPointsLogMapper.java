package cn.iocoder.yudao.module.ziwei.dal.mysql.points;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.points.ZiweiPointsLogDO;
import cn.iocoder.yudao.module.ziwei.controller.app.points.vo.AppZiweiPointsLogPageReqVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 次数变动流水 Mapper
 */
@Mapper
public interface ZiweiPointsLogMapper extends BaseMapperX<ZiweiPointsLogDO> {

    default PageResult<ZiweiPointsLogDO> selectPageByUserId(Long userId, AppZiweiPointsLogPageReqVO pageReq) {
        return selectPage(pageReq, new LambdaQueryWrapperX<ZiweiPointsLogDO>()
                .eq(ZiweiPointsLogDO::getMemberUserId, userId)
                .orderByDesc(ZiweiPointsLogDO::getId));
    }

}