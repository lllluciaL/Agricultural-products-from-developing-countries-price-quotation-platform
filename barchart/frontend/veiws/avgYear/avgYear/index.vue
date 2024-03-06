<template>
  <div class="app-container">
    <div class="chart-title">商品年平均价格柱状图</div>
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品名称" prop="aygoods">
        <el-select v-model="queryParams.aygoods" filterable placeholder="请选择商品名称" clearable @change="handleAyGoodsChange">
            <el-option
              v-for="(item,index) in goodsList"
              :key="index"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
      </el-form-item>
      <el-form-item label="日期" prop="aydate">
        <el-select v-model="queryParams.aydate" filterable placeholder="请选择日期" clearable @change="handleAyDateChange">
            <el-option
              v-for="(item,index) in dateList"
              :key="index"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div ref="chart" class="chart" style="width: 100%;height:400px;"></div>
  </div>
</template>

<script>
import { listAvgYear, getAvgYear } from "@/api/avgYear/avgYear";
import echarts from "echarts";

export default {
  name: "AvgYear",
  data() {
    return {
      dataByType: {}, // 添加该属性

      hoverIndex: -1, // 保存鼠标悬浮的柱子的索引
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      //商品
      goodsList: [],
      //日期
      dateList: [],
      // 年平均价格表格数据
      avgYearList: [],
      chartData: {},
      legendData: [],
      xAxisData: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10000,
        aygoods: null,
        aydate: null,
        ayGoodsOptions: [],
        ayDateOptions: [],

      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      value: ''
    };
  },
  created() {
    this.getList();
    //this.updateChart();
  },
  mounted() {
  //     this.updateChart();
   },
  methods: {
  // 处理商品下拉框选择变化事件
  handleAyGoodsChange() {
    // 根据选中的商品名称过滤对应的日期
    const filteredAyDate = this.avgYearList
      .filter(item => item.aygoods === this.queryParams.aygoods)
      .map(item => item.aydate);
    // 去除重复的日期
    const uniqueAyDate = [...new Set(filteredAyDate)].sort();

    this.dateList = uniqueAyDate; // 将去重后的日期赋值给下拉列表选项
  },
  // 处理日期下拉框选择变化事件
  handleAyDateChange() {
    // 根据选中的日期过滤对应的商品名称
    const filteredAyGoods = this.avgYearList
      .filter(item => item.aydate === this.queryParams.aydate)
      .map(item => item.aygoods);
    // 去除重复的商品名称
    const uniqueAyGoods = [...new Set(filteredAyGoods)];

    this.goodsList = uniqueAyGoods;
    this.goodsList.sort((a, b) => {
      return a.localeCompare(b, "en", { sensitivity: "base" });
    });
  },

    /** 查询年平均价格列表 */
  getList() {
    this.loading = true;
    listAvgYear(this.queryParams).then(response => {
      this.avgYearList = response.rows;
      this.total = response.total;
      this.loading = false;
      this.goodsList = [...new Set(response.rows.map(item => item.aygoods))];
      this.goodsList.sort((a, b) => {
        return a.localeCompare(b, "en", { sensitivity: "base" });
      });
      this.dateList = [...new Set(response.rows.map(item => item.aydate))].sort();
    this.renderChart();
  });
  },
  getChartData(){
    const chartData = {};
    const legendData = [];
    const xAxisData = [];
    const seriesData = [];

    this.avgYearList.forEach((item) => {
      //符合条件的所有数据
      if (!chartData[item.aytype]) {
        chartData[item.aytype] = {};
      }
      if (!chartData[item.aytype][item.aycountry]) {
        chartData[item.aytype][item.aycountry] = {};
      }
      if (!chartData[item.aytype][item.aycountry].aycountry) {
        chartData[item.aytype][item.aycountry].aycountry = item.aycountry;
      }
      if (!chartData[item.aytype][item.aycountry].aygoods) {
        chartData[item.aytype][item.aycountry].aygoods = item.aygoods;
      }
      if (!chartData[item.aytype][item.aycountry].ayprice) {
        chartData[item.aytype][item.aycountry].ayprice = item.ayprice;
      }
      if (!chartData[item.aytype][item.aycountry].ayunit) {
          chartData[item.aytype][item.aycountry].ayunit = item.ayunit;
      }
      if (!chartData[item.aytype][item.aycountry].aydate) {
          chartData[item.aytype][item.aycountry].aydate = item.aydate;
        }
      if (!chartData[item.aytype][item.aycountry].aytype) {
          chartData[item.aytype][item.aycountry].aytype = item.aytype;
        }

      chartData[item.aytype][item.aycountry].ayprice = item.ayprice;
      //图例
      if (!legendData.includes(item.aytype)) {
        legendData.push(item.aytype);
      }
      //国家
      if (!xAxisData.includes(item.aycountry)) {
        xAxisData.push(item.aycountry);
      }

    });

    //seriesData
    for (const aytype of legendData) {
          const seriesItem = {
            name: aytype,
            type: "bar",
            data: [],

          };
          for (const aycountry of xAxisData) {
            seriesItem.data.push(chartData[aytype][aycountry]?.ayprice ?? null);

          };
          seriesData.push(seriesItem);
        };
    return{
       chartData,
       legendData,
       xAxisData,
       seriesData,
    }

  },

    renderChart() {
      const allData=this.getChartData();
      this.chartData = allData.chartData;
      this.legendData = allData.legendData;
      this.xAxisData = allData.xAxisData;
      const chart = echarts.init(this.$refs.chart);
      const option = {
        tooltip: {
          trigger: "item",
          formatter: (params) => {
            const country = params.name;
            const price = params.value;
            const type = params.seriesName;
            const unit = this.chartData[params.seriesName][params.name]?.ayunit;

            return `国家：${country}<br>价格/单位：${price}/${unit}<br>价格类型：${type}`;
          }


        },
        legend: {
          data: allData.legendData,
        },
        xAxis: {
          type: "category",
          data: allData.xAxisData,
          axisLabel: {
            interval: 0,
            rotate: 45,
          },
          name: '国家', // 添加横坐标的图例
        },
        yAxis: {
          type: "value",
          name: '价格', // 添加纵坐标的图例

        },

        series: allData.seriesData,

      };
      console.log(allData.seriesData);
      chart.setOption(option,true);
  },

    // 表单重置
    reset() {
      this.form = {
        ayid: null,
        aycountry: null,
        aygoods: null,
        ayprice: null,
        ayunit: null,
        aydate: null,
        aytype: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.ayid)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },

}
};
</script>
<style>
  .app-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
  }
  .chart-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 20px; /* 调整标题和上边框的间距 */
  }
  
  .chart-title {
    margin-top: 20px;
    text-align: center;
    font-size: 44px;
    font-weight: bold;
    margin-bottom: 20px; /* 调整标题和表单之间的间距 */
  }
</style>

