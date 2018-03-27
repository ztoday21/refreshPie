package com.ztoday21.refreshPie;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

enum RefreshMethod {
	UNKNOWN,
	USE_INTENT,
	USE_EPDBLK,
	USE_JNIEPDC,
}

public class service_main extends Service implements OnTouchListener{

	private static final String LOG_TAG = "ztoday21.refreshPie.service_main";

	// shared value
	public static int		interval = 0;
	public static int		timeInterval = 0;
	public static boolean	isRunning = false;
	public int touchCnt;
	long prevTouchTime = 0;

	// internal used
	public TextView		tv = null;
	public Intent		refreshIntent = null;

	private com.eink.epdc.Main epdc;
	private RefreshMethod method = RefreshMethod.UNKNOWN;

	SharedPreferences prefs;
	private ArrayList<Setting.FrontActivityInfo> frontActivityInfos = new ArrayList<Setting.FrontActivityInfo>();

	@SuppressLint("HandlerLeak")
	public Handler _handler = new Handler()
	{
		private void logToFile(String activityClassName) {
			final String filePath = Environment.getExternalStorageDirectory() + "/frontactivity.txt";


			try {
				@SuppressWarnings("resource")
				RandomAccessFile file = new RandomAccessFile(filePath, "rw");
				file.seek(file.length());
				file.writeBytes("\r\n" + activityClassName);
			}
			catch (IOException e) {
				Toast.makeText(service_main.this, e.toString(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

		}

		public void handleMessage(Message msg)
		{
			switch (method) {
				case USE_INTENT:
					startActivity(refreshIntent);
					return;
				case USE_JNIEPDC:
					try {
						epdc.FullRefresh2();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return;
				case USE_EPDBLK:
					try {
						java.lang.Process process = Runtime.getRuntime().exec("/system/bin/epdblk 10");
						process.getInputStream().close();
						process.getOutputStream().close();
						process.getErrorStream().close();
						process.waitFor();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return;
				default:
					Log.i(LOG_TAG, "unsupport refresh method: " + method);
			}
		}
	};

	@Override
	public void onCreate()
	{

		Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
		if ( !(handler instanceof CustomUncaughtExceptionHandler) ) {
			Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler(this));
		}

		super.onCreate();

		prefs = getSharedPreferences(main._saveName, MODE_PRIVATE);

	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Toast.makeText(this, "Stopped refreshPie service.", Toast.LENGTH_SHORT).show();
		isRunning = false;

		uninstallTouchHandler();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId )
	{
		if (isRunning) {
			Toast.makeText(this, "Already started service.", Toast.LENGTH_SHORT).show();
			return Service.START_STICKY;
		}

		findRefreshMethod();

		installTouchHandler();

		loadSetting();

		Toast.makeText(this, "Started refreshPie service", Toast.LENGTH_SHORT).show();
		isRunning = true;

		return Service.START_STICKY;
	}

	private void installTouchHandler() {
		if (tv != null) {
			return;
		}

		tv = new TextView(this);
		tv.setOnTouchListener(this);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);

		WindowManager winmgr = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

		winmgr.addView(tv, lp);
	}

	private void uninstallTouchHandler() {
		if (tv == null) {
			return;
		}
		// FIXME: is this right way?
		// remove view from window manager
		WindowManager winmgr = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		winmgr.removeView(tv);
		tv = null;
	}

	private RefreshMethod findRefreshMethod() {
		// TODO: find refresh app
		if (method != RefreshMethod.UNKNOWN) {
			return method;
		}

		// TODO intent list
		try {
			refreshIntent = getPackageManager().getLaunchIntentForPackage("com.nextpapyrus.Refresh2");
		} catch (Exception e) {
		}
		if (refreshIntent != null) {
			method = RefreshMethod.USE_INTENT;
			return method;
		}

		try {
			epdc = new com.eink.epdc.Main();
			method = RefreshMethod.USE_JNIEPDC;
			return method;
		} catch (UnsatisfiedLinkError e) {
		}

		if (new File("/system/bin/epdblk").exists()) {
			method = RefreshMethod.USE_EPDBLK;
			return method;
		}
		return RefreshMethod.UNKNOWN;
	}

	@SuppressLint("NewApi")
	private void loadSetting() {
		frontActivityInfos = Setting.getFrontActivityInfos(this);

		timeInterval = Integer.parseInt(prefs.getString("time_interval", main.defaultTimeInterval));
		interval = Integer.parseInt(prefs.getString("interval", main.defaultInterval));
	}

	public Intent getIntentByLabel(String pkg, String cls) {
		Intent i = new Intent();
		i.setComponent(new ComponentName(pkg, cls));
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	void onDoubleTap()
	{
		if (prefs.getBoolean(Setting.keyLogFrontActivityClassname, false)) {
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

			// get the info from the currently running task
			List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);

			ComponentName componentInfo = taskInfo.get(0).topActivity;

			String frontActivityClassName = componentInfo.getClassName();
			String frontActivityPackageName = componentInfo.getPackageName();


			Boolean found = false;
			for (Setting.FrontActivityInfo info : frontActivityInfos) {
				if (info.getClassName().equals(frontActivityClassName)) {
					found = true;
					break;
				}
			}

			if (found)
				return;

			String[] packageComponents = frontActivityPackageName.split("\\.");

			if (packageComponents.length > 0)
				frontActivityPackageName = packageComponents[packageComponents.length-1];



			frontActivityInfos.add(new Setting.FrontActivityInfo(frontActivityPackageName,
					frontActivityClassName, Integer.parseInt( prefs.getString("time_interval", main.defaultTimeInterval))));

			Setting.setFrontActivityInfos(this, frontActivityInfos);

			Toast.makeText(this, "Added to refreshPie", Toast.LENGTH_LONG).show();

		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{

		if (event.getEventTime() - prevTouchTime < 300) {
			onDoubleTap();
		}

		prevTouchTime = event.getEventTime();


		touchCnt++;

		if (service_main.interval > touchCnt) {
			return false;
		}

		touchCnt = 0;

		// filtering screen class
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		// get the info from the currently running task
		List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(2);

		ComponentName componentInfo = taskInfo.get(0).topActivity;

		String frontActivityClassName = componentInfo.getClassName();

		// HACK: KeyFlip of the crema shine
		if (frontActivityClassName.contains("com.melon.wizard")) {
			componentInfo = taskInfo.get(1).topActivity;
			frontActivityClassName = componentInfo.getClassName();
		}


		//if (prefs.getBoolean(Setting.keyLogFrontActivityClassname, false)) {
		//	String log = "class : " + frontActivityClassName;
		//	// Toast.makeText(service_main.this, log, Toast.LENGTH_LONG).show();
		//	logToFile(log);
		//}

		//Toast.makeText(this, frontActivityClassName, Toast.LENGTH_SHORT).show();

		int delayTime = service_main.timeInterval;

		// filter top level class name
		if (prefs.getBoolean(Setting.keyActivityFilter, false)) {
			Setting.FrontActivityInfo infoFound = null;
			for (Setting.FrontActivityInfo info : frontActivityInfos) {
				if (info.getClassName().equals(frontActivityClassName)) {
					infoFound = info;
					break;
				}
			}

			if (infoFound != null)
				delayTime = infoFound.getRefreshDelaytime();
			else
				return false;
		}



		_handler.sendEmptyMessageDelayed(0, delayTime);
		return false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
