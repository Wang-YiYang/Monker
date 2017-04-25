package com.wyy.monker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;
import com.wyy.monker.base.BaseActivity;
import com.wyy.monker.utils.ImageUtil;
import com.wyy.monker.utils.StatusBarUtil;
import com.wyy.monker.utils.ToastUtil;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.iv)
    ImageView ivTest;

    private long mFirstExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucent(this, 0);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.hh);
        bitmap = ImageUtil.addFrame(bitmap, 10, R.color.colorPrimaryDark);
        ivTest.setImageBitmap(bitmap);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    private void exitApp() {
        if (System.currentTimeMillis() - mFirstExit > 2000) {
            ToastUtil.showToast(mContext, "在按一次退出程序");
            mFirstExit = System.currentTimeMillis();
        } else {
            MobclickAgent.onKillProcess(mContext);
            System.exit(0);
        }


    }


}
