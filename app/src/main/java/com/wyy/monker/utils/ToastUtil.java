package com.wyy.monker.utils;

import android.content.Context;
import android.widget.Toast;


/**
 * 解决方案：为了解决解决Toast重复显示问题
 */
public class ToastUtil {
	private static Toast mToast;

	public static void showToast(Context context, String text) {
		if (mToast == null) {
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	public static void showErrorToast(Context context) {
		showToast(context, "错误");
	}

}