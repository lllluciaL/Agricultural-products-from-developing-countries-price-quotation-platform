<template>
  <div class="app-container">
    <div class="chart-container">
      <div class="chart-title">劳动力价格箱线图</div>
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
        <el-form-item label="劳动力" prop="cmName">
          <el-select v-model="queryParams.cmName" filterable placeholder="请选择劳动力名称">
            <el-option
              v-for="(item,index) in cmNameList"
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
    </div>
    <div ref="chart" class="chart" style="width: 100%; height: 500px;"></div>
  </div>
</template>


<script>
import { listFoodLabor, getFoodLabor } from "@/api/foodLabor/foodLabor";
import echarts from "echarts";

export default {
  name: "FoodLabor",
  data() {
    return {
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
      // foodLabor表格数据
      foodLaborList: [],
      cmNameList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 6000,
        cmName: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询foodLabor列表 */
    getList() {
      this.loading = true;
      listFoodLabor(this.queryParams).then(response => {
        this.foodLaborList = response.rows;
        this.total = response.total;
        this.loading = false;
        this.cmNameList = [...new Set(response.rows.map(item => item.cmName))];
        this.updateChart();
      });

    },

    /** 更新图表 */
        updateChart() {
          // 创建一个空数组用于存储国家数据
          const xAxisData = [];
          // 创建一个空数组用于存储价格数据
          const dataByCountry = {};
          const outliersByCountry = {}; // 创建一个空对象用于存储按国家分类的异常值数据
         // 根据foodLaborList的数据，将数据按国家分类存储到dataByCountry对象中
          this.foodLaborList.forEach(item => {
            const country = item.adm0Name;
            if (!dataByCountry[country]) {
              dataByCountry[country] = [];
            }
            dataByCountry[country].push(item.mpPrice);
          });
          // 创建一个空数组用于存储价格数据
          const seriesData = [];
          const outliers = [];
           // 使用Set数据结构去除重复国家名称，并存储到xAxisData数组中
          const uniqueCountries = new Set();
          this.foodLaborList.forEach(item => {
            uniqueCountries.add(item.adm0Name);
          });
          xAxisData.push(...uniqueCountries);
           // 根据dataByCountry对象计算箱线图的数据
            Object.keys(dataByCountry).forEach(country => {
              const prices = dataByCountry[country];
              // 计算最小值、最大值、Q1、中位数、Q3
              prices.sort((a, b) => a - b);
              const min = prices[0];
              const max = prices[prices.length - 1];
              const q1 = this.calculateQuantile(prices, 0.25);
              const median = this.calculateQuantile(prices, 0.5);
              const q3 = this.calculateQuantile(prices, 0.75);
              const iqr = q3-q1;
              for(let i=6;i<prices.length;i++){
                if (prices[i] >= q1-iqr*1.5 || prices[i] <= q3+iqr*1.5) {
                  outliers.push(prices[i]);
                }
              }
              // 将计算出的数据添加到seriesData中
              seriesData.push([min, q1, median, q3, max]);
            });
          outliers.forEach(price => {
            const country = this.foodLaborList.find(item => item.mpPrice === price).adm0Name;
            if (!outliersByCountry[country]) {
              outliersByCountry[country] = [];
            }
            outliersByCountry[country].push(price);
          });
          // 获取异常值数据
          // 创建echarts实例
          const chart = echarts.init(this.$refs.chart);
          // 配置选项
          const option = {
            tooltip: {
              trigger: "axis",
              formatter: function(params) {
                const country = params[0].name;
                const min = params[0].data[1];
                const max = params[0].data[5];
                const q1 = params[0].data[2];
                const median = params[0].data[3];
                const q3 = params[0].data[4];
                return `国家: ${country}<br>最小值: ${min}<br>最大值: ${max}<br>Q1: ${q1}<br>中位数: ${median}<br>Q3: ${q3}`;
              },
              axisPointer: {
                type: 'shadow'
              }
            },
            grid: {
              left: "10%",
              right: "10%",
              bottom: "15%"
            },
            xAxis: {
              type: "category",
              data: xAxisData,
              boundaryGap: true,
              nameGap: 30,
              axisLabel: {
                interval: 0,
                rotate: 45,
              },
              splitArea: {
                show: false
              },
              splitLine: {
                show: false
              }
            },
            yAxis: {
              type: "value",
              splitArea: {
                show: true
              },
            },
            series:[
              {
                type: "boxplot",
                data: seriesData,
               }
            ]
          };
          // 渲染图表
          chart.setOption(option);
    },
    /** 计算分位数 */
    calculateQuantile(data, percentile) {
      const index = (data.length - 1) * percentile;
      const lowerIndex = Math.floor(index);
      const upperIndex = Math.ceil(index);

      if (lowerIndex === upperIndex) {
        return data[lowerIndex];
      }

      const lowerValue = data[lowerIndex];
      const upperValue = data[upperIndex];
      return lowerValue + (upperValue - lowerValue) * (index - lowerIndex);
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        adm0Name: null,
        cmName: null,
        mpPrice: null,
        yearMonth: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
}
};
</script>

<style>
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



