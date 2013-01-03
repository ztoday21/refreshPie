package com.ztoday21.refreshman;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class service_main extends Service implements OnTouchListener {
	
	// 값 공유
	public static int _interval = 0;
	public Intent _refreshIntent;

	@Override
	public void onCreate() 
	{
        super.onCreate();
        //Toast.makeText(this, "onCreate()", Toast.LENGTH_SHORT).show();
    }
 
	@Override
    public void onDestroy() 
    {
        super.onDestroy();
        Toast.makeText(this, "서비스 중지됨", Toast.LENGTH_SHORT).show();
    }
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId )
	{
		if( 0 != (Service.START_FLAG_RETRY & flags) )
		{
			// 원하는 작업을 하자
			// 리프레시어플 찾기
			PackageManager pm = getPackageManager();
			_refreshIntent = pm.getLaunchIntentForPackage("Refresh2");
			
			if(null != _refreshIntent)
			{
				//Don't setText, this will give the view size
				TextView tv = new TextView(this);
				tv.setOnTouchListener(this);
				
				//You must set FLAG_NOT_FOCUSABLE, otherwise your view will interfere with the keyguard
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				            WindowManager.LayoutParams.WRAP_CONTENT,
				            WindowManager.LayoutParams.WRAP_CONTENT,
				            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				            PixelFormat.TRANSLUCENT);
				
				WindowManager winmgr = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

				winmgr.addView(tv, lp);	
				
				Toast.makeText(this, "서비스 시작됨", Toast.LENGTH_SHORT).show();
			}
			else
			{
				stopSelf(startId);
				
				Toast.makeText(this, "서비스 시작 실패 Refresh2 없음", Toast.LENGTH_SHORT).show();
			}
		}
		
		return Service.START_STICKY;
	}
	
	//---------------------------------
	public int _touchCnt;

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		Toast.makeText(service_main.this, "Touch", Toast.LENGTH_SHORT).show();
		
		if( MotionEvent.ACTION_UP == event.getAction() )
		{
			_touchCnt++;
			
			if( service_main._interval <= _touchCnt )
			{
				// 터치 초기화
				_touchCnt = 0;
				
				// 리프레시 어플 실행
				if(null != _refreshIntent)
				{
					startActivity(_refreshIntent);
				}
			}
		}
		
		return false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
