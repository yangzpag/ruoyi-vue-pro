package cn.iocoder.yudao.module.ziwei.controller.app.chart;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ziwei.controller.app.chart.vo.AppZiweiChartCreateReqVO;
import cn.iocoder.yudao.module.ziwei.controller.app.chart.vo.AppZiweiChartPageReqVO;
import cn.iocoder.yudao.module.ziwei.controller.app.chart.vo.AppZiweiChartRespVO;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.chart.ZiweiChartDO;
import cn.iocoder.yudao.module.ziwei.service.chart.ZiweiChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.object.BeanUtils.toBean;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 命盘保存")
@RestController
@RequestMapping("/ziwei/chart")
@Validated
public class AppZiweiChartController {

    @Resource
    private ZiweiChartService chartService;

    @PostMapping("/create")
    @Operation(summary = "保存命盘（每用户上限 20 个）")
    public CommonResult<Long> createChart(@Valid @RequestBody AppZiweiChartCreateReqVO reqVO) {
        return success(chartService.createChart(getLoginUserId(), reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "获得我的命盘分页（不含排盘数据）")
    public CommonResult<PageResult<AppZiweiChartRespVO>> getChartPage(@Valid AppZiweiChartPageReqVO pageReq) {
        PageResult<ZiweiChartDO> pageResult = chartService.getChartPage(getLoginUserId(), pageReq);
        return success(toBean(pageResult, AppZiweiChartRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得命盘详情（含排盘数据，用于回放）")
    public CommonResult<AppZiweiChartRespVO> getChart(@RequestParam("id") Long id) {
        return success(toBean(chartService.getChart(getLoginUserId(), id), AppZiweiChartRespVO.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除命盘")
    public CommonResult<Boolean> deleteChart(@RequestParam("id") Long id) {
        chartService.deleteChart(getLoginUserId(), id);
        return success(true);
    }

}