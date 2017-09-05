package com.surine.lemon;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.surine.lemon.Data.UrlData;
import com.surine.lemon.Fragment.C2H6Fragment;
import com.surine.lemon.Fragment.CH4Fragment;
import com.surine.lemon.Fragment.COChartFragment;
import com.surine.lemon.Fragment.HumFragment;
import com.surine.lemon.Fragment.TmpChartFragment;
import com.surine.lemon.InitApp.BaseActivity;
import com.surine.lemon.JavaBean.SimpleEvent;
import com.surine.lemon.MQTT.Client;
import com.surine.lemon.Service.WarnService;
import com.surine.lemon.Utils.PatternUtil;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class MainActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    private ImageView tmp;
    private ImageView hum;
    private ImageView ch4;
    private ImageView co;
    private ImageView c2h6;
    private TextView tmp_text;
    private TextView hum_text;
    private TextView ch4_text;
    private TextView co_text;
    private TextView c2h6_text;
    private Client client;
    private TextView mTextView;
    private TextView mTextView2;
    private ImageButton mImageButton;
    private String status = "CLOSE";
    Spinner s;
    ArrayAdapter<CharSequence> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, WarnService.class);
        startService(intent);

        initView();
        SharedPreferences share = getSharedPreferences("data",MODE_PRIVATE);
        status = share.getString("STATUS","CLOSE");
        if(status.equals("CLOSE")){
            mImageButton.setImageResource(R.drawable.c);
        }else{
            mImageButton.setImageResource(R.drawable.o);
        }

        setListener();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //mqtt
                client= new Client();
                client.start();
            }
        }).start();

        replaceFragment(TmpChartFragment.getInstance("TMP"));
    }

    private void setListener() {
       mImageButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               SharedPreferences share = getSharedPreferences("data",MODE_PRIVATE);
               status = share.getString("STATUS","CLOSE");
               if(status.equals("CLOSE")){
                   //开窗
                   status = "OPEN";
                   mImageButton.setImageResource(R.drawable.c);
                   client.sendCmd(UrlData.open);
                   Toast.makeText(MainActivity.this,"宝宝关闭了厨房的窗户",Toast.LENGTH_SHORT).show();
               }else{
                   //关窗
                   status = "CLOSE";
                   mImageButton.setImageResource(R.drawable.o);
                   client.sendCmd(UrlData.close);
                   Toast.makeText(MainActivity.this,"宝宝打开了厨房的窗户",Toast.LENGTH_SHORT).show();
               }
               //储存
               SharedPreferences.Editor ed = getSharedPreferences("data",MODE_PRIVATE).edit();
               ed.putString("STATUS",status).apply();
           }
       });

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.message);
                builder.setPositiveButton("OK",null);
                builder.show();
            }
        });
    }

    private void initView() {
       tmp = (ImageView) findViewById(R.id.tmp);
       hum = (ImageView) findViewById(R.id.hum);
       ch4 = (ImageView) findViewById(R.id.ch4);
       co = (ImageView) findViewById(R.id.co);
       c2h6 = (ImageView) findViewById(R.id.c2h6);
        tmp_text = (TextView) findViewById(R.id.tmp_text);
        hum_text = (TextView) findViewById(R.id.hum_text);
        ch4_text = (TextView) findViewById(R.id.ch4_text);
        co_text = (TextView) findViewById(R.id.co_text);
        c2h6_text = (TextView) findViewById(R.id.c2h6_text);
        mTextView = (TextView) findViewById(R.id.textView);
        mTextView2 = (TextView) findViewById(R.id.status_text);
        mImageButton = (ImageButton) findViewById(R.id.button);
        adapter = ArrayAdapter.createFromResource(this, R.array.data_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        s = (Spinner)findViewById(R.id.spinner);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(this);
        s.setPrompt("数据项");
    }

    //overload
    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tran = fm.beginTransaction();
        tran.replace(R.id.fragment_chart, fragment);
        tran.commit();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent(this, WarnService.class);
        stopService(intent);

    }

    @Subscribe(threadMode = ThreadMode.MainThread, sticky = true)
    public void onMessageEvent(SimpleEvent event) {
        if(event.getId()==1){
            mTextView2.setText("我家的厨房 ：设备在线");
            String contain = event.getMessage();
            if(contain.contains(UrlData.Hum)&&!(contain.contains(UrlData.Tem))){
                hum_text.setText("湿度"+contain+"(%)");
            }else if(contain.contains(UrlData.Tem)&&!(contain.contains(UrlData.Hum))){
                tmp_text.setText("温度"+contain+"(℃)");
            }else if(contain.contains(UrlData.LPG)){
                ch4_text.setText("甲烷"+contain);
            }else if(contain.contains(UrlData.CAR)){
                co_text.setText("一氧化碳"+contain);
                if(Integer.parseInt(PatternUtil.getNumber(contain).get(0))>4){
                    playAnimation(co);
                    co_text.setText("超标"+contain);
                }
            }else if(contain.contains(UrlData.MET)){
                c2h6_text.setText("乙烷"+contain);
            }
        }else if(event.getId() == 2){
            mTextView2.setText("我家的厨房 ：设备离线");
        }
    }

    private void playAnimation(final View view) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f));
        animationSet.addAnimation(new AlphaAnimation(1.0f, 0.4f));

        animationSet.setDuration(100);
        animationSet.setInterpolator(new AccelerateInterpolator());
        animationSet.setFillAfter(true);

        animationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(new ScaleAnimation(1.4f, 1.0f, 1.4f,
                        1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f));
                animationSet.addAnimation(new AlphaAnimation(0.4f, 1.0f));

                animationSet.setDuration(200);
                animationSet.setInterpolator(new DecelerateInterpolator());
                animationSet.setFillAfter(false);

                view.startAnimation(animationSet);
            }
        });
        view.startAnimation(animationSet);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selected = adapterView.getItemAtPosition(i).toString();
        if(selected.equals("温度")){
            replaceFragment(TmpChartFragment.getInstance("TMP"));
        }else if(selected.equals("一氧化碳")){
            replaceFragment(COChartFragment.getInstance("CO"));
        }else if(selected.equals("湿度")){
            replaceFragment(HumFragment.getInstance("HUM"));
        }else if(selected.equals("甲烷")){
            replaceFragment(CH4Fragment.getInstance("CH4"));
        }else if(selected.equals("乙烷")){
            replaceFragment(C2H6Fragment.getInstance("C2H6"));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
