package com.surine.lemon

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.*
import com.bumptech.glide.Glide
import com.surine.lemon.Adapter.SimpleFragmentPagerAdapter
import com.surine.lemon.Data.UrlData
import com.surine.lemon.Fragment.*
import com.surine.lemon.InitApp.BaseActivity
import com.surine.lemon.JavaBean.SimpleEvent
import com.surine.lemon.MQTT.Client
import com.surine.lemon.Service.WarnService
import com.surine.lemon.Utils.PatternUtil
import de.greenrobot.event.EventBus
import de.greenrobot.event.Subscribe
import de.greenrobot.event.ThreadMode
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_data.*
import kotlinx.android.synthetic.main.content_sheet.*
import org.jetbrains.anko.toast
import java.util.*



class MainActivity : BaseActivity() {
    //碎片管理
    private val fragments = ArrayList<Fragment>()
    //标题管理
    private val titles = ArrayList<String>()
    //MQTT
    private val client = Client()
    //状态
    private var status = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //启动APP服务
        startService(Intent(this, WarnService::class.java))
        //初始化视图
        initView()
        Thread(Runnable {
            //启动mqtt客户
            client.start()
        }).start()
    }


    //初始化视图
    private fun initView() {
        //bar setting
        setSupportActionBar(toolbar)
        toolbar.subtitle = "设备离线"
        toolbar.setSubtitleTextAppearance(this, R.style.x)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setHomeAsUpIndicator(R.drawable.ic_action_name)

        //每日一图加载器
        Glide.with(this).load(UrlData.bing_picture).into(imageview)



        //窗户控制
        windows_ctrl.setOnClickListener {
            if (status){
                client.sendCmd(UrlData.close)
                status = false
                toast("宝宝打开了窗户")
                windows_ctrl.text = "CLOSE"
            }else{
                client.sendCmd(UrlData.open)
                status = true
                toast("宝宝关闭了窗户")
                windows_ctrl.text = "OPEN"
            }
        }



        //BottomSheetBehavior
        val behavior = BottomSheetBehavior.from(bottom_sheet)
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == STATE_EXPANDED) {
                    val hidden = TranslateAnimation(Animation.RELATIVE_TO_SELF,
                            0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            -1.0f)
                    hidden.setDuration(500)
                    pull.startAnimation(hidden)
                    pull.visibility = View.GONE
                } else {
                    pull.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
        initViewPager()
    }

    //初始化viewpager
    private fun initViewPager() {
        fragments.add(TmpFragment.getInstance("1"))
        fragments.add(HumFragment.getInstance("2"))
        fragments.add(COFragment.getInstance("3"))
        fragments.add(Ch4Fragment.getInstance("4"))
        fragments.add(C2H6Fragment.getInstance("5"))
        titles.add("温度")
        titles.add("湿度")
        titles.add("CO")
        titles.add("甲烷")
        titles.add("乙烷")
        var pagerAdapter = SimpleFragmentPagerAdapter(supportFragmentManager, fragments, titles)
        viewpager.adapter = pagerAdapter
        viewpager!!.offscreenPageLimit = 5
        tabs!!.setupWithViewPager(viewpager)
    }

    //EventBus管理
    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    //销毁处理
    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, WarnService::class.java))
    }

    //事件处理
    @Subscribe(threadMode = ThreadMode.MainThread, sticky = true)
    fun onMessageEvent(event: SimpleEvent) {
        if (event.id == 1) {
            toolbar.subtitle = "设备在线"
            var message = event.message
            when{
                message?.contains(UrlData.Hum)!! and (!message?.contains(UrlData.Tem)) -> hum_text.text = "湿度$message(%)"
                message?.contains(UrlData.Tem)!!  and (!message?.contains(UrlData.Hum))-> tmp_text.text = "温度$message(℃)"
                message?.contains(UrlData.LPG)!! -> ch4_text.text = "甲烷$message"
                message?.contains(UrlData.CAR)!! and (Integer.parseInt(PatternUtil.getNumber(message!!)[0]) > UrlData.co_max) ->
                {
                    co_text!!.text = "超标" + message
                    playAnimation(co)
                }
                message?.contains(UrlData.CAR)!! -> co_text.text = "CO$message"
                message?.contains(UrlData.MET)!! -> c2h6_text.text = "乙烷$message"
            }

        } else if (event.id == 2) {
            toolbar.subtitle = "设备离线"
        }else if(event.id == 4){
            var message = event.message
            Log.d("EEF", message)
            when{
                message?.contains("0")!! -> {
                    windows_ctrl.text = "OPEN"
                    title_sub_from_net.text = "当前窗户状态：已关闭"
                }
                message?.contains("1")!! -> {
                    windows_ctrl.text = "CLOSE"
                    title_sub_from_net.text = "当前窗户状态：已开启"
                }
            }
        }
    }


    //心跳动画
    private fun playAnimation(view: View) {
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f))
        animationSet.addAnimation(AlphaAnimation(1.0f, 0.4f))
        animationSet.duration = 100
        animationSet.interpolator = AccelerateInterpolator()
        animationSet.fillAfter = true
        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                val animationSet = AnimationSet(true)
                animationSet.addAnimation(ScaleAnimation(1.4f, 1.0f, 1.4f,
                        1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f))
                animationSet.addAnimation(AlphaAnimation(0.4f, 1.0f))
                animationSet.duration = 200
                animationSet.interpolator = DecelerateInterpolator()
                animationSet.fillAfter = false
                view.startAnimation(animationSet)
            }
        })
        view.startAnimation(animationSet)
    }


    //抽屉启动
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> drawer_layout!!.openDrawer(GravityCompat.START)
        }
        return true
    }
}
