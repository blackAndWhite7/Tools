package com.project.wei.jpush;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by wei on 2016/8/30 0030.
 */
//  一定要记得建 Application
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}
