package com.lyyjy.yfyb.fishcontroller;

import android.app.Application;

/**
 * Created by Administrator on 2016/9/17.
 */
public class ContextUtil extends Application{
    private static ContextUtil instance;

    public static ContextUtil getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }
}
