package com.ruoyi.apda.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.apda.domain.FoodLabor;
import com.ruoyi.apda.service.IFoodLaborService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * foodLaborController
 *
 * @author 陈娟娟
 * @date 2023-07-11
 */
@RestController
@RequestMapping("/foodLabor/foodLabor")
public class FoodLaborController extends BaseController
{
    @Autowired
    private IFoodLaborService foodLaborService;

    /**
     * 查询foodLabor列表
     */
    @PreAuthorize("@ss.hasPermi('foodLabor:foodLabor:list')")
    @GetMapping("/list")
    public TableDataInfo list(FoodLabor foodLabor)
    {
        startPage();
        QueryWrapper<FoodLabor> queryWrapper=new QueryWrapper<>();

        if(foodLabor.getCmName()!=null&&!"".equals(foodLabor.getCmName())){
            queryWrapper.eq("cm_name",foodLabor.getCmName());
        }
        List<FoodLabor> list = foodLaborService.list(queryWrapper);
        return getDataTable(list);
    }



    /**
     * 获取foodLabor详细信息
     */
    @PreAuthorize("@ss.hasPermi('foodLabor:foodLabor:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(foodLaborService.getById(id));
    }


}
