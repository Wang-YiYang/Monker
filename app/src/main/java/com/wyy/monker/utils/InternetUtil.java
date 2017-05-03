package com.wyy.monker.utils;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import java.lang.reflect.Method;
import java.util.List;

public class InternetUtil {
	private Boolean isOpenoldStateForGps;
	private Context context;
	private ConnectivityManager connManager; // 上网连接管理器
	private WifiManager wifiManager; // wifi管理器

	public Boolean getIsOpenoldStateForGps() {
		return isOpenoldStateForGps;
	}

	public void setIsOpenoldStateForGps(Boolean isOpenoldStateForGps) {
		this.isOpenoldStateForGps = isOpenoldStateForGps;
	}

	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList;

	public InternetUtil(Context context) {
		this.context = context;
		connManager = (ConnectivityManager) this.context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiManager = (WifiManager) this.context
				.getSystemService(Context.WIFI_SERVICE);
	}

	/**
	 * @return 网络是否连接可用
	 */
	public boolean isNetworkConnected() {

		NetworkInfo networkinfo = connManager.getActiveNetworkInfo();

		if (networkinfo != null) {
			return networkinfo.isConnected();
		}
		return false;
	}

	/**
	 * @return wifi是否连接可用
	 */
	public boolean isWifiConnected() {

		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi != null) {
			return mWifi.isConnected();
		}

		return false;
	}

	/**
	 * 当wifi不能访问网络时，mobile才会起作用
	 * 
	 * @return GPRS是否连接可用
	 */
	public boolean isMobileConnected() {

		NetworkInfo mMobile = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mMobile != null) {
			return mMobile.isConnected();
		}
		return false;
	}

	/**
	 * GPRS网络开关 反射ConnectivityManager中hide的方法setMobileDataEnabled 可以开启和关闭GPRS网络
	 * 
	 * @param isEnable
	 * @throws Exception
	 */
	public void toggleGprs(boolean isEnable) throws Exception {
		Class<?> cmClass = connManager.getClass();
		Class<?>[] argClasses = new Class[1];
		argClasses[0] = boolean.class;

		// 反射ConnectivityManager中hide的方法setMobileDataEnabled，可以开启和关闭GPRS网络
		Method method = cmClass.getMethod("setMobileDataEnabled", argClasses);
		method.invoke(connManager, isEnable);
	}

	/**
	 * WIFI网络开关
	 * 
	 * @param enabled
	 * @return 设置是否success
	 */
	public boolean toggleWiFi(boolean enabled) {

		return wifiManager.setWifiEnabled(enabled);

	}

	// 检查当前wifi状态
	public int checkWifiState() {

		return wifiManager.getWifiState();
	}

	// 判断wifi是否打开
	public boolean isWifiEnabled() {

		return wifiManager.isWifiEnabled();
	}

	/*
	 * 获取wifi列表
	 */
	public List<ScanResult> getAllWifiList() {
		wifiManager.startScan();
		return mWifiList = wifiManager.getScanResults();
	}
	/*
	 * 判断网络是否开启
	 */
	public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null   
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;   
        }   
        return false;   
    }
	/**
	 * 
	 * @return 是否处于飞行模式
	 */
	public boolean isAirplaneModeOn() {
		// 返回值是1时表示处于飞行模式
		int modeIdx = Settings.System.getInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0);
		boolean isEnabled = (modeIdx == 1);
		return isEnabled;
	}
	
	/**
	 * 飞行模式开关
	 * 
	 * @param setAirPlane
	 */
	public void toggleAirplaneMode(boolean setAirPlane) {
		Settings.System.putInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, setAirPlane ? 1 : 0);
		// 广播飞行模式信号的改变，让相应的程序可以处理。
		// 不发送广播时，在非飞行模式下，Android 2.2.1上测试关闭了Wifi,不关闭正常的通话网络(如GMS/GPRS等)。
		// 不发送广播时，在飞行模式下，Android 2.2.1上测试无法关闭飞行模式。
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		// intent.putExtra("Sponsor", "Sodino");
		// 2.3及以后，需设置此状态，否则会一直处于与运营商断连的情况
		intent.putExtra("state", setAirPlane);
		context.sendBroadcast(intent);
	}

	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * 
	 * @param context
	 * @return true 表示开启
	 */
	public static final boolean isOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps || network) {
			return true;
		}

		return false;
	}

	/**
	 * 强制帮用户打开GPS
	 * 
	 * @param context
	 */
	public static final void openGPS(Context context) {
		Intent GPSIntent = new Intent();
		GPSIntent.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		GPSIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}
}
