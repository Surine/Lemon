package com.surine.lemon.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.surine.lemon.Data.UrlData;
import com.surine.lemon.JavaBean.SimpleEvent;
import com.surine.lemon.MainActivity;
import com.surine.lemon.R;
import com.surine.lemon.Utils.PatternUtil;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class WarnService extends Service {
    private Intent intent;
    private NotificationManager manager;
    private PendingIntent pi;

    public WarnService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        intent = new Intent(this, MainActivity.class);
        pi = PendingIntent.getActivity(this,0,intent,0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("危险气体检测服务正在运行")
                .setContentText("为了您的安全请勿轻易停止此服务！")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi)
                .build();
        startForeground(1,notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MainThread, sticky = true)
    public void onMessageEvent(SimpleEvent event) {
        if(event.getId()==1){
            String contain = event.getMessage();
            if(contain.contains(UrlData.Hum)&&!(contain.contains(UrlData.Tem))){
                //tmp
            }else if(contain.contains(UrlData.Tem)&&!(contain.contains(UrlData.Hum))){
                //hum
            }else if(contain.contains(UrlData.LPG)){
                //lpg
            }else if(contain.contains(UrlData.CAR)){
                //co
                if(Integer.parseInt(PatternUtil.getNumber(contain).get(0))>4){
                    //CO超标
                    intent = new Intent(this, MainActivity.class);
                    pi = PendingIntent.getActivity(this,0,intent,0);
                     manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                           Notification notification = new NotificationCompat.Builder(this)
                            .setContentTitle("危险警告")
                            .setContentText("您的室内可燃气体超标，请注意开窗通气！")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setFullScreenIntent(pi,true)
                            .setContentIntent(pi)
                            .build();
                    manager.notify(1,notification);
                }else{
                    intent = new Intent(this, MainActivity.class);
                    pi = PendingIntent.getActivity(this,0,intent,0);
                    Notification notification = new NotificationCompat.Builder(this)
                            .setContentTitle("危险气体检测服务正在运行")
                            .setContentText("为了您的安全请勿轻易停止此服务!")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pi)
                            .build();
                    manager.notify(1,notification);
                }
            }else if(contain.contains(UrlData.MET)){
                //met
            }
        }else if(event.getId() == 3){
             if(event.getMessage().contains("N")){
                 //没火焰
                 Log.d("SSSS",event.getMessage());
             }else{
                 Log.d("SSSS",event.getMessage());
                 //报警
                 //CO超标
                 intent = new Intent(this, MainActivity.class);
                 pi = PendingIntent.getActivity(this,0,intent,0);
                 manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                 Notification notification = new NotificationCompat.Builder(this)
                         .setContentTitle("危险警告")
                         .setContentText("您的室内存在明火，请注意检查！")
                         .setWhen(System.currentTimeMillis())
                         .setSmallIcon(R.mipmap.ic_launcher)
                         .setFullScreenIntent(pi,true)
                         .setContentIntent(pi)
                         .build();
                 manager.notify(1,notification);
             }
        }
    }
}
