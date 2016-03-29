package com.materlistview.materListView;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;


/**
 * 项目配置相关
 * @author 李嘉俊
 */
public class AppConfig {
	/**
	 *屏幕的宽度
	 */
	public static int 		SCREEN_WIDTH 	= 0;
	/**
	 *屏幕的高度
	 */
	public static int 		SCREEN_HEIGHT 	= 0;
	/**
	 *屏幕的密度
	 */
	public static float 	SCREEN_DENSITY 	= 0.0f;
	private static AppConfig instance;

	public static AppConfig getInstance(Context context){
		if(instance == null){
			instance = new AppConfig();
			initScreenMetrics(context);
		}
		return instance;
	}
	/**
	 * 初始化屏幕等参数
	 * @param context
	 */
	public static void initScreenMetrics(Context context) {
		if (SCREEN_DENSITY == 0 || SCREEN_WIDTH == 0 || SCREEN_HEIGHT == 0) {
			//屏幕参数初始化
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
			windowManager.getDefaultDisplay().getMetrics(dm);
			AppConfig.SCREEN_DENSITY = dm.density;
			AppConfig.SCREEN_HEIGHT = dm.heightPixels;
			AppConfig.SCREEN_WIDTH = dm.widthPixels;

		}
	}

}
