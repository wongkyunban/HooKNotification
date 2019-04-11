package com.wong.hooknotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            HookHelper.hookNotificationManager(MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }



        TextView tv = (TextView)findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showNotification(v);

            }
        });
    }

    private boolean checkNotifySetting() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
        return manager.areNotificationsEnabled();


    }
    private void setPermission(){
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", MainActivity.this.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", MainActivity.this.getPackageName());
            intent.putExtra("app_uid", MainActivity.this.getApplicationInfo().uid);
            startActivity(intent);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
        } else if (Build.VERSION.SDK_INT >= 15) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
        }
        startActivity(intent);

    }

    private void showNotification(View v){
        if(checkNotifySetting()){

            String id = "channel_01";
            String name="我是渠道名字";

            //启动浏览器
            Intent contentIntent = new Intent();
            contentIntent.setData(Uri.parse("http://www.baidu.com"));//Url 就是你要打开的网址
            contentIntent.setAction(Intent.ACTION_VIEW);

            //点击通知栏做的事
            PendingIntent  pendingIntent = PendingIntent.getActivity(this, 1, contentIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            //获取系统通知服务
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
                Toast.makeText(this, mChannel.toString(), Toast.LENGTH_SHORT).show();
                notificationManager.createNotificationChannel(mChannel);
                notification = new Notification.Builder(this)
                        .setContentTitle("新消息1")
                        .setContentText("Hello world")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setChannelId(id)
                        .setContentIntent(pendingIntent)//设置pendingIntent,点击通知时就会用到
                        .build();
            } else {
                notification = new NotificationCompat.Builder(this)
                        .setContentTitle("新消息2")
                        .setContentText("Hello world")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setOngoing(true)
                        .setChannelId(id)
                        .setContentIntent(pendingIntent) //设置pendingIntent,点击通知时就会用到
                        .build();
            }

            notificationManager.notify(0, notification);


        }else {
            Snackbar.make(v,"请打开消息通知权限",Snackbar.LENGTH_SHORT).setAction("去设置", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPermission();
                }
            }).show();

        }
    }
}
