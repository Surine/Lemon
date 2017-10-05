package com.surine.lemon.Fragment

/**
 * Created by surine on 2017/9/3.
 */

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.surine.lemon.Data.UrlData
import com.surine.lemon.InitApp.InitChart
import com.surine.lemon.InitApp.InitChart.addEntry
import com.surine.lemon.JavaBean.SimpleEvent
import com.surine.lemon.R
import com.surine.lemon.Utils.PatternUtil
import de.greenrobot.event.EventBus
import de.greenrobot.event.Subscribe
import de.greenrobot.event.ThreadMode


/**
 * Created by surine on 2017/5/8.
 */

class COFragment : Fragment() {
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
            Log.d("MESSGEX", "未过滤" + event.message)
            if (contain?.contains(UrlData.CAR)!!) {
                Log.d("MESSGEX", event.message)
                val message = Integer.parseInt(PatternUtil.getNumber(event.message!!)[0]).toFloat()
                text_recent!!.text = message.toString()
                addEntry(myChart!!,message)
                val pref = activity.getSharedPreferences("data", Activity.MODE_PRIVATE)
                val write = activity.getSharedPreferences("data", Activity.MODE_PRIVATE).edit()
                if (pref.getFloat("HIGH_CO",0f) < message){
                    write.putFloat("HIGH_CO",message).apply()
                    text_high!!.text = message.toString()
                }
                if (pref.getFloat("LOW_CO",10000f) > message){
                    write.putFloat("LOW_CO",message).apply()
                    text_low!!.text = message.toString()
                }
                if(message > 5f){
                    text_note!!.text = "当前室内CO浓度超标，请注意安全"
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
        text_high!!.text = pref.getFloat("HIGH_CO",0f).toString()
        text_low!!.text = pref.getFloat("LOW_CO",0f).toString()
        icon_value!!.setImageResource(R.drawable.co)
        myChart = v.findViewById(R.id.chart_tm) as LineChart
        //初始化数据
        //配置图表样式
        myChart = InitChart.setChart(myChart!!,3)

        //设置空数据
        myChart!!.data = LineData()
        //update
        myChart!!.invalidate()
        return v

    }

    companion object {
        private val ARG_ = "COFragment"
        fun getInstance(title: String): COFragment {
            val fra = COFragment()
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
