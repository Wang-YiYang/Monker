package com.wyy.monker.network.interceptor;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.wyy.monker.network.bean.BaseEntity;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by Administrator on 2017/5/2.
 */

public class TokenInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // try the request
        Response originalResponse = chain.proceed(request);
        ResponseBody responseBody = originalResponse.body();//请求结果
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }

        String bodyString = buffer.clone().readString(charset);

        Logger.d("body---------->" + bodyString);
        //请求结果
        BaseEntity<String> baseEntity = new Gson().fromJson(bodyString, BaseEntity.class);

        /***************************************/
        //请求结果如果token过期
//        if (TextUtils.equals(baseEntity.getStatus(), DMConst.PublicStatus.NO_LOGIN)) {
//            UserM userM = DMApplication.getmMyselfUser();
//            //取出本地的refreshToken
//            String refreshToken = userM.getRefresh_token();
//
//            // 通过一个特定的接口获取新的token，此处要用到同步的retrofit请求
//            Call<BaseEntity<String>> call = ApiManage.getInstence().getRefreshApi().refreshToken(refreshToken);
//            //要用retrofit的同步方式
//            BaseEntity<String> newToken = call.execute().body();
//            if (!TextUtils.equals(newToken.getStatus(), DMConst.PublicStatus.NORMAL)) {
//                originalResponse.body().close();
//                new ErrorHander(DMBaseActivity.activityList.get(0), newToken.getStatus(), newToken.getInfo());
//                return originalResponse;
//            }
//            //替换本地的token
//            Logger.e(newToken.toString());
//            TokenInfoM tokenInfoM = new Gson().fromJson(newToken.getData(), TokenInfoM.class);
//            DMApplication.setNewToken(tokenInfoM.getAccess_token());
//            DMApplication.setNewRefreshToken(tokenInfoM.getRefresh_token());
//            // create a new request and modify it accordingly using the new token
//
//            //替换原先请求中的access_token参数，并且再次发起请求
//            HttpUrl.Builder builder = request.url().newBuilder();
//            Request newRequest;
//            //Get方法和Post方法请求参数位置不一样，需要分开处理
//            if (TextUtils.equals(request.method(), "GET")) {
//                //重新设置参数，主要是为了替换access_token
//                Set<String> set = request.url().queryParameterNames();
//                Iterator<String> paramNames = set.iterator();
//                while (paramNames.hasNext()) {
//                    String paramName = paramNames.next();
//                    String paramValue = request.url().queryParameter(paramName);
//                    builder.removeAllQueryParameters(paramName);//先清除原先的参数，避免重复
//                    if (TextUtils.equals(paramName, "access_token")) {
//                        builder.addEncodedQueryParameter(paramName, tokenInfoM.getAccess_token());
//                    } else {
//                        builder.addEncodedQueryParameter(paramName, paramValue);
//                    }
//                }
//                newRequest = request.newBuilder().method(request.method(), null)
//                        .url(builder.build())
//                        .build();
//            } else {
//                //post请求时处理
//                FormBody.Builder formBody = new FormBody.Builder();
//                if (request.body() instanceof FormBody) {
//                    FormBody body = (FormBody) request.body();
//                    for (int i = 0; i < body.size(); i++) {
//                        if (TextUtils.equals(body.encodedName(i), "access_token")) {
//                            formBody.addEncoded("access_token", tokenInfoM.getAccess_token());
//                        } else {
//                            formBody.addEncoded(body.encodedName(i), body.encodedValue(i));
//                        }
//                    }
//                }
//                newRequest = request.newBuilder().method(request.method(), formBody.build())
//                        .url(builder.build())
//                        .build();
//            }
//
//            originalResponse.body().close();//清理原先的请求结果
//            return chain.proceed(newRequest);//新请求结果
//        }

        // 如果成功直接返回结果
        return originalResponse;
    }
}