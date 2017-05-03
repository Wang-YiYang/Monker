package com.wyy.monker.network.observer;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wyy.monker.network.bean.BaseEntity;
import com.wyy.monker.network.bean.BaseRefreshData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * Created by Administrator on 2017/4/26.
 * 列表型数据解析处理
 */

public abstract class BaseRefreshListObserver<T> implements Observer<BaseEntity<String>> {
    private Context mContext;
    private Disposable mDisposable;
    private final String SUCCESS_CODE = "1";
    private Class<T> mClazz;

    public BaseRefreshListObserver(Context context, Class<T> clazz) {
        mContext = context;
        mClazz=clazz;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
    }

    @Override
    public void onNext(BaseEntity<String> value) {

        if (TextUtils.equals(value.getStatus(), SUCCESS_CODE)) {
            Gson gson = new Gson();
//            Class<T> entityClass = getEntityClass();
            try {
                JSONObject json = new JSONObject(value.getData());
                JSONArray arr = json.getJSONArray("list");
                JSONObject page = json.getJSONObject("page");
                List<T> list = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    T e = gson.fromJson(arr.getJSONObject(i).toString(), mClazz);
                    list.add(e);
                }
                BaseRefreshData<T> baseData = new BaseRefreshData();
                BaseRefreshData.PageBean pageBean = gson.fromJson(page.toString(), BaseRefreshData.PageBean.class);
                baseData.setList(list);
                baseData.setPage(pageBean);
                onHandleSuccess(baseData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
    }

    public abstract void onHandleSuccess(BaseRefreshData response);

    public void onHandleError(String code, String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    public Class<T> getEntityClass() {
        Type type = getClass();
        ParameterizedType pType = (ParameterizedType) type;
        Type[] params = pType.getActualTypeArguments();
        @SuppressWarnings("unchecked")
        Class<T> c = (Class<T>) params[0];
        return c;
    }
}
