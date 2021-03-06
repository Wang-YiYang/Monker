package com.wyy.monker.base;

import android.app.Application;
import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.wyy.monker.utils.CrashHandler;
import com.wyy.mylibrary.okgo.OkGo;
import com.wyy.mylibrary.okgo.cache.CacheEntity;
import com.wyy.mylibrary.okgo.cache.CacheMode;
import com.wyy.mylibrary.okgo.cookie.store.PersistentCookieStore;

import java.util.logging.Level;

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
        MobclickAgent.setDebugMode(true);
        initCrashHandler();
        initOkGo();

    }


    private void initOkGo(){
        OkGo.init(this);
        OkGo.getInstance()
                // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                .debug("OkGo", Level.INFO, true)
                //如果使用默认的 60秒,以下三行也不需要传
                .setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)  //全局的连接超时时间
                .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)     //全局的读取超时时间
                .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)    //全局的写入超时时间

                //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                .setCacheMode(CacheMode.NO_CACHE)

                //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)

                //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                .setRetryCount(0)

                //如果不想让框架管理cookie（或者叫session的保持）,以下不需要
//              .setCookieStore(new MemoryCookieStore())            //cookie使用内存缓存（app退出后，cookie消失）
                .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效

                //可以设置https的证书,以下几种方案根据需要自己设置
                .setCertificates();                                //方法一：信任所有证书,不安全有风险
//              .setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
//              .setCertificates(getAssets().open("srca.cer"))      //方法三：使用预埋证书，校验服务端证书（自签名证书）
//              //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
//               .setCertificates(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"))//

                //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
//               .setHostnameVerifier(new SafeHostnameVerifier())

                //可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        return chain.proceed(chain.request());
//                    }
//                })
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
