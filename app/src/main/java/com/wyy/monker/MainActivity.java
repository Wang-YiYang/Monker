package com.wyy.monker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.wyy.monker.base.BaseActivity;
import com.wyy.monker.utils.StatusBarUtil;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.textview)
    TextView tvTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucent(this, 0);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }
}
