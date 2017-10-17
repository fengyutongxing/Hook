package com.zhang.hook;

import android.app.Application;

/**
 * Created by zhang_shuai on 2017/10/17.
 * Del:
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils utils = new Utils(this,ProxyActivity.class);
        try {
            utils.UtilsAms();
            utils.hookSystemHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
