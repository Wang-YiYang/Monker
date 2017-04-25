package com.wyy.monker.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * 时间：2017/3/8
 * 描述：基础界面
 */

public abstract class BaseActivity extends AppCompatActivity {
    public static ArrayList<BaseActivity> mActivityList = new ArrayList<BaseActivity>();
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        mActivityList.add(this);
        mContext=this;
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    /**
     * 设置布局文件
     * @return
     */
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

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
