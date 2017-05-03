package com.wyy.monker.network.api;

import com.wyy.monker.base.BaseApplication;
import com.wyy.monker.network.interceptor.PublicHeaderInterceptor;
import com.wyy.monker.network.interceptor.TokenInterceptor;
import com.wyy.monker.utils.InternetUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者：王一阳
 * 时间：2017/1/24 14:31
 * 描述：
 */
public class ApiManage {

    private static ApiManage apiManage;


    public static ApiManage getInstence() {
        if (apiManage == null) {
            synchronized (ApiManage.class) {
                if (apiManage == null) {
                    apiManage = new ApiManage();
                }
            }
        }
        return apiManage;
    }


    /**
     * 设置缓存的拦截器，如果网络可用那么不是用缓存数据，否则使用缓存数据，缓存时间为3个小时
     * 增强用户体验，在没有网络情况下，在一段时间内也可以查看数据
     */
    Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {

            InternetUtil internetService = new InternetUtil(BaseApplication.getIntance().getContext());

            CacheControl.Builder cacheBuilder = new CacheControl.Builder();
            cacheBuilder.maxAge(0, TimeUnit.SECONDS);
            cacheBuilder.maxStale(365, TimeUnit.DAYS);
            CacheControl cacheControl = cacheBuilder.build();

            Request request = chain.request();
            if (!internetService.isNetworkConnected()) {
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            //如果网络可用不使用缓存
            if (internetService.isNetworkConnected()) {
                int maxAge = 0; // read from cache
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public ,max-age=" + maxAge)
                        .build();
            } else {
                //如果网络不可用那么使用缓存数据
                int maxStale = 60 * 60 * 3; // 3小时
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }


    };


    //缓存的位置
    File httpCacheDirectory = new File(BaseApplication.getIntance().getContext().getCacheDir(), "responses");
    int cacheSize = 10 * 1024 * 1024; // 10 MiB
    Cache cache = new Cache(httpCacheDirectory, cacheSize);

//    OkHttpClient client = new OkHttpClient.Builder()
//            .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
//            .cache(cache).build();

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(new PublicHeaderInterceptor())
            .addInterceptor(new TokenInterceptor())
            .build();

    OkHttpClient refreshClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(new PublicHeaderInterceptor())
            .build();
}
