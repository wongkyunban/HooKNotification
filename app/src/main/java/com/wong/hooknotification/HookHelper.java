package com.wong.hooknotification;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author WongKyunban
 * description
 * created at 2019-04-08 下午5:20
 * @version 1.0
 */
public class HookHelper {
    public static void hookNotificationManager(final Context context) throws Exception{
        NotificationManager notificationManager  = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Method getService = NotificationManager.class.getDeclaredMethod("getService");
        getService.setAccessible(true);
        // 第一步：得到系统的 sService
        final Object sOriginService = getService.invoke(notificationManager);


        // 第二步：得到我们的动态代理对象
        Class iNotiMngClz = Class.forName("android.app.INotificationManager");
        Object proxyNotificationManager = Proxy.newProxyInstance(context.getClass().getClassLoader(),new Class[]{iNotiMngClz},new InvocationHandler(){

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                String name = method.getName();
                if(args != null && args.length > 0){
                    for(Object arg:args){
                        Log.i("HOOK HOOK","invoke:arg="+arg);
                    }
                }
                Toast.makeText(context.getApplicationContext(),"检测到有人发通知了",Toast.LENGTH_SHORT).show();
                // 操作交由 sOriginService 处理，不拦截通知
                return method.invoke(sOriginService,args);
            }
        });
        // 第三步：偷梁换柱，使用 proxyNotificationManager 替换系统的 sService

        Field sServiceField = NotificationManager.class.getDeclaredField("sService");
        sServiceField.setAccessible(true);
        sServiceField.set(notificationManager,proxyNotificationManager);
    }
}
