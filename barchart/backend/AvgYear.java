package com.ruoyi.apda.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvgYear implements Serializable{
    private static final long serialVersionUID = 1L;

    /** id */
    @TableId(type = IdType.AUTO)
    private Long ayid;

    /** 国家名称 */
    @Excel(name = "国家名称")
    private String aycountry;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String aygoods;

    /** 年平均价格 */
    @Excel(name = "年平均价格")
    private Double ayprice;

    /** 单位 */
    @Excel(name = "单位")
    private String ayunit;

    /** 日期 */
    @Excel(name = "日期")
    private Long aydate;

    /** 价格种类 */
    @Excel(name = "价格种类")
    private String aytype;


}
