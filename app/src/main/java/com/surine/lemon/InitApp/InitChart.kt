package com.surine.lemon.InitApp

import android.graphics.Color
import android.graphics.Matrix
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.surine.lemon.Data.UrlData

/**
 * Created by surine on 2017/10/5.
 * 初始化图表配置
 */
object InitChart{

    //设置表
    fun setChart(chart:LineChart,tag:Int) : LineChart{
        // 不显示数据描述
        chart.description.isEnabled = false
        chart.setPadding(0, 0, 0, 0)
        // 没有数据的时候，显示“暂无数据”
        chart.setNoDataText("设备离线")
        // 不显示表格颜色
        chart.setDrawGridBackground(false)
        // 图表缩放
        chart.setScaleEnabled(false)
        // 不显示y轴右边的值
        chart.axisRight.setDrawLabels(false)   //右侧数据
        chart.axisLeft.setDrawLabels(false)   //左侧数据
        chart.axisLeft.setDrawGridLines(false)   //网格线
        chart.axisRight.setDrawGridLines(false)  //网格线
        chart.axisRight.isEnabled = false  //右侧竖线
        //隐藏X轴竖网格线
        chart.xAxis.setDrawGridLines(false)
        // 不显示图例
        val legend = chart.legend
        legend.isEnabled = false

        //获取x的实例
        val xAxis = chart.xAxis
        // 不显示x轴
        xAxis.setDrawAxisLine(false)
        // 设置x轴数据的位置
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.WHITE
        xAxis.textSize = 0f
        xAxis.gridColor = Color.parseColor("#40000000")
        val yAxis = chart.axisLeft
        yAxis.setDrawAxisLine(false)

        val des2 = Description()
        when{
            tag == 1 -> des2.text = "温度数据表"
            tag == 2 -> des2.text = "湿度数据表"
            tag == 3 -> des2.text = "一氧化碳数据表"
            tag == 4 -> des2.text = "甲烷数据表"
            tag == 5 -> des2.text = "乙烷数据表"
        }
        chart.description = des2

        val matrix = Matrix()

        //在图表动画显示之前进行缩放
        chart.viewPortHandler.refresh(matrix, chart, false)
        //x轴执行动画
        chart.animateX(2000)
        //  chart.invalidate();
        return chart
    }


    //表属性
    fun createSet(tag:Int): LineDataSet {
        val set = LineDataSet(null, "DataSet 1")
        set.lineWidth = 8.5f
        set.label = "温度"
        when{
            tag == 1 ->  set.label = "温度"
            tag == 2 ->  set.label = "湿度"
            tag == 3 ->  set.label = "CO"
            tag == 4 ->  set.label = "甲烷"
            tag == 5 ->  set.label = "乙烷"
        }
        set.circleRadius = 10.5f
        set.color = Color.rgb(0, 0, 0)
        set.setCircleColor(Color.rgb(240, 99, 99))
        set.highLightColor = Color.rgb(0, 0, 0)
        set.valueTextSize = 8f
        set.cubicIntensity = 0f
        set.setDrawFilled(true)  //设置包括的范围区域填充颜色
        set.fillColor = Color.rgb(30, 200, 200)
        set.lineWidth = 4f    //设置线的宽度
        set.circleSize = 5f   //设置小圆的大小
        // 设置平滑曲线
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        // 显示坐标点的小圆点
        set.setDrawCircles(true)
        // 显示坐标点的数据
        set.setDrawValues(true)
        // 不显示定位线
        set.isHighlightEnabled = false
        return set
    }

    //添加数据
    fun addEntry(myChart:LineChart,message: Float) {
        val data = myChart.data
        var set: ILineDataSet? = data.getDataSetByIndex(0)
        //如果折线不存在，创建
        if (set == null) {
            set = createSet(1)
            data.addDataSet(set)
        }
        // 选择一个随机的数据集
        val randomDataSetIndex = (Math.random() * data.dataSetCount).toInt()
        var yValue = 1f
        try {
            //设置y轴数据
            yValue = message
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        //添加entry
        data.addEntry(Entry(data.getDataSetByIndex(randomDataSetIndex).entryCount.toFloat(), yValue), randomDataSetIndex)
        //数据更新
        data.notifyDataChanged()
        // 折线更新
        myChart!!.notifyDataSetChanged()
        //设置应该是最大可见的区域（x轴上的范围）的大小（不允许进一步缩小）。
        myChart!!.setVisibleXRangeMaximum(UrlData.HEART_NUMBER_SHOW)
        //移动图表
        myChart!!.moveViewTo((data.entryCount - 1).toFloat(), 10f, YAxis.AxisDependency.LEFT)
    }

}