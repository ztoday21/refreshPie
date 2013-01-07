package com.ztoday21.refreshPie;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class service_main extends Service implements OnTouchListener {
	
	// 값 공유
	public static int		_interval = 0;
	public static int		_timeInterval = 0;
	//public static boolean 	_restart = false;
	
	// 내부 사
	public TextView		_tv = null;		
	public Intent		_refreshIntent = null;
	
	public InputMethodManager _ime = null;

	@Override
	public void onCreate() 
	{
        super.onCreate();
    }
 
	@Override
    public void onDestroy() 
    {
        super.onDestroy();
        Toast.makeText(this, "서비스 중지됨", Toast.LENGTH_SHORT).show();
        
        // window manager 에서 view 제거
        // 서비스가 멈춰도 이부분이 제거가 안되었음
        WindowManager winmgr = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        winmgr.removeView(_tv);
        
        // 후에 좀 더 정확한 서비스 기능 알아야 함
        
    }
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId )
	{
		if( 0 != (Service.START_FLAG_RETRY & flags) )
		{
			// 원하는 작업을 하자
			// 리프레시어플 찾기
			PackageManager pm = getPackageManager();
			_refreshIntent = pm.getLaunchIntentForPackage("com.nextpapyrus.Refresh2");
			
			if(null != _refreshIntent)
			{
				// text view 를 window manager 에 등록 후 touch event 연결 
				_tv = new TextView(this);
				_tv.setOnTouchListener(this);
				
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				            WindowManager.LayoutParams.WRAP_CONTENT,
				            WindowManager.LayoutParams.WRAP_CONTENT,
				            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				            PixelFormat.TRANSLUCENT);
				
				WindowManager winmgr = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

				winmgr.addView(_tv, lp);
				
				// ime check
				_ime = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				 
				
				Toast.makeText(this, "서비스 시작됨", Toast.LENGTH_SHORT).show();
			}
			else
			{
				// 서비스 시작 실패
				stopSelf(startId);		
				Toast.makeText(this, "서비스 시작 실패 Refresh2 없음", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			//if( false ==_restart )
			{
				stopSelf(startId);
				Toast.makeText(this, "재시작 요청 무시 재시작 안함", Toast.LENGTH_SHORT).show();
			}
		}
		
		return Service.START_NOT_STICKY;
	}
	
	//---------------------------------
	public int _touchCnt;

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		// 이벤트에 대한 기능 확인 필요 
		// 왜 일케 알아야 할게 많을 까나... -_-;;;
		
		//if( MotionEvent.ACTION_DOWN == event.getActionMasked() )
		{
			if( null != _ime )
			{
				// 키보드가 비활성 일때만 
				if( false == _ime.isAcceptingText() )
				{
					_touchCnt++;
				}
			}
			else
			{
				_touchCnt++;
			}
			
			if( service_main._interval <= _touchCnt )
			{
				// 터치 초기화
				_touchCnt = 0;
		
				// 리프레시 어플 실행
				if(null != _refreshIntent)
				{
					try
					{
						Thread.sleep(service_main._timeInterval);
					}
					catch( InterruptedException e )
					{
						Toast.makeText(service_main.this, e.toString(), Toast.LENGTH_LONG).show();
					}
					
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
