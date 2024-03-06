package com.ruoyi.apda.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.apda.domain.AvgYear;
import com.ruoyi.apda.service.IAvgYearService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 年平均价格Controller
 *
 * @author 陈娟娟
 * @date 2023-07-06
 */
@RestController
@RequestMapping("/avgYear/avgYear")
public class AvgYearController extends BaseController
{
    @Autowired
    private IAvgYearService avgYearService;

    /**
     * 查询年平均价格列表
     */
    @PreAuthorize("@ss.hasPermi('avgYear:avgYear:list')")
    @GetMapping("/list")
    public TableDataInfo list(AvgYear avgYear)
    {
        startPage();
        QueryWrapper<AvgYear> queryWrapper=new QueryWrapper<>();
        if(avgYear.getAydate()!=null&&!"".equals(avgYear.getAydate())){
            queryWrapper.eq("aydate",avgYear.getAydate());
        }
        if(avgYear.getAygoods()!=null&&!"".equals(avgYear.getAygoods())){
            queryWrapper.eq("aygoods",avgYear.getAygoods());
        }
        List<AvgYear> list = avgYearService.list(queryWrapper);
        return getDataTable(list);
    }

    /**
     * 获取年平均价格详细信息
     */
    @PreAuthorize("@ss.hasPermi('avgYear:avgYear:query')")
    @GetMapping(value = "/{ayid}")
    public AjaxResult getInfo(@PathVariable("ayid") Long ayid)
    {

        return success(avgYearService.getById(ayid));
    }

}
