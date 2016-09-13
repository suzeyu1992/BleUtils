package com.szysky.note.ble.util;



import android.util.Log;

import com.szysky.note.ble.activity.MainActivity;


public class SuLogUtils {
	private static final boolean DEBUG_I = true && MainActivity.DEBUG;
	private static final boolean DEBUG_E = true && MainActivity.DEBUG;
	private static final boolean DEBUG_W = true && MainActivity.DEBUG;
	private static final boolean DEBUG_S = true && MainActivity.DEBUG;
	private static final boolean DEBUG_D = true && MainActivity.DEBUG;



	public static void e( String msg) {
		e("sususu", msg);
	}

	public static void w(String msg) {
		w("sususu", msg);
	}

	public static void d( String msg) {
		d("sususu", msg);
	}
	
	public static void i(String msg) {
		i("sususu",msg);
	}
	
	
	public static void i(String tag, String msg) {
		if (DEBUG_E) {
			Log.i(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (DEBUG_E) {
			Log.e(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (DEBUG_W) {
			Log.w(tag, msg);
		}
	}

	public static void s(String msg) {
		if (DEBUG_S)
			System.out.println(msg);
	}

	public static void d(String tag, String msg) {
		// TODO Auto-generated method stub
		if (DEBUG_D)
			Log.d(tag, msg);
	}
	
	public static void suLog(String getUrl,String getKey, String method,String json) {
		//打印log信息
		SuLogUtils.i("sususu", "**********************************************************");
		SuLogUtils.i("sususu","***访问的地址:"+getUrl+"**********");
		SuLogUtils.i("sususu", "**********************************************************");
		SuLogUtils.i("sususu","***请求参数:" + getKey+"***请求方式:"+method+"**********");
		SuLogUtils.i("sususu", "**********************************************************");
		SuLogUtils.i("sususu","***返回结果:" + json+"**********");
		SuLogUtils.i("sususu", "**********************************************************");
		
	}
	
	
}

