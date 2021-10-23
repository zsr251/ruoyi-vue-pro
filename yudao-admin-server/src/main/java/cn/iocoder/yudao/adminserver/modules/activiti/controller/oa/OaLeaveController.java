package cn.iocoder.yudao.adminserver.modules.activiti.controller.oa;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.annotations.*;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.adminserver.modules.activiti.controller.oa.vo.*;
import cn.iocoder.yudao.adminserver.modules.activiti.dal.dataobject.oa.OaLeaveDO;
import cn.iocoder.yudao.adminserver.modules.activiti.convert.oa.OaLeaveConvert;
import cn.iocoder.yudao.adminserver.modules.activiti.service.oa.OaLeaveService;

@Api(tags = "请假申请")
@RestController
@RequestMapping("/oa/leave")
@Validated
public class OaLeaveController {

    @Resource
    private OaLeaveService leaveService;

    @PostMapping("/create")
    @ApiOperation("创建请假申请")
    @PreAuthorize("@ss.hasPermission('oa:leave:create')")
    public CommonResult<Long> createLeave(@Valid @RequestBody OaLeaveCreateReqVO createReqVO) {
        return success(leaveService.createLeave(createReqVO));
    }

    @PutMapping("/update")
    @ApiOperation("更新请假申请")
    @PreAuthorize("@ss.hasPermission('oa:leave:update')")
    public CommonResult<Boolean> updateLeave(@Valid @RequestBody OaLeaveUpdateReqVO updateReqVO) {
        leaveService.updateLeave(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除请假申请")
    @ApiImplicitParam(name = "id", value = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:leave:delete')")
    public CommonResult<Boolean> deleteLeave(@RequestParam("id") Long id) {
        leaveService.deleteLeave(id);
        return success(true);
    }

    @GetMapping("/get")
    @ApiOperation("获得请假申请")
    @ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Long.class)
    @PreAuthorize("@ss.hasPermission('oa:leave:query')")
    public CommonResult<OaLeaveRespVO> getLeave(@RequestParam("id") Long id) {
        OaLeaveDO leave = leaveService.getLeave(id);
        return success(OaLeaveConvert.INSTANCE.convert(leave));
    }

    @GetMapping("/list")
    @ApiOperation("获得请假申请列表")
    @ApiImplicitParam(name = "ids", value = "编号列表", required = true, example = "1024,2048", dataTypeClass = List.class)
    @PreAuthorize("@ss.hasPermission('oa:leave:query')")
    public CommonResult<List<OaLeaveRespVO>> getLeaveList(@RequestParam("ids") Collection<Long> ids) {
        List<OaLeaveDO> list = leaveService.getLeaveList(ids);
        return success(OaLeaveConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @ApiOperation("获得请假申请分页")
    @PreAuthorize("@ss.hasPermission('oa:leave:query')")
    public CommonResult<PageResult<OaLeaveRespVO>> getLeavePage(@Valid OaLeavePageReqVO pageVO) {
        //值查询自己申请请假
        pageVO.setUserId(SecurityFrameworkUtils.getLoginUser().getUsername());
        PageResult<OaLeaveDO> pageResult = leaveService.getLeavePage(pageVO);
        return success(OaLeaveConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @ApiOperation("导出请假申请 Excel")
    @PreAuthorize("@ss.hasPermission('oa:leave:export')")
    @OperateLog(type = EXPORT)
    public void exportLeaveExcel(@Valid OaLeaveExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<OaLeaveDO> list = leaveService.getLeaveList(exportReqVO);
        // 导出 Excel
        List<OaLeaveExcelVO> datas = OaLeaveConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "请假申请.xls", "数据", OaLeaveExcelVO.class, datas);
    }

}