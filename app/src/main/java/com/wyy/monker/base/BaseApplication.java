package com.wyy.monker.base;

import android.app.Application;
import android.content.Context;

import com.wyy.monker.utils.CrashHandler;

/**
 * 时间：2017/3/8
 * 描述：
 */

public class BaseApplication extends Application {
    private Context mContext;
    private static BaseApplication nIntance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        nIntance = this;
        initCrashHandler();
    }

    /**
     * 时间：2017/3/8 15:29
     * 描述：获取当前实例
     */
    public static BaseApplication getIntance() {
        return nIntance;
    }

    /**
     * 时间：2017/3/8 15:29
     * 描述：获取全局上下文
     */
    public Context getContext() {
        return mContext;
    }

   /**
    * 时间：2017/3/8 15:51
    * 描述：初始化程序崩溃捕捉处理
    */
    protected void initCrashHandler() {
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext());
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }
}
