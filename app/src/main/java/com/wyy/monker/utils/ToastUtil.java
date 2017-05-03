package com.wyy.monker.utils;

import android.content.Context;
import android.widget.Toast;

import com.wyy.monker.base.BaseApplication;


/**
 * 解决方案：为了解决解决Toast重复显示问题
 */
public class ToastUtil {
	private static Toast mToast;

	public static void show( String text) {
		if (mToast == null) {
			mToast = Toast.makeText(BaseApplication.getIntance().getContext(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	public static void showErrorToast(Context context) {
		show( "错误");
	}

}