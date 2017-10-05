package com.surine.lemon.Fragment

/**
 * Created by surine on 2017/9/3.
 */

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.surine.lemon.Data.UrlData
import com.surine.lemon.InitApp.InitChart.addEntry
import com.surine.lemon.InitApp.InitChart.setChart
import com.surine.lemon.JavaBean.SimpleEvent
import com.surine.lemon.R
import com.surine.lemon.Utils.PatternUtil
import de.greenrobot.event.EventBus
import de.greenrobot.event.Subscribe
import de.greenrobot.event.ThreadMode


/**
 * Created by surine on 2017/5/8.
 */

class C2H6Fragment : Fragment() {
    private var myChart: LineChart? = null
    private var icon_value: ImageView? = null
    private var text_recent: TextView? = null
    private var text_high: TextView? = null
    private var text_low: TextView? = null
    private var text_note: TextView? = null

    //事件处理
    @Subscribe(threadMode = ThreadMode.MainThread, sticky = true)
    fun onMessageEvent(event: SimpleEvent) {
        val contain = event.message
        if (event.id == 1) {
            if (contain?.contains(UrlData.MET)!!) {
                //分离数据
                val message = Integer.parseInt(PatternUtil.getNumber(event.message!!)[0]).toFloat()
                //设置当前值
                text_recent!!.text = message.toString()
                //添加坐标点
                addEntry(myChart!!,message)
                val pref = activity.getSharedPreferences("data", Activity.MODE_PRIVATE)
                val write = activity.getSharedPreferences("data", Activity.MODE_PRIVATE).edit()
                //显示最大值最小值
                if (pref.getFloat("HIGH_C2H6",0f) < message){
                    write.putFloat("HIGH_C2H6",message).apply()
                    text_high!!.text = message.toString()
                }
                if (pref.getFloat("LOW_C2H6",10000f) > message){
                    write.putFloat("LOW_C2H6",message).apply()
                    text_low!!.text = message.toString()
                }
                //个性化提示
                if(message > 5f){
                    text_note!!.text = "当前室内乙烷浓度超标，请注意安全"
                }else{
                    text_note!!.text = "柠檬保护您的居家安全"
                }
            }
        }
    }

    //视图
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
    }

    //初始化
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = inflater!!.inflate(R.layout.fragment_tm, container, false)
        icon_value = v.findViewById(R.id.icon_value) as ImageView
        text_note = v.findViewById(R.id.text_note) as TextView
        text_recent = v.findViewById(R.id.text_recent) as TextView
        text_high = v.findViewById(R.id.text_high) as TextView
        text_low = v.findViewById(R.id.text_low) as TextView
        val pref = activity.getSharedPreferences("data", Activity.MODE_PRIVATE)
        text_high!!.text = pref.getFloat("HIGH_C2H6",0f).toString()
        text_low!!.text = pref.getFloat("LOW_C2H6",0f).toString()
        myChart = v.findViewById(R.id.chart_tm) as LineChart
        icon_value!!.setImageResource(R.drawable.c2h6)
        //初始化数据
        //配置图表样式
        myChart = setChart(myChart!!,5)
        //设置空数据
        myChart!!.data = LineData()
        //update
        myChart!!.invalidate()
        return v

    }
    companion object {
        private val ARG_ = "C2H6Fragment"
        fun getInstance(title: String): C2H6Fragment {
            val fra = C2H6Fragment()
            val bundle = Bundle()
            bundle.putString(ARG_, title)
            fra.arguments = bundle
            return fra
        }

    }

    //EventBus事件管理
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}
