package com.materlistview.CrashHandler;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.materlistview.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 查看服务器请求/响应日志
 * 
 */
public class ServerLogActivity extends Activity {

	private static String TAG = "ServerLog";
	String curFileName = null;
	TextView tv_show = null;
//	private static String dirPath = "log_data";
	private static String dirPath = "GMALL_log_data";
	private  static String appName = "";
	private RelativeLayout rl_top;
	private Button btn_back;
	private TextView tv_title;
	private Button btn_clear;
	private ScrollView scrollView;
	public static final String APPNAME = "APPNAME";
	public static SimpleDateFormat DB_DATE_FORMAT_NORMAL = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	final String path_server = Environment.getExternalStorageDirectory()
			.getPath() + "/" + dirPath + "/server";
	final String path_crash = Environment.getExternalStorageDirectory()
			.getPath() + "/" + dirPath + "/crash";
	public static Boolean isCrash = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serverlog);
		initView();
		appName = getIntent().getStringExtra(APPNAME);
		new LoadTask().execute();
	}

	private void initView() {
		isCrash = true;
		rl_top = (RelativeLayout) findViewById(R.id.rl_top);
		btn_back = (Button) findViewById(R.id.btn_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_show = (TextView) findViewById(R.id.tv_show);
		btn_clear = (Button) findViewById(R.id.btn_clear);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btn_clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clearLog();
				tv_show.setText("没有日志记录");
				finish();
			}
		});
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		clearLog();
	}

	class LoadTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			String tmp = "";
			if (isCrash) {
				tmp = getLogString(path_crash);
			} else {
				tmp = getLogString();
			}
			return tmp;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				tv_show.setText(result);
				scroll2Bottom(scrollView, tv_show);
			} else {
				tv_show.setText("没有日志记录");
			}
		}
	}

	public static void scroll2Bottom(final ScrollView scroll, final View inner) {
		Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (scroll == null || inner == null) {
					return;
				}
				// 内层高度超过外层
				int offset = inner.getMeasuredHeight()
						- scroll.getMeasuredHeight();
				if (offset < 0) {
					Log.v("scroll2Bottom", "定位...");
					offset = 0;
				}
				scroll.scrollTo(0, offset);
			}
		});
	}

	public static boolean isSDCardExist() {
		String status = Environment.getExternalStorageState();
		boolean flag = status.equals(Environment.MEDIA_MOUNTED);
		return flag;

	}

	private String getLogString() {
		try {
			curFileName = getTodayLogFullName(path_server + "/");
			FileInputStream stream = null;
			File file = new File(curFileName);
			if (!isSDCardExist()) {
				return null;
			}
			if (!file.exists()) {
				return null;
			}
			stream = new FileInputStream(file);
			// allSize=stream.available();
			byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			String content = new String(bytes);
			stream.close();
			return content;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private String getLogString(String path) {
		try {
			curFileName = getTodayLogFullName(path + "/");
			FileInputStream stream = null;
			File file = new File(curFileName);
			if (!isSDCardExist()) {
				return null;
			}
			if (!file.exists()) {
				return null;
			}
			stream = new FileInputStream(file);
			// allSize=stream.available();
			byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			String content = new String(bytes);
			stream.close();
			return content;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static String saveSpecialString2File(String info, String desc) {
			StringBuffer sb = new StringBuffer();
			sb.append("====================这里开始========================\n");
			sb.append(desc + "\n");

			System.currentTimeMillis();
			String time = DB_DATE_FORMAT_NORMAL.format(new Date());

			sb.append(info);
			sb.append("============================================\n"
					+ android.os.Build.MODEL);
			sb.append("\n");
			sb.append(time);
			sb.append("\n============================================\n");
			try {

				String fileName = appName+"_crash_log_"
						+ (Calendar.getInstance().get(Calendar.MONTH) + 1)
						+ "_"
						+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
						+ ".txt";
				// String fileName = "crash-" + time + "-" + timestamp + ".txt";
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// String path = "/sdcard/crash/";
					String path = CrashHandler.CRASH_PATH;
					File dir = new File(path);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(
							path + fileName, true);
					fos.write(sb.toString().getBytes());
					fos.close();
				}
				return fileName;
			} catch (Exception e) {
				Log.e(TAG, "an error occured while writing file...", e);
			}
		return null;
	}


	private static String getTodayLogFullName(String path) {
		String fileName = "";
		if (isCrash) {
			fileName =appName+ "_crash_log_"
					+ (Calendar.getInstance().get(Calendar.MONTH) + 1) + "_"
					+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
					+ ".txt";
		} else {
			fileName =appName+ "_sever_log_"
					+ (Calendar.getInstance().get(Calendar.MONTH) + 1) + "_"
					+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
					+ ".txt";
		}
		return path + fileName;
	}

	/*
	 * private static String getTodayLogFullName(){ String path =
	 * Environment.getExternalStorageDirectory
	 * ().getPath()+"/"+dirPath+"/server/"; String fileName =
	 * "sever_log_"+(Calendar
	 * .getInstance().get(Calendar.MONTH)+1)+"_"+Calendar.getInstance
	 * ().get(Calendar.DAY_OF_MONTH)+".txt"; return path + fileName; }
	 */

	/**
	 * 保存信息到文件中
	 */
	public static Void saveInfo2File(String info) {

		StringBuffer sb = new StringBuffer();
		Date date = new Date(System.currentTimeMillis());
		String time = DB_DATE_FORMAT_NORMAL.format(date);
		sb.append("\n");
		sb.append("Log time - " + time + "##  currentTimeMillis - "
				+ System.currentTimeMillis());
		sb.append("\n");
		sb.append(info);
		try {
			final String path = Environment.getExternalStorageDirectory()
					.getPath() + "/" + dirPath;
			String path2 = "";
			if (isCrash) {
				path2 = path + "/crash/";
			} else {
				path2 = path + "/server/";
			}

			final String filePath = getTodayLogFullName(path2);
			if (isSDCardExist()) {
				final File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdir();
				}
				final File dir2 = new File(path2);
				if (!dir2.exists()) {
					dir2.mkdir();
				}
				File file = new File(filePath);
				if (!file.exists()) {
					file.createNewFile();
				}

				FileOutputStream fos = new FileOutputStream(filePath, true);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
		} catch (Exception e) {
			Log.e("ReqAndResLogActivity",
					"an error occured while writing file...", e);
			if (e != null)
				e.printStackTrace();
		}
		return null;
	}

	// 删除文件
	public void delFile(File file) {
		try {
			file.delete();
		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			Log.e("FileUtil", "删除文件操作出错");
			e.printStackTrace();
			Log.e("FileUtil", e.toString());
		}
	}

	/**
	 * 清除所有日志
	 */
	public void clearLog() {
		String path = "";
		if (isCrash) {
			path = path_crash + "/";
		} else {
			path = path_server + "/";
		}
		File dir = new File(path);
		if (dir != null&& dir.exists()) {
			for (File afile : dir.listFiles()) {
				if (afile != null) {
					delFile(afile);
				}
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "清除所有日志");
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			clearLog();
			tv_show.setText("没有日志记录");
			finish();
			break;
		}
		return true;

	}

}
