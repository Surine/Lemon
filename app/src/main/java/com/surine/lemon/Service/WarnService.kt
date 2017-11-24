package com.surine.lemon.Service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.surine.lemon.Data.UrlData
import com.surine.lemon.JavaBean.SimpleEvent
import com.surine.lemon.MainActivity
import com.surine.lemon.R
import com.surine.lemon.Utils.PatternUtil
import de.greenrobot.event.EventBus
import de.greenrobot.event.Subscribe
import de.greenrobot.event.ThreadMode

class WarnService : Service() {
    private var intent: Intent? = null
    private var manager: NotificationManager? = null
    private var pi: PendingIntent? = null

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        intent = Intent(this, MainActivity::class.java)
        pi = PendingIntent.getActivity(this, 0, intent, 0)
        val notification = NotificationCompat.Builder(this)
                .setContentTitle("危险气体检测服务正在运行")
                .setContentText("为了您的安全请勿轻易停止此服务！")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi)
                .build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MainThread, sticky = true)
    fun onMessageEvent(event: SimpleEvent) {
        if (event.id == 1) {
            val contain = event.message
            if (contain!!.contains(UrlData.Hum) && !contain.contains(UrlData.Tem)) {
                //tmp
            } else if (contain.contains(UrlData.Tem) && !contain.contains(UrlData.Hum)) {
                //hum
            } else if (contain.contains(UrlData.LPG)) {
                //lpg
            } else if (contain.contains(UrlData.CAR)) {
                //co
                if (Integer.parseInt(PatternUtil.getNumber(contain)[0]) >  UrlData.co_max) {
                    //CO超标
                    intent = Intent(this, MainActivity::class.java)
                    pi = PendingIntent.getActivity(this, 0, intent, 0)
                    manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    val notification = NotificationCompat.Builder(this)
                            .setContentTitle("危险警告")
                            .setContentText("您的室内可燃气体超标，请注意开窗通气！")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setFullScreenIntent(pi, true)
                            .build()
                    manager!!.notify(2, notification)
                }
            } else if (contain.contains(UrlData.MET)) {
                //met
            }
        } else if (event.id == 3) {
            if (event.message!!.contains("N")) {
                //没火焰
            } else {
                //报警
                //CO超标
                intent = Intent(this, MainActivity::class.java)
                pi = PendingIntent.getActivity(this, 0, intent, 0)
                manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val notification = NotificationCompat.Builder(this)
                        .setContentTitle("危险警告")
                        .setContentText("您的室内存在明火，请注意检查！")
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setFullScreenIntent(pi, true)
                        .build()
                manager!!.notify(3, notification)
            }
        }
    }
}
