package com.wyy.monker.network.observer;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wyy.monker.network.bean.BaseEntity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/4/26.
 * 列表型数据解析处理
 */

public abstract class BaseListObserver<T> implements Observer<BaseEntity<String>> {
    private Context mContext;

    private Disposable mDisposable;
    private final String SUCCESS_CODE = "1";
    private Class<T> mClazz;

    public BaseListObserver(Context context, Class<T> clazz) {
        mContext = context;
        mClazz = clazz;
//        DMProgressBar.showProgressDialog(context);
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
    }

    @Override
    public void onNext(BaseEntity<String> value) {
        if (TextUtils.equals(value.getStatus(), SUCCESS_CODE)) {
            Gson gson = new Gson();
            List<T> list = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(value.getData());
                for (int i = 0; i < array.length(); i++) {
                    T t = gson.fromJson(array.get(i).toString(), mClazz);
                    list.add(t);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            onHandleSuccess(list);
        } else {
            onHandleError(value.getStatus(), value.getInfo());
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.d("gesanri", "error:" + e.toString());
        mDisposable.dispose();
        Toast.makeText(mContext, "网络异常，请稍后再试", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onComplete() {
        Log.d("gesanri", "onComplete");
        mDisposable.dispose();
//        DMProgressBar.hideProgressDislog();
    }

    public abstract void onHandleSuccess(List<T> t);

    public void onHandleError(String code, String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
