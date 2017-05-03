package com.wyy.monker.network.interceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by Administrator on 2017/5/2.
 */

public class PublicHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Connection", "keep-alive")
                .addHeader("User-Agent", "dreammove-android")
                .build();
        Log.v("httpUrl", "request:" + request.toString());
        String method = request.method();

        if ("POST".equals(method)) {
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            Log.i("httpUrl", request.toString() + "-->请求参数:" + buffer.readUtf8());
        }
        return chain.proceed(request);
    }
}
