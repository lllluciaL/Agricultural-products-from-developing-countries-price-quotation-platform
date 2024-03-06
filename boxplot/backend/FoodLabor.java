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
public class FoodLabor implements Serializable {
    private static final long serialVersionUID = 1L;

    /** id */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 国家名称 */
    @Excel(name = "国家名称")
    private String adm0Name;

    /** 劳动力 */
    @Excel(name = "劳动力")
    private String cmName;

    /** 价格 */
    @Excel(name = "价格")
    private Double mpPrice;

    /** 日期 */
    @Excel(name = "日期")
    private String flyear;

}
