package com.wyy.monker.network.observer;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.wyy.monker.network.bean.BaseEntity;
import com.wyy.monker.utils.ToastUtil;

import java.lang.reflect.ParameterizedType;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.R.attr.value;

/**
 * Created by Administrator on 2017/4/25.
 */

public abstract class BaseNormalObserver<T> implements Observer<BaseEntity<String>> {
    private Context mContext;

    private Disposable mDisposable;
    private final String SUCCESS_CODE = "1";

    public BaseNormalObserver(Context context) {
        this(context, false);
    }

    public BaseNormalObserver(Context context, boolean isShowLoad) {
        mContext = context;
        if (isShowLoad) {
//            DMProgressBar.showProgressDialog(context);
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
    }

    @Override
    public void onNext(BaseEntity<String> value) {
        if (TextUtils.equals(value.getStatus(), SUCCESS_CODE)) {
            Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            T t = new Gson().fromJson(value.getData(), entityClass);
            onHandleSuccess(t);
        } else {
            onHandleError(value.getStatus(), value.getInfo());
        }
    }

    @Override
    public void onError(Throwable e) {
        Logger.e(e.toString());
        ToastUtil.show("网络异常，请稍后再试");
    }

    @Override
    public void onComplete() {
        Log.d("gesanri", "onComplete");
        mDisposable.dispose();
//        DMProgressBar.hideProgressDislog();
    }

    public abstract void onHandleSuccess(T t);

    public void onHandleError(String code, String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

}