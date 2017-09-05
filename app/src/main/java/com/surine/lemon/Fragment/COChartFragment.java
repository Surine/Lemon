package com.surine.lemon.Fragment;

/**
 * Created by surine on 2017/9/3.
 */

import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.surine.lemon.Data.UrlData;
import com.surine.lemon.JavaBean.SimpleEvent;
import com.surine.lemon.R;
import com.surine.lemon.Utils.PatternUtil;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;


/**
 * Created by surine on 2017/5/8.
 */

public class COChartFragment extends Fragment {
    LineChart chart;
    int tag = 1;
    private static final String ARG_ ="COChartFragment" ;
    View v;
    public static COChartFragment getInstance(String title) {
        COChartFragment fra = new COChartFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_, title);
        fra.setArguments(bundle);
        return fra;
    }


    @Subscribe(threadMode = ThreadMode.MainThread, sticky = true)
    public void onMessageEvent(SimpleEvent event) {
        String contain = event.getMessage();
        if(event.getId()==1){
            Log.d("MESSGEX","未过滤"+event.getMessage());
            if(contain.contains(UrlData.CAR)){
                Log.d("MESSGEX",event.getMessage());
                addEntry(event.getMessage());
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_tmp, container, false);
        //初始化数据
        initData();
        return v;

    }

    /** * x轴数据处理 * * @param valueType 数据类型 * @return x轴数据 */
    private static String[] xValuesProcess() {
        String[] time = {"10:10", "10:20", "10:30", "10:40", "10:50", "11:00", "11:10"};
        return time;
    }


    //添加数据
    private void addEntry(String message) {
        LineData data = chart.getData();

        ILineDataSet set = data.getDataSetByIndex(0);
        //如果折线不存在，创建
        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }

        // 选择一个随机的数据集
        int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());
        float yValue = 0;

        try {
            message = PatternUtil.getNumber(message).get(0);
            Log.d("MESSGEX","添加数据"+message);
            //设置y轴数据
            yValue = (float)Integer.parseInt(message);
            Log.d("MESSGEX",yValue+"");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.d("MESSGEX",e.getMessage());
        }

        //添加entry
        data.addEntry(new Entry(data.getDataSetByIndex(randomDataSetIndex).getEntryCount(), yValue), randomDataSetIndex);
        //数据更新
        data.notifyDataChanged();

        // 折线更新
        chart.notifyDataSetChanged();

        //设置应该是最大可见的区域（x轴上的范围）的大小（不允许进一步缩小）。
        chart.setVisibleXRangeMaximum(UrlData.HEART_NUMBER_SHOW);

        //移动图表
        chart.moveViewTo(data.getEntryCount() - 1, 10f, YAxis.AxisDependency.LEFT);
    }




    private void initData() {
        //初始化图表
        initChart_my();
    }

    private void initChart_my() {
        //findview
        chart = (LineChart) v.findViewById(R.id.chart_tmp);


        initChart(chart,tag = 1);   //配置温度图表样式


        //设置空数据
        chart.setData(new LineData());
        // chart2.setData(new LineData());

        //update
        chart.invalidate();

    }


    /** * 初始化图表 * * @param chart 原始图表 * @return 初始化后的图表 */

    public static LineChart initChart(LineChart chart, int i) {
        // 不显示数据描述
        chart.getDescription().setEnabled(false);
        chart.setPadding(0,0,0,0);

        // 没有数据的时候，显示“暂无数据”
        chart.setNoDataText("设备离线");
        // 不显示表格颜色
        chart.setDrawGridBackground(false);
        // 图表缩放
        chart.setScaleEnabled(false);
        // 不显示y轴右边的值
        chart.getAxisRight().setDrawLabels(false);   //右侧数据
        chart.getAxisLeft().setDrawLabels(false);   //左侧数据
        chart.getAxisLeft().setDrawGridLines(false);   //网格线
        chart.getAxisRight().setDrawGridLines(false);  //网格线
        chart.getAxisRight().setEnabled(false);  //右侧竖线

        //隐藏X轴竖网格线
        chart.getXAxis().setDrawGridLines(false);
        // 不显示图例
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        //   chart.setExtraLeftOffset(0);

        //获取x的实例
        XAxis xAxis = chart.getXAxis();
        // 不显示x轴
        xAxis.setDrawAxisLine(false);
        // 设置x轴数据的位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(0);
        xAxis.setGridColor(Color.parseColor("#40000000"));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        // yAxis.setTextColor(Color.WHITE);

        Description des2 = new Description();
        des2.setText("");
        chart.setDescription(des2);


        Matrix matrix = new Matrix();

        //在图表动画显示之前进行缩放
        chart.getViewPortHandler().refresh(matrix, chart, false);
        //x轴执行动画
        chart.animateX(2000);
        //  chart.invalidate();
        return chart;
    }



    //如果没有创建这个表，就创建
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "DataSet 1");
        set.setLineWidth(8.5f);
        set.setLabel("温度");
        set.setCircleRadius(10.5f);
        set.setColor(Color.rgb(0, 0, 0));
        set.setCircleColor(Color.rgb(240, 99, 99));
        set.setHighLightColor(Color.rgb(0, 0, 0));
        set.setValueTextSize(8f);
        set.setCubicIntensity(0f);
        set.setDrawFilled(true);  //设置包括的范围区域填充颜色
        set.setFillColor(Color.rgb(30, 200, 200));
        set.setLineWidth(4f);    //设置线的宽度
        set.setCircleSize(5f);   //设置小圆的大小
        // 设置平滑曲线
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 显示坐标点的小圆点
        set.setDrawCircles(true);
        // 显示坐标点的数据
        set.setDrawValues(true);
        // 不显示定位线
        set.setHighlightEnabled(false);
        return set;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
