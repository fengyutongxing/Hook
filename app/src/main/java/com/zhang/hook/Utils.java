package com.zhang.hook;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zhang_shuai on 2017/10/17.
 * Del:
 */

public class Utils {
    private Context mContext;
    private Class<?> mProxyActivty;
    public Utils(Context context , Class<?> proxy){
        this.mContext = context;
        this.mProxyActivty = proxy;
    }
    public void UtilsAms() throws Exception {
        //得到系统ActivityManager
        Class<?> forname = Class.forName("android.app.ActivityManagerNative");
        //得到IActivityManagerSingleton
        Field defaultField = forname.getDeclaredField("gDefault");
        defaultField.setAccessible(true);//java语言调用
        Object defaultValue = defaultField.get(null);//静态

        Class<?> forName = Class.forName("android.util.Singleton");
        Field instance = forName.getDeclaredField("mInstance");
        instance.setAccessible(true);
        Object activityManager = instance.get(defaultValue);

        Class<?> iActivity = Class.forName("android.app.IActivityManager");
        AMSInvokeHandler handler = new AMSInvokeHandler(activityManager);
        //l拦截对象
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class<?>[]{iActivity},handler);
        instance.set(defaultValue,proxy);
    }

    class AMSInvokeHandler implements InvocationHandler{
        Object iActivytManegerObj;
        public AMSInvokeHandler(Object iActivytManegerObj){
            this.iActivytManegerObj = iActivytManegerObj;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if("startActivity".contains(method.getName())){
                Intent intent = null;
                int index = 0;
                for (int i = 0; i < args.length; i++) {
                    if(args[i] instanceof Intent){
                        intent = (Intent) args[i];
                        index = i;
                        break;
                    }
                }
                //替换
                Intent proxyIntent = new Intent();
                ComponentName componetName = new ComponentName(mContext,mProxyActivty);
                proxyIntent.setComponent(componetName);
                proxyIntent.putExtra("oldIntent",intent);
                args[index] = proxyIntent;
                return method.invoke(iActivytManegerObj,args);
            }
            return method.invoke(iActivytManegerObj,args);
        }
    }

    //拦截系统回调
    public void hookSystemHandler(){
        try {
            Class<?> forName = Class.forName("android.app.ActivityThread");
            Field currentActivityThread =  forName.getDeclaredField("sCurrentActivityThread");
            currentActivityThread.setAccessible(true);
            Object objActivity = currentActivityThread.get(null);

            Field mH = forName.getDeclaredField("mH");
            mH.setAccessible(true);
            Handler handlerObj = (Handler) mH.get(objActivity);

            Field callBackObj = Handler.class.getDeclaredField("mCallback");//系统的callBack
            callBackObj.setAccessible(true);

            AcivityCallBack callBack = new AcivityCallBack(handlerObj);
            callBackObj.set(handlerObj,callBack);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class AcivityCallBack implements Handler.Callback{

        private Handler handler;
        public AcivityCallBack(Handler handler){
            this.handler = handler;
        }
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 100){
                handlerLaunchActivity(msg);
            }
            handler.handleMessage(msg);//发送消息给系统
            return true;
        }

        private void handlerLaunchActivity(Message msg) {
            Object obj = msg.obj;
            try {
                Field intnetField = obj.getClass().getDeclaredField("intent");
                intnetField.setAccessible(true);
                Intent proxyIntent = (Intent) intnetField.get(obj);
                Intent realinIntent = proxyIntent.getParcelableExtra("oldIntent");
                if(realinIntent!=null){
                    proxyIntent.setComponent(realinIntent.getComponent());//替换
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
