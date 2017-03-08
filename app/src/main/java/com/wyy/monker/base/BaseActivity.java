package com.wyy.monker.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * 作者：王一阳
 * 时间：2017/3/8
 * 描述：基础界面
 */

public abstract class BaseActivity extends AppCompatActivity {
    public static ArrayList<BaseActivity> mActivityList = new ArrayList<BaseActivity>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        mActivityList.add(this);
    }

    protected abstract int getLayoutId();


    /**
     * 时间：2017/3/8 15:48
     * 描述：结束所有页面
     */
    public static void finishAllActivity() {
        for (BaseActivity activity : mActivityList) {
            if (activity != null)
                activity.finish();
        }
    }
}
